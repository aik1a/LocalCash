# CRUD-02 — Cambios pequeños y persistencia coherente

Fecha: 2026-11-03. Estado: T00–T08 implementadas y pruebas automatizadas PASS; pendiente revisión independiente y recorrido de pantallas en teléfono.

## Objetivo

Cerrar deudas y corregir errores cotidianos sin perder datos, romper relaciones ni agregar tecnología. Cada incremento entrega pantalla, servicio, dominio y persistencia consistentes, pruebas y handoff.

## Requisitos relacionados

LC-CON-01, LC-ACC-01, LC-MOV-01, LC-BUY-01/02/03, LC-SHARED-01/02, LC-DEBT-01/02, BR-DEBT-01, BR-ACC-01, BR-CUR-01/04. Las ampliaciones CRUD propuestas no se presentan como requisitos literales del encargo docente.

## Estado inicial

Copia de trabajo: output/LocalCash-ux, SQLite v6. Diagnóstico: docs/audits/2026-11-03-crud-local.md y evidencia adjunta. Cuentas, movimientos, contactos y compras permiten crear/consultar; edición limitada a divisa histórica y pago de compra; no borrado de negocio. Deudas solo admiten PENDIENTE en esquema, modelo y UI. UX-01 tiene prueba manual positiva reportada por el usuario, sin que ello sustituya auditoría independiente de su diff.

## Alcance y decisiones propuestas

Primera entrega: permitir marcar una deuda totalmente pagada y deshacer la marca. Mantener la regla canónica existente de no crear movimiento al cambiar ese estado. Texto explícito: esta acción no modifica saldos. Cuotas cero se muestran Sin deuda, conservando participante y monto; no se marcan como dinero recibido.

Después: nombres, eliminación de registros sin uso, edición/cancelación de planificaciones y consulta por contacto. Correcciones financieras y cobros vinculados a cuentas se tratan como ampliaciones separadas con reglas previas.

## Fuera de alcance

Backend, sincronización entre dispositivos, aceptación por el contacto, pagos parciales, conversión, múltiples pagadores y rediseño general. Sin dependencias nuevas.

## Diseño propuesto

SQLite continúa como fuente única. No almacenar estados financieros solo en vistas. Los cambios de una fila usan repositorios; los de varias filas, transacciones. Conservar claves, importes, divisas, relaciones y restricciones de precisión. La UI actualiza su contenido después de confirmar escritura exitosa.

Solo incrementar versión si cambia el esquema. Las ediciones y borrados que usan columnas existentes no necesitan una versión distinta por tarea.

## Plan de implementación

Cada fila es una tarea independiente. Esta copia no tiene historia Git propia: los cambios se entregan como patch verificable contra baseline conservado; al integrarse en el repositorio operativo se usarán commits/ramas por tarea. Los IDs CRUD-02 son de esta propuesta local, no reemplazan el sprint canónico.

| ID | Objetivo y alcance | BD | Aceptación principal |
|---|---|---|---|
| T00 | Preparar continuidad: guardar baseline revisable de la copia, comprobar firma de actualización y respaldo recuperable antes de tocar datos reales | Sin cambios | Se distingue copia local de main; actualización no depende de desinstalar ni borrar datos |
| T01 | Admitir estado real de deuda en esquema, modelo y lecturas; UI deja de fijar Pendiente como texto constante | v6→v7 | Instalación nueva y upgrades soportados conservan filas, IDs, importes y cuotas; todas las deudas antiguas siguen pendientes; sin cargos ni cobros retroactivos |
| T02 | Marcar pagada/deshacer desde reparto, con confirmación por contacto/compra/monto; tratar $0 como Sin deuda | UPDATE del estado en v7, sin migración nueva | Reabrir conserva estado; doble toque es inocuo; deshacer vuelve a pendiente; cuenta y reparto conservan montos; no permite cobrar cero |
| T03 | Editar nombre de contacto desde su lista/detalle | UPDATE nombre | Nombre corregido aparece en repartos y compras existentes; mismo ID y mismas deudas; vacío rechazado |
| T04 | Renombrar cuenta | UPDATE nombre | Mismo ID, moneda, saldo inicial, historial y vínculos; vacío rechazado |
| T05 | Eliminar contacto sin referencias y cuenta sin movimientos, con confirmación | DELETE condicionado; conservar FK | Registros sin uso se borran; utilizados se conservan y se explica motivo; comprobar dentro de operación, no solo al abrir diálogo |
| T06 | Editar compra PLANIFICADA sin pago/reparto: nombre, fecha, contacto e ítems | UPDATE/INSERT/DELETE de detalles en transacción; sin esquema nuevo | Mismo ID, al menos un ítem, total recalculado y precisión correcta; fallo revierte todo; no cambia saldo ni habilita cambiar moneda asignada |
| T07 | Eliminar planificación que ya no se realizará, solo si no tiene pago/reparto | Borrado explícito de detalles y cabecera en transacción | Confirmación, sin registros huérfanos ni cambios de saldo; compra realizada o con dependencias se rechaza |
| T08 | Consultar deudas desde un contacto | Consultas por contacto, sin esquema nuevo inicialmente | Lista con compra, importe, moneda y estado; totales pendientes separados por moneda; cuotas cero excluidas del cobro |

T01/T02 deben publicarse juntos como primera entrega útil; pueden implementarse/revisarse en commits pequeños, sin presentar soporte de estado sin botón como solución completa. T03/T04 son independientes una vez fijado baseline. T05–T08 usan como base incrementos aceptados.

### Segunda ronda, después de la primera entrega

- T09: corregir movimientos manuales. Definir campos editables y política de borrado/anulación. Distinguir pagos ligados a compras; recalcular saldo y validar rango/precisión dentro de transacción. No habilitar editar o borrar el movimiento de compra por separado.
- T10: corregir un reparto erróneo. Propuesta: permitir rehacerlo solo si todas las deudas siguen pendientes y ninguna tiene cobro asociado; sustituir filas y cuota propia atómicamente. Si existen pagos, definir tratamiento antes de implementar.
- T11 opcional: registrar devolución real en una cuenta. Requiere aprobar cambio de regla, relación única entre deuda y movimiento de ingreso y nueva migración (v8 propuesta, número a confirmar al ejecutar). Ingreso, vínculo y estado deben confirmarse juntos. Deshacer debe revertir también el efecto financiero de manera definida. No crear ingresos retroactivos para deudas ya marcadas pagadas ni duplicar ingresos registrados manualmente.

## Migración de deudas

La tabla actual contiene CHECK(estado='PENDIENTE'). T01 necesita una migración explícita para admitir PENDIENTE/PAGADA, preservando PK compuesta, FK, validación del monto y triggers aplicables. Reconstruir tabla según capacidades de SQLite incluidas en los Android soportados; no depender de una función nueva del SQLite del PC. Inspeccionar también triggers de otras tablas que referencian deudas y conservarlos/recrearlos correctamente.

Conservar PENDIENTE en históricos, incluido monto cero; Sin deuda es presentación derivada del importe, no un cobro inventado. onCreate y upgrades deben converger al mismo esquema. No modificar solamente CREATE TABLE ni resetear la BD para sortear la migración.

## Validación por tarea

1. Pruebas relacionadas con el comportamiento, build APK e instrumentación cuando corresponda.
2. Verificar acción y cancelación, datos inválidos, persistencia tras cerrar/reabrir y dependencias.
3. Cambios financieros: saldo exacto, moneda incompatible, doble toque y rollback ante fallo.
4. Migraciones: fixtures v6 con datos de todas las tablas; instalación limpia v7; rutas anteriores soportadas; comparación antes/después de claves, montos, divisas y relaciones; foreign_key_check; cuotas cero y triggers.
5. Recorrido manual del incremento en variante aislada antes de actualizar la app con datos.
6. Revisión independiente y handoff; Orquestador actualiza estado/trazabilidad cuando corresponda. No cerrar sprint ni declarar merge desde este plan.

Conservar el mismo firmante y versionado apropiado para actualizar una instalación real. Si falla por firma, no desinstalar la app con datos como solución. No prometer backup sin verificar que pueda restaurarse.

## Progreso

- [x] Auditoría y prioridades registradas.
- [x] Plan dividido en tareas con criterios y efectos sobre BD.
- [x] Usuario autorizó el plan; brief local CRUD-02-incrementos.md.
- [x] T00–T08: implementación y validación automatizada; actualización de APK con datos conservados.
- [ ] Recorrido humano y revisión independiente; integración en main.

## Decisiones y descubrimientos

El usuario pidió un plan sincronizable con BD, entendido aquí como coherencia entre interfaz/código y SQLite local. No supone sincronización remota. La regla de devolución en cuenta queda propuesta para otra entrega: marcar pagada y recibir dinero en una cuenta son acciones con consecuencias distintas.

## Resultado final

Primera ronda T00–T08 implementada en copia local. Estado de deuda, renombres, borrados sin dependencias, edición/eliminación de planificación y consulta por persona. Pruebas y limitaciones en docs/handoffs/CRUD-02.md. T09–T11 no implementadas: segunda ronda/decisiones pendientes. No se declara aprobado el merge ni cerrado el sprint.
