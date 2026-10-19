# S01 — Persistence & Domain

> Plan histórico preliminar anterior a la implementación de S00. El esquema integrado actual es v6, no v1. Este listado no representa tareas vigentes ni un sprint completado. El identificador S01-T01 se reutilizó para el incremento de divisas autorizado el 18-10-2026, cerrado mediante PR #13; su alcance vigente consta en [Task Brief completado](../tasks/completed/S01-T01-divisas.md). S01-T02 se reutilizó para pagos, saldo y CRUD, cerrado mediante PR #14; ver [Task Brief completado](../tasks/completed/S01-T02-pagos-crud.md). Las tareas descritas abajo conservan exclusivamente su significado histórico.

## Objetivo

Cerrar el modelo de dominio y el esquema SQLite v1, e implementar persistencia CRUD base con separación POO/persistencia.

## Tareas

- S01-T01: resolver decisiones pendientes de `DATABASE.md` necesarias para el esquema.
- S01-T02: actualizar UML/DER y congelar esquema v1.
- S01-T03: implementar entidades/value objects y enums con invariantes.
- S01-T04: implementar `LocalCashDbHelper`.
- S01-T05: implementar repositories mínimos.
- S01-T06: CRUD de persistencia necesario para validar tablas principales.
- S01-T07: pruebas de creación, inserción, consulta, actualización y eliminación aplicables.
- S01-T08: revisar encapsulamiento y evitar setters indiscriminados.
- S01-T09: actualizar documentación/trazabilidad.

## Criterios de aceptación

- SQLite se crea correctamente.
- Relaciones/constraints definidos coinciden con `DATABASE.md`.
- Entidades no conocen SQLite.
- Activities no ejecutan SQL.
- CRUD mínimo verificable.
- Build y pruebas pasan.