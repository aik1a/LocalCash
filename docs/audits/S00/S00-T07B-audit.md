# Auditoría — S00-T07B: firma APK, actualización y conservación de datos

## Identificación

- Rol: Agente Auditor independiente; única auditoría de tarea, sin implementación ni correcciones.
- Fecha: 2026-10-18, America/Santiago.
- Repositorio: aik1a/LocalCash.
- Rama: task/S00-T07B-firma-upgrade.
- SHA final auditado: `2285deaf1e5f03754b9d705ebd7713087c1d4bc6`.
- SHA técnico ejecutado: `ece84d4cc7f3eee816f24626094597e43883e169`.
- Base main y merge-base contrastados: `68e853fc4cc726500bb03dcbf9877a3a1e1936e2`.
- SHA del commit documental inicial de esta auditoría: `95033dae0012f7a9d0c79ba3086cbc83184e13e5`.
- Task Brief: docs/tasks/active/S00-T07B-firma-upgrade.md.
- Handoff: docs/handoffs/S00-T07B-coder.md.
- Tipo: gate de integración T07B; no auditoría integral T07D ni cierre S00.

## Fuentes y alcance real

Lectura directa desde GitHub al SHA auditado: PROJECT_ROUTER, AGENTS, PROJECT_STATE, rol AUDITOR, WORKFLOW, TASK_LIFECYCLE, CONTEXT_PACKS, AUDIT_MATRIX, AUDIT_TEMPLATE, brief T07B, informe y handoff T07B, auditoría T07A, PROJECT_SPEC, ARCHITECTURE, sprint S00, DATABASE y docs/delivery/BUILD.md. Inspección de app/build.gradle, AndroidManifest.xml, LocalCashDbHelper, workflow T07B, inventory.py, source-artifacts.json y evidencia original.

La comparación main→SHA final solo añade workflow, herramienta/lista de fuentes, informe, handoff y evidencia. No modifica producción, esquema SQLite v4, dependencias, firma, requisitos, ADR ni PROJECT_STATE. La comparación SHA técnico→SHA final solo añade documentación/evidencias: el código ejecutado es el revisado. La rama apuntaba al SHA final solicitado antes de registrar esta auditoría.

La imagen arquitectónica adjunta no es necesaria para este gate; no se usa como prueba de implementación o firma. El juicio se limita al brief operativo y a los requisitos relacionados LC-P02, LC-TECH-01/04/05, LC-ACC-01 y LC-MOV-01; no revalida íntegramente los módulos heredados.

## Integridad, procedencia y verificación independiente

CI final: [run 39082526083](https://github.com/aik1a/LocalCash/actions/runs/39082526083), job 117542262210, intento 1, success, head exacto ece84d4. Checkout, comparación git y validated-sha.txt coinciden. No se atribuye esta ejecución al commit documental posterior ni se afirma un nuevo build APK.

Se descargó el [artefacto original 12119172237](https://github.com/aik1a/LocalCash/actions/runs/39082526083/artifacts/12119172237), 43171666 bytes. SHA256 calculado independientemente:

`73d1f7d482de6f64d6a48a6e7dbdcb47494d4ff39643c91e02b287b2dc498483`

Resultado propio Python/zipfile/hashlib/struct/cryptography: **PASS, exit 0**.

- CRC del ZIP final y los diez ZIP históricos internos: correctos.
- Metadatos originales consultados de los siete runs históricos aceptados: IDs, nombres, tamaños, digest, run y head SHA coinciden con las diez fuentes fijadas.
- SHA256 recalculado de cada ZIP histórico coincide con su digest original de Actions. Esto acredita los originales internos sin depender de sus nombres.
- Los 14 APK extraídos coinciden byte a byte con su entrada del ZIP histórico y con su hash de inventario.
- Certificado DER extraído independientemente del bloque de firma v2 de cada APK: SHA256 coincide con la salida de apksigner y el inventario. Certificados parseados con cryptography.
- Se comprobó un firmante por APK y ausencia de bloques v3/v3.1 en los bytes examinados. Los logs también declaran v2 true y v1/v3/v3.1/v4 false; no se acredita lineage de rotación.
- 14 logs apksigner y 14 aapt con sus 28 exit codes 0; metadatos y fingerprints contrastados.
- 61 archivos versionados coinciden por hash Git blob con el ZIP original: 56 logs/retornos y cinco archivos de inventario/estado/SHA. Ambos logs completos de jobs recuperados directamente coinciden además con sus copias Git: 63 archivos originales contrastados.
- Matriz recalculada: diez aplicaciones, diez certificados únicos y exactamente 90 pares dirigidos distintos; todos same_package=true, same_signer=false, compatible=false, SIGNER_MISMATCH.

La comprobación local verifica bytes, estructura y certificados DER; **no es una segunda verificación criptográfica de firmas**. apksigner/aapt se ejecutaron en la CI original contrastada. Localmente hay Java, pero no apksigner, aapt, adb ni emulator. No se repitió CI ni se abrió una sesión Android.

### Identidad de los diez APK principales

Todos: package `com.aik1a.localcash`, versionCode 1, versionName 1.0, un firmante, firma v2. El número API identifica el job de validación y no otro package ni minSdk.

| Entrega / artefacto | SHA256 APK | SHA256 certificado |
|---|---|---|
| T03 / 11762057450 | 840113ae00deaa046b2d02a62d8f021c1f6c468f26ff105d5da0c09c56dae603 | 663ca35e997038317416faa5be219126ea1350b8a7c199aff4be75cd8062a224 |
| T04 / 11766425002 | d15f90d9780fb9c073f3b5dcf0c1179c72db5e6ca832118080417c7a81cdf5fe | 9c8254a44b7df2bc456bb93eb0cdd3726b80ecc0ea434dbfbdbc2e38ff6a7caf |
| T05 / 11836165654 | da65e2f98e18c28dcbf670a2262df223ba30f304a377876ca8c938c289c87238 | 9b5ddf94b5ecfc900bc357e4eb4fd22e10ca33140db96017ecb0edb2e363f4c2 |
| T06A / 11863884007 | e2c9c934ddd285e4dabc5587f3751c35d93070eba92a2077437489aeae8c8117 | e11799f7c93d1d0cf38b831e3ec29e359347a33a36e17196f674d09c080cd4aa |
| T06C-24 / 11924252922 | 9a0ae95f02ff2fd0f1da72b6c93009fdff85dfc22d465e0b7c6d4ac313278e71 | 683d4526c087d2b5f9d3669b8124ebf46a035cc1ce579c41758239fb39cab50b |
| T06C-36 / 11924053647 | aa136956836bf9ca8839bff6cf81e1e911e927f5f9394a76bd8fa9e48c9b6c1d | dabb07e4c5f2eafa52dafd87f45719ced87000d68d2d7c609121c31e00ac25ce |
| T06B-24 / 12104783307 | 0e21dd4436ad24412923b590ff621e3601bf533435fe9c72baaa3cd0491a5a79 | 2537053221563de33121b9085c8102facaa10de98d021951a6dd7ac5b957ffa0 |
| T06B-36 / 12104643986 | c4161293f2b0f1bb7f145c5bb13ec7455b150ffff0d7867277a00a7a154384db | acabb162a6cb0ad28064b5ed9d8a275c58ac008f00f6753c53ba6ed4b9890ae9 |
| T07A-24 / 12106073736 | 90c177bd383da6d7d85f0fa048f29d8997d32298b5095dd86f550d873cd606ac | 3c57bd7d9da58cc64c693700640903f95fc83fcaebe50ea2ad56a97ff605194b |
| T07A-36 / 12105649198 | f6c188ae06db78a6068b64b69157b4fcfe776b6009561a1a0e9269086e9a83d0 | 49d73d9652d1cbf9c9891fb90a3d01fc94a3f2a6dcb02aea8a20471b927f191a |

Procedencia contrastada con API original de artefactos:

| Entrega | Run | SHA original |
|---|---|---|
| T03 | 38283139884 | eea0fbfc895f5d5e2cde6f3512407f3cc9c16cec |
| T04 | 38289787487 | 76480f7639a4ef2e2ec3121817307605ff9843d2 |
| T05 | 38440658476 | 45c70f7efa2bb2ffcdc2b7cb7af78c16ca1c1c50 |
| T06A | 38498296734 | ebc985dedfd61797bcf104b73b24ca9c6c926a1b |
| T06C (ambas APIs) | 38625086287 | 0f79e6c57be54144b1a756b3aff3a9644b025ca8 |
| T06B (ambas APIs) | 39042930625 | c289b0614d9d891c6ea4cfcdeec48dabc342c459 |
| T07A (ambas APIs) | 39046939759 | 32dd6e0ff09143bfc55276d5084c18472769b4af |

Los cuatro APK de instrumentación son `com.aik1a.localcash.test`, con versionCode/versionName vacíos en aapt y version_code=null en JSON. Sus hashes son d8f072ab…89191, 708e4fc0…a1de, 567520c3…304 y 025c8997…14d, correspondientes a los cuatro artefactos T06B/T07A; sus fingerprints completos coinciden con la aplicación de su mismo job. Se verificaron los valores completos contra bytes y logs, no solo estos prefijos abreviados. Se excluyen correctamente de los 90 pares y de la entrega.

Ni DN Android Debug, ni package, ni versionCode compartidos prueban compatibilidad. Los dos APK principales del mismo run también difieren. La diagonal representa el mismo archivo y no una actualización histórica.

## Workflow, herramienta y fallo inicial

El workflow usa permisos contents/actions read, checkout del SHA exacto y Build Tools 36.0.0. No ejecuta Gradle ni genera keystore. inventory.py descarga las fuentes fijadas, valida SHA256/CRC, extrae APK, exige comandos exitosos, conserva retornos y genera la matriz. Retira Authorization al redirigir la descarga; no guarda la credencial en evidencia. No se detectan claves privadas incorporadas en el diff.

Primer [run 39082443456](https://github.com/aik1a/LocalCash/actions/runs/39082443456), SHA 87381514a895eb5cb651b6b2b821b89a2401cb1d, job 117542015021: **failure**. El log original demuestra rechazo del metadata de instrumentación 12104783307-1 y posterior FileNotFoundError de inventory.json. El primer pipeline ocultó el fallo Python y el step posterior falló; no fue un inventario exitoso.

La versión revisada acepta versión vacía como null para pruebas, mantiene la exigencia numérica para la app y usa shell bash con `set -euo pipefail`. El run final completa los 14 APK, publica inventory.json y declara cero pares compatibles. La corrección queda comprobada por ejecución completa, sin convertir el primer fallo en PASS.

## Actualización, persistencia e instalación

**BLOCKED_SIGNER_MISMATCH está justificado para los diez APK inventariados.** Mismo package y certificados diferentes, sin rotación acreditada: no existe candidato compatible para el ensayo autorizado.

Omitir `adb install -r` fue correcto conforme al alcance 2–3 y a la alternativa de aceptación del brief. No era necesario provocar un rechazo del instalador para acreditar la diferencia de certificados. No existe salida ni retorno adb y no se inventa INSTALL_FAILED_UPDATE_INCOMPATIBLE observado.

**Conservación SQLite tras actualización: NOT TESTED.** No hay snapshot antes/después ni actualización compatible. Las migraciones internas v1/v2/v3→v4 de T07A son evidencia distinta y no resuelven este punto.

Las instrucciones nuevas eligen el APK principal T07A API36 por hash, verifican si el package está instalado y detienen la instalación nueva si ya existe. Exigen contrastar el APK instalado, prohíben desinstalar/pm clear/borrar SQLite/forzar downgrade/re-firmar y no prometen migración. Para un APK de usuario no aportado, el resultado es NO DETERMINADO. Una firma coincidente permitiría planificar una prueba; no garantiza por sí misma conservación de datos.

## Criterios de aceptación

| ID | Criterio | Evidencia | Estado |
|---|---|---|---|
| AC-01 | Inventario/fingerprints contrastables | API original, ZIP, bytes, DER y logs | CUMPLE |
| AC-02 | Upgrade real compatible o bloqueo explícito | 90 pares incompatibles; cero candidatos | CUMPLE por alternativa de bloqueo |
| AC-03 | Declarar preservación solo tras upgrade probado | NOT TESTED explícito, sin PASS falso | CUMPLE; preservación no demostrada |
| AC-04 | Instalación segura y límites | Procedimiento nuevo y detención por firma distinta | CUMPLE |
| AC-05 | Handoff/evidencia versionados, sin secretos | Diff y originales coincidentes | CUMPLE |

## Matriz de calidad

Aplicación de AUDIT_MATRIX al incremento T07B, no a todo el producto.

| ID | Dimensión | Peso | Estado | Evidencia |
|---|---|---:|---|---|
| Q01 | Requisitos/aceptación | 25 | CUMPLE | Cinco criterios y alternativa explícita |
| Q02 | Completitud del diagnóstico | 15 | CUMPLE | Inventario, matriz y procedimiento completos |
| Q03 | Arquitectura/POO | 15 | N/A | Sin cambio productivo; arquitectura respetada |
| Q04 | Mantenibilidad | 10 | CUMPLE | Script acotado, fallos propagados y parser comprobado |
| Q05 | Datos/seguridad | 10 | CUMPLE | Sin borrados, nuevas claves ni falsas garantías |
| Q06 | Pruebas/regresión | 15 | CUMPLE | CI exacta y contraste independiente; upgrade condicionado no aplicable |
| Q07 | Alcance/dependencias | 5 | CUMPLE | Solo infraestructura/docs/evidencia autorizadas |
| Q08 | Documentación/handoff | 5 | PARCIAL | Documentos T07B correctos; BUILD histórico pendiente T07C |

Puntaje diagnóstico normalizado: **97,1/100** (82,5/85). No sustituye gates ni representa cobertura funcional integral.

## Hallazgos

| ID | Severidad | Evidencia | Tratamiento |
|---|---|---|---|
| H-01 | DOCUMENTATION, heredado no bloqueante | docs/delivery/BUILD.md sigue en T01: dice sin dependencias, biometría/osmdroid futuras y MainActivity antigua | T07C debe actualizar la guía general y enlazar límites de firma T07B antes de entregar documentación como vigente |
| R-01 | Riesgo residual, no defecto de aceptación T07B | Diez firmantes distintos; no hay ruta histórica compatible demostrada | Orquestador decide estrategia futura autorizada; no resolver borrando datos ni generando claves en esta auditoría |

**BLOCKER: 0. MAJOR: 0. MINOR nuevos: 0. OUT-OF-SCOPE: 0.** La incompatibilidad es un bloqueo operativo de upgrade admitido por el brief, no un incumplimiento de su alternativa diagnóstica.

## Gates y veredicto

| Gate | Resultado | Justificación |
|---|---|---|
| Requisitos/criterios esenciales T07B | PASS | Diagnóstico contrastado y bloqueo documentado permitido |
| Build productivo nuevo | N/A | No cambia código compilable ni se genera APK |
| Validación de firmas/metadatos | PASS | 28 comandos originales exitosos, bytes/DER contrastados |
| Upgrade real y SQLite después | BLOCKED / NOT TESTED | Sin par compatible; no se afirma PASS |
| Arquitectura/decisiones | PASS | Sin cambios a stack, esquema ni firma privada |
| Integridad/seguridad/alcance | PASS | Originales verificados y procedimiento conservador |

**VEREDICTO: PASS WITH OBSERVATIONS.**

- Commit de entrega auditado: **APPROVED_FOR_COMMIT**.
- Integración del incremento T07B hasta 2285deaf1e5f03754b9d705ebd7713087c1d4bc6: **APPROVED_FOR_MERGE**.
- Cierre integral/avance S00: **NOT_APPROVED_FOR_SPRINT_ADVANCE**.
- Correctivos obligatorios antes de integrar T07B: **ninguno**.
- No se autoriza afirmar actualización histórica exitosa ni conservación SQLite tras upgrade.

El gate favorable acepta el diagnóstico previsto por el brief. No convierte el bloqueo operativo en una funcionalidad terminada. El Orquestador decide la integración y el tratamiento administrativo de T07B.

## Riesgos y entrega al Orquestador

- T07C: actualizar BUILD y discrepancias históricas, identificar APK debug exacto y separar instalación nueva de actualización. Preservar binarios antes de caducidad: el artefacto consolidado T07B expira 2027-01-16T00:22:35Z. No anunciar una firma estable ni exportación/importación inexistentes.
- T07D: conservar upgrade/SQLite tras upgrade como pendientes, valorar explícitamente su efecto en el gate integral y no reutilizar este PASS como autorización de cierre S00. Siguen los límites heredados T07A: cobertura API24 parcial, dispositivo físico, GPS solo aproximado, Studio y caché vacía no demostrados.
- El inventario cubre entregas aceptadas T03–T07A; no cubre todo build temporal ni instalaciones reales no aportadas. Sin APK/certificado del usuario no se extrapola incompatibilidad o compatibilidad.
- Una futura estrategia de firma o nuevo par compatible requiere tarea autorizada y ensayo real con datos de prueba antes/después. No se ejecuta T07C ni se toman decisiones de arquitectura aquí.
- Se registra exclusivamente este informe bajo docs/audits. No se modifica PROJECT_STATE, producción, certificados, requisitos ni ADR; no se hace merge, cierre ni comunicación externa al Orquestador. Este documento y el mensaje de entrega constituyen el handoff para su decisión.

El SHA del primer commit de publicación se registra en una actualización documental posterior de este mismo informe para evitar una referencia imposible al hash del propio commit que lo contiene.
