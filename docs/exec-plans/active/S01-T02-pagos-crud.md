# S01-T02 — Plan de ejecución

## Objetivo
Saldo no negativo, compras pagadas desde cuenta elegida y CRUD básico con interfaz clara.

## Progreso
- [x] Inspección y alcance autorizado
- [x] Servicios, transacciones y migración v6
- [x] Formularios, confirmación de pago, CRUD y Toast
- [ ] Pruebas locales y CI Android
- [ ] Handoff, auditoría independiente y cierre

## Decisiones
Ver Task Brief docs/tasks/active/S01-T02-pagos-crud.md. El cobro usa total completo y moneda coincidente; planificar no descuenta. Los datos históricos se preservan sin cobro retroactivo.

## Validación
Fondos exactos/insuficientes, doble pago, cancelación, rollback, mismo código de moneda, migraciones v1–v5, CRUD con referencias, protección de egresos vinculados, reinicio y biometría. Regresión heredada adaptada al nuevo requisito sin eliminar cobertura.

## Checkpoint previo a CI
Dominio34400 y SQLite host77 PASS, XML/Python/diff correctos. SDK/emulador local no disponible; Android build/recorridos pendientes del workflow de la rama.
