# MVP-S04 — Pago de compras y devolución

**Ciclo:** MVP-PULIDO completo · **Estado:** PLANNED · **Fecha objetivo global:** 02/11/2026.  
**Entrada:** sprint MVP-S03 CLOSED y SHA aceptado indicado por Orquestador.  
**Salida:** incremento probado y auditable de pago de compras y devolución. Estos sprints son unidades de resultado, no semanas ni recortes de alcance.

## Fases y microtareas

| Fase / Task Brief | Resultado | Rol | Microtareas |
|---|---|---|---:|
| [MVP-S04-T01](../tasks/active/MVP-S04-T01-vinculo-compra-pago-y-migracion.md) | Vínculo compra-pago y migración | Codificador | 4 |
| [MVP-S04-T02](../tasks/active/MVP-S04-T02-comprar-desde-cuenta.md) | Comprar desde cuenta | Codificador | 5 |
| [MVP-S04-T03](../tasks/active/MVP-S04-T03-deshacer-y-eliminar-compra-pagada.md) | Deshacer y eliminar compra pagada | Codificador | 5 |

Orden: MVP-S04-T01 → MVP-S04-T02 → MVP-S04-T03. Cada fase se completa con sus microtareas, evidencia y gate correspondiente antes de activar la siguiente.

## Contexto mínimo

PROJECT_ROUTER.md, AGENTS.md, PROJECT_STATE.md; plan maestro docs/exec-plans/active/MVP-PULIDO-COMPLETO.md; contratos docs/design/MVP-PULIDO-CONTRATOS.md; Task Brief actual. No cargar el resto de sprints ni toda la auditoría por defecto.

## Criterios de salida

- Todas las fases de este sprint CLOSED con evidencia, no solo microtareas marcadas.
- Cero BLOCKER/MAJOR abiertos que invaliden el incremento.
- Pruebas pertinentes y build del código final; límites de entorno explícitos.
- Auditoría independiente del incremento y gate APPROVED_FOR_SPRINT_ADVANCE.
- Orquestador actualiza estado/trazabilidad y baseline del siguiente sprint.
- Sin cambiar decisiones del stack ni reducir alcance por la fecha.

## Seguimiento

El estado operativo detallado está en docs/agent/goals/LOCALCASH-MVP-PULIDO.queue.json. Este documento describe el resultado; no contiene checks marcados como hechos.
