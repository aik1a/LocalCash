# Tareas de LocalCash

Este directorio conserva los Task Briefs persistentes del sistema multiagente.

## Estructura

- `active/`: tareas en DRAFT, READY, IN_PROGRESS, READY_FOR_AUDIT, CHANGES_REQUESTED o BLOCKED.
- `completed/`: tareas CLOSED después del gate técnico y cierre administrativo.

## Regla

El Orquestador crea y mantiene el Task Brief. El Codificador implementa su alcance. El Auditor emite el gate técnico. El Orquestador formaliza el cierre.

Usar:
- `docs/agent/TASK_TEMPLATE.md`
- `docs/agent/TASK_LIFECYCLE.md`
- `docs/agent/HANDOFF_TEMPLATE.md`
