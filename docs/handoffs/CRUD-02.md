# Handoff — CRUD-02, T00–T08

Fecha: 2026-11-03. Rol saliente: Codificador. Estado: implementación local lista para revisión, sin gate independiente ni merge.

## Objetivo ejecutado

Primera ronda aprobada del plan CRUD-02: persistencia y cierre de deudas, nombres de contactos/cuentas, eliminación de registros sin uso, edición/eliminación de planificaciones y consulta de deudas por contacto.

## Baseline y alcance

Copia local output/LocalCash-ux, partiendo de UX-01 y SQLite v6. Baseline de texto en output/CRUD02-before.json; ZIP previo output/LocalCash-antes-CRUD02.zip. La copia no tenía historia Git propia: se entrega patch revisable contra el baseline, sin fabricar commits o atribuir integración a main. Fuentes sincronizadas y checkout antiguo LocalCash conservados.

El usuario autorizó el plan el 2026-11-03. T09–T11 pertenecen a segunda ronda y no se implementan: corrección de movimientos, rehacer repartos y devolución que genere ingreso. Marcar deuda pagada NO altera saldos.

## Cambios realizados

- T00: baseline del código, backup de la variante uxcheck del emulador y verificación de integridad; certificado debug anterior/nuevo coincide. APK aumenta versionCode a 2 y versionName a 1.1. Wrapper Gradle original restaurado y SHA de blob verificado.
- T01/T02: SQLite v7, estados PENDIENTE/PAGADA reales; migración conserva filas y restaura triggers. Confirmación para marcar pagada o volver a pendiente. Cuotas cero dicen Sin deuda y no admiten cambio de estado.
- T03/T04: nombres editables conservando identidad y relaciones.
- T05: borrado confirmado de cuentas sin movimientos y contactos sin referencias; dependencias verificadas en la escritura.
- T06/T07: formulario de compra reutilizado para editar planes; moneda y estado fijos. Escritura/borrado de detalles/cabecera atómicos y revalidación de restricciones en repositorio.
- T08: pulsar contacto abre opciones y consulta de deudas. Compras navegables, estados y totales pendientes por moneda; importes sin moneda no se agregan.

## Archivos relevantes

LocalCashDbHelper, Deuda, DeudaRepository/Service, ContactoRepository, CuentaRepository, CompraRepository/Service, MovimientoService, RepartoActivity, ContactosActivity, CuentaActivity, NuevaCompraActivity, CompraDetalleActivity, AccionesUI y cuatro layouts. Tests: CrudChecks y LegacyV6DbHelper como fixture exacta anterior; FlowChecks añade modos crud y crud-upgrade, expectativas de versión actualizadas.

## Validación ejecutada

| Verificación | Resultado |
|---|---|
| APK principal e instrumentación, Gradle wrapper | PASS |
| Cinco suites JVM existentes | PASS: 34.400 comprobaciones |
| SQLite host, DDL Java real, nuevas instalaciones y upgrades v1–v4→v7 | PASS: 55 comprobaciones |
| Instrumentación CRUD en Android | PASS: 36 comprobaciones, incluida migración real desde helper v6 anterior |
| Regresión Android de moneda/repositorios y upgrades anteriores | PASS: 76 comprobaciones |
| Regresión Android de pago compra/cuenta | PASS |
| Prueba SQLite host del vínculo compra/movimiento v5→v6 | PASS |
| Actualización de APK uxcheck instalado, sin desinstalar | PASS: base v6→v7 |
| Comparación de datos antes/después de actualizar APK | PASS: filas de las nueve tablas idénticas; integrity_check=ok y sin FK rotas |
| Recorrido humano de nuevas pantallas en teléfono | PENDIENTE |
| Auditoría independiente | PENDIENTE |

Prueba CRUD usa BD con nombre independiente, sin borrar localcash.db. La regresión currency, que sí crea/elimina fixtures, se ejecutó exclusivamente en paquete nuevo crud02check. La variante uxcheck anterior recibió solo actualización y apertura para migrar; se exportaron sus datos antes/después. Ningún teléfono físico fue modificado.

Certificado debug comprobado: SHA-256 93188b16285ee8263886396ad66da2d283e0e8bfc9b868343ed1f9bd691035ff. Esa coincidencia acredita las compilaciones locales comparadas, no cualquier APK histórico del proyecto.

## Pendientes y límites

El intento de recorrido por UI llegó al prompt biométrico del emulador; las huellas simuladas probadas no fueron reconocidas. No se alteró la sesión ni se desactivó la protección para sortearlo. No se declara validada la interacción completa por pantallas; la cobertura nueva automatizada es de dominio, esquema y servicios/repositorios.

No cambiar divisa de una compra existente, editar pagos históricos ni borrar cuentas/contactos usados. Totales por contacto usan monedas conocidas por separado. No se hicieron ingresos automáticos al cerrar deudas.

Advertencias Gradle/manifest heredadas permanecen sin bloqueo. API24 no se volvió a ejecutar en esta entrega; la migración usa SQL compatible sin características recientes.

## Contexto mínimo siguiente agente

Leer AGENTS, PROJECT_ROUTER, PROJECT_STATE, plan CRUD-02, BUSINESS_RULES, DATABASE, este handoff y patch CRUD-02. Revisar especialmente migración y triggers, acciones de cancelación, rotación del formulario editado, lectura de estados y protección de registros con dependencias.

## Próxima acción

Auditoría independiente del patch y recorrido de pantallas nuevas en teléfono. Orquestador decide integración y cierre; no marcar sprint cerrado por los PASS automatizados.
