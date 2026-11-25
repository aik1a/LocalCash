# Pruebas de LocalCash

## Dominio Java

```sh
bash tests/domain-checks.sh
./gradlew :app:testDebugUnitTest
```

Los casos comprueban importes, cuentas, movimientos, compras, repartos y ubicación. Las pruebas JUnit están en `app/src/test/java/com/aik1a/localcash/domain`.

## Compilación Android

```sh
./gradlew :app:assembleDebug :app:assembleDebugAndroidTest
```

En Windows, utilizar `gradlew.bat`. El workflow de GitHub Actions compila la aplicación, el APK de instrumentación y las pruebas unitarias. La configuración Firebase de CI es sintética y solo permite compilar; no acredita el inicio Google real. El APK de instrumentación debe instalarse y ejecutarse para comprobar sus recorridos.

## Comprobaciones Firestore

[tests/firebase/README.md](../tests/firebase/README.md) describe el recorrido aislado contra el emulador. Sus reglas de prueba no se deben publicar en un proyecto Firebase real. Las reglas de la aplicación están en `firestore.rules`.

## Recorrido funcional

Con configuración Firebase real, acceder con Google y biometría. Crear una cuenta CLP con 20.000, ingresar 5.000 y registrar un egreso de 2.000: saldo esperado 23.000. Planificar una compra de 1.500 mantiene el saldo; pagarla desde esa cuenta produce 21.500. Comprobar contactos, reparto y estado de deudas.

Desconectar después de la carga inicial, registrar una operación, cerrar y reabrir, reconectar y comparar el documento y saldo remoto. Repetir con dos dispositivos y comprobar que una compra no genera egresos duplicados. Para mapas, comprobar ubicación actual con permiso concedido, denegación de permiso y navegación mundial sin red.

Los scripts y fuentes SQLite se conservan para reproducir la versión histórica. No son pruebas del backend Firebase ni están conectados al runner actual.
