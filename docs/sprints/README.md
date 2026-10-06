# Plan de sprints

## Principio

Cada sprint produce un incremento demostrable y compilable. Un sprint se divide en tareas pequeñas; no se entrega al agente como un único prompt gigante.

Cada tarea sigue el loop:

`EXPLORE -> PLAN -> IMPLEMENT -> BUILD -> TEST -> REVIEW -> FIX -> DOCUMENT -> REPORT`

## Orden

| Sprint | Resultado |
|---|---|
| S00 | Foundation: proyecto, estructura, dependencias y navegación base |
| S01 | Persistence & Domain: modelo POO + SQLite + repositories + CRUD base |
| S02 | Security: acceso biométrico |
| S03 | Accounts & Movements: cuentas, ingresos, egresos y saldo |
| S04 | Geolocation & Maps: ubicación opcional + osmdroid |
| S05 | Purchases & Contacts: contactos, compras, detalle y planificación |
| S06 | Shared Expenses: participantes, asignaciones y deudas |
| S07 | Integration & Delivery: pruebas, correcciones, documentación y demo |

## Reglas

- No avanzar de sprint con defectos bloqueantes del anterior.
- Una tarea debe tener criterios de aceptación observables.
- Mantener `TRACEABILITY.md` al día.
- Crear ExecPlan para S06 o cualquier trabajo que crezca a varias capas/horas.
- Si el código obliga a cambiar una decisión de diseño, documentar primero la divergencia.