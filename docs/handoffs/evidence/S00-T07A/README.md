# Evidencia S00-T07A

Rama `task/S00-T07A-integracion`; base `cc77afc2c676770329eb89e367ea44f8c11c9e1a`; SHA de ejecución `32dd6e0ff09143bfc55276d5084c18472769b4af`.

## Fuentes y autenticidad

- `source-blobs.json`: inventario de 89 blobs locales con hashes Git y tamaños verificados contra el árbol remoto. No acredita un checkout completo ni build local.
- `local-*.log`, `local-*-exit-code.txt`, `local-environment.json`: ejecuciones locales del Codificador; dominio y Gradle quedaron bloqueados. El verificador/regeneración del atlas y parsing estático sí se ejecutaron.
- `ci-run.json`, `ci-jobs.json`, `ci-artifacts.json`: metadatos recuperados directamente de GitHub para la ejecución nueva; `inherited-ci.json` identifica separadamente la CI T06B.
- `ci-API24.log` / `ci-API36.log`: logs originales de jobs descargados mediante el conector GitHub; pueden contener errores transitorios de arranque de adb, no equivalentes al resultado final.
- `artifact-verification.json`: comprobación de los ZIP descargados contra los digests de GitHub, SHA compilado, exit code y hashes reales de APK/evidencia interna.
- `SHA256SUMS.txt`: hashes de los archivos textuales versionados de esta carpeta, excluyéndose a sí mismo.

Los ZIP de Actions conservan APK principal, APK de instrumentación, logs, volcados XML y PNG cartográficos. No se desactiva `FLAG_SECURE` para conseguir capturas. Los datos son exclusivamente fixtures de AVD.

| API | Artefacto Actions | Digest SHA256 ZIP |
|---|---|---|
| 24 | [12106073736](https://github.com/aik1a/LocalCash/actions/runs/39046939759/artifacts/12106073736) | `101943ef20858a287cca8dc019902714d50fc7b1d7f38d60607cd35a34f1653b` |
| 36 | [12105649198](https://github.com/aik1a/LocalCash/actions/runs/39046939759/artifacts/12105649198) | `9ab61141e5c36e2770b9a870136c1b4941dd99c65cd6164ed29c7daa14d918ab` |

`API24/` y `API36/` conservan también copias seleccionadas de resultados internos. Los ZIP originales se entregan juntos en `LocalCash-S00-T07A-APK-evidencia.zip`; Actions declara expiración de sus copias el 2027-01-15. SHA256 de cada APK en `API*/apk-sha256.txt`.

## Revisión de un ZIP descargado

Comparar primero `sha256sum <artifact.zip>` con el digest indicado por GitHub y este informe. Tras extraer en un directorio nuevo:

```sh
cat evidence/validated-sha.txt
cat evidence/exit-code.txt
sha256sum -c evidence/apk-sha256.txt
sha256sum -c evidence/SHA256SUMS.txt
```

Los APK aparecen bajo `app/build/outputs/apk/` en el ZIP; las rutas internas de `apk-sha256.txt` son relativas a la raíz extraída. No confundir el SHA256 del ZIP con el SHA256 del APK. Las firmas debug de ambos runners pueden ser distintas; seleccionar por API/job/hash exacto, sin intentar un upgrade histórico como parte de T07A.

Consultar resultados y NOT TESTED en `docs/testing/S00-T07A.md`. Esta evidencia no autoriza merge ni cierre de tarea/sprint.
