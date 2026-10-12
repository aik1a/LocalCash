# LocalCash — Project State

**Estado del documento:** operativo  
**Responsable principal:** Agente Orquestador  
**Regla:** actualizar cuando cambie el estado real del proyecto. No usar este archivo para reemplazar requisitos, ADRs ni historial de auditorías.

## Estado actual

- **Repositorio:** `aik1a/LocalCash`
- **Rama principal:** `main`
- **Fase:** foundation / implementación inicial
- **Sprint activo:** `S00 — Foundation`
- **Estado del sprint:** IN_PROGRESS; S00-T01 cerrada, resto pendiente
- **Código Android:** proyecto base Java/XML y Gradle Groovy integrado en main mediante PR #1
- **Build validado:** evidencia histórica del 2026-10-09, `BUILD SUCCESSFUL in 25s`; no reejecutado en este cierre
- **Tests automatizados:** esqueleto sin tests; `:app:testDebugUnitTest NO-SOURCE` documentado
- **Último hito validado:** S00-T01 integrada y cerrada administrativamente el 2026-10-12 tras PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE
- **Merge S00-T01:** `3cb3d5a164bc99e5343f283efa44c4a9211426b3` ([PR #1](https://github.com/aik1a/LocalCash/pull/1))
- **Tarea activa preparada:** S00-T02, READY; aún no implementada

## Próxima acción autorizada

`S00-T02`: organizar los paquetes Java mínimos según la arquitectura aprobada, conforme a `docs/tasks/active/S00-T02-estructura-paquetes.md`.

Rol ejecutor: Codificador. Rama: `task/S00-T02-estructura-paquetes`, desde main después de este cierre. No implementar módulos, dependencias ni clases de tareas posteriores.

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

No hay bloqueo administrativo para preparar S00-T02. Las limitaciones de ejecución Android permanecen registradas para S00-T06; no se interpretan como pruebas realizadas.

## Última auditoría

`docs/audits/S00/S00-T01-reaudit.md`: PASS WITH OBSERVATIONS y APPROVED_FOR_MERGE del estado funcional `b63e5e2c4de6accb9c952022f12b07a0b614d62d`. El único commit posterior anterior al merge (`7288b9f6b40bdcb3c77385e9e138acaff96973d7`) añadió ese informe; sin cambios funcionales.

No se repitió la auditoría. El sprint S00 no está cerrado ni tiene autorización de avance al sprint siguiente.

## Validaciones pendientes — S00-T06

- OBS-02: instalar APK, abrir MainActivity, comprobar «LocalCash» y ausencia de errores de inicio.
- OBS-03: sincronizar Gradle en Android Studio y compilar desde checkout limpio sin cachés previamente preparadas.
- Conservar `NO EJECUTADO` hasta obtener evidencia real; usar los registros de `docs/handoffs/evidence/S00-T01/`.
- Mantener documentada la deprecación `android.builtInKotlin=false` (OBS-04), sin actualizar versiones solo para eliminarla.

El traslado a S00-T06 fue autorizado por el usuario el 2026-10-12 y se registra en `docs/sprints/S00-foundation.md`.

## Reglas de actualización

El Orquestador debe actualizar este archivo cuando:
- cambia el sprint activo;
- se valida o invalida un build;
- aparece o se resuelve un bloqueador;
- cambia la próxima acción autorizada;
- una decisión pendiente pasa a aceptada mediante documentación correspondiente.

No registrar aquí detalles extensos de implementación; usar handoffs, auditorías, ADRs y documentos del sprint.