# Task Brief — MVP-S07-T02: Recorrido real de UX y offline

**Sprint:** MVP-S07 · **Fase:** 2 · **Rol:** Codificador  
**Estado inicial:** DRAFT — activar al satisfacer dependencias  
**Dependencia:** MVP-S07-T01.  
**Rama de trabajo:** task/MVP-S07-T02-recorrido-real-de-ux-y-offline.  
**Base:** main aceptado con este plan integrado; mientras el plan esté en PR documental, leerlo en su rama y crear la tarea desde el commit de planificación indicado por el Orquestador. El código funcional inicial debe coincidir con 9b8a651d66982eea3a41e1979342468fe4cad176; si difiere, registrar y adaptar contexto antes de editar. Fases posteriores parten del SHA aceptado al cierre previo, nunca del baseline antiguo a ciegas.

## Objetivo y alcance

Verificar uso real y separar pruebas humanas de emulador.

## Requisitos relacionados

- LC-AUTH-01
- LC-MAP-01
- LC-BUY-01
- LC-DEBT-01
- Contratos POL aplicables: consultar docs/design/MVP-PULIDO-CONTRATOS.md.
- Criterios EV3 y ampliaciones del usuario: mapa de cobertura en docs/exec-plans/active/MVP-PULIDO-COMPLETO.md.

## Context Pack

Leer PROJECT_ROUTER.md, AGENTS.md, PROJECT_STATE.md, docs/agent/roles/CODER.md, el sprint y este Task Brief. Cargar después solo estos archivos/componentes:
- docs/testing/MANUAL_TESTS.md
- docs/testing/TEST_PLAN.md
- app/src/main/res/layout

Los nombres de componentes nuevos son propuestas, no prueba de que existan. Comprobar el código real.

## Microtareas — orden obligatorio

| ID | Acción | Criterio de aceptación | Verificación/evidencia |
|---|---|---|---|
| MVP-S07-T02-M01 | Recorrer pantallas con teclado y fuente grande | Todas las operaciones alcanzables, importes legibles, nombres largos y cancelaciones correctas. | Registro entorno/API/tamaño/fuente; evidencia respetando FLAG_SECURE |
| MVP-S07-T02-M02 | Probar apertura, huella y lifecycle | Frío, reabrir, Home, bloqueo, cancelación, rotación y reinicio sin perder dinero ni abrir contenido privado. | Logcat y recorrido Android reproducible |
| MVP-S07-T02-M03 | Probar modo avión con datos persistidos | Crear/editar/borrar/consultar/mapa/deudas funcionan offline y sobreviven reinicio; ubicación denegada no bloquea guardar sin GPS. | Registro recorrido offline y snapshots |
| MVP-S07-T02-M04 | Comprobar teléfono físico o registrar bloqueo real | No llamar físico al emulador; si no hay acceso, dejar evidencia parcial y tarea BLOCKED, no goal terminado. | Modelo/API real y resultado o motivo de bloqueo |

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
2. Crear docs/handoffs/MVP-S07-T02-coder.md siguiendo HANDOFF_TEMPLATE.md (si es tarea de Orquestador, handoff de planificación).
3. Registrar SHA, diff, comandos/exit codes, escenarios, pendientes y siguiente ID.
4. Codificador informa READY_FOR_AUDIT; Auditor independiente revisa el diff de esta fase y regresiones relevantes, con veredicto para este ID. No repetir una auditoría integral del producto en cada microtarea.
5. Orquestador formaliza APPROVED/CLOSED, integra lo aceptado y activa la siguiente fase sin preguntar al usuario por cada paso rutinario.
6. Al terminar el sprint, Auditor emite gate de avance y Orquestador actualiza PROJECT_STATE y trazabilidad. No confundir fin de microtareas con sprint aceptado.
