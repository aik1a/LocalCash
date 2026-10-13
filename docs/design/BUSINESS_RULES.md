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