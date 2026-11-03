# AV-03 — Avisos y validaciones claras

Autorización: usuario, 2026-11-03. Rol: Codificador. Base: CRUD-02 local, SQLite v7, APK 1.1. Alcance: AV01–AV08 de docs/audits/2026-11-03-validaciones-y-avisos.md y confirmaciones breves de éxito. Sin nuevas dependencias, migraciones ni cambios de reglas financieras.

## Objetivo

Explicar qué impide cada acción y cómo corregirlo; mantener visible el aviso y los datos escritos.

## Contexto

AGENTS, PROJECT_ROUTER, PROJECT_STATE, rol CODER y auditoría AV01–AV08. Archivos: AccionesUI, formularios de compra/reparto/movimiento, detalle de compra, repositorios que explican rechazos y mensaje de diferencia en Reparto.

## Pasos

1. Helpers sencillos de error en campo/texto, aviso persistente y éxito. Validar nombre/productos/cuotas/monto antes de guardar y llevar el foco al error.
2. Alertas de rechazo con motivo funcional, ayuda junto a controles bloqueados y confirmaciones breves; distinguir fallos conocidos de fallos técnicos.
3. Build, regresión de dominio/persistencia y pruebas de mensajes/validación con vistas Android en memoria; handoff y patch contra baseline AV03-before.json.

## Aceptación

- Compra sin nombre/productos, cantidad inválida y precio negativo señalan el motivo correcto; precio cero sigue permitido.
- Cuotas inválidas identifican a la persona; suma distinta muestra total, suma y falta/sobra sin cambiar el cálculo.
- Borrado rechazado conserva datos y muestra una alerta persistente.
- Moneda pendiente y estado fijo explican por qué no se puede continuar.
- Movimiento exige monto positivo; saldo inicial de cuenta conserva permiso de cero/negativo.
- No se muestran SQL ni stacktraces. Escribir y refrescar son resultados distintos cuando la escritura ya se confirmó.
- SQLite continúa en v7; no alteraciones de saldos ni ingresos automáticos.

## Límites de validación

No sustituir pruebas con vistas en memoria por recorrido humano. Control visual del emulador por Computadora requiere permiso propio pendiente; no se sortea esa restricción.

## Resultado de implementación

Implementado localmente; build e instrumentación PASS. Ver docs/handoffs/AV-03.md. No implica gate independiente ni integración en main.
