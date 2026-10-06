# LocalCash — Project State

**Estado del documento:** operativo  
**Responsable principal:** Agente Orquestador  
**Regla:** actualizar cuando cambie el estado real del proyecto. No usar este archivo para reemplazar requisitos, ADRs ni historial de auditorías.

## Estado actual

- **Repositorio:** `aik1a/LocalCash`
- **Rama principal:** `main`
- **Fase:** preparación / foundation
- **Sprint activo:** `S00 — Foundation`
- **Estado del sprint:** READY / no iniciado
- **Código Android:** todavía no implementado
- **Build validado:** no disponible todavía
- **Tests automatizados:** no disponibles todavía
- **Último hito validado:** estructura documental inicial y protocolo multiagente creados

## Próxima acción autorizada

`S00-T01`: verificar o crear el proyecto Android Java con Gradle Groovy conforme a `docs/sprints/S00-foundation.md`.

Antes de implementarlo, el Codificador debe inspeccionar el repositorio real y confirmar qué archivos/proyecto Android existen.

## Decisiones vigentes

- Android nativo en Java.
- UI en XML.
- Gradle con Groovy DSL.
- Persistencia local mediante SQLite + `SQLiteOpenHelper`.
- SQLite como única fuente persistente dentro del alcance actual.
- Autenticación mediante `BiometricPrompt`.
- Mapas mediante osmdroid 6.1.18.
- Sin Firebase, backend remoto, Room, Compose, DI ni arquitectura empresarial completa salvo decisión posterior explícita.
- Arquitectura base: Presentation → Application/Services → Domain → Repositories → SQLiteOpenHelper → SQLite.
- Proyecto académico: priorizar claridad, trazabilidad y demostrabilidad sobre sobreingeniería.

## Decisiones pendientes relevantes

Resolver solo cuando el sprint correspondiente las necesite:
- esquema físico SQLite definitivo;
- tipos/constraints exactos de columnas;
- política de borrado y cascadas;
- estrategia exacta de reparto personalizado y redondeo;
- soporte o exclusión de pagos parciales;
- posible redundancia entre participantes y asignaciones;
- si completar una compra genera automáticamente un movimiento financiero;
- campos opcionales definitivos de contactos;
- estrategia de tiles/cache que garantice el comportamiento offline del mapa.

## Bloqueadores

Ninguno documentado para iniciar S00.

## Última auditoría

No existe auditoría de código todavía. La primera auditoría debe ejecutarse al completar una unidad implementable de S00.

## Reglas de actualización

El Orquestador debe actualizar este archivo cuando:
- cambia el sprint activo;
- se valida o invalida un build;
- aparece o se resuelve un bloqueador;
- cambia la próxima acción autorizada;
- una decisión pendiente pasa a aceptada mediante documentación correspondiente.

No registrar aquí detalles extensos de implementación; usar handoffs, auditorías, ADRs y documentos del sprint.