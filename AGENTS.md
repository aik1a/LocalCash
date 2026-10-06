# LocalCash — instrucciones para agentes

## 1. Objetivo

Implementar LocalCash respetando estrictamente el alcance académico. El repositorio y su documentación son la fuente de contexto; no depender de conversaciones externas.

## 2. Lectura obligatoria antes de modificar código

1. `docs/requirements/PROJECT_SPEC.md`
2. `ARCHITECTURE.md`
3. `docs/design/BUSINESS_RULES.md`
4. `docs/design/DOMAIN_MODEL.md`
5. `docs/design/DATABASE.md`
6. `docs/requirements/TRACEABILITY.md`
7. `docs/sprints/README.md`
8. Documento del sprint activo

Para una tarea compleja, leer además `.agent/PLANS.md` y su ExecPlan activo.

## 3. Restricciones del proyecto

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

## 4. Reglas POO

- Estado de entidades `private`.
- No generar getters/setters indiscriminadamente.
- Constructores y métodos deben preservar invariantes.
- Preferir operaciones de dominio (`marcarComoPagada`, `calcularTotal`) sobre setters de estado.
- Las entidades no conocen SQLite ni la UI Android.
- Las Activities/Fragments no contienen SQL ni cálculos de negocio.
- Repositories encapsulan persistencia.
- Services se crean solo para coordinar operaciones que involucran varias entidades/repositorios.
- Value Objects simples pueden existir sin tabla propia; `Ubicacion` es el caso inicial.

## 5. Persistencia

SQLite es la única fuente persistente de verdad del alcance actual. Mantener claves foráneas y transacciones cuando una operación modifica varias filas relacionadas. No duplicar datos derivados sin una razón documentada.

## 6. Loop de trabajo

Para cada tarea:

EXPLORE -> PLAN -> IMPLEMENT -> BUILD -> TEST -> REVIEW -> FIX -> DOCUMENT -> REPORT

Repetir BUILD/TEST/REVIEW/FIX hasta cumplir los criterios de aceptación o hasta detectar un bloqueo real.

No declarar una tarea terminada solo porque el código fue escrito.

## 7. Definition of Done

Una tarea está DONE únicamente cuando:
- cumple sus criterios de aceptación;
- el proyecto compila;
- las pruebas aplicables pasan;
- el flujo manual requerido fue verificado o queda explícitamente pendiente por limitación de entorno;
- no se introdujeron dependencias o funcionalidades fuera de alcance;
- se revisó el diff;
- la documentación afectada coincide con la implementación;
- `TRACEABILITY.md` está actualizado;
- no queda un defecto bloqueante conocido.

## 8. Control de alcance

Si falta una regla necesaria para implementar correctamente:
1. no inventarla silenciosamente;
2. revisar la documentación;
3. registrar la decisión pendiente o discrepancia;
4. proponer la alternativa mínima compatible con el encargo.

## 9. Planificación

Tareas pequeñas: plan breve dentro de la ejecución.

Features complejas, refactors significativos o trabajo de varias horas: usar un ExecPlan conforme a `.agent/PLANS.md`, mantenerlo actualizado y moverlo a `docs/exec-plans/completed/` al finalizar.