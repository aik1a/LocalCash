# UI-06 — Extensión visual a toda la aplicación

## Objetivo
Aplicar la dirección oscura/lavanda de UI-05 a todas las superficies propias de LocalCash. Nueva aclaración del usuario (2026-11-03) amplía explícitamente el alcance a todas las pantallas, sustituyendo las exclusiones visuales anteriores de compras/contactos/reparto/mapa.

## Requisitos relacionados
AGENTS.md, PROJECT_ROUTER.md, PROJECT_STATE.md y encargo visual ampliado. Solo presentación nativa Java/XML.

## Estado inicial
UI-05 está presente y se confirma en la jerarquía real de MainActivity. Continúan sin rediseño siete pantallas, dos formularios y tres filas. Hay cambios externos recientes y Gradle 9.8.2 elegido por el usuario: conservarlos. Biometría, SQL, negocio y navegación quedan intactos.

## Alcance
Bloqueo/acceso propio; contactos y consulta de deudas; historial, alta/edición/detalle de compras; pagos; reparto/deudas; mapa y su fallback de error; formularios de divisa/contacto y diálogos propios.

## Fuera de alcance
Diálogo biométrico del sistema, permisos del sistema, lógica, SQLite, cálculos, operaciones, datos de ejemplo, dependencias, tema global y animaciones.

## Diseño propuesto
Reutilizar recursos cuentas_* existentes. Aplicar el tema de vistas local a cada raíz y contextos locales a diálogos/controles creados en Java. Filas XML para listas nativas; formularios con márgenes, campos oscuros y botones lavanda. Mantener IDs, tipos, textos funcionales, listeners y operaciones.

## Plan de implementación
1. Estilos reutilizables y extensión de contextos locales. Compilar.
2. Acceso, contactos, historial y sus filas/diálogos. Compilar.
3. Crear/editar/detalle de compra, pago, reparto y deudas. Compilar.
4. Mapa normal y error. Compilar.
5. Pruebas pertinentes, revisión estática de contratos y verificación de navegación sin modificar datos del usuario. Documentar.

## Validación
assembleDebug tras cada bloque; testDebugUnitTest y assembleDebugAndroidTest. Instrumentación existente feedback/crud solo con fixtures sin borrar localcash.db; nunca ejecutar el modo de cuentas destructivo sobre esta instalación. Verificación por jerarquía en emulador activo; FLAG_SECURE se mantiene y puede impedir comparación por capturas.

## Progreso
- [x] Inspección de layouts y sus clases/listeners/filas/diálogos.
- [x] Estilos/contextos y build.
- [x] Acceso/contactos/historial y build.
- [x] Compras/reparto y build.
- [x] Mapa/error y build.
- [x] 29 pruebas unitarias, 16 comprobaciones Android de feedback y 38 de CRUD aislado.
- [x] Revisión de contratos de 12 layouts, revisión de diff y handoff.
- [x] APK desplegado en emulator-5554; abre desafío biométrico.
- [x] Tras desbloqueo, navegación real de contactos/alta/cancelación, compras/registro/calendario/cancelación, detalle/reparto y cuenta/mapa/retorno verificada por jerarquía.
- [ ] Comparación visual humana, teclado, listas largas y texto grande: pendientes; capturas protegidas por FLAG_SECURE.

## Decisiones y descubrimientos
La app real ya muestra UI-05; el problema informado es alcance visual incompleto respecto de la nueva aclaración. No hay un fallo de carga del layout de Cuentas. La ampliación se aplica sin cambiar el tema global ni el diálogo biométrico nativo. Se conserva íntegra la funcionalidad actual.

## Resultado final
Implementación visual completa de las superficies propias, con builds y pruebas citados arriba. Lint conserva un error heredado en AccesoActivity.java:78, fuera del alcance visual; no se oculta ni se cambia navegación. Se corrigió tintado de dos iconos con vectores nativos. La prueba CRUD se autorizó tras demostrar aislamiento y el hash de la base real fue idéntico antes/después. Se verificaron las rutas de consulta y cancelación descritas arriba. No se ha verificado equivalencia visual por capturas protegidas ni recorrido humano completo, teclado o listas largas. Handoff y evidencia: docs/handoffs/UI-06-todas-las-pantallas.md. Sin cierre de sprint ni aprobación independiente.
