# Estructura mínima de paquetes Java

**Tarea:** S00-T02
**Namespace y applicationId:** `com.aik1a.localcash`, sin cambios.

## Estructura implementada

| Paquete | Clase real | Responsabilidad |
|---|---|---|
| `com.aik1a.localcash.presentation` | `MainActivity` | Mostrar el layout inicial `activity_main` |

Ruta: `app/src/main/java/com/aik1a/localcash/presentation/MainActivity.java`.

El manifiesto registra `.presentation.MainActivity` como launcher. La clase importa `com.aik1a.localcash.R`, generada por Android a partir del namespace del módulo; R no se mueve ni se crea manualmente.

MainActivity sigue extendiendo Activity del framework. Su onCreate conserva `super.onCreate(savedInstanceState)` y `setContentView(R.layout.activity_main)`. El layout y `app_name = LocalCash` no se modifican.

## Capas futuras, todavía no implementadas

La separación aprobada está en `ARCHITECTURE.md`:

- Application/Services: coordinar operaciones cuando una tarea real lo requiera.
- Domain: entidades y reglas de negocio sin dependencia de UI Android ni SQLite.
- Repositories: encapsular persistencia y mapeo de datos.
- SQLiteOpenHelper/SQLite: administración y almacenamiento local de la base.

No se crearon paquetes, clases, interfaces ni directorios vacíos para estas capas. Sus nombres y archivos concretos se documentarán cuando se implementen las tareas correspondientes.

## Validación

La estructura y las referencias se revisaron sobre los archivos versionados en GitHub. Build y lanzamiento de la nueva clase: NO EJECUTADO por bloqueo de creación de procesos en el sandbox Windows.

Evidencia de S00-T02: `docs/handoffs/evidence/S00-T02/verification.json`. Las validaciones Android pendientes de S00-T01 permanecen asignadas a S00-T06; no se presentan como comprobadas por este cambio.
