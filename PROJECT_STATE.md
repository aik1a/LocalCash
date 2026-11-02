# LocalCash — Project State

**Estado del documento:** operativo  
**Responsable principal:** Agente Orquestador  
**Regla:** actualizar cuando cambie el estado real del proyecto. No usar este archivo para reemplazar requisitos, ADRs ni historial de auditorías.

## Estado actual

- **Repositorio:** `aik1a/LocalCash`, `main`. **Fecha objetivo de entrega:** 19 de octubre de 2026.
- **Sprint:** S00, `CLOSED` (cierre académico con observaciones, 2026-10-18; recepción docente NOT VERIFIED).
- **Tareas cerradas:** S00-T01, T02, T03, T04, T05, T06A (GPS opcional), T06C (biometría), T06B (mapas offline) y **T07A (integración/regresión)** y **T07B (firmas/upgrade: diagnóstico)** y **T07C (documentación/entrega)** y **T07D (gate integral)**.
- **Funcionalidades integradas:** cuentas/movimientos/saldos, contactos/compras, gastos compartidos/deudas, ubicación opcional, acceso biométrico mediante AndroidX `BiometricPrompt` y mapas offline Santiago Centro mediante osmdroid 6.1.18. SQLite **v5**, Java/XML, almacenamiento financiero local.
- **Último merge funcional:** [PR #13](https://github.com/aik1a/LocalCash/pull/13) — `a34cc2e3e09e499136cd529eea7cc05053647fb5` (S01-T01, 2026-10-19). Diez divisas, configuración inicial SQLite, moneda por cuenta/compra, participación «Tú» y formularios simplificados.
- **Gate T06C:** [auditoría independiente](docs/audits/S00/S00-T06C-audit.md) `PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE`, 97,5/100, sin defectos BLOCKER/MAJOR; observación documental para T07.
- **CI T06C:** [run 38625086287](https://github.com/aik1a/LocalCash/actions/runs/38625086287), SHA `0f79e6c57be54144b1a756b3aff3a9644b025ca8`, build exit 0, 5483 comprobaciones por API24 y API36, pruebas biométricas y regresión T03–T06A PASS.
- **T06B:** CLOSED, auditoría independiente `PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE`, 97,5/100; [informe](docs/audits/S00/S00-T06B-audit.md); [CI 39042930625](https://github.com/aik1a/LocalCash/actions/runs/39042930625) API24/API36 PASS; SHA funcional `c289b0614d9d891c6ea4cfcdeec48dabc342c459`.
- **Última integración de auditoría:** S00-T07D CLOSED, [PR #12](https://github.com/aik1a/LocalCash/pull/12), merge `53c1517ec6a7eb64a7ab646df5565145aa455fd3`; gate PASS WITH OBSERVATIONS / APPROVED_FOR_SPRINT_ADVANCE (suficiencia académica, 0 BLOCKER, 0 MAJOR, 3 MINOR). H-01 documental corregida en T07C; paquete académico contrastado. Upgrade histórico BLOCKED_SIGNER_MISMATCH; conservación SQLite tras upgrade NOT TESTED.

- **Incremento posterior a S00:** S01-T01 CLOSED, auditoría [S01-T01](docs/audits/S01/S01-T01-audit.md) PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE; CI [39178585250](https://github.com/aik1a/LocalCash/actions/runs/39178585250) API24/API36 SUCCESS sobre `6b7813cb81a8742c1b6fd12f8259ec637852487f`. No representa cierre integral del roadmap S01.

## Próxima acción autorizada

**S00 CLOSED por decisión del Orquestador** tras auditoría independiente T07D PASS WITH OBSERVATIONS / APPROVED_FOR_SPRINT_ADVANCE. Preparar entrega efectiva y verificar acceso del docente al repositorio/paquete; el paquete histórico S00 no incluye S01-T01 y no se declara regenerado. El APK del incremento está en los artefactos de CI 39178585250; recepción docente y exposición NOT VERIFIED. Se recomienda ensayo manual en Android Studio sobre emulador descartable, sin convertirlo retroactivamente en prueba realizada. No abrir cambios funcionales sin nueva tarea y nueva validación.

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
- **T07 (cerrada en alcance académico):** T07A regresión, T07B diagnóstico de firmas (upgrade histórico bloqueado), T07C documentación/paquete y T07D gate integral. La conservación de datos tras upgrade no fue comprobada y no se declara completada; H-01 documental resuelta.
- **Decisiones fuera de alcance:** tipo de cuenta, compras que creen movimientos automáticamente, pagos parciales y campos extra de contacto; no ampliar sin autorización expresa.

## Bloqueadores y riesgos

- **T06B integrada:** no hay bloqueo pendiente del mapa. Cobertura deliberadamente limitada a Santiago Centro z14–16.
- Nunca obtener paquetes de mosaicos predescargados desde `tile.openstreetmap.org`: su política prohíbe prefetch y uso offline.
- **T07B verificado:** diez firmantes APK históricos diferentes, 90 pares dirigidos incompatibles; actualización in-place BLOCKED_SIGNER_MISMATCH, conservación de datos tras actualización NOT TESTED. **No desinstalar instalaciones con datos**. Documentado en T07C.
- GPS solo aproximado y equipo físico aún no probados; no presentar emulador como prueba física.
- Mantenimiento: advertencias Gradle heredadas sin bloqueo demostrado; `FLAG_SECURE` biométrico puede ocultar capturas, debe mantenerse y sustituirse evidencia con XML/logs/PNG del ZIP real.

## Última auditoría

`docs/audits/S00/S00-T07D-integral-audit.md`: **PASS WITH OBSERVATIONS / APPROVED_FOR_SPRINT_ADVANCE**, 85/100 diagnóstico; [PR #12](https://github.com/aik1a/LocalCash/pull/12) fusionado. S00 cerrado académicamente por Orquestador, sin acreditar recepción docente.

## Seguimiento y riesgos residuales

- **S00-T06B CLOSED:** atlas y mapa offline integrados y auditados, PR #8.
- **S00-T07A CLOSED:** PR #9 merge `2c89e242cf352cc55400c3e4fb198937fedca205`, SHA CI `32dd6e0ff09143bfc55276d5084c18472769b4af`, auditoría PASS WITH OBSERVATIONS; 2 MINOR no bloqueantes (API24 regresión parcial, filtros workflow Gradle raíz). Brief en `docs/tasks/completed/`.
- **S00-T07B CLOSED:** PR #10 merge `f080ffea24529187c8296b2615c77fcaa1ab1f16`, auditoría PASS WITH OBSERVATIONS, 10 certificados principales distintos/90 pares incompatibles, upgrade BLOCKED_SIGNER_MISMATCH y conservación SQLite tras upgrade NOT TESTED. Brief archivado.
- **S00-T07C CLOSED:** PR #11 merge `4d1848a150f83973c31859f418c6ef475cc44c39`, auditoría PASS WITH OBSERVATIONS; paquete ZIP verificado (93 hashes), H-01 documental corregido, brief archivado.
- **S00-T07D CLOSED:** PR #12 merge `53c1517ec6a7eb64a7ab646df5565145aa455fd3`, gate integral PASS WITH OBSERVATIONS / APPROVED_FOR_SPRINT_ADVANCE. Dispositivo físico, Sync Studio limpio, recorrido humano, regresión financiera/GPS completa API24 y conservación SQLite tras upgrade NOT TESTED; recepción docente NOT VERIFIED.
- Una auditoría independiente por incremento; Orquestador formaliza cierres/merges. No reabrir T01–T06C salvo defecto concreto.

## Reglas de actualización

El Orquestador debe actualizar este archivo cuando:
- cambia el sprint activo;
- se valida o invalida un build;
- aparece o se resuelve un bloqueador;
- cambia la próxima acción autorizada;
- una decisión pendiente pasa a aceptada mediante documentación correspondiente.

No registrar aquí detalles extensos de implementación; usar handoffs, auditorías, ADRs y documentos del sprint.

## Ciclo nuevo — pulido completo MVP (01/11/2026)

**Esta sección define la próxima acción del nuevo encargo y no altera los cierres históricos anteriores.** Aikia solicita documentar el alcance completo por sprints/fases/microtareas para ejecución paso a paso. Fecha objetivo nueva: **02/11/2026**, sin hora oficial informada.

- Baseline inspeccionado: 9b8a651d66982eea3a41e1979342468fe4cad176, rollback S01-T02 conservado; código SQLite v5 y S01-T01 funcional. No se ha implementado este nuevo plan.
- Plan: docs/exec-plans/active/MVP-PULIDO-COMPLETO.md.
- Entrada del agente: docs/agent/goals/LOCALCASH-MVP-PULIDO.md.
- Cola y checkpoint: docs/agent/goals/LOCALCASH-MVP-PULIDO.queue.json y .checkpoint.md.
- Alcance completo: CRUD, saldo suficiente, compras/pagos/undo, deudas pagadas y anulación segura, UI/swipe, privacidad, validación y entrega. Las compras con movimiento y el estado PAGADA son ampliaciones planificadas del nuevo ciclo; su implementación no se infiere del encargo documental.
- Desglose: **7 sprints, 23 fases, 91 microtareas**. No hay cortes A/B. Prefijo MVP-Sxx independiente de sprints históricos.
- Estado: **PREPARED / NOT_STARTED**; fases futuras DRAFT. Primera fase preparada READY: MVP-S01-T01, microtarea MVP-S01-T01-M01 PENDING.
- Próxima acción cuando se invoque la implementación: identificar checkout/entorno y comprobar baseline en MVP-S01-T01. Registrar reglas nuevas en documentos canónicos durante MVP-S01-T02. No implementar ni fusionar funcionalidad solo porque se creó este plan.
- Gates de roles vigentes. No declarar entregado/validado el producto por documentar tareas ni retirar alcance automáticamente por la fecha.
