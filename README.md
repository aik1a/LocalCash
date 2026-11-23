# LocalCash — MVP Android con Firebase

Aplicación académica de finanzas personales en Java, XML y Gradle Groovy. Google identifica al usuario; la biometría protege el acceso local. Firestore guarda datos por UID, con caché persistente para trabajar sin conexión después de preparar los datos.

## Estado

Base verificada: `736f748db92cff20c8caf24f8deca5136147d057`, rama `task/FB-mvp-delivery`. [CI de la base](https://github.com/aik1a/LocalCash/actions/runs/45102302173): SUCCESS con configuración sintética. Las 29 unitarias de dominio también se ejecutaron localmente el 2026-11-23, sin fallos.

**Aceptación final pendiente.** Compilar no acredita Google real, reglas desplegadas, biometría ni recorrido financiero offline/reinicio/reconexión. Ver [comprobaciones para cerrar](docs/delivery/CIERRE-FIREBASE-MVP.md).

## Compilar

Colocar la configuración cliente en `app/google-services.json` (ignorada por Git). Registrar la firma del APK, habilitar Google y preparar Firestore con las reglas productivas de `firestore.rules`. Nunca desplegar las reglas anónimas del prototipo de `tests/firebase`.

```powershell
.\gradlew.bat :app:assembleDebug :app:assembleDebugAndroidTest :app:testDebugUnitTest
```

No importa historial SQLite. Usar instalación de pruebas y datos sintéticos; conservar instalaciones existentes con datos. [Guía](docs/delivery/FIREBASE-MVP.md).

## Continuidad

Leer `PROJECT_ROUTER.md`, `AGENTS.md` y el inicio de `PROJECT_STATE.md`. Las restricciones históricas de SQLite como única persistencia están sustituidas para este incremento por Firebase. Las pruebas antiguas no acreditan el backend actual.

