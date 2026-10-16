# LocalCash — Project State

**Estado del documento:** operativo  
**Responsable principal:** Agente Orquestador  
**Regla:** actualizar cuando cambie el estado real del proyecto. No usar este archivo para reemplazar requisitos, ADRs ni historial de auditorías.

## Estado actual

- **Repositorio:** `aik1a/LocalCash`, `main`. **Fecha objetivo de entrega:** 19 de octubre de 2026.
- **Sprint:** S00, `IN_PROGRESS` (**no cerrado**).
- **Tareas cerradas:** S00-T01, T02, T03, T04, T05, T06A (GPS opcional) y **T06C (biometría)**.
- **Funcionalidades integradas:** cuentas/movimientos/saldos, contactos/compras, gastos compartidos/deudas, ubicación opcional y acceso biométrico mediante AndroidX `BiometricPrompt`. SQLite **v4**, Java/XML, almacenamiento financiero local.
- **Último merge funcional:** [PR #7](https://github.com/aik1a/LocalCash/pull/7) — `1c944cde59ed6aba8e8af2f9529b29435e40af59` (T06C).
- **Gate T06C:** [auditoría independiente](docs/audits/S00/S00-T06C-audit.md) `PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE`, 97,5/100, sin defectos BLOCKER/MAJOR; observación documental para T07.
- **CI T06C:** [run 38625086287](https://github.com/aik1a/LocalCash/actions/runs/38625086287), SHA `0f79e6c57be54144b1a756b3aff3a9644b025ca8`, build exit 0, 5483 comprobaciones por API24 y API36, pruebas biométricas y regresión T03–T06A PASS.
- **Siguiente incremento:** S00-T06B (**READY**) — osmdroid y atlas offline georreferenciado, conforme a [ADR-002](docs/design/decisions/ADR-002-osmdroid-cartografia-offline.md). La cartografía todavía no se implementa; S00-T07 pendiente.

## Próxima acción autorizada

**Agente Codificador — S00-T06B: mapa offline.** Leer `docs/tasks/active/S00-T06B-mapas.md` (READY), [ADR-002](docs/design/decisions/ADR-002-osmdroid-cartografia-offline.md) (ACCEPTED) y contexto del router. Rama autorizada `task/S00-T06B-mapas` creada desde `main` que incorpora T06C PR #7.

Implementar `MapView` osmdroid 6.1.18 y marcadores de movimientos GPS existentes, **mosaicos reales PNG propios derivados de datos OSM ODbL** en ZIP APK, cobertura declarada Santiago Centro z14–16, proveedor local sin fallback online, atribución permanente. Validar **instalación limpia sin red antes de la primera apertura**, biometría intacta, SQLite v4, emulador, build SHA exacto y regresión T03–T06C; entregar evidencia, APK, handoff y **una auditoría independiente**.

**Límites:** Codificador no fusiona, no modifica `PROJECT_STATE.md`, no cierra T06B/S00 y no altera decisiones ADR. Si el ZIP o su origen legal no son verificables, escalar el bloqueo al Orquestador, sin declarar PASS.

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

- **T06B — decisión cerrada de diseño:** [ADR-002](docs/design/decisions/ADR-002-osmdroid-cartografia-offline.md) ACCEPTED; fuente: datos vectoriales de OSM con licencia/atribución, render de mosaicos propio y empaquetado en APK `LocalCashOSM/z/x/y.png`, cobertura Santiago Centro bbox WGS84 [-33.465,-70.675,-33.435,-70.645], zoom 14–16. Implementación/prueba **pendientes**, no confundirse con aprobación de diseño.
- **T07:** validar compatibilidad de firma APK e instalación/actualización preservando datos, pruebas integrales y en dispositivo físico si disponible, depuración documental histórica H-01 de T05/T06A/T06C, documentación/evidencias de entrega y gate del sprint.
- **Decisiones fuera de alcance:** tipo de cuenta, compras que creen movimientos automáticamente, pagos parciales y campos extra de contacto; no ampliar sin autorización expresa.

## Bloqueadores y riesgos

- **No hay bloqueo de planificación para iniciar T06B**, porque el atlas y su procedencia están definidos en ADR-002. Sí falta crear recursos cartográficos reales y demostrar que se ven offline desde primer uso: si la generación falla o la licencia no es verificable, detener T06B y escalar.
- Nunca obtener paquetes de mosaicos predescargados desde `tile.openstreetmap.org`: su política prohíbe prefetch y uso offline.
- Firmas APK de iteraciones anteriores difieren; **no desinstalar instalaciones con datos** como modo de probar actualización. Resolver/documentar en T07.
- GPS solo aproximado y equipo físico aún no probados; no presentar emulador como prueba física.
- Mantenimiento: advertencias Gradle heredadas sin bloqueo demostrado; `FLAG_SECURE` biométrico puede ocultar capturas, debe mantenerse y sustituirse evidencia con XML/logs/PNG del ZIP real.

## Última auditoría

`docs/audits/S00/S00-T06C-audit.md`: **PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE**, 97,5/100; [PR #7](https://github.com/aik1a/LocalCash/pull/7) fusionado. Riesgo/documentación H-01 pendiente T07. El sprint S00 no está cerrado.

## Seguimiento pendiente

- **S00-T06B READY:** generar cartografía Santiago Centro OSM con procedencia, ZIP osmdroid offline, integración de marcadores y pruebas desde instalación limpia sin red; auditar antes de merge.
- **S00-T07 pendiente:** regresión final, validación APK/firma y datos, revisión documental H-01, presentación y entrega.
- Una auditoría independiente por incremento; Orquestador formaliza cierres/merges. No reabrir T01–T06C salvo defecto concreto.

## Reglas de actualización

El Orquestador debe actualizar este archivo cuando:
- cambia el sprint activo;
- se valida o invalida un build;
- aparece o se resuelve un bloqueador;
- cambia la próxima acción autorizada;
- una decisión pendiente pasa a aceptada mediante documentación correspondiente.

No registrar aquí detalles extensos de implementación; usar handoffs, auditorías, ADRs y documentos del sprint.