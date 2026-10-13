# Modelo de dominio POO

**Estado:** Cuenta/Movimiento (T03) y Contacto/Compra/DetalleCompra (T04) implementados; reparto/deudas siguen siendo diseño.

## Implementación actual S00-T03

Cuenta y Movimiento son objetos inmutables con estado privado. IDs 0 representan objetos nuevos; IDs positivos identifican registros. Cuenta exige nombre no vacío y calcula saldo con movimientos de su propia cuenta; Movimiento exige cuenta persistida, tipo INGRESO/EGRESO y monto positivo. Dinero convierte texto decimal a centésimas long de forma exacta, sin redondear.

Cuenta no incluye todavía tipo (catálogo pendiente). Movimiento incluye descripción opcional y fecha epoch milisegundos; ubicación queda fuera de esta tarea. T04 incorpora contactos/compras; no hay clases de deudas/reparto. Los saldos negativos son posibles: no se exige bloquear egresos por fondos insuficientes. Overflow se rechaza, no se convierte en saldo incorrecto.

## Implementación S00-T04

Contacto contiene ID y nombre no vacío. DetalleCompra es inmutable: descripción, cantidad entera positiva y precio unitario no negativo en centésimas; calcula su subtotal exacto. Compra exige nombre, estado PLANIFICADA/REALIZADA, fecha y uno o más detalles; referencia opcional por ID a contacto existente. Copia y protege su lista, calcula total exacto y rechaza overflow. No expone setTotal ni setter de estado.

CompraService coordina el contacto opcional y CompraRepository. Este último guarda cabecera/detalles en una transacción. No se implementa conversión de estado ni efecto contable automático; la decisión sigue pendiente.

## Principios

- Estado privado.
- Objetos nacen en un estado válido.
- Métodos expresan operaciones del dominio.
- Setters genéricos solo cuando sean realmente necesarios.
- Entidades no conocen UI ni SQLite.
- Datos derivados se calculan cuando sea razonable en vez de mantenerse duplicados.

## Cuenta

**Responsabilidad:** representar una cuenta financiera del usuario.

Estado candidato:
- `id`
- `nombre`
- `tipo`
- `saldoInicial`

Comportamiento:
- `calcularSaldo(List<Movimiento>)`

Invariantes:
- nombre no vacío;
- saldo actual no se modifica mediante `setSaldo`.

Pendiente: cerrar valores de `TipoCuenta`.

## Movimiento

**Responsabilidad:** representar un ingreso o egreso perteneciente a una cuenta.

Estado candidato:
- `id`
- referencia/ID de cuenta
- `tipo`
- `monto`
- `descripcion`
- `fecha`
- `ubicacion` opcional

Invariantes:
- `monto > 0`;
- tipo pertenece a `INGRESO | EGRESO`;
- ubicación nula o válida.

Comportamiento:
- `getMontoConSigno()`
- `asignarUbicacion(...)`
- `eliminarUbicacion()`
- `tieneUbicacion()`

El signo contable deriva de `tipo`; no guardar egresos como montos negativos.

## Ubicacion

**Tipo:** Value Object.

Estado:
- `latitud`
- `longitud`

Preferentemente inmutable. Validar rangos geográficos. No requiere tabla propia en el diseño actual.

## Contacto

**Responsabilidad:** representar una persona de la agenda local que puede vincularse a compras/gastos compartidos.

Estado mínimo confirmado:
- `id`
- `nombre`

Campos adicionales como teléfono/email son decisiones de diseño, no requisitos explícitos; agregar solo si se aprueban.

## Compra

**Responsabilidad:** representar una adquisición realizada o planificada y sus detalles.

Estado candidato:
- `id`
- `nombre`/descripción
- `fecha`
- `estado`
- colección de `DetalleCompra`

Estado cerrado candidato:
- `PLANIFICADA`
- `REALIZADA`

Comportamiento:
- `calcularTotal()`
- transición controlada a realizada si el flujo la requiere.

No usar `setTotal`: el total debe derivar de los detalles cuando éstos existan.

## DetalleCompra

**Responsabilidad:** representar un producto o servicio incluido en una compra.

Estado candidato:
- `id`
- descripción
- cantidad
- precio unitario

Invariantes candidatas:
- cantidad > 0;
- precio >= 0.

Comportamiento:
- `calcularSubtotal()`.

## Participante

**Responsabilidad:** expresar participación de un contacto en una compra compartida.

Puede ser una entidad/asociación persistente. Su forma final depende del DER definitivo.

## Asignacion

**Responsabilidad:** relacionar un detalle de compra con un contacto y, cuando corresponda, el monto que se le asigna.

Esta entidad permite soportar reparto por producto/servicio y distribución personalizada sin introducir lógica en la UI.

## Deuda

**Responsabilidad:** representar el monto pendiente/asignado a un contacto dentro de una compra compartida.

Estado candidato:
- `id`
- compra
- contacto
- monto
- estado

Estados candidatos:
- `PENDIENTE`
- `PAGADA`

Comportamiento:
- `marcarComoPagada()`
- `estaPagada()`

No exponer un `setEstado` genérico si permite transiciones inválidas.

## Relación conceptual

```text
Cuenta 1 ---- N Movimiento
Movimiento 0..1 ---- 1 Ubicacion (composición conceptual, no tabla)

Compra 1 ---- N DetalleCompra
Compra 1 ---- N Participante ---- 1 Contacto
DetalleCompra 1 ---- N Asignacion ---- 1 Contacto
Compra 1 ---- N Deuda ---- 1 Contacto
```
