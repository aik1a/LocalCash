# Reglas de negocio

**Estado:** v1; las reglas marcadas PENDIENTE no deben inventarse durante implementación.

## BR-MOV-01 — Tipo y monto

Un movimiento tiene tipo `INGRESO` o `EGRESO`. Su monto se almacena como magnitud positiva. El efecto sobre el saldo depende del tipo.

## BR-ACC-01 — Saldo

El saldo actual de una cuenta se deriva del saldo inicial y sus movimientos:

`saldo = saldoInicial + ingresos - egresos`

No se mantiene un `saldoActual` modificable como fuente independiente mientras no exista una necesidad demostrada.

## BR-GEO-01 — Ubicación opcional

Un movimiento puede existir sin ubicación. Si tiene ubicación, latitud/longitud deben formar un par válido.

## BR-BUY-01 — Estado de compra

Una compra puede representar una adquisición planificada o realizada. Estados iniciales: `PLANIFICADA` y `REALIZADA`.

PENDIENTE: definir transición exacta y efecto sobre movimientos financieros (si completar una compra crea automáticamente un movimiento o si son operaciones separadas).

## BR-BUY-02 — Total

Cuando una compra posee detalles, su total se deriva de la suma de subtotales. Evitar permitir un total que contradiga sus detalles.

## BR-SHARE-01 — Participantes

Los participantes de un gasto compartido provienen de la agenda local de contactos.

## BR-SHARE-02 — Distribución

El encargo exige distribución equitativa o personalizada. El diseño v1 permitirá representar asignaciones por producto/servicio y monto por contacto.

**Decisión de implementación mínima del Orquestador (2026-10-13, S00-T05):** para una compra REALIZADA con dos o más contactos distintos seleccionados expresamente, calcular el total de detalles y repartirlo en centésimas enteras. EQUITATIVO: cuota base = total / N; resto = total % N; distribuir una centésima extra a los primeros contactos por ID ascendente. PERSONALIZADO: ingresar importes no negativos por contacto y exigir suma exacta = total antes de guardar. Evitar duplicar un reparto existente; ninguna modalidad altera automáticamente cuentas o movimientos. Se permiten importes de 0 cuando la asignación global es válida. La especificación por producto sigue en los detalles de la compra; no se exige repartir cada ítem por separado.

## BR-DEBT-01 — Deuda

Cada participante debe poder tener un monto claramente asignado. Una deuda no puede tener monto negativo.

**Decisión de alcance S00-T05:** pagos parciales excluidos del MVP académico. Se permite registrar deuda completa como `PENDIENTE` y opcionalmente marcarla `PAGADA` sin generar movimientos. Reutilizar, de forma simplificada, una asociación compra/contacto/monto/estado en SQLite cuando sea suficiente, sin tablas redundantes.

## BR-SEC-01 — Biometría

LocalCash no almacena huellas ni plantillas biométricas. La validación se delega a Android mediante `BiometricPrompt`.

## S01-T01 — Reglas aprobadas por Aikia, 18-10-2026

Estas reglas sustituyen el mínimo de dos contactos y la precisión uniforme de S00
para los nuevos formularios. No recalculan repartos históricos.

- BR-CUR-01: elegir moneda principal una sola vez tras autenticar, persistida en SQLite;
  se usa como selección inicial al crear cuentas y compras, que pueden usar otra divisa.
- BR-CUR-02: catálogo CLP/USD/EUR/ARS/MXN/COP/PEN/BRL/GBP/JPY. CLP y JPY:
  enteros; las demás: dos decimales. Exceso de precisión se rechaza, no se redondea.
- BR-CUR-03: movimientos heredan divisa de cuenta; detalles, cuotas y deudas heredan
  compra. Moneda asignada fija. No conversión ni total sumado entre distintas monedas.
- BR-CUR-04: datos previos conservan importes exactos y requieren asignación explícita
  de divisa; no asignar CLP/JPY mientras existan fracciones en sus importes relacionados.
- BR-SHARE-03: el usuario participa como «Tú», sin necesitar contacto propio. Basta
  un contacto seleccionado. Un único contacto existente se preselecciona; con varios,
  el usuario elige a quién incluir. El contacto asociado a la compra no determina por sí
  mismo el conjunto de participantes.
- BR-SHARE-04: reparto equitativo divide en la unidad mínima de la moneda. El resto
  va primero a Tú, luego a contactos por ID ascendente. Ejemplo 100 CLP entre Tú y
  dos contactos: 34/33/33. La suma siempre coincide con el total.
- BR-SHARE-05: personalizado exige tu importe y el de cada contacto seleccionado;
  todos >=0, misma precisión que la compra y suma exacta. La cuota propia no es deuda.
- BR-UX-01: nombres de contactos sin IDs visibles; etiquetas simples («Cantidad»,
  «Precio unitario», «Monto»). Ejemplos en campos; validaciones se explican al fallar.

## UX-01 — Pago explícito de compras (autorización 2026-11-02)

Sustituye la exclusión previa de compra→movimiento únicamente para el pago completo confirmado por el usuario. Las compras PLANIFICADAS no alteran saldos. Desde el detalle se elige una cuenta con la misma divisa y se confirma el importe total efectivamente pagado. Esto crea exactamente un EGRESO y cambia la compra a REALIZADA en una transacción. La asociación persistente impide repetir el pago desde la compra. El reparto y las deudas no descuentan dinero adicional; no se descuentan únicamente las cuotas propias si el usuario pagó el total.

Las compras históricas permanecen sin pago asociado; antes de registrar su pago se advierte comprobar que el egreso no fue anotado manualmente. No se detectan automáticamente duplicados semánticos históricos. Un total cero no produce movimiento, ya que BR-MOV-01 exige un monto positivo. Pagos parciales, múltiples pagadores, conversión, vincular movimientos históricos y cobro de deudas quedan fuera de este incremento.

## CRUD-02 — Primera ronda aprobada, 2026-11-03

Las deudas positivas admiten PENDIENTE/PAGADA y se puede corregir una marca accidental. Cambiar estado no genera ingresos ni modifica saldos; la UI lo explica. La cuota cero conserva su participación pero se presenta Sin deuda, sin acción de cobro. No hay aceptación remota ni pagos parciales.

Renombrar cuentas/contactos conserva IDs, divisas e historial. Solo se permite eliminar una cuenta sin movimientos o un contacto sin referencias de compras/deudas; confirmar no evita la revalidación en persistencia.

Solo se edita/elimina una compra PLANIFICADA sin pago ni reparto. Edición conserva ID, moneda y estado, exige detalles válidos y recalcula total. Borrado elimina cabecera/detalles atómicamente. Compras realizadas y movimientos asociados no se alteran desde este flujo.

La consulta por contacto muestra compras y estados, suma solo deudas positivas pendientes y separa monedas. Importes sin moneda definida se muestran por compra sin agregarlos. Cobros que creen ingreso, corrección de movimientos y rehacer repartos quedan para la segunda ronda.
