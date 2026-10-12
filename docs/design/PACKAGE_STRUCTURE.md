# Estructura de paquetes Java — S00-T03

Namespace/applicationId: `com.aik1a.localcash`.

| Paquete | Clases | Responsabilidad |
|---|---|---|
| presentation | MainActivity, CuentaActivity, Pantalla | Lista/formulario de cuentas, historial/formulario de movimientos, márgenes del sistema |
| application | MovimientoService | Coordinar repositories, validación de saldo y transacción de registro |
| domain | Cuenta, Movimiento, Dinero | Invariantes, saldo derivado y conversión monetaria exacta, sin Android/SQL |
| data | LocalCashDbHelper, CuentaRepository, MovimientoRepository | Esquema SQLite v1, inserción y lectura con parámetros, reconstrucción de dominio |

Launcher `.presentation.MainActivity`; `.presentation.CuentaActivity` interna (no exportada). No hay SQL ni cálculo de saldo en Activities. No se crearon capas vacías ni clases de módulos futuros.

Validación del incremento y SHA exacto: `docs/handoffs/S00-T03-coder.md`. Historial S00-T02 permanece en sus handoffs originales.
