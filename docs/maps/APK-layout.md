# Datos OSM dentro del APK — S00-T06B

El archivo fuente versionado es `tools/maps/santiago-centro.osm.xml.gz`, copiado a `app/src/main/assets/maps/`. Durante empaquetado, AAPT expande automáticamente el sufijo .gz: el APK compilado contiene **`assets/maps/santiago-centro.osm.xml`**, sin compresión gzip adicional. Las referencias .gz de metadata/avisos indican el nombre en fuentes, no un nombre de entrada que deba buscarse literalmente en el APK.

Para obtener la base derivada legible por máquina, abrir el APK como ZIP y extraer `assets/maps/santiago-centro.osm.xml`. Ya es XML UTF-8; no hay que descomprimir con gzip ese archivo del APK.
La copia de fuentes y el paquete de entrega también conservan el .gz original. La verificación compara bytes: XML extraído del APK == gzip.decompress(extracto versionado).

- Gzip fuente SHA256: `1aaa9a71b9453178345ac7ea47e273a66187a555368ecabe8ac1924408a3e27d`.
- XML APK/descomprimido SHA256: `8503f464f1fe48655a52b69eb75ca13a1a1069b5e79ee0798cfa65503e46f3b7`.
- Licencia: ODbL 1.0, © OpenStreetMap contributors. URI: https://opendatacommons.org/licenses/odbl/1-0/.
- Avisos/procedencia: `assets/maps/LICENSE-OSM.txt` y `assets/maps/provenance.json`.

El atlas de imágenes no cambia: **`assets/maps/santiago_centro.zip`**, 875101 bytes, SHA256 `f37bc75ae6e1c996d6527b411f805eb7bc304d898920a258d208d7acc605ab1d`. Entradas LocalCashOSM/z/x/y.png. Android solo usa ese ZIP con OfflineTileProvider; no consulta el XML para dibujar.
