# UI-05 — Presentación visual de cuentas

Fecha: 2026-11-03. Rol: Codificador. Implementación local lista para revisión visual; sin commit, push, merge ni cierre de sprint.

Rama real de esta copia: `master`; `git rev-parse --verify HEAD` no encuentra una revisión. El índice contiene archivos añadidos y cambios locales adicionales. Esta copia no permite atribuir el diff a un commit de main: se documenta la discrepancia y se entrega el árbol local para revisión sin inventar una base Git verificada.

## Encargo y alcance

Aproximación de las capturas del usuario en las superficies de cuentas con Android nativo Java/XML: fondo oscuro lila, tarjeta lavanda, bordes suaves, superficies translúcidas, importes jerarquizados y diálogos coordinados. Sin librerías, desenfoque avanzado ni nuevas funciones.

## Archivos de esta intervención

Layouts modificados:
- `app/src/main/res/layout/activity_main.xml`
- `app/src/main/res/layout/activity_cuenta.xml`
- `app/src/main/res/layout/dialog_cuenta.xml`
- `app/src/main/res/layout/dialog_movimiento.xml`

Filas XML añadidas:
- `row_cuenta_destacada.xml`: primera cuenta con tarjeta lavanda.
- `row_cuenta.xml`: cuentas restantes compactas.
- `row_movimiento.xml`: descripción, fecha/tipo, ubicación e importe con indicador nativo de ingreso/egreso.

Recursos añadidos en `app/src/main/res/`:
- `values/cuentas_colors.xml`, `values/cuentas_styles.xml`: paleta y estilos locales.
- `drawable/cuentas_*.xml`: degradados, campos, tarjetas, botones, selector, vidrio aproximado y vectores nativos. No se cambia `values/styles.xml` ni el tema de la aplicación.

Presentación Java:
- `MainActivity.java`: filas XML con reciclado por tipo; nombre, divisa y saldo de los datos existentes; contexto local del formulario/diálogo.
- `CuentaActivity.java`: filas XML con los mismos movimientos y orden; color/icono según tipo; contexto local de formularios/diálogos.
- `CuentasVisual.java` (nuevo): contexto de diálogo y márgenes del campo de renombrado exclusivos de cuentas.
- `AccionesUI.java`: usa ese contexto al construir diálogos y enmarca el campo de nombre solo en CuentaActivity. Para las demás Activities devuelve exactamente el contexto/campo original; no cambia callbacks, validaciones ni operaciones.

Documentación: este handoff y `docs/exec-plans/active/UI-05-cuentas.md`.

## Conservación del trabajo previo

Se inspeccionaron los archivos y el estado staged/unstaged antes de editar. Ya existían cambios locales de UX-01, CRUD-02, AV-03 y UT-04, incluso en MainActivity y CuentaActivity; no se revirtieron ni se reemplazaron sus validaciones/mantenimiento. No se ejecutó reset, checkout, clean, add ni commit.

Se revisó el diff de presentación y la invariancia de las estadísticas de diff de dominio/data/application respecto del inicio. Esta intervención no cambia SQL, entidades, cálculos, services, repositories, navegación, permisos, biometría ni dependencias. Las diferencias previas de esas áreas siguen siendo anteriores a UI-05. No se modifican pantallas de compras, contactos, reparto o mapa.

La carga financiera original permanece en los mismos métodos. Los mensajes de rechazo/éxito, listeners y callbacks se conservaron. Solo se redistribuye el texto de cada movimiento entre elementos visuales. No se incorporan nombres, importes o movimientos ficticios de las referencias.

## Evidencia ejecutada

- Bloque 1, recursos: `:app:assembleDebug` PASS.
- Bloque 2, Cuentas/Crear: `:app:assembleDebug` PASS.
- Bloque 3, detalle/filas: `:app:assembleDebug` PASS.
- Bloque 4, registrar/mantenimiento: `:app:assembleDebug` PASS.
- Tras ajustes finales: `:app:assembleDebug :app:assembleDebugAndroidTest` PASS.
- `:app:testDebugUnitTest`: 29 passed, 0 skipped, 0 failed. Repetidas tras el último ajuste Java, mismo resultado.
- Revisión estática de los cuatro layouts: todos los IDs y tipos de vista consumidos por Java se conservan.
- Inspecciones IDE de CuentasVisual, selector y estilos locales: sin errores. AccionesUI conserva dos sugerencias de estilo sin errores.
- `git diff --check` sobre layouts/recursos y Activities afectadas: sin errores de espacios; Git avisa conversión LF→CRLF.
- `:app:lintDebug` se ejecutó y FALLÓ por un error preexistente en AccesoActivity.java:79, GestureBackNavigation/onBackPressed. Ese archivo y el flujo biométrico no se modifican. El informe de esa ejecución tenía 150 warnings; posteriormente se corrigieron los tres avisos de recursos visuales sin uso. No se declara lint aprobado.

## Ejecución en dispositivo y limitaciones

Se usó la skill local `android-cli` (`C:/Users/Admin/.android-studio/skills/android-cli/SKILL.md`) para listar/iniciar el AVD Pixel_10_Pro (API 37), instalar/abrir el APK y detener el emulador al finalizar. Había una instalación previa: no se desinstaló ni se limpió su base.

Android Studio deploy y `android run` intentaron iniciar la app, pero devolvieron Error type 3: MainActivity does not exist. La inspección `studio_ui_state` también falló al obtener el XML del dispositivo. No se atribuye ese fallo a un cambio concreto de la app sin evidencia.

Se intentó la prueba instrumentada existente `am instrument -w -e mode feedback com.aik1a.localcash.test/com.aik1a.localcash.FlowChecks`; no produjo salida y se canceló tras aproximadamente 157 segundos. Resultado: SIN RESULTADO, no PASS. No se ejecutó el modo destructivo de cuentas, que borra localcash.db. No se alteró FLAG_SECURE, la sesión biométrica ni permisos.

## Pendientes para revisión

1. Abrir la app en un dispositivo/emulador operativo y comparar visualmente con las capturas.
2. Comprobar creación/cancelación, selector de divisa, errores de campos, teclado y desplazamiento de formularios.
3. Verificar navegación de ida/vuelta, listas largas, texto grande y ventanas pequeñas.
4. Comprobar opciones, renombrado/cancelación, eliminación/cancelación y rechazo de eliminación de cuenta con movimientos.
5. Ejecutar las pruebas instrumentadas pertinentes en un emulador descartable y resolver aparte el error heredado de lint si se autoriza.

No se afirma equivalencia visual verificada ni recorrido manual completado. El encargo no requiere cambios de lógica; no quedó ningún cambio funcional necesario para el diseño. La aceptación visual/revisión independiente y el cierre corresponden al usuario/Orquestador.

## Contexto documental

CONTEXT_PACKS.md y HANDOFF_TEMPLATE.md, referidos por el router/rol, no existen en esta copia (confirmado con lectura y búsqueda IDE). Se conserva este handoff con alcance, archivos, evidencia, límites y pendientes. No se modifica PROJECT_STATE.md ni se declara cerrado ningún sprint.
