# Prototipo F1-T01

Prueba aislada, sin modificar pantallas ni conectar el proyecto Firebase del usuario.
Firestore Android **27.2.0**, verificado el 2026-11-22 contra notas oficiales y
metadata Maven Google. Dependencia solo `debugImplementation` para que los registradores Firebase estén
en el APK objetivo de instrumentación; release no incluye Firebase y la app
financiera sigue usando SQLite. `debug` permite INTERNET/HTTP local; release conserva su manifest.
No usa Google Services plugin ni `google-services.json`. Opciones explícitas del
proyecto **demo-localcash-f1**, host fijo del emulador Android `10.0.2.2:8080`.

En un emulador Android descartable sin datos personales, instalar app e
instrumentación compiladas. No desinstalar ni limpiar datos entre fases:

```sh
./gradlew :app:assembleDebug :app:assembleDebugAndroidTest
adb -s emulator-5554 install -r app/build/outputs/apk/debug/app-debug.apk
adb -s emulator-5554 install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
firebase emulators:start --only firestore --project demo-localcash-f1 --config tests/firebase/firebase.json
```

En otra terminal, manteniendo vivo el emulador Firestore:

```sh
python tests/firebase/run-offline.py --serial emulator-5554 --output verification/FB-F1-T01
```

El modo `firestore` se añade al dispatcher `FlowChecks`, sin cambiar sus otros modos. Fases: preparar documento sintético con
confirmación servidor; bloquear red del **SDK** y actualizar; comprobar dato local
con pendingWrites sin confundirlo con Task remota; forzar cierre del proceso y abrir
otra instrumentación offline; reconectar y comprobar confirmación remota del mismo
ID; rechazar valor inválido y comprobar recuperación del valor confirmado.
El proceso detiene la red antes de leer/reanudar, no borra caché ni SQLite.
La prueba usa `disableNetwork`, no demuestra por sí sola modo avión de toda la app.
Ejecutar sobre dispositivo diferente o reiniciar/borrar el emulador Firestore entre
fases invalida el recorrido.

Las reglas son exclusivamente de prototipo, anónimas y acotadas a un documento
sintético: **no desplegarlas** en Firebase real. Autenticación, aislamiento A/B,
pago compuesto/concurrencia, historial completo y Google real se prueban en las
siguientes tareas. No declarar viabilidad F1 aprobada sin ejecutar este recorrido.

Fuentes: [caché](https://firebase.google.com/docs/firestore/manage-data/enable-offline),
[emulador](https://firebase.google.com/docs/emulator-suite/connect_firestore),
[versiones](https://firebase.google.com/support/release-notes/android).
