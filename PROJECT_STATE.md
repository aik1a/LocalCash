# LocalCash — Project State

**Estado del documento:** operativo  
**Responsable principal:** Agente Orquestador  
**Regla:** actualizar cuando cambie el estado real del proyecto. No usar este archivo para reemplazar requisitos, ADRs ni historial de auditorías.

## Estado actual

- **Repositorio:** `aik1a/LocalCash`, rama `main`.
- **Fase:** implementación acelerada de un MVP académico; entrega prevista **antes del 19 de octubre de 2026**.
- **Sprint operativo:** S00 (ruta acelerada de trabajo); **IN_PROGRESS**.
- **Cerradas:** S00-T01, S00-T02 y S00-T03 (cuentas, movimientos y SQLite funcional).
- **Último merge:** PR #3, `f484642361c20246eb07dfc030a0e41e01b76647`.
- **Build real:** GitHub Actions run `38283139884` del SHA `eea0fbfc895f5d5e2cde6f3512407f3cc9c16cec`: PASS, 18 comprobaciones de dominio y pruebas UI/SQLite en emulador PASS.
- **Aplicación actual:** cuentas múltiples, ingresos/egresos, historial/saldo y SQLite persistente; probada en emulador y tras relanzar.
- **Próxima tarea:** S00-T04 — contactos y compras locales, READY.

## Próxima acción autorizada

**S00-T04:** crear contactos y compras con detalles/estados, lista/historial y persistencia SQLite. Task Brief: `docs/tasks/active/S00-T04-contactos-compras.md`; rama `task/S00-T04-contactos-compras` desde main.

**Continuidad importante:** implementar migración SQLite aditiva desde v1 sin borrar cuentas ni movimientos; demostrar que los saldos anteriores se conservan. La asociación automática compra→movimiento sigue pendiente de decisión, no debe inventarse. Una auditoría al final del incremento.

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

No hay bloqueo para asignar T04. **SQLite v1** requiere migración aditiva para añadir tablas; `onUpgrade` actual debe cambiar sin destruir información histórica. Pendiente decisión compra→movimiento y reparto (futuras tareas).

La apertura y persistencia **sí se probaron en emulador en T03**. Sin prueba en dispositivo físico ni sync gráfico Android Studio (reservados para T07). No reutilizar builds antiguos para commits nuevos.

## Última auditoría

`docs/audits/S00/S00-T03-audit.md`: PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE; [PR #3](https://github.com/aik1a/LocalCash/pull/3) fusionada. Sprint S00 aún no cerrado.

## Seguimiento de validaciones pendientes

- Módulos funcionales T04, T05 y T06.
- Comprobación final en dispositivo físico y Studio cuando exista entorno, T07.
- Mantener avisos de deprecación documentados; no actualizaciones de versión arbitrarias.
- Build y auditoría únicos por incremento funcional; sin burocracia intermedia.

## Reglas de actualización

El Orquestador debe actualizar este archivo cuando:
- cambia el sprint activo;
- se valida o invalida un build;
- aparece o se resuelve un bloqueador;
- cambia la próxima acción autorizada;
- una decisión pendiente pasa a aceptada mediante documentación correspondiente.

No registrar aquí detalles extensos de implementación; usar handoffs, auditorías, ADRs y documentos del sprint.