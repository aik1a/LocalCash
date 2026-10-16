# Task Brief — S00-T06B: mapa georreferenciado offline (osmdroid)

**Sprint:** S00 (subtarea T06) · **Rol autorizado:** Codificador · **Estado:** READY — 2026-10-16 (ADR-002 aceptado).  
**Rama:** `task/S00-T06B-mapas`, creada desde `main` posterior a PR #7 (T06C).  
**Objetivo:** visualizar movimientos con coordenadas persistidas en SQLite v4 sobre **cartografía real empaquetada y funcional sin red desde la primera instalación**, usando osmdroid 6.1.18.

## Context Pack mínimo y requisitos

- Fuente normativa: `LocalCash Proyecto Android.pdf`; IDs `LC-P04`, `LC-MAP-01`, `LC-TECH-03`, `LC-MOV-02`. Regresión `LC-AUTH-01`.
- Leer `PROJECT_ROUTER.md`, `AGENTS.md`, `PROJECT_STATE.md`, `docs/agent/roles/CODER.md`, este Task Brief, **`docs/design/decisions/ADR-002-osmdroid-cartografia-offline.md` (vinculante)**, `ARCHITECTURE.md`, `docs/design/DATABASE.md`, `docs/sprints/S04-geolocation-maps.md`, `docs/testing/S00-T06C.md` y código real T06A/T06C.
- Java/XML/Groovy; `minSdk 24`; SQLite v4 como única persistencia financiera, biometría previa intacta.
- Si surge duda de licencia/origen del dato, compatibilidad real de formatos o alcance obligatorio del PDF, escalar al Orquestador **antes de sustituir** la decisión ADR.

## Alcance autorizado

1. Añadir **solo** dependencia Android directa `org.osmdroid:osmdroid-android:6.1.18`, compatible con stack existente. `MapView` XML id `@+id/map`, inicialización/configuración con `Configuration.getInstance().setUserAgentValue(getPackageName())`, lifecycle y zoom/pan. No agregar plataforma de mapas alternativa.
2. Añadir pantalla de mapa protegida por **`ActividadProtegida`** (T06C). Navegación natural desde pantalla de cuenta y movimientos; acceso directo/intents sin autenticación deben activar gate sin mostrar datos ni mapa privado.
3. Listar **solo movimientos con `Ubicacion` válida** de la cuenta solicitada a través de Service/Repository existente; dibujar un marcador por movimiento, detalle básico al tocarlo (tipo, monto y descripción o fecha), sin introducir SQL en UI ni modificar datos. Sin coordenadas: mensaje vacío informativo. Puntos fuera de cobertura: advertir claramente, no inventar/falsificar ubicación ni impedir ver operaciones financieras.
4. Producir y empaquetar un **atlas cartográfico propio derivado de datos OSM abiertos**, nunca copiando tiles de servidores públicos: cumplir íntegramente **ADR-002**. Cobertura demo **Santiago Centro WGS84** [sur -33.465, oeste -70.675, norte -33.435, este -70.645], centro (-33.450, -70.660), zoom 14–16; tiles 256px PNG XYZ bajo estructura `LocalCashOSM/{z}/{x}/{y}.png`. La representación debe contener calles/elementos reconocibles derivados de OSM, no un fondo genérico.
5. Crear script sencillo reproducible `tools/maps/generate_offline_tiles.py` y guardar extracto/versionado, procedencia de datos OSM, licencia, fecha, consulta, hash y comando en `docs/maps/S00-T06B-data-provenance.md`. Herramientas de generación pertenecen a desarrollo, **no** a dependencias runtime Android.
6. Incluir `app/src/main/assets/maps/santiago_centro.zip` (objetivo ≤6 MiB); copiar a `getFilesDir()` al primer uso y configurar **`OfflineTileProvider` sin fallback online**. Manejar ausencia/corrupción del ZIP informando fallo local sin crash. No solicitar permiso externo de archivos ni conexión a Internet para abrir mapa. Mostrar atribución permanente visible `Datos del mapa © OpenStreetMap contributors · ODbL` con vínculo a `https://www.openstreetmap.org/copyright` y mensaje de cobertura limitada.
7. Mantener intactas todas las funciones T03–T06C (biometría, ubicación opcional, compras, reparto/deudas, SQLite v4). No tracking, descargas de tiles runtime, geocoding, cuentas nuevas, backend, pagos ni refactors extensos.

## Criterios de aceptación verificables

- [ ] Un archivo ZIP **real no vacío**, con fuente/tilepath `LocalCashOSM/z/x/y.png`, puede decodificarse y cargarse en osmdroid 6.1.18. El mapa representa **calles de Santiago Centro** derivadas de datos OSM, con zoom 14–16 y navegación pan funcional.
- [ ] **Instalación limpia en emulador sin red antes de la primera apertura del mapa**: se registran/conservan coordenadas del movimiento `(-33.45,-70.66)`, se autentica el usuario mediante biometría Android y aparecen mapa base offline y marcador correcto. Evidencia instrumentada del estado de conectividad, archivos, fuente y tile/marker visibles (en `FLAG_SECURE`, validar XML/API + PNG de archivo sin deshabilitar protección).
- [ ] Marcadores solo para registros georreferenciados de la cuenta. Movimientos sin ubicación no generan marcador, ni crash. El mapa sin puntos muestra estado claro; fuera de bbox muestra advertencia de límites de cobertura.
- [ ] Cancelación/error biométrico, reentrada desde segundo plano y acceso directo a `MapaActivity` no permiten leer datos ni abrir pantalla privada sin nuevo éxito del prompt. No introducir bypass en pruebas.
- [ ] `MapView.onResume/onPause` y reapertura de la app no producen fugas/crash; ZIP ausente/corrupto se gestiona sin habilitar descarga online.
- [ ] Atribución OSM/ODbL y procedencia/licencia del extracto verificables; **no se descargan tiles de tile.openstreetmap.org** ni se usa red como requisito de mapa.
- [ ] Gradle `:app:assembleDebug :app:assembleDebugAndroidTest` SHA exacto PASS retorno 0; checks de dominio, integración SQLite v4, T03–T06C, GPS/sin GPS y biometría API24/API36 pertinente PASS. Tests destructivos solo en emuladores desechables.
- [ ] APK, logs, evidence del SHA, tamaño/hash de ZIP, pruebas offline, documento `docs/testing/S00-T06B.md`, handoff en `docs/handoffs/S00-T06B-coder.md` y limitaciones identificadas.

## Fuera de alcance

- Cobertura global o fuera del polígono demo; mapas comerciales online; sincronización/red requerida; descarga/prefetch masivo de tiles públicos OSM; render/vector engine Android nuevo; geocoding; tracking GPS; nueva migración SQLite; firma/upgrade histórico de APK (T07).
- Auditoría propia, merge, cierre administrativo, cambios a `PROJECT_STATE.md` o cierre S00.

## Validación y entrega

- Implementar en la rama designada, usando ExecPlan si la generación/atlas involucra varios pasos.
- CI debe probar mapa con red **desactivada desde antes de primera apertura** sobre emulador **recién instalado**. No usar caché acumulada. Probar centro de cobertura, ausencia de coordenadas, área exterior, ciclo de vida y biometría.
- Si los datos OSM/render/ZIP no cumplen el ADR o se descubre un bloqueo verificable, detener afirmaciones de completitud y escalar al Orquestador. No sustituir por cuadrícula sin cartografía ni tiles OSM públicos descargados.
- Handoff con commit exacto, run CI, APK, hashes y limitaciones. **Una auditoría independiente** al terminar; Codificador no fusiona ni cierra tarea/sprint.
