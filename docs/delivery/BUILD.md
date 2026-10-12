# Compilación y ejecución

**Estado:** S00-T01; build de consola verificado el 2026-10-09. Arranque de la aplicación pendiente.

## Configuración comprobada

| Elemento | Valor |
|---|---|
| Android Studio instalado | 2026.1.4; build AI-261.26222.65.2614.16204760 |
| Android Gradle Plugin | 9.3.2 |
| Gradle Wrapper | 9.5.0, distribución bin |
| JDK usado para compilar | OpenJDK 25.0.3, JBR de Android Studio |
| Compatibilidad del código Java | 17 |
| SDK de compilación | API 36, minorApiLevel 1; plataforma android-36.1 |
| SDK Build Tools | 36.0.0 |
| SDK mínimo / objetivo | 24 / 36 |
| namespace / applicationId | com.aik1a.localcash |

El módulo `app` usa Java, layouts XML y scripts Gradle Groovy. No hay dependencias de aplicación. El soporte Kotlin integrado de AGP está desactivado mediante `android.builtInKotlin=false`. osmdroid y biometría pertenecen a tareas posteriores.

## Preparación

1. Abrir la raíz del repositorio en Android Studio.
2. Usar un JDK compatible con Gradle 9.5.0; la validación utilizó el JBR indicado arriba.
3. Instalar la plataforma SDK Android 36.1 y Build Tools 36.0.0.
4. Configurar la ruta del SDK mediante Android Studio o `local.properties` (archivo local ignorado por Git).
5. Sincronizar Gradle. La sincronización desde la interfaz de Android Studio no fue probada.

## Comandos

Windows, desde la raíz:

```powershell
.\gradlew.bat :app:assembleDebug
```

Linux/macOS:

```sh
./gradlew :app:assembleDebug
```

La primera ejecución requiere acceso a los repositorios de plugins y a la distribución Gradle. Linux/macOS no fueron ejecutados en esta validación.

## Validación ejecutada en Windows

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio1\jbr'
$env:GRADLE_USER_HOME = Join-Path (Get-Location) '.gradle-user-home'
$env:ANDROID_USER_HOME = Join-Path (Get-Location) '.android-user-home'
.\gradlew.bat --offline --no-daemon clean :app:assembleDebug :app:testDebugUnitTest
```

Se usó una copia local de cachés previamente disponibles, sin alterar las cachés del usuario. `local.properties` apuntó al SDK instalado. No se verificó una descarga desde caché vacía.

Resultado: `BUILD SUCCESSFUL in 25s`, 37 tareas ejecutadas, código de salida 0. `:app:testDebugUnitTest NO-SOURCE`: no hay pruebas unitarias ni instrumentadas en el esqueleto. No se agregaron pruebas nuevas, conforme al Task Brief.

APK: `app/build/outputs/apk/debug/app-debug.apk`.

SHA-256 del APK validado:
`C9A2EF89AF4A840DEF451D5F1AD88571320A65C98EFBDA02E26071F4D0159BDF`.

Evidencia: `docs/handoffs/evidence/S00-T01/clean-build-tests.txt` y `apk-badging.txt`.

## Ejecución pendiente

El manifiesto y el APK declaran `com.aik1a.localcash.MainActivity` como Activity lanzable. Su layout muestra «LocalCash».

No se verificó su apertura: no había dispositivo conectado y el emulador aislado permaneció `offline`. El intento inicial sobre el AVD existente falló por permisos de su lock. Ambos intentos se detuvieron. Véase `docs/handoffs/evidence/S00-T01/manual.txt`.

## Advertencias observadas

- La inicialización de métricas intentó acceder a `C:\Users\Admin\.android\analytics.settings` y recibió acceso denegado; el build final terminó correctamente.
- `android.builtInKotlin=false` está deprecado y se eliminará en AGP 10; no actualizar AGP sin revisar la configuración.
- Gradle indicó uso de características deprecadas incompatibles con Gradle 10. No se ocultaron las advertencias.

Referencia oficial para el SDK de API menor: [Configure your build](https://developer.android.com/build).

## Revalidación acotada de S00-T01 — 2026-10-10

Se restauraron los caracteres dañados en UTF-8 sin alterar comandos, resultados, versiones ni configuración funcional. La comprobación se realiza sobre bytes recuperados de GitHub, no sobre la visualización de la terminal.

La nueva ejecución local está bloqueada antes de iniciar procesos. El intento de `adb devices` fue rechazado al preparar el sandbox Windows: `managed networking requires the elevated Windows sandbox backend`.

Por ello, instalación del APK, apertura de MainActivity, texto «LocalCash», ausencia de errores de inicio, sincronización en Android Studio y compilación desde un checkout con caché vacía permanecen **NO EJECUTADO**. No se solicitó elevación ni se modificó el entorno para eludir la restricción.

Se conserva la evidencia de compilación del 2026-10-09; no equivale a una nueva compilación ni a un build sin cachés preparadas. La advertencia `android.builtInKotlin=false` sigue vigente y documentada arriba.

Evidencia de esta corrección: `docs/handoffs/evidence/S00-T01/revalidation-blocker.txt` y `encoding-verification.json`. Comprobación reproducible de bytes en un checkout: `powershell -NoProfile -File docs/handoffs/evidence/S00-T01/verify-utf8.ps1 -Ref HEAD`.
