# Handoff - S00-T01

**Rol saliente:** Codificador (Codex)
**Fecha:** 2026-10-09
**Rama/commit:** `task/S00-T01-proyecto-android-base` / implementación `88502e38c24d3b178bae18bc62c2e1f4505c2042`; el commit de documentación posterior contiene este handoff y la evidencia.
**Sprint:** S00 - Foundation

## Objetivo ejecutado

Crear el proyecto Android base Java/XML y Gradle Groovy sobre la documentación existente, sin funciones de negocio.

## Estado

`PARTIAL`: implementación y compilación verificadas; instalación y apertura de la Activity pendientes por limitación del emulador. No se emite veredicto de auditoría ni se autoriza cierre.

## Cambios realizados

- Módulo app; namespace y applicationId `com.aik1a.localcash`.
- MainActivity basada en Activity del framework y layout XML que muestra «LocalCash».
- minSdk 24, targetSdk 36 y compileSdk 36.1, sin permisos ni dependencias de aplicación.
- AGP 9.3.2, Wrapper Gradle 9.5.0 y compatibilidad Java 17; JDK de build OpenJDK 25.0.3.
- Kotlin integrado de AGP desactivado para mantener el proyecto Java.
- .gitignore para archivos locales, builds y cachés; .gitattributes para scripts en Windows/Unix.
- Wrapper JAR generado con Gradle instalado; gradlew registrado con modo 100755.

## Archivos modificados

Agregados:
- `.gitattributes`, `.gitignore`, `settings.gradle`, `build.gradle`, `gradle.properties`.
- `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`, `gradle/wrapper/gradle-wrapper.properties`.
- `app/build.gradle`.
- `app/src/main/AndroidManifest.xml`.
- `app/src/main/java/com/aik1a/localcash/MainActivity.java`.
- `app/src/main/res/layout/activity_main.xml`.
- `app/src/main/res/values/strings.xml`.
- Este handoff y evidencia bajo `docs/handoffs/evidence/S00-T01/`.

Modificado:
- `docs/delivery/BUILD.md`.

## Validación ejecutada

| Verificación | Comando/escenario | Resultado |
|---|---|---|
| Wrapper | Gradle 9.5.0: wrapper --gradle-version 9.5.0 --distribution-type bin | PASS |
| Build | gradlew.bat --offline --no-daemon :app:assembleDebug | PASS; BUILD SUCCESSFUL in 21s, salida 0 |
| Build limpio | gradlew.bat --offline --no-daemon clean :app:assembleDebug :app:testDebugUnitTest | PASS; BUILD SUCCESSFUL in 25s, 37 tareas ejecutadas, salida 0 |
| Tests | :app:testDebugUnitTest | N/A; NO-SOURCE, ningún caso ejecutado |
| APK | aapt dump badging app/build/outputs/apk/debug/app-debug.apk | PASS; package correcto, sdkVersion 24, targetSdkVersion 36, MainActivity lanzable |
| Diff | git diff --cached --check y revisión del diff/estructura | PASS; revisión del Codificador, no auditoría independiente |
| Manual | AVD existente y AVD aislado en workspace | NO EJECUTADO; lock denegado en primer intento, dispositivo offline en segundo |

Evidencia versionada:
- `clean-build-tests.txt`: salida completa del build limpio y tarea de tests.
- `assembleDebug.txt`: build anterior exitoso.
- `apk-badging.txt`: metadatos del APK.
- `manual.txt`: comandos y limitaciones de ejecución.
- `checks.txt`: comprobaciones de estructura, alcance y diff.

## Documentación actualizada

BUILD.md documenta configuración, comandos, resultado, cachés usadas y limitaciones verificadas. PROJECT_STATE.md, Task Brief, trazabilidad, requisitos y arquitectura no fueron modificados. El Orquestador conserva su cierre.

## Decisiones tomadas

Ninguna decisión arquitectónica ni funcional. Configuración técnica dentro del Task Brief: herramientas instaladas, SDK 36.1 y Java 17. La plataforma android-36 estaba incompleta y el primer build la rechazó; se utilizó la plataforma 36.1 realmente instalada sin cambiar minSdk 24 ni targetSdk 36. No se instalaron nuevas dependencias.

## Hallazgos o riesgos

- Kotlin integrado añadió una dependencia de anotaciones no disponible offline; desactivarlo resolvió el build Java. La opción está deprecada para AGP 10.
- Advertencias de métricas por permisos y deprecaciones Gradle; no impidieron el build final.
- No se probó sync de Android Studio ni checkout con caché vacía, ni ejecución en API 24.
- Git por consola quedó sin respuesta. La fuente main se recuperó con el conector; los blobs y el árbol base fueron comprobados contra GitHub. Los commits se publicaron mediante API sobre main `acc1fd9275e414606d895a49a2c834980e1d6575`, preservando su documentación.

## Pendientes

- Instalar y abrir el APK en un dispositivo o emulador operativo; comprobar que muestra «LocalCash».
- Auditoría independiente de S00-T01.
- El Orquestador decide aceptación, trazabilidad, cierre y cualquier avance.

## Contexto mínimo para el siguiente agente

Leer:
- Task Brief `docs/tasks/active/S00-T01-proyecto-android-base.md`.
- `docs/agent/roles/AUDITOR.md` si actúa como Auditor.
- `docs/delivery/BUILD.md` y este handoff.

Revisar:
- Diff de la rama frente a main y evidencia citada.
- Activity, manifiesto, Gradle Groovy, SDK mínimo y Wrapper.

## Próxima acción sugerida

Auditar S00-T01 y completar la prueba de instalación/apertura. No avanzar a S00-T02 ni hacer merge sin autorización del Orquestador.

## Corrección acotada y entrega para reauditoría — 2026-10-10

**Rol:** Codificador (Codex).
**Base auditada:** `f1af509fe45b13df67f1898eae03ed57568e239a`.
**Rama:** `task/S00-T01-proyecto-android-base`.
**Estado vigente:** `PARTIAL`. Correcciones documentales comprobadas sobre los blobs de Git; validación Android y comprobaciones adicionales NO EJECUTADO por bloqueo de procesos locales.

### Correcciones realizadas

- Restaurados 111 caracteres de reemplazo: 48 en BUILD.md, 54 en este handoff y 9 en manual.txt.
- La restauración del texto histórico solo cambia los caracteres dañados; se conservaron comandos, valores, resultados y significado. Los apartados nuevos se distinguen de la evidencia histórica.
- Inspeccionados también `checks.txt`, `assembleDebug.txt`, `clean-build-tests.txt` y `apk-badging.txt`: UTF-8 válido y cero caracteres de reemplazo; no se modifican.
- Guardado mediante blobs Base64 de bytes UTF-8; comprobación por recuperación de esos mismos blobs, comparación exacta de bytes y decodificación estricta.
- No se cambian código, configuración funcional, versiones, dependencias, PROJECT_STATE.md, requisitos ni ADR.

### Archivos de esta corrección

Modificados:
- `docs/delivery/BUILD.md`.
- `docs/handoffs/S00-T01-coder.md`.
- `docs/handoffs/evidence/S00-T01/manual.txt`.

Agregados:
- `docs/handoffs/evidence/S00-T01/revalidation-blocker.txt`.
- `docs/handoffs/evidence/S00-T01/encoding-verification.json`.
- `docs/handoffs/evidence/S00-T01/verify-utf8.ps1`.

### Validación de la corrección

| Verificación | Método/comando | Resultado |
|---|---|---|
| Codificación histórica | Lectura de los siete documentos de S00-T01 en Base64 y decodificación UTF-8 estricta | 111 caracteres dañados en los tres documentos señalados; cero en los otros cuatro |
| Codificación corregida | Recuperar blobs Git por SHA, comparar bytes con los enviados y decodificar UTF-8 estrictamente | PASS; cero caracteres de reemplazo; evidencia en encoding-verification.json |
| Entorno Android | adb devices por exec_command | NO EJECUTADO; sandbox rechazó la creación del proceso |
| Instalación / MainActivity / LocalCash / errores de inicio | Requieren adb/dispositivo funcional | NO EJECUTADO; no se pudo consultar dispositivos ni lanzar procesos |
| Sincronización Android Studio | Interfaz de Android Studio | NO EJECUTADO; no hay canal de control de la aplicación disponible y la ejecución local está bloqueada |
| Checkout limpio sin cachés preparadas | Requiere Git, Wrapper, SDK y descarga de dependencias | NO EJECUTADO; creación de procesos bloqueada antes de iniciar comandos |

La prueba de byte a byte se ejecutó en el entorno de herramientas sobre los blobs remotos. El script PowerShell adjunto permite reproducir la comprobación desde Git en un entorno funcional; no pudo ejecutarse localmente en esta sesión.

El error exacto, el comando intentado y la distinción entre pruebas históricas y pendientes están en `revalidation-blocker.txt` y `manual.txt`. Se mantiene documentada la deprecación de `android.builtInKotlin=false`; no se actualizan herramientas para eliminarla.

### Pendientes y solicitud de reauditoría

Solicito al Auditor reauditoría independiente del nuevo commit de esta rama, limitada a la corrección de codificación, la evidencia actualizada y las regresiones relacionadas: integridad de bytes UTF-8, preservación del texto histórico/resultados/comandos, y ausencia de cambios en código, versiones o configuración funcional.

La instalación y apertura, el sync de Android Studio y el checkout sin cachés siguen pendientes por el bloqueo documentado. No se declara S00-T01 aceptada ni cerrada, no se avanza a S00-T02 y no se hace merge. La aceptación corresponde al Auditor; el cierre y cualquier avance corresponden al Orquestador.
