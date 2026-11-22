# FB-F0-T03 — Decisiones y objetivo de entrega

Fecha: 2026-11-22. Rol: Orquestador. Estado: decisiones aceptadas localmente para iniciar el prototipo; publicación pendiente.
Base: d7657d1cd798e3f86f9ac9fbe75a80af1fd28c4b.
Rama: task/FB-F0-T03-datos-firma-firebase. PR destino: feat/firebase-migration.

## Objetivo autorizado

El usuario solicita terminar LocalCash de forma autónoma y dejar las intervenciones imprescindibles para el final. Resultado esperado: MVP universitario Java/XML con Firebase en lugar de SQLite, Google Auth, biometría y mapas vigentes, funcionamiento offline tras acceso y preparación inicial conectados. Conservar pantallas, navegación, campos, parámetros, formatos, monedas, precisión y reglas de cálculo. Ajustar UI solo cuando Auth o estados asíncronos lo necesiten. No rediseñar ni añadir funcionalidades, frameworks o motores propios de sincronización.

La autonomía permite preparar y ejecutar trabajo autorizado; no sustituye auditoría independiente ni acredita pruebas no realizadas. La ejecución depende de los accesos y entornos disponibles, no implica un proceso de fondo permanente.

## Decisiones confirmadas y evidencia

- El usuario no requiere importar historial SQLite. Usar datos sintéticos nuevos; preservar la instalación anterior y sus datos. No borrar ni desinstalar para resolver firma.
- Conservar parámetros significa conservar estructura, reglas y formatos; no se ha solicitado trasladar valores de una base personal.
- Proyecto de desarrollo: localcash-f6298. Cuenta responsable: usuario que lo creó y mantiene acceso a Firebase; no registrar correo personal.
- Android registrado: com.aik1a.localcash. JSON cliente actualizado proporcionado por el usuario y comprobado: clientes OAuth Android y web presentes y SHA coincidente. No se publica el JSON ni claves en este documento.
- SHA-1 proporcionada por usuario tras solicitar signingReport debug: DC:36:46:26:36:63:FA:20:0D:E1:F0:BD:AB:12:F5:86:05:6B:F3:35. Firebase console confirma su registro. No demuestra firma de APK instalado ni custodia de keystore.
- Usuario comunicó haber guardado Google como proveedor. La configuración OAuth existe; login Google real sigue NOT TESTED.
- Consola comprobada: plan Spark sin costo; no se autoriza facturación.
- Primera conexión y preparación pertenecen al contrato propuesto vigente; no se ha recibido una exigencia distinta del usuario.

## Pendientes y estrategia mínima

1. T02: usuario comunica PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE para 126b30b3467d7737ccd8f48361a798ff291e20da hacia integración. Informe independiente y detalle editorial no disponibles en esta copia ni publicados en PR #20 al comprobarlo. No recrear informe, no repetir inventario, no fusionar sobre un SHA distinto.
2. Distribución: ensayos en emulador descartable con datos sintéticos. No actualizar teléfono con datos sin comprobar firma. Definir firma estable del APK de entrega y comprobar distribución antes de F6. No se inventa disponibilidad de una clave histórica.
3. F0 no se declara cerrada mientras falte tramitar T02 y dejar resuelta la distribución aplicable. No se activa F1 por este documento.

## Próxima tarea autorizada: FB-F1-T01 (READY local)

Rol Codificador. Abrir rama desde el HEAD aceptado de integración después del cierre de F0; destino feat/firebase-migration. Leer router, estado, AGENTS, rol CODER, protocolo Firebase, ADR-003, ExecPlan FB-01 y baseline T02 aceptada.

Objetivo: prototipo mínimo aislado con un documento sintético, caché persistente Firestore, escritura y consulta offline, muerte de proceso y reconexión. Utilizar Emulator Suite para esta viabilidad; Google real corresponde a F2. No migrar finanzas ni reemplazar módulos todavía.

Archivos previstos: Gradle raíz/app solo dependencias necesarias, configuración mínima de emuladores/reglas y prueba Android del prototipo. INTERNET solo en el alcance requerido. No modificar sources/, dominio financiero ni layouts existentes; no añadir backend propio o doble escritura SQLite/Firestore.

Aceptación: build e instrumentación compilables; crear conectado, preparar dato, cortar red, leer/escribir, matar/reabrir proceso, recuperar dato pendiente, reconectar y comprobar remoto sin duplicados. Separar guardado local de confirmación remota y comprobar rechazo visible. Datos solo sintéticos; documentar comandos, versiones, entorno, SHA y resultados. Si falta Android/emulador, declarar BLOCKED/NOT TESTED sin sustituir el recorrido por build.

Auditor independiente revisa el SHA exacto y evidencia; autor no se autoaudita. Pago atómico, dos clientes, conflictos, esquema definitivo y Google real quedan en tareas F1/F2 previstas. No considerar la caché garantía de historial completo.

## Validación y handoff de esta intervención

Se inspeccionaron GitHub PR #20, ramas y consola Firebase, se clonó el repositorio y se comprobó el JSON sin imprimir claves. El conector rechazó creación de rama con 403; esto no acredita un fallo de todas las vías de publicación. No se modificó app, Firebase remoto, tests ni sources/. Build, login real y offline: NOT TESTED.

Este brief conserva decisiones y continuidad sin duplicar inventario. Validar diff y enlaces locales antes de publicar. Próxima acción: tramitar evidencia T02, registrar pendientes de distribución y cerrar F0 únicamente cuando corresponda; después activar el brief F1-T01. Devolver al usuario al final solo bloqueos que requieran su acción.

## Actualización MVP — 2026-11-22

Informe original T02 recibido, preservado y aceptado para su SHA; integración local, no merge remoto. Usuario pide avanzar con MVP y diferir bloqueos externos al final. F0 suficiente para viabilidad: datos nuevos sintéticos en emulador limpio, instalación anterior intacta, proyecto Firebase identificado. Firma de entrega se valida antes de distribuir, no condiciona prototipo. Las frases de pendientes anteriores son históricas; READY local F1-T01 sustituye DRAFT sin declarar viabilidad demostrada.
