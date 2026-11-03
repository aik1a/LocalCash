# MAP-05 — Mapa mundial offline

## Objetivo y requisitos
Resolver LC-MAP-01 con navegación mundial y centrado en posición actual, por solicitud explícita del usuario. Mantener el funcionamiento offline elegido.

## Estado inicial
Main c28a27b87aeeb092a936b8ba6b05b83404ac1c13: atlas raster limitado al centro de Santiago, desplazamiento bloqueado, zoom14–16, botón visible Santiago Centro. Esto era una decisión interna, no una exigencia del docente.

## Alcance y diseño
- Adaptador oficial osmdroid-mapsforge 6.1.18, misma versión que osmdroid actual.
- Base mundial Mapsforge de baja resolución y Chile detallado incluidos en APK. Ningún permiso INTERNET ni API remota en ejecución.
- Preparación de recursos durante compilación mediante script Python: URLs y SHA256 guardados en provenance.json. Assets generados grandes, no versionados en Git.
- Copia privada de mapas en hilo de archivos; validación SHA256 inicial y cabecera Mapsforge.
- Selector Android para importar localmente otro .map; validación y sustitución atómica.
- LocationManager foreground, fix fresco con timeout, permisos y cancelación al pausar.
- Root privado estable para no eludir visibilidad de ActividadProtegida durante carga asíncrona.
- Sin cambios de base de datos, transacciones financieras o autenticación.

## Fuera de alcance
Descarga mundial detallada dentro de la app, servidor/backend, geocodificación y seguimiento continuo. La base mundial no sustituye mapas regionales de calles. No se afirma que el archivo Chile incluya toda frontera política o cada calle existente.

## Pasos y progreso
- [x] Inspeccionar código, requisito y error de alcance previo.
- [x] Confirmar funcionamiento offline y centrado GPS con usuario.
- [x] Implementar proveedor vectorial y preparación de datos.
- [x] Implementar navegación mundial, Mi ubicación e importación.
- [x] Sustituir pruebas que aceptaban el rechazo territorial por pruebas mundiales.
- [ ] Compilar app/instrumentación y ejecutar unitarias.
- [ ] Ejecutar pruebas offline/GPS y regresión en emulador.
- [ ] Registrar resultados reales y entregar APK/PR.

## Validación
CI dedicado: prepare_vector_maps.py, tests/domain-checks.sh, assembleDebug, assembleDebugAndroidTest, testDebugUnitTest; API36 instalación limpia y red deshabilitada, GPS La Florida/París, tiles mundiales Tokio/Europa, importación inválida/válida, recreación y SQLite intacto. Prueba física pendiente.

## Decisiones y límites
El usuario elige base mundial offline con paquetes por zonas frente a calles mundiales por internet. Incluir Chile detallado produce un APK grande (mapa fuente aproximadamente 651 MB). Se informa esta consecuencia. Los datos OSM se descargan desde servidor de paquetes vectoriales permitido, nunca desde tile.openstreetmap.org. La adquisición inicial durante build sí necesita internet; ejecución Android no.

## Resultado
En implementación y validación; no se declara aceptación hasta obtener resultados del build y emulador.
