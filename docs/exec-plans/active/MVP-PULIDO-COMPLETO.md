# MVP-PULIDO — ExecPlan completo por sprints y microtareas

## Objetivo

Completar el pulido académico de LocalCash: CRUD, saldo no negativo, compras con pago y devolución, deudas pagadas y anulación segura, interfaz y swipe, respuesta, privacidad, pruebas y entrega. Todo el alcance forma un único goal, dividido en **7 sprints, 23 fases y 91 microtareas**; no hay corte A/B ni funciones remitidas a “después” dentro de este alcance.

**Fecha objetivo:** 02/11/2026, America/Santiago; hora oficial no informada. La fecha no convierte tareas incompletas en terminadas. Los sprints son unidades de resultado consecutivas, no siete semanas. Este encargo documenta la ejecución; no inicia código ni un proceso en segundo plano.

## Requisitos relacionados

Pilares LC-P01/P02/P03/P04; LC-CON-01; LC-ACC-01; LC-MOV-01/02; LC-MAP-01; LC-BUY-01/02/03; LC-SHARED-01/02; LC-DEBT-01/02; LC-TECH-01/02/03/04/05/06.

La matriz EV3 exige Java, interfaces, SQLiteOpenHelper, relaciones, CRUD mediante DAO, gestión financiera, compras/contactos/deudas, buenas prácticas, mapas y pruebas offline. Saldo suficiente, pago vinculado, devolución, cierre de deudas y swipe se documentan como ampliaciones de producto solicitadas por Aikia; no se atribuyen literalmente al PDF docente.

## Estado inicial

- main al preparar el plan: 9b8a651d66982eea3a41e1979342468fe4cad176.
- Rollback conservado; S01-T01 divisas está implementado; S01-T02/PR14 fue revertido tras fallo de apertura reportado.
- Código actual: Java/XML, SQLite v5, diez divisas y reparto exacto, osmdroid offline Santiago Centro y BiometricPrompt.
- Auditoría de esta conversación: CRUD update/delete ausente en registros principales, sobregiro posible, compras sin pago/transición, deudas solo PENDIENTE. Dominios 34.400 aserciones y SQLite host 55 comprobaciones PASS; no acredita teléfono físico.
- No reabrir ni renumerar los sprints históricos S00–S07. Este ciclo usa prefijo MVP-Sxx para evitar colisiones y conserva auditorías antiguas.
- Ver informe de referencia docs/audits/planning/MVP-PULIDO-2026-11-01.md. Es diagnóstico, no aprobación del nuevo código.

## Alcance completo y trazabilidad

| Necesidad | Sprint/fases que la completan |
|---|---|
| Cuentas: edición/borrado seguro | MVP-S02-T01 |
| Contactos: edición/borrado y referencias | MVP-S02-T02 |
| Movimientos: edición/borrado y saldo derivado | MVP-S01-T03 + MVP-S02-T03 |
| Saldo no negativo con históricos preservados | MVP-S01-T02/T03 + pruebas MVP-S07 |
| Compras y detalles CRUD, filtros | MVP-S03-T01/T02/T03 |
| Elegir cuenta, cobrar una vez, moneda igual | MVP-S04-T01/T02 |
| Deshacer/recomprar/eliminar con devolución | MVP-S04-T03 + MVP-S05-T03 |
| Deudas PENDIENTE/PAGADA y corrección de registro | MVP-S05-T01/T02 |
| Resolver reparto pendiente antes de anular compra | MVP-S05-T03 |
| Divisas exactas y sobrantes sin pérdidas | Preservación transversal POL-13 + MVP-S07 |
| Acciones visibles y swipe izquierdo | MVP-S06-T02; acciones básicas acompañan cada CRUD |
| Interfaz legible, errores, teclado y estados | MVP-S03-T03 + MVP-S06-T01 |
| I/O y lifecycle sin filtrar datos tras bloqueo | MVP-S06-T03 |
| Privacidad/backup, biometría y cobertura real del mapa | MVP-S06-T04 |
| CI, pruebas, firma, APK, guía y demo | MVP-S01-T04 + MVP-S07 |

## Fuera de alcance

Room/Compose/Kotlin/MVVM/DI, servidor, conversión monetaria, pagos parciales, reembolsos bancarios entre personas, ampliación de mapas, campos adicionales del dibujo, contraseña propia o almacenamiento de huellas. No son necesarios para cerrar este pulido.

## Diseño propuesto

Conservar Presentation → Services/Domain → Repositories → SQLiteOpenHelper. La UI no calcula saldos ni contiene SQL. Ampliar repositorios actuales, sin duplicar DAOs.

- Saldo: validar comandos mutantes y escribir en la misma transacción. Mantener reconstrucción de históricos negativos y cálculo puro.
- Compras: actualizar detalles/cabecera atómicamente y proteger pago/reparto.
- Pago: vínculo único por compra, egreso único y estado juntos; cero sin movimiento inválido; legado sin cobro retroactivo.
- Deudas: estado persistido real y pago completo sin ingresos automáticos. Anular solo repartos completamente pendientes; si hay pagadas, proteger y explicar.
- Esquema previsto v5→v6 (pago)→v7 (estado deuda), con fixtures y migraciones aditivas/tabla afectada preservada. Ver contratos; ajustar a versión real al comenzar.
- UX: menú/detalle accesibles y gesto que revela acciones; confirmaciones por consecuencia, no mensajes técnicos; la misma operación comparte lógica.
- Rendimiento: ExecutorService pequeño, ownership del helper explícito y callbacks condicionados a sesión/actividad/solicitud vigente.

## Plan de implementación

| Sprint | Resultado | Fases | Microtareas |
|---|---|---:|---:|
| [MVP-S01](../../sprints/MVP-S01-base-reglas-y-proteccion-de-saldo.md) | Base, reglas y protección de saldo | 4 | 13 |
| [MVP-S02](../../sprints/MVP-S02-crud-de-cuentas-contactos-y-movimientos.md) | CRUD de cuentas, contactos y movimientos | 3 | 13 |
| [MVP-S03](../../sprints/MVP-S03-crud-y-organizacion-de-compras.md) | CRUD y organización de compras | 3 | 11 |
| [MVP-S04](../../sprints/MVP-S04-pago-de-compras-y-devolucion.md) | Pago de compras y devolución | 3 | 14 |
| [MVP-S05](../../sprints/MVP-S05-deudas-y-anulacion-de-repartos.md) | Deudas y anulación de repartos | 3 | 12 |
| [MVP-S06](../../sprints/MVP-S06-interfaz-gestos-respuesta-y-privacidad.md) | Interfaz, gestos, respuesta y privacidad | 4 | 16 |
| [MVP-S07](../../sprints/MVP-S07-regresion-apk-y-entrega.md) | Regresión, APK y entrega | 3 | 12 |

**Orden obligatorio:** MVP-S01 → MVP-S02 → MVP-S03 → MVP-S04 → MVP-S05 → MVP-S06 → MVP-S07. Los Task Briefs enlazados contienen alcance, requisitos, contexto, criterios, verificaciones, rama y handoff. Cada fila micro equivale a un cambio o comprobación observable; no se crea una rama/PR por cada microtarea.

## Mecánica de ejecución automatizable

1. Leer el goal docs/agent/goals/LOCALCASH-MVP-PULIDO.md y la cola JSON.
2. Comprobar rama, SHA y cambios ajenos. No volver a 9b8a651d66982eea3a41e1979342468fe4cad176 si ya hay fases aceptadas.
3. Orquestador activa una fase READY cuando dependencias están CLOSED. Codificador solo implementa fase asignada.
4. Microtareas dentro de fase: PENDING → IN_PROGRESS → VERIFIED, o BLOCKED con motivo. VERIFIED significa criterio comprobado, no merge aceptado.
5. Por cada micro: registrar comando/resultado/SHA/ruta de evidencia. Actualizar cursor y handoff al pausar. No cerrar formalmente por haber escrito código.
6. Pruebas focalizadas durante la fase; build y regresiones pertinentes antes del handoff. No exigir build Android por edición documental o por cada línea de código.
7. Terminar fase: READY_FOR_AUDIT; auditor independiente revisa ese diff y regresiones. No repetir auditoría integral por microtarea.
8. Orquestador formaliza APPROVED/CLOSED y siguiente READY sin pedir al usuario confirmación por acciones rutinarias ya autorizadas.
9. Fin de sprint: gate independiente de avance; después activar el siguiente sprint.
10. Fin del goal: todos los sprints CLOSED, todos los criterios y evidencia obligatoria, APK y documentación correctos.

Los estados formales y su autoridad siguen TASK_LIFECYCLE.md. La cola no otorga al Codificador permiso para autoaprobarse. Si no hay herramienta/agente Auditor u Orquestador disponible, generar handoff ejecutable para ese rol; no fabricar un PASS ni afirmar que sigue corriendo.

La automatización está en la selección y reanudación de tareas con estado persistente. Estos archivos no son un cron ni un proceso autónomo iniciado.

## Validación

- Base: bash tests/domain-checks.sh; python tests/sqlite-currency-checks.py.
- Android: bash gradlew --no-daemon --console=plain :app:assembleDebug :app:assembleDebugAndroidTest.
- CI: SHA exacto, API24/API36 y logs revisados. El dispatcher heredado no equivale a regresión financiera completa API24; ajustar y comprobar cobertura real.
- Pruebas nuevas: CRUD, saldo proyectado, rollback, vínculo, cero/histórico, doble toque/reintento, devolución, estado deuda y anulación de reparto.
- UX real: fuente grande/teclado/nombres largos, foco y acciones sin gesto, swipe y scroll, Home/rotación, biometría y modo avión.
- Upgrade: mismo firmante en instalación de prueba, datos antes/después. Firmantes históricos incompatibles no se arreglan desinstalando datos.
- Diferenciar host, emulador, recorrido humano y teléfono; no declarar ninguno sin evidencia.

## Progreso

**Preparación documental:** completa en esta entrega. **Implementación:** no iniciada.

- [ ] MVP-S01 aceptado.
- [ ] MVP-S02 aceptado.
- [ ] MVP-S03 aceptado.
- [ ] MVP-S04 aceptado.
- [ ] MVP-S05 aceptado.
- [ ] MVP-S06 aceptado.
- [ ] MVP-S07 aceptado.
- [ ] Goal completo y entrega verificada.

Estado detallado: docs/agent/goals/LOCALCASH-MVP-PULIDO.queue.json.

## Decisiones y descubrimientos

- 01/11/2026: Aikia pidió alcance total, sin cortes, incluyendo deudas pagadas y swipe; sustituye la priorización A/B del informe inicial.
- 01/11/2026: se documentan 7 sprints/23 fases/91 microtareas con gates por fase/sprint y checkpoint por micro.
- La independencia del Auditor se mantiene; las microtareas no introducen nuevas aprobaciones humanas.
- No se garantiza cumplir toda la implementación el 02/11 por documentar una cola detallada. Ante límite de tiempo, reportar avance real y continuar el mismo alcance cuando se ordene; no omitir tareas silenciosamente.

## Resultado final

**Pendiente de implementación.** Completar solo al finalizar el goal con cambios, pruebas, artefactos, límites y referencias de auditoría reales. No copiar aquí el resultado del sprint S00 como aprobación de este incremento.
