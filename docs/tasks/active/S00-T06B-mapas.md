# Task Brief preliminar — S00-T06B: mapa georreferenciado sin conexión

**Sprint:** S00 (subtarea T06) · **Rol:** Codificador · **Estado:** DRAFT (no autorizada hasta resolver cobertura de tiles offline).
**Rama futura:** `task/S00-T06B-mapas`, desde el `main` que integre T06A aprobada.
**Objetivo:** ofrecer una pantalla Android Java/XML con `osmdroid 6.1.18` que represente movimientos con coordenadas desde SQLite y pueda demostrarse en emulador sin conexión dentro de un área definida.

## Contexto

Norma: `LocalCash Proyecto Android.pdf`, pilar de mapas y funcionamiento 100% local/offline. Requisitos `LC-P04`, `LC-MAP-01`, `LC-TECH-03` y `LC-MOV-02` (T06A). Consultar `PROJECT_ROUTER.md`, `AGENTS.md`, `PROJECT_STATE.md`, rol `CODER.md`, `ARCHITECTURE.md`, `docs/sprints/S04-geolocation-maps.md`, `docs/design/DATABASE.md`, `docs/delivery/BUILD.md` y código de T06A.

## Alcance previsto

1. Añadir solo dependencia aprobada `osmdroid 6.1.18` y configurar `MapView` en Java/XML. Ajustar permisos/manifest estrictamente necesarios y `Configuration.getInstance().setUserAgentValue(getPackageName())`.
2. Vista navegable desde cuentas/movimientos para explorar puntos **solo cuando** exista ubicación persistida, con marcadores y detalle básico (sin duplicar datos). Pantalla vacía informativa sin coordenadas.
3. `MapView` con `onResume`/`onPause`; zoom y desplazamiento funcionales, sin requerir API externa.
4. Proporcionar **tiles/base cartográfica offline verificables en instalación nueva** para una zona de demostración documentada, con fuente, licencia y atribución correctamente identificadas; si la cobertura se limita a una zona, reflejarlo explícitamente. No aceptar un mapa vacío o tiles obtenidos de red como prueba del requisito offline. Elegir formato compatible, tamaño acotado y modo de empaquetado tras decisión de diseño.
5. Sin introducir backend, geocodificación, descargas automáticas o nuevas funcionalidades de compras/deudas.

## Validación y criterios previstos

- [ ] Pantalla osmdroid abre y muestra marcadores de movimientos geolocalizados reales consultados de SQLite.
- [ ] Los movimientos sin coordenadas no producen marcador ni error.
- [ ] Instalar APK desde cero en emulador limpio, **desactivar red antes del primer mapa** y visualizar base cartográfica y marcadores en área de prueba; capturas y evidencias.
- [ ] Pan/zoom y lifecycle no fallan; ubicación denegada no impide consultar movimientos con coordenadas ya persistidas.
- [ ] Regresión de SQLite v4, cuentas, movimientos y deudas; build SHA exacto con exit 0, pruebas CI/emulador e informe de handoff.

## Bloqueador de planificación, no defecto de código

La fuente actual aún no define cómo garantizar tiles offline. El Orquestador debe seleccionar/documentar con precisión cobertura, formato y licencia del recurso cartográfico **antes** de pasar este Brief a READY. No exigir base cartográfica mundial sin conexión para un MVP universitario. Sin esa decisión, esta subtarea no está autorizada para implementación.
