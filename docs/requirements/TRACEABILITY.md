# Matriz de trazabilidad

Esta matriz conecta requisitos con diseño, sprint, código y validación. Debe actualizarse en la misma iteración que la implementación.

| Requisito | Diseño principal | Sprint | Código | Prueba | Estado |
|---|---|---:|---|---|---|
| LC-P01 | ARCHITECTURE | S00 / T01 | `app/build.gradle`, `MainActivity.java`, XML y manifiesto | BUILD-01; logs S00-T01 y reauditoría favorable | Proyecto base integrado; S00-T01 CLOSED |
| LC-P02 / LC-TECH-01 | DATABASE / ADR-001 | S01 | Pendiente | DB-01 | Pendiente |
| LC-P03 / LC-AUTH-01 | ARCHITECTURE | S02 | Pendiente | AUTH-01 | Pendiente |
| LC-CON-01 | DOMAIN_MODEL / DATABASE | S05 | Pendiente | CON-01 | Pendiente |
| LC-ACC-01 | DOMAIN_MODEL / DATABASE | S03 | Pendiente | ACC-01 | Pendiente |
| LC-MOV-01 | DOMAIN_MODEL / DATABASE | S03 | Pendiente | MOV-01 | Pendiente |
| LC-MOV-02 | DOMAIN_MODEL / DATABASE | S04 | Pendiente | GEO-01 | Pendiente |
| LC-P04 / LC-MAP-01 | ARCHITECTURE | S04 | Pendiente | MAP-01 | Pendiente |
| LC-BUY-01 | DOMAIN_MODEL / DATABASE | S05 | Pendiente | BUY-01 | Pendiente |
| LC-BUY-02 | BUSINESS_RULES / DATABASE | S05 | Pendiente | BUY-02 | Pendiente |
| LC-BUY-03 | BUSINESS_RULES / DATABASE | S05 | Pendiente | BUY-03 | Pendiente |
| LC-SHARED-01 | DOMAIN_MODEL / DATABASE | S06 | Pendiente | SHARE-01 | Pendiente |
| LC-SHARED-02 | DOMAIN_MODEL / DATABASE | S06 | Pendiente | SHARE-02 | Pendiente |
| LC-DEBT-01 | BUSINESS_RULES | S06 | Pendiente | DEBT-01 | Pendiente |
| LC-DEBT-02 | BUSINESS_RULES | S06 | Pendiente | DEBT-02 | Pendiente |
| LC-TECH-05 | docs/ | S07 | Pendiente | DOC-01 | En progreso |
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

S00-T02 queda READY para definir los paquetes mínimos; no implementada. El sprint S00 continúa abierto.
