# LocalCash — Project State

**Estado del documento:** operativo  
**Responsable principal:** Agente Orquestador  
**Regla:** actualizar cuando cambie el estado real del proyecto. No usar este archivo para reemplazar requisitos, ADRs ni historial de auditorías.

## Estado actual

- **Repositorio:** `aik1a/LocalCash`, `main`. **Fecha objetivo de entrega académica:** 19 de octubre de 2026.
- **Sprint:** S00 — entrega acelerada, **IN_PROGRESS**.
- **Tareas cerradas:** S00-T01 (base), T02 (estructura), T03 (cuentas/movimientos) y **T04 (contactos/compras)**.
- **Funcionalidad actual:** cuentas, ingresos/egresos/saldos, contactos, compras realizadas/planificadas con ítems e historial, persistencia SQLite v2.
- **Último gate:** `docs/audits/S00/S00-T04-audit.md`, PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE.
- **Último merge:** [PR #4](https://github.com/aik1a/LocalCash/pull/4), `93d52298319a5cb57fd902d7d52b513fee7918c4`.
- **Validación T04:** [Actions run 38289787487](https://github.com/aik1a/LocalCash/actions/runs/38289787487), código `76480f7639a4ef2e2ec3121817307605ff9843d2`, build PASS 0, 33 pruebas dominio PASS, UI/migración/regresión/reinicio en emulador PASS.
- **Próxima tarea:** S00-T05 — gastos compartidos y deudas, READY.

## Próxima acción autorizada

Codificador: `docs/tasks/active/S00-T05-gastos-compartidos.md`. Rama `task/S00-T05-gastos-compartidos` desde main actual. Reutilizar compras y contactos; reparto equitativo/personalizado exacto y persistente con SQLite v3. Una auditoría al finalizar. Biometría, ubicación/mapa y entrega final quedan para T06–T07.

## Decisiones vigentes

- Android nativo en Java.
- UI en XML.
- Gradle con Groovy DSL.
- Persistencia local mediante SQLite + `SQLiteOpenHelper`.
- SQLite como única fuente persistente dentro del alcance actual.
- Autenticación mediante `BiometricPrompt`.
- Mapas mediante osmdroid 6.1.18.
- Sin Firebase, backend remoto, Room, Compose, DI ni arquitectura empresarial completa salvo decisión posterior explícita.
- Arquitectura base: Presentation → Application/Services → Domain → Repositories → SQLiteOpenHelper → SQLite.
- Proyecto académico: priorizar claridad, trazabilidad y demostrabilidad sobre sobreingeniería.

## Decisiones pendientes relevantes

Resolver solo cuando el sprint correspondiente las necesite:
- esquema físico SQLite definitivo;
- tipos/constraints exactos de columnas;
- política de borrado y cascadas;
- reparto equitativo/personalizado: decisión mínima aceptada para S00-T05 en Task Brief y BUSINESS_RULES; validar su implementación;
- pagos parciales: excluidos del MVP S00-T05 por decisión operativa documentada;
- reparto/deudas: tabla única mínima de asignaciones por compra/contacto autorizada para T05; evitar redundancia;
- si completar una compra genera automáticamente un movimiento financiero;
- campos opcionales definitivos de contactos;
- estrategia de tiles/cache que garantice el comportamiento offline del mapa.

## Bloqueadores y riesgos

No hay bloqueo para T05. Se debe migrar SQLite **v2→v3 aditivamente**, preservando toda la información de T03/T04.

**Aviso de firma APK (T07):** T03 y T04 entregaron APKs con certificados distintos. El APK T04 **no actualiza directamente** una instalación T03 que conserve datos. No desinstalar APK anterior que contenga datos. Para prueba de actualización real, utilizar la misma clave/firma o una vía de respaldo compatible, y documentar evidencia. No considerar esto fallo de migración SQLite interna, que sí pasó en CI.

Dispositivo físico y sync gráfico Android Studio: NO EJECUTADO. El emulador CI sí validó funcionalidad/reinicio.

La vinculación automática compra→movimiento sigue pendiente; T05 no debe inventarla. Advertencias Gradle/Kotlin permanecen documentadas.

## Última auditoría

`docs/audits/S00/S00-T04-audit.md`: PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE; [PR #4](https://github.com/aik1a/LocalCash/pull/4) integrado. Sprint no cerrado.

## Seguimiento pendiente

- S00-T05: reparto/deudas.
- S00-T06: biometría, ubicación y mapas con requisito offline.
- S00-T07: pruebas integrales, firma de APK, manual, documentación y exposición.
- Un build real del SHA nuevo y auditoría única por incremento; sin burocracia intermedia.

## Reglas de actualización

El Orquestador debe actualizar este archivo cuando:
- cambia el sprint activo;
- se valida o invalida un build;
- aparece o se resuelve un bloqueador;
- cambia la próxima acción autorizada;
- una decisión pendiente pasa a aceptada mediante documentación correspondiente.

No registrar aquí detalles extensos de implementación; usar handoffs, auditorías, ADRs y documentos del sprint.