# Prompt para pegar en OTRO CHAT de Work

Ejecuta como goal el pulido de LocalCash **solo hasta completar MVP-S03**.

Repositorio: aik1a/LocalCash.
Rama de entrada e integración: task/mvp-s01-s03-goal-20261102.
Goal normativo de esta ejecución: docs/agent/goals/LOCALCASH-MVP-HASTA-S03.md.
Cola operativa: docs/agent/goals/LOCALCASH-MVP-HASTA-S03.queue.json.
Checkpoint: docs/agent/goals/LOCALCASH-MVP-HASTA-S03.checkpoint.md.

Este mensaje invoca la implementación en este nuevo chat de Work. No me devuelvas otra planificación: comienza inspeccionando repo/rama/entorno y ejecuta el primer paso pendiente dentro del alcance.

Actúa como Orquestador de la ejecución y coordina Codificador y Auditor independientes según WORKFLOW/TASK_LIFECYCLE. Usa agentes separados cuando la herramienta lo permita; no dejes al Codificador autoauditar su código. La fase MVP-S01-T02 es documental del Orquestador. No dos escritores sobre el mismo checkout.

Lee PROJECT_ROUTER.md, AGENTS.md, PROJECT_STATE.md y el goal limitado. La rama ya contiene la planificación del PR15, aunque ese PR no esté fusionado. No uses main como si contuviera esos documentos ni repliques el código de PR14.

Alcance autorizado: **MVP-S01, MVP-S02 y MVP-S03: 10 fases, 37 microtareas**.
Incluye reglas/saldo/CI; CRUD de cuentas, contactos y movimientos; CRUD, detalles, filtros y formularios de compras. Conserva divisas, biometría y mapas existentes.

Avanza en orden: una fase pequeña, sus microtareas, pruebas relacionadas, handoff y auditoría correspondiente; después activa la siguiente. Registra evidencia, commits de trabajo y checkpoint para retomar sin preguntarme por cada paso rutinario. Repara los fallos relacionados y vuelve a probar. Preserva cambios ajenos y datos existentes.

El límite posterior de este prompt prevalece sobre el goal de siete sprints: usa la cola limitada y **detente después de MVP-S03-T03 / MVP-S03-T03-M03 y sus gates**. No implementar S04–S07, pagos de compras, devoluciones, deudas PAGADA, swipe ni migraciones v6/v7 en esta ejecución.

Mantén Java/XML/SQLiteOpenHelper/Groovy. No introducir Room, Compose, Kotlin, MVVM, DI, backend ni refactor general. No force-push, borrado de datos ni desinstalación con datos. No fusionar código a main sin gate independiente y autorización de integración aplicable; deja cambios revisables en la rama del goal/PR.

Primera fase preparada: MVP-S01-T01.
Primera microtarea pendiente: MVP-S01-T01-M01.
Antes de empezar, comprueba si otro agente ya avanzó; no sobrescribas progreso ni vuelvas al baseline antiguo.

No declares terminado solo porque escribiste código o compiló. Ejecuta pruebas y flujos pertinentes; distingue host/emulador/teléfono y registra límites reales. Si falta un rol o entorno esencial, deja handoff y bloqueo exacto; no inventes aprobación ni avances saltando la dependencia.

Al terminar, entrega qué quedó implementado hasta S03, commits/PR, pruebas, límites y siguiente paso detenido en S04.
