# Diseño SQLite

**Estado:** PRELIMINAR — no congelar `CREATE TABLE` hasta resolver las decisiones pendientes indicadas aquí.

## Principios

- SQLite es la única persistencia del alcance actual.
- Acceso mediante `SQLiteOpenHelper` y repositories.
- Activar/usar integridad referencial según corresponda.
- SQL fuera de Activities/Fragments.
- Evitar almacenar datos derivados que puedan quedar inconsistentes.
- Operaciones multi-tabla que deban ser atómicas deben usar transacciones.

## Tablas candidatas

1. `cuentas`
2. `movimientos`
3. `contactos`
4. `compras`
5. `detalle_compra`
6. `participantes`
7. `asignaciones`
8. `deudas`

## Mapeo dominio -> persistencia

| Dominio | Persistencia |
|---|---|
| Cuenta | `cuentas` |
| Movimiento | `movimientos` |
| Ubicacion | `movimientos.latitud` + `movimientos.longitud` nullable |
| Contacto | `contactos` |
| Compra | `compras` |
| DetalleCompra | `detalle_compra` |
| Participante | `participantes` |
| Asignacion | `asignaciones` |
| Deuda | `deudas` |

## Relaciones candidatas

```text
cuentas 1 ---- N movimientos
compras 1 ---- N detalle_compra
compras 1 ---- N participantes ---- N:1 contactos
detalle_compra 1 ---- N asignaciones ---- N:1 contactos
compras 1 ---- N deudas ---- N:1 contactos
```

## Decisiones técnicas recomendadas a cerrar en S01

- PK: `INTEGER PRIMARY KEY`.
- FK explícitas entre relaciones.
- Dinero: preferir entero en unidad mínima (centavos/pesos según decisión del proyecto) antes que `REAL`, para evitar errores de coma flotante.
- Fecha/hora: escoger una representación única (ISO-8601 TEXT o epoch INTEGER) y usarla en todo el proyecto.
- Latitud/longitud: nullable y ambas presentes o ambas ausentes.
- Índices solo donde exista una consulta real que los justifique.

## Decisiones pendientes antes del freeze de esquema

- campos definitivos de cada tabla;
- valores de tipo de cuenta;
- representación definitiva de dinero y fecha;
- políticas `ON DELETE`/`ON UPDATE`;
- si una compra realizada genera movimiento automáticamente;
- pagos parciales de deuda sí/no;
- algoritmo/redondeo de distribución;
- si `participantes` y `deudas` pueden simplificarse sin perder trazabilidad.

## Regla de freeze

Cuando S01 cierre estas decisiones:
1. actualizar este archivo con columnas/tipos/constraints definitivos;
2. actualizar `database-er.md`;
3. implementar `LocalCashDbHelper`;
4. añadir pruebas CRUD;
5. actualizar trazabilidad.