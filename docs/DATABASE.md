# Datos de LocalCash

La persistencia financiera utiliza Cloud Firestore. Cada usuario dispone de documentos bajo `users/{uid}`.

| Colección | Datos principales |
|---|---|
| `configuracion` | Documento `principal` con la divisa de configuración |
| `cuentas` | ID, nombre, saldo inicial, divisa y contador de usos |
| `movimientos` | Cuenta, tipo INGRESO o EGRESO, monto, descripción, fecha y ubicación opcional |
| `contactos` | ID, nombre y contador de usos |
| `compras` | Nombre, estado, fecha, divisa, detalles, total, pago asociado y reparto |

Los detalles de compra contienen descripción, cantidad y precio unitario. El reparto vincula identificadores de contactos con sus importes y estados. Los contactos no representan usuarios autenticados ni comparten permisos.

## Importes y saldos

Los importes se representan en centésimas mediante `long`. USD 10,50 equivale a 1050; CLP 1.500 equivale a 150000. Las monedas sin decimales conservan múltiplos de 100. Las operaciones comprueban desbordamientos y compatibilidad de divisa.

El saldo se deriva de saldo inicial más ingresos menos egresos. El modelo admite saldos negativos. Una compra planificada no modifica el saldo; su pago crea el egreso y conserva el vínculo con la cuenta mediante `WriteBatch`. El método `LocalCashStore.transaccion` agrupa escrituras, no realiza una transacción remota de lectura y escritura.

## Acceso

`firestore.rules` restringe las rutas al UID autenticado y valida tipos, divisas y condiciones de las operaciones. Los movimientos son de solo creación. El pago utiliza el ID estable `p-{compraId}`. La validación Java de repartos complementa las restricciones remotas; las reglas no comprueban exhaustivamente toda combinación de mapas arbitrarios.

La configuración cliente Firebase se proporciona fuera del repositorio. La caché persistente permite consultar datos previamente cargados y encolar operaciones; la primera autenticación y preparación requieren conexión. No se importa automáticamente el historial SQLite.
