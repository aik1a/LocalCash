# Diseño SQLite

**Estado:** cuentas/movimientos implementados en S00-T03, versión 1. El resto sigue PRELIMINAR.

## Esquema mínimo S00-T03 (decisiones implementadas)

Base `localcash.db`, administrada por `data.LocalCashDbHelper`.

| Tabla | Columnas y restricciones |
|---|---|
| cuentas | id INTEGER PRIMARY KEY; nombre TEXT NOT NULL, trim no vacío; saldo_inicial INTEGER NOT NULL |
| movimientos | id INTEGER PRIMARY KEY; cuenta_id INTEGER NOT NULL FK cuentas(id) ON DELETE RESTRICT; tipo TEXT NOT NULL INGRESO/EGRESO; monto INTEGER NOT NULL > 0; descripcion TEXT NOT NULL (vacío permitido); fecha INTEGER NOT NULL >= 0 |

- Dinero en enteros de centésimas: 100,25 se guarda como 10025; sin REAL ni redondeo silencioso. UI acepta coma o punto y hasta dos decimales sin separador de miles. Sin conversión ni moneda implícita.
- Saldo inicial puede ser cero o negativo; saldo actual siempre derivado por Cuenta.calcularSaldo. No se impide saldo negativo: no existe regla de fondos insuficientes en el encargo.
- Fecha epoch en milisegundos al registrar, mostrada en hora local. Orden fecha ASC, id ASC; índice (cuenta_id, fecha, id) para el historial.
- FK activas en onConfigure. No hay edición/borrado en este incremento. RESTRICT conserva relación; futuras migraciones explícitas, sin borrar datos.
- Identidad por ID; nombres iguales permitidos (no se exige unicidad). Tipo de cuenta se mantiene pendiente y no se inventa catálogo.
- La transacción de registrar valida el saldo contra overflow antes del insert; las operaciones de creación son insert de una fila.
- Compras/deudas, ubicación y sus políticas siguen pendientes. Esta versión no crea tablas futuras.

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
