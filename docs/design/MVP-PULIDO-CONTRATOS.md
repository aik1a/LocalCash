# Contratos del pulido completo — MVP-PULIDO

**Naturaleza:** diseño del incremento autorizado para documentar; no acredita implementación. Estas reglas pasan a BUSINESS_RULES/DATABASE en MVP-S01-T02. Fuente primaria: PDF docente; ampliaciones de UX/CRUD/saldo/pago/deudas/gesto solicitadas por Aikia.

| ID | Contrato | Casos mínimos |
|---|---|---|
| POL-01 | Saldo derivado con enteros exactos; no saldo actual modificable paralelo. | Editar 300→400 aplica diferencia 100; overflow rechazado. |
| POL-02 | Cuentas nuevas con saldo inicial >=0; operaciones nuevas no crean saldo negativo. Validar y escribir dentro de una transacción. | Egreso igual al disponible deja cero; superior rechaza sin escritura. |
| POL-03 | Históricos negativos se pueden leer; operaciones que mantengan/mejoren el saldo se permiten, las que empeoran se rechazan. Renombrar no obliga a corregir saldo. | Cuenta -500 admite ingreso 100 y queda -400, no egreso 1. |
| POL-04 | Edición sustituye original; eliminación simula retirarlo y revalida saldo actual proyectado. No motor de solvencia histórica. | Borrar ingreso puede rechazarse; borrar egreso devuelve importe. |
| POL-05 | Planificar no cobra; comprar requiere cuenta de la misma divisa y saldo suficiente, muestra resumen y confirma. Cargo por el total, no solo cuota Tú. | Sin cuenta apta hay orientación; CLP no paga USD; doble ejecución no duplica cargo. |
| POL-06 | Estado REALIZADA + vínculo + egreso se guardan juntos. Una compra tiene como máximo un pago activo y un movimiento pertenece como máximo a una compra. | Fallo intermedio revierte todo; no se repite al reabrir. |
| POL-07 | Total cero se puede completar sin crear Movimiento de cero. Registrar marca de pago/completado de cero para distinguirlo de legado. Históricos realizados sin vínculo no se cobran retroactivamente. | Deshacer cero/histórico no devuelve dinero inventado. |
| POL-08 | Deshacer pago retira vínculo y egreso original, vuelve a planificada y restituye saldo a la cuenta original una vez. Eliminar pagada sigue la misma restitución atómica con confirmación. | Reintento no duplica devolución; no añadir además un ingreso de “reembolso”. |
| POL-09 | Cuenta solo se elimina sin movimientos/pagos; contacto solo sin referencias. Compra elimina hijos propios explícitamente en transacción, sin cascada que borre deudas ajenas. Moneda asignada permanece fija. | ID inexistente nunca informa éxito; filas ajenas intactas. |
| POL-10 | Compras con reparto/pago no permiten cambiar importes directamente. Metadatos inocuos sí. Movimiento vinculado se administra desde la compra, no se edita/elimina directamente. | Botones explican el motivo y ruta adecuada. |
| POL-11 | Deuda admite PENDIENTE/PAGADA; representa registro de pago completo, sin transferencia automática ni pago parcial. La corrección de estado es para error de registro, no una devolución real. | Marcar dos veces no duplica efecto; Tú no es deuda; cuentas no cambian. |
| POL-12 | Reparto totalmente pendiente se puede anular con confirmación. Si hay deuda pagada se bloquean anulación, devolución y borrado automático. No sugerir falsear un pago para sortear el bloqueo. | Anular pendiente y deshacer compra puede ser una transacción; si cualquier paso falla, todo queda intacto. |
| POL-13 | Mantener S01-T01: CLP/JPY enteros, resto del catálogo dos decimales; long en centésimas históricas. Exceso de precisión rechazado; reparto en unidad mínima y sobrante estable. | 100 CLP →34/33/33; 100 USD →33,34/33,33/33,33; no cambio ni suma entre divisas. |
| POL-14 | Menú visible y detalle exponen las mismas acciones que swipe izquierdo. Deslizar solo revela opciones, nunca borra directamente. Operar por ID estable, no posición de fila. | Gesto no interfiere con scroll; alternativa accesible y confirmaciones. |
| POL-15 | Etiquetas simples, estados legibles, moneda visible, teclado adecuado, error persistente y éxito discreto. No mostrar detalles del esquema ni IDs internos. | Texto grande, teclado, nombres largos y listas vacías utilizables. |
| POL-16 | I/O fuera del hilo UI en rutas intervenidas mediante ExecutorService simple. Callbacks solo con actividad/solicitud/sesión vigentes; helper no se cierra mientras trabaja. | Home/rotación/bloqueo no muestran datos privados ni duplican operaciones. |
| POL-17 | BiometricPrompt, FLAG_SECURE, mapas y SQLite locales se conservan. Explicitación de política de backup financiero por versión; no afirmar que eliminar permiso INTERNET impide Auto Backup. | Permiso GPS denegado permite guardar sin ubicación; cobertura real del atlas explicada. |
| POL-18 | Actualización requiere firmante compatible; preparar versión y probar upgrade con datos en instalación de prueba. No desinstalar datos reales por incompatibilidad. | Firma histórica incompatible queda BLOCKED, no PASS. |
| POL-19 | No quitar alcance al llegar la fecha: registrar pendientes/bloqueos y avance real. Fecha objetivo 02/11/2026, sin hora oficial informada. | Ninguna tarea pendiente se convierte en DONE por presión de entrega. |

## Decisiones técnicas mínimas previstas

- Conservar Java 17 como nivel de lenguaje, XML, Groovy, SQLiteOpenHelper, osmdroid 6.1.18 y BiometricPrompt. Usar toolchain real del repositorio (CI JDK25) sin actualizar dependencias por costumbre.
- No añadir Room, Compose, Kotlin, MVVM, DI, servidor, cambio de divisas online, pagos parciales ni campos extra para imitar el dibujo.
- MVP-S04 propone v5→v6 para vínculo compra-pago. La representación exacta de cero admite vínculo sin egreso; debe restringirse y probarse en servicio/persistencia.
- MVP-S05 propone v6→v7 para estado de deuda. Reconstruir solo tabla afectada dentro de migración transaccional; nunca borrar la base ni apagar FK como arreglo.
- Si el código vigente ya cambió de versión al comenzar, recalcular la secuencia sin reaplicar migraciones ni renumerar datos silenciosamente; registrar diferencia antes de editar.
- Pagos reales de contactos no se reembolsan automáticamente: esa gestión bancaria excede el registro local del MVP.
- Renombrar DAO no agrega valor: ampliar los repositorios existentes.
