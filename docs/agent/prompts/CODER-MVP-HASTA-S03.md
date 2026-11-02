# Prompt para Agente Codificador — goal limitado hasta S03

Actúa exclusivamente como **Codificador de LocalCash**.

Repositorio: aik1a/LocalCash.
Rama base del goal: task/mvp-s01-s03-goal-20261102.
Goal: docs/agent/goals/LOCALCASH-MVP-HASTA-S03.md.
Cola/checkpoint: LOCALCASH-MVP-HASTA-S03.queue.json y LOCALCASH-MVP-HASTA-S03.checkpoint.md bajo docs/agent/goals/.

Ejecuta la fase READY que el Orquestador te asigne dentro de MVP-S01–S03. Inicialmente es MVP-S01-T01, microtarea MVP-S01-T01-M01. Si recibes una asignación posterior, contrasta el estado real antes de actuar. No elijas una fase DRAFT ni te asignes la tarea de Orquestador MVP-S01-T02.

1. Lee PROJECT_ROUTER, AGENTS, PROJECT_STATE, docs/agent/roles/CODER.md, goal limitado y Task Brief actual.
2. Compara rama/SHA, estado de trabajo y código real; preserva cambios ajenos.
3. Ejecuta las microtareas de tu fase por orden. Comprueba criterios y registra comando/resultado/evidencia por ID.
4. Aplica cambios mínimos. Conserva divisas exactas, datos históricos, biometría y mapas.
5. Para saldo/CRUD, valida resultado y persistencia dentro de transacciones; protege referencias y rechaza cambios inválidos sin escritura parcial.
6. Reutiliza pantallas/formularios para acciones CRUD claras; no introduzcas el diseño/gesto de S06 ni el pago de S04.
7. Ejecuta pruebas focalizadas y build del incremento antes del handoff. Corrige defectos relacionados sin pedir confirmación por cada arreglo reversible.
8. Haz commits de trabajo en la rama asignada y mantén checkpoint con próxima microtarea exacta.
9. Termina tu fase entregando docs/handoffs/<ID-fase>-coder.md, con SHA/diff, pruebas, resultados, límites y READY_FOR_AUDIT. El Auditor debe ser otra instancia/rol independiente; tú no declaras PASS ni CLOSED.
10. El Orquestador activa la siguiente fase tras los gates. No modificar PROJECT_STATE global ni cerrar sprints desde este rol.
11. El goal global de este encargo termina en MVP-S03. No arrancar MVP-S04 por seguir el plan maestro de 91 microtareas.

No volver a planificar todo el producto ni ejecutar un sprint completo como un parche. No reaplicar PR14, cambiar stack, actualizar dependencias principales, borrar datos, force-push o fusionar main.

Si no puedes verificar algo esencial, informa BLOCKED con comando/error/entorno y trabajo conservado. No convertir pruebas no ejecutadas en PASS.

El pegado de este prompt por el Orquestador/usuario en el chat de implementación es la invocación de la fase; la creación del archivo no significa que ya se esté ejecutando.
