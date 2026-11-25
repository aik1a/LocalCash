# LocalCash

Aplicación Android de finanzas personales en Java y XML, con Firebase Authentication, Cloud Firestore y acceso biométrico.

Permite administrar cuentas por divisa, ingresos, egresos, compras, contactos y gastos compartidos. Incluye mapa mundial básico offline, calles de Chile, ubicación actual e importación de mapas `.map` de otros países.

## Configuración

1. Clonar el repositorio y abrirlo en Android Studio desde `main`.
2. Usar JDK 25 para Gradle e instalar Android SDK 36.1 y Build Tools 36.0.0. Android mínimo: API 24.
3. Registrar `com.aik1a.localcash` en Firebase y colocar la configuración en `app/google-services.json`, excluido de Git.
4. Habilitar Google en Firebase Authentication y registrar la huella SHA del certificado de firma del APK.
5. Crear Cloud Firestore y publicar las reglas de `firestore.rules`, en la raíz.
6. Ejecutar en un dispositivo con servicios Google y biometría configurada.

## Compilación y pruebas

Windows:

```powershell
.\gradlew.bat :app:assembleDebug :app:assembleDebugAndroidTest :app:testDebugUnitTest
```

Linux o macOS:

```sh
./gradlew :app:assembleDebug :app:assembleDebugAndroidTest :app:testDebugUnitTest
bash tests/domain-checks.sh
```

El APK se genera en `app/build/outputs/apk/debug/app-debug.apk`. GitHub Actions utiliza configuración Firebase sintética para compilar; el acceso Google requiere la configuración real y la firma registrada.

Las pruebas unitarias están en `app/src/test`. El recorrido aislado Firestore utiliza `tests/firebase/run-offline.py` y Firebase Emulator con el proyecto `demo-localcash-f1`, según `tests/firebase/firebase.json`. **Las reglas de esa carpeta son solo de prueba y no deben publicarse en Firebase real.** Las fuentes y scripts SQLite corresponden a la versión histórica.

## Datos y funcionamiento

Los datos se guardan por usuario bajo `users/{uid}`, en las colecciones `configuracion`, `cuentas`, `movimientos`, `contactos` y `compras`. El dominio financiero usa importes enteros; los repositorios y `LocalCashStore` coordinan el acceso a Firestore.

La primera autenticación y descarga de datos requieren conexión. La caché permite trabajar con información previamente cargada y sincronizar cambios al reconectar. La aplicación no importa automáticamente el historial SQLite ni realiza transferencias bancarias o conversiones de moneda.

La primera compilación descarga cartografía Mapsforge y necesita espacio libre: el mapa de Chile ocupa aproximadamente 650 MB. Los recursos se incluyen en el APK y se consultan sin red. La base mundial no contiene todas las calles.

Datos cartográficos © OpenStreetMap contributors, licencia ODbL 1.0. Atribución y licencia en `app/src/main/assets/maps/LICENSE-OSM.txt` y en los recursos vectoriales generados.
