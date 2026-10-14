# LocalCash — Project State

**Estado del documento:** operativo  
**Responsable principal:** Agente Orquestador  
**Regla:** actualizar cuando cambie el estado real del proyecto. No usar este archivo para reemplazar requisitos, ADRs ni historial de auditorías.

## Estado actual

- **Repositorio:** `aik1a/LocalCash`, `main`. **Entrega académica:** 19 de octubre de 2026.
- **Sprint:** S00 acelerado, **IN_PROGRESS**.
- **Tareas cerradas:** S00-T01, T02, T03, T04, T05 y **T06A (ubicación opcional)**.
- **Funcionalidad integrada:** cuentas/movimientos/saldos, contactos/compras, reparto/deudas y ubicación opcional con consentimiento y permisos Android. Persistencia SQLite **v4**.
- **Último merge:** [PR #6](https://github.com/aik1a/LocalCash/pull/6), `b0f0d06f1d8837e85e80b75aa62e806a090f420b`.
- **Gate T06A:** `docs/audits/S00/S00-T06A-audit.md`, `PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE`, H-01 documental no bloqueante diferida a T07.
- **CI T06A:** [run 38498296734](https://github.com/aik1a/LocalCash/actions/runs/38498296734), SHA funcional `ebc985dedfd61797bcf104b73b24ca9c6c926a1b`: build exit 0, 5.483 checks dominio, regresión y migraciones SQLite v1/v2/v3→v4, emulador con permisos/GPS/ausencia/reinicio PASS.
- **Próximo incremento READY:** S00-T06C, acceso biométrico. T06B mapas conserva pendiente de tiles offline.

## Próxima acción autorizada

**Codificador — S00-T06C: acceso biométrico.** Brief `docs/tasks/active/S00-T06C-biometria.md`, estado **READY**; rama `task/S00-T06C-biometria` desde main posterior a T06A. Proteger contenido financiero antes de desbloqueo mediante `BiometricPrompt`, manejar cancelaciones, falta de biometría, reentrada y evitar bypass. Usar compatibilidad AndroidX Biometric 1.1.0 aprobada, minSdk24. Pruebas en emulador del SHA exacto y regresión T03–T06A; handoff único y auditoría independiente. Codificador no hace merge, cierre ni modifica este archivo.

**T06B:** `docs/tasks/active/S00-T06B-mapas.md`, DRAFT hasta escoger fuente y cobertura de tiles legal/reproducible que funcionen desde instalación limpia con red desactivada. No convertirlo en READY por conveniencia. **S00 no está cerrado**; S00-T07 pendiente.

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

- **T06C autorizada:** `BiometricPrompt` vía AndroidX Biometric 1.1.0 y Groovy/Java como dependencia puntual necesaria para minSdk24; no modificar minSdk ni crear autenticación propia, PIN o bypass. Protección del contenido en todas las pantallas, reentrada y compatibilidad de pruebas.
- **T06B aún bloqueada para implementación:** origen, formato, licencia, atribución, cobertura, empaquetado y prueba real de tiles offline reproducibles. No aceptar `MapView` que dependa de Internet ni descarga no autorizada de OSM.
- **T07:** firma/apk actualizable preservando datos, pruebas integrales/instalación en dispositivo, consolidar discrepancias históricas de DOMAIN_MODEL y DATABASE (T05/T06A H-01), documentación y exposición.
- **No expandir sin decisión:** pagos parciales, compra→movimiento automático, tipos de cuenta o campos extra de contacto.

## Bloqueadores y riesgos

- **Sin bloqueo para comenzar T06C.** Mapas T06B sí requiere una decisión cartográfica válida antes de codificar.
- Compatibilidad de firma entre APK T03/T04/T05/T06A sin prueba de instalación incremental; no desinstalar apps con datos de usuario. Pruebas SQLite aditivas T06A sí pasaron en emulador.
- Ubicación aproximada solamente y GPS físico: no probados en dispositivo, no falsificar evidencia. El GPS simulado/permisos denegados y flujo sin fix sí pasaron.
- Emulador CI no equivale a prueba Android Studio/dispositivo físico; firma de entrega y prueba física en T07. Advertencias Gradle heredadas no bloquean T06C sin evidencia de impacto.

## Última auditoría

`docs/audits/S00/S00-T06A-audit.md` — **PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE**, 97,5/100, [PR #6](https://github.com/aik1a/LocalCash/pull/6) fusionado. H-01 documental no bloqueante para T07. S00 sigue abierto.

## Seguimiento pendiente

- S00-T06C (**READY**): acceso `BiometricPrompt`, seguridad frente a bypass, AndroidX minSdk24, build y pruebas en emulador.
- S00-T06B (**DRAFT**): definir y documentar tiles para osmdroid 6.1.18 offline reproducibles, luego implementar mapa.
- S00-T07: regresión integral y firma, prueba física si disponible, documentación/entrega y cierre documental T05/T06A.
- Auditoría independiente antes de cada merge funcional; no cerrar sprint hasta gate integral aprobado.

## Reglas de actualización

El Orquestador debe actualizar este archivo cuando:
- cambia el sprint activo;
- se valida o invalida un build;
- aparece o se resuelve un bloqueador;
- cambia la próxima acción autorizada;
- una decisión pendiente pasa a aceptada mediante documentación correspondiente.

No registrar aquí detalles extensos de implementación; usar handoffs, auditorías, ADRs y documentos del sprint.