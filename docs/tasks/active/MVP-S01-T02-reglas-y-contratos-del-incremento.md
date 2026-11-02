# Task Brief — MVP-S01-T02: Reglas y contratos del incremento

**Sprint:** MVP-S01 · **Fase:** 2 · **Rol:** Orquestador  
**Estado inicial:** DRAFT — activar al satisfacer dependencias  
**Dependencia:** MVP-S01-T01.  
**Rama de trabajo:** task/MVP-S01-T02-reglas-y-contratos-del-incremento.  
**Base:** main aceptado con este plan integrado; mientras el plan esté en PR documental, leerlo en su rama y crear la tarea desde el commit de planificación indicado por el Orquestador. El código funcional inicial debe coincidir con 9b8a651d66982eea3a41e1979342468fe4cad176; si difiere, registrar y adaptar contexto antes de editar. Fases posteriores parten del SHA aceptado al cierre previo, nunca del baseline antiguo a ciegas.

## Objetivo y alcance

Trasladar el alcance acordado a reglas y contratos verificables.

## Requisitos relacionados

- LC-ACC-01
- LC-MOV-01
- LC-BUY-01
- LC-BUY-02
- LC-DEBT-01
- Contratos POL aplicables: consultar docs/design/MVP-PULIDO-CONTRATOS.md.
- Criterios EV3 y ampliaciones del usuario: mapa de cobertura en docs/exec-plans/active/MVP-PULIDO-COMPLETO.md.

## Context Pack

Leer PROJECT_ROUTER.md, AGENTS.md, PROJECT_STATE.md, docs/agent/roles/ORCHESTRATOR.md, el sprint y este Task Brief. Cargar después solo estos archivos/componentes:
- docs/design/BUSINESS_RULES.md
- docs/design/DATABASE.md
- docs/requirements/TRACEABILITY.md
- docs/requirements/PROJECT_SPEC.md

Los nombres de componentes nuevos son propuestas, no prueba de que existan. Comprobar el código real.

## Microtareas — orden obligatorio

| ID | Acción | Criterio de aceptación | Verificación/evidencia |
|---|---|---|---|
| MVP-S01-T02-M01 | Registrar reglas de saldo y legado | Nuevas operaciones no crean déficit; negativos previos legibles y recuperables; cálculo puro no se convierte en validación de lectura. | Revisión de reglas POL-01 a POL-04 y ejemplos |
| MVP-S01-T02-M02 | Registrar contratos de pago y devolución | Definir cuenta de igual divisa, cargo total único, compra cero e históricos sin cobro retroactivo. | Tabla de estados y casos POL-05 a POL-08 |
| MVP-S01-T02-M03 | Registrar reglas CRUD y deudas | Definir borrado protegido, corrección de pago registrado y anulación de reparto pendiente sin borrar pagos reales. | Tabla POL-09 a POL-12 |
| MVP-S01-T02-M04 | Actualizar trazabilidad y contexto | Mapear matriz EV3 y pedidos UX a fases; separar decisión registrada de función implementada. | Diff documental; sin marcar requisitos funcionales DONE |

Cada microtarea depende de la anterior en la cola global. La primera de esta fase requiere además que la fase previa esté CLOSED por el Orquestador (si aplica). Estado detallado y cursor: docs/agent/goals/LOCALCASH-MVP-PULIDO.queue.json.

## Fuera de alcance

- Funciones de fases posteriores; no implementar todo el sprint como un único cambio.
- Cambiar stack, introducir capas generales, ampliar mapas o rehacer divisas.
- Reaplicar PR14, tocar datos reales para facilitar pruebas o borrar trabajo ajeno.
- Declarar auditoría independiente del propio código o fusionar a main desde el rol Codificador.

## Validación requerida

- Revisar consistencia de reglas, IDs, referencias y trazabilidad; no ejecutar build por cambios solo documentales.
- No cambiar estados de funcionalidad a DONE al registrar un contrato o crear una tarea.

## Entrega y cierre

1. Actualizar evidencia de microtareas en la cola sin falsificar resultados.
2. Crear docs/handoffs/MVP-S01-T02-coder.md siguiendo HANDOFF_TEMPLATE.md (si es tarea de Orquestador, handoff de planificación).
3. Registrar SHA, diff, comandos/exit codes, escenarios, pendientes y siguiente ID.
4. Orquestador valida la tarea documental y registra su cierre sin atribuir aprobación funcional.
5. Orquestador formaliza APPROVED/CLOSED, integra lo aceptado y activa la siguiente fase sin preguntar al usuario por cada paso rutinario.
6. Al terminar el sprint, Auditor emite gate de avance y Orquestador actualiza PROJECT_STATE y trazabilidad. No confundir fin de microtareas con sprint aceptado.
