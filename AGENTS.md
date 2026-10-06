# LocalCash — instrucciones para agentes

## 1. Objetivo

Implementar LocalCash respetando estrictamente el alcance académico. El repositorio y su documentación son la fuente de contexto; no depender de conversaciones externas.

## 2. Entrada obligatoria

Antes de modificar código o emitir una auditoría:
1. `PROJECT_ROUTER.md`
2. `PROJECT_STATE.md`
3. este `AGENTS.md`

Luego seguir el Context Pack indicado por el Orquestador. No cargar todos los documentos por defecto.

## 3. Fuente de verdad y sincronización

- GitHub es la fuente de verdad mutable de código y documentación operativa.
- El encargo docente original es la fuente normativa primaria.
- Library mantiene un espejo persistente de documentos/artefactos importantes, pero no debe desplazar una versión más reciente de GitHub.
- Los chats no son fuente de verdad.
- Ante discrepancias, aplicar el protocolo de `PROJECT_ROUTER.md`.

## 4. Roles

Leer las reglas del rol asignado:
- Orquestador: `docs/agent/roles/ORCHESTRATOR.md`
- Codificador: `docs/agent/roles/CODER.md`
- Auditor: `docs/agent/roles/AUDITOR.md`

Ningún agente debe asumir automáticamente funciones de otro rol.

## 5. Restricciones del proyecto

Proyecto académico de segundo año. Priorizar claridad, cumplimiento, POO correcta, persistencia y demostrabilidad por sobre sofisticación arquitectónica.

Stack aprobado:
- Android Studio + Java.
- XML para interfaz.
- Gradle/Groovy.
- SQLite + `SQLiteOpenHelper`.
- `BiometricPrompt`.
- osmdroid 6.1.18.

No introducir sin decisión explícita documentada:
- Firebase o backend.
- Room.
- Jetpack Compose.
- Clean Architecture completa.
- frameworks de dependency injection.
- microservicios, sincronización remota u otras funcionalidades fuera del alcance.

## 6. Reglas POO

- Estado de entidades `private`.
- No generar getters/setters indiscriminadamente.
- Constructores y métodos deben preservar invariantes.
- Preferir operaciones de dominio (`marcarComoPagada`, `calcularTotal`) sobre setters de estado.
- Las entidades no conocen SQLite ni la UI Android.
- Las Activities/Fragments no contienen SQL ni cálculos de negocio.
- Repositories encapsulan persistencia.
- Services se crean solo para coordinar operaciones que involucran varias entidades/repositorios.
- Value Objects simples pueden existir sin tabla propia; `Ubicacion` es el caso inicial.

## 7. Persistencia

SQLite es la única fuente persistente de verdad del alcance actual. Mantener claves foráneas y transacciones cuando una operación modifica varias filas relacionadas. No duplicar datos derivados sin una razón documentada.

## 8. Loop de trabajo

Para cada tarea:

`EXPLORE -> PLAN -> IMPLEMENT -> BUILD -> TEST -> REVIEW -> FIX -> DOCUMENT -> REPORT`

Repetir BUILD/TEST/REVIEW/FIX hasta cumplir criterios de aceptación o detectar un bloqueo real.

No declarar una tarea terminada solo porque el código fue escrito.

## 9. Definition of Done

Una tarea está DONE únicamente cuando:
- cumple sus criterios de aceptación;
- el proyecto compila cuando la tarea afecta código compilable;
- las pruebas aplicables pasan;
- el flujo manual requerido fue verificado o queda explícitamente pendiente por limitación de entorno;
- no se introdujeron dependencias o funcionalidades fuera de alcance;
- se revisó el diff;
- la documentación afectada coincide con la implementación;
- la trazabilidad relacionada está actualizada o entregada al Orquestador para cierre;
- no queda un defecto bloqueante conocido;
- existe handoff verificable si cambia el agente responsable.

## 10. Control de alcance

Si falta una regla necesaria para implementar correctamente:
1. no inventarla silenciosamente;
2. revisar la documentación;
3. registrar la decisión pendiente o discrepancia;
4. proponer la alternativa mínima compatible con el encargo;
5. escalar al Orquestador cuando afecte alcance o comportamiento no definido.

## 11. Planificación

Tareas pequeñas: plan breve dentro de la ejecución.

Features complejas, refactors significativos o trabajo de varias horas: usar un ExecPlan conforme a `.agent/PLANS.md`, mantenerlo actualizado y moverlo a `docs/exec-plans/completed/` al finalizar.

## 12. Handoffs y auditoría

- Usar `docs/agent/HANDOFF_TEMPLATE.md` al transferir trabajo.
- Auditorías independientes se guardan bajo `docs/audits/`.
- El Auditor no corrige el código durante una auditoría normal.
- El Orquestador es quien autoriza avance, corrección o cierre.