# LocalCash — Project State

**Estado del documento:** operativo  
**Responsable principal:** Agente Orquestador  
**Regla:** actualizar cuando cambie el estado real del proyecto. No usar este archivo para reemplazar requisitos, ADRs ni historial de auditorías.

## Estado actual

- **Repositorio:** `aik1a/LocalCash`, `main`. **Fecha objetivo de entrega:** 19 de octubre de 2026.
- **Sprint:** S00, `IN_PROGRESS` (**no cerrado**).
- **Tareas cerradas:** S00-T01, T02, T03, T04, T05, T06A (GPS opcional), T06C (biometría), T06B (mapas offline) y **T07A (integración/regresión)** y **T07B (firmas/upgrade: diagnóstico)**.
- **Funcionalidades integradas:** cuentas/movimientos/saldos, contactos/compras, gastos compartidos/deudas, ubicación opcional, acceso biométrico mediante AndroidX `BiometricPrompt` y mapas offline Santiago Centro mediante osmdroid 6.1.18. SQLite **v4**, Java/XML, almacenamiento financiero local.
- **Último merge funcional:** [PR #8](https://github.com/aik1a/LocalCash/pull/8) — `f1664ec4d87bf16cd9f7668cbda62e0ef0f406e9` (T06B).
- **Gate T06C:** [auditoría independiente](docs/audits/S00/S00-T06C-audit.md) `PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE`, 97,5/100, sin defectos BLOCKER/MAJOR; observación documental para T07.
- **CI T06C:** [run 38625086287](https://github.com/aik1a/LocalCash/actions/runs/38625086287), SHA `0f79e6c57be54144b1a756b3aff3a9644b025ca8`, build exit 0, 5483 comprobaciones por API24 y API36, pruebas biométricas y regresión T03–T06A PASS.
- **T06B:** CLOSED, auditoría independiente `PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE`, 97,5/100; [informe](docs/audits/S00/S00-T06B-audit.md); [CI 39042930625](https://github.com/aik1a/LocalCash/actions/runs/39042930625) API24/API36 PASS; SHA funcional `c289b0614d9d891c6ea4cfcdeec48dabc342c459`.
- **Siguiente incremento:** S00-T07C — documentación H-01, trazabilidad y paquete académico; T07B CLOSED por PR #10, auditoría PASS WITH OBSERVATIONS. Upgrade histórico BLOCKED_SIGNER_MISMATCH; conservación SQLite tras upgrade NOT TESTED.

## Próxima acción autorizada

**Codificador — ejecutar S00-T07C** conforme a [Task Brief](docs/tasks/active/S00-T07C-documentacion-entrega.md) en `task/S00-T07C-documentacion-entrega` desde main posterior a PR #10. T07D (auditoría integral) permanece PLANNED. T07A/T07B CLOSED, sin cierre S00. Mantener S00 `IN_PROGRESS` hasta validación integral y gate independiente. T07 incluye firma/upgrade sin pérdida de datos, regresión integrada, documentación técnica H-01, evidencia de entrega y pruebas físicas solo si hay dispositivo disponible. No inventar PASS para dispositivo físico ni actualización histórica no ejecutada.

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
- **T07B verificado:** diez firmantes APK históricos diferentes, 90 pares dirigidos incompatibles; actualización in-place BLOCKED_SIGNER_MISMATCH, conservación de datos tras actualización NOT TESTED. **No desinstalar instalaciones con datos**. Documentar en T07C.
- GPS solo aproximado y equipo físico aún no probados; no presentar emulador como prueba física.
- Mantenimiento: advertencias Gradle heredadas sin bloqueo demostrado; `FLAG_SECURE` biométrico puede ocultar capturas, debe mantenerse y sustituirse evidencia con XML/logs/PNG del ZIP real.

## Última auditoría

`docs/audits/S00/S00-T06B-audit.md`: **PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE**, 97,5/100; [PR #8](https://github.com/aik1a/LocalCash/pull/8) fusionado. H-01 documental de T06B y hallazgos históricos se revisan en T07. S00 sigue abierto.

## Seguimiento pendiente

- **S00-T06B CLOSED:** atlas y mapa offline integrados y auditados, PR #8.
- **S00-T07A CLOSED:** PR #9 merge `2c89e242cf352cc55400c3e4fb198937fedca205`, SHA CI `32dd6e0ff09143bfc55276d5084c18472769b4af`, auditoría PASS WITH OBSERVATIONS; 2 MINOR no bloqueantes (API24 regresión parcial, filtros workflow Gradle raíz). Brief en `docs/tasks/completed/`.
- **S00-T07B CLOSED:** PR #10 merge `f080ffea24529187c8296b2615c77fcaa1ab1f16`, auditoría PASS WITH OBSERVATIONS, 10 certificados principales distintos/90 pares incompatibles, upgrade BLOCKED_SIGNER_MISMATCH y conservación SQLite tras upgrade NOT TESTED. Brief archivado.
- **S00-T07C READY:** documentación H-01, trazabilidad, paquete de entrega. T07D PLANNED; dispositivo físico NOT TESTED; S00 sigue abierto.
- Una auditoría independiente por incremento; Orquestador formaliza cierres/merges. No reabrir T01–T06C salvo defecto concreto.

## Reglas de actualización

El Orquestador debe actualizar este archivo cuando:
- cambia el sprint activo;
- se valida o invalida un build;
- aparece o se resuelve un bloqueador;
- cambia la próxima acción autorizada;
- una decisión pendiente pasa a aceptada mediante documentación correspondiente.

No registrar aquí detalles extensos de implementación; usar handoffs, auditorías, ADRs y documentos del sprint.