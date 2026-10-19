# S01-T02 — Saldo disponible, pago de compras y CRUD
Estado: IN_PROGRESS. Autorización expresa de Aikia, 19-10-2026.
Base: main 523f3bd50c58f0f827e4e1efb864f05bd8ddea09; rama task/S01-T02-pagos-crud.
## Alcance y criterios
- Nuevas cuentas sin saldo inicial negativo; egresos manuales y edición/eliminación de movimientos nunca dejan saldo negativo. Leer saldo histórico negativo sin reescribirlo; permitir ingresos de recuperación.
- Compra planificada no descuenta. Al realizarla, preguntar cuenta de misma divisa, mostrar total/saldo y confirmar. Rechazar fondos insuficientes; guardar estado, egreso y vínculo en una sola transacción. Una compra no se cobra dos veces, incluso reintentos.
- Crear una compra realizada también requiere elección y confirmación de cuenta; no guardar compra ni egreso si falla.
- CRUD básico de cuentas (nombre y borrar vacías), contactos (nombre y borrar sin referencias), movimientos manuales (tipo/monto/descripcion y borrar con validación de saldo), compras (editar planificada con ítems, renombrar realizada, borrar sin reparto; si se pagó en esta versión, revertir su egreso atómicamente).
- No permitir editar/borrar directamente un movimiento generado por una compra. No borrar compras con deudas/reparto para no eliminar compromisos ajenos. Confirmar eliminación antes de actuar.
- Compras realizadas antiguas conservan estado/importes sin cobro retroactivo; mostrar que no tienen pago vinculado.
- Migración SQLite v5→v6 aditiva; conservar datos/FK/divisas/ubicaciones. Mantener Java/XML, biometría y FLAG_SECURE.
- Interfaz con acciones visibles, estados legibles, Toast de éxito y errores persistentes/inline; sin aclaraciones técnicas en etiquetas.
- Dominio, SQL/migración, Android repositorios, UI y regresión en CI API24/API36; documentar límites. Auditoría independiente antes de merge.
## Decisiones
Sin conversión entre divisas ni cobros parciales. Se descuenta total de compra, no solo cuota propia; reparto registra deudas y no duplica cobro. Un egreso se genera exclusivamente con confirmación expresa. Guardar compras pagadas crea estado REALIZADA; guardado planificado no toca cuentas.
## Context Pack
PROJECT_ROUTER, AGENTS, PROJECT_STATE, rol CODER, DATABASE, DOMAIN_MODEL, BUSINESS_RULES, servicios/repositories/Activities y pruebas afectadas.
