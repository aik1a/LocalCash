# Handoff — FB-F0-T02

**Rol saliente:** Orquestador
**Fecha:** 2026-11-16
**Rama/commit:** task/FB-F0-T02-baseline-inventario; base d7657d1cd798e3f86f9ac9fbe75a80af1fd28c4b; commits identificables en el PR de esta rama
**Sprint:** FB-01 / F0 abierta

## Objetivo ejecutado

Inventario del comportamiento integrado SQLite v7 y garantías a conservar. [Baseline](../design/FIREBASE_BASELINE.md) contiene nueve tablas, seis repositorios, tres servicios, pantallas, IDs, transacciones/triggers, matriz operación/regla/garantía/riesgo/prueba y CI efectivo.

## Estado

DONE en elaboración, control documental y publicación; integración pendiente en PR #20. Brief permanece activo hasta cierre administrativo. No aprobación técnica Firebase ni cierre F0.

## Cambios realizados y archivos modificados

- docs/design/FIREBASE_BASELINE.md: inventario y matriz, evidencia recuperada y decisiones pendientes.
- PROJECT_ROUTER.md, PROJECT_STATE.md, ARCHITECTURE.md, docs/design/ARCHITECTURE.md, docs/design/DATABASE.md y docs/requirements/TRACEABILITY.md: conciliación operativa con antecedentes preservados.
- docs/exec-plans/active/FB-01-firebase-google-offline.md y docs/tasks/active/FB-F0-T02-baseline-inventario.md: progreso y criterios documentales.
- Este handoff. No cambios en código, tests, workflows, dependencias, reglas o sources/.

## Validación ejecutada

| Verificación | Comando/escenario | Resultado |
|---|---|---|
| Base remota | git ls-remote de main/integración/tarea/etiqueta; clon limpio, switch -c | PASS; refs exactas en baseline; sin tocar cambios ajenos |
| Comportamiento y criterios | Lectura selectiva helper, seis repositorios, tres servicios, dominio, UI/manifest, pruebas y nueve YAML; contraste cuatro criterios del brief | PASS documental; no equivale a ejecutar pruebas |
| Evidencia remota | GET pulls/16, Actions runs por SHA y jobs de 41705745479; log job API36 126160459675 | Merge confirmado; run histórico failure, API24 success/API36 failure por REALIZADA; cero runs consultados para c28a27b/d7657d1 |
| Diff y alcance | git diff --check; revisión git diff y lista de archivos contra d7657d1 | PASS documental |
| Enlaces | Resolución de destinos Markdown locales de archivos modificados, sin fragmentos; rutas de clases/pruebas contrastadas con árbol | PASS: 74 enlaces locales en 10 archivos; no se certifica cada URL externa o fragmento |
| Build / tests / manual | No requeridos por cambio exclusivamente documental | NOT TESTED ahora; tampoco auditoría independiente |

## Documentación actualizada

Estado operativo y arquitectura describen integración #16, v7/nueve tablas y versión declarada. Entradas anteriores se identifican como históricas. No se alteró el resultado de auditorías ni el contenido de handoffs históricos. Resultados locales anteriores se atribuyen a su fuente, sin presentarlos como repetidos.

## Decisiones tomadas

Ninguna regla funcional nueva. Se preserva alcance ADR-003 y criterio MVP. No se amplía alcance para corregir tests: se registra necesidad de tarea separada, a cargo del Orquestador.

## Hallazgos o riesgos

- CI final #16 no fue integralmente verde; discrepancia de etiquetas en relaunch-checks.py frente a ComprasActivity. Necesita corrección acotada y reejecución, sin afirmar que sea el único fallo.
- Workflows no invocan 29 JUnit ni modes payment/crud/crud-upgrade/feedback; push no cubre ramas FB y filtros omiten cambios Gradle raíz. Planificar cobertura antes de primera implementación funcional.
- Firestore debe resolver pago único, reparto atómico, borrados concurrentes, precisión/moneda fija e historial completo; SQL no concede esas garantías al backend nuevo.
- Identidad long local permea dominio/UI y orden de reparto; importación necesita origen estable. Firma compatible/datos existentes aún sin resolver.

## Pendientes

FB-F0-T03: instalaciones/datos, respaldo, firmante compatible/distribución y responsable/proyecto Firebase. Decisiones F1 enumeradas en baseline, sin configurar servicios ahora. Migración directa v5→v7, aceptación manual/GPS físico y auditoría independiente siguen pendientes según fuentes; este documento no los cierra. No se sincroniza Library porque no se cierra fase/hito.

## Contexto mínimo para el siguiente agente

Leer PROJECT_ROUTER.md, PROJECT_STATE.md, rol Orquestador, FIREBASE_PROTOCOL.md, ADR-003, FB-01 y FIREBASE_BASELINE.md. Para corrección CI, revisar run 41705745479, tests/relaunch-checks.py, ComprasActivity y s00-t06c-build.yml; emitir brief separado, sin mezclar con inventario de datos reales.

## Próxima acción sugerida

Preparar/ejecutar F0-T03 con metadatos y decisiones explícitas del responsable. No iniciar F1 ni distribuir APK hasta gate aplicable. Publicar este trabajo como PR a feat/firebase-migration; no fusionar.

## Publicación

Rama publicada mediante git push -u origin task/FB-F0-T02-baseline-inventario. [PR #20](https://github.com/aik1a/LocalCash/pull/20) abierto hacia feat/firebase-migration, base d7657d1. Commit de inventario: 573dfb19c2c04c0658b871989981bb588cc76773; un segundo commit documental registra publicación y limpia espacios finales del handoff. Head definitivo verificable en el PR y remoto. No se fusionó ni se cerró F0 o el brief. Control final git diff d7657d1..HEAD --check sin incidencias tras esa limpieza.
