# Auditoría independiente — S00-T06A: ubicación opcional de movimientos

## Identificación
- **Fecha:** 2026-10-14
- **Rol:** Auditor independiente (F4; revisión única de T06A, sin modificar código ni administrar el sprint).
- **Rama:** `task/S00-T06A-ubicacion` vs `main`
- **Base main:** `e9117e8999045c4cb5f6fd7a4aaed200520b3b6f`
- **SHA funcional compilado/probado:** `ebc985dedfd61797bcf104b73b24ca9c6c926a1b`
- **Diff:** 3 commits por delante de main, ninguno por detrás. Commit posterior al SHA funcional: únicamente ExecPlan/handoff/evidencias, sin código de producción ni tests.
- **Task Brief:** `docs/tasks/active/S00-T06A-ubicacion.md`
- **Handoff:** `docs/handoffs/S00-T06A-coder.md`
- **Fuentes:** PDF docente `LocalCash Proyecto Android.pdf` (funcionalidad de movimientos y ubicación); `PROJECT_ROUTER.md`; `PROJECT_STATE.md`; `AGENTS.md`; rol `docs/agent/roles/AUDITOR.md`; matriz y plantilla en `docs/audits/`; `docs/requirements/PROJECT_SPEC.md`; `ARCHITECTURE.md`; `docs/design/BUSINESS_RULES.md`, `DOMAIN_MODEL.md`, `DATABASE.md`; `docs/sprints/S04-geolocation-maps.md`; código, tests y workflow de la rama.

## Criterios de aceptación del Task Brief

| ID | Criterio | Evidencia comprobada | Estado |
|---|---|---|---|
| AC-01 | Registrar/consultar movimiento sin ubicación, par NULL | Constructor antiguo de Movimiento, MovimientoRepository, FlowChecks "Sin consentimiento" + lectura SQLite | CUMPLE |
| AC-02 | Opt-in expreso; permisos y GPS reales de Android en código productivo; par persistente | CuentaActivity/CapturaUbicacion/LocationManager; test instrumentado con `adb emu geo fix`, UI, lectura exacta | CUMPLE |
| AC-03 | Denegación, ubicación desactivada o falta de fix no bloquean movimiento ni producen par parcial | Android PermissionController real, `cmd location set-location-enabled`, test provider sin fix y timeout 15s; seis movimientos nuevos, uno con GPS | CUMPLE |
| AC-04 | NaN, infinito y coordenadas fuera de límites rechazadas | Ubicacion, 23 checks de dominio, CHECK SQLite en INSERT/UPDATE | CUMPLE |
| AC-05 | Migración SQLite v3→v4 aditiva sin pérdida, instalaciones v1/v2 compatibles | LocalCashDbHelper; FlowChecks fixtures v1/v2/v3, snapshots de todas las tablas/columnas previas, FK/saldos/deudas/NULL | CUMPLE |
| AC-06 | Build y regresión sobre SHA exacto; emulador, relanzamiento | Actions run 38498296734, job 115639006790; BUILD SUCCESSFUL exit 0; 5483 checks; flujos T03–T05 y T06A; force-stop + lectura UI | CUMPLE |
| AC-07 | Documentación, APK y handoff con limitaciones | Handoff y docs/testing; artefacto Actions; advertencia dispositivo físico/firma; H-01 documental menor | CUMPLE CON OBSERVACIÓN |

## Matriz Q01–Q08

| Dimensión | Peso | Estado | Puntos | Justificación |
|---|---:|---|---:|---|
| Q01 Requisitos y aceptación | 25 | CUMPLE | 25 | LC-MOV-02 y requisitos técnicos del Task Brief completos |
| Q02 Completitud funcional | 15 | CUMPLE | 15 | Registro con/sin coordenadas, consentimiento, rechazo y reintento, historial persistido |
| Q03 Arquitectura/POO | 15 | CUMPLE | 15 | Ubicacion inmutable en dominio, persistencia en Repository/SQLiteOpenHelper, LocationManager en adaptador de presentación |
| Q04 Calidad/mantenibilidad | 10 | CUMPLE | 10 | Métodos breves, par validado, API anterior compatible, manejo de ciclo de vida |
| Q05 Persistencia, datos y permisos | 10 | CUMPLE | 10 | v4 aditiva, FK/constraints de par, no permiso al inicio, no tracking ni backend |
| Q06 Build/tests/regresión | 15 | CUMPLE | 15 | SHA validado, Gradle exit 0, 5483 checks, pruebas Android de ubicación, migraciones y force-stop |
| Q07 Alcance/dependencias | 5 | CUMPLE | 5 | Sin mapas, biometría, backend o nuevas dependencias; sin cambios funcionales no relacionados |
| Q08 Docs, trazabilidad y handoff | 5 | PARCIAL | 2,5 | Handoff y evidencia suficientes; descripción preliminar de Movimiento en DOMAIN_MODEL mantiene operaciones candidatas incompatibles con el modelo inmutable (H-01) |

**Puntaje diagnóstico: 97,5/100.** El puntaje no sustituye gates críticos.

## Evidencia técnica original verificada

- **Run original:** https://github.com/aik1a/LocalCash/actions/runs/38498296734
- **Job:** `115639006790`, `conclusion=success`; **head_sha** `ebc985dedfd61797bcf104b73b24ca9c6c926a1b`. Checkout y `git rev-parse HEAD` coinciden.
- **Comando:** `bash gradlew --no-daemon --console=plain :app:assembleDebug :app:assembleDebugAndroidTest`; salida literal **`BUILD SUCCESSFUL in 49s`**, retorno **0** (guardado en `docs/handoffs/evidence/S00-T06A/exit-code.txt`). Workflow conserva código de Gradle con `PIPESTATUS[0]`.
- **Comando de dominio:** `bash tests/domain-checks.sh`: 18 cuentas/dinero + 15 compras/contactos + 5427 reparto + 23 ubicación = **5483 PASS** en log original.
- **APK:** `app-debug.apk`, 92963 bytes; SHA256 `e2c9c934ddd285e4dabc5587f3751c35d93070eba92a2077437489aeae8c8117`.
- **Artefacto CI:** `11863884007`, SHA256 ZIP `e69534724d8a45411ab943d202054e5f694e73430816ec5906cfb8e9ac4d9968` (no expirado al verificar).
- **Emulador:** API 36 x86_64 google_apis; instrumentación real y `location-relaunch-checks.py`; marcadores de log original `LOCALCASH_FLOW_PASS`, `LOCALCASH_PURCHASE_PASS`, `LOCALCASH_SHARED_PASS`, `LOCALCASH_SHARED_RESTART_PASS`, `LOCALCASH_LOCATION_PASS`, `LOCALCASH_LOCATION_RESTART_PASS`.
- **Migraciones:** fixtures con DDL/filas de v1, v2, v3; v3 incluye seis tablas y deudas; comparación de valores preexistentes, dos NULL en movimientos anteriores, cálculos y `PRAGMA foreign_key_check`; ver código `FlowChecks.verificarUbicaciones`.
- **Permisos/GPS:** permiso inicialmente denegado por diálogo del sistema; prueba sin opt-in comprueba que no se solicitó permiso; `pm grant` + `adb emu geo fix -70.66 -33.45`, callback real `LocationManager`, par recibido `(-33.45, -70.65999833333333)`; se exige tolerancia solo al comparar contra la inyección del emulador y **igualdad exacta** contra SQLite/UI del valor recibido. GPS desactivado, retirada de consentimiento y test provider sin fix con timeout real de 15 s se ejecutaron con éxito.
- **Persistencia real:** force-stop y cold start, saldo `131,50`, movimiento GPS, otros sin ubicación y deudas intactas visibles por UI; captura/XML disponible en artefacto.

**Intento anterior documentado:** [run 38497503997](https://github.com/aik1a/LocalCash/actions/runs/38497503997), SHA `4c331d8326fa0ff55a994be81ec1f159e1c6a800`, falló una aserción de longitude literal del test. El commit `ebc985d` corrige expectativa de cuantización del emulador y conserva comparación exacta de la coordenada capturada/persistida; los cambios respecto al intento previo afectan pruebas y documentación, no el código productivo. Run posterior pasa completo.

## Hallazgos

| ID | Severidad | Criterio | Evidencia | Tratamiento |
|---|---|---|---|---|
| H-01 | DOCUMENTATION | Q08 | `DOMAIN_MODEL.md` mantiene en la descripción candidata de Movimiento `asignarUbicacion`/`eliminarUbicacion` y un encabezado de estado que no refleja T06A, mientras la sección implementada y el código usan `Movimiento` inmutable y ubicación elegida al crear. | Consolidar texto histórico en T07. **No bloquea T06A ni exige cambio de código**. |

**BLOCKER/MAJOR:** ninguno. **Correcciones obligatorias de T06A:** ninguna.

## Gates críticos

| Gate | Resultado | Soporte |
|---|---|---|
| Requisitos obligatorios | PASS | AC-01–AC-07, LC-MOV-02, fuente normativa |
| Criterios esenciales | PASS | Registro opcional, pares válidos, permisos, fallback y persistencia |
| Build del SHA exacto | PASS | Run original, exit 0 |
| Tests requeridos | PASS | 5483 checks, Android Instrumentation, regresión y relanzamiento |
| Arquitectura y decisiones | PASS | Java/XML, SQLiteOpenHelper, capas conservadas |
| Integridad y seguridad aplicable | PASS | Par atómico, CHECK, no acceso a ubicación sin opt-in |
| Alcance/dependencias | PASS | Sin feature T06B/T06C ni dependencias nuevas |

## Veredicto y autorizaciones

**VEREDICTO: `PASS WITH OBSERVATIONS`**
- **Commit:** `APPROVED_FOR_COMMIT` (ya registrado).
- **Merge:** `APPROVED_FOR_MERGE` **solo para el diff auditado**.
- **Avance integral/cierre sprint S00:** `NOT_APPROVED_FOR_SPRINT_ADVANCE` — T06B, T06C y T07 pendientes.

## Observaciones y riesgos residuales

- H-01 documentación no bloqueante; evitar confundir propuestas preliminares con API realmente implementada.
- No se ejecutó en equipo GPS físico, ni un caso específico de **permiso aproximado solamente** en dispositivo real. El permiso fino, denegación, proveedor inactivo y timeout sí están probados; el flujo aproximado puede fallar sin proveedor de red pero no debe bloquear el movimiento sin ubicación. Mantener en pruebas integrales/dispositivo T07.
- No se probó actualización in situ de APK firmado desde T05 preservando datos; incompatibilidad de firmas heredada, tratar en T07 sin desinstalar datos de usuario. Los fixtures SQLite sí demuestran migraciones internas de esquema.
- Las pruebas de fixture **eliminan BD en emulador de pruebas**; nunca ejecutar sobre instalación con datos reales.
- T06B debe definir todavía una fuente de tiles reproducible offline; mapas y biometría no forman parte de este gate.
- Mensajes transitorios de adb/gráficos y cierre del simulador en runner no invalidan un job completado con tests PASS.

## Conclusión

La revisión independiente del repositorio y del log original confirma un incremento funcional de ubicación optativa con consentimiento explícito, manejo de permisos/falta de GPS, persistencia SQLite v4 aditiva, registros antiguos intactos y regresión relacionada completa. Sin defectos críticos. **`APPROVED_FOR_MERGE`** para S00-T06A. El **Orquestador** formalizará, si corresponde, el merge/cierre y estado de la siguiente tarea. Esta auditoría no modifica Java/XML/tests, no hace merge ni cambia `PROJECT_STATE.md`.
