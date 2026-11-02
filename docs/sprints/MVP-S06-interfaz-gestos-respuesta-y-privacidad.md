# MVP-S06 — Interfaz, gestos, respuesta y privacidad

**Ciclo:** MVP-PULIDO completo · **Estado:** PLANNED · **Fecha objetivo global:** 02/11/2026.  
**Entrada:** sprint MVP-S05 CLOSED y SHA aceptado indicado por Orquestador.  
**Salida:** incremento probado y auditable de interfaz, gestos, respuesta y privacidad. Estos sprints son unidades de resultado, no semanas ni recortes de alcance.

## Fases y microtareas

| Fase / Task Brief | Resultado | Rol | Microtareas |
|---|---|---|---:|
| [MVP-S06-T01](../tasks/active/MVP-S06-T01-coherencia-visual-y-navegacion.md) | Coherencia visual y navegación | Codificador | 4 |
| [MVP-S06-T02](../tasks/active/MVP-S06-T02-acciones-visibles-y-swipe-izquierdo.md) | Acciones visibles y swipe izquierdo | Codificador | 4 |
| [MVP-S06-T03](../tasks/active/MVP-S06-T03-respuesta-y-ciclo-de-vida.md) | Respuesta y ciclo de vida | Codificador | 4 |
| [MVP-S06-T04](../tasks/active/MVP-S06-T04-privacidad-cobertura-y-documentacion-de-ux.md) | Privacidad, cobertura y documentación de UX | Codificador | 4 |

Orden: MVP-S06-T01 → MVP-S06-T02 → MVP-S06-T03 → MVP-S06-T04. Cada fase se completa con sus microtareas, evidencia y gate correspondiente antes de activar la siguiente.

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
