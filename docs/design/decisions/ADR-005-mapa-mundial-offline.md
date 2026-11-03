# ADR-005 — Navegación mundial offline

Estado: implementada en rama MAP-05, validación pendiente. Sustituye la restricción territorial de ADR-002 para esta corrección.

El usuario detecta que el límite a Santiago Centro impide el uso real y solicita mapa mundial, centrar ubicación actual y mantener 100% offline. Esa restricción no procede del encargo docente.

Se conserva osmdroid 6.1.18 y se incorpora su módulo oficial `org.osmdroid:osmdroid-mapsforge:6.1.18` (Mapsforge transitivo 0.20.0, licencia LGPL; adaptador Apache 2.0). Vector mundial básico + Chile detallado incluidos en APK; otros mapas de países importables mediante Storage Access Framework. Sin backend, sin permiso INTERNET, sin permisos generales de almacenamiento y sin cambios SQLite.

Un archivo mundial básico no tiene todas las calles. La cobertura navegable es mundial; el detalle depende de los paquetes incluidos o importados. No se limita el desplazamiento a un rectángulo ni se rechazan marcadores exteriores. GPS usa permisos Android y datos frescos, no coordenadas prefijadas o caché presentada como actual.

Los archivos grandes se obtienen durante la compilación; se guardan SHA256/URL/licencia en los assets y se cachean en CI. Primer build requiere Python3/internet; builds posteriores reutilizan recursos. El APK contiene los datos para primera apertura sin red. Esto incrementa significativamente su tamaño: archivo Chile aproximadamente 651 MB. Los proveedores no reciben coordenadas ni datos financieros.

Fuentes: https://github.com/osmdroid/osmdroid/tree/osmdroid-parent-6.1.18/osmdroid-mapsforge ; https://download.mapsforge.org/ ; https://www.openstreetmap.org/copyright .
