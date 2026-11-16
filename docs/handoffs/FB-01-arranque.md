# FB-01 — Handoff de arranque documental

Fecha: 2026-11-16. Rol: Orquestador.

## Solicitud y alcance

El usuario confirma Firebase como sustituto de SQLite, Google Auth y operación offline; solicita documentar fases, publicar la planificación, preparar ramas y fijar protocolos. Esta entrega modifica únicamente documentación. No inicia implementación ni configura Firebase.

## Base y publicación

Base funcional: main c28a27b87aeeb092a936b8ba6b05b83404ac1c13. Publicar etiqueta baseline/sqlite-v7-20261116 sin moverla posteriormente. Publicar docs/firebase-roadmap y abrir PR hacia main; al integrarlo, crear feat/firebase-migration desde el merge exacto. Verificar los resultados mediante referencias remotas y estado del PR; este documento no acredita anticipadamente que una operación remota haya terminado.

## Entregables

- ExecPlan docs/exec-plans/active/FB-01-firebase-google-offline.md: fases F0–F6, límites, aceptación y decisiones pendientes.
- ADR-003: procedencia de la nueva instrucción docente, sustitución del alcance anterior y propuesta Firestore.
- FIREBASE_PROTOCOL.md: ramas, roles, auditoría independiente, severidades, evidencia, límites y recuperación.
- Brief FB-F0-T02 en active: inventario y conciliación de baseline.
- Entradas en router, estado, AGENTS, especificación y trazabilidad.

## Validación y límites

Revisión documental, enlaces locales y git diff --check; los resultados finales se verifican antes del commit. Sin build ni pruebas Android/Firebase por no cambiar código compilable. Sin auditoría funcional independiente ni cierre de F0. La publicación documental usa la excepción de orquestación de TASK_LIFECYCLE.md y la instrucción explícita del usuario.

## Continuidad

Siguiente trabajo: ejecutar FB-F0-T02 desde feat/firebase-migration con el brief versionado. Completar F0-T03: determinar proyecto/cuenta Firebase, instalaciones a conservar y firma. Después abrir tareas F1 de prototipo offline/integridad. No empezar migración masiva ni asumir que la caché resuelve concurrencia o pérdida de datos.
