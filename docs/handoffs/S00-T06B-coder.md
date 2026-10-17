# Handoff — S00-T06B
**Rol saliente:** Codificador  
**Fecha:** 2026-10-17  
**Sprint:** S00  
**Rama:** `task/S00-T06B-mapas`  
**Base:** `9f13e3036aad9815020df3b59bea66a357f1fc9b`  
**SHA funcional:** `c289b0614d9d891c6ea4cfcdeec48dabc342c459`  
**CI:** https://github.com/aik1a/LocalCash/actions/runs/39042930625  

## Objetivo ejecutado
Visualizar movimientos persistidos con ubicación sobre cartografía real offline desde primera apertura, con osmdroid 6.1.18 y biometría existente intacta.

## Estado
**DONE — implementación y validación, pendiente auditoría independiente**. La tarea continúa abierta, pendiente de una auditoría independiente. Sin merge, cierre ni cambios en PROJECT_STATE.md/ADR-002.

## Implementación
- Única dependencia Android nueva: org.osmdroid:osmdroid-android:6.1.18; Java/XML/Groovy y minSdk24/target36 intactos.
- MapaActivity hereda ActividadProtegida. Entrada desde CuentaActivity y guard ante intents internos directos sin éxito biométrico.
- MovimientoService/Ubicacion existentes alimentan marcadores de la cuenta, con tipo/monto/descripción/fecha al tocar. Registros sin ubicación no crean marcador; coordenadas exteriores se conservan y se advierten. Zoom 14–16, pan, centrado y restauración/lifecycle.
- AtlasOffline copia/valida ZIP desde APK a filesDir, SHA256 y estructura antes de usar. Ausencia de copia privada se resuelve desde APK; corrupción produce error local controlado. No habilita red.
- MapaLocalView es un MapView XML id map. Recibe OfflineTileProvider directamente en el constructor de MapView para asignar el proveedor antes de dibujar y evitar crear temporalmente un MapTileProviderBasic. Esta asignación por constructor conserva la decisión de proveedor/archivo local del ADR; no hay fuente online ni descargador. setUseDataConnection(false), fuente LocalCashOSM y rutas PNG XYZ.
- Atribución OSM/ODbL permanente con enlace; extracto legible por máquina, procedencia/licencia y metadata disponibles dentro del APK.
- Sin cambios en servicios financieros, repositorios, dominio, SQLite v4 ni reglas de ubicación/cuentas/compras/deudas. Mapa no solicita GPS. Sesión biométrica productiva sin modificaciones.

## Cartografía y licencia
Overpass principal HTTP502 y kumi HTTP500. Se documentó el fallback permitido por ADR-002 a la API oficial OSM; la bbox completa excedía 50000 nodos, por lo que se solicitaron una sola vez cuatro cuadrantes acotados de la misma cobertura. Se seleccionaron 3279 ways y 15023 nodos referenciados. URLs/fechas UTC/hashes/filtros en tools/maps/download-provenance.json; extracto versionado tools/maps/santiago-centro.osm.xml.gz.
Datos © OpenStreetMap contributors, ODbL 1.0. Extracto y avisos incluidos en assets/maps para acceso sin depender de permisos del repositorio privado. No se descargaron tiles de OSMF.
64 PNG 256px XYZ z14/15/16, bbox [-33.465,-70.675,-33.435,-70.645], centro [-33.450,-70.660].
ZIP 875101 bytes, SHA256 `f37bc75ae6e1c996d6527b411f805eb7bc304d898920a258d208d7acc605ab1d`.
Extracto gzip SHA256 `1aaa9a71b9453178345ac7ea47e273a66187a555368ecabe8ac1924408a3e27d`.
Script tools/maps/generate_offline_tiles.py (Python 3.12/Pillow12.3.0); CI regenera sin red OSM y compara bit a bit. El script de adquisición es mantenimiento manual, no build/runtime. Ver docs/maps/S00-T06B-data-provenance.md.

## Archivos modificados
- `.github/workflows/s00-t06b-build.yml`
- `app/build.gradle`
- `app/src/androidTest/java/com/aik1a/localcash/BiometricChecks.java`
- `app/src/androidTest/java/com/aik1a/localcash/FlowChecks.java`
- `app/src/androidTest/java/com/aik1a/localcash/MapChecks.java`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/assets/maps/LICENSE-OSM.txt`
- `app/src/main/assets/maps/metadata.json`
- `app/src/main/assets/maps/provenance.json`
- `app/src/main/assets/maps/santiago-centro.osm.xml.gz`
- `app/src/main/assets/maps/santiago_centro.zip`
- `app/src/main/java/com/aik1a/localcash/presentation/AtlasOffline.java`
- `app/src/main/java/com/aik1a/localcash/presentation/CuentaActivity.java`
- `app/src/main/java/com/aik1a/localcash/presentation/MapaActivity.java`
- `app/src/main/java/com/aik1a/localcash/presentation/MapaLocalView.java`
- `app/src/main/res/layout/activity_cuenta.xml`
- `app/src/main/res/layout/activity_mapa.xml`
- `docs/exec-plans/active/S00-T06B.md`
- `docs/maps/LICENSE-OSM.txt`
- `docs/maps/S00-T06B-data-provenance.md`
- `docs/maps/source-fallback.md`
- `docs/testing/S00-T06B.md`
- `tests/biometric-emulator.py`
- `tests/maps-emulator.py`
- `tools/maps/acquire_osm.py`
- `tools/maps/download-provenance.json`
- `tools/maps/generate_offline_tiles.py`
- `tools/maps/requirements.txt`
- `tools/maps/santiago-centro.osm.xml.gz`
- `tools/maps/santiago-centro.overpassql`
- `tools/maps/verify_atlas.py`

## Validación ejecutada
| Verificación | Comando/escenario | Resultado |
|---|---|---|
| Build API24/API36, SHA exacto | :app:assembleDebug :app:assembleDebugAndroidTest | PASS; retorno 0 en ambos |
| Dominio | bash tests/domain-checks.sh | PASS; 5483 comprobaciones por job |
| Atlas | verify_atlas.py + generar y cmp | PASS; 64 PNG/XYZ z14–16 y regeneración bit a bit |
| Biometría | prepare/enroll/security, sensor del AVD | PASS en ambas APIs; 8 rutas protegidas |
| Primera apertura offline | maps-emulator.py + MapChecks | PASS API24/API36; sin red global y sin conexión activa, sin copia previa |
| Base/marcadores | proveedor real, igualdad píxeles PNG por zoom, render MapView | PASS; 587/505/516 colores y 781 píxeles del marcador |
| UI/lifecycle | cuenta→mapa, pan/zoom/detalles, recreación, background/cancelación/reintento/reapertura | PASS ambas APIs |
| Ausencia/corrupción/cobertura | sin GPS, otra cuenta, punto exterior, archivo corrupto/ausente | PASS; sin marcador falso/fallback, error controlado |
| Regresión T03–T06A y SQLite | flujos instrumentados y force-stop/relanzamiento API36 | PASS; cuentas/compras/deudas/GPS/permisos/migraciones v1/v2/v3→v4 |
| Artefactos | digest ZIP GitHub, APK contra CI, atlas y XML extraídos del APK | PASS |

## Evidencia y APK
APK seleccionado del job API36: `LocalCash-S00-T06B-debug.apk`.
SHA256 APK: `c4161293f2b0f1bb7f145c5bb13ec7455b150ffff0d7867277a00a7a154384db`.
- Artefacto API24: https://github.com/aik1a/LocalCash/actions/runs/39042930625/artifacts/12104783307
- Artefacto API36: https://github.com/aik1a/LocalCash/actions/runs/39042930625/artifacts/12104643986
- Paquete persistente: `LocalCash-S00-T06B-APK-evidencia.zip`; APK, atlas/extracto/avisos, documentación, logs/XML/PNG y ZIP originales de Actions.
- Logs/manifest seleccionados versionados bajo `docs/handoffs/evidence/S00-T06B/`.
- Empaquetado verificado: AAPT expande el asset fuente .gz a `assets/maps/santiago-centro.osm.xml` dentro del APK. XML SHA256 `8503f464f1fe48655a52b69eb75ca13a1a1069b5e79ee0798cfa65503e46f3b7`, idéntico a descomprimir el extracto versionado. Ver `docs/maps/APK-layout.md` para extraerlo; las referencias .gz en metadatos/avisos de fuente designan el asset antes del empaquetado. El ZIP PNG permanece idéntico con su nombre obligatorio.

## Correcciones durante validación
- Primera ejecución 39041332830: API36 pasó mapa offline y regresión completa; API24 se detuvo antes del mapa por svc wifi disable con retorno137.
- Se registran retornos de comandos y se exige ausencia de red global predeterminada más ConnectivityManager sin conexión activa; no se infiere desconexión por ejecutar un comando. ADB con tiempos de espera; AVD 2GiB.
- Segunda ejecución 39042257492: API24 confirmó Active default network:none; la comprobación shell run-as ... test -f no era compatible con Android7. Se eliminó esa comprobación duplicada; MapChecks conserva obligatoriamente File.exists()==false antes del primer mapa.
- API36 de la segunda ejecución fue cancelada al publicar la corrección; no se presenta como resultado final.
- Las correcciones fueron de instrumentación/preparación; no se introdujeron bypass, callbacks de éxito falsos ni modificación de la sesión. El resultado final es exclusivamente el SHA/run indicados.

## Documentación y trazabilidad entregada
ExecPlan, docs/testing/S00-T06B.md, docs/maps/S00-T06B-data-provenance.md, avisos de licencia, evidencia y este handoff. Requisitos LC-P04/LC-MAP-01/LC-TECH-03/LC-MOV-02 y regresión LC-AUTH-01. El Orquestador mantiene cierre/estado y trazabilidad administrativa.

## Límites reales
- Cartografía acotada a la bbox de Santiago Centro, z14–16, estilo simple propio. Fuera de ella no se promete base; los registros/posiciones originales permanecen disponibles.
- Pruebas en AVD API24/API36, no dispositivo físico. Firma/upgrade histórico con datos reales pendientes de T07.
- Capturas convencionales pueden ser negras por FLAG_SECURE. Los PNG del proveedor y el render de MapView se obtienen por instrumentación tras autenticación, conservando el flag; fixtures sintéticos, sin datos reales.
- Los tests borran/preparan SQLite y corrompen/eliminan copias del atlas solo en emuladores desechables. No ejecutar el APK instrumentado sobre datos reales.

## Contexto para el siguiente agente
Leer Task Brief, ADR-002, rol Auditor, documento de pruebas, procedencia y este handoff. Comparar el SHA funcional con base, revisar CI/logs y APK/hash; comprobar los PNG/render y la conectividad previa a primera apertura. El commit posterior de documentación/evidencia no modifica el código/APK validado.

## Próxima acción
Una única auditoría independiente de S00-T06B. El Codificador no emite veredicto independiente ni autoriza merge/cierre.
