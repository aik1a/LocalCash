# Handoff — S00-T07C

**Rol saliente:** Codificador. **Fecha:** 2026-10-18, America/Santiago.  
**Rama:** task/S00-T07C-documentacion-entrega. **Base:** 9084b573ebe055485e3cdda11ca8db2d8df6a3bc, main posterior a PR #10.  
El SHA final se entrega fuera del documento para evitar autorreferencia. S00 permanece abierto.

## Objetivo y estado

Documentación técnica/trazabilidad y material académico preparados, sujetos a auditoría independiente T07C. No se ejecuta nueva regresión Android, no se cambia producción y no se declara cierre de tarea/sprint.

## Cambios y H-01

- ARCHITECTURE.md raíz actualizado: Java/XML, componentes reales, ocho rutas financieras protegidas, gate AndroidX, SQLite v4, osmdroid y atlas exclusivamente local; distingue cifrado no implementado y operaciones de dominio inexistentes.
- docs/design/ARCHITECTURE.md añadido como acceso al documento canónico raíz. La ruta solicitada no existía; no se crean arquitecturas duplicadas.
- BUILD vigente con SDK/JDK/Gradle/dependencias reales, compilación app/tests, APK exacto, instalación nueva y bloqueo de upgrade. Versiones previas completas conservadas como antecedentes explícitos, también en arquitectura y pauta.
- TRACEABILITY revisada frente a PDF, código y pruebas; corrige pendientes actuales falsos y añade LC-TECH-04; no altera requisitos ni reescribe resultados históricos. Único ajuste de enlace histórico: brief T06B archivado.
- PRESENTATION con guion por módulos y datos esperados. No presenta el guion como recorrido manual ejecutado.
- S00-final-delivery como índice, SQLite resumido, referencias CI/APK, checklist del PDF p.6 y pendientes.
- S00-package-manifest.json fija hashes, fuente/CI y límites; evidencia documental bajo docs/handoffs/evidence/S00-T07C/.

## Archivos modificados

ARCHITECTURE.md; docs/design/ARCHITECTURE.md; docs/delivery/BUILD.md; docs/delivery/PRESENTATION.md; docs/requirements/TRACEABILITY.md; docs/delivery/S00-final-delivery.md; docs/delivery/S00-package-manifest.json; este handoff y evidencia documental T07C.

## Contraste realizado

PDF docente original de 7 páginas, SHA256 eea19ac075c5105b9a6c24f2c6d925ecb273c6092a2f97c8c01e58f800f7c199; texto y páginas 5–6 renderizadas/inspeccionadas. El PDF pide repositorio, documentación técnica y pauta; cuatro pilares Java/SQLite offline/biometría/mapa.

Se revisaron Gradle, manifest, helper v4, Activity/Service/Repository/Domain pertinentes, seguridad, captura y mapa. No se infieren notificaciones, cargos automáticos, marcado de deuda pagada ni reparto por ítem. El código app, Gradle y tests de la base coincide por árboles/blobs Git con el SHA compilado T07A.

Se consultaron en vivo runs T06B 39042930625, T07A 39046939759 y T07B 39082526083: success, SHA exactos. Artefactos T07A no expirados. Hash local del APK seleccionado y atlas dentro del APK coinciden con la evidencia original. No se usa un nuevo commit documental como SHA de compilación.

## Validación y paquete

Rutas/enlaces internos, manifest de entrega, ocho Activities, configuración, hashes, identidad de fuentes, versiones históricas preservadas y correspondencia CI se comprueban con el validador/resultados versionados en evidencia T07C. Resultado local: **PASS, retorno 0**; 7 documentos Markdown, 55 enlaces internos sin roturas, 23 IDs, ocho rutas, tres antecedentes íntegros, hashes y referencias CI concordantes. La comprobación de enlaces internos no significa autenticación de todos los enlaces HTTP externos.

El ZIP académico complementario se entrega con nombre/hash en el mensaje final. Incluye APK exacto, guías, PDF fuente y evidencia seleccionada; no sustituye el código completo del repositorio privado ni acredita recepción docente. No contiene claves privadas. Las guías y sus referencias canónicas quedan en GitHub.

Build nuevo T07C: N/A, cambio exclusivamente documental. Pruebas Android nuevas T07C: NO EJECUTADAS; no necesarias para describir el código intacto y no presentadas como PASS. La evidencia funcional proviene de T07A/T06B, la de firmas de T07B.

## Limitaciones conservadas

- Diez certificados principales históricos diferentes; ningún par inventariado compatible.
- Actualización in-place BLOCKED_SIGNER_MISMATCH; conservación SQLite después de actualizar NOT TESTED. No se borran instalaciones con datos ni se crea migración/firma nueva.
- Regresión financiera/GPS completa API24 NOT TESTED.
- Dispositivo físico y sensor físico NOT TESTED.
- GPS exclusivamente aproximado NOT TESTED.
- Sync Android Studio limpio/caché vacía NOT TESTED.
- Exposición/recorrido manual humano NOT TESTED.
- Mapa limitado a Santiago Centro z14–16; FLAG_SECURE permanece.
- Repositorio privado y artefactos con caducidad requieren acceso autorizado/preservación de binarios.
- MINOR de filtros push Gradle raíz del workflow T07A documentado, sin corregir infraestructura fuera de este brief.

## Pendientes y próxima acción

El Orquestador solicita una única auditoría independiente de T07C sobre el SHA final, documentación, validación y paquete. T07D evalúa el gate integral y límites; el Codificador no los autoriza. No se modifica PROJECT_STATE.md, requisitos, ADRs, dependencias ni código productivo; no se hace merge ni cierre T07C/S00.
