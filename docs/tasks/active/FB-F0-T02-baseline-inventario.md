# FB-F0-T02 — Baseline e inventario para migración

Estado: IN_PROGRESS — entrega documental preparada y verificada para PR; pendiente de integración/cierre administrativo. Rol: Orquestador. Fase F0 del roadmap FB-01.

## Objetivo

Aplicar criterio MVP académico: inventario conciso centrado en operaciones reales y riesgos concretos; no proponer nuevas capas, frameworks, funcionalidades ni una reescritura general como resultado de esta tarea.

Producir el inventario del código integrado y sus garantías para que F1 diseñe Firebase sobre hechos comprobados.

## Contexto obligatorio

PROJECT_ROUTER.md, PROJECT_STATE.md, AGENTS.md, docs/agent/roles/ORCHESTRATOR.md, docs/agent/FIREBASE_PROTOCOL.md, ADR-003 y ExecPlan FB-01. Consultar selectivamente repositorios, servicios, dominio, manifest y pruebas Android/JUnit. Base funcional c28a27b87aeeb092a936b8ba6b05b83404ac1c13; base operativa será el SHA de feat/firebase-migration al iniciar.

## Alcance

- Contrastar PR #16, esquema v7 y versión APK con estado/documentación.
- Inventariar operaciones de las nueve tablas, lectores de helper, servicios transaccionales, triggers, reglas de borrado/moneda y referencias long.
- Relacionar pantallas y runners actuales con módulos, pruebas y filtros de CI; distinguir evidencia histórica de pruebas repetidas.
- Elaborar matriz operación → invariante → garantía SQLite actual → riesgo Firestore → prueba F1 requerida.
- Registrar discrepancias documentales actuales y actualizar descripciones operativas afectadas preservando el historial.

## Fuera de alcance

Implementar Firebase, cambiar dominio, agregar dependencias, configurar servicios remotos o importar datos reales.

## Aceptación

- [x] Inventario con rutas reales y SHA, sin suponer que una garantía SQLite existe en Firestore.
- [x] Matriz cubre cuentas/movimientos, compra/pago, reparto/deudas, configuración y borrados.
- [x] Estado concilia merges actuales; evidencia y pendientes diferenciados.
- [x] Estrategia para completar F0-T03 y decisiones F1 enumeradas.

## Validación, entrega y rama

Rama task/FB-F0-T02-baseline-inventario desde feat/firebase-migration; destino PR: feat/firebase-migration. Commits/push documentales autorizados. Entregar docs/design/FIREBASE_BASELINE.md, diff documental pertinente y handoff conforme a docs/agent/HANDOFF_TEMPLATE.md. Verificar enlaces/diff/coherencia; no exigir build por documentos ni reportar pruebas Android no ejecutadas. Cierre documental por Orquestador; F0 no cierra hasta resolver también F0-T03. No iniciar F1 solo por completar este inventario.

## Entrega documental — 2026-11-16

[Baseline](../../design/FIREBASE_BASELINE.md) y [handoff](../../handoffs/FB-F0-T02-baseline-inventario.md). Criterios contrastados por el Orquestador; no auditoría independiente. Checks marcados corresponden a contenido/documentación, no aprobación funcional de Firebase. No fusionar ni cerrar F0 como efecto de esta entrega.
