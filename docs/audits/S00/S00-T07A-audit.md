# Auditoría — S00-T07A: build y regresión integral

## Identificación

- **Rol:** Agente Auditor, revisión técnica de tarea.
- **Fecha:** 2026-10-17, America/Santiago.
- **Repositorio/rama:** aik1a/LocalCash, `task/S00-T07A-integracion`.
- **Base main verificada:** `cc77afc2c676770329eb89e367ea44f8c11c9e1a`.
- **SHA ejecutado y auditado:** `32dd6e0ff09143bfc55276d5084c18472769b4af`.
- **Head documental revisado:** `752107ef11162c8b8543dc3f557ac4e239fceb49`.
- **Task Brief:** `docs/tasks/active/S00-T07A-integracion.md`.
- **Handoff:** `docs/handoffs/S00-T07A-coder.md`.
- **Tipo:** auditoría T07A; no gate integral T07D.

Transparencia de roles: este chat implementó T06B y después verificó la entrega ya existente de T07A, sin escribir su workflow ni código productivo. El usuario solicitó explícitamente el cambio a Auditor. Se aplica la excepción de cambio de rol de `docs/agent/WORKFLOW.md`; esta revisión no se presenta como un agente nuevo sin contexto previo. El juicio de T07A se basa en fuentes originales recuperadas y verificaciones propias, no en la respuesta anterior ni únicamente en el handoff.

## Fuentes y diff

Se leyeron desde GitHub router, AGENTS, PROJECT_STATE, rol Auditor, WORKFLOW, TASK_LIFECYCLE, Task Brief, informe T07A, handoff, PROJECT_SPEC, TRACEABILITY, CONTEXT_PACKS, AUDIT_MATRIX y AUDIT_TEMPLATE. Se contrastaron workflow, configuración Android/manifest y código real de FlowChecks, MapChecks, BiometricChecks, scripts de biometría/mapas/dominio/dispatcher, SQLiteOpenHelper, gate y sesión biométrica y MapView.

La comparación GitHub de main al head documental contiene exclusivamente:
- infraestructura: nuevo `.github/workflows/s00-t07a-build.yml`;
- documentación: informe de pruebas, handoff y evidencia bajo `docs/handoffs/evidence/S00-T07A/`.

No cambia producción, tests existentes, dependencias, recursos cartográficos, SQLite v4, requisitos, ADR ni PROJECT_STATE. Del SHA ejecutado al head revisado solo hay documentación/evidencia. No se atribuye build nuevo al commit documental.

El workflow hace checkout del SHA del evento, compara `git rev-parse HEAD`, compila app e instrumentación, conserva PIPESTATUS de Gradle y publica APK/evidencia incluso ante fallo. No restaura snapshots AVD. En la ejecución auditada, los logs de checkout y `validated-sha.txt` coinciden con el SHA solicitado. API del run confirma evento push, head exacto y conclusión success.

## Criterios de aceptación

| ID | Criterio | Evidencia contrastada | Estado |
|---|---|---|---|
| AC-01 | Build exacto, exit 0, APK/hash | Checkout, logs Gradle, exit-code, ZIP y APK originales | CUMPLE |
| AC-02 | Pruebas existentes y regresión real documentadas | Logs originales + assertions y dispatch reales; dominio ambas APIs y recorrido completo API36 | CUMPLE, cobertura API24 parcial declarada |
| AC-03 | Instalación limpia, persistencia, mapa/biometría o NOT TESTED justificado | AVD nuevo, paquete ausente, install sin -r, gates reales, red desconectada, relanzamientos | CUMPLE |
| AC-04 | Sin BLOCKER/MAJOR conocidos, límites explícitos | Diff sin cambios de producción, suites exitosas y límites separados de fallos | CUMPLE |
| AC-05 | Handoff/evidencia versionados, diff revisado | Identidad de 35 logs verificada contra originales y Git blob; docs contrastadas | CUMPLE |

El brief admite límites documentados; no exige declarar éxito de todas las plataformas. No se transforma NOT TESTED en PASS ni se usa T07A para afirmar upgrade histórico o cierre de S00.

## Evidencia original y resultados

**CI:** [39046939759](https://github.com/aik1a/LocalCash/actions/runs/39046939759), intento 1, success.

| Plataforma | Job | Gradle | Resultado Android |
|---|---|---|---|
| API24 | 117437427146 | BUILD SUCCESSFUL in 1m 4s, 63 tareas ejecutadas, exit 0 | Instalación nueva, biometría y mapa offline PASS |
| API36 | 117437427132 | BUILD SUCCESSFUL in 1m 35s, 63 tareas ejecutadas, exit 0 | Lo anterior y regresión financiera/GPS completa PASS |

Comando comprobado en workflow y logs:
`bash gradlew --no-daemon --console=plain :app:assembleDebug :app:assembleDebugAndroidTest`.

Dominio: `bash tests/domain-checks.sh`, **5483 comprobaciones por job**, desglosadas 18 + 15 + 5427 + 23. No son 5483 métodos instrumentados. Se inspeccionó el script que compila/ejecuta las cuatro clases y se contrastó su salida original.

### Descarga y comprobación propia de bytes

Se descargaron nuevamente ambos ZIP originales mediante GitHub y se verificaron CRC y SHA256. No se usó el archivo `artifact-verification.json` del Codificador como sustituto de calcular los hashes.

| API / artefacto | SHA256 ZIP | SHA256 APK principal |
|---|---|---|
| 24 / [12106073736](https://github.com/aik1a/LocalCash/actions/runs/39046939759/artifacts/12106073736) | `101943ef20858a287cca8dc019902714d50fc7b1d7f38d60607cd35a34f1653b` | `90c177bd383da6d7d85f0fa048f29d8997d32298b5095dd86f550d873cd606ac` |
| 36 / [12105649198](https://github.com/aik1a/LocalCash/actions/runs/39046939759/artifacts/12105649198) | `9ab61141e5c36e2770b9a870136c1b4941dd99c65cd6164ed29c7daa14d918ab` | `f6c188ae06db78a6068b64b69157b4fcfe776b6009561a1a0e9269086e9a83d0` |

APK instrumentación: API24 `567520c34f872b0798069810529e17bc8a3b605ba485cd9932972cf8620ee304`; API36 `025c89976efe19a17266bf8a21d0ea577b7ce2db8bfe8692cf994f704873c14d`.

Verificación propia con Python/zipfile/hashlib/Pillow: **PASS, retorno 0**.
- 116 entradas de `evidence/SHA256SUMS.txt` API24 y 150 API36 verificadas; más dos APK en cada `apk-sha256.txt`: **118 y 152**, concordantes con el informe.
- SHA exacto, exit 0, API declarada, instalación Success y paquete inicialmente ausente contrastados en cada ZIP.
- 33 logs/archivos versionados API24/API36 coinciden byte a byte con originales del artefacto, usando hashes Git blob del árbol remoto; los dos logs completos CI recuperados del endpoint de jobs también coinciden: **35/35 PASS**.
- Atlas dentro de ambos APK: SHA256 `f37bc75ae6e1c996d6527b411f805eb7bc304d898920a258d208d7acc605ab1d`, 64 PNG 256×256 en `LocalCashOSM/{z}/{x}/{y}.png`, z14–16.
- XML OSM empaquetado: `assets/maps/santiago-centro.osm.xml`, SHA256 `8503f464f1fe48655a52b69eb75ca13a1a1069b5e79ee0798cfa65503e46f3b7`. No se exige extensión .gz dentro del APK: AAPT expande ese asset.
- PNG del proveedor extraídos del emulador comparados por píxeles contra PNG del atlas: z14/z15/z16 idénticos, 587/505/516 colores en ambas APIs.
- Inspección visual propia del render autenticado API36: calles, nombres y marcador visibles; no es un mapa vacío con marcadores.
- Red original `Active default network: none` presente en ambos ZIP antes del primer mapa.

No se repitió CI sin necesidad. En Work no existen javac, adb, emulator, sdkmanager ni /dev/kvm; nueva ejecución local Android/Java: **NOT TESTED**. La prueba propia anterior es sobre archivos/bytes, no se presenta como una nueva sesión de emulador. Advertencia local de Pillow por getdata deprecado: no afecta comparación de píxeles ni resultado.

## Cobertura contrastada con el código de pruebas

| Área | Qué verifica realmente | API24 | API36 |
|---|---|---|---|
| Instalación limpia | Paquete ausente, AVD nuevo, install app/tests sin -r; atlas privado inexistente por File.exists antes de mapa | PASS | PASS |
| Biometría | Enrolamiento en ajustes, sensor finger touch, sin enrolar, cancelación, dedo incorrecto, lockout/reintento, sesión, background, rotación, force-stop y PID distinto tras muerte de proceso | PASS | PASS |
| Rutas protegidas | Ocho intents internos con extra authenticated=true, ausencia de contenido privado, gate visible; intent externo rechazado | PASS | PASS |
| Mapa offline | ConnectivityManager desconectado, sin INTERNET, OfflineTileProvider real, fuente local, tiles, dos marcadores de cuenta, exclusión sin GPS/otra cuenta, vacío, fuera de cobertura, detalle, pan/zoom y recreación | PASS | PASS |
| Integridad mapa | ZIP corrupto produce error local; copia ausente recuperada desde APK; saldo/datos v4 intactos y reapertura con gate biométrico | PASS | PASS |
| Cuentas/movimientos | Formularios, dos cuentas, saldo 125,50, rechazo cero/negativo/overflow, FK y helper reabierto | NOT TESTED completo | PASS |
| Contactos/compras | Dos contactos, compra dos ítems 232,00, planificada futura, vínculo opcional, rollback segundo detalle, persistencia sin movimientos automáticos | NOT TESTED completo | PASS |
| Reparto/deudas | Selección explícita, 100,01/3 = 33,34 + 33,34 + 33,33; 20+30+50; errores, FK/PK/CHECK, rollback y relanzamiento | NOT TESTED completo | PASS |
| Migraciones | Fixtures internos SQLite v1/v2/v3→v4; snapshots, FK, NULL previos, saldos y deudas preservados | NOT TESTED | PASS |
| Ubicación | Opt-in, denegación real, fix inyectado por sensor GPS emulado y callback productivo, retirada, proveedor apagado, timeout 15s y persistencia al relanzar | NOT TESTED completo | PASS |

El dispatcher contiene condición explícita API36; en API24 imprime que no ejecuta la regresión completa. No se infiere éxito financiero/GPS API24 a partir del job verde. La prueba GPS concede FINE y COARSE y usa comandos/control de permisos de API36; no demuestra ubicación exclusivamente aproximada.

Las assertions instrumentadas pueden finalizar con RESULT_CANCELED; el harness exige además los marcadores PASS esperados, no solo returncode de adb. Se comprobaron los marcadores originales, los métodos que los emiten y las assertions previas. El alimentador utiliza el sensor del emulador; no cambia sesión ni llama callbacks de éxito propios. El PIN configura únicamente el bloqueo del AVD.

El render MapView se obtiene mediante Canvas con sesión autenticada y conserva FLAG_SECURE. Los 781 píxeles distintos prueban el dibujo del marcador, mientras que la comparación del proveedor/atlas y la inspección visual acreditan la base real. Detalle/zoom/pan son verificaciones instrumentadas del listener/controlador; no equivalen a recorrido manual humano exhaustivo por gestos.

API24: `svc wifi disable` devuelve 137 y queda registrado. No invalida la prueba offline: antes del mapa se exige red global ausente más ConnectivityManager desconectado, y el APK carece de permiso INTERNET. No se afirma desconexión desde el arranque del AVD.

Persistencia demostrada: reapertura de helper, force-stop/relanzamiento de fixtures y migraciones internas. **No demuestra instalación de actualización de APK histórico ni compatibilidad de firmas.**

## Matriz de calidad

Se aplica AUDIT_MATRIX al alcance T07A; no se repite una auditoría de diseño completo de módulos heredados.

| ID | Dimensión | Peso | Estado | Evidencia / observación |
|---|---|---:|---|---|
| Q01 | Requisitos y aceptación | 25 | CUMPLE | AC-01 a AC-05 y límites admitidos por brief |
| Q02 | Completitud funcional del incremento | 15 | CUMPLE | Entrega reproducible, sin placeholders ni defecto funcional demostrado |
| Q03 | Arquitectura/POO | 15 | CUMPLE | Diff sin cambios de producción/stack; pruebas usan servicios/repositorios productivos |
| Q04 | Calidad/mantenibilidad | 10 | PARCIAL | Workflow legible y acotado; disparadores incompletos para futuros cambios Gradle, H-02 |
| Q05 | Persistencia/datos/seguridad | 10 | CUMPLE | V4, snapshots/transacciones y gate Android real; mapa exclusivamente local |
| Q06 | Build/tests/regresión | 15 | PARCIAL | Ambos builds y pruebas verificadas; recorrido completo API24 no cubierto, H-01 |
| Q07 | Alcance/dependencias | 5 | CUMPLE | Solo workflow/docs/evidencia, dependencias productivas intactas |
| Q08 | Documentación/trazabilidad/handoff | 5 | CUMPLE | Identidad exacta y límites honestos; cierre/trazabilidad administrativa al Orquestador |

**Puntaje diagnóstico: 87,5/100**. Los gates esenciales prevalecen sobre el puntaje.

## Hallazgos

| ID | Severidad | Evidencia | Tratamiento |
|---|---|---|---|
| H-01 | MINOR — cobertura | Dispatcher omite finanzas/GPS/migraciones completos API24; informe lo declara NOT TESTED | No es defecto funcional comprobado. No bloquea integrar evidencia T07A. T07D debe conservar el límite y decidir si requiere ampliar harness/cobertura; nunca presentarlo como PASS |
| H-02 | MINOR — infraestructura | paths de push cubren app/tests/tools/maps/workflow, pero no build.gradle raíz, settings.gradle, gradle.properties, gradlew ni gradle/wrapper | No invalida este run exacto. Para cambios futuros de esas rutas, ejecutar workflow_dispatch o ampliar filtros mediante tarea autorizada. No se modifica workflow durante auditoría |

**BLOCKER: 0. MAJOR: 0. OUT-OF-SCOPE: 0.** No se demuestra regresión funcional en el incremento. No se convierten limitaciones de entorno o pruebas pendientes en bugs de la aplicación.

## Gates críticos

| Gate | Resultado | Justificación |
|---|---|---|
| Requisitos/criterios esenciales T07A | PASS | Build y regresión integrada demostrados, límites permitidos y documentados |
| Build requerido | PASS | Ambos assemble, SHA exacto y exit 0, ambas APIs |
| Tests requeridos | PASS con observación | Dominio ambas APIs, regresión completa API36; alcance parcial API24 transparente |
| Arquitectura/ADR/dependencias | PASS | Sin cambio productivo ni tecnología nueva |
| Integridad/seguridad aplicable | PASS | Pruebas originales y assertions de SQLite/biometría/offline contrastadas |
| Alcance/evidencia/handoff | PASS | Diff acotado, hashes y originales verificados |

## Veredicto y autorizaciones

**VEREDICTO: PASS WITH OBSERVATIONS.**

- **Commit de implementación/entrega:** APPROVED_FOR_COMMIT.
- **Integración de T07A hasta head documental 752107ef11162c8b8543dc3f557ac4e239fceb49:** **APPROVED_FOR_MERGE**.
- **Avance/cierre de S00:** **NOT_APPROVED_FOR_SPRINT_ADVANCE**; fuera del gate T07A y reservado a T07D.
- **Correctivos obligatorios antes de integrar T07A:** ninguno.
- Esta autorización no afirma cobertura completa API24, firma de distribución, upgrade histórico ni pruebas físicas.

La falta de recorrido completo API24 no bloquea este incremento de validación: existe ejecución completa API36, comprobación de compatibilidad biométrica/cartográfica API24 y el brief exige registrar límites en lugar de inventar resultados. Una decisión posterior que exija paridad total deberá asignar esa ampliación, no reetiquetar esta evidencia.

## Riesgos y traslado

- **T07B:** firma estable, certificados debug distintos y upgrade APK histórico preservando datos siguen **NOT TESTED** aquí. Migración SQLite no los sustituye. No desinstalar una instalación con datos para simular upgrade.
- **T07C:** mantener instrucciones coherentes con Java/XML, SQLite v4, biometría y mapas actuales; resolver discrepancias documentales H-01 heredadas; identificar APK debug y conservar evidencia antes de caducidad de artefactos. Cobertura geográfica: Santiago Centro, z14–16.
- **T07D:** evaluar H-01, equipo físico/sensor físico, recorrido manual humano, GPS exclusivamente aproximado, Android Studio y Sync/caché vacía. Todos permanecen **NOT TESTED**, no fallos demostrados. Valorar además el contexto previo del chat declarado en identificación.
- **CI futura:** atender H-02 si cambian archivos raíz/Gradle; los Actions usan versiones por tag, sin prometer reproducibilidad binaria del APK.

## Cierre de la actuación

Se registra exclusivamente este documento bajo docs/audits. No se modifica producción, workflow, tests, PROJECT_STATE ni el veredicto del Codificador; no se ejecuta T07B, no se hace merge ni se cierra T07A/S00. El SHA del commit documental se entrega al Orquestador fuera del informe para evitar autorreferencia.
