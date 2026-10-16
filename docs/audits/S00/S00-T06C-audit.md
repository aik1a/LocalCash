# Auditoría independiente — S00-T06C: autenticación biométrica

## Identificación

- **Tipo:** auditoría única de tarea, no gate integral de sprint.
- **Fecha:** 2026-10-15.
- **Rol:** Agente Auditor; no implementó S00-T06C, no modifica código.
- **Repositorio/rama:** `aik1a/LocalCash` / `task/S00-T06C-biometria`.
- **Base main:** `0e1d818d81ef1085f41780b03e84c65c0cdb0426`.
- **SHA funcional auditado:** `0f79e6c57be54144b1a756b3aff3a9644b025ca8`.
- **PR abierto:** [#7](https://github.com/aik1a/LocalCash/pull/7), sin fusionar al emitir este dictamen.
- **Handoff:** `docs/handoffs/S00-T06C-coder.md`.
- **Task Brief:** `docs/tasks/active/S00-T06C-biometria.md`, estado READY al auditar.
- **Diff:** 11 commits por delante de main, 0 detrás. Comparación SHA funcional → rama muestra 1 commit ulterior solo de ARCHITECTURE/ExecPlan/handoff/evidencias/pruebas documentadas; no cambia código Java, XML, Gradle o tests ejecutables.
- **Fuentes de referencia:** `PROJECT_ROUTER.md`, `AGENTS.md`, `PROJECT_STATE.md`, `docs/agent/roles/AUDITOR.md`, `docs/audits/AUDIT_MATRIX.md`, `docs/audits/AUDIT_TEMPLATE.md`, `docs/requirements/PROJECT_SPEC.md` (LC-P03, LC-AUTH-01, LC-TECH-02), `ARCHITECTURE.md`, `docs/sprints/S02-security.md`, Task Brief, handoff, código/CI y pruebas. El PDF docente es la fuente normativa primaria referenciada por esos documentos; este dictamen contrasta los requisitos de biometría expresamente trazados en el repositorio.

## Criterios de aceptación

| ID | Criterio del Task Brief | Evidencia examinada | Resultado |
|---|---|---|---|
| AC-01 | Sin éxito biométrico no se muestran cuentas, movimientos, contactos, compras o deudas | `ActividadProtegida.onCreate/onResume` carga solo `activity_bloqueo` antes de autenticación; `crearContenido`/repositorios se difieren; las 7 Activities heredan de esa clase; 7 intent directos instrumentados en API24/36 | CUMPLE |
| AC-02 | Éxito desbloquea; cancelación, error, dedo incorrecto y reentrada mantienen gate | `AccesoActivity` usa `BiometricPrompt.AuthenticationCallback`; `biometric-emulator.py` ejecuta challenge + dedo 1, cancelación, dedo 2, lockout/retry, background, force-stop, rotación y nuevo prompt | CUMPLE |
| AC-03 | Sin enrolamiento/capacidad biométrica, estado informativo sin exposición ni fallback abierto | `BiometricManager.canAuthenticate(BIOMETRIC_STRONG)`; mensajes de ausencia; prueba en emulador antes del enrolamiento en las dos APIs | CUMPLE |
| AC-04 | Usar BiometricPrompt y no persistir material biométrico | `androidx.biometric:biometric:1.1.0`, `LocalCashApplication` solo conserva booleano/generación en RAM; SHA/diff sin cambios de entidades/SQLite v4; sin PIN propio ni servidor | CUMPLE |
| AC-05 | Compatibilidad minSdk24/targetSdk36; protección de todas las rutas internas | Gradle conserva minSdk24/targetSdk36; `FragmentActivity` compatible; jobs de emulador API24 y API36 PASS; `exported=false` para Activities internas; extras maliciosos ignorados | CUMPLE |
| AC-06 | Build exacto, prueba de éxito/cancelación/bloqueo y regresión T03–T06A | Workflow run `38625086287`, jobs `116062441534` y `116062442463`: build exit 0, 5483 checks por job, biometría en ambas APIs; regresión en API36 con SQLite v4, GPS/permisos/migraciones y relanzamiento | CUMPLE |
| AC-07 | Handoff, documentación, APK y solicitud de auditoría única | `docs/testing/S00-T06C.md`, `docs/handoffs/S00-T06C-coder.md`, artifacts originales y SHA256; limitaciones declaradas | CUMPLE |

## Matriz de calidad Q01–Q08

| ID | Dimensión | Peso | Estado | Puntos | Razón |
|---|---|---:|---|---:|---|
| Q01 | Requisitos y criterios de aceptación | 25 | CUMPLE | 25 | AC-01–07 y LC-P03/AUTH-01/TECH-02 verificados sobre código y CI |
| Q02 | Completitud funcional | 15 | CUMPLE | 15 | Inicio, desbloqueo, reintento, errores, retorno de background y rutas internas |
| Q03 | Arquitectura y POO | 15 | CUMPLE | 15 | Gate transversal sin lógica SQL en UI; sesión de aplicación efímera; módulos funcionales conservados |
| Q04 | Calidad/mantenibilidad | 10 | CUMPLE | 10 | 3 componentes focalizados; adaptaciones pequeñas a siete Activities; control de lifecycle y generación |
| Q05 | Persistencia/seguridad | 10 | CUMPLE | 10 | No persistir autorización/huellas, cerrar sesión al background, FLAG_SECURE, actividades internas no exportadas, SQLite v4 intacto |
| Q06 | Build/tests/regresión | 15 | CUMPLE | 15 | Ambos jobs exitosos, build exit 0, biometría ejercida por huella emulada y regresión T03–T06A |
| Q07 | Alcance/dependencias | 5 | CUMPLE | 5 | Única dependencia directa añadida autorizada, sin mapas, backend, PIN o cambio de versión SQLite |
| Q08 | Documentación/trazabilidad/handoff | 5 | PARCIAL | 2,5 | Entrega completa, con H-01 documental histórico en ARCHITECTURE pendiente de T07 |

**Puntuación diagnóstica: 97,5/100.** El puntaje no sustituye gates críticos.

## Evidencia original de CI contrastada

- **[GitHub Actions run 38625086287](https://github.com/aik1a/LocalCash/actions/runs/38625086287):** estado `completed`, conclusión `success`, head_sha exacto `0f79e6c57be54144b1a756b3aff3a9644b025ca8`. Ambos jobs de la matriz `validate (24)` / `validate (36)` terminaron `success`.
- **Build:** `bash gradlew --no-daemon --console=plain :app:assembleDebug :app:assembleDebugAndroidTest`, log original `BUILD SUCCESSFUL in 1m 29s` (API24) y `1m 22s` (API36). Retorno `0` comprobado en logs originales y `docs/handoffs/evidence/S00-T06C/API{24,36}/exit-code.txt`. El workflow preserva `PIPESTATUS[0]` antes de emitir el estado.
- **Dominio:** cada job imprime 18 checks de dinero/cuentas + 15 de compras/contactos + 5427 de reparto + 23 de ubicación = **5483 PASS**.
- **Biometría real en emulador:** `tests/biometric-emulator.py` usa Android Settings para enrolar huella y `adb emu finger touch` para respuestas al prompt real. Se revisó que las pruebas no invocan `onAuthenticationSucceeded`, no asignan el booleano de sesión ni desactivan el gate. API24 y API36 reportan `LOCALCASH_BIO_SEED_PASS`, siete `LOCALCASH_BIO_DIRECT_PASS` y `LOCALCASH_BIOMETRIC_PASS`.
- **Cobertura de seguridad:** cancelación sin exposición, dedo no enrolado, lockout, reintento, navegación protegida, background, force-stop, rotación, muerte/restauración con PID comprobado, extra `authenticated=true` inútil, intent directo interno bloqueado para siete Activities y rechazo de Activities no exportadas.
- **Prueba real de muerte de proceso:** API24 PID anterior `5828`, nuevo `5892`; API36 anterior `6125`, nuevo `6184`, tras confirmar ausencia del PID viejo. La sesión requiere nuevo desafío.
- **Regresión API36:** marcadores `LOCALCASH_FLOW_PASS`, `LOCALCASH_PURCHASE_PASS`, `LOCALCASH_SHARED_PASS`, `LOCALCASH_SHARED_RESTART_PASS`, `LOCALCASH_LOCATION_PASS`, `LOCALCASH_LOCATION_RESTART_PASS`, `LOCALCASH_BIOMETRIC_REGRESSION_PASS`. `location-flow.log` evidencia migración v1/v2/v3→v4, denegación real, GPS con `LocationManager`, NULL y exactitud de coordenadas.
- **APK API36:** SHA256 `aa136956836bf9ca8839bff6cf81e1e911e927f5f9394a76bd8fa9e48c9b6c1d`; artefacto `11924053647`, ZIP digest `sha256:e4472fe37740224176f15858e7d6b426d1b89bd2f78caf434ade13e67aab4f95`.
- **APK API24:** SHA256 `9a0ae95f02ff2fd0f1da72b6c93009fdff85dfc22d465e0b7c6d4ac313278e71`; artefacto `11924252922`, ZIP digest `sha256:53848fae41221b560b050cc8d9a1779d563a63274680c7b658bc4b26331bf655`.
- **Límite de independencia:** esta auditoría revisó fuente, pruebas, logs originales y estados de Actions, sin ejecutar un build Android adicional fuera de GitHub. Las conclusiones de ejecución se atribuyen a ese CI trazable; no se presentan como corrida independiente adicional.

## Hallazgos

| ID | Severidad | Criterio | Evidencia y tratamiento |
|---|---|---|---|
| H-01 | DOCUMENTATION | Q08 | `ARCHITECTURE.md` conserva `Movimiento.asignarUbicacion(...)` como método esperado en sección POO, incompatible con el `Movimiento` inmutable ya aceptado en T06A. Discrepancia histórica no vinculada con el gate biométrico; consolidar documentación en T07, sin alterar dominio ni bloquear T06C. |

**BLOCKER: 0 · MAJOR: 0 · MINOR funcional: 0.** No se exigen correcciones de código para T06C.

## Gates críticos

| Gate | Resultado | Soporte |
|---|---|---|
| Requisitos obligatorios | PASS | AC-01–AC-07; requisitos biométricos trazados |
| Criterios esenciales | PASS | Ningún contenido privado visible antes de éxito; rutas internas probadas |
| Build requerido | PASS | Jobs API24/API36, exit 0 sobre SHA auditado |
| Tests requeridos | PASS | Huella real simulada por Android, seguridad y regresión API36 |
| Arquitectura y decisiones | PASS | BiometricPrompt AndroidX compatible, capas intactas |
| Integridad y seguridad aplicable | PASS | No almacenamiento de biometría, sesión efímera, acceso directo cerrado |
| Alcance/dependencias | PASS | Biometric 1.1.0 aprobada, sin cambios de SQLite/mapas |

## Veredicto y autorizaciones

**VEREDICTO: `PASS WITH OBSERVATIONS`**

- **Commit:** `APPROVED_FOR_COMMIT` (ya registrado).
- **Merge:** `APPROVED_FOR_MERGE` del diff auditado de la rama hacia `main`; autorización técnica, no ejecución de merge.
- **Avance/cierre integral de S00:** `NOT_APPROVED_FOR_SPRINT_ADVANCE`. S00-T06B (mapas offline) y S00-T07 (integración y entrega) aún pendientes.

## Observaciones y riesgos residuales

1. Validación en emuladores API24 y API36, no en hardware biométrico físico; las APIs intermedias no se ejecutaron individualmente. No es defecto demostrado ni bloquea el alcance aprobado; conservar para validación integral si hay dispositivo disponible.
2. Prueba de actualización APK con firma anterior preservando datos no se realizó; pendiente de S00-T07. No desinstalar datos de usuario para intentar resolverlo.
3. `FLAG_SECURE` impide capturas de contenido por diseño; usar evidencia de jerarquía UI y eventos del sistema ya preservada, no quitar esta protección para facilitar demostración.
4. Los fixtures instrumentados eliminan SQLite exclusivamente en emuladores descartables. No ejecutar pruebas destructivas sobre una instalación de usuario.
5. No se exige regresión de T06B todavía: mapas siguen DRAFT y fuera de alcance del incremento.

## Conclusión

Se constata `BiometricPrompt` compatible con API24/36, gate previo a crear contenido/repositories, protección de acceso directo a todas las pantallas privadas, reinvalidación al volver de segundo plano y tras reinicio del proceso, ausencia de fallback abierto, éxito/cancelación/error verificados y regresión de las funciones existentes. El CI original respalda el SHA exacto. Sin defectos críticos. **Aprobado técnicamente el merge de T06C**; corresponde al Orquestador formalizar integración, cierre y próxima iteración. **Esta auditoría no altera código, no fusiona PR #7 y no modifica `PROJECT_STATE.md`.**
