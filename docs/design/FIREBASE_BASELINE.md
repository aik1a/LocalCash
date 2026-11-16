# FB-F0-T02 — Baseline e inventario para migración

Fecha: 2026-11-16. Responsable: Orquestador. Inspección estática y control documental; no auditoría independiente ni validación funcional nueva. Alcance: [brief](../tasks/active/FB-F0-T02-baseline-inventario.md), [protocolo](../agent/FIREBASE_PROTOCOL.md), [ADR-003](decisions/ADR-003-firebase-google-offline.md) y [FB-01](../exec-plans/active/FB-01-firebase-google-offline.md).

## 1. Base reproducible y procedencia

- Remoto comprobado con `git ls-remote`: main `f0e491d2f620fefcdfd18ad317a85c00a3b29a39`; integración `feat/firebase-migration` en `d7657d1cd798e3f86f9ac9fbe75a80af1fd28c4b`. Rama de tarea `task/FB-F0-T02-baseline-inventario`, creada desde esta última en clon limpio. No se reutilizó ni alteró la copia LocalCash con una eliminación local del wrapper JAR.
- Base funcional: `c28a27b87aeeb092a936b8ba6b05b83404ac1c13`. La etiqueta anotada `baseline/sqlite-v7-20261116` tiene objeto `e769eb88cea864ec714e3f853526f3335262996e` y apunta a ese commit; no es evidencia de firma o pruebas.
- [PR #16](https://github.com/aik1a/LocalCash/pull/16): merge confirmado por API el 2026-11-03 19:46:33 UTC, head `2bfec047b92a6bf2e31f70fae701be3a54b49673`, merge `c28a27b`. Integró pagos, CRUD T00–T08, avisos, unitarias y rediseño nativo. Estar integrado no equivale a revisión independiente o cierre de sprint. Main incorporó después el roadmap documental (#19); la integración añade el criterio MVP.
- [app/build.gradle](../../app/build.gradle): APK declarado 1.1.1/versionCode 3, applicationId `com.aik1a.localcash`, minSdk24, targetSdk36, compileSdk36.1, Java17. Esto describe configuración versionada, no un APK construido ahora.
- [LocalCashDbHelper.java](../../app/src/main/java/com/aik1a/localcash/data/LocalCashDbHelper.java): `localcash.db`, versión 7, nueve tablas, FK activadas en onConfigure. No hay Firebase implementado. [Manifest](../../app/src/main/AndroidManifest.xml): elimina INTERNET y almacenamiento externo; mantiene biometría y ubicación opcional. La adaptación de red se hará en una tarea funcional posterior.

## 2. Inventario de persistencia y operaciones

Todas las rutas abreviadas de clases siguientes parten de [app/src/main/java/com/aik1a/localcash/](../../app/src/main/java/com/aik1a/localcash/). Los seis repositorios concretos están en [data/](../../app/src/main/java/com/aik1a/localcash/data/); los tres servicios en [application/](../../app/src/main/java/com/aik1a/localcash/application/). No existen repositorios separados para detalle, cuota propia o vínculo de pago.

| Tabla v7 | Identidad, datos y restricciones reales | Repositorio / servicio y operaciones |
|---|---|---|
| cuentas | id INTEGER PK; nombre no vacío, saldo_inicial entero; divisa nullable histórica o catálogo | CuentaRepository: crear, listar, buscar, asignar divisa, renombrar, eliminarSinUso. MovimientoService coordina cuentas, saldo y movimientos |
| movimientos | id INTEGER PK; FK cuenta_id RESTRICT; tipo INGRESO/EGRESO, monto positivo, descripción, fecha >=0, par latitud/longitud opcional validado | MovimientoRepository crear/listar; MovimientoService.registrar y calcularSaldo; sin edición/borrado de movimientos en la API actual |
| contactos | id INTEGER PK; nombre no vacío, sin unicidad por nombre | ContactoRepository crear/listar/buscar/renombrar/eliminarSinUso; CompraService y DeudaService consumen contactos |
| compras | id INTEGER PK; nombre, estado PLANIFICADA/REALIZADA, fecha >=0, contacto_id nullable FK RESTRICT, divisa nullable histórica | CompraRepository crea/lee/asigna divisa/edita o elimina planificación/registra vínculo; CompraService coordina pago y consultas por cuenta |
| detalle_compra | id INTEGER PK; FK compra_id RESTRICT, descripción no vacía, cantidad >0, precio_unitario >=0 | CompraRepository inserta cabecera y detalles juntos; lee ítems por id ASC; reemplaza detalles al editar plan, los elimina antes de cabecera al borrar |
| deudas | PK(compra_id,contacto_id); ambas FK RESTRICT; monto integer >=0; estado PENDIENTE/PAGADA | DeudaRepository registra reparto, lee por compra/contacto y cambia estado positivo; DeudaService calcula pendientes por divisa |
| configuracion | id PK CHECK(id=1), divisa del catálogo NOT NULL | ConfiguracionRepository buscar/exigir/configurarPrimeraVez; insert CONFLICT_IGNORE y comprobación de valor; ausencia significa configuración pendiente |
| reparto_usuario | compra_id PK/FK RESTRICT; monto integer >=0 | DeudaRepository inserta/consulta cuota propia en la transacción de reparto; no crea contacto o deuda para «Tú» |
| compra_movimiento | compra_id PK/FK RESTRICT; movimiento_id NOT NULL UNIQUE/FK RESTRICT | CompraRepository.registrarPago/buscarCuentaPago; CompraService.pagar crea el único egreso y cambia estado dentro de transacción exterior |

Índices explícitos: movimientos_cuenta_fecha(cuenta_id,fecha,id) y detalle_por_compra(compra_id,id). Cuentas/contactos se listan por id ASC, movimientos por fecha ASC/id ASC, compras por fecha DESC/id DESC y deudas de compra por contacto_id ASC. Los índices de PK/UNIQUE aportan identidad/unicidad, no reglas de suma.

### Transacciones y límites de las garantías

- MovimientoService.registrar: lee cuenta e historial completo, valida divisa y calcula saldo incluyendo nuevo movimiento para detectar overflow antes del insert; transacción local. CuentaRepository.asignarDivisa y CompraRepository.asignarDivisa validan historial/ítems/cuotas y actualizan solo si divisa IS NULL dentro de transacción.
- CompraRepository.crear: cabecera y todos los detalles atómicos. actualizarPlanificacion: revalida estado, ausencia de pago/reparto y moneda fija; actualiza cabecera, borra detalles e inserta reemplazos en una transacción. eliminarPlanificacion: misma revalidación y borrado detalles/cabecera atómico. No hay CAS remoto ni semántica de conflictos distribuida.
- CompraService.pagar: transacción exterior sobre el mismo helper que la transacción anidada de MovimientoService; valida inexistencia de vínculo y misma divisa, registra EGRESO por total completo, inserta vínculo y actualiza REALIZADA. Un error revierte conjunto. registrarPago por sí solo no abre transacción: la garantía corresponde al servicio, no a cualquier llamada al repositorio.
- DeudaRepository.registrar: vuelve a leer compra y reconstruir reparto válido, rechaza reparto existente (deudas o cuota propia), inserta contactos y cuota propia juntos. PK evita repetir contacto, pero suma exacta y exclusión de segundo reparto pertenecen a dominio/repositorio, no a un CHECK SQL entre filas.
- Renombres actualizan una fila y conservan IDs, moneda e historial. Borrar cuenta/contacto usa DELETE condicionado por NOT EXISTS y FK RESTRICT: cuenta sin movimientos; contacto sin compras ni deudas, incluso pagadas o cero. No hay cascadas de borrado. Confirmar en UI no sustituye revalidación persistente.
- Triggers reales: cuentas_divisa_fija y compras_divisa_fija; cuentas_precision_insert/update; compras_precision_update (valida detalle/deuda/cuota al asignar moneda); usuario_precision_INSERT/UPDATE, movimientos_precision_INSERT/UPDATE, detalles_precision_INSERT/UPDATE y deudas_precision_INSERT/UPDATE. Rechazan fracciones CLP/JPY y cambio de moneda ya asignada. Configuración inicial inmutable por API, sin trigger de inmutabilidad equivalente.
- El helper ejecuta pasos v1→v7; v5 añade monedas/configuración/cuota propia, v6 vínculo de pago y v7 reconstruye deudas para PAGADA, retirando/recreando tres triggers. El nombre `deudas_v7` es temporal de migración, no una décima tabla permanente. Migración y versión se gestionan por SQLiteOpenHelper. La existencia del código no demuestra ejecución de todas las rutas de actualización.

### IDs, dinero, fechas y lecturas derivadas

[Dominio](../../app/src/main/java/com/aik1a/localcash/domain/): Cuenta/Movimiento/Compra/Contacto usan long; 0 representa entidad nueva y SQLite asigna ID por INTEGER PRIMARY KEY, sin AUTOINCREMENT explícito. Los IDs son locales a tabla/base; pueden coincidir entre instalaciones y pueden reutilizarse tras borrado. Deuda/Reparto usan referencias long y reparto usa Map/TreeMap de IDs; DetalleCompra no expone el ID de su fila. No convertir esos valores directamente en identidad global.

La UI también depende de long: CUENTA_ID en CuentaActivity/MapaActivity/ComprasActivity; COMPRA_ID en CompraDetalleActivity/RepartoActivity; EDITAR_COMPRA_ID en NuevaCompraActivity; usa getLongExtra y -1 como ausencia. La importación deberá conservar relaciones, orden de reparto y un mapeo estable por origen/tabla/ID, incluida identidad compuesta y vínculos. F1 decide implementación string sin romper ese contrato; el orden lexicográfico de IDs nuevos no debe cambiar cuotas históricas.

Dinero usa long en centésimas para TODAS las divisas: 100 CLP = 10000 almacenado; CLP/JPY exigen múltiplos de 100, las otras ocho admiten centésimas. No reescalar ni redondear históricos. Cuenta.calcularSaldo suma saldo inicial y montos con signo con Math.addExact; permite saldo negativo, rechaza otra cuenta y overflow. Compra.calcularTotal suma cantidad × precio_unitario con operaciones exactas. No existen columnas saldoActual ni totalCompra. Fechas son epoch ms funcionales; registro usa reloj local y compras permiten fecha elegida. No sustituirlas por confirmación del servidor.

Reparto.equitativoConUsuario divide en unidad mínima; resto primero a Tú y luego contactos por ID ascendente. 100 CLP / tres personas = 34/33/33. Personalizado exige montos >=0, precisión correcta y suma exacta; contactos distintos y compra REALIZADA. Existen métodos históricos sin cuota propia para reconstrucción/pruebas; no agregar cuota retroactiva. Se reparte total, no cada ítem.

Compra PLANIFICADA no afecta saldo. Pago explícito completo también se permite sobre compra histórica REALIZADA sin vínculo; UI advierte revisar egreso manual previo. No detecta duplicados semánticos históricos. Total cero no genera pago. Repartir o marcar deuda PAGADA/PENDIENTE no genera movimientos; cuota cero no admite cobro. DeudaService.totalesPendientes suma solo positivas pendientes por divisa, excluye sin divisa de agregados. No hay pagos parciales, devolución implícita, conversión, reparto colaborativo ni rehacer reparto.

## 3. Pantallas, consumidores y pruebas existentes

Clases bajo [presentation/](../../app/src/main/java/com/aik1a/localcash/presentation/), layouts bajo [res/layout/](../../app/src/main/res/layout/); variantes horizontales de Main y Mapa en [layout-land/](../../app/src/main/res/layout-land/).

| Flujo y pantallas reales | Dependencias actuales | Pruebas relacionadas presentes (no repetidas aquí) |
|---|---|---|
| MainActivity, CuentaActivity: cuentas, renombre/borrado, ingresos/egresos/saldo, compras de cuenta | helper, MovimientoService, CompraService; DivisasUI para asignación histórica | FlowChecks sin mode; CurrencyChecks; CrudChecks; CuentaTest, DineroTest |
| ContactosActivity: agenda y consulta compras/deudas por contacto | helper, CompraService, DeudaService | FlowChecks purchases/shared; CrudChecks, CompraYDeudaTest |
| ComprasActivity, NuevaCompraActivity: historial ordenado por fecha, filtro por cuenta, crear/editar plan e ítems | helper y CompraService; IDs long y lecturas síncronas | FlowChecks purchases; CrudChecks, CompraYDeudaTest |
| CompraDetalleActivity: detalle, pago confirmado, cuenta de cargo, borrar/editar plan | helper, CompraService; consulta cuota/deuda | PaymentChecks; CrudChecks; FeedbackChecks |
| RepartoActivity: participantes, cuotas, estado de deudas | helper, DeudaService | FlowChecks shared; CurrencyChecks, CrudChecks, RepartoTest |
| MapaActivity, CapturaUbicacion, MapaLocalView, AtlasOffline | helper y MovimientoService para marcadores por cuenta; LocationManager y ZIP local | FlowChecks location; MapChecks, UbicacionTest; scripts GPS/mapa/relanzamiento |
| ActividadProtegida, AccesoActivity, LocalCashApplication | gate BiometricPrompt y sesión en memoria; ActividadProtegida lee ConfiguracionRepository; DivisasUI lee/escribe repositorios directamente | BiometricChecks y scripts biometric-emulator.py/currency-ui.py |
| Avisos/formularios compartidos | AccionesUI, Pantalla, validación y excepciones locales | presentation/FeedbackChecks; tests/FeedbackDomainChecks.java |

Lectores/creadores de LocalCashDbHelper en producción: los ocho Activities financieros de la tabla, ActividadProtegida y DivisasUI; además los seis repositorios y tres servicios lo reciben. AccesoActivity/LocalCashApplication no son repositorios. La UI espera retornos/excepciones síncronas; F1 debe fijar estados de carga, local, pendiente, confirmado y rechazado, sin cerrar helper/listeners antes del resultado asíncrono.

Pruebas Android bajo [androidTest/java/com/aik1a/localcash/](../../app/src/androidTest/java/com/aik1a/localcash/): FlowChecks es el único Instrumentation registrado; despacha modes purchases/shared/location, bio-*, maps, currency, payment, crud, crud-upgrade y feedback. Las otras clases son auxiliares, no runners autónomos. CrudChecks usa data/LegacyV6DbHelper y snapshots de nueve tablas, FK, triggers, rollback y estado; crud-upgrade verifica una instalación previamente preparada, no crea por sí solo un upgrade APK. PaymentChecks verifica divisa incompatible, pago, saldo y duplicado secuencial, sin concurrencia de dos clientes. Varios modos borran/alteran localcash.db: solo ejecutar en instalación sintética descartable.

[Unitarias JUnit](../../app/src/test/java/com/aik1a/localcash/domain/): CuentaTest, CompraYDeudaTest, DineroTest, RepartoTest, UbicacionTest (29 métodos @Test contados estáticamente). [tests/](../../tests/): domain-checks.sh compila/ejecuta DomainChecks, PurchaseDomainChecks, SharedDomainChecks, LocationDomainChecks y CurrencyDomainChecks; no equivale a JUnit y no incluye FeedbackDomainChecks. sqlite-currency-checks.py y payment-sqlite-checks.py son comprobaciones SQL del host; no sustituyen servicios Android ni reglas Firestore.

## 4. Matriz de garantías a preservar

Reglas: [BUSINESS_RULES](BUSINESS_RULES.md), incluidas las sustituciones S01/UX-01/CRUD-02. Las pruebas de la última columna son requeridas para la viabilidad/diseño F1 o su implementación posterior; todas pendientes para Firestore. Ninguna garantía SQL se presume trasladada.

| Operación | Regla funcional | Garantía SQLite actual y capa | Riesgo al migrar | Prueba necesaria |
|---|---|---|---|---|
| Crear/configurar cuenta y moneda principal | BR-CUR-01/02/03; nombre válido | PK fila 1 + API primera vez; CHECK catálogo; dominio; triggers moneda fija | Dos inicializaciones, moneda mutable o nullable tratada como cero | Reintento y dos clientes configurando distinto; campos inválidos; histórico sin divisa |
| Asignar moneda histórica | BR-CUR-04 | Transacción revisa todos los importes; UPDATE condicionado; triggers precisión/fijeza | Historial parcial permite CLP/JPY sobre fracciones | Cuenta/compra con fracciones en movimiento, detalle, deuda y cuota; rechazo sin pérdida; carrera con nuevo importe |
| Registrar movimiento y saldo | BR-MOV-01, BR-ACC-01, BR-CUR-03 | FK/CHECK; transacción valida historial completo y overflow; saldo derivado | Duplicado por reintento, caché incompleta, cuenta borrada, overflow concurrente | Ingreso/egreso, saldo negativo permitido, overflow, ID estable/reinicio, historial faltante no presentado definitivo; carrera borrar/insertar |
| Ubicación y mapa | BR-GEO-01, FB-R05 | CHECK par/rangos, VO Ubicacion; ZIP offline independiente de SQL | Coordenadas parciales o marcadores de otra cuenta/UID | Nulos/par inválido/NaN, filtro cuenta, modo avión y reinicio con atlas local |
| Crear compra y detalles | BR-BUY-01/02 | Dominio exige detalles y total exacto; transacción cabecera+ítems; FK/CHECK | Cabecera incompleta, total calculado con ítems ausentes, overflow | Fallo intermedio/rechazo, cero/precios/cantidad inválida, overflow, persistencia offline y totalidad de detalles |
| Pagar compra completa | UX-01, BR-MOV-01, BR-CUR-03 | Transacción servicio: egreso+vínculo+REALIZADA; PK compra y UNIQUE movimiento | Dos dispositivos pagan; lote reintentado crea otro egreso; estado aparente confirmado | Misma/distinta cuenta, cero y divisa incorrecta; fallo de cada escritura; reintento estable; dos clientes; rechazo/reinicio/reconexión sin doble cargo |
| Reparto equitativo/personalizado | BR-SHARE-03/04/05, BR-DEBT-01 | Revalidación compra persistida; transacción deudas+cuota; PK/FK, suma en dominio | Repartos mezclados, ID nuevo cambia orden, escritura parcial | 100 CLP→34/33/33 y centavo USD; duplicados, suma incorrecta, cero; segundo reparto concurrente; rollback/rechazo y reinicio |
| Marcar/deshacer deuda y consultar pendientes | CRUD-02, BR-DEBT-01 | UPDATE par exacto y monto>0; CHECK estado; suma por moneda con addExact; ningún movimiento | Última escritura oculta conflicto, agregados parciales, ingreso accidental | PAGADA/PENDIENTE, cero sin acción, monedas separadas/sin moneda, saldo inalterado; política de conflicto explícita |
| Renombrar cuenta/contacto | CRUD-02 | UPDATE nombre con dominio, conserva PK e historial | Reemplazar documento elimina campos o rompe referencias | Referencias/saldos intactos, nombre inválido; renombre concurrente y borrado según política F1 |
| Borrar cuenta/contacto sin uso | CRUD-02 | DELETE NOT EXISTS + FK RESTRICT | Chequeo de caché omite referencias; alta simultánea deja huérfano | Uso por movimiento/compra/deuda pagada/cero; carrera crear referencia vs borrar; rechazo no desaparece silenciosamente |
| Editar/borrar planificación | CRUD-02 | Transacción revalida PLANIFICADA sin pago/reparto y moneda fija; detalles+cabecera juntos | Edición/borrado simultáneo con pago o reparto; subcolección huérfana | Cambio estado/moneda rechazado, rollback al insertar/borrar, carrera pagar/editar/borrar, saldos intactos |
| Importar base y relaciones | FB-R01/04; conservación F0-T03 | Snapshot local coherente posible; nueve tablas y migraciones versionadas | IDs colisionan entre bases; repetición duplica pago; original borrado prematuramente | Dos orígenes con id=1, conteos/relaciones/cuotas/saldos exactos, interrupción y reanudación, confirmación remota antes de finalizar; firma compatible |

No todas las invariantes están en SQL: suma de reparto, compra con al menos un detalle y coherencia pago/estado/importe dependen del camino de servicio/dominio. No certificar seguridad de escrituras arbitrarias por tener FK/triggers. El código actual no resuelve concurrencia distribuida: F1 debe demostrar la alternativa mínima que conserve integridad y FB-R03 antes de migrar módulos.

## 5. CI: qué se ejecuta realmente

Inspección de los nueve [workflows](../../.github/workflows/) y sus scripts, sobre la base operativa indicada. No se modificaron ni dispararon workflows en esta tarea.

| Workflow | Activación / checkout | Trabajo declarado en YAML |
|---|---|---|
| s00-t02-build.yml | Push solo task/S00-T02-estructura-paquetes y su YAML; dispatch; checkout FIJO 6cea41252f26a76af44f81f990a99f0b95cf74b1 | Solo assembleDebug histórico; no valida el HEAD elegido por dispatch |
| s00-t03-build.yml | Push task/S00-T03-cuentas-movimientos, app/tests/YAML; dispatch; github.sha | Dominio, assemble app/instrumentación; API36 FlowChecks cuentas |
| s00-t04-build.yml | Push task/S00-T04-contactos-compras, app/tests/YAML; dispatch; github.sha | Anteriores + purchases y relaunch-checks, API36 |
| s00-t05-build.yml | Push task/S00-T05-gastos-compartidos, app/tests/YAML; dispatch; github.sha | Anteriores + shared/shared-relaunch-checks, API36 |
| s00-t06a-build.yml | Push task/S00-T06A-ubicacion, app/tests/YAML; dispatch; github.sha | Anteriores + location/GPS/relanzamiento, API36 |
| s00-t06b-build.yml | Push task/S00-T06B-mapas, app/tests/tools/maps/YAML; dispatch; head.sha o github.sha | Reproduce/verifica atlas, dominio, assemble, biometría/mapas; matriz API24/36 y dispatcher regresión |
| s00-t06c-build.yml | Push task/S00-T06C-biometria; PR opened/synchronize/reopened sin filtro de rama destino; paths app/tests/gradle.properties/su YAML (push omite gradle.properties); dispatch | Dominio, SQL monedas host, assemble; currency nativo, biometría, mapa, currency-ui y dispatcher API24/36; checkout head SHA exacto |
| s00-t07a-build.yml | Push task/S00-T07A-integracion, app/tests/tools/maps/YAML; dispatch; head.sha o github.sha; sin trigger PR | Atlas, dominio, assemble, biometría/mapas y dispatcher API24/36 |
| s00-t07b-signatures.yml | Push task/S00-T07B-firma-upgrade, tools/apk/YAML; dispatch; github.sha | Descarga artefactos retenidos/inventario firmas; no prueba upgrade con datos ni flujos financieros |

Todos ofrecen workflow_dispatch; elegir ref no corrige el SHA fijado en T02. Ningún push incluye feat/firebase-migration o task/FB-*; solo T06C tiene trigger PR, condicionado por paths. Documentos solos no disparan CI Android. Cambios solo en Gradle raíz/wrapper/settings u otro YAML pueden quedar fuera del filtro T06C. No existe pipeline Firebase/reglas/Emulator Suite todavía; planificar ajuste acotado antes del primer cambio funcional.

Ningún workflow llama :app:testDebugUnitTest; assembleDebugAndroidTest compila, no ejecuta modos. Ninguno invoca payment, crud, crud-upgrade, feedback ni payment-sqlite-checks.py. Dispatcher ejecuta cuentas/compras/reinicio/shared/GPS solo API36; API24 imprime que esa regresión completa se ejecuta en API36. Mapas, biometría y monedas sí tienen pasos explícitos en T06C para ambas APIs. connectedAndroidTest genérico no sustituye invocación de cada modo y preparación biométrica.

### Evidencia histórica recuperada ahora

Consulta remota realizada el 2026-11-16, sin rerun:

- [Run 41705745479](https://github.com/aik1a/LocalCash/actions/runs/41705745479), head final PR #16 `2bfec047...`: completed/failure; job [API24](https://github.com/aik1a/LocalCash/actions/runs/41705745479/job/126160459023) success y [API36](https://github.com/aik1a/LocalCash/actions/runs/41705745479/job/126160459675) failure. El log API36 muestra AssertionError: REALIZADA en tests/relaunch-checks.py:34, tras cuentas y purchases; el dispatcher aborta antes de shared/location. No afirmar regresión integral PASS.
- Discrepancia estática concordante: relaunch-checks.py busca REALIZADA/PLANIFICADA y ComprasActivity muestra Realizada/Planificada. Es un candidato concreto a corrección de prueba, sin demostrar por ello ausencia de otros defectos. Corregir y repetir corresponde a tarea separada: aquí no se tocó prueba ni código.
- API Actions consultada por SHA del merge c28a27b y de integración d7657d1 devuelve total_count=0. No hay evidencia de ejecución nueva sobre esos SHA en esa consulta.
- El cuerpo de PR #16 y [SYNC-UI](../handoffs/SYNC-UI-2026-11-03.md) declaran build, 29 JUnit, feedback y CRUD locales en momentos anteriores; [CI-MAIN](../handoffs/CI-MAIN-2026-11-03.md) conserva intentos fallidos previos. Son antecedentes atribuidos, no pruebas repetidas ni una auditoría independiente. Las auditorías S00 no se extienden a pagos/CRUD/Firebase.

## 6. Conciliación operativa e historial

Se actualizan entradas operativas de PROJECT_STATE, arquitectura, DATABASE y TRACEABILITY con enlaces a esta baseline. Se preservan debajo los registros por fecha, handoffs, auditorías y planes anteriores. Las frases v4/seis tablas, v5, «sin pago» o «solo local/no merge» de esos antecedentes ya no describen la app integrada tras #16; sus resultados y límites siguen siendo históricos, sin convertir integración en aprobación técnica. BUSINESS_RULES conserva sus decisiones sucesivas y no se modifica ninguna regla en F0-T02.

La app real sigue siendo SQLite v7; Firestore/Google/offline objetivo están planificados. La única diferencia de alcance aplicable proviene de ADR-003, no de este inventario. No se regeneró paquete académico ni se verificó recepción docente. Firma histórica incompatible y v5→v7 directa continúan pendientes; el upgrade sintético/local v6→v7 declarado en CRUD-02 no resuelve firma ni conservación de cualquier instalación real.

## 7. Próxima unidad: F0-T03 y decisiones para F1

F0 permanece abierta. Recomendar un brief F0-T03 de Orquestador con objetivo de registrar conservación/distribución y responsable Firebase; sin configurar servicios ni importar datos todavía.

Datos a obtener del usuario/responsable, sin copiar datos personales al repo:

1. Instalaciones que deben conservarse: dispositivo/variante/applicationId, versión, esquema, existencia y volumen aproximado de datos (nueve tablas), respaldo recuperable y UID Google al que se vincularían. No inspeccionar ni subir bases reales en esta tarea.
2. APK instalado y huella del firmante, disponibilidad del keystore compatible y custodio (solo metadatos; nunca claves). Estrategia de firma estable para próximos APK y prueba de upgrade con copia sintética. Si no es compatible, decisión explícita sobre distribución/conservación; no desinstalar para sortearlo.
3. Conservar/importar históricos o comienzo limpio autorizado con respaldo; cuándo se considera migración completa y qué hacer si se interrumpe. No decidir pérdida de datos por omisión.
4. Proyecto Firebase existente o responsable/cuenta que creará uno de desarrollo, permisos disponibles, identificador Android/huellas necesarias y dispositivo Google compatible para F2. No habilitar facturación ni publicar credenciales como efecto de este inventario.
5. Confirmar que primera autenticación/preparación conectada satisface la indicación docente; no se recibió nuevo documento escrito. Si exige primera instalación offline, escalar incompatibilidad antes de implementar Google obligatorio.

Entregables F0-T03: inventario de metadatos, decisión documentada de conservación/firma/distribución y responsable de desarrollo; respaldo/prueba de actualización acordados con límites. Gate F0: resolver esas decisiones junto con esta baseline; no basta publicar T02. Luego emitir brief F1-T01 pequeño con datos sintéticos y criterios de offline/reinicio/reconexión. Preparar contexto de pago para F1-T02 y restricciones para F1-T03, sin migrar todo a la vez.

Decisiones F1 aún abiertas: estructura documental y tamaño de agregados/detalles; IDs string y orden estable de reparto/importación; atomicidad e identidad de pago ante dos clientes; reparto y borrado referencial concurrentes; moneda fija y precisión en reglas; cobertura de caché para saldos/listas y preparación; tratamiento de pendientes/rechazos y cierre/cambio de UID; política mínima de conflictos por operación y contratos asíncronos. Probarlas, no aceptar genéricamente «última escritura gana». Si preservar una operación offline exige cambiar una regla, registrar la incompatibilidad y elevar al Orquestador/usuario, sin alterar la regla para acomodar el backend.

## 8. Verificación de esta entrega

Ejecutado ahora: inspección de refs/remoto, clon limpio, lectura selectiva de código/DDL/UI/pruebas/workflows, consulta PR #16 y run/jobs/log API36; control de diff, rutas/enlaces locales y criterios del brief registrado en el [handoff](../handoffs/FB-F0-T02-baseline-inventario.md). No se ejecutaron build, JUnit, pruebas Android/manuales, Firebase ni auditoría independiente: NOT TESTED en esta tarea. Es una entrega documental y no un gate de viabilidad de F1.
