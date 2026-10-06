# ADR-001 — SQLite como única persistencia

**Estado:** ACCEPTED

## Contexto

LocalCash debe mantener su funcionalidad financiera principal de forma local/offline y el encargo exige SQLite.

## Decisión

SQLite será la única fuente persistente de datos de la versión evaluada. La base se administrará mediante `SQLiteOpenHelper` y el acceso se encapsulará en repositories.

## Alternativas descartadas

### Firebase / backend remoto

No se utiliza porque introduce infraestructura y sincronización no necesarias para el alcance y debilita el principio de operación local.

### Room

No se utiliza en esta versión porque el proyecto busca trabajar directamente con SQLite/CRUD y `SQLiteOpenHelper` mantiene la solución alineada con el nivel académico y el stack acordado.

## Consecuencias

- No existe capa de sincronización remota.
- Las funciones principales no dependen de conectividad de red.
- Los repositories deben poder reconstruir el dominio desde SQLite.
- Migraciones futuras se gestionarán por versión del `SQLiteOpenHelper`.