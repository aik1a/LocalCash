# Compilación, instalación y ejecución

**Guía vigente: T07C, 2026-10-18.** Fuentes contrastadas: main 9084b573ebe055485e3cdda11ca8db2d8df6a3bc, Gradle/manifest y CI T07A. Las instrucciones no significan que Sync Studio limpio o un dispositivo físico hayan sido probados.

## Configuración real

| Elemento | Valor |
|---|---|
| Android Gradle Plugin / wrapper | 9.3.2 / Gradle 9.5.0 bin |
| JDK de CI | Temurin 25; lenguaje Java 17 |
| compileSdk / Build Tools | 36.1 (platforms;android-36.1) / 36.0.0 |
| minSdk / targetSdk | 24 / 36 |
| applicationId / namespace | com.aik1a.localcash |
| versionCode / versionName | 1 / 1.0 |
| UI / persistencia | XML / SQLiteOpenHelper, localcash.db v4 |
| Dependencias directas Android | androidx.biometric:biometric:1.1.0 y org.osmdroid:osmdroid-android:6.1.18 |
| Repositorios | google, mavenCentral; plugins también gradlePluginPortal |
| Configuración | android.useAndroidX=true; android.builtInKotlin=false |
| Runner instrumentado | com.aik1a.localcash.FlowChecks |

No se deben agregar Room, Compose, backend ni claves de firma para compilar esta entrega. Las dependencias transitivas se observan con `:app:dependencies --configuration debugRuntimeClasspath`.

## Obtener fuentes

Con acceso autorizado al repositorio:
```sh
git clone https://github.com/aik1a/LocalCash.git
cd LocalCash
git checkout 9084b573ebe055485e3cdda11ca8db2d8df6a3bc
```
Esa base aceptada contiene el código actual tras PR #10. Para reproducir **el checkout del build histórico T07A**, usar `git checkout 32dd6e0ff09143bfc55276d5084c18472769b4af`. No se promete un APK de bytes idénticos por recompilar: el entorno/firmante debug puede diferir. Para la documentación T07C usar su rama/commit entregado en el handoff.

## Preparar Android Studio o CLI

1. Abrir la raíz que contiene settings.gradle, no solo app/.
2. Configurar JDK 25, SDK Android 36.1 y Build Tools 36.0.0. Android Studio debe poder cargar AGP 9.3.2; no se certifica otra combinación.
3. Instalar platform-tools y, para pruebas, emulator e imagen google_apis x86_64 API24/API36; CI usa AVD de 2048 MiB y KVM.
4. Definir la ruta SDK por Android Studio o local.properties local (no versionarlo). En CLI con sdkmanager disponible:
```sh
sdkmanager "platform-tools" "platforms;android-36.1" "build-tools;36.0.0"
```
5. Sincronizar Gradle desde Android Studio. **Sync Studio desde entorno limpio: NOT TESTED.** Actions sí compiló mediante wrapper.

La primera preparación/compilación puede requerir Internet para SDK, Gradle y dependencias. **La aplicación instalada opera offline**; no confundir ese requisito con un build inicial sin descargas. El atlas ya está en assets; no se obtiene OSM ni tiles durante build/ejecución.

## Compilar

Linux/macOS, desde la raíz:
```sh
bash gradlew --no-daemon --console=plain :app:assembleDebug :app:assembleDebugAndroidTest
```
Windows PowerShell:
```powershell
.\gradlew.bat --no-daemon --console=plain :app:assembleDebug :app:assembleDebugAndroidTest
$LASTEXITCODE
```
Salidas:
- `app/build/outputs/apk/debug/app-debug.apk`: aplicación.
- `app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk`: instrumentación; **no es la app de entrega**.

[CI T07A 39046939759](https://github.com/aik1a/LocalCash/actions/runs/39046939759): SHA 32dd6e0ff09143bfc55276d5084c18472769b4af, API24/API36, ambos assemble PASS/exit 0, 63 tareas ejecutadas por job. [Resultados completos](../testing/S00-T07A.md). T07C no vuelve a compilar ni adjudica ese build a su nuevo commit documental.

## Pruebas reproducibles

Con JDK y entorno disponibles:
```sh
bash tests/domain-checks.sh
```
CI T07A ejecutó 5483 comprobaciones por job: 18 dinero/cuentas + 15 compras/contactos + 5427 reparto + 23 ubicación. No son 5483 tests instrumentados.

Para instrumentación usar el [workflow T07A](../../.github/workflows/s00-t07a-build.yml), que instala app/tests en AVD nuevo, enrola huella mediante ajustes/sensor y ejecuta los scripts de biometría, mapa y dispatcher. **Los fixtures borran/recrean la BD de prueba: solo AVD descartable, nunca un equipo con datos reales.** Un `connectedAndroidTest` genérico no reproduce por sí solo todos los modos de FlowChecks.

El workflow permite workflow_dispatch. Sus filtros push solo apuntan a su rama T07A y omiten archivos Gradle raíz: cambios documentales T07C no disparan build automático, y un futuro cambio de Gradle debe solicitar ejecución explícita (observación MINOR T07A, sin modificar el workflow aquí).

Para verificar/reproducir el atlas con Python 3.12 y Pillow 12.3.0:
```sh
python -m pip install -r tools/maps/requirements.txt
python tools/maps/verify_atlas.py
python tools/maps/generate_offline_tiles.py --output /tmp/atlas/santiago_centro.zip
cmp app/src/main/assets/maps/santiago_centro.zip /tmp/atlas/santiago_centro.zip
```
Pillow es herramienta de mantenimiento, no dependencia Android. [Procedencia/licencia](../maps/S00-T06B-data-provenance.md).

## APK seleccionado y comprobación

[Artefacto T07A API36 12105649198](https://github.com/aik1a/LocalCash/actions/runs/39046939759/artifacts/12105649198). Dentro del ZIP, extraer `app/build/outputs/apk/debug/app-debug.apk`. Es debug, no release.

- APK SHA256: `f6c188ae06db78a6068b64b69157b4fcfe776b6009561a1a0e9269086e9a83d0`.
- Certificado SHA256: `49d73d9652d1cbf9c9891fb90a3d01fc94a3f2a6dcb02aea8a20471b927f191a`.
- ZIP Actions SHA256: `9ab61141e5c36e2770b9a870136c1b4941dd99c65cd6164ed29c7daa14d918ab`.
- API36 es la plataforma de validación; **el APK conserva minSdk 24**.
- El repositorio es privado: las referencias de descarga requieren acceso autorizado. Caducidad del artefacto: enero de 2027.

Hash local:
```sh
sha256sum app-debug.apk
```
En PowerShell:
```powershell
Get-FileHash -Algorithm SHA256 .\app-debug.apk
```
Con Build Tools 36.0.0 en PATH:
```sh
apksigner verify --verbose --print-certs app-debug.apk
aapt dump badging app-debug.apk
```

## Instalar y ejecutar sin comprometer datos

**Instalación nueva:** Android API24 o superior, espacio para app/SQLite/atlas privado y biometría fuerte compatible enrolada en Ajustes Android. Para AVD, enrolar huella desde ajustes y usar el sensor emulado, sin modificar el gate.

Comprobar `adb shell pm list packages com.aik1a.localcash`. Si ya existe el paquete principal, detener la instalación nueva y leer el apartado de actualización. Si no existe:
```sh
adb install app-debug.apk
adb shell am start -n com.aik1a.localcash/.presentation.MainActivity
```
Autenticar con la huella configurada. Sin biometría registrada/compatible el acceso queda bloqueado; el PIN del bloqueo Android no es un acceso alternativo de LocalCash.

**Instalación existente:** [T07B y procedimiento completo](../testing/S00-T07B.md) comprobó diez certificados principales distintos y cero pares históricos compatibles. Actualización in-place **BLOCKED_SIGNER_MISMATCH**; conservación SQLite después de actualizar **NOT TESTED**. No desinstalar, borrar datos/SQLite, forzar downgrade ni re-firmar para superar el bloqueo.

Solo un APK de mismo package, firma compatible y versionCode no decreciente permite planificar una prueba `adb install -r` en AVD descartable con datos antes/después. Ningún par inventariado cumple firma. Para un APK de usuario no aportado, compatibilidad NO DETERMINADA. No hay exportación/importación o estrategia de migración nueva autorizada.

## Demostrar offline y límites

Desactivar Wi-Fi/datos antes de abrir el mapa por primera vez. La cartografía Santiago Centro z14–16 está incluida en el APK; no pide Internet ni almacenamiento externo. GPS es opcional al registrar movimientos. Fuera de cobertura se muestra aviso y no se promete cartografía. El enlace externo de atribución no es requisito para usar el mapa.

[Guía de demostración](PRESENTATION.md) · [Arquitectura](../../ARCHITECTURE.md) · [SQLite v4](../design/DATABASE.md).

NOT TESTED: recorrido financiero/GPS completo API24, dispositivo físico, GPS exclusivamente aproximado, Sync Studio limpio y upgrade histórico conservando datos. FLAG_SECURE puede impedir capturas del sistema; no se desactiva para la entrega.

Advertencias heredadas: android.builtInKotlin=false deprecado, APIs deprecadas, avisos de manifest merger por retirada de permisos y compatibilidad futura Gradle 10. No se actualizan versiones para eliminarlas en T07C.

## Antecedente histórico preservado

La siguiente guía documenta **T01 (2026-10-09/10)**. Sus versiones de Activity, falta de dependencias, NO-SOURCE y límites Windows corresponden al esqueleto de entonces; no sustituyen los resultados T07A ni las instrucciones vigentes anteriores.

<details>
<summary>BUILD anterior a T07C — antecedente íntegro T01</summary>

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

</details>
