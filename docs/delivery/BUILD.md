# Compilaci�n y ejecuci�n

**Estado:** S00-T01; build de consola verificado el 2026-10-09. Arranque de la aplicaci�n pendiente.

## Configuraci�n comprobada

| Elemento | Valor |
|---|---|
| Android Studio instalado | 2026.1.4; build AI-261.26222.65.2614.16204760 |
| Android Gradle Plugin | 9.3.2 |
| Gradle Wrapper | 9.5.0, distribuci�n bin |
| JDK usado para compilar | OpenJDK 25.0.3, JBR de Android Studio |
| Compatibilidad del c�digo Java | 17 |
| SDK de compilaci�n | API 36, minorApiLevel 1; plataforma android-36.1 |
| SDK Build Tools | 36.0.0 |
| SDK m�nimo / objetivo | 24 / 36 |
| namespace / applicationId | com.aik1a.localcash |

El m�dulo `app` usa Java, layouts XML y scripts Gradle Groovy. No hay dependencias de aplicaci�n. El soporte Kotlin integrado de AGP est� desactivado mediante `android.builtInKotlin=false`. osmdroid y biometr�a pertenecen a tareas posteriores.

## Preparaci�n

1. Abrir la ra�z del repositorio en Android Studio.
2. Usar un JDK compatible con Gradle 9.5.0; la validaci�n utiliz� el JBR indicado arriba.
3. Instalar la plataforma SDK Android 36.1 y Build Tools 36.0.0.
4. Configurar la ruta del SDK mediante Android Studio o `local.properties` (archivo local ignorado por Git).
5. Sincronizar Gradle. La sincronizaci�n desde la interfaz de Android Studio no fue probada.

## Comandos

Windows, desde la ra�z:

```powershell
.\gradlew.bat :app:assembleDebug
```

Linux/macOS:

```sh
./gradlew :app:assembleDebug
```

La primera ejecuci�n requiere acceso a los repositorios de plugins y a la distribuci�n Gradle. Linux/macOS no fueron ejecutados en esta validaci�n.

## Validaci�n ejecutada en Windows

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio1\jbr'
$env:GRADLE_USER_HOME = Join-Path (Get-Location) '.gradle-user-home'
$env:ANDROID_USER_HOME = Join-Path (Get-Location) '.android-user-home'
.\gradlew.bat --offline --no-daemon clean :app:assembleDebug :app:testDebugUnitTest
```

Se us� una copia local de cach�s previamente disponibles, sin alterar las cach�s del usuario. `local.properties` apunt� al SDK instalado. No se verific� una descarga desde cach� vac�a.

Resultado: `BUILD SUCCESSFUL in 25s`, 37 tareas ejecutadas, c�digo de salida 0. `:app:testDebugUnitTest NO-SOURCE`: no hay pruebas unitarias ni instrumentadas en el esqueleto. No se agregaron pruebas nuevas, conforme al Task Brief.

APK: `app/build/outputs/apk/debug/app-debug.apk`.

SHA-256 del APK validado:
`C9A2EF89AF4A840DEF451D5F1AD88571320A65C98EFBDA02E26071F4D0159BDF`.

Evidencia: `docs/handoffs/evidence/S00-T01/clean-build-tests.txt` y `apk-badging.txt`.

## Ejecuci�n pendiente

El manifiesto y el APK declaran `com.aik1a.localcash.MainActivity` como Activity lanzable. Su layout muestra �LocalCash�.

No se verific� su apertura: no hab�a dispositivo conectado y el emulador aislado permaneci� `offline`. El intento inicial sobre el AVD existente fall� por permisos de su lock. Ambos intentos se detuvieron. V�ase `docs/handoffs/evidence/S00-T01/manual.txt`.

## Advertencias observadas

- La inicializaci�n de m�tricas intent� acceder a `C:\Users\Admin\.android\analytics.settings` y recibi� acceso denegado; el build final termin� correctamente.
- `android.builtInKotlin=false` est� deprecado y se eliminar� en AGP 10; no actualizar AGP sin revisar la configuraci�n.
- Gradle indic� uso de caracter�sticas deprecadas incompatibles con Gradle 10. No se ocultaron las advertencias.

Referencia oficial para el SDK de API menor: [Configure your build](https://developer.android.com/build).
