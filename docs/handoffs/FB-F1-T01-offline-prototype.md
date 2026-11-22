# Handoff — FB-F1-T01

Fecha: 2026-11-22. Rol: Codificador. Base aceptada **local**: dbebac2.
Rama: task/FB-F1-T01-offline-prototype. Destino previsto: feat/firebase-migration.
Estado: **PARTIAL / preparado para auditoría; recorrido Android pendiente**.
No se publica PR ni se fusiona desde esta entrega.

## Resultado

Prototipo aislado de persistencia Firestore mediante el modo `firestore` del runner
existente. Cinco fases reproducibles: prepare, offline, resumeOffline, reconnect y
reject. IDs estables sintéticos, snapshot local con pendingWrites, confirmación
remota separada, nuevo proceso y rechazo PERMISSION_DENIED con recuperación.

SDK oficial Firestore **27.2.0**, solo debugImplementation: registradores presentes
en el APK objetivo de instrumentación; release no añade esa dependencia. Debug
permite INTERNET y HTTP para Emulator Suite. No se modifican UI, dominio financiero,
SQLite, Google Auth, configuración del usuario ni sources/. Configuración explícita
para demo-localcash-f1 y 10.0.2.2:8080, sin credenciales ni google-services.json.
Las reglas de prueba son anónimas y acotadas; NO aptas para desplegar en Firebase.

Archivos: app/build.gradle; manifest debug; FlowChecks y FirestoreOfflineChecks en
androidTest; tests/firebase/{firebase.json,firestore.rules,run-offline.py,README.md};
este handoff. No hay runner adicional: AGP fija el registrado en Gradle; se reutiliza
FlowChecks sin alterar sus otros modos.

## Validación realmente ejecutada

| Verificación | Resultado |
|---|---|
| Gradle9.8.2, AGP9.5, Temurin25, SDK36.1: assembleDebug + assembleDebugAndroidTest + testDebugUnitTest | **PASS**, exit0, BUILD SUCCESSFUL, 72 tareas (33 ejecutadas,39 actualizadas) |
| JUnit vía Gradle | **PASS**, 29 pruebas, cero fallos/errores |
| Manifest combinado debug | INTERNET y cleartext presentes; FirestoreRegistrar presente en APK objetivo |
| Manifest combinado instrumentación | FlowChecks registrado para com.aik1a.localcash |
| Script Python AST y JSON | **PASS** |
| git diff --check | **PASS** |
| Recorrido emulator Firestore/Android, muerte de proceso, sincronización y rechazo | **NOT TESTED**, no emulador Android disponible |
| Google real, aislamiento A/B, modo avión completo | **NOT TESTED**, fuera de esta prueba F1-T01 |

Build real: Gradle preparado de android-readiness con `--no-daemon --console=plain
--info :app:assembleDebug :app:assembleDebugAndroidTest :app:testDebugUnitTest`, cwd
LocalCash-offline-prototype. Variables de proceso para JDK/SDK/cache y proxy de red;
no se modificaron versiones del repo ni aceptaron licencias artificialmente.
Log interno: android-readiness/prototype-build.log y prototype-build.exit. No se
versiona log del entorno; APK generados en rutas normales app/build/outputs/apk/.

## Próxima acción

Auditor independiente revisa el SHA final y alcance. Ejecutar las instrucciones de
[tests/firebase/README.md](../../tests/firebase/README.md) en emulador descartable;
no borrar datos/caché entre fases ni reiniciar Emulator Suite. El script guarda SHA,
API, runId y logs por fase. Usa disableNetwork del SDK; no acredita por sí solo modo
avión de toda la aplicación. Hasta obtener esa evidencia no declarar F1 viable,
ni módulos migrados, ni producción Firebase lista. Pago atómico/concurrencia,
esquema definitivo y Google real pertenecen a tareas siguientes.
