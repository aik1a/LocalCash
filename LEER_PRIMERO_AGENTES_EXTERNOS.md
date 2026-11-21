# LocalCash — Continuidad para una cuenta sin contexto

Preparado el 21 de noviembre de 2026. Esta rama contiene el repositorio completo y la documentación de continuidad. No necesita acceso al chat original.

## Estado remoto verificado para el relevo

Rama de continuidad: handoff/external-agents, creada desde feat/firebase-migration en d7657d1cd798e3f86f9ac9fbe75a80af1fd28c4b. Es una copia de entrada; las tareas nuevas mantienen PR hacia feat/firebase-migration, no hacia esta rama.

La tarea FB-F0-T02 YA tiene entrega: PR #20 abierto, https://github.com/aik1a/LocalCash/pull/20, head 126b30b3467d7737ccd8f48361a798ff291e20da. No repetir el inventario ni darlo por aceptado. Próxima acción recomendada: revisión independiente del PR #20 contra su brief. Consultar su rama task/FB-F0-T02-baseline-inventario para FIREBASE_BASELINE.md y handoff entregados. F0 sigue abierta; comprobar comentarios e informes antes de iniciar una nueva revisión. No se aprueba ni fusiona ese PR por publicar este relevo.

Al iniciar, comprobar nuevamente estos datos: son una fotografía, no una garantía de que el remoto no haya cambiado.

## Instrucción de arranque

Actúa como Orquestador de LocalCash. Antes de implementar, lee este archivo, AGENTS.md, PROJECT_ROUTER.md, PROJECT_STATE.md, docs/agent/FIREBASE_PROTOCOL.md y docs/exec-plans/active/FB-01-firebase-google-offline.md. Contrasta el estado remoto de aik1a/LocalCash, rama feat/firebase-migration, sus PR y auditorías. No presupongas que las tareas siguen pendientes solo porque esta copia las describe así. No sobrescribas trabajo ajeno. Después identifica la primera tarea no completada y prepara o ejecuta su brief según el rol autorizado.

## Proyecto y requisitos confirmados

LocalCash es un MVP académico Android de alumno. Prioridad: solución simple, legible, demostrable y funcional; evitar sobreingeniería. Java/XML, Gradle Groovy, minSdk 24. El docente cambió el encargo según confirmación expresa del usuario: reemplazar SQLite por Firebase, iniciar sesión con Google y mantener funcionamiento offline. El PDF antiguo exige SQLite: es antecedente histórico, no motivo para rechazar la actualización. El usuario no entregó un nuevo PDF docente.

Propuesta: Cloud Firestore con caché persistente y Firebase Authentication/Google mediante Credential Manager. Mantener biometría y mapas locales osmdroid. El primer acceso Google y la preparación de datos necesitan conexión; el uso posterior debe probarse offline, incluyendo reinicio y reconexión. No prometer que la caché contiene todo el historial sin comprobarlo.

## Estado conocido y límites de esta copia

Último commit local observado: d7657d1; rama feat/firebase-migration. Base funcional SQLite: c28a27b87aeeb092a936b8ba6b05b83404ac1c13, esquema v7, APK declarado 1.1.1/versionCode 3. PR #19 documental integrado; etiqueta baseline/sqlite-v7-20261116. El remoto se verificó el 21 de noviembre mediante el conector GitHub: integración sigue en d7657d1 y PR #20 está abierto. El inventario ya fue entregado y requiere revisión; no consta aprobación en esta comprobación. Este paquete no acredita implementación Firebase ni pruebas nuevas. La primera tarea preparada era FB-F0-T02, inventario documental. Revisar antes de repetirla.

Existe app funcional: cuentas, movimientos, saldos, compras, contactos, reparto/deudas, biometría y mapas offline. Nueve tablas SQLite. Hay servicios con transacciones, IDs long y dinero entero en centésimas. No cambiar precisión, reglas de pago ni redondeo. El manifiesto actual elimina INTERNET. Varias secciones históricas de estado/arquitectura están desactualizadas. Firmas APK históricas incompatibles: no desinstalar una app con datos para actualizar.

## Plan completo y cumplimiento

La especificación detallada y los criterios por tarea están en docs/exec-plans/active/FB-01-firebase-google-offline.md; leerlo completo para planificar.

F0: registrar alcance, inventariar código/garantías/pruebas y resolver proyecto Firebase, datos a conservar y firmas. Salida: base verificable y decisiones pendientes explícitas.
F1: prototipo de persistencia offline, reinicio/reconexión, pago atómico e idempotente y conflictos concretos. Salida: viabilidad demostrada antes de migrar módulos.
F2: Google Auth, sesión, biometría, aislamiento por UID y reglas. Salida: Google real y pruebas usuario A/B; no basta emulador Auth.
F3: configuración, cuentas, movimientos, saldos y lectores del mapa. Salida: recorrido online/offline con historial suficiente y dinero correcto.
F4: contactos, compras, detalle, pago, reparto y deudas. Salida: integridad sin huérfanos ni doble cargo; cerrar deuda no altera saldos fuera de las reglas existentes.
F5: importación de datos existentes, validación de relaciones/importes y retirada SQLite. Salida: importación reanudable sin duplicados y actualización compatible, o decisión explícita de instalación limpia. No borrar original por haber encolado escrituras.
F6: regresión integrada, auditoría independiente, documentación y entrega. Salida: evidencia FB-R01–05 sobre SHA final y cero defectos bloqueantes.

## Roles, permisos y ramas

Orquestador: planificación, briefs, decisiones, estado y cierre administrativo. No inventa requisitos ni se atribuye auditoría independiente.
Codificador: solo tarea asignada, código/pruebas/documentación pertinente; no amplía alcance ni fusiona o cierra su propia tarea.
Auditor: inspecciona código/evidencia y escribe informe; no corrige implementación ni fusiona PR. Un chat que implementó no se vuelve independiente por cambiar de nombre de rol.

main conserva aplicación SQLite mientras se migra. Integración: feat/firebase-migration. Una rama task/FB-Fx-Tyy-descripcion por tarea, con PR a integración. main recibe el incremento completo tras gate F6. No force-push ni mover etiqueta base. Fuentes sources/ solo lectura; no datos personales, secretos, borrados ni infraestructura de producción implícita.

No crear motores propios de sincronización, frameworks genéricos, capas empresariales, Room, Compose, backend propio ni funcionalidades colaborativas por iniciativa. Usar SDK oficiales y estructura existente. Auditoría y documentación proporcionales al MVP, no por cada commit.

## Revisión y aceptación

Cada brief: objetivo, alcance, archivos previstos, exclusiones, criterios, base/destino, pruebas y handoff. Cada auditoría: PR, base/head SHA exactos, criterios contrastados, fuentes inspeccionadas, evidencia real, hallazgos y veredicto. PASS/PASS WITH OBSERVATIONS permiten cierre solo si todos los criterios obligatorios pasan. FAIL devuelve correcciones; BLOCKED reconoce evidencia/decisión/entorno faltante. Indicar APPROVED_FOR_MERGE o NOT_APPROVED_FOR_MERGE con SHA y destino. Un cambio funcional posterior requiere reevaluación.

BLOCKER: pérdida de datos, acceso entre usuarios, doble cargo, corrupción monetaria o secreto publicado. MAJOR: requisito obligatorio incumplido, fallo offline/reinicio o evidencia crítica ausente. Ambos impiden merge. MINOR/observaciones de estilo no deben bloquear cuando se cumple aceptación; registrar seguimiento acotado.

Pruebas proporcionales: documentación requiere diff/enlaces/coherencia; no build Android. Código requiere build y pruebas pertinentes. Reglas requieren emulador A/B/anónimo y datos inválidos; Auth requiere Google real; finanzas requieren atomicidad/reintentos/conflictos concretos. Recorrido final: iniciar conectado, preparar datos, modo avión, usar módulos, matar/reabrir proceso, reconectar y comparar remoto. Registrar NOT TESTED cuando corresponda; build verde no equivale a flujo manual aprobado.

## Primera auditoría: FB-F0-T02

Cuando exista PR del inventario, Auditor independiente lee brief, protocolo y handoff; inspecciona diff completo y código citado. Contrasta nueve tablas, operaciones, triggers, IDs, dinero, pagos, borrados, runners y CI. No audita exhaustivamente toda la app ni exige Firebase funcionando. No modifica código ni documentos del implementador. Solo escribe docs/audits/FB-01/FB-F0-T02-audit.md y evidencia propia. Evalúa exactitud y utilidad del inventario; aprobación solo de esta tarea, no cierre F0 ni aprobación Firebase.

## Documentos incluidos

En esta rama, código, pruebas, workflows y documentación están en sus rutas normales del repositorio. El ZIP local preparado previamente usa un directorio repositorio/ y representa una fotografía anterior a la comprobación del PR #20. Para trabajar con Git, clonar el remoto y contrastar versiones antes de aplicar documentos. No cargar todo por defecto: router y contexto de la tarea. El PDF docente no está incluido en el repositorio: obtenerlo del usuario solo si hace falta contrastar el encargo histórico.

Antes de terminar cada tarea dejar handoff con lo hecho, pruebas realmente ejecutadas, commit/PR y siguiente acción. No depender de memoria del chat ni afirmar publicación o aceptación sin verificarla.

