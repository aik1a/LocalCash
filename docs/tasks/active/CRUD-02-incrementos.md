# Task Briefs — CRUD-02 T00–T08

Autorización: usuario, 2026-11-03. Rol de implementación: Codificador. Base: copia local UX-01, SQLite v6. Estado: implementado localmente, pendiente revisión independiente. Sin commit ni merge; patch contra baseline guardado.

## Objetivo y criterios por tarea

Cada fila del plan aprobado es una unidad verificable; la última columna es su criterio de aceptación principal.

| ID | Objetivo y alcance | Persistencia | Aceptación |
|---|---|---|---|
| T00 | Preparar continuidad: guardar baseline revisable de la copia, comprobar firma de actualización y respaldo recuperable antes de tocar datos reales | Sin cambios | Se distingue copia local de main; actualización no depende de desinstalar ni borrar datos |
| T01 | Admitir estado real de deuda en esquema, modelo y lecturas; UI deja de fijar Pendiente como texto constante | v6→v7 | Instalación nueva y upgrades soportados conservan filas, IDs, importes y cuotas; todas las deudas antiguas siguen pendientes; sin cargos ni cobros retroactivos |
| T02 | Marcar pagada/deshacer desde reparto, con confirmación por contacto/compra/monto; tratar $0 como Sin deuda | UPDATE del estado en v7, sin migración nueva | Reabrir conserva estado; doble toque es inocuo; deshacer vuelve a pendiente; cuenta y reparto conservan montos; no permite cobrar cero |
| T03 | Editar nombre de contacto desde su lista/detalle | UPDATE nombre | Nombre corregido aparece en repartos y compras existentes; mismo ID y mismas deudas; vacío rechazado |
| T04 | Renombrar cuenta | UPDATE nombre | Mismo ID, moneda, saldo inicial, historial y vínculos; vacío rechazado |
| T05 | Eliminar contacto sin referencias y cuenta sin movimientos, con confirmación | DELETE condicionado; conservar FK | Registros sin uso se borran; utilizados se conservan y se explica motivo; comprobar dentro de operación, no solo al abrir diálogo |
| T06 | Editar compra PLANIFICADA sin pago/reparto: nombre, fecha, contacto e ítems | UPDATE/INSERT/DELETE de detalles en transacción; sin esquema nuevo | Mismo ID, al menos un ítem, total recalculado y precisión correcta; fallo revierte todo; no cambia saldo ni habilita cambiar moneda asignada |
| T07 | Eliminar planificación que ya no se realizará, solo si no tiene pago/reparto | Borrado explícito de detalles y cabecera en transacción | Confirmación, sin registros huérfanos ni cambios de saldo; compra realizada o con dependencias se rechaza |
| T08 | Consultar deudas desde un contacto | Consultas por contacto, sin esquema nuevo inicialmente | Lista con compra, importe, moneda y estado; totales pendientes separados por moneda; cuotas cero excluidas del cobro |

## Context Pack

AGENTS.md, PROJECT_ROUTER.md, PROJECT_STATE.md, rol CODER, plan CRUD-02, auditoría CRUD local, BUSINESS_RULES.md, DATABASE.md, DOMAIN_MODEL.md y código Java/XML del módulo afectado.

## Fuera de alcance

Segunda ronda T09–T11, conversión, cobros que creen movimientos, backend y dependencias nuevas.

## Validación y entrega

Build APK/pruebas, instrumentación de migración v6→v7 y CRUD, regresiones de divisas/pagos, snapshot antes/después de actualización con datos. Entrega: patch/código, evidencia, documentación y handoff CRUD-02.md. Aceptación técnica independiente y recorrido manual siguen pendientes.
