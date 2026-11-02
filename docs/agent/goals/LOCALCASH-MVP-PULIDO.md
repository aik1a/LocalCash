# Goal operativo — LOCALCASH-MVP-PULIDO

## Instrucción de entrada para el agente

Completa el pulido total de LocalCash siguiendo este goal, el ExecPlan y la cola persistente. Conserva Java/XML/SQLite y toda la funcionalidad sana. No reapliques PR14 ni conviertas el alcance completo en un solo parche. No detengas el trabajo para pedir confirmación por pasos rutinarios ya autorizados; continúa dentro del rol y fase asignados hasta verificarlos o encontrar un bloqueo real.

**Este goal está PREPARADO; no está ejecutándose por existir este documento.** El encargo actual prepara documentación. La ejecución funcional comienza cuando el usuario invoque al agente implementador/coordinador con este goal. No hay tarea programada en segundo plano.

## Documentos de entrada

1. PROJECT_ROUTER.md, AGENTS.md, PROJECT_STATE.md.
2. docs/exec-plans/active/MVP-PULIDO-COMPLETO.md.
3. docs/design/MVP-PULIDO-CONTRATOS.md.
4. docs/agent/goals/LOCALCASH-MVP-PULIDO.queue.json.
5. Únicamente el sprint, Task Brief y Context Pack de la fase actual.
6. Rol asignado en docs/agent/roles/. Codificador y Auditor de la misma iteración deben ser independientes.

## Selección automática del siguiente paso

- Si hay una microtarea IN_PROGRESS, reanudarla tras contrastar archivos, diff y evidencia; no comenzar otra para evitar resolverla.
- Si hay una fase CHANGES_REQUESTED, corregir solo hallazgos obligatorios y regresiones afectadas.
- Si la fase está READY/IN_PROGRESS, tomar la primera microtarea PENDING cuyas dependencias estén VERIFIED y cuyo Task Brief permita ejecutarla.
- Dependencia entre fases: la anterior debe estar CLOSED por el Orquestador, no solo con código escrito.
- Si toda la fase está VERIFIED, preparar handoff y solicitar revisión al rol Auditor. No implementar la siguiente fase antes de activación.
- Solo Orquestador modifica estados formales de apertura/cierre y cursor entre fases; Codificador puede registrar su progreso micro y reportar READY_FOR_AUDIT en handoff.
- Auditor registra informe y gate; Orquestador integra lo aceptado y activa la siguiente fase sin otra confirmación del usuario por rutina.
- Continuar hasta goal completo o bloqueo esencial. No saltar una dependencia para aparentar avance.

## Ciclo de cada microtarea

EXPLORE → PLAN BREVE → IMPLEMENTAR → COMPROBAR → CORREGIR SI HACE FALTA → REGISTRAR.

1. Leer el criterio y el código afectado, no toda la documentación.
2. Cambiar lo mínimo; preservar datos, decisiones y trabajo ajeno.
3. Comprobar el comportamiento indicado. No escribir tests que solo copien la implementación.
4. Registrar evidencia estructurada, incluyendo resultado no ejecutado cuando corresponda.
5. Marcar VERIFIED solo si se demostró el criterio completo; si falta evidencia, mantener IN_PROGRESS o BLOCKED.
6. Actualizar current_microtask al siguiente candidato dentro de la misma fase y last_verified_microtask.
7. Tras cambios nuevos o fallos, repetir pruebas pertinentes. No repetir pruebas amplias ya aprobadas sin cambios o preocupación nueva.

## Ramas, commits y PR

- Una rama por fase formal, indicada en su Task Brief; no una rama/PR por micro.
- Commits de trabajo pequeños y verificables en esa rama, sin secretos, APK/BD personales ni reescritura de historial.
- No force-push, rollback de datos, desinstalación de instalación con datos, actualización general de dependencias ni merge a main desde Codificador.
- El Orquestador puede preparar PR en borrador y formalizar merge solo con gate independiente favorable y autorización aplicable.
- Si main se mueve, inspeccionar el cambio y preservar código aceptado; no restaurar baseline antiguo a ciegas.

## Registro mínimo por microtarea

Campos de la cola: status, evidence, implementation_sha, blocker. En evidence registrar objetos con comando/escenario, resultado, código de salida cuando exista, entorno/API, SHA probado y ruta relativa de log o referencia CI. Nunca inventar un SHA o poner PASS donde no se ejecutó.

Estados micro: PENDING, IN_PROGRESS, VERIFIED, BLOCKED. No equivalen a los estados formales de tarea DRAFT/READY/IN_PROGRESS/READY_FOR_AUDIT/CHANGES_REQUESTED/APPROVED/CLOSED/BLOCKED.

## Checkpoint al pausar o cambiar de contexto

Actualizar docs/agent/goals/LOCALCASH-MVP-PULIDO.checkpoint.md con fase, microtarea, rama/SHA reales, archivos modificados, último resultado, bloqueo si existe y siguiente acción exacta. Crear/actualizar el handoff de fase. Otro agente debe poder continuar desde esos archivos sin buscar chats.

## Qué automatizar y qué no falsear

Automatizar selección de la siguiente microtarea, comandos de build/tests, chequeos de integridad, registros y preparación de handoff/PR. Respetar gates de rol. Si el entorno no permite teléfono físico o revisión independiente, registrar el pendiente y entregar el handoff; no declararlo ejecutado ni sustituirlo por una promesa.

Si se repite un fallo sin cambiar nada, investigar la causa en lugar de ejecutar el mismo comando indefinidamente. Reparar fallos relacionados dentro de la fase. Escalar solo decisiones que cambien alcance/requisitos o acciones externas no autorizadas.

## Criterio del goal completo

- 7 sprints CLOSED y 23 fases CLOSED con gates válidos.
- 91 microtareas VERIFIED con evidencia correspondiente.
- Alcance completo funcionando: CRUD, saldo, compras/pagos/undo, deudas, swipe/menú, UI y privacidad.
- Build/regresión del SHA final, recorrido real y artefacto/guía correctos.
- Cero BLOCKER/MAJOR que impidan requisitos.
- Teléfono/upgrade/handoffs pendientes no convertidos artificialmente en DONE.
- Informe final breve con resultado, pruebas, APK y límites; PROJECT_STATE y trazabilidad actualizados.

## Prompt de arranque: Codificador

Actúa exclusivamente como Codificador de LocalCash. Lee este goal y la cola; ejecuta la fase READY asignada por el Orquestador empezando en current_microtask. Completa sus microtareas por orden, comprueba cada criterio y registra evidencia/checkpoint. No cambies de rol, no te autoaudites, no fusiones a main. Termina con handoff READY_FOR_AUDIT o bloqueo demostrado. No implementes código hasta que el usuario haya invocado esta ejecución.

## Prompt de arranque: Orquestador

Actúa exclusivamente como Orquestador. Contrasta repo/cola/checkpoint, activa la fase correspondiente al scope completo y asigna su Task Brief al Codificador. Coordina auditor independiente y cierre según WORKFLOW/TASK_LIFECYCLE; activa el siguiente paso tras gate sin pedir al usuario confirmación por cada microtarea. No implementes ni certifiques el código tú mismo.

## Prompt de arranque: Auditor

Actúa exclusivamente como Auditor independiente. Lee el Task Brief, diff/SHA, handoff y evidencia de la fase asignada; verifica criterios y regresiones concretas, aplica AUDIT_MATRIX y emite veredicto/gate. No corrijas el mismo código ni uses la aprobación histórica de S00 como gate de este incremento. En cierre de sprint, revisa incremento integrado y emite gate de avance.
