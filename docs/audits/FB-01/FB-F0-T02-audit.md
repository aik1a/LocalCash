# Auditoría independiente — FB-F0-T02

## Identificación y alcance exacto

- Autor de la entrega: Orquestador; commits Git atribuidos a `a1kia`, PR presentado por `aik1a`.
- Auditor: Codex, sesión independiente de revisión; no elaboró la entrega ni intervino en sus dos commits.
- Fecha: 2026-11-21, America/Santiago. Última comprobación remota: 23:54:58 UTC (20:54:58 local).
- PR auditado: [#20](https://github.com/aik1a/LocalCash/pull/20), abierto.
- Base: `d7657d1cd798e3f86f9ac9fbe75a80af1fd28c4b`.
- Head: `126b30b3467d7737ccd8f48361a798ff291e20da`.
- Destino: `feat/firebase-migration`; origen del autor: `task/FB-F0-T02-baseline-inventario`.
- Base funcional: `c28a27b87aeeb092a936b8ba6b05b83404ac1c13`.
- Tipo: auditoría documental de tarea; no gate funcional ni cierre de fase.
- Informe preparado en rama separada `audit/FB-F0-T02-independent`, desde la base aceptada d7657d1. No se modifica la rama del PR ni la entrega del autor.

Al inicio y antes del veredicto se consultaron PR, comentarios de conversación, comentarios inline y revisiones: todos los conjuntos de comentarios/revisiones estaban vacíos. Base/head/destino coinciden con el encargo; no hay cambio de alcance. Se revisaron PR recientes y el historial de `docs/audits/FB-01` en las refs clonadas: no apareció otra auditoría de T02. `handoff/external-agents` apunta a cfc0c08952c6b037faf02f48080e6bba213d6e54 y solo se usó como continuidad. Main continúa en f0e491d2f620fefcdfd18ad317a85c00a3b29a39. La etiqueta anotada conserva objeto e769eb88cea864ec714e3f853526f3335262996e y destino c28a27b.

## Fuentes inspeccionadas

La entrega, el brief y el handoff se leyeron en el head auditado, junto con el diff completo `d7657d1..126b30b` (10 archivos, 266 inserciones, 8 eliminaciones).

- `PROJECT_ROUTER.md`, `PROJECT_STATE.md`, `AGENTS.md`, `docs/agent/roles/AUDITOR.md`, `docs/agent/CONTEXT_PACKS.md`, `docs/agent/FIREBASE_PROTOCOL.md`, `docs/audits/AUDIT_MATRIX.md` y `AUDIT_TEMPLATE.md`.
- `docs/design/decisions/ADR-003-firebase-google-offline.md`, ExecPlan `docs/exec-plans/active/FB-01-firebase-google-offline.md`, brief activo y handoff `FB-F0-T02-baseline-inventario.md`, `docs/design/FIREBASE_BASELINE.md` (148 líneas).
- Cambios completos de `ARCHITECTURE.md`, `docs/design/ARCHITECTURE.md`, `docs/design/DATABASE.md`, `docs/requirements/TRACEABILITY.md`; reglas vigentes en `docs/design/BUSINESS_RULES.md`.
- Bajo `app/src/main/java/com/aik1a/localcash/`: helper; los seis repositorios Cuenta, Movimiento, Contacto, Compra, Deuda y Configuracion; servicios Movimiento, Compra y Deuda; dominio Cuenta, Movimiento, Compra, DetalleCompra, Contacto, Deuda, Reparto, Dinero, Divisa y Ubicacion.
- Consumidores de helper y claves de Intent en las ocho Activities financieras, ActividadProtegida y DivisasUI; flujo de pago de CompraDetalleActivity; etiquetas en ComprasActivity. Manifest y `app/build.gradle`.
- `FlowChecks`, `PaymentChecks`, secciones de fixture/upgrade/rollback de `CrudChecks`, ubicación del helper LegacyV6 y métodos JUnit. Los nueve YAML de `.github/workflows/`; scripts `domain-checks.sh`, `biometric-regression-dispatch.py`, `biometric-emulator.py` (dispatcher), `relaunch-checks.py` y preparación SQL de los dos scripts host de monedas/pagos.
- PR #16, run 41705745479, sus jobs y log API36; handoffs `SYNC-UI-2026-11-03.md` y `CI-MAIN-2026-11-03.md`, tratados como antecedentes atribuidos.

Las referencias archivo/línea siguientes corresponden al head auditado, no a la rama del informe. [Entrega exacta](https://github.com/aik1a/LocalCash/blob/126b30b3467d7737ccd8f48361a798ff291e20da/docs/design/FIREBASE_BASELINE.md).

## Criterios de aceptación

| ID | Criterio | Contraste independiente | Resultado |
|---|---|---|---|
| AC-01 | Rutas/SHA reales y garantías correctamente atribuidas | Nueve tablas cotejadas con helper; PK/FK/CHECK, índices, triggers y migraciones v1–v7. Repositorios/servicios cotejados con sección 2; separa SQL de dominio/servicio y declara pendientes Firestore en sección 4. Una imprecisión editorial H-01 no cambia la matriz. | CUMPLE |
| AC-02 | Matriz cubre todos los módulos exigidos | Las 12 filas incluyen configuración/divisa histórica, cuentas/movimientos, ubicación, compra/detalles/pago, reparto/deudas, renombres, ambos borrados e importación. Riesgos y pruebas pendientes concretos; no presume equivalencia SQL/Firestore. | CUMPLE |
| AC-03 | Estado conciliado con merges/evidencia e historial preservado | API confirma #16 integrado; entradas operativas describen v7/APK declarado 1.1.1. El diff conserva registros previos y delimita su vigencia. Failure histórico y límites de CI explícitos; no extrapola PASS ni inventa auditoría funcional. | CUMPLE |
| AC-04 | F0-T03 y decisiones F1 explícitas, F0 abierta | Sección 7 enumera conservación/firma/distribución/responsable Firebase y decisiones de IDs, atomicidad, caché, rechazos y contratos asíncronos. Router/estado/plan/handoff no autorizan F1 ni cierran F0. | CUMPLE |
| AC-05 | Diff documental pertinente y MVP académico | Solo los 10 Markdown previstos; sin código, dependencias, workflows, servicios, requisitos nuevos ni capas adicionales. | CUMPLE |

## Contraste técnico del contenido

Se contrastaron las nueve tablas, sin reconstruir ni sustituir el inventario del autor. Helper líneas 8–155 confirma versión, FK activadas, nueve tablas permanentes y `deudas_v7` temporal. Las garantías descritas sobre moneda/precisión y sus límites concuerdan con los triggers; configuración es inmutable por API, no por trigger.

MovimientoService.registrar lee historial completo y valida overflow en transacción; Cuenta.calcularSaldo admite negativo y usa suma exacta. CompraRepository.crear/actualizarPlanificacion/eliminarPlanificacion delimitan las transacciones declaradas. CompraService.pagar líneas 48–66 comparte helper con MovimientoService, comprueba vínculo/divisa y coordina egreso, vínculo y estado; registrarPago no promete atomicidad aislada. No hay garantía de saldo suficiente descrita ni implementada: no se introduce una regla distinta.

DeudaRepository.registrar líneas 12–36 revalida la compra/reparto e inserta deudas y cuota propia atómicamente. cambiarEstado limita la actualización al par y monto positivo; DeudaService agrega pendientes por divisa sin movimientos. CuentaRepository.eliminarSinUso y ContactoRepository.eliminarSinUso combinan DELETE condicionado con FK RESTRICT; planificación elimina ítems/cabecera tras revalidación. No se observaron omisiones críticas para diseñar F1.

Dominio y UI confirman IDs long locales, referencias compuestas, ausencia de ID de detalle en dominio, extras long y centésimas para todas las divisas. Divisa/Dinero y Reparto corroboran precisión CLP/JPY, operaciones exactas, resto primero a Tú y luego IDs ascendentes. Total/saldo son derivados. Las referencias a fechas, compras históricas sin vínculo, cero, reparto sin movimientos y aviso de egreso manual concuerdan con código/reglas. La advertencia sobre identidad y orden de importación es pertinente.

Runners/scripts/YAML confirman un único Instrumentation registrado, auxiliares despachados por mode y 29 métodos JUnit presentes. Ningún workflow invoca JUnit ni payment/crud/crud-upgrade/feedback; compilar instrumentación no ejecuta esos modos. T06C es el único trigger PR y sus paths omiten documentos; push no cubre ramas FB. T02 fija un SHA histórico. Dispatcher omite regresión completa en API24. Las lagunas quedan como trabajo separado, no como condición de build ficticia para este PR.

## Matriz canónica de calidad

| ID | Peso | Resultado | Evidencia / aplicación |
|---|---:|---|---|
| Q01 Requisitos/aceptación | 25 | CUMPLE | AC-01–05, ADR-003 y brief |
| Q02 Completitud funcional | 15 | N/A | No se entrega funcionalidad ejecutable |
| Q03 Arquitectura/POO | 15 | N/A | No se modifica arquitectura ejecutable; descripción contrastada en Q01/Q08 |
| Q04 Calidad del código | 10 | N/A | No hay código nuevo |
| Q05 Datos/seguridad | 10 | CUMPLE | Garantías y riesgos documentados sin atribuirlos a Firebase; no se operan datos personales |
| Q06 Validación | 15 | CUMPLE | Diff/enlaces/coherencia y evidencia remota; build Android no exigible |
| Q07 Alcance/dependencias | 5 | CUMPLE | 10 Markdown; árboles ejecutables idénticos |
| Q08 Documentación/handoff | 5 | PARCIAL | H-01 editorial, resto trazable y conciliado |

Puntaje diagnóstico normalizado: 95,8/100 sobre 60 puntos aplicables. No sustituye los gates.

## Evidencia inspeccionada y verificaciones repetidas

**Histórica inspeccionada, sin rerun:** [PR #16](https://github.com/aik1a/LocalCash/pull/16) confirma merge c28a27b el 2026-11-03 19:46:33 UTC y head 2bfec047b92a6bf2e31f70fae701be3a54b49673. [Run 41705745479](https://github.com/aik1a/LocalCash/actions/runs/41705745479) corresponde a ese head, intento 1, completed/failure. Jobs 126160459023 (API24) success y 126160459675 (API36) failure. El log API36 contiene traceback en `tests/relaunch-checks.py:34` y `AssertionError: REALIZADA`. La discrepancia con `ComprasActivity.java:43` (Realizada/Planificada) se confirma estáticamente; no se demuestra que sea el único problema. El dispatcher se detiene antes de shared/location. Las afirmaciones locales de JUnit/CRUD/feedback proceden de #16/SYNC-UI; no se presentan como repetidas.

**Ejecutado por este auditor:** clon e inspección del head exacto; consultas GET de PR/comentarios/reviews/run/jobs/log; `git ls-remote`, `git cat-file`, `git diff --stat`, diff completo y `git diff d7657d1 126b30b --check` (exit 0); comparación de árboles de app/tests/.github/tools entre c28a27b, d7657d1 y 126b30b, idénticos. Control Python independiente de destinos Markdown locales en los diez archivos del diff: 74, cero rotos; conteo estático de @Test: 29. No se certifican todos los sitios externos ni fragmentos por ese control. Consulta Actions por SHA de merge e integración: total_count=0 en ambos; resultado acotado a esas consultas, no afirmación universal de ausencia de pruebas.

Evidencia propia: [captura remota resumida](evidence/FB-F0-T02-remote.json) y [control documental reproducible](evidence/FB-F0-T02-checks.py). El script usa objetos Git del head auditado y puede repetirse desde la rama del informe. Comando: `python docs/audits/FB-01/evidence/FB-F0-T02-checks.py`.

Build Android, JUnit, scripts SQL host, instrumentación, pruebas manuales/físicas, Firebase, offline/reinicio/reconexión de Firebase: **NOT TESTED** por este auditor. No son criterios ejecutables de este diff documental. No se accedió a instalaciones con datos ni se ejecutaron modos destructivos; `sources/` no se modificó.

## Hallazgos

| ID | Severidad | Archivo/línea y evidencia | Impacto y seguimiento |
|---|---|---|---|
| H-01 | MINOR | `docs/design/FIREBASE_BASELINE.md:36` dice «inserta contactos y cuota propia juntos». `DeudaRepository.java:25–33` inserta filas en deudas y reparto_usuario; los contactos ya existen y DeudaService los consulta. | Imprecisión editorial acotada. Tabla de inventario y matriz describen correctamente deudas/cuota, por lo que no falta cobertura ni se requiere cambiar código. Responsable: Orquestador/autor, aclarar en la próxima actualización documental de T02 o al preparar contexto F1. No condiciona el merge ni exige nueva auditoría integral. |

Cero BLOCKER y cero MAJOR. El failure histórico de CI y la cobertura ausente ya están identificados por el autor: requieren seguimiento separado antes de confiar en CI funcional, sin convertir esta auditoría en corrección de la app.

## Gates y veredicto

| Gate | Resultado |
|---|---|
| Requisitos y criterios esenciales documentales | PASS |
| Evidencia documental, refs y trazabilidad | PASS con H-01 menor |
| Arquitectura/decisiones y alcance | PASS |
| Garantías financieras descritas y límites | PASS documental, sin certificación funcional |
| Build/pruebas funcionales requeridos | N/A para este diff; NOT TESTED |

**VEREDICTO: PASS WITH OBSERVATIONS.**

**APPROVED_FOR_MERGE** exclusivamente para `126b30b3467d7737ccd8f48361a798ff291e20da` del PR #20 hacia `feat/firebase-migration`, comparado con base `d7657d1cd798e3f86f9ac9fbe75a80af1fd28c4b`.

Commit del autor: APPROVED_FOR_COMMIT. Fase/sprint: **NOT_APPROVED_FOR_PHASE_ADVANCE / NOT_APPROVED_FOR_SPRINT_ADVANCE**. La aprobación solo cubre FB-F0-T02; no cierra F0, no habilita F1 y no aprueba Firebase ni los incrementos funcionales históricos. Si cambia el SHA, el Orquestador debe evaluar el cambio antes de aplicar esta autorización.

## Devolución al Orquestador

No hay correcciones obligatorias bloqueantes. Puede tramitar la integración de #20 verificando el SHA exacto y registrar el cierre administrativo de T02 cuando corresponda; este auditor no fusiona ni cierra tareas. Siguiente unidad justificada: preparar F0-T03, pues conservación de instalaciones/datos, firma/distribución y responsable Firebase siguen pendientes y son dependencias del gate F0. Mantener una tarea separada para corregir la aserción de relanzamiento y ajustar cobertura CI antes del primer cambio funcional. Atender H-01 en mantenimiento documental acotado, sin repetir el inventario.

Publicación del informe: no completada. El push de la rama de auditoría falló por falta de autenticación Git de escritura; la alternativa mediante GitHub API create_tree devolvió 403, Resource not accessible by integration. No se creó rama remota ni PR de auditoría; los tres archivos quedan en la rama local separada desde d7657d1. Este límite de publicación no afecta el contraste ni el veredicto sobre #20.
