# S01-T01 — Divisas y configuración inicial

Autorización: Aikia, conversación del 18-10-2026: implementar selector de 10 divisas,
configuración inicial persistente y divisa por cuenta. Base main 171aacfb15f8a4974ce228e33fef08e841f6bc46.
Rama: task/S01-T01-divisas. Trabajo adicional a S00 cerrado; sin merge automático.

## Objetivo y alcance
Elegir moneda principal una vez, guardarla en SQLite y usarla como selección inicial
al crear cuentas/compras. Catálogo CLP, USD, EUR, ARS, MXN, COP, PEN, BRL, GBP, JPY.
CLP/JPY: cero decimales; restantes: dos. Movimientos heredan cuenta y deudas compra.
Mantener Java/XML, SQLiteOpenHelper y biometría. Sin conversión ni total entre monedas.

## Criterios de aceptación
- Configuración solicitada después de autenticar mientras falte; reinicios la conservan.
- Crear cuentas/compras con moneda explícita, montos y formato coherentes; mostrar código.
- Reparto equitativo CLP/JPY en pesos/yenes enteros y suma exacta.
- Migración v1–v4 → v5 aditiva: conservar importes, IDs, FK y ubicación; registros previos
  sin divisa requieren asignación explícita, nunca conversión o redondeo silencioso.
- Rechazar asignar CLP/JPY si existen fracciones, incluso deudas antiguas.
- No cambiar moneda ya asignada ni aceptar movimientos nuevos sin moneda definida.
- Pruebas de dominio, SQLite/migración y build Android; documentar límites de entorno.
- Handoff y PR para revisión; no modificar cierre/auditoría histórica de S00.

## Ampliación autorizada durante implementación (18-10-2026)
Aikia solicita incluir al usuario como «Tú» en el reparto (basta un contacto), eliminar
numerales/IDs visibles y quitar aclaraciones técnicas entre paréntesis de formularios.
Criterios adicionales: cuota propia persistida como participación, no deuda ficticia;
reparto personalizado valida suma de Tú + contactos; reparto previo conserva sus cuotas;
UI sin #id y etiquetas Cantidad/Precio unitario/Monto con ejemplos.
