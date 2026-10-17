# Procedencia y reproducción — S00-T06B

## Fuente aprobada y fallback
ADR-002 permite otro proveedor de datos OSM licenciados si Overpass no responde. Se documentó el fallback antes de obtener/renderizar: Overpass principal devolvió HTTP502; kumi HTTP500. La API oficial OSM 0.6 rechazó la bbox completa por exceder 50000 nodos; se obtuvieron una sola vez cuatro cuadrantes pequeños de la misma área. No se descargaron mosaicos públicos ni se usa el servidor tile.openstreetmap.org.

La consulta Overpass original está en tools/maps/santiago-centro.overpassql (solo registro del intento; no es dependencia de build). El script acquire_osm.py documenta el fallback manual y selección; NO se ejecuta en CI ni Android. Las cuatro URLs exactas, fechas UTC y hashes SHA256 de respuestas están en tools/maps/download-provenance.json (también en assets/maps/provenance.json). La selección se conserva en tools/maps/santiago-centro.osm.xml.gz: 3279 ways, 15023 nodos referenciados, ordenados por ID OSM. Filtro: highway; waterway; leisure=park/garden; natural=water; sin inventar geometrías. Cada elemento conserva ID y versión del dato fuente.

## Licencia y acceso al extracto
Datos © OpenStreetMap contributors; selección bajo ODbL 1.0: https://opendatacommons.org/licenses/odbl/1-0/. Atribución/copyright: https://www.openstreetmap.org/copyright. No es cartografía del estilo estándar OSM, sino render propio. El código/estilo propio es CC0; no modifica los derechos/avisos ODbL del dato subyacente. Ver LICENSE-OSM.txt.
El mismo extracto comprimido y los avisos están incluidos dentro del APK en assets/maps/, de modo que un receptor puede obtener datos/algoritmo sin depender de acceso al repositorio privado: abrir el APK como ZIP y descomprimir el .gz. Entregar estos avisos con el atlas.

## Cobertura y límites
Bbox WGS84 sur -33.465, oeste -70.675, norte -33.435, este -70.645. Centro (-33.450,-70.660), z14/15/16. Se dibujan todos los XYZ que intersectan la bbox; un tile de borde puede extenderse fuera de ella. La API devuelve completos los ways/nodos de borde; fuera del área declarada no se promete cartografía. Los puntos exteriores conservan coordenadas y se advierten en la pantalla.

## Reproducción sin red
Python 3.12 y Pillow 12.3.0, instalado solo como herramienta de mantenimiento/CI. No se incluye Pillow en Android.

```sh
python -m pip install -r tools/maps/requirements.txt
python tools/maps/generate_offline_tiles.py --output /tmp/atlas/santiago_centro.zip --preview /tmp/atlas/preview.png
cmp app/src/main/assets/maps/santiago_centro.zip /tmp/atlas/santiago_centro.zip
python tools/maps/verify_atlas.py
```

ZIP con orden estable, fechas fijas, permisos estables y PNG RGB 256px; sin timestamps dinámicos. Solo entradas LocalCashOSM/{z}/{x}/{y}.png (XYZ, no TMS). Sin fuente/fallback online en Android ni durante build. Fuente, tamaño, hashes y motor se conservan en assets/maps/metadata.json.

## Identidad del atlas
- ZIP: 64 PNG, 875101 bytes.
- ZIP SHA256: `f37bc75ae6e1c996d6527b411f805eb7bc304d898920a258d208d7acc605ab1d`.
- Extracto gzip SHA256: `1aaa9a71b9453178345ac7ea47e273a66187a555368ecabe8ac1924408a3e27d`.
- XML descomprimido SHA256: `8503f464f1fe48655a52b69eb75ca13a1a1069b5e79ee0798cfa65503e46f3b7`.

## Evidencia
Ver docs/testing/S00-T06B.md y handoff de implementación. Este documento por sí solo no acredita PASS de emulador. La previsualización generada sirve para inspeccionar calles/parques; la instrumentación debe demostrar carga mediante OfflineTileProvider y dibujo de MapView con marcador manteniendo FLAG_SECURE.
