# ADR-002 — Cartografía offline acotada para S00-T06B

**Estado:** ACCEPTED — Orquestador, 2026-10-16.  
**Alcance:** LocalCash académico Android Java/XML; **decisión de diseño**, no prueba de implementación.  
**Requisitos:** LC-P04, LC-MAP-01, LC-TECH-03, LC-MOV-02.  
**Dependencia permitida:** `org.osmdroid:osmdroid-android:6.1.18`.

## Contexto

El proyecto requiere `MapView` y marcadores georreferenciados, demostrables **offline desde primera instalación**, no únicamente después de una visita conectada. osmdroid permite archivos de mosaicos locales y `OfflineTileProvider` sin intentar conexión a Internet. El servidor público `tile.openstreetmap.org` **prohíbe explícitamente crear archivos ZIP/MBTiles mediante descarga masiva, prefetch y uso offline**. Debe usarse dato OSM bajo su licencia, **no los mosaicos del servidor OSMF**.

## Decisión vinculante para el incremento

1. **Fuente de datos:** datos vectoriales de OpenStreetMap (© OpenStreetMap contributors; ODbL 1.0), obtenidos **una sola vez, fuera de Android/CI** mediante una consulta pequeña de Overpass API, filtrando solo vías, parques y elementos de referencia necesarios. Registrar URL/consulta exacta, fecha UTC, tags, sha256 del extracto y atribución en `docs/maps/S00-T06B-data-provenance.md`. El extracto seleccionable se conserva en el repositorio (o, si supera un tamaño razonable, en artefacto verificable con instrucciones de reconstrucción); no pedirlo durante compilación CI ni ejecución de la app. Si Overpass no responde, preparar extracto de la misma cobertura desde otro proveedor de **datos OSM licenciados**, documentando cambio de fuente antes de seguir; nunca descargar tiles de OSMF.
2. **Área declarada de demostración, Santiago Centro:** bbox WGS84 **sur -33.465, oeste -70.675, norte -33.435, este -70.645**; centro inicial **(-33.450, -70.660)**, incluye el GPS simulado de T06A. La cartografía **no pretende cobertura nacional**. Mosaicos Web Mercator `XYZ` de **256×256 PNG** en niveles **z=14,15,16**; se generan todos los mosaicos que intersecten la bbox, con padding de datos razonable cuando corresponda. Controlar los bordes del área e informar claramente que fuera de ella no existe cartografía base offline. No ocultar ni alterar coordenadas que queden fuera.
3. **Render de mosaicos propio y local, sin servidor de tiles:** script Python de mantenimiento `tools/maps/generate_offline_tiles.py` que lea el extracto OSM versionado, pinte una base cartográfica realmente informativa (mínimo vías principales/secundarias, parques y límites reconocibles; no imagen vacía, cuadrícula genérica ni captura de una web), recorte a la cuadrícula XYZ y construya ZIP reproducible. Herramientas solo de generación local/CI (por ejemplo Pillow) **no** se incorporan como dependencia Android. Registrar comando, tamaño, recuento, suma SHA256 y procedencia.
4. **Archivo entregado en APK:** `app/src/main/assets/maps/santiago_centro.zip`. Cada entrada debe seguir exactamente **`LocalCashOSM/{z}/{x}/{y}.png`**, con mayúsculas idénticas en el nombre de fuente (`LocalCashOSM`). Fuente PNG 256px, XYZ (no TMS invertido). Tamaño objetivo **≤ 6 MiB** para el ZIP del demo; si se supera, optimizar el render antes de ampliar cobertura. Incluir los mosaicos necesarios para un centro/mapa legibles con la red apagada.
5. **Uso Android offline exclusivamente:** copiar ZIP desde `assets` a directorio privado `getFilesDir()` cuando la pantalla de mapa se abra por primera vez; no usar almacenamiento externo ni permisos de escritura externos. Abrir desde `OfflineTileProvider(new SimpleRegisterReceiver(context), new File[]{archivo})`, asignar mediante `MapView.setTileProvider(...)`, configurar fuente cuyo **nombre coincida exactamente** con `LocalCashOSM`, desactivar conectividad `MapView.setUseDataConnection(false)` y aplicar límites de zoom/cobertura. No configurar fuente online como fallback ante archivo faltante/corrupto: mostrar error local y continuar usando el resto de la app.
6. **osmdroid normal:** en Java `Configuration.getInstance().setUserAgentValue(getPackageName())`; `MapView` en XML con id `@+id/map`; `onResume/onPause` respectivos. No usar backend, WebView, geocoding ni permisos GPS para visualizar movimientos ya persistidos. Los marcadores se derivan exclusivamente de movimientos con `Ubicacion` válida en SQLite v4, nunca de la última ubicación del dispositivo.
7. **Atribución permanente** visible junto/al pie del mapa: `Datos del mapa © OpenStreetMap contributors · ODbL` y enlace `https://www.openstreetmap.org/copyright`; explicar visualmente `Área offline demostrativa: Santiago Centro (z14–16)`. El estilo/render generado localmente es propio. Adjuntar licencia/atribución en documentación y metadata de provenance de la app si corresponde.

## Alternativas descartadas

- Descargar tiles públicos de `tile.openstreetmap.org` mediante MOBAC/script: **prohibido por política de uso y difícil de reproducir legalmente**.
- Mapa descargado de red al primer inicio / caché preexistente: incumple demostración en instalación limpia offline.
- Convertir a MapLibre o descargar mapas mundiales: cambia stack aprobado y sobrepasa alcance académico.
- Poner marcadores sobre rejilla vacía o imagen decorativa sin calles basadas en OSM: no demuestra una base cartográfica utilizable.

## Gate obligatorio de implementación (Codificador)

- Entregar extracto OSM o procedencia verificable, script reproducible y ZIP de tiles real; comprobar manual y automáticamente estructura `LocalCashOSM/Z/X/Y.png`, conteo, PNG decodificables y un punto dentro de la bbox con calles visibles.
- **Prueba instrumentada de primera instalación:** instalar APK en emulador limpio, dejar red deshabilitada **antes de abrir el mapa por primera vez** (verificar conectividad), autenticar con biometría real del emulador, registrar/consultar punto `-33.45,-70.66` desde SQLite v4; mostrar mosaicos y marcador; pan/zoom z14–16 y ciclo de vida sin crash. Reabrir sin red; no permite fallback online.
- Simular ubicación ausente, movimientos sin GPS y punto fuera de bbox: no crash, sin marcador falso, mensaje de cobertura clara y funcionalidades financieras intactas.
- Capturas, jerarquía UI y logs con evidencia de base cartográfica **no vacía**; `FLAG_SECURE` vigente por T06C puede impedir capturas y se debe probar con evidencia alternativa sin desactivar protección. Build Gradle exit 0, dominio/SQLite/regresión T03–T06C en CI y APK en artefactos. Pedir una auditoría independiente.
- Si la obtención/render de mosaicos no es viable con licencia y trazabilidad verificables, **no entregar mapa vacío como PASS**: documentar bloqueo real y escalarlo al Orquestador, sin inventar resultados.

## Riesgos y límites aceptados

- La cobertura es intencionalmente acotada; marcadores fuera del área de demo pueden carecer de fondo cartográfico. Etiquetarlo.
- El render custom offline puede ser más sencillo que el mapa público normal. Debe ser legible y usar geometría OSM real; no se exige mapa comercial.
- La disponibilidad temporal de Overpass solo influye en la preparación del artefacto, no en el build reproducible ni en la app final.
- Decisión no equivale a validación de ZIP, licencia derivada o pruebas: **todos son entregables obligatorios del Codificador y gates del Auditor**.

## Fuentes verificadas

- Osmdroid, código de `OfflineTileProvider` y ejemplo de uso: https://github.com/osmdroid/osmdroid/blob/master/osmdroid-android/src/main/java/org/osmdroid/tileprovider/modules/OfflineTileProvider.java y https://github.com/osmdroid/osmdroid/blob/master/OpenStreetMapViewer/src/main/java/org/osmdroid/samplefragments/tileproviders/SampleOfflineGemfOnly.java
- `Offline Map Tiles` de osmdroid, nombre de tilesource y archivos: https://github.com/osmdroid/osmdroid/wiki/Offline-Map-Tiles
- OSMF, prohibición explícita de archivo/prefetch de tiles públicos: https://operations.osmfoundation.org/policies/tiles/
- OSMF, atribución y ODbL: https://osmfoundation.org/wiki/Licence/Attribution_Guidelines y https://www.openstreetmap.org/copyright
- Overpass QL: https://wiki.openstreetmap.org/wiki/Overpass_API/Overpass_QL
