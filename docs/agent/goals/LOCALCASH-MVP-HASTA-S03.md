# Goal de Work — LocalCash hasta MVP-S03

**Estado:** PREPARED / NOT_STARTED. **Preparación:** 02/11/2026.  
**Repositorio:** aik1a/LocalCash. **Rama de entrada e integración:** task/mvp-s01-s03-goal-20261102.  
**Alcance:** MVP-S01, MVP-S02 y MVP-S03 completos: **3 sprints, 10 fases, 37 microtareas**.  
**Parada obligatoria:** al cerrar MVP-S03-T03 / verificar MVP-S03-T03-M03 y completar gates de cierre de MVP-S03. **No empezar MVP-S04.**

Este encargo prepara el prompt para abrir OTRO CHAT de Work. No inicia ejecución en el chat de planificación. Al pegar el prompt de arranque en ese otro chat, queda invocada la implementación de este alcance.

## 1. Resultado esperado

- MVP-S01: baseline comprobado, reglas documentadas, protección de saldo y CI pertinente.
- MVP-S02: edición/eliminación segura de cuentas, contactos y movimientos; saldos correctos.
- MVP-S03: edición/eliminación de compras y detalles, filtros y formularios claros; relaciones protegidas.

El pago desde cuenta, transición planificada→realizada con cargo, devolución, deudas PAGADA, swipe, migraciones de pago/deuda y rediseño general pertenecen a MVP-S04–S07 y **no se implementan en esta ejecución**. Se conserva lo que ya funciona. No declarar el goal completo de siete sprints cerrado.

## 2. Fuente y rama correctas

- La rama parte de 3254707ced46afa983bdd6831711da3621587664, que contiene la planificación del PR15. El main consultado al preparar el encargo continúa en 9b8a651d66982eea3a41e1979342468fe4cad176; el PR15 sigue sin fusionar.
- La nueva rama incorpora el plan completo, por lo que no depende de que el usuario fusione PR15 para poder leer instrucciones.
- Leer el código vigente antes de editar. Si hay avances ajenos, preservarlos y reconciliar el estado; nunca restaurar el baseline antiguo a ciegas.
- No trabajar en main ni en la rama documental original. La rama de integración es task/mvp-s01-s03-goal-20261102. Crear desde ella ramas por fase conforme a los Task Briefs si se requiere separación; este encargo precisa la rama base, no elimina los gates.
- No abrir dos agentes escritores sobre el mismo checkout/rama/archivos. Usar secuencia y worktrees separados cuando haya roles distintos.

## 3. Orden de lectura mínimo

1. PROJECT_ROUTER.md, AGENTS.md, PROJECT_STATE.md.
2. Este goal y docs/agent/goals/LOCALCASH-MVP-HASTA-S03.queue.json.
3. Rol asignado: ORCHESTRATOR.md, CODER.md o AUDITOR.md bajo docs/agent/roles/.
4. ExecPlan docs/exec-plans/active/MVP-PULIDO-COMPLETO.md y contratos docs/design/MVP-PULIDO-CONTRATOS.md, limitados a estas tres etapas.
5. Sprint y Task Brief de la fase actual; solo sus componentes y reglas relevantes.

**Precedencia del alcance:** esta instrucción posterior restringe la ejecución del plan completo a S01–S03. Cuando un documento heredado mencione la cola de 91 microtareas, usar para esta ejecución la cola LOCALCASH-MVP-HASTA-S03. Las reglas técnicas/roles se conservan; el límite nuevo no autoriza saltarlas.

## 4. Coordinación para trabajar sin consultas rutinarias

El chat Work que coordine el goal actúa como Orquestador. Asigna una fase READY al Codificador y entrega su Task Brief, rama/base y microtarea inicial. El Codificador ejecuta esa unidad, registra evidencias y devuelve handoff. Un Auditor distinto revisa el diff/commit exacto. El Orquestador integra lo aprobado y activa la siguiente fase. La tarea MVP-S01-T02 corresponde al Orquestador, no debe obligarse al Codificador a cambiar de rol.

Usar las herramientas de agentes/delegación cuando estén disponibles y autorizadas en el chat de ejecución. No fabricar agentes, conversaciones ni auditorías. Si no existe capacidad para el rol necesario, dejar un handoff ejecutable y declarar qué gate falta; no autoproclamarse Auditor independiente ni seguir como si hubiera aprobado.

No preguntar al usuario por cada microtarea, commit de trabajo, prueba o corrección reversible dentro del alcance. Pedir intervención solo cuando una decisión cambie el alcance/requisitos, falte información esencial imposible de obtener o exista una acción destructiva/no autorizada.

## 5. Secuencia de fases

| Fase | Resultado | Rol | Microtareas |
|---|---|---|---:|
| MVP-S01-T01 | Baseline y entorno | Codificador | 3 |
| MVP-S01-T02 | Reglas y contratos del incremento | Orquestador | 4 |
| MVP-S01-T03 | Saldo suficiente y validación central | Codificador | 4 |
| MVP-S01-T04 | Validación continua del nuevo ciclo | Codificador | 2 |
| MVP-S02-T01 | CRUD de cuentas | Codificador | 4 |
| MVP-S02-T02 | CRUD de contactos | Codificador | 4 |
| MVP-S02-T03 | CRUD de movimientos manuales | Codificador | 5 |
| MVP-S03-T01 | Edición de compras y detalles | Codificador | 4 |
| MVP-S03-T02 | Eliminación segura de compras | Codificador | 4 |
| MVP-S03-T03 | Formularios y lista de compras | Codificador | 3 |

Cada fase depende del cierre de la anterior. Dentro de fase, cada microtarea depende de la verificación de la anterior. No crear una rama ni hacer auditoría integral por cada microtarea. Respetar la definición de tareas y gates existente.

## 6. Reglas de implementación

- Java/XML/Groovy/SQLiteOpenHelper/osmdroid/BiometricPrompt sin cambios de stack.
- Conservar divisas y reparto exacto; sin redondeo silencioso ni conversión monetaria.
- Saldo: validar el resultado de registrar/editar/eliminar dentro de la operación. No romper lectura de negativos históricos; permitir recuperación sin empeorarlos.
- CRUD por ID estable, filas afectadas comprobadas, referencias y rollback protegidos; no SQL ni cálculo financiero en Activities.
- Compras: edición no puede cambiar estado para saltarse el pago futuro. No añadir un botón Comprar que no efectúe el flujo acordado.
- No cobrar compras antiguas, no añadir pagos/vínculos/tablas de S04 y no introducir v6/v7 anticipadas.
- En S02, la guardia de movimientos vinculados es un contrato compatible con v5; no una excusa para implementar el módulo de pagos.
- En S01-T02 se pueden documentar contratos futuros existentes, pero no implementarlos ni marcar sus requisitos completados.
- Interfaz de CRUD simple: acciones visibles, precarga, confirmación de borrado y errores humanos. Swipe y pulido visual general quedan fuera de esta ejecución.
- No reaplicar PR14, borrar datos del usuario, desinstalar para resolver firma, versionar secretos ni alterar trabajo ajeno.

## 7. Evidencia y reanudación

Ciclo: inspeccionar → plan breve → implementar → comprobar → corregir → registrar → siguiente microtarea.

- Estados micro: PENDING, IN_PROGRESS, VERIFIED, BLOCKED. VERIFIED exige comprobar todo su criterio; no equivale a aceptación externa.
- Estados formales de tarea: los de TASK_LIFECYCLE.md, con su autoridad por rol.
- Registrar evidencia por micro: comando o escenario, resultado, exit code si existe, entorno/API, SHA probado y ruta de log/CI.
- Codificador registra su progreso micro y prepara READY_FOR_AUDIT; no cierra sprints ni autoaprueba merges.
- Si se interrumpe, actualizar LOCALCASH-MVP-HASTA-S03.checkpoint.md y handoff con rama/SHA reales, archivos tocados, última prueba, bloqueo y siguiente ID.
- No repetir una comprobación amplia ya aprobada sin cambios relevantes. No ejecutar el mismo comando fallido indefinidamente sin investigar la causa.

## 8. Validación mínima del alcance

- Dominio y precisión monetaria existentes más casos nuevos de saldo y CRUD.
- SQLite: referencias, actualización/borrado, rechazo sin cambio, rollback y persistencia.
- Android build: wrapper del proyecto; JDK/toolchain según guía vigente. La falta de SDK no prueba fallo del código.
- CI del SHA exacto y cobertura declarada por API; no confundir aserciones host con recorridos UI.
- Flujos: editar gasto 300→400 aplica diferencia 100; borrar ingreso que produce déficit se rechaza; cuenta vacía se elimina; contacto usado se protege; compra y detalles se actualizan juntos.
- Reapertura: datos nuevos/corregidos persisten, registros eliminados no vuelven.
- Regresión de biometría, ubicación opcional y mapas existentes, sin modificar su alcance.
- Declarar por separado host, emulador, recorrido humano y teléfono físico. Si falta una prueba esencial, registrar BLOCKED o pendiente; no inventar PASS.

## 9. Condición de parada y salida

Cuando MVP-S03 esté cerrado con gates válidos:

1. No activar S04, aunque la cola completa tenga pendientes.
2. Dejar el goal limitado completo y el goal de siete sprints parcialmente ejecutado.
3. Entregar resumen de cambios, PR/commits, pruebas, limitaciones y handoff para S04 sin implementarlo.
4. No fusionar a main por iniciativa del Codificador. El Orquestador integra fases auditadas en la rama del goal; merge final a main queda sujeto a gates y autorización de integración aplicable.

Si se bloquea antes, informar el ID exacto y evidencia, conservar avance y dejar la próxima acción concreta. No declarar S03 terminado ni abrir el siguiente sprint para evitar el bloqueo.

## 10. Prompts de entrada

- Para pegar en el otro chat Work y coordinar el goal: docs/agent/prompts/WORK-MVP-HASTA-S03.md.
- Para el agente Codificador de cada fase: docs/agent/prompts/CODER-MVP-HASTA-S03.md.

**Estado inicial de este paquete:** documentación preparada; cero microtareas implementadas por este encargo.
