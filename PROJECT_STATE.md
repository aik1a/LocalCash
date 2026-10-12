# LocalCash — Project State

**Estado del documento:** operativo  
**Responsable principal:** Agente Orquestador  
**Regla:** actualizar cuando cambie el estado real del proyecto. No usar este archivo para reemplazar requisitos, ADRs ni historial de auditorías.

## Estado actual

- **Repositorio:** `aik1a/LocalCash`, rama `main`.
- **Fase:** implementación acelerada de un MVP académico; entrega prevista **antes del 19 de octubre de 2026**.
- **Sprint operativo:** S00 (ruta acelerada de trabajo); **IN_PROGRESS**.
- **Cerradas:** S00-T01 (proyecto Android Java/XML) y S00-T02 (organización de paquete).
- **Último merge:** PR #2, `d39a35b99fe96349f06052dff01163957ed54c4f`.
- **Build real:** GitHub Actions run `38267899607`, `assembleDebug` del commit `6cea41252f26a76af44f81f990a99f0b95cf74b1`: SUCCESS, retorno 0.
- **Aplicación actual:** solo pantalla inicial «LocalCash»; **no hay todavía SQLite ni módulos funcionales**.
- **Próxima tarea:** S00-T03 — cuentas y movimientos SQLite con pantallas reales, READY.

## Próxima acción autorizada

**S00-T03:** implementar el primer flujo completo y demostrable: crear/listar cuentas, registrar ingresos/egresos, calcular saldo y persistirlo en SQLite. Task Brief: `docs/tasks/active/S00-T03-cuentas-movimientos.md`. Rama de código `task/S00-T03-cuentas-movimientos`, desde main.

**Motivo del cambio:** priorización expresa del usuario el 2026-10-12 por plazo académico. Se reemplaza la secuencia de tareas solo preparatorias por entregas funcionales, sin eliminar requisitos ni alterar los cuatro pilares docentes. Las dependencias, permisos y documentación técnica se integran cuando una funcionalidad realmente los necesita. Los sprints S01–S07 mantienen sus criterios técnicos de referencia, pero no implican implementar tareas duplicadas.

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
- estrategia exacta de reparto personalizado y redondeo;
- soporte o exclusión de pagos parciales;
- posible redundancia entre participantes y asignaciones;
- si completar una compra genera automáticamente un movimiento financiero;
- campos opcionales definitivos de contactos;
- estrategia de tiles/cache que garantice el comportamiento offline del mapa.

## Bloqueadores y riesgos

- Ningún bloqueo para asignar S00-T03. Los problemas de sandbox Windows se resolvieron para **build** usando GitHub Actions; no volver a repetir intentos fallidos.
- **Apertura manual Android, instalación y sync de Studio:** siguen NO EJECUTADO y se verificarán durante la integración de entrega (S00-T07), sin declarar PASS anticipado.
- Workflow de S00-T02 usa checkout fijado a SHA histórico: para cada nuevo cambio se debe compilar el **SHA nuevo**, no reutilizar esa salida.
- **Fecha objetivo:** dejar un flujo demostrable de cuentas y movimientos antes de añadir complejidad de otros módulos.

## Última auditoría

`docs/audits/S00/S00-T02-reaudit-H01.md`: PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE. H-01 de build resuelto con run `38267899607`. PR #2 fusionada, S00-T02 CLOSED. Ninguna auditoría de funcionalidades financieras existe todavía.

## Seguimiento de validaciones pendientes

- Apertura de `com.aik1a.localcash.presentation.MainActivity`, texto «LocalCash», errores de inicio: NO EJECUTADO.
- Sincronización de Android Studio: NO EJECUTADO.
- Mantener la advertencia `android.builtInKotlin=false` documentada; no actualizar herramientas por estética.
- Ejecutar un build Android real por **incremento funcional**, preferentemente CI con checkout del SHA exacto y APK verificable. Auditoría breve al completar cada incremento, sin reauditar documentos sin cambio relevante.

## Reglas de actualización

El Orquestador debe actualizar este archivo cuando:
- cambia el sprint activo;
- se valida o invalida un build;
- aparece o se resuelve un bloqueador;
- cambia la próxima acción autorizada;
- una decisión pendiente pasa a aceptada mediante documentación correspondiente.

No registrar aquí detalles extensos de implementación; usar handoffs, auditorías, ADRs y documentos del sprint.