# Task Brief — MVP-S07-T03: APK, entrega y gate final

**Sprint:** MVP-S07 · **Fase:** 3 · **Rol:** Codificador  
**Estado inicial:** DRAFT — activar al satisfacer dependencias  
**Dependencia:** MVP-S07-T02.  
**Rama de trabajo:** task/MVP-S07-T03-apk-entrega-y-gate-final.  
**Base:** main aceptado con este plan integrado; mientras el plan esté en PR documental, leerlo en su rama y crear la tarea desde el commit de planificación indicado por el Orquestador. El código funcional inicial debe coincidir con 9b8a651d66982eea3a41e1979342468fe4cad176; si difiere, registrar y adaptar contexto antes de editar. Fases posteriores parten del SHA aceptado al cierre previo, nunca del baseline antiguo a ciegas.

## Objetivo y alcance

Entregar un artefacto correcto, identificable y conservando datos.

## Requisitos relacionados

- LC-TECH-04
- LC-TECH-05
- LC-TECH-06
- Contratos POL aplicables: consultar docs/design/MVP-PULIDO-CONTRATOS.md.
- Criterios EV3 y ampliaciones del usuario: mapa de cobertura en docs/exec-plans/active/MVP-PULIDO-COMPLETO.md.

## Context Pack

Leer PROJECT_ROUTER.md, AGENTS.md, PROJECT_STATE.md, docs/agent/roles/CODER.md, el sprint y este Task Brief. Cargar después solo estos archivos/componentes:
- app/build.gradle
- docs/delivery/BUILD.md
- docs/delivery/PRESENTATION.md
- docs/requirements/TRACEABILITY.md
- PROJECT_STATE.md

Los nombres de componentes nuevos son propuestas, no prueba de que existan. Comprobar el código real.

## Microtareas — orden obligatorio

| ID | Acción | Criterio de aceptación | Verificación/evidencia |
|---|---|---|---|
| MVP-S07-T03-M01 | Preparar versión y firmante estable | Incrementar versión apropiada; firmar sin versionar claves/secretos; identificar SHA/certificado/APK y no desinstalar datos para resolver incompatibilidad. | Certificado, versionCode, hash y build exacto |
| MVP-S07-T03-M02 | Probar actualización con datos | Baseline y nueva versión de mismo firmante en instalación de prueba; cuentas/deudas/pagos/ubicaciones conservados. | Instalación upgrade y comparación pre/post; bloqueo histórico separado |
| MVP-S07-T03-M03 | Preparar guía y demo completas | Instrucciones llevan al SHA/APK final; demostración cubre matriz y pedidos extra sin inventar resultados. | Paquete/documentación comprobados |
| MVP-S07-T03-M04 | Preparar entrega al Auditor y cierre administrativo | Entregar artefactos, trazabilidad y contexto de revisión; dejar READY_FOR_AUDIT. Auditor y Orquestador ejecutan después sus gates externos. | Handoff final y lista de gates pendientes; no autoproclamar PASS |

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
2. Crear docs/handoffs/MVP-S07-T03-coder.md siguiendo HANDOFF_TEMPLATE.md (si es tarea de Orquestador, handoff de planificación).
3. Registrar SHA, diff, comandos/exit codes, escenarios, pendientes y siguiente ID.
4. Codificador informa READY_FOR_AUDIT; Auditor independiente revisa el diff de esta fase y regresiones relevantes, con veredicto para este ID. No repetir una auditoría integral del producto en cada microtarea.
5. Orquestador formaliza APPROVED/CLOSED, integra lo aceptado y activa la siguiente fase sin preguntar al usuario por cada paso rutinario.
6. Al terminar el sprint, Auditor emite gate de avance y Orquestador actualiza PROJECT_STATE y trazabilidad. No confundir fin de microtareas con sprint aceptado.
