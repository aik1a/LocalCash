# S04 — Geolocation & Maps

## Objetivo

Permitir ubicación opcional en movimientos y visualizarlos en osmdroid.

## Tareas

- Integrar `Ubicacion` con `Movimiento`.
- Persistir latitud/longitud opcionales.
- Configurar osmdroid 6.1.18 y `MapView`.
- Configurar `Configuration.getInstance().setUserAgentValue(getPackageName())`.
- Implementar ciclo de vida necesario (`onResume`/`onPause`).
- Mostrar movimientos geolocalizados.
- Manejar ausencia de ubicación/permisos sin bloquear registro financiero.
- Pruebas y documentación.

## Criterios de aceptación

- Un movimiento puede guardarse sin GPS.
- Coordenadas válidas sobreviven persistencia.
- El mapa puede representar un movimiento geolocalizado.
- Ausencia/rechazo de ubicación no provoca crash ni impide un movimiento sin ubicación.