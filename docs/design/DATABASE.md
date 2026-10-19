# Diseño SQLite

**Estado actual:** esquema v6, integrado por S01-T01: configuración inicial, divisas de cuentas/compras y cuota propia del reparto. Incluye cuentas/movimientos (T03), contactos/compras/detalles (T04), deudas (T05) y ubicación opcional (T06A). Las secciones v1–v4 siguientes conservan la evolución histórica; la sección S01-T01 documenta v5.

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
- T03 solo creó cuentas/movimientos. T04 añade las tablas documentadas debajo; T05 añade deudas/reparto. T06A añade ubicación opcional.

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

## Migración aditiva S00-T06A, v3 → v4

Se añaden únicamente movimientos.latitud REAL nullable y movimientos.longitud REAL nullable mediante ALTER TABLE. La creación nueva sigue el mismo DDL aditivo; v1→v4 y v2→v4 crean primero tablas de compras/deudas y luego las columnas. Los registros anteriores conservan todos sus valores e IDs, con ambas coordenadas NULL.

CHECK latitud: NULL o numérico en [-90,90]. CHECK longitud: ambos NULL o ambos presentes, numéricos y longitud en [-180,180]. No acepta pares incompletos en INSERT/UPDATE. Ubicacion rechaza NaN/infinito y fuera de rango antes de persistir; MovimientoRepository inserta/reconstruye el par opcional y valida su completitud.

No hay tabla de ubicaciones, direcciones ni historial GPS. Las coordenadas pertenecen al movimiento capturado explícitamente; no afectan su monto ni saldo. Sin modificación de cuentas/compras/deudas ni nueva dependencia.

Validación: fixtures originales v1/v2/v3, snapshots de columnas previas en las seis tablas correspondientes, NULL antiguos y FK intactas; dominio, SQL inválido, UI/permisos/GPS y reinicio de proceso en CI. Ver docs/testing/S00-T06A.md y handoff de la tarea.

## Diseño preliminar histórico (sustituido para T05)

1. `cuentas`
2. `movimientos`
3. `contactos`
4. `compras`
5. `detalle_compra`
6. `deudas` (T05; sustituye participantes/asignaciones separadas)

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




## S01-T01 — Divisas y cuota propia, esquema v5

Ampliación solicitada por Aikia el 18-10-2026. Este apartado sustituye la regla
«sin moneda» de los incrementos anteriores; no modifica sus evidencias históricas.

| Tabla/campo | Regla |
|---|---|
| configuracion | Fila única id=1; divisa TEXT NOT NULL del catálogo. Ausencia significa configuración inicial pendiente. |
| cuentas.divisa | Código TEXT del catálogo. Nullable únicamente para registros previos sin moneda identificada. |
| compras.divisa | Igual; los detalles y las deudas heredan esta moneda. |
| reparto_usuario | compra_id PK/FK compras RESTRICT; monto INTEGER >=0; cuota de «Tú», sin contacto o deuda ficticios. |

Catálogo fijo: CLP, USD, EUR, ARS, MXN, COP, PEN, BRL, GBP, JPY. CLP/JPY admiten
unidades enteras; las demás dos decimales. La representación existente NO se reescala:
100 CLP continúa almacenándose como 10000. No usar REAL para dinero. Cantidades y
precios se validan antes de guardar y la UI muestra códigos sin sumar distintas monedas.

Migraciones v1–v4→v5 aditivas: se ejecutan pasos anteriores necesarios y luego
se crean configuracion/reparto_usuario y se agregan columnas divisa. No se asigna
moneda principal ni por registro automáticamente. Configuración se pide tras biometría;
la moneda de cada cuenta/compra previa se asigna explícitamente al abrirla. Antes de
asignar CLP/JPY se valida saldo inicial, movimientos, precios unitarios y cuotas/deudas
según corresponda. Si hay fracciones se conserva todo y se rechaza la asignación.
Nunca se redondea, divide ni multiplica el importe histórico durante la migración.

Divisa ya asignada es fija (repositorios y triggers). CHECK protege catálogo; triggers
protegen precisión CLP/JPY en INSERT/UPDATE de montos y asignación de moneda.
Los servicios impiden nuevos movimientos/repartos sin divisa. Las lecturas históricas
sin divisa conservan representación decimal exacta y se etiquetan «Sin divisa».
Constructores antiguos se conservan para reconstrucción histórica y pruebas, sin
inventar una moneda. Nuevos formularios siempre guardan divisa explícita.

La cuota propia y todas las deudas de contactos se guardan en la misma transacción.
DeudaRepository revalida contra la compra persistida, exige suma exacta incluyendo
al usuario y rechaza un segundo reparto. Los repartos antiguos no reciben una cuota
propia retroactiva. Persistir cuotas evita tener que inferirlas al cambiar reglas.

Validación: tests/CurrencyDomainChecks.java, tests/sqlite-currency-checks.py,
CurrencyChecks Android, tests/currency-ui.py y workflow s00-t06c-build.yml. La
migración lógica SQLite no acredita actualización APK entre certificados distintos.

## S01-T02 — SQLite v6, pagos y CRUD (19-10-2026)

La migración v5→v6 es aditiva: agrega `pago_compra(compra_id PRIMARY KEY REFERENCES compras(id) ON DELETE RESTRICT, movimiento_id INTEGER NOT NULL UNIQUE REFERENCES movimientos(id) ON DELETE RESTRICT)` y triggers que rechazan nuevos saldos iniciales negativos y cambios al saldo inicial. No cambia filas históricas ni cobra compras realizadas anteriores.

El vínculo identifica el egreso de una compra. Crear una realizada o pagar una planificada guarda compra/ítems, egreso, vínculo y estado dentro de una transacción SQLite; verifica total positivo, moneda coincidente y fondos. Reintentar el mismo pago no vuelve a descontar. El dominio sigue calculando saldos exactos; repositories revalidan en transacción antes de insertar/editar/borrar movimientos. Un saldo negativo histórico se conserva y solo se permite mantenerlo/mejorarlo, nunca empeorarlo.

CRUD: cuenta cambia nombre y se elimina solo sin movimientos; saldo inicial/divisa permanecen fijos. Contacto cambia nombre y se elimina sin compras/deudas vinculadas. Movimiento manual conserva fecha/ubicación al editar; edición/borrado no puede producir saldo negativo. Movimiento de compra se protege. Compra planificada puede editar sus datos/ítems sin cambiar divisa; realizada solo cambia nombre. Eliminar compra sin reparto borra sus ítems y revierte su egreso/vínculo en una transacción; un reparto bloquea esa eliminación. No se borran deudas en cascada.
