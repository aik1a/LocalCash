# UI-07 — Cuentas con igual jerarquía y acentos

Fecha: 2026-11-03. Ajuste visual final solicitado por el usuario. Continúa UI-06; no cambia reglas funcionales ni cierra sprint.

## Resultado

Todas las cuentas usan la misma tarjeta lavanda, tipografía, iconos y altura mínima. Se eliminó del SimpleAdapter de MainActivity la selección de layout por posición. row_cuenta.xml incluye el mismo contenido de tarjeta para todas las filas; el nombre histórico row_cuenta_destacada.xml solo identifica el recurso compartido, no una cuenta seleccionada.

Se reforzó la jerarquía de acentos:
- Botones principales lavanda; acciones de navegación/Añadir ítem/consultar reparto con acento violeta.
- Botones secundarios con bordes más visibles y texto lavanda.
- Ingreso verde y egreso rosado, con selección visible en sus RadioButton nativos.
- Selección de reparto con fondo de acento.
- Eliminación con fondo/borde rosado, también en su confirmación nativa.
- Totales y secciones con acento; estados deshabilitados diferenciados en los botones principales/secundarios.
- Filas de contactos/compras/detalles con borde lila suave.

## Archivos

Java (solo presentación): MainActivity.java y AccionesUI.java.
Layouts: row_cuenta.xml, activity_main.xml, activity_nueva_compra.xml, activity_compra_detalle.xml, activity_reparto.xml y dialog_movimiento.xml.
Recursos: values/cuentas_styles.xml; drawable/cuentas_boton.xml, cuentas_secundario.xml, cuentas_fila.xml, cuentas_acento.xml, cuentas_peligro.xml, cuentas_opcion.xml, cuentas_opcion_ingreso.xml y cuentas_opcion_egreso.xml; color/cuentas_texto_primario.xml y cuentas_texto_secundario.xml.

Se conservan IDs, tipos de vista, datos, orden de cuentas, listeners, mensajes, validaciones y operaciones. Sin cambios de lógica financiera, SQL, permisos, biometría, navegación, dependencias ni tema global.

## Verificación ejecutada

- assembleDebug tras unificar las cuentas: PASS.
- assembleDebug + assembleDebugAndroidTest + testDebugUnitTest tras los acentos finales: PASS; 29 pruebas, 0 fallos, 0 omitidas.
- Instrumentación existente mode=feedback sobre vistas en memoria: FEEDBACK_PASS, 16 comprobaciones. No se ejecuta ningún modo de pruebas que modifique registros del usuario.
- git diff --check en recursos/MainActivity/AccionesUI: PASS.
- APK final desplegado correctamente en emulator-5554, también después de terminar la instrumentación.
- La ejecución anterior se interrumpió durante la escritura de algunos ajustes. Se leyeron los archivos actuales, se completaron únicamente los cambios faltantes y se repitieron build y pruebas antes de entregar.

## Límites

La equivalencia visual por capturas permanece limitada por FLAG_SECURE; no se ha desactivado. Los recorridos de consulta/cancelación documentados en UI-06 siguen siendo evidencia de esa entrega, no se presentan como repetidos sobre UI-07. Lint no se repitió en este ajuste; UI-06 conserva el error heredado GestureBackNavigation de AccesoActivity. Sin commit/push/merge ni aprobación de auditoría independiente.
