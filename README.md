# LocalCash

Aplicación Android de finanzas personales desarrollada en Java y XML. Permite administrar cuentas, ingresos, egresos, compras, contactos y gastos compartidos.

## Funciones

- Inicio de sesión con Google y acceso protegido con biometría.
- Datos financieros privados por usuario en Cloud Firestore.
- Caché persistente y estado de sincronización para conexiones intermitentes.
- Cuentas por divisa, movimientos y cálculo de saldos con importes enteros.
- Compras planificadas, pago asociado a una cuenta y distribución de gastos.
- Mapa mundial básico sin conexión, calles de Chile y ubicación actual con permiso del usuario. Otros países admiten mapas `.map` importados desde el dispositivo.

## Configuración

1. Clonar este repositorio y abrirlo en Android Studio desde `main`.
2. Usar JDK 25 para Gradle; el código Java tiene compatibilidad 17. Instalar Android SDK 36.1 y Build Tools 36.0.0. Android mínimo: API 24.
3. Registrar `com.aik1a.localcash` en Firebase y colocar su configuración en `app/google-services.json`, excluido de Git.
4. Habilitar Google en Firebase Authentication y registrar la huella SHA del certificado usado para firmar el APK.
5. Crear Cloud Firestore y publicar las reglas de `firestore.rules`.
6. Ejecutar en un dispositivo con servicios Google y biometría configurada.

La primera compilación descarga cartografía de OpenStreetMap en formato Mapsforge. El mapa detallado de Chile ocupa aproximadamente 650 MB; se necesita conexión, tiempo y espacio libre para preparar y empaquetar estos recursos. Los archivos se reutilizan en compilaciones posteriores y se incluyen en el APK para consultarlos sin conexión.

## Compilar y probar

Windows:

```powershell
.\gradlew.bat :app:assembleDebug :app:assembleDebugAndroidTest :app:testDebugUnitTest
```

Linux o macOS:

```sh
./gradlew :app:assembleDebug :app:assembleDebugAndroidTest :app:testDebugUnitTest
bash tests/domain-checks.sh
```

APK: `app/build/outputs/apk/debug/app-debug.apk`.

GitHub Actions compila con una configuración Firebase sintética. Para iniciar sesión con Google, compilar con la configuración real del proyecto y la firma registrada.

## Documentación

- [Arquitectura](ARCHITECTURE.md).
- [Datos y reglas financieras](docs/DATABASE.md).
- [Pruebas](docs/TESTING.md).
- [Requisitos funcionales](docs/requirements/PROJECT_SPEC.md).
- [Cartografía y licencias](docs/maps/README.md).

La primera autenticación y descarga de datos financieros requieren conexión. La aplicación no importa automáticamente el historial de versiones SQLite y no realiza transferencias bancarias ni conversión de moneda.
