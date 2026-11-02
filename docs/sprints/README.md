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

## Ciclo adicional MVP-PULIDO — alcance completo

Este ciclo nace del encargo de Aikia del 01/11/2026; fecha objetivo 02/11/2026. No reemplaza ni renumera los sprints históricos de la tabla anterior. Ejecución no iniciada. Entrada: docs/agent/goals/LOCALCASH-MVP-PULIDO.md.

| Sprint nuevo | Resultado | Fases | Microtareas |
|---|---|---:|---:|
| [MVP-S01](MVP-S01-base-reglas-y-proteccion-de-saldo.md) | Base, reglas y protección de saldo | 4 | 13 |
| [MVP-S02](MVP-S02-crud-de-cuentas-contactos-y-movimientos.md) | CRUD de cuentas, contactos y movimientos | 3 | 13 |
| [MVP-S03](MVP-S03-crud-y-organizacion-de-compras.md) | CRUD y organización de compras | 3 | 11 |
| [MVP-S04](MVP-S04-pago-de-compras-y-devolucion.md) | Pago de compras y devolución | 3 | 14 |
| [MVP-S05](MVP-S05-deudas-y-anulacion-de-repartos.md) | Deudas y anulación de repartos | 3 | 12 |
| [MVP-S06](MVP-S06-interfaz-gestos-respuesta-y-privacidad.md) | Interfaz, gestos, respuesta y privacidad | 4 | 16 |
| [MVP-S07](MVP-S07-regresion-apk-y-entrega.md) | Regresión, APK y entrega | 3 | 12 |

Total: 23 fases / 91 microtareas. Todos los sprints pertenecen al mismo goal y alcance; una fase es una tarea formal con varias microtareas, no una invitación a implementar el sprint completo en un parche.
