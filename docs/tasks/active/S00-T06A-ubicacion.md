# Task Brief — S00-T06A: ubicación opcional de movimientos

**Sprint:** S00 (subtarea de T06) · **Rol:** Codificador · **Estado:** READY · **Fecha máxima de proyecto:** 2026-10-19
**Rama de trabajo:** `task/S00-T06A-ubicacion` desde `main` vigente tras PR #5 (sin reutilizar branch T05).
**Objetivo:** registrar un movimiento con ubicación geográfica opcional, conservarlo en SQLite y consultar el par de coordenadas persistido sin afectar movimientos sin ubicación.

## Requisitos y Context Pack

- Fuente normativa: `LocalCash Proyecto Android.pdf`, módulos de cuentas/movimientos geolocalizados y SQLite local/offline.
- `LC-MOV-02`, `LC-P02`, `LC-TECH-01` y regresión relacionada `LC-MOV-01`.
- Leer `PROJECT_ROUTER.md`, `AGENTS.md`, `PROJECT_STATE.md`, `docs/agent/roles/CODER.md`, `ARCHITECTURE.md`, `docs/design/{BUSINESS_RULES,DOMAIN_MODEL,DATABASE}.md`, `docs/requirements/PROJECT_SPEC.md`, `docs/sprints/S04-geolocation-maps.md`, este Brief y código existente `Movimiento`/`MovimientoRepository`/`MovimientoService`/`CuentaActivity`/`LocalCashDbHelper`. Usar plan breve o ExecPlan si la complejidad lo exige.

## Alcance

1. Definir `Ubicacion` como Value Object (latitud/longitud finitos y en rangos geográficos), opcional en `Movimiento` sin romper el constructor/API anterior innecesariamente.
2. Añadir columnas `latitud` y `longitud` anulables a `movimientos`; incrementar `LocalCashDbHelper` **v3→v4** de manera aditiva, sin modificar/descartar registros ni tablas anteriores. Manejar instalación nueva y cadena de migración v1→v4, v2→v4 y v3→v4.
3. Extender repositorio/servicio para guardar y consultar el par opcional, manteniendo SQL fuera de UI y validando ambos presentes o ambos ausentes.
4. UI Java/XML: en registro de movimiento permitir al usuario optar explícitamente por capturar ubicación actual con Android Location. Solicitar permisos necesarios en tiempo de uso, sin bloqueo al denegar permiso; registrar movimiento sin ubicación cuando no se haya solicitado o no se consiga coordenada. Mostrar estado comprensible. Evitar solicitar permisos al abrir la app.
5. Sin conexión a servidores ni dependencia obligatoria de red para registrar la transacción. No almacenar localización sin consentimiento explícito del flujo.
6. Actualizar únicamente `DATABASE.md`, `DOMAIN_MODEL.md` y `docs/testing/` pertinentes; handoff único y evidencia.

## Fuera de alcance

- MapView/osmdroid, marcadores/mapas y descarga de tiles: T06B.
- Biometría: T06C.
- Tracking en segundo plano, servicio GPS persistente, historial de rutas, direcciones, reverse geocoding, nuevas cuentas, integración compra→movimiento o sincronización remota.
- No modificar T03–T05 salvo por migración aditiva y compatibilidad indispensable.
- No merge, auditoría propia, cierre administrativo, `PROJECT_STATE.md` ni firma de APK.

## Criterios de aceptación

- [ ] Movimiento sin ubicación se registra y consulta igual que antes (par NULL).
- [ ] Usuario elige guardar ubicación; permiso concedido + ubicación conocida produce dos coordenadas válidas y persistentes.
- [ ] Permiso denegado, ubicación desactivada o sin fix: error/no disponibilidad visibles, sin crash y posibilidad de completar movimiento sin coordenadas; no guardar par incompleto.
- [ ] No se aceptan NaN/infinito ni coordenadas fuera de [-90,90] y [-180,180].
- [ ] SQLite v4 conserva exactamente cuentas, saldos derivados, movimientos previos, contactos, compras, detalles y deudas; migraciones v3→v4 y secuenciales probadas. FK y datos antiguos intactos.
- [ ] Build `:app:assembleDebug` del SHA exacto PASS retorno 0; dominio, migración SQLite, UI y relanzamiento en emulador PASS, junto con regresión mínima T03–T05.
- [ ] Documentación y handoff enlazan commit, CI original, APK, resultados, limitaciones.

## Validación y entrega

- Compilar `bash gradlew --no-daemon --console=plain :app:assembleDebug :app:assembleDebugAndroidTest`.
- Emulador CI: un movimiento sin GPS; uno con coordenadas simuladas y permiso; rechazo/ausencia de permiso; force-stop y consulta tras abrir; fixture SQLite v3 real con todas las tablas anteriores, snapshot previo/post migración; regresión mínima del cálculo de saldo y deudas.
- Escenarios que requieran permisos/ubicación reales deben ejercitar código productivo; no declararlos PASS solo por mocks de repositorio.
- Evidencia de SHA, salida, exit-code, APK y handoff `docs/handoffs/S00-T06A-coder.md`. Solicitar auditoría independiente solo al terminar este incremento. **Una auditoría, no checkpoints intermedios.**
