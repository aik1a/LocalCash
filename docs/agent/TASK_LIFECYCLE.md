# LocalCash — Ciclo de vida de tareas

## Propósito

Dar a cada chat/agente una unidad de trabajo persistente y verificable. El Orquestador crea la tarea; el Codificador implementa; el Auditor autoriza técnicamente; el Orquestador la cierra.

## Ubicación

- tareas activas: `docs/tasks/active/`
- tareas cerradas: `docs/tasks/completed/`

Nombre recomendado:
`Sxx-Tyy-descripcion-corta.md`

Ejemplo:
`S00-T01-proyecto-android-base.md`

## Estados

- `DRAFT`: el Orquestador la está preparando.
- `READY`: criterios/contexto completos; puede asignarse.
- `IN_PROGRESS`: Codificador trabajando.
- `READY_FOR_AUDIT`: implementación y handoff disponibles.
- `CHANGES_REQUESTED`: Auditor emitió FAIL y requiere corrección.
- `BLOCKED`: falta entorno, evidencia o decisión externa.
- `APPROVED`: Auditor emitió gate técnico favorable.
- `CLOSED`: Orquestador completó cierre, trazabilidad/estado y archivado.

## Autoridad de estado

- Orquestador: DRAFT, READY, IN_PROGRESS, CLOSED y resolución de BLOCKED.
- Codificador: reporta que una tarea está lista para auditoría mediante handoff; no la cierra.
- Auditor: emite el veredicto/gate que habilita APPROVED o devuelve CHANGES_REQUESTED/BLOCKED.

El registro formal del estado puede actualizarlo el Orquestador para mantener una sola autoridad documental.

## Ramas

Para cambios de código, usar por defecto una rama de tarea:
`task/Sxx-Tyy-descripcion`

Reglas:
- partir del estado aceptado indicado por el Orquestador;
- commits de trabajo permitidos en la rama de tarea;
- no fusionar a `main` sin gate del Auditor;
- `main` representa trabajo aceptado;
- cambios documentales pequeños de orquestación pueden ir directo a `main` cuando no alteren código funcional ni requieran gate técnico.

## Gates

### Gate de implementación
Antes de `READY_FOR_AUDIT`:
- criterios implementados;
- build/tests aplicables ejecutados;
- handoff creado;
- diff revisado por el Codificador.

### Gate de auditoría
El Auditor declara explícitamente cuando aplique:
- `APPROVED_FOR_COMMIT` / `NOT_APPROVED_FOR_COMMIT`
- `APPROVED_FOR_MERGE` / `NOT_APPROVED_FOR_MERGE`
- `APPROVED_FOR_SPRINT_ADVANCE` / `NOT_APPROVED_FOR_SPRINT_ADVANCE`

### Gate de cierre
El Orquestador mueve la tarea a `completed/` solo después de:
- aprobación técnica;
- merge/cierre correspondiente;
- trazabilidad/documentación coherente;
- `PROJECT_STATE.md` actualizado si cambió la próxima acción.

## Correcciones

Un FAIL no reabre el alcance completo. El Orquestador convierte los hallazgos obligatorios en una corrección acotada sobre la misma tarea o en una tarea correctiva nueva cuando el alcance sea significativo.