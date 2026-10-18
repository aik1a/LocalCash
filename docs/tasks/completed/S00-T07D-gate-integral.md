# Task Brief — S00-T07D: auditoría integral y gate de sprint

**Sprint:** S00 · **Rol:** Auditor independiente; cierre posterior exclusivo del Orquestador · **Estado:** CLOSED (PASS WITH OBSERVATIONS / APPROVED_FOR_SPRINT_ADVANCE; PR #12) · **Rama autorizada:** `audit/S00-T07D-integral`, desde main posterior a PR #11 y cierre administrativo T07C.

## Objetivo
Evaluar de forma independiente si LocalCash cumple el encargo docente y si S00 puede cerrarse y entregarse.

## Requisitos relacionados
Todos los LC-P, LC-TECH y requisitos funcionales de docs/requirements/PROJECT_SPEC.md.

## Context Pack
PROJECT_ROUTER.md, AGENTS.md, PROJECT_STATE.md, docs/agent/roles/AUDITOR.md, docs/agent/WORKFLOW.md, docs/agent/TASK_LIFECYCLE.md, docs/requirements/PROJECT_SPEC.md, docs/requirements/TRACEABILITY.md, docs/sprints/S00-foundation.md, auditorías T01–T07C relevantes, código main, CI y artefactos T07A/B/C, PDF docente.

## Alcance
1. Auditar código integrado, build y regresión contra SHA exacto, requisitos y alcance académico.
2. Contrastar biometría, persistencia SQLite, módulos financieros, mapas offline y entrega.
3. Evaluar instalación limpia, firma/upgrade y conservación de datos distinguiendo PASS/FAIL/BLOCKED/NOT TESTED; no confundir limitación documentada con prueba satisfactoria.
4. Verificar documentación H-01, trazabilidad, APK/hash, demo, enlaces y límites de hardware físico.
5. Emitir defectos con severidad y gates `APPROVED_FOR_MERGE` cuando aplique y `APPROVED_FOR_SPRINT_ADVANCE` o `NOT_APPROVED_FOR_SPRINT_ADVANCE`.

## Fuera de alcance
Implementar correctivos, reescribir código funcional, aprobar el propio trabajo, cerrar S00 o hacer merge.

## Criterios de aceptación
- [ ] Informe independiente con SHA, método, evidencias, límites y hallazgos.
- [ ] Veredicto explícito PASS / PASS WITH OBSERVATIONS / FAIL / BLOCKED.
- [ ] Gate explícito de avance/cierre S00; riesgos residuales evaluados.
- [ ] Informe `docs/audits/S00/S00-T07D-integral-audit.md` y handoff al Orquestador.

## Validación
Revisar CI, hashes y pruebas reproducibles disponibles; ejecutar comprobaciones independientes si el entorno permite. No inventar ejecución local.

## Entrega
Informe documental versionado y veredicto. Orquestador decide integración documental y cierre administrativo solo tras gate favorable.


## Cierre administrativo — 2026-10-18

- Auditoría: `docs/audits/S00/S00-T07D-integral-audit.md`, SHA publicación `e1c83d9d94f7b613194d2e41006f0fffeeffbb4a`.
- Gate: PASS WITH OBSERVATIONS / APPROVED_FOR_SPRINT_ADVANCE, 0 BLOCKER, 0 MAJOR, 3 MINOR; suficiencia académica, no cobertura exhaustiva.
- PR #12 merge `53c1517ec6a7eb64a7ab646df5565145aa455fd3`.
- Orquestador: S00 CLOSED académicamente. Recepción docente NOT VERIFIED. Upgrade histórico BLOCKED_SIGNER_MISMATCH; conservación tras upgrade NOT TESTED.
