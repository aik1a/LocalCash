# Handoff - S00-T01

**Rol saliente:** Codificador (Codex)
**Fecha:** 2026-10-09
**Rama/commit:** `task/S00-T01-proyecto-android-base` / implementaci�n `88502e38c24d3b178bae18bc62c2e1f4505c2042`; el commit de documentaci�n posterior contiene este handoff y la evidencia.
**Sprint:** S00 - Foundation

## Objetivo ejecutado

Crear el proyecto Android base Java/XML y Gradle Groovy sobre la documentaci�n existente, sin funciones de negocio.

## Estado

`PARTIAL`: implementaci�n y compilaci�n verificadas; instalaci�n y apertura de la Activity pendientes por limitaci�n del emulador. No se emite veredicto de auditor�a ni se autoriza cierre.

## Cambios realizados

- M�dulo app; namespace y applicationId `com.aik1a.localcash`.
- MainActivity basada en Activity del framework y layout XML que muestra �LocalCash�.
- minSdk 24, targetSdk 36 y compileSdk 36.1, sin permisos ni dependencias de aplicaci�n.
- AGP 9.3.2, Wrapper Gradle 9.5.0 y compatibilidad Java 17; JDK de build OpenJDK 25.0.3.
- Kotlin integrado de AGP desactivado para mantener el proyecto Java.
- .gitignore para archivos locales, builds y cach�s; .gitattributes para scripts en Windows/Unix.
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

## Validaci�n ejecutada

| Verificaci�n | Comando/escenario | Resultado |
|---|---|---|
| Wrapper | Gradle 9.5.0: wrapper --gradle-version 9.5.0 --distribution-type bin | PASS |
| Build | gradlew.bat --offline --no-daemon :app:assembleDebug | PASS; BUILD SUCCESSFUL in 21s, salida 0 |
| Build limpio | gradlew.bat --offline --no-daemon clean :app:assembleDebug :app:testDebugUnitTest | PASS; BUILD SUCCESSFUL in 25s, 37 tareas ejecutadas, salida 0 |
| Tests | :app:testDebugUnitTest | N/A; NO-SOURCE, ning�n caso ejecutado |
| APK | aapt dump badging app/build/outputs/apk/debug/app-debug.apk | PASS; package correcto, sdkVersion 24, targetSdkVersion 36, MainActivity lanzable |
| Diff | git diff --cached --check y revisi�n del diff/estructura | PASS; revisi�n del Codificador, no auditor�a independiente |
| Manual | AVD existente y AVD aislado en workspace | NO EJECUTADO; lock denegado en primer intento, dispositivo offline en segundo |

Evidencia versionada:
- `clean-build-tests.txt`: salida completa del build limpio y tarea de tests.
- `assembleDebug.txt`: build anterior exitoso.
- `apk-badging.txt`: metadatos del APK.
- `manual.txt`: comandos y limitaciones de ejecuci�n.
- `checks.txt`: comprobaciones de estructura, alcance y diff.

## Documentaci�n actualizada

BUILD.md documenta configuraci�n, comandos, resultado, cach�s usadas y limitaciones verificadas. PROJECT_STATE.md, Task Brief, trazabilidad, requisitos y arquitectura no fueron modificados. El Orquestador conserva su cierre.

## Decisiones tomadas

Ninguna decisi�n arquitect�nica ni funcional. Configuraci�n t�cnica dentro del Task Brief: herramientas instaladas, SDK 36.1 y Java 17. La plataforma android-36 estaba incompleta y el primer build la rechaz�; se utiliz� la plataforma 36.1 realmente instalada sin cambiar minSdk 24 ni targetSdk 36. No se instalaron nuevas dependencias.

## Hallazgos o riesgos

- Kotlin integrado a�adi� una dependencia de anotaciones no disponible offline; desactivarlo resolvi� el build Java. La opci�n est� deprecada para AGP 10.
- Advertencias de m�tricas por permisos y deprecaciones Gradle; no impidieron el build final.
- No se prob� sync de Android Studio ni checkout con cach� vac�a, ni ejecuci�n en API 24.
- Git por consola qued� sin respuesta. La fuente main se recuper� con el conector; los blobs y el �rbol base fueron comprobados contra GitHub. Los commits se publicaron mediante API sobre main `acc1fd9275e414606d895a49a2c834980e1d6575`, preservando su documentaci�n.

## Pendientes

- Instalar y abrir el APK en un dispositivo o emulador operativo; comprobar que muestra �LocalCash�.
- Auditor�a independiente de S00-T01.
- El Orquestador decide aceptaci�n, trazabilidad, cierre y cualquier avance.

## Contexto m�nimo para el siguiente agente

Leer:
- Task Brief `docs/tasks/active/S00-T01-proyecto-android-base.md`.
- `docs/agent/roles/AUDITOR.md` si act�a como Auditor.
- `docs/delivery/BUILD.md` y este handoff.

Revisar:
- Diff de la rama frente a main y evidencia citada.
- Activity, manifiesto, Gradle Groovy, SDK m�nimo y Wrapper.

## Pr�xima acci�n sugerida

Auditar S00-T01 y completar la prueba de instalaci�n/apertura. No avanzar a S00-T02 ni hacer merge sin autorizaci�n del Orquestador.
