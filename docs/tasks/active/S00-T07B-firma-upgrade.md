# Task Brief — S00-T07B: firma APK y actualización preservando datos

**Sprint:** S00 · **Rol:** Codificador · **Estado:** PLANNED (habilitar tras evidencia base T07A) · **Rama prevista:** `task/S00-T07B-firma-upgrade`, desde main aceptado tras T07A.

## Objetivo
Determinar con evidencia si un APK de LocalCash puede actualizar una instalación anterior preservando datos SQLite y documentar un procedimiento seguro de instalación.

## Requisitos relacionados
LC-P02, LC-TECH-01/04/05, LC-ACC-01, LC-MOV-01 y continuidad de persistencia; riesgos documentados en PROJECT_STATE.md y auditorías T04–T06B.

## Context Pack
PROJECT_ROUTER.md, AGENTS.md, PROJECT_STATE.md, docs/agent/roles/CODER.md, docs/sprints/S00-foundation.md, docs/design/DATABASE.md, BUILD.md, docs/testing/S00-T07A.md, evidencia de APK/firmas de T03–T07A y código/manifest/build.gradle reales.

## Alcance
1. Inventariar APK históricos realmente disponibles, SHA256, packageName, versionCode, certificados de firma (digest) y procedencia. No deducir igualdad de firma por nombre.
2. Comprobar en AVD desechable la actualización `adb install -r` solo entre APK compatibles y conservando datos de prueba; registrar datos antes/después y resultado del instalador.
3. Si los certificados difieren, documentar explícitamente que la actualización in-place está bloqueada; no forzar downgrade ni borrar datos. Definir instalación limpia para usuarios nuevos y limitación para instalaciones previas, sin afirmar migración de datos inexistente.
4. Mantener firma privada/credenciales fuera de GitHub. No inventar una nueva clave de release sin autorización.
5. Crear `docs/testing/S00-T07B.md` y `docs/handoffs/S00-T07B-coder.md` con matriz de compatibilidad y evidencia.

## Fuera de alcance
Extracción de datos privados reales, borrado/desinstalación como prueba de upgrade, claves nuevas no autorizadas, cambios de esquema, mecanismos de exportación/importación no aprobados, merge y cierre administrativo.

## Criterios de aceptación
- [ ] Inventario y fingerprints de certificados contrastables.
- [ ] Resultado de actualización real cuando hay par de APK compatibles, o bloqueo explícito por firma/evidencia insuficiente.
- [ ] Preservación SQLite demostrada solo si se ejecutó actualización compatible.
- [ ] Procedimiento de instalación seguro y limitaciones documentadas.
- [ ] Handoff y evidencias versionadas, sin secretos.

## Validación
`apksigner verify --print-certs` u herramienta equivalente, `adb install -r` en emulador desechable y comprobación de SQLite antes/después si procede.

## Entrega
Commit documental/técnico mínimo, evidencias y handoff para auditoría; sin autoaprobación ni cambios de PROJECT_STATE.md.
