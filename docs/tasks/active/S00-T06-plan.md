# Plan operativo — S00-T06: biometría, ubicación y mapas offline

**Responsable:** Orquestador · **Sprint:** S00, entrega académica acelerada · **Estado:** PREPARADO (no equivale a código implementado)
**Baseline:** `main` tras [merge T06A PR #6](https://github.com/aik1a/LocalCash/pull/6) `b0f0d06f1d8837e85e80b75aa62e806a090f420b`.
**Fecha límite de entrega total:** 2026-10-19.

## Propósito y requisitos

Completar los pilares pendientes de la fuente normativa `LocalCash Proyecto Android.pdf`: biometría obligatoria, ubicación opcional para movimientos y representación espacial mediante mapas utilizables sin dependencia de conectividad continua. Requisitos `LC-P03`, `LC-AUTH-01`, `LC-TECH-02`, `LC-MOV-02`, `LC-P04`, `LC-MAP-01`, `LC-TECH-03`.

## Secuencia de tareas pequeñas (sin mezclar roles)

| Orden | Brief | Estado | Dependencia | Entregable |
|---|---|---|---|---|
| 1 | [S00-T06A-ubicacion.md](../completed/S00-T06A-ubicacion.md) | CLOSED · PR #6 | main aceptado T05 | Ubicación optativa, SQLite v4, CI/emulador PASS |
| 3 | [S00-T06B-mapas.md](S00-T06B-mapas.md) | DRAFT (pendiente origen de tiles y prueba offline) | T06A CLOSED; desarrollo pospuesto, no aprobado | MapView osmdroid, marcadores desde SQLite, demostración offline |
| 2 | [S00-T06C-biometria.md](S00-T06C-biometria.md) | READY, priorizada por bloqueo cartográfico de T06B | T06A CLOSED | Gate BiometricPrompt y todas las pantallas financieras protegidas |

Cada tarea se implementa en su rama desde `main` actualizado después del merge previo. El Codificador entrega un único handoff por incremento, evidencias reales del SHA y solicitud de auditoría independiente; el Auditor emite un gate por incremento (no volver a auditar T01–T05). **No fusionar sin APPROVED_FOR_MERGE**. El Orquestador ejecuta merges y cierres; no marcar S00 como cerrado hasta T07 y gate integral.

## Decisión pendiente de cartografía sin conexión (riesgo real)

Antes de pasar T06B a READY, definir en documento de diseño/ADR una solución **comprobable sin red en primera ejecución de la demo**: por ejemplo tiles offline empaquetados para el área de prueba, compatibles con osmdroid 6.1.18. No dar por cumplido el pilar offline con `MapView` que requiera descargar OSM por Internet o con caché precargada de forma no reproducible. Acotar y documentar cobertura; no prometer mapa global offline sin datos. El Codificador debe evaluar tamaño, formato, atribución/licencia de tiles y funcionamiento en emulador sin conexión; escalar una incompatibilidad técnica real al Orquestador. No introducir servidor/backend, ubicación obligatoria ni sincronización.

## Gate integral S00-T06

- [x] T06A: alta de movimiento con/sin ubicación, ubicación validada y persistida tras cierre/arranque; migración v3→v4 que conserva TODAS las tablas existentes y datos de T03–T05.
- [ ] T06B: consultar marcadores georreferenciados en osmdroid, respetar ciclos de vida/permisos, abrir mapa realmente sin red en área declarada.
- [ ] T06C: contenido financiero inaccesible antes de éxito biométrico; cancelación, errores, reentrada y acceso directo a Activities cubiertos; sin guardar datos de huellas.
- [ ] Cada SHA funcional compila en CI; tests de regresión seleccionados; capturas/logs, emulador/reinicio cuando aplique.
- [ ] Ningún cambio a pagos, reparto o cuentas más allá de regresión requerida. Sin overengineering.
- [ ] Auditoría independiente por tarea, handoff; luego evaluación administrativa S00-T06. Mantener S00 abierto para T07.

## Riesgos y límites

- **Firma de APK entre entregas anteriores**: compatibilidad de actualización preservando datos se verifica en S00-T07; no desinstalar apps con datos de usuario.
- **Emulador vs dispositivo físico**: evidencias de CI no se hacen pasar por validación física.
- **Biometría y API antigua**: el proyecto declara minSdk 24, targetSdk 36; garantizar comportamiento compatible o documentar límite y decisión; no introducir bypass de contenido protegido.
- **T05/T06A H-01** documental pendiente para T07, no obstáculo para T06C. La integración T06A está auditada y cerrada.
