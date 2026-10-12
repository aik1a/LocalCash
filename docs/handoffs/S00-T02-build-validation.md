# Handoff — S00-T02: validación técnica de H-01

**Rol saliente:** ejecución técnica / Codificador
**Fecha:** 2026-10-12
**Rama:** `task/S00-T02-estructura-paquetes`
**SHA exacto compilado:** `6cea41252f26a76af44f81f990a99f0b95cf74b1`
**Sprint:** S00 — Foundation

## Objetivo y estado

**DONE para la ejecución técnica solicitada.** Se obtuvo una compilación Android real del SHA auditado. S00-T02 no se cierra; el veredicto de auditoría y la autorización de merge siguen pendientes.

## Validación ejecutada

- Ejecución: https://github.com/aik1a/LocalCash/actions/runs/38267899607
- Job: https://github.com/aik1a/LocalCash/actions/runs/38267899607/job/114874619655
- Comando: `bash ./gradlew --no-daemon --console=plain :app:assembleDebug`.
- Resultado: `BUILD SUCCESSFUL in 1m 8s`; 33 actionable tasks: 33 executed.
- Código de retorno Gradle: **0**, registrado desde PIPESTATUS[0].
- Checkout verificado mediante `git rev-parse HEAD` y comparación con el SHA completo.
- Runner: Ubuntu 24.04.5; Temurin 25.0.4.1+1; plataforma android-36.1; Build Tools 36.0.0.
- Gradle 9.5.0 y AGP 9.3.2 se usaron desde el proyecto, sin modificaciones.
- APK SHA-256: `9b7c8ae0e42d521f1a1c11a7381bd7e9c989f603592df48fa1058e3362b27a1c`.
- Tests: no ejecutados en esta validación acotada; no existen casos según la auditoría.
- Manual / H-02: NO EJECUTADO; este workflow no instala ni abre la aplicación.

El SHA de evento del workflow es `e05bc50ce9f6d153d5a96685112a339fcc6a40cf`; corresponde a la configuración CI. **El código compilado es 6cea412...**, fijado explícitamente en checkout y comprobado en el log.

## Evidencia

- Salida completa de la invocación de Gradle, JDK, SHA y retorno: `docs/handoffs/evidence/S00-T02/actions-assembleDebug.log`.
- Log completo del job accesible en la ejecución enlazada.
- Artefacto (log original, exit-code.txt, validated-sha.txt, java-version.txt, apk-sha256.txt y APK): https://github.com/aik1a/LocalCash/actions/runs/38267899607/artifacts/11755861315
- Artefacto SHA-256: `3cdd38e3f1d3eadfb6b6d1f0db880e773cdff11a5061fb32a180000bb72e4f5c`.
- Caducidad del artefacto: 2027-01-10T15:07:18Z. La salida textual permanece versionada en el repositorio.

## Preparación y cambios realizados

Work dispone de JDK 17, sin SDK Android disponible en las rutas comprobadas; Git directo no pudo autenticar el repositorio privado. Se utilizó el conector GitHub y la alternativa Actions autorizada. No se volvió a intentar el sandbox Windows.

Se añadió únicamente `.github/workflows/s00-t02-build.yml` y esta evidencia documental. El workflow usa permisos contents:read, checkout fijo y conserva artefactos. No cambia código funcional, dependencias, versiones del proyecto ni arquitectura.

Primera ejecución: https://github.com/aik1a/LocalCash/actions/runs/38267734648
Falló antes de Gradle porque setup-android intentaba instalar el paquete retirado `tools`: `Warning: Failed to find package 'tools'`, retorno sdkmanager 1. Se ajustó exclusivamente el parámetro del workflow a `packages: platform-tools`; la segunda ejecución terminó correctamente.

Commits de CI: `3663f78bac42beb8e70641e3e704d2e345bc4d72`, `e05bc50ce9f6d153d5a96685112a339fcc6a40cf`.
Commit de log: `424949d6f0b87eb61ddf3e6e87eb3660700c1f15`.

Se conserva la advertencia de deprecación `android.builtInKotlin=false` y las advertencias Gradle; no se alteró configuración para ocultarlas.

## Próxima acción: solicitud de reauditoría

Solicito al Auditor **reauditoría acotada de H-01 / AC-07 / gate de build** de `docs/audits/S00/S00-T02-audit.md`, usando el SHA exacto compilado, ejecución y log indicados. Revisar además que los commits posteriores solo añaden CI y evidencia. No se modifica el informe independiente ni se autoriza el propio merge.

El Orquestador conserva la decisión de aceptación, cierre y avance. Sin merge, sin cierre de S00-T02 y sin trabajo de S00-T03. H-02 permanece pendiente.
