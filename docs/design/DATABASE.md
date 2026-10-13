# Diseño SQLite

**Estado:** esquema v3: cuentas/movimientos (T03), contactos/compras/detalles (T04) y deudas (T05).

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
- T03 solo creó cuentas/movimientos. T04 añade las tablas documentadas debajo; deudas, reparto y ubicación siguen pendientes.

## Migración aditiva S00-T04, v1 → v2

SQLiteOpenHelper aumenta la versión a 2. onUpgrade(1,2) solo crea las tres tablas e índice siguientes; no borra ni reescribe cuentas/movimientos. SQLiteOpenHelper hace atómica la migración y el cambio de versión. onCreate crea las tablas contables originales y las nuevas para instalaciones limpias.

| Tabla | Columnas/restricciones |
|---|---|
| contactos | id INTEGER PRIMARY KEY; nombre TEXT NOT NULL, trim no vacío |
| compras | id INTEGER PRIMARY KEY; nombre TEXT NOT NULL no vacío; estado TEXT PLANIFICADA/REALIZADA; fecha INTEGER NOT NULL >= 0; contacto_id nullable FK contactos(id), ON DELETE RESTRICT |
| detalle_compra | id INTEGER PRIMARY KEY; compra_id INTEGER NOT NULL FK compras(id), ON DELETE RESTRICT; descripcion TEXT no vacía; cantidad INTEGER > 0; precio_unitario INTEGER >= 0 |

- Cantidades enteras positivas (unidades de producto/servicio); precios en centésimas long, como T03. Subtotal con multiplyExact y total con addExact, sin redondeos ni columnas derivadas.
- Fecha epoch en milisegundos seleccionada con DatePicker; puede expresar la fecha realizada o planificada. Se muestra en la zona local. No se imponen restricciones de fecha no especificadas; no hay notificaciones ni ejecución automática.
- Al menos un detalle válido en cada Compra; colección inmutable y total validado antes del insert. Cabecera y detalles se guardan en una transacción de CompraRepository; un fallo revierte toda la compra.
- Índice detalle_por_compra(compra_id,id) para consulta y orden de ítems. Historial por fecha DESC,id DESC. Sin catálogo de productos ni campos obligatorios de teléfono/email.
- Sin edición/borrado ni transición de estados en T04. Estado elegido al registrar. La política compra→movimiento sigue PENDIENTE; no se genera movimiento, no se solicita cuenta de cargo ni se altera saldo.
- Prueba de migración parte del DDL v1 y registros previos; el emulador comprueba saldos/historial, compras/contactos y reinicio de proceso. Ver handoff S00-T04.

## Principios

- SQLite es la única persistencia del alcance actual.
- Acceso mediante `SQLiteOpenHelper` y repositories.
- Activar/usar integridad referencial según corresponda.
- SQL fuera de Activities/Fragments.
- Evitar almacenar datos derivados que puedan quedar inconsistentes.
- Operaciones multi-tabla que deban ser atómicas deben usar transacciones.

## Migración aditiva S00-T05, v2 → v3

SQLiteOpenHelper crea únicamente deudas. Para instalaciones v1 se ejecutan en secuencia crearCompras y crearDeudas; onCreate incluye ambas. No se borra ni reescribe información anterior. La migración de SQLiteOpenHelper es atómica.

| Tabla | Columnas/restricciones |
|---|---|
| deudas | compra_id INTEGER NOT NULL FK compras(id) RESTRICT; contacto_id INTEGER NOT NULL FK contactos(id) RESTRICT; monto INTEGER NOT NULL >= 0 y typeof integer; estado TEXT NOT NULL, PENDIENTE; PK(compra_id,contacto_id) |

Una sola asociación representa participante, asignación y deuda del total de una compra. Sustituye las tres tablas candidatas del diseño preliminar; no hay total redundante ni reparto por ítem. El modo es una operación de cálculo transitoria, no un dato necesario para consultar cuánto debe cada persona. Marcar PAGADA no se implementa en T05.

DeudaRepository registra todas las filas en una transacción, revalida el reparto contra la compra REALIZADA persistida y rechaza compras ya repartidas, incluso con otro conjunto de participantes. Las FK y PK protegen contactos/compras y duplicados; un error revierte todos los inserts. La suma exacta es una invariante de Reparto/repository; no se pretende hacer un CHECK SQL entre varias filas. No hay edición/borrado ni generación de movimientos.

Pruebas: fixture v2 con snapshot de todas las filas previas, FK/PK/CHECK, trigger que falla en el segundo insert, cuota cero, rechazo de total ficticio y reparto repetido. Flujo UI y reinicio real en emulador; evidencia y estado final en handoff S00-T05.

## Diseño preliminar histórico (sustituido para T05)

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
| Participante | `deudas` (asociación mínima T05) |
| Asignacion | `deudas.monto` (total por contacto T05) |
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

## Decisiones pendientes para módulos restantes y ampliaciones

- campos definitivos de las tablas restantes;
- valores de tipo de cuenta;
- dinero y fecha de los módulos restantes (cuentas/movimientos ya definidos arriba);
- políticas `ON DELETE`/`ON UPDATE`;
- si una compra realizada genera movimiento automáticamente;
- pagos parciales: excluidos de T05; ampliación futura;
- algoritmo cerrado para T05 en BUSINESS_RULES;
- simplificación aprobada/implementada para T05: tabla única deudas.

## Regla de freeze

Cuando S01 cierre estas decisiones:
1. actualizar este archivo con columnas/tipos/constraints definitivos;
2. actualizar `database-er.md`;
3. implementar `LocalCashDbHelper`;
4. añadir pruebas CRUD;
5. actualizar trazabilidad.

