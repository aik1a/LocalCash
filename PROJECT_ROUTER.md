# LocalCash — Project Router

**Propósito:** ser el punto de entrada para cualquier agente que trabaje en LocalCash. Este archivo no reemplaza la documentación técnica: indica qué leer, qué fuente manda y cómo cargar solo el contexto necesario.

## 1. Fuentes de verdad

LocalCash distingue entre fuente normativa y fuente operativa.

### Fuente normativa
- Encargo docente original: `LocalCash Proyecto Android.pdf`.
- Especificación derivada y trazable: `docs/requirements/PROJECT_SPEC.md`.
- Decisiones aceptadas: `docs/design/decisions/`.

Si una decisión interna contradice el encargo docente, prevalece el encargo y la discrepancia debe escalarse al Orquestador.

### Fuente operativa
- Código y documentación versionada del repositorio GitHub.
- Estado actual: `PROJECT_STATE.md`.
- Sprint activo: `docs/sprints/<sprint-activo>.md`.
- Trazabilidad: `docs/requirements/TRACEABILITY.md`.

Para saber **qué debe existir**, consultar requisitos y decisiones. Para saber **qué existe realmente**, inspeccionar el repositorio y validar build/tests. Nunca asumir implementación desde un documento.

## 2. Orden mínimo de lectura

Todo agente:
1. `PROJECT_ROUTER.md`
2. `AGENTS.md`
3. `PROJECT_STATE.md`

Después, según el rol:

### Orquestador
- `docs/requirements/PROJECT_SPEC.md`
- `docs/requirements/TRACEABILITY.md`
- `docs/sprints/README.md`
- sprint activo
- auditorías/handoffs relevantes
- documentos de diseño afectados

### Codificador
- sprint activo
- tarea asignada
- únicamente los documentos de diseño/requisitos relacionados con esa tarea
- código real afectado por la tarea

### Auditor
- criterios de aceptación de la tarea/sprint
- requisitos relacionados
- arquitectura/reglas afectadas
- diff/commits a auditar
- resultados de build y pruebas

## 3. Carga selectiva de contexto

No cargar todos los documentos del repositorio por defecto.

Ejemplos:
- Persistencia: `DATABASE.md`, `DOMAIN_MODEL.md`, ADR SQLite y sprint correspondiente.
- Cuentas/movimientos: `DOMAIN_MODEL.md`, `BUSINESS_RULES.md`, `DATABASE.md`, S03.
- Mapas: `ARCHITECTURE.md`, `DATABASE.md` si persiste coordenadas, S04.
- Seguridad: `ARCHITECTURE.md`, S02.
- Gastos compartidos: `DOMAIN_MODEL.md`, `BUSINESS_RULES.md`, `DATABASE.md`, S06 y ExecPlan activo si existe.

## 4. Resolución de discrepancias

Si código, documentación y requisitos no coinciden:
1. no inventar una reconciliación;
2. identificar exactamente la discrepancia;
3. determinar si afecta alcance, diseño o implementación;
4. registrarla en el handoff o auditoría;
5. escalar al Orquestador si requiere decisión;
6. actualizar la documentación solo cuando exista una decisión o implementación verificada.

## 5. Roles

Las reglas específicas están en:
- `docs/agent/roles/ORCHESTRATOR.md`
- `docs/agent/roles/CODER.md`
- `docs/agent/roles/AUDITOR.md`

Los agentes deben actuar solo dentro del rol asignado.

## 6. Handoffs

Cada cambio de agente debe dejar un handoff breve y verificable siguiendo `docs/agent/HANDOFF_TEMPLATE.md`.

Los handoffs registran estado y continuidad; no reemplazan `PROJECT_STATE.md`, `TRACEABILITY.md` ni los ExecPlans.

## 7. Library y GitHub

- **GitHub** es la fuente de verdad mutable para código y documentación de trabajo.
- **Library** conserva un espejo persistente de documentos y artefactos importantes.
- Si una copia de Library y GitHub difieren, usar GitHub para trabajo operativo y sincronizar Library en un checkpoint.
- El PDF docente y referencias visuales pueden mantenerse en Library/Project Sources como fuentes originales.
- Los chats son deliberación temporal y no constituyen fuente de verdad del proyecto.

## 8. Regla de avance

No avanzar de tarea o sprint solo por haber escrito código. El avance requiere los criterios definidos en `AGENTS.md`, resultados verificables y el control del Orquestador.

## 9. Sistema multiagente

Para coordinación por roles:
- flujo y gates: `docs/agent/WORKFLOW.md`
- bancos de verdad/contexto: `docs/agent/CONTEXT_PACKS.md`
- roles: `docs/agent/roles/`
- prompts para chats nuevos: `docs/agent/prompts/`
- Task Brief: `docs/agent/TASK_TEMPLATE.md`
- ciclo de vida y ramas de tareas: `docs/agent/TASK_LIFECYCLE.md`
- tareas activas/cerradas: `docs/tasks/active/` y `docs/tasks/completed/`
- handoff: `docs/agent/HANDOFF_TEMPLATE.md`

El prompt de un agente es solo un bootstrap. No reemplaza el repositorio como fuente de verdad.

## 10. Ciclo de pulido completo MVP

Para el encargo de pulido con fecha objetivo 02/11/2026, comenzar en docs/agent/goals/LOCALCASH-MVP-PULIDO.md. El ExecPlan es docs/exec-plans/active/MVP-PULIDO-COMPLETO.md; las reglas de incremento están en docs/design/MVP-PULIDO-CONTRATOS.md. La cola y checkpoint del goal indican la fase/microtarea actual. Incluye 7 sprints MVP-S01–MVP-S07, 23 fases y 91 microtareas, sin sustituir ni reabrir por nombre el roadmap histórico S00–S07. Estado inicial: PREPARED, implementación no iniciada.

## 11. Ejecución actual limitada hasta MVP-S03 (02/11/2026)

Para el encargo posterior de trabajar solo hasta S03, comenzar en docs/agent/goals/LOCALCASH-MVP-HASTA-S03.md y usar su cola/checkpoint propios. Prompt para OTRO CHAT de Work: docs/agent/prompts/WORK-MVP-HASTA-S03.md; prompt de Codificador: docs/agent/prompts/CODER-MVP-HASTA-S03.md. Rama preparada: task/mvp-s01-s03-goal-20261102. El plan completo permanece como referencia, pero esta ejecución comprende únicamente 3 sprints/10 fases/37 microtareas y se detiene antes de MVP-S04. Documentación preparada; ejecución funcional no iniciada en este chat.
