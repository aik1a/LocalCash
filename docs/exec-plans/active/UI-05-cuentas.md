# UI-05 — Presentación de cuentas

## Objetivo
Aproximar las capturas aportadas por el usuario mediante Java/XML nativo, sin cambios funcionales.

## Requisitos relacionados
Encargo visual del usuario (2026-11-03); AGENTS.md y PROJECT_ROUTER.md. No se modifican requisitos funcionales ni se cierra sprint.

## Estado inicial
MainActivity y CuentaActivity usan SimpleAdapter con simple_list_item_2; formularios XML y AlertDialog nativo. Existen cambios locales previos staged y unstaged: conservarlos íntegros. CONTEXT_PACKS.md y HANDOFF_TEMPLATE.md citados por el router no existen en esta copia.

## Alcance
Cuentas, crear cuenta, detalle, movimientos, registrar movimiento y diálogos de mantenimiento de cuentas. Recursos reutilizables exclusivos para estas superficies.

## Fuera de alcance
Lógica, persistencia, navegación, permisos, biometría, tema global, dependencias, compras/contactos/reparto/mapa.

## Diseño propuesto
Paleta oscura lila, degradados lavanda, borde translúcido, tipografía y separación jerárquica. Temas locales de vistas y diálogos; filas XML con datos existentes. Las utilidades compartidas solo reciben estilo cuando el contexto es MainActivity o CuentaActivity.

## Plan de implementación
1. Recursos visuales locales; compilar.
2. Cuentas, filas y crear cuenta; compilar.
3. Detalle y filas de movimientos; compilar.
4. Registrar movimiento y mantenimiento; compilar.
5. Pruebas existentes, revisión de diff y handoff.

## Validación
assembleDebug tras cada bloque; testDebugUnitTest y assembleDebugAndroidTest al terminar. Consultar dispositivo disponible y verificar UI si la sesión biométrica lo permite, sin retirar FLAG_SECURE ni alterar datos personales. Documentar los escenarios no ejecutados.

## Progreso
- [x] Entrada obligatoria y exploración de conexiones/listeners/diálogos.
- [x] Recursos y build.
- [x] Cuentas/crear y build.
- [x] Detalle/filas y build.
- [x] Movimiento/mantenimiento y build.
- [x] Pruebas unitarias (29/29), compilación de instrumentación, revisión estática y handoff.
- [ ] Comparación visual y recorridos en dispositivo operativo (bloqueados por entorno).
- [ ] Pruebas instrumentadas con resultado (intento feedback sin salida, cancelado).

## Decisiones y descubrimientos
No se aplica el skill image-to-code: su flujo es para prototipos web y contradice el alcance Android/XML de este encargo. No hay activos raster en el diseño; se usarán drawables XML nativos. Los textos y mensajes originales se conservan; no se incorporan datos de ejemplo.

## Resultado final
Implementación visual local lista para revisión. Los cuatro bloques y los APK finales compilan; 29 pruebas unitarias pasan. IDs/tipos conservados. Lint bloqueado por GestureBackNavigation heredado en AccesoActivity.java:79. AVD API37 arrancó, pero fallaron lanzamiento e inspección UI; la instrumentación feedback se canceló sin salida. Comparación visual, teclado y recorridos manuales NO VERIFICADOS. Handoff verificable: docs/handoffs/UI-05-cuentas.md. Este plan sigue activo solo para la validación pendiente; no se cierra sprint ni se autoriza avance.
