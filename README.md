# LocalCash

Aplicación Android nativa en Java para gestión financiera personal local/offline.

## Fuente de autoridad

1. Documento original del docente: `LocalCash Proyecto Android.pdf` (conservar en `sources/originales/` al crear el repositorio).
2. `docs/requirements/PROJECT_SPEC.md`: traducción técnica controlada del encargo.
3. `ARCHITECTURE.md`: arquitectura aprobada.
4. `docs/design/BUSINESS_RULES.md`, `DOMAIN_MODEL.md` y `DATABASE.md`.
5. `docs/sprints/README.md` y el sprint activo.
6. Código y pruebas.

Si una decisión propuesta contradice el documento original, se debe detener la implementación y registrar la discrepancia.

## Stack aprobado

- Android Studio
- Java
- XML
- Gradle con Groovy DSL
- SQLite mediante `SQLiteOpenHelper`
- `BiometricPrompt`
- osmdroid `6.1.18`
- RecyclerView para listados cuando corresponda

No forman parte del alcance actual: Firebase, backend remoto, Room, Jetpack Compose, Clean Architecture completa ni frameworks de inyección de dependencias.

## Cómo trabajar

Leer `AGENTS.md`. Para cambios grandes o de varios pasos, seguir `.agent/PLANS.md` y crear un ExecPlan en `docs/exec-plans/active/`.

Cada tarea debe terminar en un incremento compilable y verificable. La documentación y `docs/requirements/TRACEABILITY.md` se actualizan en la misma iteración que el código.