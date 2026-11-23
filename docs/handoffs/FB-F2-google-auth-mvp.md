# Handoff — Google Auth MVP (F2 WIP)

Fecha: 2026-11-23. Rol: Codificador. Rama: task/FB-F2-google-auth-mvp.
Base remota: 93a20f6a0dec0589b8f149546332e7cf92eead87.
Estado: código de acceso implementado, **Google real NOT TESTED**. Trabajo F2
paralelo autorizado por usuario; no aprobación F1/F2 ni entrega final.

## Qué cambia

- Botón «Continuar con Google» en el bloqueo existente, sin rediseñar módulos.
- Credential Manager obtiene token Google y FirebaseAuth autentica realmente.
  Cancelación, cuenta no disponible, error y reintento mantienen acceso bloqueado.
- Sesión Firebase persistida permite volver offline; biometría fuerte sigue siendo
  obligatoria antes de crear contenido financiero. No simula Google sin conexión
  en primer acceso. Restauración/rotación no vuelve verdadera la biometría.
- Protección en Activities y callbacks comprueba Google + sesión biométrica;
  pérdida/cambio de identidad oculta contenido y vuelve al bloqueo.
- INTERNET habilitado en main. Mapas, cálculos, formatos y navegación financiera
  existentes se mantienen. Firestore ya disponible en producción para siguiente
  bloque de datos, pero este commit **no migra finanzas**.

## Límite SQLite transitorio

No hay selector ni cierre de cuenta nuevo. Antes de biometría se guarda un UID
propietario local y se exige el mismo usuario después. Si ya existe localcash.db y
no hay propietario, se rechaza adjudicarla al primer Google; si aparece otro UID,
se cierra esa sesión Firebase y se mantiene bloqueo. No leer, borrar ni desinstalar
la base anterior. Probar este WIP en instalación limpia de emulador separada.
Esto es una guardia temporal para SQLite, **no una certificación de aislamiento
Firestore A/B**. Tras integrar repositorios UID, adaptar bootstrap de
ActividadProtegida a LocalCashStore y retirar esta limitación según Orquestador.

## Configurar y compilar

Colocar el JSON cliente actualizado de Firebase en **app/google-services.json**,
con paquete com.aik1a.localcash, Google habilitado, cliente OAuth web y SHA del APK
que se compila. Archivo ignorado por Git; no está en esta entrega. No hace falta
cuenta de servicio. Se usó el JSON provisto por usuario fuera del repositorio para
compilar, sin imprimir ni versionar claves, IDs de cliente o correo.

```sh
bash gradlew :app:assembleDebug :app:assembleDebugAndroidTest :app:testDebugUnitTest
```

Sin JSON de configuración la compilación Google Services debe solicitarlo; no
hay fallback que permita entrar con una identidad simulada. La firma debug de
este entorno no se presume igual a la del PC del usuario: registrar SHA1/256 del
APK probado en Firebase antes de comprobar Google real. No reemplazar una app con
datos para resolver firmas.

Versiones contrastadas con fuentes primarias: FirebaseAuth25.0.0,
Firestore27.2.0, Google Services4.6.0 (metadata MavenGoogle),
CredentialManager/PlayServicesAuth1.3.0 y googleid1.1.1 (guía FirebaseGoogle).
[Guía Google](https://firebase.google.com/docs/auth/android/google-signin),
[Credential Manager](https://developer.android.com/identity/sign-in/credential-manager-siwg).

## Evidencia y pendientes

Build app/instrumentación y 29 JUnit de dominio ejecutados con herramientas
preparadas (JDK25, Gradle9.8.2, AGP9.5, SDK36.1): **BUILD SUCCESSFUL exit0**,
71 tareas (7 ejecutadas/64 actualizadas); **29 JUnit, cero fallos/errores**.
Manifest combinado debug confirma INTERNET; JSON confirmado ignorado por Git.
Diff y ausencia de JSON versionado
contrastados. No se añadió batería de mocks ni otra infraestructura de tests.

Google real, cancelación en dispositivo, biometría+navegación manual, token
caducado, segundo plano y primer acceso offline: **NOT TESTED** por no haber
Google/dispositivo en este entorno. Build no acredita esos flujos. Los runners
Android históricos compilan pero no han sido adaptados para entrar con Google.
Auditor independiente recibe el SHA; sin merge/push ni servicios remotos tocados.
