# Cartografía offline

El APK incluye un mapa mundial básico y un mapa detallado de Chile en formato Mapsforge. `tools/maps/prepare_vector_maps.py` y la tarea Gradle `prepareOfflineMaps` descargan los recursos durante la compilación, verifican su cabecera y tamaño y registran origen y SHA-256. Los recursos preparados se reutilizan localmente y en CI.

El mapa de Chile ocupa aproximadamente 650 MB. La aplicación instala los recursos en su almacenamiento privado durante la primera apertura y después los consulta sin red. Los mapas de otros países se pueden importar mediante el selector de documentos Android.

La base mundial no contiene todas las calles. La ubicación actual utiliza los permisos de ubicación de Android y se representa separada de los movimientos financieros.

Datos © OpenStreetMap contributors, licencia ODbL 1.0. Atribución: https://www.openstreetmap.org/copyright . osmdroid y su adaptador Mapsforge conservan sus licencias correspondientes. El atlas histórico de Santiago Centro y sus archivos de procedencia permanecen como recursos cartográficos de la versión inicial.
