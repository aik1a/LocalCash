# S01 — Persistence & Domain

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