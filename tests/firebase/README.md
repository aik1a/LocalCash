# Comprobación aislada de Firestore

El runner `FlowChecks` admite `mode=firestore`. El recorrido usa el proyecto sintético `demo-localcash-f1` y Firestore Emulator en `10.0.2.2:8080`. Comprueba confirmación remota, escritura local sin red, reinicio del proceso, reconexión y rechazo de un valor inválido.

```sh
./gradlew :app:assembleDebug :app:assembleDebugAndroidTest
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
firebase emulators:start --only firestore --project demo-localcash-f1 --config tests/firebase/firebase.json
```

En otra terminal:

```sh
python tests/firebase/run-offline.py --serial emulator-5554 --output verification/firestore
```

Usar un emulador Android de pruebas y conservar el mismo proceso de Firestore Emulator durante las fases. La desconexión se aplica al SDK; no representa modo avión de toda la aplicación. El recorrido no comprueba el inicio Google ni el pago compuesto de compras.

Las reglas de esta carpeta son exclusivamente de prueba. **No publicarlas en Firebase real.** La aplicación utiliza las reglas de `firestore.rules` en la raíz del repositorio.
