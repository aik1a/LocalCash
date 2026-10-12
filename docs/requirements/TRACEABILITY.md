# Matriz de trazabilidad

Esta matriz conecta requisitos con diseño, sprint, código y validación. Debe actualizarse en la misma iteración que la implementación.

| Requisito | Diseño principal | Sprint | Código | Prueba | Estado |
|---|---|---:|---|---|---|
| LC-P01 | ARCHITECTURE | S00 / T01-T02 | `app/build.gradle`, `app/src/main/java/com/aik1a/localcash/presentation/MainActivity.java`, XML y manifiesto | BUILD-01; Actions run `38267899607` (S00-T02, SHA exacto), auditoría favorable | Proyecto base y estructura presentacional integrados; T01-T02 CLOSED |
| LC-P02 / LC-TECH-01 | DATABASE / ADR-001 | S00-T03 | `LocalCashDbHelper`, repositories de cuentas/movimientos | DB-01 / Actions run `38283139884` | Implementado en flujo T03; otros módulos pendientes |
| LC-P03 / LC-AUTH-01 | ARCHITECTURE | S02 | Pendiente | AUTH-01 | Pendiente |
| LC-CON-01 | DOMAIN_MODEL / DATABASE | S05 | Pendiente | CON-01 | Pendiente |
| LC-ACC-01 | DOMAIN_MODEL / DATABASE | S00-T03 | `Cuenta`, `CuentaRepository`, `MainActivity` | ACC-01, dos cuentas en emulador | Implementado (crear/listar/consultar saldo) |
| LC-MOV-01 | DOMAIN_MODEL / DATABASE | S00-T03 | `Movimiento`, `MovimientoService`, `CuentaActivity` | MOV-01, ingreso/egreso/persistencia en emulador | Implementado (registro/historial) |
| LC-MOV-02 | DOMAIN_MODEL / DATABASE | S04 | Pendiente | GEO-01 | Pendiente |
| LC-P04 / LC-MAP-01 | ARCHITECTURE | S04 | Pendiente | MAP-01 | Pendiente |
| LC-BUY-01 | DOMAIN_MODEL / DATABASE | S05 | Pendiente | BUY-01 | Pendiente |
| LC-BUY-02 | BUSINESS_RULES / DATABASE | S05 | Pendiente | BUY-02 | Pendiente |
| LC-BUY-03 | BUSINESS_RULES / DATABASE | S05 | Pendiente | BUY-03 | Pendiente |
| LC-SHARED-01 | DOMAIN_MODEL / DATABASE | S06 | Pendiente | SHARE-01 | Pendiente |
| LC-SHARED-02 | DOMAIN_MODEL / DATABASE | S06 | Pendiente | SHARE-02 | Pendiente |
| LC-DEBT-01 | BUSINESS_RULES | S06 | Pendiente | DEBT-01 | Pendiente |
| LC-DEBT-02 | BUSINESS_RULES | S06 | Pendiente | DEBT-02 | Pendiente |
| LC-TECH-05 | docs/ | S00/S07 | `docs/design/PACKAGE_STRUCTURE.md`; documentación BUILD | Evidencia/handoffs S00-T01 y S00-T02 | En progreso; estructura inicial documentada |
| LC-TECH-06 | delivery/PRESENTATION | S07 | Pendiente | DEMO-01 | Pendiente |

## Cierre S00-T01 — 2026-10-12

- Commit funcional auditado: `b63e5e2c4de6accb9c952022f12b07a0b614d62d`.
- Gate: PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE en `docs/audits/S00/S00-T01-reaudit.md`.
- Head integrado: `7288b9f6b40bdcb3c77385e9e138acaff96973d7`; respecto del auditado, solo añadió el informe de reauditoría.
- Merge: `3cb3d5a164bc99e5343f283efa44c4a9211426b3`, PR #1. Task Brief archivado en `docs/tasks/completed/S00-T01-proyecto-android-base.md`.
- LC-P01: Java nativo implementado en el proyecto base. No se considera completado el conjunto de funcionalidades de LocalCash.
- BUILD-01: compilación histórica del Codificador, `BUILD SUCCESSFUL in 25s`, salida 0; evidencia `docs/handoffs/evidence/S00-T01/clean-build-tests.txt`. Tests: `NO-SOURCE`; sin ejecución nueva ni certificación de caché vacía.

| Validación pendiente | Estado real | Tarea responsable | Evidencia inicial |
|---|---|---|---|
| Instalación, MainActivity, texto «LocalCash» y errores de inicio (OBS-02) | NO EJECUTADO | S00-T06 | `manual.txt`, `revalidation-blocker.txt` bajo evidencia S00-T01 |
| Sync Android Studio / checkout y caché vacía (OBS-03) | NO EJECUTADO | S00-T06 | `BUILD.md`, `revalidation-blocker.txt` |
| Deprecación Kotlin integrado (OBS-04) | Riesgo aceptado/documentado | Seguimiento en S00-T06 | `BUILD.md`, reauditoría S00-T01 |

S00-T02 fue cerrada el 2026-10-12; S00-T03 quedó READY. El sprint S00 continúa abierto.

## Cierre S00-T02 — 2026-10-12

- Código auditado: `6cea41252f26a76af44f81f990a99f0b95cf74b1`.
- Validación BUILD-01: [Actions run 38267899607](https://github.com/aik1a/LocalCash/actions/runs/38267899607), checkout del SHA exacto; `:app:assembleDebug` finaliza con `BUILD SUCCESSFUL in 1m 8s`, 33 tareas, código 0. APK publicado como artefacto.
- Gate: `docs/audits/S00/S00-T02-reaudit-H01.md`, PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE.
- Merge: [PR #2](https://github.com/aik1a/LocalCash/pull/2), `d39a35b99fe96349f06052dff01163957ed54c4f`.
- Estructura: `com.aik1a.localcash.presentation.MainActivity`, `docs/design/PACKAGE_STRUCTURE.md`.
- **Pendiente:** H-02, ejecución en dispositivo/emulador, incorporada a S00-T06 y no declarada PASS.
- El workflow S00-T02 fija el SHA auditado: no confirma builds de otros commits.

## S00-T03 — Cierre 2026-10-12

- `eea0fbfc895f5d5e2cde6f3512407f3cc9c16cec`: [CI y emulador PASS](https://github.com/aik1a/LocalCash/actions/runs/38283139884), 18 comprobaciones de dominio PASS, reinicio con SQLite persistente.
- Gate `docs/audits/S00/S00-T03-audit.md` PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE.
- [PR #3](https://github.com/aik1a/LocalCash/pull/3), merge `f484642361c20246eb07dfc030a0e41e01b76647`.
- Pendiente: migración SQLite aditiva en T04; dispositivo físico y Android Studio en T07.
