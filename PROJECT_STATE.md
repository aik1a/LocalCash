# LocalCash — Project State

**Estado del documento:** operativo  
**Responsable principal:** Agente Orquestador  
**Regla:** actualizar cuando cambie el estado real del proyecto. No usar este archivo para reemplazar requisitos, ADRs ni historial de auditorías.

## Estado actual

- **Repositorio:** `aik1a/LocalCash`, `main`. **Fecha objetivo de entrega:** 19 de octubre de 2026.
- **Sprint:** S00, `IN_PROGRESS` (**no cerrado**).
- **Tareas cerradas:** S00-T01, T02, T03, T04, T05, T06A (GPS opcional) **T06C (biometría)** y **T06B (mapas offline)**.
- **Funcionalidades integradas:** cuentas/movimientos/saldos, contactos/compras, gastos compartidos/deudas, ubicación opcional, acceso biométrico mediante AndroidX `BiometricPrompt` y mapas offline Santiago Centro mediante osmdroid 6.1.18. SQLite **v4**, Java/XML, almacenamiento financiero local.
- **Último merge funcional:** [PR #8](https://github.com/aik1a/LocalCash/pull/8) — `f1664ec4d87bf16cd9f7668cbda62e0ef0f406e9` (T06B).
- **Gate T06C:** [auditoría independiente](docs/audits/S00/S00-T06C-audit.md) `PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE`, 97,5/100, sin defectos BLOCKER/MAJOR; observación documental para T07.
- **CI T06C:** [run 38625086287](https://github.com/aik1a/LocalCash/actions/runs/38625086287), SHA `0f79e6c57be54144b1a756b3aff3a9644b025ca8`, build exit 0, 5483 comprobaciones por API24 y API36, pruebas biométricas y regresión T03–T06A PASS.
- **T06B:** CLOSED, auditoría independiente `PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE`, 97,5/100; [informe](docs/audits/S00/S00-T06B-audit.md); [CI 39042930625](https://github.com/aik1a/LocalCash/actions/runs/39042930625) API24/API36 PASS; SHA funcional `c289b0614d9d891c6ea4cfcdeec48dabc342c459`.
- **Siguiente incremento:** S00-T07 — integración final, firma APK y conservación de datos, regresión y documentación/entrega.

## Próxima acción autorizada

**Orquestador — preparar Task Brief S00-T07** y asignar tareas acotadas de cierre técnico/documental al Codificador. Mantener S00 `IN_PROGRESS` hasta validación integral y gate independiente. T07 incluye firma/upgrade sin pérdida de datos, regresión integrada, documentación técnica H-01, evidencia de entrega y pruebas físicas solo si hay dispositivo disponible. No inventar PASS para dispositivo físico ni actualización histórica no ejecutada.

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

- **T06B — implementada e integrada:** ADR-002 ACCEPTED, atlas OSM real ZIP offline Santiago Centro z14–16, atribución y marcadores SQLite; auditoría favorable y PR #8 fusionado.
- **T07:** validar compatibilidad de firma APK e instalación/actualización preservando datos, pruebas integrales y en dispositivo físico si disponible, depuración documental histórica H-01 de T05/T06A/T06C, documentación/evidencias de entrega y gate del sprint.
- **Decisiones fuera de alcance:** tipo de cuenta, compras que creen movimientos automáticamente, pagos parciales y campos extra de contacto; no ampliar sin autorización expresa.

## Bloqueadores y riesgos

- **T06B integrada:** no hay bloqueo pendiente del mapa. Cobertura deliberadamente limitada a Santiago Centro z14–16.
- Nunca obtener paquetes de mosaicos predescargados desde `tile.openstreetmap.org`: su política prohíbe prefetch y uso offline.
- Firmas APK de iteraciones anteriores difieren; **no desinstalar instalaciones con datos** como modo de probar actualización. Resolver/documentar en T07.
- GPS solo aproximado y equipo físico aún no probados; no presentar emulador como prueba física.
- Mantenimiento: advertencias Gradle heredadas sin bloqueo demostrado; `FLAG_SECURE` biométrico puede ocultar capturas, debe mantenerse y sustituirse evidencia con XML/logs/PNG del ZIP real.

## Última auditoría

`docs/audits/S00/S00-T06B-audit.md`: **PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE**, 97,5/100; [PR #8](https://github.com/aik1a/LocalCash/pull/8) fusionado. H-01 documental de T06B y hallazgos históricos se revisan en T07. S00 sigue abierto.

## Seguimiento pendiente

- **S00-T06B CLOSED:** atlas y mapa offline integrados y auditados, PR #8.
- **S00-T07 pendiente:** regresión final, validación APK/firma y datos, revisión documental H-01, presentación y entrega.
- Una auditoría independiente por incremento; Orquestador formaliza cierres/merges. No reabrir T01–T06C salvo defecto concreto.

## Reglas de actualización

El Orquestador debe actualizar este archivo cuando:
- cambia el sprint activo;
- se valida o invalida un build;
- aparece o se resuelve un bloqueador;
- cambia la próxima acción autorizada;
- una decisión pendiente pasa a aceptada mediante documentación correspondiente.

No registrar aquí detalles extensos de implementación; usar handoffs, auditorías, ADRs y documentos del sprint.