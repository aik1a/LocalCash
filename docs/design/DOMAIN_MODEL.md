# Modelo de dominio POO

**Estado:** Cuenta/Movimiento (T03) y Contacto/Compra/DetalleCompra (T04) implementados; Reparto/Deuda (T05) implementados.

## Implementación actual S00-T03

Cuenta y Movimiento son objetos inmutables con estado privado. IDs 0 representan objetos nuevos; IDs positivos identifican registros. Cuenta exige nombre no vacío y calcula saldo con movimientos de su propia cuenta; Movimiento exige cuenta persistida, tipo INGRESO/EGRESO y monto positivo. Dinero convierte texto decimal a centésimas long de forma exacta, sin redondear.

Cuenta no incluye todavía tipo (catálogo pendiente). Movimiento incluye descripción opcional y fecha epoch milisegundos; T06A incorpora ubicación opcional. T04 incorpora contactos/compras; T05 incorpora las clases Reparto/Deuda descritas debajo. Los saldos negativos son posibles: no se exige bloquear egresos por fondos insuficientes. Overflow se rechaza, no se convierte en saldo incorrecto.

## Implementación S00-T04

Contacto contiene ID y nombre no vacío. DetalleCompra es inmutable: descripción, cantidad entera positiva y precio unitario no negativo en centésimas; calcula su subtotal exacto. Compra exige nombre, estado PLANIFICADA/REALIZADA, fecha y uno o más detalles; referencia opcional por ID a contacto existente. Copia y protege su lista, calcula total exacto y rechaza overflow. No expone setTotal ni setter de estado.

CompraService coordina el contacto opcional y CompraRepository. Este último guarda cabecera/detalles en una transacción. No se implementa conversión de estado ni efecto contable automático; la decisión sigue pendiente.

## Implementación S00-T05

Reparto es inmutable y transitorio: exige compra persistida REALIZADA y 2+ contactos distintos, IDs positivos. Equitativo ordena IDs y distribuye el resto en centésimas a los primeros. Personalizado exige importes >= 0 y suma exacta con addExact. Mapa protegido; ninguna lógica Android/SQLite en dominio.

Deuda representa compra/contacto/monto PENDIENTE. La tabla única deudas representa la participación, asignación del costo total y deuda; Participante/Asignacion no requieren clases ni tablas separadas en este incremento. Los ítems siguen detallados en Compra. No pagos parciales ni operación PAGADA en T05.

DeudaService coordina compra, agenda y persistencia. DeudaRepository revalida el total persistido y registra atómicamente; no altera saldos. RepartoActivity captura selección/importes y muestra el resultado persistido desde el detalle de una compra.

## Implementación S00-T06A

Ubicacion es un Value Object inmutable con latitud/longitud double finitos y rangos geográficos. Movimiento continúa inmutable; el constructor anterior delega con ubicación null, y un overload admite Ubicacion opcional. getUbicacion/tieneUbicacion consultan el par. No setters ni cambios sobre movimientos previos.

MovimientoService conserva la API antigua y añade registro con ubicación. MovimientoRepository guarda/reconstruye ambos valores juntos; SQLite v4 protege pares/rangos. El saldo continúa usando únicamente tipo/monto.

CuentaActivity ofrece consentimiento por movimiento, permisos FINE/COARSE en tiempo de uso y estado visible. CapturaUbicacion coordina LocationManager durante el formulario, timeout de 15 segundos y cancelación al cerrar/salir. Sin permisos/proveedor/fix se permite registrar con null. No captura en segundo plano, geocoding ni mapas.

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

## Diseño preliminar de Participante (simplificado en T05)

**Responsabilidad:** expresar participación de un contacto en una compra compartida.

Puede ser una entidad/asociación persistente. Su forma final depende del DER definitivo.

## Diseño preliminar de Asignacion (simplificado en T05)

**Responsabilidad:** relacionar un detalle de compra con un contacto y, cuando corresponda, el monto que se le asigna.

Esta entidad permite soportar reparto por producto/servicio y distribución personalizada sin introducir lógica en la UI.

## Diseño preliminar de Deuda (implementación mínima T05 arriba)

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

## Relación conceptual preliminar (T05: Compra → Deuda → Contacto)

```text
Cuenta 1 ---- N Movimiento
Movimiento 0..1 ---- 1 Ubicacion (composición conceptual, no tabla)

Compra 1 ---- N DetalleCompra
Compra 1 ---- N Participante ---- 1 Contacto
DetalleCompra 1 ---- N Asignacion ---- 1 Contacto
Compra 1 ---- N Deuda ---- 1 Contacto
```



## Ampliación S01-T01 — Divisas y usuario del dispositivo

Sustituye precisión uniforme y mínimo de dos contactos para nuevos formularios.
Divisa es un enum inmutable con diez códigos/nombres, decimales y validación de unidad
mínima. Cuenta y Compra tienen divisa fija; los constructores históricos delegan con
null para poder reconstruir datos aún no clasificados. Los formularios nuevos y servicios
requieren moneda. Dinero mantiene escala de centésimas y añade parseo/formato por
divisa con BigDecimal exacto. No hay tipo de cambio ni suma multimoneda.

Reparto agrega montoUsuario nullable. Las operaciones equitativoConUsuario y
personalizadoConUsuario permiten un contacto más Tú, exigen suma exacta incluyendo
ambos y mantienen la cuota propia separada del mapa de contactos. El mapa sigue
inmutable y solo contiene IDs positivos reales. Para monedas sin decimales el resto
se reparte en pesos/yenes completos. Las operaciones históricas sin usuario continúan
leyendo/revalidando repartos anteriores; no se reinterpretan deudas guardadas.

## UX-01 — Relación compra y pago

Una compra puede asociarse con un movimiento de egreso único mediante compra_movimiento. La cuenta se obtiene a través del movimiento; no se duplica el saldo ni el importe. CompraService coordina el pago explícito del total y el cambio a REALIZADA; entidades siguen independientes de Android/SQLite. Repartos y deudas se mantienen como información separada del pago. Nueva conducta autorizada el 2026-11-02, pendiente de gate independiente.

## CRUD-02 — Estado y operaciones de mantenimiento

Deuda reconstruye y valida estado persistido PENDIENTE/PAGADA. Cuota cero es Sin deuda en presentación, manteniendo asociación. Repositorios realizan cambios de estado, nombres y borrados condicionados; servicios exponen operaciones y calculan totales por moneda. No se introducen setters genéricos ni dependencia Android en entidades. Edición de una planificación crea una Compra válida con la identidad y divisa originales antes de persistirla.
