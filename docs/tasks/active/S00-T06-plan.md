# Plan operativo — S00-T06: biometría, ubicación y mapas offline

**Responsable:** Orquestador · **Sprint:** S00, entrega académica acelerada · **Estado:** PREPARADO (no equivale a código implementado)
**Baseline:** `main` tras [merge T06C PR #7](https://github.com/aik1a/LocalCash/pull/7) `1c944cde59ed6aba8e8af2f9529b29435e40af59`.
**Fecha límite de entrega total:** 2026-10-19.

## Propósito y requisitos

Completar los pilares pendientes de la fuente normativa `LocalCash Proyecto Android.pdf`: biometría obligatoria, ubicación opcional para movimientos y representación espacial mediante mapas utilizables sin dependencia de conectividad continua. Requisitos `LC-P03`, `LC-AUTH-01`, `LC-TECH-02`, `LC-MOV-02`, `LC-P04`, `LC-MAP-01`, `LC-TECH-03`.

## Secuencia de tareas pequeñas (sin mezclar roles)

| Orden | Brief | Estado | Dependencia | Entregable |
|---|---|---|---|---|
| 1 | [S00-T06A-ubicacion.md](../completed/S00-T06A-ubicacion.md) | CLOSED · PR #6 | main aceptado T05 | Ubicación optativa, SQLite v4, CI/emulador PASS |
| 3 | [S00-T06B-mapas.md](S00-T06B-mapas.md) | READY — ADR-002 aceptado | T06A/T06C CLOSED; implementar sobre main de PR #7 | osmdroid, mosaicos OSM propios empaquetados en APK y demostración offline |
| 2 | [S00-T06C-biometria.md](../completed/S00-T06C-biometria.md) | CLOSED — PR #7 | T06A CLOSED | BiometricPrompt y protección de las 7 Activities, CI API24/API36 PASS |

Cada tarea se implementa en su rama desde `main` actualizado después del merge previo. El Codificador entrega un único handoff por incremento, evidencias reales del SHA y solicitud de auditoría independiente; el Auditor emite un gate por incremento (no volver a auditar T01–T05). **No fusionar sin APPROVED_FOR_MERGE**. El Orquestador ejecuta merges y cierres; no marcar S00 como cerrado hasta T07 y gate integral.

## Decisión de cartografía offline aceptada

[ADR-002 — osmdroid y cartografía OSM offline](../../design/decisions/ADR-002-osmdroid-cartografia-offline.md) **ACCEPTED**: render local de datos OSM con licencia ODbL a atlas ZIP `LocalCashOSM/z/x/y.png` incluido en APK, bbox acotada de Santiago Centro (-33.465,-70.675,-33.435,-70.645), z14–16 y `OfflineTileProvider` sin red/fallback; no se descargan tiles OSMF. El Codificador debe **producir** los datos/mosaicos con procedencia, probar el ZIP real y validar instalación limpia sin red. La decisión resuelve la planificación; no demuestra todavía implementación o funcionamiento.

## Gate integral S00-T06

- [x] T06A: alta de movimiento con/sin ubicación, ubicación validada y persistida tras cierre/arranque; migración v3→v4 que conserva TODAS las tablas existentes y datos de T03–T05.
- [ ] T06B: consultar marcadores georreferenciados en osmdroid, respetar ciclos de vida/permisos, abrir mapa realmente sin red en área declarada.
- [x] T06C: contenido financiero inaccesible antes de éxito biométrico; cancelación, errores, reentrada y acceso directo a Activities cubiertos; sin guardar datos de huellas.
- [ ] Cada SHA funcional compila en CI; tests de regresión seleccionados; capturas/logs, emulador/reinicio cuando aplique.
- [ ] Ningún cambio a pagos, reparto o cuentas más allá de regresión requerida. Sin overengineering.
- [ ] Auditoría independiente por tarea, handoff; luego evaluación administrativa S00-T06. Mantener S00 abierto para T07.

## Riesgos y límites

- **Firma de APK entre entregas anteriores**: compatibilidad de actualización preservando datos se verifica en S00-T07; no desinstalar apps con datos de usuario.
- **Emulador vs dispositivo físico**: evidencias de CI no se hacen pasar por validación física.
- **Biometría y API antigua**: el proyecto declara minSdk 24, targetSdk 36; garantizar comportamiento compatible o documentar límite y decisión; no introducir bypass de contenido protegido.
- **T05/T06A/T06C H-01** documental pendiente para T07, sin bloquear T06B. T06A y T06C están auditadas y fusionadas.
