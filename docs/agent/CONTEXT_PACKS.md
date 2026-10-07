# LocalCash — Bancos de verdad y Context Packs

## Propósito

Evitar que cada agente cargue toda la documentación. Este archivo define qué fuentes son obligatorias por rol y qué documentos agregar según sprint/tarea.

## Jerarquía común de verdad

1. `LocalCash Proyecto Android.pdf` — fuente normativa primaria.
2. Código y documentación vigentes de GitHub `aik1a/LocalCash` — fuente operativa.
3. `docs/requirements/PROJECT_SPEC.md` — traducción operable del encargo.
4. `docs/design/decisions/` — decisiones aceptadas.
5. `PROJECT_STATE.md` — estado operativo actual.
6. Sprint activo, Task Brief, handoffs y auditorías — contexto de ejecución.

Si el código contradice un requisito, el código no redefine el requisito. La discrepancia debe escalarse.

## Banco de verdad — Orquestador

Siempre:
- `PROJECT_ROUTER.md`
- `PROJECT_STATE.md`
- `AGENTS.md`
- `docs/agent/roles/ORCHESTRATOR.md`

Al planificar/cerrar sprint:
- `docs/requirements/PROJECT_SPEC.md`
- `docs/requirements/TRACEABILITY.md`
- `docs/sprints/README.md`
- sprint activo
- último handoff y auditoría relevante

Agregar solo diseño afectado por la próxima tarea.

## Banco de verdad — Codificador

Siempre:
- `PROJECT_ROUTER.md`
- `AGENTS.md`
- `PROJECT_STATE.md`
- `docs/agent/roles/CODER.md`
- Task Brief de la tarea
- código real afectado

Agregar únicamente documentos técnicos directamente relacionados con la tarea.

No necesita leer auditorías históricas completas, sprints futuros ni documentos de entrega salvo que la tarea los afecte.

## Banco de verdad — Auditor

Siempre:
- `PROJECT_ROUTER.md`
- `AGENTS.md`
- `PROJECT_STATE.md`
- `docs/agent/roles/AUDITOR.md`
- Task Brief / criterios de aceptación
- handoff del Codificador
- diff/commit a revisar
- evidencia de build/tests

Agregar requisitos y diseño afectados. El Auditor no debe basar el veredicto en la explicación del Codificador cuando el repositorio o la evidencia muestran otra cosa.

## Context Pack por sprint

### S00 — Foundation
- `ARCHITECTURE.md`
- `docs/delivery/BUILD.md`
- `docs/sprints/S00-foundation.md`

### S01 — Persistence & Domain
- `docs/design/DOMAIN_MODEL.md`
- `docs/design/DATABASE.md`
- `docs/design/decisions/ADR-001-local-sqlite.md`
- `docs/testing/TEST_PLAN.md`
- `docs/sprints/S01-persistence-domain.md`

### S02 — Security
- `ARCHITECTURE.md`
- `docs/requirements/PROJECT_SPEC.md` (biometría)
- `docs/sprints/S02-security.md`

### S03 — Accounts & Movements
- `docs/design/DOMAIN_MODEL.md`
- `docs/design/BUSINESS_RULES.md`
- `docs/design/DATABASE.md`
- `docs/sprints/S03-accounts-movements.md`

### S04 — Geolocation & Maps
- `ARCHITECTURE.md`
- `docs/design/DATABASE.md`
- `docs/delivery/BUILD.md` cuando cambien permisos/dependencias
- `docs/sprints/S04-geolocation-maps.md`

### S05 — Purchases & Contacts
- `docs/design/DOMAIN_MODEL.md`
- `docs/design/BUSINESS_RULES.md`
- `docs/design/DATABASE.md`
- `docs/sprints/S05-purchases-contacts.md`

### S06 — Shared Expenses
- `docs/design/DOMAIN_MODEL.md`
- `docs/design/BUSINESS_RULES.md`
- `docs/design/DATABASE.md`
- `docs/sprints/S06-shared-expenses.md`
- `.agent/PLANS.md`
- ExecPlan activo si existe

### S07 — Integration & Delivery
- `docs/requirements/TRACEABILITY.md`
- `docs/testing/TEST_PLAN.md`
- `docs/testing/MANUAL_TESTS.md`
- `docs/delivery/BUILD.md`
- `docs/delivery/PRESENTATION.md`
- `docs/sprints/S07-integration-delivery.md`

## Regla para nuevas decisiones

Una conversación no se convierte en fuente de verdad por sí sola. Una decisión aceptada debe terminar en el documento canónico que corresponda: ADR, reglas de negocio, modelo de dominio, BD, sprint o `PROJECT_STATE.md`.