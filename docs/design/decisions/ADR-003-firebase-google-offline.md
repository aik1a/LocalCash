# ADR-003 — Firebase, Google y operación offline

Fecha: 2026-11-16.
Estado: cambio de requisitos CONFIRMADO por el usuario como nueva indicación docente; diseño técnico PROPUESTO, pendiente de viabilidad F1.

## Contexto

El encargo original y ADR-001 exigían SQLite local. El usuario informa y confirma una actualización docente: reemplazar SQLite por Firebase, usar inicio de sesión con Google y mantener operación offline. Se registra esta procedencia sin modificar el PDF ni atribuirle el nuevo requisito.

## Decisión de alcance

SQLite deja de ser un requisito de la versión objetivo. Se incorporan Firebase, Google Auth y funcionamiento offline. Esta actualización sustituye las exclusiones previas de Firebase y la decisión de SQLite como única persistencia para el nuevo incremento; la implementación actual sigue usando SQLite hasta migrarse y validarse. Java/XML, biometría, mapas y reglas funcionales continúan vigentes.

## Propuesta técnica

Cloud Firestore con caché persistente Android y Firebase Authentication con Google/Credential Manager. UID como frontera de propiedad. Biometría para desbloqueo local. Primer acceso y preparación de datos con conexión; uso posterior offline y sincronización al reconectar, sujeto a contrato validado.

No se acepta todavía un esquema definitivo, estrategia de conflictos o equivalencia entre transacciones SQL y Firestore. Se resolverán con un prototipo antes de migrar módulos. La caché Firestore no se presenta como garantía de conservar todo el historial indefinidamente.

## Consecuencias

Adaptar IDs, repositorios, servicios, pantallas asíncronas, reglas de acceso y pruebas. Definir importación de datos existentes y firma compatible. Google requiere conexión para autenticación inicial; sin preparación previa no se garantiza acceso a datos remotos. Separar guardado local, pendiente y confirmación remota.

Plan y criterios: [FB-01](../../exec-plans/active/FB-01-firebase-google-offline.md).
