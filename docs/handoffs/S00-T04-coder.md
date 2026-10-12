# Handoff — S00-T04

**Rol:** Codificador · **Fecha:** 2026-10-12 · **Sprint:** S00  
**Rama:** task/S00-T04-contactos-compras  
**Base:** main, e1e42caa1a76af2c264cb316262c4ca5d092de7a

## Objetivo y cambios

Directorio local de contactos; compras realizadas/planificadas con fecha seleccionable, varios productos/servicios, cantidades, precios, total derivado e historial/detalle; contacto opcional. Integrado desde MainActivity con Java/XML.

- SQLite v2: migración aditiva desde v1, solo CREATE TABLE/INDEX nuevos. Sin DROP ni reescritura de cuentas/movimientos.
- Contacto, Compra y DetalleCompra inmutables y validados. Cantidad entera positiva, precio no negativo en centésimas, aritmética exacta y lista protegida; sin total persistido.
- ContactoRepository, CompraRepository y CompraService; compra y detalles atómicos. SQL fuera de Activities. Sin movimientos automáticos al realizar compra: decisión sigue pendiente.
- Formulario acepta varios ítems, permite quitar ítems del borrador, rechaza datos inválidos y evita guardar un ítem todavía no añadido. Fecha con DatePicker; estado seleccionado al crear. El borrador conserva sus ítems ante recreación de Activity.
- Pantallas ContactosActivity, ComprasActivity, NuevaCompraActivity y CompraDetalleActivity. Sin dependencias/versiones nuevas de runtime ni módulos fuera de alcance.

## Estado y validación

DONE (implementación y validación completas; gate pendiente del Auditor).

**SHA exacto compilado:** 76480f7639a4ef2e2ec3121817307605ff9843d2. [Run 38289787487](https://github.com/aik1a/LocalCash/actions/runs/38289787487), SUCCESS, retorno 0. Un commit posterior solo incorpora docs/evidencia; no se presenta como build diferente.

| Validación | Resultado |
|---|---|
| assembleDebug + assembleDebugAndroidTest | PASS, exit 0; log completo en evidence/S00-T04/assembleDebug.log |
| Dominio cuentas/movimientos y compras/contactos | PASS, 33 comprobaciones |
| Migración v1→v2 y saldos/historial previos | PASS, dos cuentas, dos movimientos, saldos 125,50 y 300,00 |
| Rollback al fallar segundo detalle | PASS, sin cabecera ni detalle parcial |
| Alta de contactos y compras desde formularios | PASS, Ana/Luis, realizada y planificada con fecha futura |
| Total de dos ítems y vínculo opcional | PASS, 2 × 100,25 + 3 × 10,50 = 232,00, vínculo a Ana; planificada sin contacto |
| Cantidad 0/negativa/decimal y compra vacía | PASS, rechazadas por UI |
| Regresión contable y ausencia de movimientos automáticos | PASS |
| Proceso cerrado y aplicación relanzada | PASS, cuentas/compras/detalles/contactos conservados |
| Studio / dispositivo físico | NO EJECUTADO |

Evidencia definitiva: evidence/S00-T04/verification.json, logs y XML. Artifact 11766425002 incluye APK, evidencia y capturas; SHA256 APK d15f90d9780fb9c073f3b5dcf0c1179c72db5e6ca832118080417c7a81cdf5fe.

Se corrigió una validación de lista inmutable en Compra detectada por tests y se protegió el campo de cantidad contra reinterpretación de signo/decimales. También se corrigieron referencias/registro/selectores del runner; el espacio insuficiente del AVD se resolvió liberando SDKs sin uso en el runner. Solo el último run se considera PASS del incremento.

## Archivos

app/src/main: dominio nuevo, dos repositories y CompraService, LocalCashDbHelper, cuatro Activities, MainActivity/manifiesto y layouts nuevos. Pruebas: PurchaseDomainChecks, FlowChecks con escenarios de regresión y compras/migración, domain-checks.sh, relaunch-checks.py y workflow s00-t04-build.yml. Docs: DATABASE.md, DOMAIN_MODEL.md, docs/testing/S00-T04.md y este handoff/evidencia.

## Decisiones y límites

Fecha epoch milisegundos y centésimas long mantienen T03. Cantidades en unidades enteras; precio cero permitido. Contacto solo ID/nombre, sin teléfono/email obligatorio; ID opcional de contacto en compra. FK RESTRICT; no edición/borrado persistente ni transición de estados en este incremento. No se inventó cuenta de cargo, reparto, deuda, recordatorio, biometría ni mapas. Fecha futura puede elegirse en el formulario; no se agregaron restricciones de calendario no especificadas.

## Riesgos y pendientes

- Dispositivo físico y sync gráfico Android Studio: NO EJECUTADO; CI/emulador se reportan separadamente.
- Migración prueba una BD v1 reconstruida con el mismo DDL y datos previos, no una instalación de APK de usuario. Actualizar un APK requiere la misma firma Android; Se compararon los certificados públicos del APK T03 entregado y este APK T04: **son distintos; este APK no puede actualizar directamente el APK T03 distribuido**. No desinstalar una app con datos para forzar una actualización; compilar con el keystore original cuando se pruebe actualización en dispositivo.
- Lecturas/listas síncronas para volumen de demo académico; no se evaluaron miles de compras. No se agregan optimizaciones sin necesidad.

## Próximo agente

Solicito una única auditoría independiente de S00-T04 completo: criterios CON-01/BUY-01/BUY-02/BUY-03, preservación contable v1→v2, rollback de compra, total exacto, vínculo opcional y relanzamiento. Leer el Task Brief, DATABASE.md, este handoff y la evidencia; revisar diff desde la base y SHA compilado.

La evidencia de LC-CON-01, LC-BUY-01/02/03, LC-P02 y LC-TECH-01 se entrega al Orquestador para trazabilidad tras el gate. PROJECT_STATE.md no se modifica; no hubo merge, cierre ni auditoría intermedia.
