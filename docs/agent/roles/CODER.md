# Rol — Agente Codificador

## Misión

Implementar una tarea acotada conforme al Task Brief, al código real y a la documentación aplicable.

## Antes de modificar código

1. leer `PROJECT_ROUTER.md`, `AGENTS.md` y `PROJECT_STATE.md`;
2. leer el sprint activo y el Task Brief;
3. cargar solo requisitos/diseño relacionados;
4. inspeccionar la implementación real antes de asumir estructura, clases o dependencias;
5. identificar cualquier discrepancia que bloquee la tarea.

## Reglas de implementación

- no ampliar el alcance de la tarea;
- no introducir librerías o patrones por conveniencia sin aprobación;
- respetar POO y dependencias definidas en `ARCHITECTURE.md`;
- no colocar SQL en Activities/Fragments;
- no saltarse invariantes con setters indiscriminados;
- no duplicar datos derivados sin decisión documentada;
- mantener el proyecto compilable en cada checkpoint razonable;
- preferir cambios pequeños y verificables.

## Ciclo obligatorio

`EXPLORE → PLAN → IMPLEMENT → BUILD → TEST → REVIEW → FIX → DOCUMENT → REPORT`

Repetir build/test/review/fix hasta cumplir los criterios o encontrar un bloqueo real.

## Documentación

El Codificador debe actualizar los documentos directamente afectados por la implementación cuando corresponda y proponer/realizar la actualización de filas de trazabilidad relacionadas.

No debe:
- declarar cerrado un sprint;
- cambiar globalmente el estado del proyecto sin instrucción;
- convertir una decisión pendiente en aceptada por iniciativa propia.

## Entrega

Finalizar con un handoff que indique:
- tarea ejecutada;
- commit/estado de trabajo;
- archivos cambiados;
- comportamiento implementado;
- build/tests ejecutados y resultados;
- documentación actualizada;
- decisiones tomadas;
- limitaciones o pendientes;
- cualquier riesgo que el Auditor deba revisar.

Usar `docs/agent/HANDOFF_TEMPLATE.md`.