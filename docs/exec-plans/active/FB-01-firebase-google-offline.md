# FB-01 — Firebase, Google Auth y operación offline

Fecha: 2026-11-16. Estado: PLANIFICADO; implementación no iniciada.
Responsabilidad de esta entrega: Orquestador, revisión y planificación; no auditoría independiente.

## Objetivo

Migrar LocalCash de SQLite a Cloud Firestore, incorporar inicio de sesión con Google mediante Firebase Authentication y mantener los flujos financieros offline después de una primera autenticación y preparación con conexión. Conservar Java/XML, dominio financiero, biometría y mapas locales. Cada fase produce evidencia verificable antes de avanzar.

## Requisitos relacionados y procedencia

El usuario comunica el 2026-11-16 una nueva indicación del docente y confirma expresamente: sustituir SQLite por Firebase, iniciar sesión con Google y mantener funcionamiento offline. Esta actualización sustituye las restricciones previas sobre SQLite/Firebase; el PDF original permanece intacto como antecedente histórico. No se ha recibido un nuevo documento docente escrito.

- FB-R01: Cloud Firestore sustituye SQLite como persistencia de la aplicación final.
- FB-R02: Google es el proveedor de inicio de sesión mediante Firebase Authentication.
- FB-R03: consultar y modificar datos preparados en el dispositivo sin conexión, conservar cambios tras reiniciar y sincronizar al reconectar.
- FB-R04: aislar datos por UID; conservar las reglas financieras y evitar duplicados por reintentos.
- FB-R05: mantener Java, XML, biometría y mapas offline.
- LC-P02/LC-TECH-01 quedan reformulados por FB-R01/03; LC-AUTH-01/LC-TECH-02 continúan como protección local, complementada por FB-R02. Los demás módulos funcionales continúan vigentes.

## Estado inicial comprobado

Base: aik1a/LocalCash main c28a27b87aeeb092a936b8ba6b05b83404ac1c13, consultada el 2026-11-16.

- APK declarado 1.1.1, versionCode 3; Java 17, minSdk 24, targetSdk 36.
- SQLite v7, nueve tablas: cuentas, movimientos, contactos, compras, detalle_compra, deudas, configuracion, reparto_usuario y compra_movimiento.
- Repositorios concretos y servicios dependen de LocalCashDbHelper; servicios usan transacciones SQLite y las pantallas consumen resultados síncronos/excepciones SQLite.
- IDs long locales; importes enteros en centésimas, precisión por moneda, saldos derivados; no introducir doubles para dinero.
- Pago de compra crea egreso y vínculo único en una transacción. Repartos y eliminación de registros relacionados también requieren consistencia.
- BiometricPrompt protege rutas financieras; osmdroid usa un atlas local. El manifiesto elimina INTERNET.
- PROJECT_STATE/ARCHITECTURE contienen descripciones históricas que no reflejan todos los merges. No tomar sus versiones antiguas del esquema como estado real.
- Hay evidencia histórica de pruebas; esta planificación no ejecutó build, pruebas Android ni Firebase. Persiste el riesgo histórico de firmas APK incompatibles.

## Alcance y límites

Criterio confirmado por el usuario: MVP académico de alumno, sin sobreingeniería. Usar las capacidades del SDK antes de diseñar infraestructura propia; conservar arquitectura simple y documentación breve. Prototipo y auditorías deben ser proporcionales al requisito y al riesgo real. Ver «Calidad MVP y proporcionalidad» en docs/agent/FIREBASE_PROTOCOL.md. No convertir los riesgos aquí enumerados en funcionalidades adicionales ni en una plataforma empresarial.

Incluye identidad, almacenamiento, modelo documental, adaptación asíncrona, comportamiento offline, migración de datos y regresión. Firebase no implica colaboración entre contactos: los contactos siguen siendo registros del propietario, no cuentas con acceso compartido. No añadir notificaciones, pagos reales, conversión monetaria ni backend propio sin nueva necesidad demostrada.

No utilizar SQLite como segunda persistencia financiera en la solución final. Durante la transición podrá leerse exclusivamente para importar instalaciones existentes. La política final sobre conservar ese importador se decide con la estrategia de distribución.

## Diseño propuesto

Flujo: UI Java/XML → servicios/dominio → repositorios asíncronos → SDK Firestore con caché persistente → Firestore remoto. Firebase Auth aporta el UID; biometría desbloquea la UI local. Mantener la separación simple existente, sin introducir frameworks arquitectónicos.

Propuesta de rutas: users/{uid}/cuentas/{id}, movimientos/{id}, contactos/{id}, compras/{id} y configuracion/principal. Detalles, reparto y deudas se agruparán por compra o en subcolecciones según el prototipo F1, evitando documentos ilimitados. Todas las referencias deben permanecer dentro del mismo UID. La ruta definitiva se fija antes de migrar módulos.

Adoptar IDs string generables offline. Los registros importados tendrán un mapeo persistido y determinista por origen/tabla/ID; dos instalaciones con id=1 no deben colisionar. Pagos y otras operaciones compuestas tendrán identidad estable para reintentos. Diseñar importes, moneda, fechas locales y fecha de confirmación remota explícitamente; serverTimestamp no sustituye la fecha funcional local.

Las claves foráneas, triggers y restricciones SQL no se trasladan automáticamente: distribuir las invariantes entre dominio, reglas Firestore y escrituras atómicas. Las reglas deben validar propietario, tipos, campos permitidos e inmutabilidad relevante. No basta con comprobar que el usuario inició sesión.

### Contrato offline propuesto y pruebas obligatorias

| Situación | Comportamiento esperado |
|---|---|
| Instalación nueva sin Internet | Informar que Google requiere conexión inicial; no simular una identidad autenticada |
| Primer acceso conectado | Autenticar, preparar datos necesarios y verificar que la caché permite el recorrido offline antes de indicarlo como listo |
| Sesión previamente iniciada, modo avión | Abrir datos locales tras biometría; registrar cambios de los módulos migrados |
| Cerrar y reabrir el proceso offline | Conservar lecturas y escrituras locales pendientes |
| Reconexión | Enviar cambios pendientes y distinguir confirmación remota de guardado local |
| Escritura rechazada por reglas/sesión | Mostrar rechazo y reconciliar la vista; no seguir presentándola como confirmada |
| Datos aún no descargados | Indicar indisponibilidad offline; no mostrar saldo cero ni lista vacía como información completa |
| Cierre/cambio de cuenta con pendientes | No descartar datos silenciosamente; resolver pendientes y evitar exposición a otra cuenta |

Firestore Android activa persistencia offline por defecto, pero su caché no garantiza que todo el historial esté disponible ni que jamás sea expulsado. F1 debe demostrar cobertura, consultas, política de caché y preparación de datos. Una recuperación tras borrar datos/desinstalar requiere Internet. Las transacciones Firestore requieren conexión; los lotes pueden encolarse offline. No usar confirmación de Task remota como única señal de guardado local.

## Roadmap por fases

### F0 — Requisitos, base y decisiones

Objetivo: disponer de una base coherente y reproducible.

- F0-T01: registrar actualización docente, ADR y trazabilidad del alcance nuevo.
- F0-T02: conciliar estado actual con main, inventariar repositorios, operaciones SQL, pruebas y flujos existentes; recuperar evidencia de PR #16.
- F0-T03: inventariar instalaciones/datos que se deben conservar, fijar estrategia de firma y distribución, identificar proyecto Firebase existente o responsable de crearlo.

Aceptación: requisitos nuevos documentados, baseline exacto y riesgos identificados; decidir conservación de datos antes de distribuir una actualización. No desinstalar una instalación con datos para resolver firmas.

### F1 — Prueba de viabilidad offline e integridad

Depende de F0. Objetivo: resolver riesgos antes de reemplazar módulos.

- F1-T01: prototipo mínimo con caché persistente, documento del usuario y pruebas de escritura offline/reinicio/reconexión, sin conectar datos reales.
- F1-T02: probar pago compuesto con ID estable, lotes, reglas y reintentos; documentar cómo impedir doble cargo desde dos dispositivos.
- F1-T03: diseñar restricciones de borrado/moneda/reparto ante operaciones concurrentes, cobertura de caché y recuperación de rechazos. Especificar conflictos por operación.
- F1-T04: fijar estructura documental y contratos asíncronos, incluidos loading, datos locales, pendientes, confirmación y error.

Aceptación: prueba reproducible sin pérdida ni doble egreso por reintento, evidencia de persistencia tras muerte de proceso y decisiones registradas para invariantes que antes garantizaba SQLite. Si una operación exige validación remota, explicitarlo y resolverlo frente a FB-R03 antes de avanzar. No declarar suficiente una política genérica de última escritura para operaciones financieras.

### F2 — Google Auth, sesión y reglas por usuario

Depende de F1. Objetivo: entrar con Google y acceder solo a datos propios.

- F2-T01: configurar app Android Firebase, Google provider, plugin Google Services, BoM y dependencias compatibles de Auth/Firestore/Credential Manager; huellas de firma y cliente web OAuth. Registrar versiones exactas al implementar.
- F2-T02: pantalla de entrada Google, cancelación/error/reintento y restauración de sesión; integrar gate biométrico sin bucles de navegación.
- F2-T03: reglas e índices versionados, pruebas del emulador para usuario A/B y acceso anónimo; cierre/cambio de cuenta con caché/listeners y pendientes.

Aceptación: primer acceso real con Google, reapertura offline y biometría verificadas; usuario B no ve datos de A ni desde UI ni por acceso remoto. El emulador Auth no sustituye la prueba real del proveedor Google.

### F3 — Cuentas, configuración y movimientos

Depende de F2. Objetivo: completar un primer recorrido financiero sobre Firestore.

- F3-T01: adaptar IDs, mapeos, configuración y cuentas, preservando validaciones del dominio.
- F3-T02: movimientos, consulta de historial y cálculo de saldo desde información completa; preparar caché necesaria.
- F3-T03: adaptar pantallas y mapa consumidor de movimientos a resultados asíncronos, sin SQL ni excepciones SQLite en estos flujos.

Aceptación: crear cuenta, registrar ingreso/egreso, calcular saldo y ver ubicación con y sin red; reapertura sin conexión conserva los datos. Monedas, precisión y saldo coinciden con casos conocidos. No computar saldo definitivo con historial parcial.

### F4 — Contactos, compras, reparto y deudas

Depende de F3. Objetivo: migrar las operaciones relacionadas preservando sus reglas.

- F4-T01: contactos, compras, detalle, planificación y operaciones permitidas de edición/borrado.
- F4-T02: pago de compra y vínculo con movimiento mediante la estrategia validada en F1.
- F4-T03: reparto equitativo/personalizado, participación propia y cierre de deudas; mismas reglas de centésimas y redondeo existentes.

Aceptación: recorridos offline y reconexión sin registros huérfanos, pérdida de detalle, alteración de saldos por cerrar deuda ni pagos duplicados. Probar interrupciones y conflictos definidos en F1.

### F5 — Datos existentes y retirada de SQLite

Depende de F4. Objetivo: pasar a la versión Firebase con una estrategia segura y comprobable.

- F5-T01: si se conservan instalaciones, snapshot coherente de las nueve tablas, importación por fases con marcador de finalización y mapeo estable; vinculación explícita al UID correcto.
- F5-T02: validar conteos, relaciones, importes, saldos y reejecución tras interrupción; comprobar confirmación remota antes de considerar completada la migración. No borrar el original por haber encolado escrituras.
- F5-T03: retirar helper/repositorios/fixtures SQLite de la operación normal y adaptar documentación/tests. Si se decide comenzar sin datos históricos, registrar esa decisión y conservar los respaldos acordados antes de retirar el importador.

Aceptación: datos importados exactamente una vez, respaldo recuperable y actualización instalada probada con firma compatible; o decisión explícita de instalación limpia documentada. App final funciona mediante Firestore sin dependencia funcional de SQLite.

### F6 — Regresión, revisión independiente y entrega

Depende de F5. Objetivo: demostrar el nuevo alcance completo.

- F6-T01: build, unitarias de dominio, pruebas de reglas y repositorios, recorridos Android API24/API36 y Google real en dispositivo compatible.
- F6-T02: prueba de aceptación: acceder conectado, preparar datos, activar modo avión, usar todos los módulos, matar/reabrir proceso, recuperar red y contrastar datos remotos. Incluir token no renovable, rechazos, cambio de cuenta y caché incompleta.
- F6-T03: revisión independiente del incremento y correcciones; actualizar arquitectura, esquema Firestore, reglas de negocio afectadas, guía Firebase/build, manual offline, trazabilidad y PROJECT_STATE.
- F6-T04: APK con firma estable, guion docente y paquete de entrega. Sincronizar documentos canónicos a Library al cerrar el hito.

Aceptación: requisitos FB-R01–05 demostrados, cero defectos bloqueantes conocidos, evidencia asociada al SHA final y veredicto independiente favorable. No extender auditorías históricas al nuevo backend.

## Validación y contexto para tareas

Cada Task Brief debe incluir objetivo, alcance, criterios anteriores aplicables, contexto mínimo, SHA base, pruebas, rama y handoff conforme a docs/agent/TASK_TEMPLATE.md. Emitir briefs por tarea al abrir su fase; este roadmap no autoriza implementar todas las fases como una única tarea.

Build previsto: ./gradlew :app:assembleDebug :app:assembleDebugAndroidTest :app:testDebugUnitTest (en Windows, gradlew.bat). Firebase Emulator Suite: añadir comandos reproducibles para pruebas de reglas al definir el proyecto. Pruebas Android: adaptar los runners actuales; no presumir que connectedAndroidTest cubre todos los runners personalizados. Registrar dispositivo, firma, conectividad, SHA, resultado y pendientes.

## Decisiones y descubrimientos

- Confirmado por el usuario: Firebase sustituye SQLite, Google Auth obligatorio y operación offline.
- Propuesta técnica: Cloud Firestore y su caché persistente, Firebase Auth con Credential Manager; biometría complementaria.
- Pendiente F0: necesidad de conservar datos de instalaciones existentes y configuración del proyecto Firebase.
- Pendiente F1: contrato offline definitivo, concurrencia entre dispositivos, atomicidad de pago, borrados e integridad referencial. No asumir acceso compartido entre contactos.
- Internet inicial para Google y descarga de datos es una limitación explícita del diseño propuesto. Si el docente exige primera instalación completamente offline, debe revisarse el requisito de Google obligatorio antes de implementar.

## Actualización F0-T02 — 2026-11-16

Inventario documental preparado desde integración `d7657d1cd798e3f86f9ac9fbe75a80af1fd28c4b`: [baseline y matriz](../../design/FIREBASE_BASELINE.md), [handoff](../../handoffs/FB-F0-T02-baseline-inventario.md). PR #16 confirmado integrado en c28a27b; esquema v7/APK declarado 1.1.1. CI histórico final #16: API24 success/API36 failure en relanzamiento, sin repetición en esta tarea. Las garantías y lagunas de CI están identificadas; su corrección exige otra tarea. F0-T03 queda como siguiente unidad recomendada; F0 y viabilidad F1 siguen pendientes. No hay cambios funcionales, servicios remotos ni nuevas pruebas Android. El resultado de la sesión de arranque inferior se conserva como antecedente, no estado actual de publicación.

## Progreso

- [x] Inspección de main y fuentes pertinentes.
- [x] Actualización docente registrada y roadmap por fases preparado.
- [ ] F0 completada y brief F1 listo.
- [ ] F1 viabilidad demostrada.
- [ ] F2 identidad y aislamiento comprobados.
- [ ] F3 cuentas/movimientos migrados.
- [ ] F4 compras/deudas migradas.
- [ ] F5 datos existentes resueltos y SQLite retirado.
- [ ] F6 revisión, aceptación y entrega.

## Resultado histórico de la sesión de arranque

Plan local preparado; no implementación, no configuración remota Firebase, no push/merge, no nueva evidencia de build o aceptación. La próxima tarea es F0-T02; completar F0 antes del prototipo. Mantener este documento vivo y moverlo a completed solo al cumplir F6.

## Referencias oficiales consultadas el 2026-11-16

- Google Auth Android: https://firebase.google.com/docs/auth/android/google-signin
- Caché y funcionamiento offline: https://firebase.google.com/docs/firestore/manage-data/enable-offline
- Transacciones y lotes: https://firebase.google.com/docs/firestore/manage-data/transactions
