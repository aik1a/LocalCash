# LocalCash — Sistema multiagente

Este directorio define cómo trabajan los agentes de LocalCash sin depender del historial de chats.

## Documentos canónicos

- `WORKFLOW.md`: fases, gates, autoridad sobre commits/avance y separación de responsabilidades.
- `CONTEXT_PACKS.md`: banco de verdad y documentos que debe consultar cada rol/sprint.
- `TASK_TEMPLATE.md`: plantilla de Task Brief.
- `TASK_LIFECYCLE.md`: estados, ramas y gates de una tarea.
- `HANDOFF_TEMPLATE.md`: transferencia verificable entre agentes.
- `roles/ORCHESTRATOR.md`: contrato del Orquestador.
- `roles/CODER.md`: contrato del Codificador.
- `roles/AUDITOR.md`: contrato del Auditor.
- `prompts/`: prompts bootstrap para iniciar chats nuevos por rol.

## Regla principal

Los prompts no contienen la verdad completa del proyecto. Solo arrancan el rol. La verdad vigente se recupera del repositorio mediante `PROJECT_ROUTER.md`, `PROJECT_STATE.md` y el Context Pack correspondiente.

## Ciclo resumido

`ORQUESTADOR -> TASK BRIEF -> CODIFICADOR -> HANDOFF -> AUDITOR -> VEREDICTO -> ORQUESTADOR`

Si el Auditor falla la tarea:
`ORQUESTADOR -> TASK CORRECTIVA -> CODIFICADOR -> RE-AUDITORÍA`.

## Cambio de chat/agente

Cada chat nuevo debe recibir un rol único y usar su prompt bootstrap. La continuidad se conserva en GitHub mediante Task Briefs, handoffs, auditorías, ExecPlans y `PROJECT_STATE.md`, no mediante memoria del chat anterior.