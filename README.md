# LocalCash

Aplicación Android académica en Java para gestión financiera personal local/offline.

## Estado

El repositorio contiene la aplicación Android implementada: cuentas, movimientos, compras, contactos, deudas y biometría, con SQLite local. Esta rama incorpora la corrección **MAP-05** del mapa. Consulta el handoff y los resultados de validación de la corrección antes de integrarla a main.

Consultar primero:
1. `PROJECT_ROUTER.md`
2. `PROJECT_STATE.md`
3. `AGENTS.md`

## Pilares del proyecto

- Android nativo en Java.
- Persistencia local SQLite mediante `SQLiteOpenHelper`.
- Autenticación biométrica.
- Movimientos con geolocalización opcional y visualización en mapa.
- Compras, contactos, gastos compartidos y deudas.
- Funcionamiento principal offline.
- Sin backend/Firebase/Room/Compose dentro del alcance actual.

## Documentación

- Arquitectura: `ARCHITECTURE.md`
- Requisitos: `docs/requirements/PROJECT_SPEC.md`
- Trazabilidad: `docs/requirements/TRACEABILITY.md`
- Dominio: `docs/design/DOMAIN_MODEL.md`
- Base de datos: `docs/design/DATABASE.md`
- Reglas de negocio: `docs/design/BUSINESS_RULES.md`
- Sprints: `docs/sprints/`
- Testing: `docs/testing/`
- Operación multiagente: `docs/agent/`
- Auditorías: `docs/audits/`
- ExecPlans: `docs/exec-plans/`

## Flujo de agentes

`ORQUESTADOR → TASK BRIEF → CODIFICADOR → BUILD/TEST → HANDOFF → AUDITOR → ORQUESTADOR`

GitHub es la fuente de verdad mutable del proyecto. Library conserva un espejo persistente de documentos y artefactos importantes.

## Corrección MAP-05: mapa mundial offline

La rama de corrección incorpora base mundial offline, calles de Chile e importación local de archivos Mapsforge para otras zonas. Al abrir, solicita la ubicación actual; el botón se llama **Mi ubicación**. No hay bloqueo a Santiago Centro. La base mundial es general: el detalle de calles requiere el paquete regional correspondiente.

En Windows: `gradlew.bat :app:assembleDebug`. En Linux/macOS: `bash gradlew :app:assembleDebug`.

Primer build: internet para preparar los archivos cartográficos, además del SDK/Gradle. `bash gradlew :app:assembleDebug` ejecuta la preparación automáticamente y reutiliza los assets generados. El APK incluye los datos y funciona sin conexión desde primera apertura. El archivo Chile ocupa aproximadamente 651 MB; se necesita espacio adicional durante instalación. Procedencia/hashes en `assets/maps/vector/provenance.json` del APK. Ver [ADR-005](docs/design/decisions/ADR-005-mapa-mundial-offline.md).

