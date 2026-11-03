# Auditoría local — cobertura CRUD y cierre de deudas

Fecha: 2026-11-03. Objeto: copia output/LocalCash-ux abierta en Android Studio y probada por el usuario. No se reemplazó por main ni por el checkout antiguo LocalCash. Base histórica declarada: 9b8a651d66982eea3a41e1979342468fe4cad176 + incremento UX-01 local. Huella actual de 55 fuentes Java/XML: b516fac92faa163d22b40b3678d86f608e67a73a2913f28b8e4969c33390acc6. Detalle por archivo y reproducción: 2026-11-03-crud-evidence.json.

Tipo: diagnóstico de cobertura solicitado por el usuario; sin correcciones de código ni gate independiente del incremento UX-01, implementado anteriormente en este chat. No es autorización de merge ni cierre de sprint.

## Criterios y método

- Inspeccionar UI, servicios, repositorios, dominio y restricciones SQLite actuales.
- Separar operaciones existentes en código persistente de edición de un formulario todavía sin guardar.
- Explicar el pendiente perpetuo y priorizar mejoras mínimas; no exigir CRUD completo por simetría.
- No modificar código, BD del usuario, APK, PROJECT_STATE ni reglas canónicas.

Se consultaron PROJECT_ROUTER, AGENTS, PROJECT_STATE, BUSINESS_RULES, DATABASE, handoff UX-01 y reglas del Auditor/matriz/plantilla disponibles en el checkout de referencia. Falta un brief específico de nueva implementación: este pedido autoriza el diagnóstico, no fija el comportamiento financiero de una devolución.

## Inventario

| Módulo | Crear | Consultar | Actualizar guardado | Eliminar guardado |
|---|---|---|---|---|
| Cuentas | Sí | Lista, detalle, saldo, movimientos | Solo asignar divisa a históricos sin moneda; no renombrar ni corregir saldo inicial | No |
| Movimientos | Sí | Historial por cuenta y mapa si hay coordenadas | No | No |
| Contactos | Sí | Lista, búsqueda interna por ID y nombre en compras/repartos | No | No |
| Compras | Sí | Lista y detalle; filtro por cuenta pagadora | Limitado: pagar y pasar a REALIZADA; asignar moneda histórica. Sin edición de nombre, fecha, contacto o productos | No; tampoco cancelar una planificada |
| Detalles de compra | Sí, al guardar compra | Sí | Solo antes de guardar; quitar y volver a añadir en formulario | Solo quitar del borrador, no de compra persistida |
| Repartos/deudas | Sí | Por compra, con cuota propia y contacto | No; tampoco cerrar deuda o rehacer reparto | No |
| Preferencia de moneda principal | Primera configuración | Lectura para valores iniciales | No, decisión explícita vigente | No; no es un CRUD que requiera borrado |

Los únicos updates funcionales de repositorios son asignación de moneda histórica y cambio de estado al pagar compra. No hay delete de datos de negocio en repositorios/servicios. El borrado de un atlas temporal en AtlasOffline no es CRUD financiero.

## Hallazgos y evidencia

### H01 — Alta prioridad de uso: las deudas no tienen cierre

- domain/Deuda.java:16 devuelve siempre PENDIENTE, sin campo de estado real.
- data/DeudaRepository.java:28 inserta PENDIENTE; :47 ni siquiera consulta estado.
- presentation/RepartoActivity.java:117–123 oculta el formulario y presenta filas de texto con Pendiente fijo; no acción de cobro.
- data/LocalCashDbHelper.java:49 impone CHECK(estado = 'PENDIENTE').

Reproducción aislada con el DDL real de crearDeudas: UPDATE a PAGADA falla con CHECK constraint failed. No basta añadir un botón. Deben cambiar modelo, lectura, actualización persistente, UI y migración compatible con datos previos.

BUSINESS_RULES BR-DEBT-01 permite opcionalmente marcar PAGADA sin movimientos; DATABASE declara que no se implementó en T05. Es una limitación histórica de producto, no una regresión probada del último cambio.

Propuesta mínima: Marcar como pagada, confirmación por contacto/compra/importe, mostrar estado persistido al reabrir y permitir corregir una marca accidental. Es registro local: el contacto no recibe ni acepta solicitudes.

Decisión antes de implementar: solo marcar estado (compatible con regla previa, no aumenta saldo) o registrar devolución efectiva en una cuenta compatible y generar ingreso una sola vez. Si se elige lo segundo, el ingreso, estado y eventual anulación deben coordinarse; no crear ingreso al aceptar una promesa de pago. Pagos parciales y confirmaciones remotas siguen fuera de alcance.

### H02 — Fricción concreta: una cuota cero queda pendiente

Reparto admite importes no negativos y DeudaRepository crea todas las filas. SQLite permite monto=0 y aplica PENDIENTE; la UI lo muestra igual que una deuda positiva. Reproducción: (0, PENDIENTE).

Recomendación: conservar la participación pero presentar Sin deuda y no ofrecer cobro de $0. No eliminar esa participación ni inventar dinero recibido.

### H03 — Faltan correcciones de bajo impacto

ContactoRepository solo crear/listar/buscar; ContactosActivity solo crea y lista. CuentaRepository tiene crear/listar/buscar y asignarDivisa histórica; MainActivity no ofrece edición. Un error de nombre queda permanente.

Priorizar renombrar contactos/cuentas sin alterar IDs, divisas, cuotas o movimientos. Permitir eliminar contacto sin referencias y cuenta sin movimientos, con confirmación; comprobar dependencias en persistencia, no solo UI. Para registros utilizados, bloquear con explicación antes de considerar archivado.

### H04 — Una compra planificada guardada no puede ajustarse ni cancelarse

CompraRepository no actualiza sus datos/detalles ni elimina. NuevaCompraActivity:97 quita ítems solo de su lista en memoria; guardar crea una compra nueva. Corregir fecha, precio o producto exige otra compra y deja la anterior.

Priorizar edición y cancelación/eliminación de planificadas sin pago ni reparto. Revalidar total y al menos un detalle; cabecera/detalles atómicos. La creación de estado CANCELADA no es obligatoria: puede elegirse borrado confirmado de planificación sin dependencias.

### H05 — No hay forma de corregir un egreso ni un pago confirmado erróneo

MovimientoRepository solo crea/lista; CuentaActivity no tiene selección de movimiento para editar. CompraService.pagar evita un segundo pago, pero no tiene reversión. Una cuenta o importe equivocado queda sin corrección disponible.

Priorizar después de H01/H03/H04: corrección de movimientos manuales con recálculo del saldo. Los pagos asociados a compras deben corregirse desde un flujo coordinado; no cambiar solo monto o borrar solo movimiento porque el total, vínculo y reparto podrían discrepar. No habilitar edición libre de compras ya pagadas/repartidas sin decidir esas consecuencias.

### H06 — Falta consulta de deudas por persona

DeudaService solo expone listarDeudas(compraId); ContactosActivity no abre un detalle. Consultar cuánto debe una persona exige recorrer compras. Mejora siguiente: detalle del contacto con pendientes/pagadas y totales separados por moneda. No mezclar CLP y USD ni exigir un dashboard nuevo.

## Validación y límites

Auditoría estática de código y XML, búsquedas de métodos/rutas de actualización/borrado, reproducción de CHECK, cuota cero y FK en SQLite del host en memoria. No se abrió ni modificó localcash.db del teléfono/emulador. No se ejecutó build: no hubo cambios compilables y un build no demuestra la existencia de rutas CRUD. La validación manual positiva reportada por el usuario corresponde al recorrido anterior, no prueba CRUD ausente.

Las FK RESTRICT bloquean borrar contactos utilizados y movimientos enlazados. Es protección existente, no un error: cualquier futuro borrado debe respetar dependencias. Referencia primaria: [SQLite Foreign Keys](https://www.sqlite.org/foreignkeys.html).
El cambio de restricciones de estado requiere una migración explícita apropiada para las versiones Android soportadas, conservando filas, FK y triggers; no cambiar solo onCreate. Referencia primaria: [SQLite ALTER TABLE](https://www.sqlite.org/lang_altertable.html).

## Evaluación y próximos incrementos

Cobertura actual: alta en creación/consulta; limitada en actualización y ausente en eliminación de datos de negocio. No se declara incumplido un CRUD completo exigido por el docente: no se verificó tal requisito literal. Se detectan carencias de uso actuales y oportunidades de ampliación.

Orden propuesto:
1. Cerrar deudas, corregir marca y tratar cuotas cero; decidir efecto sobre saldo.
2. Renombrar contactos/cuentas y eliminar únicamente registros sin dependencias.
3. Editar o cancelar planificaciones.
4. Corregir movimientos manuales y definir anulación de pagos asociados.
5. Consulta por contacto y corrección controlada de repartos.

Pruebas futuras: reapertura y persistencia del estado; doble toque no duplica ingresos; undo coherente; cuotas cero; divisas incompatibles; FK al borrar usados; cancelar no cambia saldo; edición conserva identidad y reparto; operaciones de varias tablas revierten completas si falla una.

Veredicto de cobertura: PARCIAL. Gates commit/merge/avance: N/A, sin cambio funcional presentado para aceptación. No es auditoría independiente de mi implementación anterior.
