# S00 — Entrega acelerada de LocalCash (2026-10-12)

## Objetivo operativo

Disponer antes del **19 de octubre de 2026** de una aplicación Android académica **demostrable**, con módulos del PDF docente. La ruta de trabajo prioriza funciones utilizables; se sustituye la descomposición de tareas exclusivamente preparatorias de S00 por incrementos funcionales.

**No se cambian los requisitos del docente:** Java nativo, SQLite offline, autenticación biométrica, mapas interactivos y módulos de cuentas/movimientos, contactos, compras y gastos compartidos.

## Estado y prioridades

| Tarea | Entregable visible y verificable | Estado | Objetivo temporal |
|---|---|---|---|
| S00-T01 | Proyecto Android Java/XML/Gradle | CLOSED | Completado |
| S00-T02 | Paquete Java de presentación; build real en CI | CLOSED | Completado |
| S00-T03 | Cuentas, movimientos, saldo y SQLite comprobados en emulador | **CLOSED** | 12 oct |
| S00-T04 | Contactos, compras, ítems, historial, migración SQLite v2 y emulador PASS | **CLOSED** | 13 oct |
| **S00-T05** | **Reparto equitativo/personalizado de compras entre contactos y deudas** | **READY** | 14–15 oct |
| S00-T06 | Acceso biométrico, ubicación opcional y mapas interactivos; validar uso offline | PENDIENTE | 17 oct |
| S00-T07 | Build de integración, comprobaciones manuales, trazabilidad, documentación y pauta de exposición | PENDIENTE | 18 oct |

Las fechas son **objetivos de organización**, no resultados ni garantías de implementación. Reservar tiempo antes del 19 para correcciones. No empezar simultáneamente tareas que dependan del mismo modelo de datos.

## Regla de ejecución simplificada

- Cada tarea entrega un **flujo que funcione**, no solo un archivo de configuración.
- Incorporar osmdroid, biometría y permisos **en las tareas que los usan**, nunca como tareas aisladas.
- Mantener Java, XML, Gradle Groovy, `SQLiteOpenHelper` y la separación mínima UI / dominio / repositories.
- No crear clases/capas vacías, infraestructura remota, Firebase, Room, Compose, servicios inventados ni funciones comerciales.
- El Codificador puede realizar varios commits dentro de **una sola tarea**, pero la auditoría se solicita al terminar el incremento. No auditar cada ajuste documental.
- Compilar cada incremento real mediante CI o Android Studio y comprobar **su SHA exacto**. No repetir el sandbox Windows ya bloqueado.
- Documentar solo decisiones de datos requeridas por la funcionalidad actual. Sin inventar requisitos; elevar al Orquestador las decisiones funcionales abiertas que afecten compra/reparto.
- El Auditor sigue emitiendo gates de tarea. El cierre del sprint y avance administrativo requieren gate integral correspondiente.

## Pendientes heredados

El build S00-T03, las pruebas de dominio, el flujo UI/SQLite y el relanzamiento en emulador **están validados**: [Actions run 38283139884](https://github.com/aik1a/LocalCash/actions/runs/38283139884). Instalación física/sync Studio siguen pendientes para T07. S00-T04 conserva datos SQLite v1 mediante migración aditiva comprobada [Actions run 38289787487](https://github.com/aik1a/LocalCash/actions/runs/38289787487). T05 deberá migrar v2→v3. Los APK T03 y T04 tienen firmas distintas, por lo que la actualización de instalación de usuario requiere recuperar la misma firma antes de T07; no desinstalar sin respaldo.

## Relación con documentos históricos

`docs/sprints/S01-...` hasta `S07-...` siguen siendo referencias de requisitos/arquitectura y pruebas. Esta planificación **reordena y agrupa la ejecución** por plazo; no autoriza cerrar anticipadamente esos sprints ni afirmar funcionalidades terminadas. Evitar duplicar tareas ya demostradas en el MVP.
