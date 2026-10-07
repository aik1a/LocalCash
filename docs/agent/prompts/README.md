# LocalCash — Banco de prompts para agentes

Estos archivos son los prompts canónicos para iniciar chats independientes de LocalCash.

Los prompts son **bootstraps**, no sustituyen la documentación del repositorio. Cada agente debe recuperar el contexto vigente desde GitHub mediante `PROJECT_ROUTER.md`, `PROJECT_STATE.md` y su Context Pack.

## Regla de un rol por chat

Por defecto, cada chat trabaja con un solo rol:

- **Orquestador**: gobierna trabajo, fases, tareas y cierre administrativo.
- **Codificador**: implementa una tarea acotada.
- **Auditor**: revisa independientemente y emite gates técnicos.

No mezclar Codificador y Auditor para la misma tarea salvo instrucción explícita del usuario.

## Qué prompt usar

| Necesidad | Prompt |
|---|---|
| Ordenar proyecto/sprint y crear siguiente tarea | `ORCHESTRATOR.md` |
| Implementar Task Brief | `CODER.md` |
| Corregir hallazgos ya asignados | sección correctiva de `CODER.md` |
| Auditar tarea/commit | `AUDITOR.md` |
| Auditar cierre de sprint | sección gate de sprint de `AUDITOR.md` |

## Flujo normal

`ORQUESTADOR -> TASK BRIEF -> CODIFICADOR -> HANDOFF -> AUDITOR -> VEREDICTO -> ORQUESTADOR`

Si el Auditor emite FAIL:

`ORQUESTADOR -> TAREA CORRECTIVA -> CODIFICADOR -> REAUDITORÍA`

## Autoridad resumida

| Acción | Orquestador | Codificador | Auditor |
|---|---:|---:|---:|
| Definir siguiente tarea | Sí | No | No |
| Crear Task Brief | Sí | No | No |
| Implementar código | No por defecto | Sí | No en auditoría normal |
| Ejecutar build/tests | Sí | Sí | Sí |
| Aprobar técnicamente | No | No | Sí |
| Autorizar merge | No | No | Sí |
| Formalizar cierre | Sí | No | No |
| Actualizar PROJECT_STATE | Sí | Solo si se instruye | No |
| Cerrar sprint | Sí, tras gate | No | Autoriza/no autoriza |

## Uso en un chat nuevo

1. Abrir un chat dentro del Project LocalCash.
2. Pegar el bloque **Prompt para iniciar un chat nuevo** del rol correspondiente.
3. Para Codificador/Auditor, indicar el ID/ruta del Task Brief cuando exista.
4. No copiar todos los documentos manualmente.
5. El agente debe usar GitHub como fuente operativa vigente.

## Documentos relacionados

- contratos de rol: `docs/agent/roles/`
- bancos de verdad: `docs/agent/CONTEXT_PACKS.md`
- workflow/gates: `docs/agent/WORKFLOW.md`
- ciclo de vida: `docs/agent/TASK_LIFECYCLE.md`
- handoff: `docs/agent/HANDOFF_TEMPLATE.md`
- tareas: `docs/tasks/`
