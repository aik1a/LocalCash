# Task Brief — S00-T02: estructura mínima de paquetes

**Sprint:** S00 — Foundation
**Rol asignado:** Codificador (Codex)
**Estado:** CLOSED — 2026-10-12 (PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE)
**Rama de trabajo:** `task/S00-T02-estructura-paquetes`, desde main después del cierre administrativo de S00-T01
**Base funcional aceptada:** merge S00-T01 `3cb3d5a164bc99e5343f283efa44c4a9211426b3`; incluir también la documentación de cierre vigente en main
**Repositorio:** `aik1a/LocalCash`

## Objetivo observable

Organizar el único componente Java existente en un paquete de presentación coherente con ARCHITECTURE.md y documentar la estructura mínima, manteniendo el comportamiento de la pantalla inicial. No implementar capas futuras.

## Requisitos relacionados

- LC-P01: Android nativo Java.
- LC-TECH-05: documentación técnica de la arquitectura.
- Tarea S00-T02 del sprint Foundation; BUILD-01 para comprobar que la reorganización no rompe el proyecto.

## Context Pack obligatorio

1. `PROJECT_ROUTER.md`.
2. `AGENTS.md`.
3. `PROJECT_STATE.md`.
4. `docs/agent/roles/CODER.md`.
5. Este Task Brief.
6. `docs/sprints/S00-foundation.md`.
7. `ARCHITECTURE.md`.
8. `docs/delivery/BUILD.md`.
9. Código real: MainActivity, AndroidManifest.xml, app/build.gradle y XML inicial.

Consultar `docs/agent/HANDOFF_TEMPLATE.md` para entregar resultados. No cargar documentación de sprints futuros.

## Contexto real de partida

S00-T01 está integrada y CLOSED tras gate favorable; las pruebas Android pendientes se conservan para S00-T06. Actualmente existe `com.aik1a.localcash.MainActivity`, registrada como `.MainActivity`, con un layout que muestra «LocalCash». No existen entidades, servicios, repositorios ni persistencia.

## Alcance autorizado

1. Inspeccionar el checkout real de main; registrar SHA y crear la rama de tarea desde él.
2. Ubicar MainActivity en `com.aik1a.localcash.presentation`, usando el paquete y ruta Java correspondientes. Esta ubicación corresponde a Presentation en la arquitectura vigente.
3. Ajustar el import de R y la referencia del manifiesto a la nueva ubicación, sin cambiar el comportamiento.
4. Crear `docs/design/PACKAGE_STRUCTURE.md` con la ubicación real de la Activity y una explicación breve de la separación aprobada. Distinguir las capas futuras, todavía no implementadas; crear sus paquetes/clases solo cuando una tarea real los necesite.
5. Si el estado real difiere de este punto de partida, describir la discrepancia en el handoff y escalar cualquier decisión de alcance.

## Fuera de alcance

- SQLite, dominio, repositories/services, biometría, mapas, permisos y dependencias.
- Pantallas, navegación, lógica de negocio o cambios visuales.
- Clases, interfaces, subpaquetes o directorios vacíos para preparar capas futuras.
- Cambiar namespace/applicationId `com.aik1a.localcash`, versiones, SDK, Java, DSL, configuración Kotlin o arquitectura.
- Cambiar requisitos, ADR, PROJECT_STATE.md o cerrar la tarea/sprint.
- Resolver los pendientes de entorno S00-T06 mediante cambios estructurales.
- Merge a main o implementación de S00-T03.

## Archivos/componentes esperados

- Mover `app/src/main/java/com/aik1a/localcash/MainActivity.java` a `app/src/main/java/com/aik1a/localcash/presentation/MainActivity.java`.
- `app/src/main/AndroidManifest.xml`: referencia correcta a la Activity.
- `docs/design/PACKAGE_STRUCTURE.md`.
- Handoff `docs/handoffs/S00-T02-coder.md` y evidencia pertinente bajo `docs/handoffs/evidence/S00-T02/`.

No modificar Gradle ni XML de la pantalla salvo una discrepancia documentada que requiera autorización.

## Criterios de aceptación

- [ ] MainActivity declara `package com.aik1a.localcash.presentation;` y usa la clase R correcta.
- [ ] La ruta del archivo coincide con el paquete; no queda una Activity duplicada en la raíz.
- [ ] El manifiesto registra la nueva clase como launcher y conserva exported e intent-filter.
- [ ] El layout, texto «LocalCash», namespace/applicationId y configuración funcional siguen iguales.
- [ ] La estructura está documentada según las capas aprobadas, sin clases o paquetes vacíos.
- [ ] No se introducen dependencias, Kotlin, módulos de negocio ni alcance posterior.
- [ ] Build y verificaciones aplicables ejecutados o limitación concreta reproducible registrada.
- [ ] Rama, SHA, diff, evidencia y handoff entregados para auditoría independiente.

## Validación requerida

- **Build:** ejecutar `gradlew.bat :app:assembleDebug` o `./gradlew :app:assembleDebug`; registrar comando, salida y código de retorno.
- **Tests:** ejecutar las pruebas existentes si aplica; NO-SOURCE significa ningún caso ejecutado. No se exigen tests nuevos para el cambio de paquete.
- **Revisión de regresión:** paquete/ruta/import R, una sola MainActivity, referencia del manifiesto y ausencia de cambios ajenos.
- **Manual:** si hay dispositivo/emulador funcional, comprobar el lanzamiento de la nueva clase y que muestra «LocalCash»; de lo contrario, conservar NO EJECUTADO con motivo. Esto no cancela las validaciones integrales pendientes de S00-T06.
- **Limitación:** si el entorno impide build/adb, entregar PARTIAL/BLOCKED con error exacto, sin inventar PASS ni modificar versiones/configuración para resolver el entorno.
- **Texto:** guardar documentación en UTF-8 y comprobar que los blobs versionados no contienen caracteres de reemplazo.

## Entrega esperada

- Commits publicados solo en `task/S00-T02-estructura-paquetes`.
- Estado DONE/PARTIAL/BLOCKED, SHA final y archivos modificados.
- Implementación y requisitos cubiertos; build/tests/manual con resultados y evidencia.
- Handoff usando HANDOFF_TEMPLATE.md; pendientes de cierre/trazabilidad para el Orquestador.
- Solicitud de auditoría independiente; no autorizar el propio cambio ni hacer merge.

La preparación de este Task Brief no implementa S00-T02. El Codificador está autorizado únicamente a esta tarea; aceptación del Auditor y cierre del Orquestador.

## Cierre administrativo — 2026-10-12

- Commit funcional auditado: `6cea41252f26a76af44f81f990a99f0b95cf74b1`.
- Validación: [GitHub Actions run 38267899607](https://github.com/aik1a/LocalCash/actions/runs/38267899607); `assembleDebug` exit 0, 33 tareas, APK generado.
- Informe: `docs/audits/S00/S00-T02-reaudit-H01.md`, PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE.
- Merge: PR #2, `d39a35b99fe96349f06052dff01163957ed54c4f`.
- H-01 resuelto; H-02 (abrir la aplicación en Android) permanece NO EJECUTADO para S00-T06.
- Archivo histórico: criterios de aceptación originales conservados; su checklist previa no significa pruebas manuales ejecutadas.
- Cierre administrativo por Orquestador; sprint S00 sigue IN_PROGRESS.
