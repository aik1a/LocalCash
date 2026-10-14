# Task Brief — S00-T05: gastos compartidos y deudas

**Estado:** CLOSED — 2026-10-13 (PASS WITH OBSERVATIONS) · **Sprint:** S00, entrega acelerada · **Rol:** Codificador
**Rama nueva:** `task/S00-T05-gastos-compartidos` desde `main` vigente.
**Objetivo:** flujo demostrable offline para repartir el total de una compra existente entre contactos y consultar cuánto debe cada participante.

## Fuente normativa y requisitos

- PDF docente `LocalCash Proyecto Android.pdf`: Módulo 3, «Registros Compartidos y Deudas»: compras grupales detalladas por productos/servicios, contactos desde directorio y distribución equitativa o personalizada de la deuda.
- `LC-SHARED-01`, `LC-SHARED-02`, `LC-DEBT-01`, `LC-DEBT-02`.
- Consultar `PROJECT_ROUTER.md`, `AGENTS.md`, `PROJECT_STATE.md`, `docs/agent/roles/CODER.md`, `docs/design/{BUSINESS_RULES,DOMAIN_MODEL,DATABASE}.md`, `ARCHITECTURE.md`, `docs/sprints/S06-shared-expenses.md`, este brief y código real del branch main.
- **No reimplementar compras/contactos/cuentas**: reutilizar tablas y clases de T03/T04.

## Decisiones de diseño mínimas explícitas del Orquestador (2026-10-13)

Estas decisiones completan las reglas pendientes **solo para el flujo académico**; no constituyen requisitos adicionales del docente:

1. Reutilizar compras **REALIZADAS** con sus productos/servicios y total calculado. No crear compras clonadas para repartir; compras PLANIFICADAS aún no generan deudas.
2. Elegir **dos o más contactos distintos**, explícitamente desde la agenda. El contacto opcional ya asociado a una compra **no es participante automático**. Una compra admite un reparto inicial; no duplicar asignaciones si ya fue repartida.
3. Modo **EQUITATIVO**: trabajar en centésimas `long`; cuota base `total / N`, resto `total % N`. Ordenar participantes por ID de contacto ascendente y distribuir una centésima adicional a los primeros `resto` participantes. Así la suma exacta es el total, sin pérdida ni redondeos flotantes.
4. Modo **PERSONALIZADO**: ingresar el monto asignado a cada contacto en centésimas; todos los importes deben ser **>= 0**, sin overflow, y la suma debe ser **exactamente igual** al total de la compra antes de guardar. Un importe cero se permite si el reparto total sigue siendo válido.
5. Mostrar monto asignado/pendiente por contacto. Estado inicial `PENDIENTE`. Marcar `PAGADA` puede implementarse como operación simple de estado completo **solo si no dificulta el MVP**; **pagos parciales excluidos**. No generar movimientos bancarios, descuentos ni transferencias al dividir o marcar deuda.
6. Persistencia mínima: puede utilizarse **una sola tabla de asignaciones por compra/contacto**, con monto asignado y estado, en lugar de tablas redundantes `participantes` + `asignaciones` + `deudas`, siempre que preserve requisitos y trazabilidad; registrar explícitamente esta simplificación en `DATABASE.md` y `DOMAIN_MODEL.md`. No almacenar total redundante. No se exige asignación a nivel de ítem: los ítems ya se detallan en la compra y el PDF exige distribuir su **costo total**.

## Alcance del Codificador

1. Implementar las entidades/comportamientos mínimos de reparto y deudas, con invariantes, cálculo exacto en dominio y repositorio de persistencia; SQL fuera de Activities.
2. Extender `LocalCashDbHelper` de v2 a v3 **sin borrar tablas o datos**. Soportar instalaciones v2→v3 y, cuando corresponda, migración secuencial v1→v3.
3. Pantalla Java/XML desde Compras para seleccionar compra REALIZADA, elegir participantes y modo de reparto, registrar y consultar montos por persona; rechazar selección duplicada, cuota negativa, suma desigual, valores no válidos y reparto duplicado.
4. Mantener funcionamiento de cuentas/movimientos, contactos, compras y migración anterior.
5. Pruebas de dominio y CI/emulador para: reparto exacto 100,01 / 3 (33,34 + 33,34 + 33,33 en orden por ID), personalizado (p.ej. 20 + 30 + 50 = 100), suma inválida, duplicados, persistencia tras relanzar, FK, transacción y regresión v2→v3 (cuentas, movimientos y compras intactos). Probar monto cero/overflow donde corresponda.
6. Compilar **SHA exacto nuevo** con evidencia, retorno, APK y un único handoff. **Una auditoría al terminar**, sin checkpoints burocráticos.

## Fuera de alcance

- Pagos parciales, transferencias reales, cartera de cobranza, cobro automático, conciliación, múltiples monedas, backend, notificaciones, nuevas dependencias y rediseño general.
- Biometría, ubicación, osmdroid/mapas (S00-T06). No modificar app signing/keystores para ocultar el aviso T04; la prueba de actualización de APK firmado queda para S00-T07.
- Cambiar compras realizadas/planificadas, automatizar compra→movimiento o alterar saldos de cuentas sin decisión nueva del Orquestador.
- Merge, cierre de tarea/sprint, actualización de `PROJECT_STATE.md`.

## Criterios de aceptación

- [ ] Reutiliza compra real con ítems e importe total existente.
- [ ] Selecciona 2+ contactos reales distintos.
- [ ] Reparto equitativo exacto y determinista, sin errores en centésimas.
- [ ] Reparto personalizado válido cuando suma exactamente el total; inválido rechazado sin escritura parcial.
- [ ] Cuánto corresponde a cada contacto es visible y persistente; no se registra un reparto duplicado.
- [ ] SQLite v3 conserva cuentas/movimientos/contactos/compras de v2.
- [ ] Build y pruebas reales sobre SHA exacto PASS; datos sobreviven a relanzamiento.
- [ ] Handoff UTF-8 y evidencia suficiente para gate único.

**Validación:** GitHub Actions/emulador, siguiendo el patrón funcional de T04; no reutilizar builds antiguos. El Codificador prepara un plan de ejecución breve si es necesario conforme a `.agent/PLANS.md`, sin detenerse para pedir aprobación de detalles ya definidos.

**Entrega:** DONE/PARTIAL/BLOCKED; SHA funcional, diff, evidencia, APK y solicitud de una sola auditoría independiente. No fusionar.

## Cierre administrativo — 2026-10-13

- **SHA funcional validado:** `45c70f7efa2bb2ffcdc2b7cb7af78c16ca1c1c50`.
- **CI y emulador:** [run 38440658476](https://github.com/aik1a/LocalCash/actions/runs/38440658476), build `:app:assembleDebug` PASS, exit 0; 5.460 comprobaciones dominio PASS; migración SQLite v2→v3, regresión, integridad, UI y relanzamiento PASS.
- **Gate:** [auditoría independiente](../../audits/S00/S00-T05-audit.md), `PASS WITH OBSERVATIONS`, `APPROVED_FOR_MERGE`, 97,5/100; H-01 documental diferida a S00-T07.
- **Merge:** [PR #5](https://github.com/aik1a/LocalCash/pull/5), commit `5c9e9c86c9913f6079b684a150c86d783d48c189` integrado a `main`.
- **Alcance aceptado:** repartos equitativo y personalizado, deudas persistentes, tabla única `deudas`, SQLite v3. Sin pagos parciales ni movimiento automático por dividir.
- **Límite heredado:** compatibilidad de firma para actualizar APK conservando datos no resuelta; tarea S00-T07. No confundir con migración SQLite interna validada.
- **Próxima etapa:** S00-T06 (biometría, ubicación opcional y mapa offline). Sprint S00 permanece abierto.
