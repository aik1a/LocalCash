# Task Brief — S00-T07A: build y regresión integral

**Sprint:** S00 · **Rol:** Codificador · **Estado:** READY · **Rama autorizada:** `task/S00-T07A-integracion` (desde main actual).

## Objetivo
Producir evidencia reproducible de compilación e instalación limpia del estado integrado de LocalCash, con regresión de los módulos del encargo docente y APK identificable.

## Requisitos relacionados
LC-P01/02/03/04, LC-AUTH-01, LC-CON-01, LC-ACC-01, LC-MOV-01/02, LC-MAP-01, LC-BUY-01/02/03, LC-SHARED-01/02, LC-DEBT-01/02, LC-TECH-01/02/03/04.

## Context Pack
PROJECT_ROUTER.md, AGENTS.md, PROJECT_STATE.md, docs/agent/roles/CODER.md, docs/sprints/S00-foundation.md, docs/requirements/PROJECT_SPEC.md, docs/requirements/TRACEABILITY.md, docs/audits/S00/S00-T06B-audit.md, docs/testing/S00-T06B.md y workflow CI actual. Consultar código real y pruebas existentes.

## Alcance
1. Identificar SHA base y configuración Gradle/Android. Ejecutar `:app:assembleDebug :app:assembleDebugAndroidTest` desde checkout del SHA exacto en entorno disponible o GitHub Actions, guardando logs, exit code, hashes y APK.
2. Ejecutar regresión integral en emulador limpio (API24 y API36 cuando sea posible): biometría y accesos protegidos, cuentas/saldos/movimientos, contactos, compras/programadas, reparto/deudas, GPS opcional, SQLite v4 y migraciones, mapa OSM offline desde primera apertura sin red, marcadores, reinicio y persistencia.
3. Diferenciar tests automatizados de recorridos UI manuales; registrar PASS/FAIL/NOT TESTED por escenario y plataforma. No inventar evidencia.
4. Corregir solo defectos funcionales demostrados y acotados necesarios para el gate, con pruebas de regresión; escalar cambios de arquitectura/requisitos.
5. Entregar `docs/testing/S00-T07A.md`, evidencias bajo `docs/handoffs/evidence/S00-T07A/`, handoff `docs/handoffs/S00-T07A-coder.md`, SHA, APK/hash y límites.

## Fuera de alcance
Re-firmar APK históricos, prometer upgrade con firmas distintas (T07B), rehacer documentación general (T07C), añadir funciones, modificar ADR, merge, cerrar tarea/sprint o autoauditar.

## Criterios de aceptación
- [ ] Build exacto con exit 0 y APK/hash identificados, o bloqueo preciso con logs.
- [ ] Pruebas automatizadas existentes y regresión integral documentadas con resultados reales.
- [ ] Instalación limpia, persistencia y cartografía offline/biometría verificadas o marcadas NOT TESTED con causa.
- [ ] Sin regresiones BLOCKER/MAJOR conocidas; limitaciones explícitas.
- [ ] Handoff y evidencias versionados, diff revisado.

## Validación
Build Gradle y pruebas Java/Android; AVD API24/API36 cuando el entorno lo permita; escenarios de instalación limpia y relanzamiento; no usar datos reales.

## Entrega
Commit(s) en rama de tarea, informe de pruebas, artefactos verificables y handoff para auditoría independiente. Codificador no fusiona ni cambia PROJECT_STATE.md.
