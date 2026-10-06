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

PENDIENTE: cerrar UX y algoritmo exacto de reparto equitativo, redondeos y validación de que la suma asignada coincida con el total.

## BR-DEBT-01 — Deuda

Cada participante debe poder tener un monto claramente asignado. Una deuda no puede tener monto negativo.

PENDIENTE: decidir si se soportan pagos parciales. Hasta que se apruebe, el modelo solo contempla `PENDIENTE`/`PAGADA`.

## BR-SEC-01 — Biometría

LocalCash no almacena huellas ni plantillas biométricas. La validación se delega a Android mediante `BiometricPrompt`.