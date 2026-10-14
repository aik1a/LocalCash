# Auditoría independiente — S00-T05: gastos compartidos y deudas

## Identificación

- **Sprint:** S00 — entrega académica acelerada
- **Tarea:** S00-T05 — gastos compartidos y deudas
- **Auditor:** Agente Auditor independiente
- **Fecha:** 2026-10-13
- **Rama auditada:** `task/S00-T05-gastos-compartidos`
- **Base `main`:** `df74f118a50aa044ef8ff3e17a83070de5a9f7e3`
- **SHA funcional compilado/probado:** `45c70f7efa2bb2ffcdc2b7cb7af78c16ca1c1c50`
- **Alcance del diff:** 2 commits adelante de main, 0 detrás al inspeccionar. El commit posterior al SHA funcional contiene exclusivamente documentación y evidencias; no modificó app/tests/workflow.
- **Tipo:** única auditoría del incremento completo; no reauditoría de S00-T01–T04.
- **Task Brief:** `docs/tasks/active/S00-T05-gastos-compartidos.md`
- **Handoff:** `docs/handoffs/S00-T05-coder.md`
- **Referencia CI original:** https://github.com/aik1a/LocalCash/actions/runs/38440658476
- **Documento normativo:** `LocalCash Proyecto Android.pdf`, módulo 3 (págs. 5–6), reparto entre contactos y registro de importes adeudados.

## Fuentes verificadas

- `PROJECT_ROUTER.md`, `AGENTS.md`, `PROJECT_STATE.md`, `docs/agent/roles/AUDITOR.md`.
- `docs/audits/AUDIT_MATRIX.md`, `docs/audits/AUDIT_TEMPLATE.md`.
- `docs/requirements/PROJECT_SPEC.md`, requisitos LC-SHARED-01/02, LC-DEBT-01/02 y LC-TECH-01.
- `docs/design/BUSINESS_RULES.md`, `docs/design/DOMAIN_MODEL.md`, `docs/design/DATABASE.md`.
- Task Brief S00-T05, `docs/testing/S00-T05.md`, handoff y evidencia versionada.
- Diff `main...task/S00-T05-gastos-compartidos`; revisión de `Reparto.java`, `Deuda.java`, `DeudaService.java`, `DeudaRepository.java`, `LocalCashDbHelper.java`, `RepartoActivity.java`, integración con `CompraDetalleActivity.java`, XML y Manifest.
- Tests `SharedDomainChecks.java`, `FlowChecks.java`, `shared-relaunch-checks.py` y workflow `s00-t05-build.yml`.
- **GitHub Actions original**: ejecución y jobs, **log original** de job `115446054645`, hash del artefacto y registros exportados. No se aceptó el handoff como prueba autónoma.

## Criterios de aceptación del Task Brief

| ID | Criterio | Evidencia verificada | Estado |
|---|---|---|---|
| AC-01 | Compra REALIZADA, existente, con detalles y total derivado; PLANIFICADA excluida | CompraDetalleActivity / Reparto, Reparto constructor; FlowChecks rechaza planificada | CUMPLE |
| AC-02 | Dos o más contactos existentes, selección explícita, sin duplicados ni selección automática del contacto asociado | RepartoActivity / DeudaService / Reparto, UI FlowChecks y dominio | CUMPLE |
| AC-03 | Equitativo exacto, residuo por ID ascendente | Reparto.equitativo; 100,01/3 = 33,34+33,34+33,33, barrido de totales y MAX_VALUE en SharedDomainChecks | CUMPLE |
| AC-04 | Personalizado no negativo, suma exacta, cero válido; negativos, rango, duplicados e importes inválidos rechazados | Reparto.personalizado / Dinero.parsear y FlowChecks; overflow por Math.addExact | CUMPLE |
| AC-05 | Consulta persistente por contacto; reparto único por compra; sin pagos parciales ni movimientos contables automáticos | DeudaRepository con comprobación de reparto previo, FK, PK y transacción; RepartoActivity oculta guardar al consultar | CUMPLE |
| AC-06 | SQLite v3 aditiva conserva cuentas, movimientos, contactos, compras, detalles | LocalCashDbHelper onUpgrade; FlowChecks MIG-01 (v1→v3), MIG-02 (v2→v3 con snapshot de las cinco tablas), foreign_key_check | CUMPLE |
| AC-07 | Build y tests del SHA exacto | Actions run 38440658476, head_sha=45c70f7..., job 115446054645 SUCCESS; BUILD SUCCESSFUL, exit-code 0, comprobaciones de dominio PASS | CUMPLE |
| AC-08 | UI Java/XML, recorrido y persistencia tras relanzamiento | FlowChecks en API36 x86_64 y `shared-relaunch-checks.py`; marcadores LOCALCASH_SHARED_PASS y LOCALCASH_SHARED_RESTART_PASS en log original | CUMPLE |
| AC-09 | Handoff UTF-8, evidencia, alcance y documentación | Handoff, docs/testing, DATABASE, DOMAIN_MODEL, verificación JSON y artefacto Actions | CUMPLE, con observación documental no bloqueante |

## Matriz de calidad Q01–Q08

| ID | Dimensión | Peso | Estado | Puntos | Evidencia y observación |
|---|---|---:|---|---:|---|
| Q01 | Requisitos y aceptación | 25% | CUMPLE | 25 | AC-01–09; LC-SHARED/DEBT; PDF y Task Brief respetados |
| Q02 | Completitud funcional | 15% | CUMPLE | 15 | Flujo de compra a reparto, consulta y errores probados |
| Q03 | Arquitectura y POO | 15% | CUMPLE | 15 | Cálculos en Reparto, orquestación en Service, SQLite en Repository/Helper, sin SQL en Activity |
| Q04 | Calidad y mantenibilidad | 10% | CUMPLE | 10 | Clases acotadas, invariantes, errores controlados; complejidad proporcionada al proyecto |
| Q05 | Persistencia e integridad | 10% | CUMPLE | 10 | V3 aditiva, FK/PK/CHECK, rollback de segundo insert, revalidación persistida, sin duplicar total |
| Q06 | Build, pruebas y regresión | 15% | CUMPLE | 15 | Build original y 5.460 checks; migración, emulador y force-stop |
| Q07 | Alcance y dependencias | 5% | CUMPLE | 5 | No backend/Room/Compose/DI; sin cambios contables ni funcionalidades T06/T07 |
| Q08 | Documentación y handoff | 5% | PARCIAL | 2,5 | Handoff/evidencias correctos; permanecen apartados preliminares de modelo/BD susceptibles de confundirse con el esquema vigente (H-01) |

**Puntaje diagnóstico normalizado: 97,5 / 100.** No compensa gates críticos; estos se comprobaron por separado.

## Validación verificada

### Build y SHA

- **Run:** https://github.com/aik1a/LocalCash/actions/runs/38440658476
- **Job original:** `115446054645`, concluido `success`. HEAD y `GITHUB_SHA` coinciden con `45c70f7efa2bb2ffcdc2b7cb7af78c16ca1c1c50`.
- **Comando:** `bash gradlew --no-daemon --console=plain :app:assembleDebug :app:assembleDebugAndroidTest`.
- **Resultado original en log:** `BUILD SUCCESSFUL in 50s`.
- **Código de retorno comprobado:** `0`, también en `docs/handoffs/evidence/S00-T05/exit-code.txt`. El workflow preserva `PIPESTATUS[0]` del comando Gradle.
- **APK:** `app-debug.apk` (87.011 bytes), SHA256 `da65e2f98e18c28dcbf670a2262df223ba30f304a377876ca8c938c289c87238`.
- **Artefacto original CI:** ID `11836165654`, hash ZIP `2a6706391617e6763fc09ece1a465968781c9b40e5c12454ffc2d33a93bc8e18`; no expirado al verificar.

### Pruebas de dominio

- **Comando:** `bash tests/domain-checks.sh`.
- **Log original:** PASS 18 (cuentas/dinero), 15 (contactos/compras), **5.427** (reparto); total **5.460** comprobaciones.
- **Casos leídos en fuente:** 10001 centésimas / 3 IDs desordenados, reparto personalizado, cuota cero, totales cero y máximo long, duplicados, participantes insuficientes, negativos, suma desigual, overflow, planificada/no persistida, inmutabilidad, conservación con totales 0..299 y grupos 2..10.

### Integración SQLite y emulador

- **Entorno del run:** emulador Android API 36, x86_64, google_apis. Instrumentación `FlowChecks`.
- **Resultados del log original:** `LOCALCASH_FLOW_PASS`, `LOCALCASH_PURCHASE_PASS`, `LOCALCASH_SHARED_PASS`, `LOCALCASH_SHARED_RESTART_PASS`.
- **Migración:** fixture de esquema v2 obtenido en emulador desde los datos de prueba previos, retirando tabla deudas y fijando versión 2; snapshot de cuentas/movimientos/contactos/compras/detalle_compra coincide después del upgrade. Adicionalmente v1→v3 preserva cuentas/movimientos.
- **Integridad:** FK de compra/contacto, PK para asignación duplicada, CHECK de deuda negativa, `PRAGMA foreign_key_check`, trigger que aborta el segundo insert y comprueba rollback completo, revalidación de total persistido, contacto inexistente, compra planificada y reparto duplicado.
- **UI:** desde compra REALIZADA, selección de 2+, formulario equitativo/personalizado; se muestran importes y PENDIENTE; se prueban mensajes visibles para entradas inválidas.
- **Relanzamiento:** `adb shell am force-stop` + arranque frío + `shared-relaunch-checks.py`: ambas deudas y saldo 125,50 siguen visibles y no se permite guardar otro reparto. Verificación observada en el log original.

Los sondeos adb con exit 1 durante el arranque del emulador fueron **transitorios**; éste terminó arrancando y los flujos se completaron exitosamente. No se reinterpretan como fracaso de pruebas.

## Hallazgos

| ID | Severidad | Criterio | Evidencia | Acción necesaria |
|---|---|---|---|---|
| H-01 | DOCUMENTATION | Q08 | `DOMAIN_MODEL.md` sigue incluyendo relaciones conceptuales preliminares con `Participante`/`Asignacion` separadas y `DATABASE.md` conserva decisiones T05 rotuladas pendientes; las secciones explícitas de implementación v3 sí describen correctamente la tabla única `deudas`. | Aclarar o retirar los apartados históricos durante consolidación documental, preferentemente T07. **No es condición de merge de T05**. |

**BLOCKER abiertos:** 0. **MAJOR abiertos:** 0. **Correcciones funcionales obligatorias para T05:** ninguna.

## Gates críticos

| Gate | Resultado | Fundamento |
|---|---|---|
| Requisitos obligatorios de T05 | PASS | LC-SHARED-01/02, LC-DEBT-01/02 satisfechos dentro del alcance autorizado |
| Criterios esenciales | PASS | AC-01–08 con fuente, prueba y flujo verificable |
| Build del SHA exacto | PASS | Head SHA comprobado, BUILD SUCCESSFUL, exit 0 |
| Tests requeridos | PASS | Dominio, SQLite, migración, UI, regresión y relanzamiento del run original |
| Arquitectura/decisiones canónicas | PASS | Java/XML, dominio/repositorio/helper, tabla mínima aprobada |
| Integridad y seguridad aplicable | PASS | FK, PK, CHECK, transacción, sin pérdidas de datos ni escrituras parciales observadas |
| Alcance y dependencias | PASS | Diff acotado, sin tecnologías no aprobadas |

## Veredicto y autorizaciones

**VEREDICTO: `PASS WITH OBSERVATIONS`**

- **Commit:** `APPROVED_FOR_COMMIT` (implementación ya registrada).
- **Merge:** `APPROVED_FOR_MERGE` (autorización **técnica**, sin ejecutar merge).
- **Avance/cierre integral de S00:** `NOT_APPROVED_FOR_SPRINT_ADVANCE` como gate global: T06 y T07 aún pendientes, fuera del alcance de esta auditoría. El Orquestador puede preparar T06 después de formalizar T05; el presente veredicto no cierra S00.

## Observaciones no bloqueantes y riesgos residuales

- H-01: actualizar apartados preliminares de documentación cuando se consolide el modelo, sin rediseño de esquema.
- **Firma APK heredada T03/T04:** no se probó actualización real de APK conservando datos entre firmas distintas; corresponde a S00-T07, no invalida migración interna SQLite v2→v3 en CI. No desinstalar aplicaciones con datos.
- **Dispositivo físico:** no ejecutado. Emulador CI sí validó persistencia y flujo; prueba física no es gate de T05.
- **Fixture v2:** prueba de migración de esquema y preservación de filas, no prueba de instalación incremental de APK con firmas diferentes.
- Advertencias Gradle deprecadas y comprobaciones adb transitorias no afectaron el build ni las verificaciones.
- Las pruebas de fixture que reinician base de datos deben ejecutarse únicamente en emulador desechable.

## Conclusión y handoff al Orquestador

El incremento S00-T05 cumple el flujo académico solicitado: reparte compras REALIZADAS entre contactos con centésimas exactas, valida repartos personalizados, registra deudas atómicamente en SQLite v3 y recupera importes tras relanzamiento. El run original del SHA funcional verifica build, dominio, integridad, migración y regresión relacionada. Solo subsiste H-01 documental no bloqueante.

**Autorización técnica de merge concedida al Orquestador:** `APPROVED_FOR_MERGE`. El Auditor **no realizó merge, no modificó Java/XML/Gradle, no cerró S00-T05, no actualizó `PROJECT_STATE.md` ni inició S00-T06**.
