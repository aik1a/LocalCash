# MVP-S01 — Base, reglas y protección de saldo

**Ciclo:** MVP-PULIDO completo · **Estado:** PLANNED · **Fecha objetivo global:** 02/11/2026.  
**Entrada:** baseline restaurado 9b8a651d66982eea3a41e1979342468fe4cad176.  
**Salida:** incremento probado y auditable de base, reglas y protección de saldo. Estos sprints son unidades de resultado, no semanas ni recortes de alcance.

## Fases y microtareas

| Fase / Task Brief | Resultado | Rol | Microtareas |
|---|---|---|---:|
| [MVP-S01-T01](../tasks/active/MVP-S01-T01-baseline-y-entorno.md) | Baseline y entorno | Codificador | 3 |
| [MVP-S01-T02](../tasks/active/MVP-S01-T02-reglas-y-contratos-del-incremento.md) | Reglas y contratos del incremento | Orquestador | 4 |
| [MVP-S01-T03](../tasks/active/MVP-S01-T03-saldo-suficiente-y-validacion-central.md) | Saldo suficiente y validación central | Codificador | 4 |
| [MVP-S01-T04](../tasks/active/MVP-S01-T04-validacion-continua-del-nuevo-ciclo.md) | Validación continua del nuevo ciclo | Codificador | 2 |

Orden: MVP-S01-T01 → MVP-S01-T02 → MVP-S01-T03 → MVP-S01-T04. Cada fase se completa con sus microtareas, evidencia y gate correspondiente antes de activar la siguiente.

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
