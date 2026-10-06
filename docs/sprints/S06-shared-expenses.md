# S06 — Shared Expenses & Debts

## Objetivo

Implementar división de productos/servicios entre contactos y generar montos/deudas claras por participante.

## Planificación

Crear un ExecPlan antes de implementar este sprint porque cruza dominio, reglas de negocio, persistencia y UI.

## Tareas previstas

- Cerrar algoritmo de reparto equitativo/personalizado y redondeos.
- Implementar participantes y asignaciones.
- Generar deudas por contacto.
- Validar suma de asignaciones contra total.
- Marcar deuda pagada según alcance aprobado.
- Resolver explícitamente pagos parciales sí/no.
- Integrar UI.
- Pruebas de casos normales y bordes.
- Documentación/trazabilidad.

## Criterios de aceptación

- Compra compartida detalla productos/servicios.
- Participantes provienen de contactos.
- Cada participante tiene un monto inequívoco.
- Distribución no pierde/crea dinero por errores de redondeo según regla aprobada.
- Persistencia y cálculo permanecen consistentes tras recarga.