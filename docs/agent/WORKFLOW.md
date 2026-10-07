# LocalCash — Flujo multiagente y gates

## Propósito

Definir cómo cooperan Orquestador, Codificador y Auditor sin mezclar responsabilidades. Este documento regula el ciclo de trabajo; los detalles de cada rol están en `docs/agent/roles/`.

## Principio de autoridad

- El **Orquestador** decide qué trabajo se ejecuta, crea Task Briefs, mantiene el estado global y ejecuta el cierre administrativo de tareas/sprints.
- El **Codificador** implementa únicamente la tarea asignada y aporta evidencia técnica.
- El **Auditor** revisa de forma independiente y emite el veredicto que habilita o bloquea el cierre técnico y el avance.

El Auditor no reemplaza al Orquestador: **autoriza técnicamente**, mientras el Orquestador **formaliza el avance** y actualiza estado/trazabilidad.

## Fases operativas

### F0 — Orientación
Responsable principal: Orquestador.

Objetivo: conocer el estado real antes de planificar.

Entradas mínimas:
- `PROJECT_ROUTER.md`
- `PROJECT_STATE.md`
- `AGENTS.md`
- repositorio real

Salida: estado confirmado, bloqueadores detectados y sprint/tarea candidata.

### F1 — Planificación de sprint
Responsable principal: Orquestador.

Objetivo: convertir el sprint activo en tareas pequeñas y verificables.

Salida:
- tareas con ID;
- dependencias entre tareas;
- criterios de aceptación;
- Context Pack por tarea;
- necesidad o no de ExecPlan.

No se autoriza implementación si falta una decisión funcional imprescindible.

### F2 — Preparación de tarea
Responsable principal: Orquestador.

Objetivo: producir un Task Brief ejecutable por un Codificador nuevo sin depender del chat previo.

Debe incluir:
- objetivo;
- alcance y fuera de alcance;
- requisitos relacionados;
- archivos/documentos a consultar;
- criterios de aceptación;
- build/tests requeridos;
- branch/commit esperado cuando corresponda.

### F3 — Implementación
Responsable principal: Codificador.

Objetivo: implementar el Task Brief con cambios mínimos y verificables.

Ciclo normal:
`EXPLORE -> PLAN -> IMPLEMENT -> BUILD -> TEST -> REVIEW -> FIX -> DOCUMENT -> REPORT`

Para cambios pequeños el plan puede ser breve. Para trabajo complejo se usa ExecPlan.

Salida:
- código/documentación modificada;
- build/tests ejecutados;
- handoff;
- commit(s) de trabajo si fueron autorizados.

### F4 — Auditoría
Responsable principal: Auditor.

Objetivo: validar el trabajo contra requisitos, diseño y criterios de aceptación.

Veredictos:
- `PASS`
- `PASS WITH OBSERVATIONS`
- `FAIL`
- `BLOCKED`

Un `PASS` o `PASS WITH OBSERVATIONS` habilita el cierre técnico de la tarea. `FAIL` devuelve el trabajo a corrección. `BLOCKED` vuelve al Orquestador para resolver falta de evidencia, entorno o decisión.

### F5 — Corrección
Responsables: Orquestador -> Codificador -> Auditor.

El Orquestador convierte hallazgos obligatorios en una tarea correctiva acotada. El Codificador corrige. El Auditor reevalúa únicamente el alcance afectado y regresiones relevantes.

No se permite que el Auditor corrija silenciosamente el mismo código que audita.

### F6 — Cierre de tarea
Responsable principal: Orquestador.

Requiere:
- veredicto de Auditor compatible con cierre;
- evidencia de build/tests;
- documentación afectada coherente;
- trazabilidad actualizable/actualizada;
- handoff final.

El Orquestador actualiza `PROJECT_STATE.md` cuando cambie la próxima acción autorizada o el estado global.

### F7 — Cierre de sprint / avance de fase
Responsables: Auditor + Orquestador.

El Auditor realiza gate de sprint y emite autorización técnica de avance.

El Orquestador solo puede marcar el sprint como cerrado y preparar el siguiente cuando:
- todas las tareas obligatorias estén cerradas;
- no existan BLOCKER/MAJOR abiertos que impidan avance;
- build y pruebas aplicables estén validados;
- trazabilidad/documentación estén coherentes;
- el Auditor emita `APPROVED_FOR_SPRINT_ADVANCE`.

## Autoridad sobre commits y ramas

### Código
- El Codificador puede crear commits de trabajo en la rama asignada cuando el Task Brief lo autorice.
- El Codificador **no puede declarar esos commits aceptados ni fusionarlos a `main` por sí solo**.
- El Auditor revisa el diff/commit y puede emitir `APPROVED_FOR_COMMIT` o `APPROVED_FOR_MERGE` según el gate solicitado.
- El Orquestador realiza o coordina el cierre/merge después de la aprobación.

### Documentación de auditoría
El Auditor puede crear/actualizar documentos bajo `docs/audits/` sin modificar código funcional.

### Main
`main` representa estado aceptado. No debe recibir código de una tarea que no haya pasado el gate de auditoría correspondiente.

## Matriz de permisos

| Acción | Orquestador | Codificador | Auditor |
|---|---|---|---|
| Definir siguiente tarea | Sí | No | No |
| Crear Task Brief | Sí | No | No |
| Implementar código | Solo si se solicita explícitamente | Sí | No durante auditoría normal |
| Cambiar requisitos | No sin decisión del usuario/fuente | No | No |
| Actualizar docs técnicas afectadas | Sí | Sí, dentro de tarea | Puede señalar discrepancias |
| Ejecutar build/tests | Sí | Sí | Sí/validar evidencia |
| Emitir veredicto independiente | No | No | Sí |
| Autorizar merge/avance técnico | No | No | Sí |
| Actualizar `PROJECT_STATE.md` global | Sí | Solo si se le instruye | No |
| Cerrar sprint administrativamente | Sí, tras gate | No | No |
| Escribir auditoría | Puede registrar resumen | No | Sí |

## Regla de separación

Un mismo chat/agente no debe actuar como Codificador y Auditor de la misma tarea salvo instrucción explícita del usuario. Si se solicita modo correctivo al Auditor, debe conservarse primero el veredicto original.