# Task Brief — MVP-S04-T03: Deshacer y eliminar compra pagada

**Sprint:** MVP-S04 · **Fase:** 3 · **Rol:** Codificador  
**Estado inicial:** DRAFT — activar al satisfacer dependencias  
**Dependencia:** MVP-S04-T02.  
**Rama de trabajo:** task/MVP-S04-T03-deshacer-y-eliminar-compra-pagada.  
**Base:** main aceptado con este plan integrado; mientras el plan esté en PR documental, leerlo en su rama y crear la tarea desde el commit de planificación indicado por el Orquestador. El código funcional inicial debe coincidir con 9b8a651d66982eea3a41e1979342468fe4cad176; si difiere, registrar y adaptar contexto antes de editar. Fases posteriores parten del SHA aceptado al cierre previo, nunca del baseline antiguo a ciegas.

## Objetivo y alcance

Restituir el importe una sola vez y proteger relaciones.

## Requisitos relacionados

- LC-BUY-01
- LC-BUY-02
- LC-MOV-01
- Contratos POL aplicables: consultar docs/design/MVP-PULIDO-CONTRATOS.md.
- Criterios EV3 y ampliaciones del usuario: mapa de cobertura en docs/exec-plans/active/MVP-PULIDO-COMPLETO.md.

## Context Pack

Leer PROJECT_ROUTER.md, AGENTS.md, PROJECT_STATE.md, docs/agent/roles/CODER.md, el sprint y este Task Brief. Cargar después solo estos archivos/componentes:
- app/src/main/java/com/aik1a/localcash/application/CompraService.java
- app/src/main/java/com/aik1a/localcash/data/CompraRepository.java
- app/src/main/java/com/aik1a/localcash/presentation/CompraDetalleActivity.java

Los nombres de componentes nuevos son propuestas, no prueba de que existan. Comprobar el código real.

## Microtareas — orden obligatorio

| ID | Acción | Criterio de aceptación | Verificación/evidencia |
|---|---|---|---|
| MVP-S04-T03-M01 | Implementar deshacer pago sin reparto | Retirar vínculo/egreso y volver a planificada en una transacción; saldo vuelve a cuenta original; no añadir ingreso duplicado. | Prueba devolución exacta, FK y rollback |
| MVP-S04-T03-M02 | Gestionar compra cero e histórico sin vínculo | Cero no devuelve dinero; histórico no tiene devolución inventada, solo cambio de registro explícito si se ofrece. | Pruebas de ramas sin cargo |
| MVP-S04-T03-M03 | Conectar confirmación y doble ejecución | Mensaje muestra devolución y cuenta correctas; segunda ejecución no devuelve dos veces; importe pagado no editable directamente. | UI confirmación y prueba de repetición |
| MVP-S04-T03-M04 | Conectar eliminación con pago | Compra pagada sin reparto puede eliminarse con devolución explícita atómica; con reparto se bloquea hasta sprint 5. | Borrado integral, cancelación y ausencia de huérfanos |
| MVP-S04-T03-M05 | Validar ciclo completo y regresiones | Planificar/comprar/deshacer/recomprar/borrar; bloquear acciones directas del movimiento vinculado; otra cuenta intacta. | Suite Android API24/API36 y handoff |

Cada microtarea depende de la anterior en la cola global. La primera de esta fase requiere además que la fase previa esté CLOSED por el Orquestador (si aplica). Estado detallado y cursor: docs/agent/goals/LOCALCASH-MVP-PULIDO.queue.json.

## Fuera de alcance

- Funciones de fases posteriores; no implementar todo el sprint como un único cambio.
- Cambiar stack, introducir capas generales, ampliar mapas o rehacer divisas.
- Reaplicar PR14, tocar datos reales para facilitar pruebas o borrar trabajo ajeno.
- Declarar auditoría independiente del propio código o fusionar a main desde el rol Codificador.

## Validación requerida

- Pruebas de comportamiento indicadas arriba; adaptar/añadir solo las necesarias.
- Si cambia Java/XML/Gradle: build Android pertinente antes del handoff. Usar wrapper y entorno configurado en docs/delivery/BUILD.md.
- Comandos base: bash tests/domain-checks.sh; python tests/sqlite-currency-checks.py; bash gradlew --no-daemon --console=plain :app:assembleDebug :app:assembleDebugAndroidTest.
- No repetir build por cada cambio de texto o microtarea sin código compilable. Pruebas focalizadas durante la fase; build/gate al terminarla y regresión relacionada.
- Android, emulador y teléfono se registran por separado. Si falta entorno esencial, BLOCKED con comando/error exacto; no inventar PASS.

## Entrega y cierre

1. Actualizar evidencia de microtareas en la cola sin falsificar resultados.
2. Crear docs/handoffs/MVP-S04-T03-coder.md siguiendo HANDOFF_TEMPLATE.md (si es tarea de Orquestador, handoff de planificación).
3. Registrar SHA, diff, comandos/exit codes, escenarios, pendientes y siguiente ID.
4. Codificador informa READY_FOR_AUDIT; Auditor independiente revisa el diff de esta fase y regresiones relevantes, con veredicto para este ID. No repetir una auditoría integral del producto en cada microtarea.
5. Orquestador formaliza APPROVED/CLOSED, integra lo aceptado y activa la siguiente fase sin preguntar al usuario por cada paso rutinario.
6. Al terminar el sprint, Auditor emite gate de avance y Orquestador actualiza PROJECT_STATE y trazabilidad. No confundir fin de microtareas con sprint aceptado.
