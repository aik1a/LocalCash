# Evidencia documental T07C

Resultado: validación documental PASS/retorno 0; no build, emulador nuevo ni autoauditoría. Base 9084b573ebe055485e3cdda11ca8db2d8df6a3bc.

- validation-result.json: resultado y límites.
- source-checks.json: configuración/código y antecedentes recuperados de GitHub, identidad de árboles y metadatos CI.
- tree-paths.json: rutas del árbol base contrastado.
- validate_documentation.py: comprobador de documentación; usa biblioteca estándar Python.
- El APK, ZIP original y PDF son entradas externas: contrastar primero sus SHA256 con el manifest de entrega.

Ejemplo reproducible desde la raíz del repositorio con estas entradas descargadas:
```sh
python docs/handoffs/evidence/S00-T07C/validate_documentation.py \
  --docs-root . \
  --sources docs/handoffs/evidence/S00-T07C/source-checks.json \
  --tree-file docs/handoffs/evidence/S00-T07C/tree-paths.json \
  --apk /ruta/app-debug.apk \
  --artifact /ruta/API36.zip \
  --pdf /ruta/LocalCash-Proyecto-Android.pdf \
  --output /tmp/localcash-t07c-validation.json
```

Solo se revisan los Markdown de documentation_files en el manifest. No se afirma que todos los documentos históricos del repositorio ni todos los HTTP externos hayan sido comprobados. CI y artefactos se contrastaron por API autenticada; el resultado conserva IDs/SHA. Los resultados de Android proceden de T07A, no de este script.
