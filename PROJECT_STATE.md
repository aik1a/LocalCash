# LocalCash — Project State

**Estado del documento:** operativo  
**Responsable principal:** Agente Orquestador  
**Regla:** actualizar cuando cambie el estado real del proyecto. No usar este archivo para reemplazar requisitos, ADRs ni historial de auditorías.

## Estado actual

- **Repositorio:** `aik1a/LocalCash`
- **Rama principal:** `main`
- **Fase:** foundation / implementación inicial
- **Sprint activo:** `S00 — Foundation`
- **Estado del sprint:** IN_PROGRESS; S00-T01 y S00-T02 CLOSED
- **Código Android:** proyecto base Java/XML + Gradle Groovy; MainActivity en `com.aik1a.localcash.presentation`
- **Build validado:** GitHub Actions `38267899607`: `assembleDebug` para SHA `6cea41252f26a76af44f81f990a99f0b95cf74b1`, `BUILD SUCCESSFUL in 1m 8s`, 33 tareas, código 0
- **Tests automatizados:** no hay tests implementados; S00-T02 no introdujo casos nuevos
- **Último hito validado:** S00-T02 reauditoría PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE; fusionada mediante PR #2 (`d39a35b99fe96349f06052dff01163957ed54c4f`), cierre administrativo el 2026-10-12
- **Tarea activa preparada:** S00-T03, READY; todavía no implementada

## Próxima acción autorizada

`S00-T03`: preparar la dependencia aprobada de mapas, osmdroid 6.1.18, de forma acotada y compilar el cambio, conforme a `docs/tasks/active/S00-T03-dependencias.md`.

**Rol ejecutor:** Codificador. Rama `task/S00-T03-dependencias`, creada desde el main vigente. No implementar mapas, biometría funcional, permisos, pantallas ni otras tareas.

## Decisiones vigentes

- Android nativo en Java.
- UI en XML.
- Gradle con Groovy DSL.
- Persistencia local mediante SQLite + `SQLiteOpenHelper`.
- SQLite como única fuente persistente dentro del alcance actual.
- Autenticación mediante `BiometricPrompt`.
- Mapas mediante osmdroid 6.1.18.
- Sin Firebase, backend remoto, Room, Compose, DI ni arquitectura empresarial completa salvo decisión posterior explícita.
- Arquitectura base: Presentation → Application/Services → Domain → Repositories → SQLiteOpenHelper → SQLite.
- Proyecto académico: priorizar claridad, trazabilidad y demostrabilidad sobre sobreingeniería.

## Decisiones pendientes relevantes

Resolver solo cuando el sprint correspondiente las necesite:
- esquema físico SQLite definitivo;
- tipos/constraints exactos de columnas;
- política de borrado y cascadas;
- estrategia exacta de reparto personalizado y redondeo;
- soporte o exclusión de pagos parciales;
- posible redundancia entre participantes y asignaciones;
- si completar una compra genera automáticamente un movimiento financiero;
- campos opcionales definitivos de contactos;
- estrategia de tiles/cache que garantice el comportamiento offline del mapa.

## Bloqueadores

Ninguno para asignar S00-T03. H-01 (build S00-T02) quedó resuelto. No existe autorización de cierre del sprint S00.

## Última auditoría

`docs/audits/S00/S00-T02-reaudit-H01.md`: PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE. Commit funcional `6cea41252f26a76af44f81f990a99f0b95cf74b1` compilado con Actions (run `38267899607`), y solo cambios CI/documentación después.

PR #2 merge `d39a35b99fe96349f06052dff01163957ed54c4f`. Sin reauditoría integral de S00.

## Validaciones pendientes — S00-T06

- OBS-02 / H-02: instalar APK, abrir `com.aik1a.localcash.presentation.MainActivity`, comprobar «LocalCash» y ausencia de errores de inicio; sigue NO EJECUTADO.
- OBS-03: sincronización Gradle en Android Studio; build desde checkout limpio y caché sin preparar. El runner CI de S00-T02 demuestra compilación de un checkout fijado, pero no una sincronización visual de Studio.
- OBS-04: mantener documentada la deprecación `android.builtInKotlin=false`; sin actualización arbitraria de versión.
- Workflow `s00-t02-build.yml` tiene checkout fijado al SHA antiguo. No usarlo como evidencia de commits nuevos sin actualizar la referencia y comprobar la salida.
- Mantener evidencia histórica y separar pruebas realmente ejecutadas de las pendientes.

## Reglas de actualización

El Orquestador debe actualizar este archivo cuando:
- cambia el sprint activo;
- se valida o invalida un build;
- aparece o se resuelve un bloqueador;
- cambia la próxima acción autorizada;
- una decisión pendiente pasa a aceptada mediante documentación correspondiente.

No registrar aquí detalles extensos de implementación; usar handoffs, auditorías, ADRs y documentos del sprint.