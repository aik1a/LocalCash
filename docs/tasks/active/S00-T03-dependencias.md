# Task Brief — S00-T03: dependencias aprobadas

**Sprint:** S00 — Foundation
**Rol asignado:** Codificador (Codex)
**Estado:** READY
**Repositorio:** `aik1a/LocalCash`
**Rama:** `task/S00-T03-dependencias`, desde el **main vigente después del cierre S00-T02**.

## Objetivo observable

Añadir exclusivamente la dependencia de mapas aprobada **osmdroid 6.1.18** al proyecto Android Java/Gradle Groovy y obtener un build verificable de la configuración resultante. No implementar aún el mapa.

## Requisitos relacionados

- `LC-P04` y `LC-TECH-03` (mapas/osmdroid), dependencias preparatorias de S00 sin afirmar funcionalidad de S04.
- `LC-TECH-05`: documentación de dependencias.
- `BUILD-01`: continuidad de compilación.

La autenticación biométrica (`BiometricPrompt`) es obligatoria en S02. No se añade ahora una biblioteca de biometría sin código que la use; su integración se hará en el sprint correspondiente, con versión justificada.

## Context Pack mínimo

1. `PROJECT_ROUTER.md`, `AGENTS.md`, `PROJECT_STATE.md`.
2. `docs/agent/roles/CODER.md`.
3. Este Task Brief, `docs/sprints/S00-foundation.md`.
4. `ARCHITECTURE.md`, `docs/requirements/PROJECT_SPEC.md`, `docs/delivery/BUILD.md`.
5. Archivos reales `app/build.gradle`, `settings.gradle`, `gradle.properties` y fuente Android vigente.

No cargar modelos de base de datos o tareas futuras.

## Alcance

1. Inspeccionar `main` y confirmar SHA antes de crear la rama.
2. En `app/build.gradle`, declarar **solo** `implementation 'org.osmdroid:osmdroid-android:6.1.18'` (si todavía no figura). Reutilizar los repositorios actuales; no agregar repositorios redundantes.
3. Mantener intactos Java, XML, `MainActivity`, manifiesto, package, namespace, permisos, minSdk y versiones AGP/Gradle/JDK.
4. Actualizar `docs/delivery/BUILD.md` con dependencia declarada y resultados **realmente comprobados**; indicar que el uso de mapas y permisos sigue pendiente.
5. Ejecutar `:app:assembleDebug` para el **SHA exacto que contenga el cambio de dependencia**, con salida, código de retorno y evidencia. Si no hay ejecución local, usar Work/GitHub Actions como vía de validación. No repetir intentos en un sandbox que ya demostró bloquear procesos.
6. Si un fallo demuestra incompatibilidad de la dependencia aprobada, reportar el error y escalar antes de modificar versiones principales.

## Fuera de alcance

- Crear MapView, mostrar tiles, solicitar ubicación o agregar permisos (S00-T04 y S04).
- Biometría funcional, vistas, navegación, persistencia, entidades o nuevas clases.
- Cambiar osmdroid a otra versión, introducir bibliotecas adicionales, Room/Firebase/Compose/DI.
- Actualizar AGP/Gradle, Kotlin, SDK, JDK o arquitectura sin autorización.
- Retirar o reinterpretar la evidencia histórica de S00-T02.
- Mergear a main, cerrar T03 o iniciar T04.

## Archivos previstos

- `app/build.gradle`.
- `docs/delivery/BUILD.md`.
- `docs/handoffs/S00-T03-coder.md` y evidencia bajo `docs/handoffs/evidence/S00-T03/`.
- Ajustes mínimos de workflow de validación **solo si son necesarios** para compilar el SHA nuevo; no confiar en `s00-t02-build.yml` sin verificar, pues fija un SHA antiguo.

## Criterios de aceptación

- [ ] La única dependencia nueva de aplicación es osmdroid 6.1.18, descargada de repositorios ya definidos.
- [ ] `assembleDebug` termina con código 0 en un entorno Android funcional, compilando el SHA que incorpora la dependencia.
- [ ] Java/XML, permisos, código de UI y versiones del proyecto permanecen intactos.
- [ ] No se introducen clases/paquetes vacíos ni funciones de mapas/biometría.
- [ ] Documentación UTF-8, dependencias y limitaciones verificables.
- [ ] Handoff con rama/SHA, diff, comando, log y resultado; solicitud de auditoría independiente.

## Validación requerida

Build `./gradlew :app:assembleDebug` (o `gradlew.bat` Windows). Anexar log y exit code; en Actions verificar `git rev-parse HEAD` del checkout exacto y artefacto cuando esté disponible.

Tests: ejecutar existentes si corresponde; no inventar casos o `NO-SOURCE`. Manual: no hay pantalla nueva que probar; H-02 del arranque inicial permanece en S00-T06.

En caso de sandbox bloqueado, informar `PARTIAL/BLOCKED` y trasladar el build a un entorno ejecutable, sin declarar PASS por revisión estática.

## Entrega

Commits solo en rama T03, estado DONE/PARTIAL/BLOCKED, SHA, lista de cambios, resultados y evidencia, handoff, y solicitud de auditoría. El Auditor autoriza técnicamente; Orquestador realiza cierre.
