# ExecPlans activos

Contiene planes vivos de tareas complejas actualmente en ejecución.

Crear un ExecPlan solo cuando la tarea cumpla los criterios de `.agent/PLANS.md`.

## Reglas

- un archivo por trabajo complejo;
- mantener progreso y decisiones actualizados durante la ejecución;
- registrar divergencias entre plan y realidad;
- no usar ExecPlans para tareas pequeñas;
- al finalizar y validar, mover el plan a `../completed/`.

Nombre recomendado:

`<SPRINT>-<TASK-ID>-<slug>.md`