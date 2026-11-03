# MAP-05 — Mapa mundial offline y ubicación actual

Autorización: solicitud urgente del usuario de corregir el mapa, 2026-11-03; confirma mapa mundial, mantener 100% offline y centrar en ubicación actual. Rama: `fix/world-map-offline`.

## Objetivo
Eliminar la restricción artificial a Santiago Centro y permitir navegación mundial, centrado en GPS actual y consulta de movimientos sin conexión.

## Alcance
Base mundial vectorial incluida; calles de Chile incluidas; importación local de un archivo Mapsforge adicional para calles de otros países. osmdroid se conserva; se incorpora su adaptador oficial Mapsforge 6.1.18 para interpretar estos datos offline. Datos financieros y SQLite no cambian.

## Aceptación
- Primera apertura sin conexión: mapa mundial disponible y calles reales de La Florida renderizadas.
- Mi ubicación pide permisos y centra un fix fresco; sin permisos/GPS, mapa navegable con aviso.
- Se puede desplazar/centrar a París o Tokio y consultar coordenadas sin rechazo territorial.
- Base mundial es de baja resolución: no se promete detalle mundial de calles sin paquetes regionales.
- Datos regionales importables mediante selector Android; formato inválido rechazado sin reemplazar datos.
- Biometría, FLAG_SECURE, saldos y SQLite v7 conservados.
- Build, pruebas unitarias y ejecución en emulador verificables; no declarar prueba física no ejecutada.

Contexto: `PROJECT_ROUTER.md`, PDF docente, `PROJECT_SPEC.md` LC-MAP-01/LC-TECH-03, código `MapaActivity`, `MapaLocalView`, histórico ADR-002.
