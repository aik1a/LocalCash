# UI-06 — Todas las superficies de LocalCash

Fecha: 2026-11-03. Rol: Codificador. Continuación visual de UI-05 por aclaración explícita del usuario: todas las pantallas deben tener el mismo rediseño. Implementación local, sin commit/push/merge ni cierre de sprint.

## Alcance ampliado

Se sustituyen las exclusiones visuales anteriores de compras, contactos, reparto y mapa por esta autorización nueva. Se mantienen las demás restricciones: Java/XML nativo, mismas acciones/campos/mensajes/datos, sin cambios de negocio, SQLite, servicios, repositorios, navegación, permisos o biometría. Sin nuevas dependencias, tema global, funciones ni animaciones. El diálogo biométrico de Android conserva su interfaz del sistema; el bloqueo propio de LocalCash sí se rediseña.

## Implementación y archivos

Se reutiliza la paleta oscura/lavanda, bordes y degradados de UI-05.

Layouts rediseñados en app/src/main/res/layout:
- activity_bloqueo.xml: pantalla propia de acceso/bloqueo, con desplazamiento para ventanas pequeñas.
- activity_contactos.xml y dialog_contacto.xml: lista, vacío y alta de contacto.
- activity_compras.xml: historial y vacío, incluidos los filtros existentes por cuenta.
- activity_nueva_compra.xml: registrar/editar planificación, campos, selectores, fecha, productos, total, ayudas y errores.
- activity_compra_detalle.xml: detalle, total, productos, planificación, pago y reparto.
- activity_reparto.xml y row_participante.xml: modos de reparto, cuotas, participantes, errores y deudas.
- activity_mapa.xml: cabecera, contenedor del mapa offline, controles, marcador y atribución.
- dialog_divisa.xml: configuración inicial y moneda histórica, incluidos errores.
- row_detalle.xml y row_item_borrador.xml: productos y detalles/deudas.
- row_cuenta_destacada.xml: iconos de tinta nativos; mismo aspecto sin android:tint.

Layouts nuevos:
- row_contacto.xml, row_compra.xml y row_accion.xml: filas y acciones nativas.
- activity_mapa_error.xml: traslada a XML la presentación del error ya existente, conservando mensaje y acción de retorno.

Recursos:
- values/cuentas_styles.xml: ayudas, secciones, resúmenes, errores y selectores; estilos locales.
- drawable/cuentas_ic_contactos.xml y cuentas_ic_compras.xml.
- drawable/cuentas_ic_wallet_tinta.xml y cuentas_ic_chevron_tinta.xml: variantes nativas para la tarjeta clara.

Ajustes Java exclusivamente visuales:
- CuentasVisual.java: extiende el contexto de diálogo y márgenes de nombre a todas las Activities protegidas.
- ComprasActivity.java: SimpleAdapter usa row_compra.
- ContactosActivity.java: ArrayAdapter usa row_contacto; filas de acción XML y contextos oscuros en consulta de deudas/formulario/diálogos.
- DivisasUI.java: contexto local del diálogo y de su formulario.
- NuevaCompraActivity.java: adaptadores de Spinner usan el contexto del control; DatePickerDialog usa el tema local.
- CompraDetalleActivity.java: estilo de diálogos de selección/confirmación de pago.
- RepartoActivity.java: estilo de confirmaciones y acciones de deuda desde row_accion.
- MapaActivity.java: fallback usa activity_mapa_error en vez de construir vistas manualmente.

No se modifica AccesoActivity ni ActividadProtegida. El tema de aplicación/manifest y values/styles.xml quedan como estaban al iniciar esta ampliación. Se conserva la actualización a Gradle 9.8.2 realizada por el usuario. Los cambios externos recientes de dominio/data/application no pertenecen a UI-06 y no se revirtieron.

## Evidencia ejecutada

- Cuatro bloques (estilos, acceso/contactos/historial, compras/reparto y mapa/error): assembleDebug PASS tras cada uno.
- APK de aplicación y APK de instrumentación: PASS.
- testDebugUnitTest: 29 passed, 0 skipped, 0 failed.
- Instrumentación existente mode=feedback: FEEDBACK_PASS, 16 comprobaciones Android de validación y mensajes.
- Instrumentación existente mode=crud: CRUD02_PASS, 38 comprobaciones en bases de fixture separadas.
- Revisión estática de 12 layouts modificados: IDs, tipos, textos de controles y restricciones inputType/digits/maxLines/saveEnabled/checkedButton/selectAllOnFocus conservados.
- Búsqueda de constructores AlertDialog.Builder(this/activity), Button(this) y TextView(this) sin estilo en presentation: sin coincidencias.
- git diff --check de presentation/recursos: sin errores de espacios; advertencias de conversión LF→CRLF.
- lintDebug: un error heredado GestureBackNavigation en AccesoActivity.java:78 y 164 warnings. No se declara lint aprobado. Los dos errores UseAppTint que lint detectó en la tarjeta clara de UI-05 se resolvieron con vectores nativos de color tinta.

## Aislamiento de la prueba CRUD

El primer intento fue rechazado por revisión automática por posible modificación de datos. Antes de reintentarlo se inspeccionaron CrudChecks, FlowChecks y LocalCashDbHelper:
- mode=crud selecciona CrudChecks.ejecutar, no el modo de cuentas que borra localcash.db ni crud-upgrade.
- FixtureContext devuelve this en getApplicationContext, y sus dos openOrCreateDatabase redirigen al nombre de fixture.
- Las operaciones usan crud02-<nano>.db y crud02-fresh-<nano>.db.
Con esa evidencia se autorizó el mismo comando, sin modificar el runner ni el aislamiento. Hash de databases/localcash.db antes y después, idéntico:
225e4a9de2e716fa79bd5599ad57ddf1f482da0860b26dfdf0cde0a514f5b17a.
No se ha borrado ni sustituido la base del usuario.

## Dispositivo y límites

Antes de modificar UI-06 se confirmó mediante jerarquía real de MainActivity que UI-05 ya estaba ejecutándose. El APK completo se desplegó correctamente con studio_deploy en emulator-5554. Se verificó que abre el desafío biométrico nativo; FLAG_SECURE sigue activo y las capturas resultan negras. No se desactivó ni se modificó esa protección.

Tras el desbloqueo habitual se verificaron por jerarquía real estas rutas en el APK final:
- Cuentas → Contactos → Crear contacto → Cancelar → Contactos → Cuentas.
- Cuentas → Compras → Registrar compra → calendario de fecha → Cancelar calendario → Cancelar formulario → Compras.
- Compras → detalle de compra existente → reparto/deudas existente → detalle → Compras.
- Cuentas → detalle de cuenta existente → mapa offline con MapLocalView presente → detalle de cuenta.

No se ejecutaron pagos, borrados, cambios de nombre ni se guardaron contactos/compras/repartos sobre datos reales para comprobar estilo. La comparación visual de colores y fidelidad por capturas sigue impedida por FLAG_SECURE. No se declaran probados teclado, listas largas, texto grande, todos los errores de formulario ni la reproducción del fallback del mapa.

## Pendientes

Revisión humana de fidelidad visual, teclado, ventanas pequeñas/texto grande, listas largas y cancelación/errores de cada formulario tras desbloqueo. Confirmar las transiciones y diálogos con el usuario sin operaciones financieras de prueba sobre sus registros. El error de lint de acceso requiere una tarea aparte si se autoriza modificar navegación.

## Continuidad

El historial UI-05 documenta correctamente el alcance anterior; UI-06 registra su ampliación. No se declara cerrado sprint ni aprobada auditoría independiente. PROJECT_STATE.md y los documentos funcionales no se alteran por este cambio de presentación.
