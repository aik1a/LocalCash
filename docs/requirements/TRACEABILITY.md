# Matriz de trazabilidad

**Revisión T07C: 2026-10-18.** Fuente normativa: PDF docente, páginas 3–6; traducción canónica en [PROJECT_SPEC](PROJECT_SPEC.md). Se revisa documentación, no requisitos ni estados administrativos. Base inspeccionada: 9084b573ebe055485e3cdda11ca8db2d8df6a3bc.

La tabla siguiente refleja el incremento integrado. Las entradas históricas al final conservan el estado de cada fecha; no son pendientes vigentes por sí mismas.

## Estado contrastado actual

Rutas de código abreviadas por clase: clases presentation/application/data/domain bajo app/src/main/java/com/aik1a/localcash/. [Arquitectura canónica](../../ARCHITECTURE.md) · [Esquema SQLite](../design/DATABASE.md).

| Requisito | Correspondencia docente | Código/documento real | Evidencia | Alcance y límite |
|---|---|---|---|---|
| LC-P01 | Java nativo | app/build.gradle; presentation y layouts XML | Build T07A, ambos assemble exit 0 | Implementado; Sync Studio limpio NOT TESTED |
| LC-P02 / LC-TECH-01 | SQLite local | LocalCashDbHelper v4; seis tablas; repositories | T07A, snapshots/migraciones/rollback/reinicio API36 | Implementado; upgrade APK con datos NOT TESTED |
| LC-P03 / LC-AUTH-01 / LC-TECH-02 | Biometría Android | AccesoActivity, ActividadProtegida, LocalCashApplication; ocho rutas | T07A API24/API36, sensor emulado y rutas directas | Implementado y probado en AVD; físico NOT TESTED |
| LC-P04 / LC-MAP-01 / LC-TECH-03 | Mapa interactivo offline | MapaActivity, MapaLocalView, AtlasOffline; osmdroid 6.1.18 | T06B y T07A API24/API36: base real, marcadores, offline/lifecycle | Implementado; Santiago Centro z14–16 |
| LC-CON-01 | Directorio local | Contacto, ContactoRepository, ContactosActivity | T07A purchase-flow.log y relanzamiento API36 | Crear/listar y vínculo local implementados |
| LC-ACC-01 | Cuentas múltiples | Cuenta, CuentaRepository, MovimientoService, MainActivity | T07A android-flow.log: dos cuentas/saldos API36 | Crear/listar/consultar saldo; sin catálogo de tipos ni edición/borrado general |
| LC-MOV-01 | Ingresos/egresos | Movimiento, MovimientoService, MovimientoRepository, CuentaActivity | T07A android-flow.log, invalidaciones y persistencia API36 | Implementado; saldo derivado |
| LC-MOV-02 | Ubicación opcional | Ubicacion, CapturaUbicacion; columnas latitud/longitud | T07A location-flow.log, permisos/fix/timeout/reinicio API36 | Implementado; exclusivamente aproximada NOT TESTED |
| LC-BUY-01 | Compras/historial/ítems | Compra, DetalleCompra, CompraRepository y Activities | T07A purchase-flow.log y restart-checks.log API36 | Implementado; sin cargo automático a cuentas |
| LC-BUY-02 | Adquisiciones futuras | Compra.Estado.PLANIFICADA, fecha, NuevaCompraActivity | T07A fecha futura/total estimado API36 | Implementado como registro planificado; sin recordatorios |
| LC-BUY-03 | Vínculo con contacto | compra.contacto_id opcional; CompraService | T07A compra realizada con Ana y planificada sin contacto API36 | Vínculo local implementado; no envío/notificación remota |
| LC-SHARED-01 | Compra compartida detallada | DetalleCompra, CompraService, RepartoActivity | T07A shared-flow.log API36; ítems y total real | Implementado; se reparte total, no ítems individualmente |
| LC-SHARED-02 | Participantes desde agenda | ContactoRepository, DeudaService, selección explícita | T07A 2+ contactos distintos seleccionados API36 | Implementado; contacto de compra no se añade automáticamente |
| LC-DEBT-01 | Deuda por participante | Deuda, DeudaRepository, tabla deudas en v4 | T07A FK/PK/CHECK/rollback/reinicio API36 | Monto PENDIENTE persistido; sin pago parcial ni marcar pagada |
| LC-DEBT-02 | Reparto equitativo/personalizado | Reparto y DeudaService | T07A 100,01/3 y 20+30+50; 5427 checks reparto por job | Implementado; suma exacta y orden de resto por ID |
| LC-TECH-04 | Código fuente completo Git | Repositorio Android: app, wrapper, scripts y recursos | Árbol Git y código inspeccionados T07C; base posterior PR #10 | Disponible en repositorio privado con acceso autorizado |
| LC-TECH-05 | Documentación técnica | ARCHITECTURE.md; docs/design/DATABASE.md; docs/delivery/BUILD.md | Contraste T07C con código, PDF y CI; historial conservado | Documentación actualizada; auditoría independiente T07C pendiente |
| LC-TECH-06 | Pauta de exposición | docs/delivery/PRESENTATION.md y S00-final-delivery.md | Guion/datos esperados/checklist T07C | Material preparado; exposición/recorrido manual humano NOT TESTED |

## Referencias de validación y límites transversales

- [CI T07A 39046939759](https://github.com/aik1a/LocalCash/actions/runs/39046939759), SHA 32dd6e0ff09143bfc55276d5084c18472769b4af: ambos assemble PASS/0 y 5483 comprobaciones por API24/API36; [informe](../testing/S00-T07A.md) y [auditoría](../audits/S00/S00-T07A-audit.md).
- [CI T06B 39042930625](https://github.com/aik1a/LocalCash/actions/runs/39042930625): cartografía real OSM y gate biométrico; [auditoría](../audits/S00/S00-T06B-audit.md).
- [CI T07B 39082526083](https://github.com/aik1a/LocalCash/actions/runs/39082526083), SHA ece84d4cc7f3eee816f24626094597e43883e169: diez certificados principales distintos, cero pares compatibles entre inventariados. Actualización in-place **BLOCKED_SIGNER_MISMATCH**; conservación SQLite después de actualizar **NOT TESTED**. [Informe](../testing/S00-T07B.md) y [auditoría](../audits/S00/S00-T07B-audit.md).
- Regresión financiera/GPS completa API24: NOT TESTED. Dispositivo físico: NOT TESTED. GPS exclusivamente aproximado: NOT TESTED. Sync Android Studio desde entorno limpio: NOT TESTED. Exposición/recorrido manual humano: NOT TESTED.
- “Implementado” identifica código contrastado y los escenarios probados; no significa cumplimiento exhaustivo en toda plataforma ni cierre de S00.

El PDF permite enlace de compra con contacto para notificar **o** mantener registro conjunto: se implementa vínculo local, no notificación remota. “Programar” se materializa como estado/fecha futura, sin temporizador/recordatorio. El reparto del total mantiene los productos detallados conforme a la decisión mínima aprobada T05. La “privacidad absoluta” del resumen docente no se transforma en garantía técnica: hay gate Android/almacenamiento local, no cifrado de SQLite declarado.

## Comprobación de los entregables académicos

| PDF p.6 | Entrega versionada | Estado documental |
|---|---|---|
| Repositorio GitHub con código completo Java/SQLite/biometría/mapas/módulos | Repositorio y rutas de código; acceso autorizado al repo privado | Disponible |
| Arquitectura, estructura SQLite, dependencias y compilación/ejecución | ARCHITECTURE, DATABASE y BUILD enlazados arriba | Actualizado T07C, pendiente auditoría |
| Pauta de exposición y demostración offline | [PRESENTATION](../delivery/PRESENTATION.md) y [paquete académico](../delivery/S00-final-delivery.md) | Preparado; exposición no ejecutada |

## Registro histórico — conservar contexto de cada fecha

Las referencias/pendientes de esta sección describen la fecha de su registro. La tabla actual y los informes T07A/T07B anteriores prevalecen para conocer lo implementado. Se corrige únicamente el enlace de T06B al brief ya archivado; no se cambian resultados ni fechas históricos.

## Cierre S00-T01 — 2026-10-12

- Commit funcional auditado: `b63e5e2c4de6accb9c952022f12b07a0b614d62d`.
- Gate: PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE en `docs/audits/S00/S00-T01-reaudit.md`.
- Head integrado: `7288b9f6b40bdcb3c77385e9e138acaff96973d7`; respecto del auditado, solo añadió el informe de reauditoría.
- Merge: `3cb3d5a164bc99e5343f283efa44c4a9211426b3`, PR #1. Task Brief archivado en `docs/tasks/completed/S00-T01-proyecto-android-base.md`.
- LC-P01: Java nativo implementado en el proyecto base. No se considera completado el conjunto de funcionalidades de LocalCash.
- BUILD-01: compilación histórica del Codificador, `BUILD SUCCESSFUL in 25s`, salida 0; evidencia `docs/handoffs/evidence/S00-T01/clean-build-tests.txt`. Tests: `NO-SOURCE`; sin ejecución nueva ni certificación de caché vacía.

| Validación pendiente | Estado real | Tarea responsable | Evidencia inicial |
|---|---|---|---|
| Instalación, MainActivity, texto «LocalCash» y errores de inicio (OBS-02) | NO EJECUTADO | S00-T06 | `manual.txt`, `revalidation-blocker.txt` bajo evidencia S00-T01 |
| Sync Android Studio / checkout y caché vacía (OBS-03) | NO EJECUTADO | S00-T06 | `BUILD.md`, `revalidation-blocker.txt` |
| Deprecación Kotlin integrado (OBS-04) | Riesgo aceptado/documentado | Seguimiento en S00-T06 | `BUILD.md`, reauditoría S00-T01 |

S00-T02 fue cerrada el 2026-10-12; S00-T03 quedó READY. El sprint S00 continúa abierto.

## Cierre S00-T02 — 2026-10-12

- Código auditado: `6cea41252f26a76af44f81f990a99f0b95cf74b1`.
- Validación BUILD-01: [Actions run 38267899607](https://github.com/aik1a/LocalCash/actions/runs/38267899607), checkout del SHA exacto; `:app:assembleDebug` finaliza con `BUILD SUCCESSFUL in 1m 8s`, 33 tareas, código 0. APK publicado como artefacto.
- Gate: `docs/audits/S00/S00-T02-reaudit-H01.md`, PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE.
- Merge: [PR #2](https://github.com/aik1a/LocalCash/pull/2), `d39a35b99fe96349f06052dff01163957ed54c4f`.
- Estructura: `com.aik1a.localcash.presentation.MainActivity`, `docs/design/PACKAGE_STRUCTURE.md`.
- **Pendiente:** H-02, ejecución en dispositivo/emulador, incorporada a S00-T06 y no declarada PASS.
- El workflow S00-T02 fija el SHA auditado: no confirma builds de otros commits.

## S00-T03 — Cierre 2026-10-12

- `eea0fbfc895f5d5e2cde6f3512407f3cc9c16cec`: [CI y emulador PASS](https://github.com/aik1a/LocalCash/actions/runs/38283139884), 18 comprobaciones de dominio PASS, reinicio con SQLite persistente.
- Gate `docs/audits/S00/S00-T03-audit.md` PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE.
- [PR #3](https://github.com/aik1a/LocalCash/pull/3), merge `f484642361c20246eb07dfc030a0e41e01b76647`.
- Pendiente: migración SQLite aditiva en T04; dispositivo físico y Android Studio en T07.

## Cierre S00-T04 — 2026-10-13

- SHA compilado `76480f7639a4ef2e2ec3121817307605ff9843d2`; [Actions run 38289787487](https://github.com/aik1a/LocalCash/actions/runs/38289787487): build PASS, 33 pruebas dominio, migración v1→v2/regresión/flujo UI/reinicio PASS.
- Auditoría `docs/audits/S00/S00-T04-audit.md`: PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE.
- [PR #4](https://github.com/aik1a/LocalCash/pull/4), merge `93d52298319a5cb57fd902d7d52b513fee7918c4`; Task Brief archivado.
- Límite importante: actualización APK T03→T04 con certificados diferentes NO VALIDADA; T07 debe resolver firma. Migración SQLite interna sí probada.
- Compras realizadas NO generan movimientos financieros automáticos (regla pendiente). Repartos T05 pendientes.

## Cierre S00-T05 — 2026-10-13

- **Código verificado:** `45c70f7efa2bb2ffcdc2b7cb7af78c16ca1c1c50`; CI [run 38440658476](https://github.com/aik1a/LocalCash/actions/runs/38440658476): `BUILD SUCCESSFUL`, exit 0, 5.460 comprobaciones de dominio PASS, emulador Android API 36 y SQLite v2→v3 / relanzamiento PASS.
- **Auditoría independiente:** `docs/audits/S00/S00-T05-audit.md` — PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE; H-01 documental no bloqueante para S00-T07.
- **Merge a main:** [PR #5](https://github.com/aik1a/LocalCash/pull/5), `5c9e9c86c9913f6079b684a150c86d783d48c189`.
- **Implementado:** LC-SHARED-01/02, LC-DEBT-01/02; las deudas no generan movimientos y pagos parciales no pertenecen a T05. Task Brief en `docs/tasks/completed/S00-T05-gastos-compartidos.md`.
- **Pendiente:** LC-AUTH-01 y LC-P03; LC-MOV-02; LC-MAP-01 y LC-P04; en S00-T06. Entrega final, firma de APK y H-01 en S00-T07.

## Cierre S00-T06A — 2026-10-14

- **Código compilado/probado:** `ebc985dedfd61797bcf104b73b24ca9c6c926a1b`; [CI original 38498296734](https://github.com/aik1a/LocalCash/actions/runs/38498296734): build exit 0, 5.483 comprobaciones dominio, migraciones v1/v2/v3→v4, permisos/GPS/fallback y relanzamiento API36 PASS.
- **Auditoría independiente:** `docs/audits/S00/S00-T06A-audit.md` — `PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE`; observación documental H-01 para T07.
- **Merge:** [PR #6](https://github.com/aik1a/LocalCash/pull/6), `b0f0d06f1d8837e85e80b75aa62e806a090f420b`. Brief archivado en `docs/tasks/completed/S00-T06A-ubicacion.md`.
- **Siguiente autorizado:** `S00-T06C`, biometría Java/AndroidX, mientras `S00-T06B` conserva decisión cartográfica offline pendiente. El sprint S00 no se cierra.

## Cierre S00-T06C y habilitación T06B — 2026-10-16

- **Biometría (LC-P03/AUTH-01/TECH-02):** código funcional SHA `0f79e6c57be54144b1a756b3aff3a9644b025ca8`; [CI 38625086287](https://github.com/aik1a/LocalCash/actions/runs/38625086287) API24 y API36: build exit 0, 5483 comprobaciones dominio cada job, sesión, prompt, siete Activities, cancelación/error/reentrada y regreso de segundo plano PASS, regresión T03–T06A PASS en API36. [Auditoría T06C](../audits/S00/S00-T06C-audit.md): PASS WITH OBSERVATIONS/APPROVED_FOR_MERGE. [PR #7](https://github.com/aik1a/LocalCash/pull/7) fusionado, `1c944cde59ed6aba8e8af2f9529b29435e40af59`. Brief en `docs/tasks/completed/S00-T06C-biometria.md`.
- **Mapas (LC-P04/MAP-01/TECH-03):** diseño aprobado por [ADR-002](../design/decisions/ADR-002-osmdroid-cartografia-offline.md) y [Task Brief T06B](../tasks/completed/S00-T06B-mapas.md) READY. La cartografía **no está generada ni implementada**; no marcar cumplimiento MAP-01 hasta aportar ZIP offline y CI de instalación limpia sin red.
- **T07:** pendientes firma/upgrade, validación integral, soporte físico cuando disponible, corrección de discrepancias documentales heredadas y entrega.

## Cierre S00-T06B y habilitación T07 — 2026-10-17

- **SHA funcional auditado:** `c289b0614d9d891c6ea4cfcdeec48dabc342c459`; [CI 39042930625](https://github.com/aik1a/LocalCash/actions/runs/39042930625) API24/API36 PASS, 5483 checks de dominio por job, mapa offline y biometría PASS.
- **Auditoría independiente:** `docs/audits/S00/S00-T06B-audit.md` — PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE, 97,5/100, sin BLOCKER/MAJOR. Informe commit `b3dcef5577c9626bfcd5da7216054a5025f8ae6e`.
- **Merge:** [PR #8](https://github.com/aik1a/LocalCash/pull/8), `f1664ec4d87bf16cd9f7668cbda62e0ef0f406e9`.
- **Implementado:** LC-P04, LC-MAP-01, LC-TECH-03; cartografía OSM real offline Santiago Centro z14–16, marcadores de movimientos georreferenciados de la cuenta y gate biométrico.
- **Límites:** cobertura geográfica de demostración; dispositivo físico no probado; firma/actualización de APK con conservación de datos pendiente de T07. H-01 documental no bloqueante (ARCHITECTURE.md, BUILD.md) para T07.
- **S00 permanece IN_PROGRESS.** T07 es la próxima tarea de integración/cierre, sujeta a Task Brief y gate integral independiente.


## Incremento solicitado S01-T01 — 18-10-2026 (rama de trabajo, no integrado)

El cierre y las evidencias de S00 anteriores se conservan. Aikia autoriza en conversación
una ampliación funcional; no se presenta como exigencia literal del PDF docente.

| Cambio | Requisitos vinculados | Implementación | Evidencia actual |
|---|---|---|---|
| Moneda principal inicial persistida | LC-P02, LC-AUTH-01 | ActividadProtegida, ConfiguracionRepository, esquema v5 | Tests de SQLite; validación Android en curso |
| Divisa por cuenta/compra y precisión | LC-ACC-01, LC-MOV-01, LC-BUY-01 | Divisa, Dinero, Cuenta/Compra, formularios y repositorios | 28.917 checks específicos de dominio; SQL real sobre host |
| Migración sin conversión ni pérdida | LC-TECH-01 | v1–v4→v5 aditiva, asignación explícita | 55 checks SQLite host; CurrencyChecks preparado para Android |
| Tú + un contacto y cuota propia | LC-SHARED-02, LC-DEBT-01/02 | Reparto, DeudaService/Repository, reparto_usuario | Suma y precisión probadas; persistencia/UX Android en curso |
| Etiquetas y nombres legibles | LC-CON-01, LC-BUY-01 | Sin IDs visibles; etiquetas simples y ejemplos | XML validado; smoke UI preparado |

PR: https://github.com/aik1a/LocalCash/pull/13. Ver handoff S01-T01 y CI de la rama
para resultado final; auditoría independiente y merge aún pendientes.
