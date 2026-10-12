# S00 — Foundation

## Objetivo

Obtener un proyecto Android Java compilable con estructura mínima, dependencias aprobadas y navegación base, listo para construir persistencia.

## Tareas

- S00-T01 — CLOSED (2026-10-12): proyecto Android Java con Gradle Groovy integrado mediante PR #1.
- S00-T02 — READY: definir paquetes coherentes con `ARCHITECTURE.md` sin sobrefragmentar; Task Brief `docs/tasks/active/S00-T02-estructura-paquetes.md`.
- S00-T03: agregar dependencias necesarias, incluyendo osmdroid 6.1.18 y biometría cuando corresponda.
- S00-T04: configurar permisos requeridos por el proyecto y documentar por qué existen.
- S00-T05: crear navegación/pantalla base suficiente para integrar módulos posteriores.
- S00-T06 — PENDIENTE: asegurar build limpio y registrar comandos; recoger las validaciones Android pendientes de S00-T01.
- S00-T07: actualizar documentación y trazabilidad.

## Criterios de aceptación

- Proyecto compila desde checkout limpio con instrucciones documentadas.
- No existe Firebase/Room/Compose/backend.
- Stack coincide con documentación.
- Estructura no contiene capas/clases vacías creadas solo por arquitectura.

## Seguimiento tras S00-T01

El cierre de S00-T01 se apoya en PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE, `docs/audits/S00/S00-T01-reaudit.md`. Merge `3cb3d5a164bc99e5343f283efa44c4a9211426b3`. El cierre administrativo de la tarea no cierra S00 ni autoriza avanzar a S01.

### Validaciones conservadas para S00-T06

Por instrucción del usuario del 2026-10-12, S00-T06 debe resolver:

- OBS-02: instalar el APK en un dispositivo/emulador funcional, abrir MainActivity, comprobar «LocalCash» y ausencia de errores que impidan iniciar.
- OBS-03: sincronización Gradle en Android Studio y build desde checkout limpio con caché sin preparar; registrar JDK/SDK, comando y salida, diferenciando esta prueba del build histórico con cachés.
- Mantener `NO EJECUTADO` cuando exista un impedimento real; documentar el error exacto y no declarar PASS.
- Conservar la advertencia `android.builtInKotlin=false` (OBS-04); no actualizar versiones para ocultarla.
- Consultar `docs/handoffs/evidence/S00-T01/` y `docs/delivery/BUILD.md`; agregar evidencia nueva sin sustituir los registros históricos.

S00-T06 no se asigna todavía: el siguiente trabajo autorizado es S00-T02.
