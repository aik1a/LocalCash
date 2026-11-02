# Referencia diagnóstica previa — no es gate del nuevo código

El informe siguiente conserva el análisis de la sesión del 01/11/2026. Su división A/B quedó sustituida por el pedido posterior de alcance completo y por docs/exec-plans/active/MVP-PULIDO-COMPLETO.md. No declara implementadas ni aprobadas las tareas del nuevo ciclo.

# LocalCash — Auditoría y plan de pulido del MVP
**Fecha:** 01/11/2026 · **Fecha límite solicitada:** 02/11/2026 · **Zona:** America/Santiago  
**Nivel objetivo:** proyecto académico / MVP junior.  
**Repositorio:** aik1a/LocalCash · **Rama:** main · **Commit:** 9b8a651d66982eea3a41e1979342468fe4cad176.  
**Alcance de esta entrega:** auditoría, investigación, segunda revisión y planificación. No se modificó ni publicó código del proyecto.

## 1. Dictamen y decisión recomendada

**El proyecto tiene una base aprovechable, pero no satisface el CRUD completo de la matriz ni todos los comportamientos financieros solicitados.** La prioridad es permitir corregir registros sin romper saldos o relaciones. La mayor ampliación pendiente es vincular compras y movimientos; debe tratarse como una tarea propia.

- **Matriz docente:** NO CUMPLE completamente, por falta comprobada de actualización y eliminación de registros principales.
- **Reglas solicitadas por Aikia:** PARCIAL; faltan protección de saldo, cobro de compra desde cuenta y reversión.
- **Calidad estructural:** adecuada para continuar; no hay razón para reemplazar el stack.
- **Certificación final del APK en un teléfono:** PENDIENTE. Esta auditoría no ejecutó Android ni una revisión visual real.
- **Gates de commit/merge/avance:** N/A. No existe un incremento nuevo implementado que pueda aprobarse.

**Recomendación para el 02/11:** cerrar primero saldo, CRUD y una interfaz mínima utilizable; reservar la prueba de apertura y el recorrido final. Añadir pago y reversión de compras solo si queda un bloque completo de implementación y pruebas. No reaplicar PR #14 en bloque.

La hora oficial de entrega y las horas de trabajo disponibles no fueron informadas. El roadmap usa esfuerzo efectivo, no supone que Aikia dispone de todo el día. No promete completar dos jornadas de trabajo dentro de una tarde.

## 2. Fuentes, método y límites

### Fuentes del proyecto

Se consultaron el PDF docente original, la matriz EV3 en Excel, la imagen de arquitectura y la documentación vigente: PROJECT_ROUTER, AGENTS, PROJECT_STATE, rol AUDITOR, matriz/plantilla de auditoría, contexto de agentes, requisitos, reglas, base de datos, arquitectura, trazabilidad, handoff y Task Brief S01-T01. Se inspeccionaron dominio, servicios, repositorios, Activities, XML, manifiesto, Gradle, pruebas y workflows relevantes.

La imagen es una referencia conceptual; no obliga a crear cada columna dibujada. En particular, **no se debe almacenar una huella biométrica**, añadir contraseñas propias o persistir un saldo calculado solo para imitarla.

### Verificación realizada

| Comprobación | Resultado | Límite |
|---|---|---|
| Consulta nueva de main | Continúa en 9b8a651, rollback de S01-T02 | No hay cambios funcionales nuevos desde la primera auditoría de esta sesión |
| Identidad de fuentes | 89 archivos descargados comprobados contra SHA de blob Git | Se normalizó únicamente un salto de línea añadido durante el transporte; después coincidieron byte a byte |
| Suites de dominio | PASS: 18 + 15 + 5.427 + 23 + 28.917 = **34.400 comprobaciones** | Son aserciones; no 34.400 flujos de usuario |
| SQLite host con DDL Java real | PASS: **55 comprobaciones** | No sustituye SQLite Android ni actualización del APK |
| Casos adicionales de auditoría | Confirmados: saldo inicial negativo, sobregiro, bloqueo de PAGADA, restricciones FK y precisión monetaria | Dominio JVM y SQLite del host; no se declara reproducción desde pantalla |
| XML y accesibilidad | Lectura estática y revisión de etiquetas | No se midieron contraste, áreas táctiles ni recortes en render real |
| CI histórico S01-T01 | API24/API36 SUCCESS; logs contrastados en esta sesión | Código técnico idéntico al actual; la diferencia neta hasta main contiene solo documentación |
| Android local | NO EJECUTADO | Sin SDK/adb/emulador disponible; JDK local 17, CI utiliza 25 |
| Teléfono, recorrido humano y Android Studio limpio | NO PROBADOS | Deben formar parte del cierre de la implementación |
| Conservación de datos tras upgrade APK | NO PROBADA | Riesgo de firma histórica ya documentado |

CI de referencia: [39178585250](https://github.com/aik1a/LocalCash/actions/runs/39178585250), código técnico 6b7813cb81a8742c1b6fd12f8259ec637852487f. En API24 no consta toda la regresión financiera/GPS heredada; sí la validación específica de divisas, biometría y mapas. No se ejecutó de nuevo una CI sin cambios.

### Reconciliación con solicitudes anteriores

El historial recuperado contiene pedidos explícitos de saldo no negativo, elegir cuenta al comprar, no descontar una planificación, cobrar una sola vez y deshacer devolviendo el cargo. Son requisitos del plan actual, aunque BUSINESS_RULES todavía no los incorpora. **La observación anterior de “no imponer el bloqueo” omitía ese contexto y queda corregida.**

El repositorio sigue mandando para determinar qué está implementado. Las conversaciones no acreditan que una funcionalidad exista.

## 3. Matriz de hallazgos y mejoras

**P0:** necesario para la entrega prioritaria. **P1:** siguiente incremento o corrección acotada si cabe. **P2:** después del plazo.  
La severidad refleja el impacto; la prioridad también considera esfuerzo y riesgo. Una mejora de producto puede ser importante sin caber en el primer corte.

Las rutas de esta sección son relativas al repositorio; las líneas corresponden al commit auditado. Prefijos:  
D = app/src/main/java/com/aik1a/localcash/domain/  
A = app/src/main/java/com/aik1a/localcash/application/  
R = app/src/main/java/com/aik1a/localcash/data/  
UI = app/src/main/java/com/aik1a/localcash/presentation/

| ID | Hallazgo y evidencia | Impacto / severidad | Propuesta mínima | Prioridad / aceptación |
|---|---|---|---|---|
| F01 | R/CuentaRepository, MovimientoRepository, ContactoRepository y CompraRepository crean/consultan; no ofrecen CRUD general. Los únicos updates ordinarios observados asignan divisa histórica. No hay delete. | MAJOR frente al criterio 5 de la matriz. Los errores de captura quedan permanentes. | Añadir actualización y eliminación por ID en los repositorios existentes. UI con acciones explícitas. Verificar filas afectadas. No duplicar una capa DAO solo por el nombre. | P0. Crear→consultar→editar→consultar→eliminar→reiniciar, conservando registros ajenos. |
| F02 | D/Cuenta.java:14–38 admite negativos; A/MovimientoService.java:42–55 solo comprueba precisión y overflow. tests/DomainChecks.java:20–21 espera saldo negativo. | MAJOR frente al pedido del usuario. Se reproduce 1.000 − 1.500 = −500 CLP. | Validar saldo proyectado dentro de transacción al registrar/editar/borrar movimientos y al corregir saldo inicial. Mantener lectura de datos históricos. | P0. Saldo exacto permite cero; sobregiro rechazado sin cambios; borrar un ingreso no puede crear déficit. |
| F03 | A/CompraService y UI/CompraDetalleActivity.java:18–44 no permiten completar una planificación ni cobrarla. No hay vínculo compra–movimiento. | MAJOR frente al flujo solicitado; no es un cálculo roto de la funcionalidad actual. | Servicio pequeño para confirmar compra, cuenta de igual divisa, egreso y vínculo único en una transacción. Deshacer por la misma ruta. | P1 en el segundo corte por riesgo y esfuerzo; requisito pendiente explícito. Doble toque no duplica cargo. |
| F04 | R/LocalCashDbHelper.java:43–48 restringe estado a PENDIENTE; D/Deuda.java devuelve siempre PENDIENTE. | MINOR en el alcance académico vigente: PAGADA era opcional. Limita uso posterior. | Posteriormente admitir PENDIENTE/PAGADA y leer el estado real. Sin pagos parciales ni ingreso automático. | P2. No añadir solo un botón: requiere migración y prueba de conservación de filas. |
| F05 | UI/MainActivity, CuentaActivity y otras pantallas llaman repositorios en callbacks de UI; UI/MapaActivity prepara y verifica archivos del atlas síncronamente. | MINOR / riesgo de respuesta lenta; no se midió un ANR. | Un ExecutorService sencillo para las operaciones tocadas, resultados al hilo principal; proteger lifecycle y sesión. Evitar una migración general a MVVM. [S1][S2] | P1 acotado. No bloquear interfaz, no cerrar helper antes de terminar tareas, no mostrar datos tras bloquear sesión. |
| F06 | UI/MainActivity.java:46–51 consulta saldo por cuenta; A/MovimientoService.java:35–36 carga historial completo. R/CompraRepository.java:48–70 consulta detalles por compra. | MINOR / escalabilidad, no fallo demostrado con datos pequeños. | Mantener ahora; si se observa lentitud, calcular resúmenes con consultas agrupadas y mapearlos una vez. | P2. Evitar optimización SQL de último minuto sin un problema medido. |
| F07 | Pantallas con ListView y botones nativos; no existen acciones de edición/borrado, filtros de compras ni gesto. XML con textos repetidos y estados técnicos en mayúsculas. | MINOR de UX, más grave cuando oculta una operación necesaria. | Menú visible “⋮” o botones en detalle. Etiquetas “Planificada/Realizada”, divisa siempre visible, éxito breve y error persistente. Filtros simples solo si caben. | P0 para acciones de CRUD; decoración/filtros P1. Todas las acciones deben poder usarse sin deslizar. |
| F08 | UI/activity_nueva_compra.xml: Cantidad usa numberDecimal y signos aunque el dominio exige entero positivo. Spinners Estado/Contacto carecen de asociación explícita labelFor o nombre accesible. | MINOR; el dominio rechaza datos inválidos, pero la entrada induce errores. | Teclado entero para cantidad, etiquetas asociadas, ejemplos por divisa, controles de al menos 48dp y texto flexible. [S3][S4] | P0 en las pantallas tocadas. Probar teclado, foco, fuente grande y nombres largos. |
| F09 | Manifiesto:12–15 no declara allowBackup ni reglas de extracción. Auto Backup puede incluir la BD aunque la app no tenga permiso INTERNET. | Configuración de privacidad incompleta; **no prueba que se haya subido ningún dato**. | Para la promesa estrictamente local, explicitar política y excluir BD de cloud/D2D con reglas por versión; documentar diferencias de fabricantes. [S8][S9] | P1 acotado, sin crear exportaciones ni sistema de cifrado nuevo. Comprobar manifest fusionado y reglas. |
| F10 | CI s00-t06c-build.yml sí cubre pull_request para app/tests, pero omite build.gradle, settings.gradle y gradle/** en paths. Otros workflows apuntan a ramas históricas; no hay push general a main. | MINOR. Algunos cambios del build pueden omitir validación automática. | Ampliar filtros del workflow reutilizable, conservar ejecución manual y validar SHA exacto. No rehacer toda la CI. [S10] | P0 al preparar la tarea. Un PR con cambios Gradle debe disparar comprobaciones. |
| F11 | docs/delivery/BUILD.md:15 declara SQLite v4 y :29 indica checkout anterior a divisas. PROJECT_STATE y helper están en v5. | DOCUMENTATION. Se puede entregar o compilar una versión distinta de la auditada. | Alinear guía, versión SQLite, SHA/APK de entrega y trazabilidad con el último incremento aceptado. | P0 de cierre. La guía lleva a la versión entregada, no al paquete histórico S00. |
| F12 | app/build.gradle mantiene versionCode 1; el estado documenta firmantes históricos diferentes y upgrade bloqueado. | Riesgo de distribución y conservación de datos; no es la causa demostrada del fallo de apertura de PR14. | Reutilizar firmante para futuras versiones, incrementar versionCode y ensayar instalación conservando datos en equipo de prueba. [S7] | P0 si se actualiza instalación existente. No desinstalar una instalación con datos para “resolver” la firma. |
| F13 | Falta evidencia reciente de recorrido humano, teléfono físico y render visual. PR14 tenía CI favorable, pero el usuario informó que no abría y pidió rollback. | Brecha de validación esencial para la entrega. | Smoke real del APK final antes de añadir más trabajo. Si falla, capturar primer FATAL EXCEPTION/logcat; aislar la tarea responsable. | P0. No considerar terminado solo porque compila o pasan suites heredadas. |
| F14 | Imagen conceptual incluye usuario/password_hash/huella, saldo_actual y más entidades que el modelo vigente. | DOCUMENTATION; trasladarla literalmente agregaría riesgos y trabajo innecesario. | Actualizar después el diagrama al modelo real. Mantener biometría Android, saldo derivado y esquema reducido. | P2, salvo aclaración breve en la exposición. |

Los hallazgos F01–F03 se diferencian: F01 incumple directamente la matriz; F02 y F03 son brechas frente al comportamiento que el usuario pidió. No se atribuyen retroactivamente a un requisito docente que no lo diga.

## 4. Matriz docente: dónde concentrar el esfuerzo

| Criterio EV3 | Estado observado | Para mejorar antes de entregar |
|---|---|---|
| 1. Interfaces Android | Parcial; estructura y navegación existen, sin certificación visual | Acciones CRUD visibles, errores útiles, recorrido con teclado/fuente grande |
| 2. Java | Base implementada y CI favorable sobre el mismo código | Mantener ejecución estable después de cada tarea; no cambiar lenguaje ni arquitectura |
| 3. SQLiteOpenHelper | Cumple en código y pruebas disponibles | Conservar datos y transacciones al introducir operaciones nuevas |
| 4. Modelo y relaciones | Coherente con alcance actual | Reglas explícitas de borrado; vínculo único solo si se implementa pago |
| 5. CRUD mediante DAO | No cumple completamente | Actualizar y eliminar en repositorios y exponerlos en UI |
| 6. Cuentas y movimientos | Parcial frente a administración completa y pedido de saldo | Editar/borrar con validación transaccional y saldo correcto |
| 7. Compras, contactos y deudas | Funcionalidad base presente; ciclo de compra incompleto | CRUD básico; pago/transición en un incremento completo; PAGADA posterior |
| 8. Buenas prácticas Java | Base apropiada para junior, con puntos por pulir | Métodos legibles, mensajes compartidos y responsabilidades existentes; evitar refactor general |
| 9. Mapas | Offline implementado con cobertura limitada | Probar que continúa funcionando; no ampliar cartografía ahora |
| 10. Pruebas y offline | Evidencia automatizada fuerte, cierre humano pendiente | Añadir casos de operaciones nuevas y validar el APK final en modo avión |

El 80/100 anterior era una estimación diagnóstica. No hay una nueva nota certificable sin ejecutar las interfaces; tampoco corresponde prometer 100/100 después de hacer cambios.

## 5. Reglas propuestas para implementar sin contradicciones

Estas son propuestas operativas concretas para el siguiente Task Brief. Deben quedar en las reglas y pruebas del incremento; todavía no están implementadas.

### 5.1 Dinero, divisas y “redondeo”

**Conservar S01-T01.** CLP y JPY enteros; USD y las demás divisas del catálogo con dos decimales. El dinero sigue almacenado como long en centésimas históricas: 1.000 CLP = 100.000 unidades persistidas. No convertir todo el esquema a “unidades por moneda”.

- Entrada: aceptar coma o punto decimal según el parser existente; sin separadores de miles ambiguos. Ejemplo CLP 2500; USD 25,50.
- Presentación: código de divisa visible. No confundir formateo con conversión.
- Precisión extra: rechazar con un error humano. No truncar ni redondear silenciosamente el historial.
- Reparto: distribuir unidades mínimas y resto. 100 CLP / 3 = 34 + 33 + 33; 100,00 USD / 3 = 33,34 + 33,33 + 33,33.
- No sumar saldos de monedas diferentes ni permitir pagar una compra USD desde una cuenta CLP.
- “Redondear cada cuota por separado” puede romper la conservación del total; no reemplazar el reparto exacto actual.
- BigDecimal permite definir una política de redondeo, pero la documentación del lenguaje no decide cuál quiere el producto. [S6] Para este plazo, mantener la regla vigente de precisión exacta.

### 5.2 Saldo no negativo sin romper datos existentes

La regla se aplica al ejecutar una operación, no prohibiendo reconstruir cualquier Cuenta histórica.

1. Nueva cuenta: saldo inicial >= 0.
2. Egreso nuevo: saldo resultante >= 0.
3. Edición: simular sustitución del movimiento original, no sumarlo dos veces.
4. Eliminación: simular retirada del original; borrar un ingreso también puede dejar la cuenta sin fondos.
5. Corrección de saldo inicial: validar el resultado con los movimientos existentes.
6. Cuenta histórica ya negativa: permitir lectura y operaciones que mantengan o mejoren su saldo; impedir las que lo empeoren. No borrar ni modificar datos para que “pasen”.
7. Cambio de nombre: no debe fallar solo porque una cuenta histórica ya estaba negativa.
8. Comprobar operación y persistir dentro de la misma transacción; confirmar que el registro aún existe.
9. Mantener calcularSaldo como cálculo puro con Math.addExact. Puede conservar su test que obtiene un negativo; añadir pruebas de rechazo en los comandos/servicios que modifican datos.
10. Para este MVP se valida el saldo actual proyectado. No añadir un motor que reconstruya solvencia en cada instante histórico.

Mensaje propuesto: “Saldo insuficiente. Disponible: CLP 1.000. Necesitas: CLP 1.500.” No mostrar una excepción SQL al usuario.

### 5.3 CRUD acotado y seguro

| Registro | Editar | Eliminar | Límite deliberado |
|---|---|---|---|
| Cuenta | Nombre; corregir saldo inicial con validación | Solo sin movimientos ni vínculos de compra | Divisa asignada sigue fija; no añadir tipos de crédito |
| Contacto | Nombre | Solo si no lo referencia una compra/deuda | Mensaje concreto si está en uso; no borrar compras por cascada |
| Movimiento manual | Tipo, monto y descripción | Sí, tras validar saldo proyectado | Mantener cuenta, fecha y ubicación en este corte para limitar riesgo; no migrarlo entre cuentas |
| Compra sin reparto ni pago vinculado | Nombre, fecha, contacto y detalles; total siempre derivado | Detalles y cabecera en una transacción | Moneda fija; al menos un ítem válido |
| Compra con reparto | Cambios de metadatos que no alteran importes, si caben | Bloquear por ahora | No recalcular ni borrar deudas silenciosamente |
| Movimiento generado por compra, si se añade pago | Gestionar desde la compra | Gestionar desde la compra | Evita cargo huérfano o doble devolución |

Bloquear una operación protegida es una regla del MVP, no una implementación de CRUD irrestricto. Debe explicarse en UI y en la demostración.

Los repositorios actuales ya desempeñan la función DAO. Los nombres “Repository” no son un incumplimiento por sí mismos. Usar update/delete parametrizados por ID y verificar el número de filas afectadas. [S1]

### 5.4 Compra, pago y deshacer: segundo corte indivisible

Flujo propuesto:

1. Planificada: no altera la cuenta ni reserva dinero automáticamente.
2. Comprar: elegir cuenta con la misma divisa; mostrar total, disponible y saldo posterior; confirmar.
3. Dentro de una transacción: releer compra y cuenta, verificar estado/saldo, crear egreso, guardar vínculo único y cambiar estado.
4. Doble confirmación, reinicio o reintento: no crear un segundo cargo.
5. Deshacer: confirmar, retirar el egreso vinculado y volver a PLANIFICADA atómicamente; vuelve el importe a la cuenta original una sola vez.
6. Compra con reparto: bloquear deshacer/borrado por este plazo y explicar “La compra tiene un reparto registrado”. La resolución editable de deudas queda fuera de este corte.
7. REALIZADA histórica sin vínculo: conservarla; no cobrarla retroactivamente ni inventar una devolución. Identificarla como registro sin pago asociado.
8. Nueva compra REALIZADA: usar el mismo flujo de pago; no saltárselo por elegir el estado en un formulario.
9. Total cero: completar sin egreso, ya que Movimiento exige monto > 0; deshacer no devuelve dinero.
10. Si el usuario paga el gasto grupal, el cargo es el total de la compra. Las cuotas de contactos representan reembolsos pendientes; no reducir el cargo a la cuota “Tú”.

Modelo mínimo sugerido si se ejecuta este corte: tabla de vínculo con compra_id como PK/FK y movimiento_id UNIQUE/FK, con restricciones de borrado. Migración aditiva v5→v6, sin atribuir pagos a compras antiguas. La unión y las transacciones deben probarse tanto en instalación nueva como en migración.

No se necesita ledger bancario, backend, sincronización, tipos de cambio ni un historial de eventos empresarial. Una transacción Android con beginTransaction / setTransactionSuccessful / endTransaction es suficiente para este alcance. [S5] Mantener las FK y borrar hijos propios en orden explícito. [S11]

### 5.5 Interfaz mínima para usar las funciones

- Conservar Activities, XML y listas existentes.
- Acción visible “⋮” o botones en detalle: Editar, Eliminar y, cuando esté implementado el pago, Comprar/Deshacer.
- Pulsación larga puede ser un acceso adicional. El swipe izquierdo queda para después; no debe ser la única forma de operar. [S4]
- Confirmar acciones destructivas con nombre del registro y efecto en saldo.
- Error junto al campo o diálogo que permanece abierto; Toast breve para éxito. No mostrar simultáneamente varias alertas por la misma operación.
- Deshabilitar Guardar/Comprar mientras se procesa y revalidar en persistencia; la deshabilitación sola no garantiza ausencia de duplicados.
- Saldo y moneda primero; detalles secundarios después. Evitar IDs internos, mayúsculas técnicas y aclaraciones permanentes sobre el esquema.
- Fuente en sp, márgenes consistentes, controles de al menos 48dp, etiquetas asociadas. Validar con fuente al 200% donde esté disponible. [S3][S12]
- Estado de formulario ante rotación/interrupción: verificar lo que ya existe antes de añadir ViewModel o persistencia de borradores. [S13]
- No desactivar biometría o FLAG_SECURE para facilitar capturas.

## 6. Buenas prácticas: adoptar, posponer o descartar

| Decisión | Tratamiento | Razón |
|---|---|---|
| Java/XML/SQLiteOpenHelper/osmdroid/BiometricPrompt | Conservar | Stack académico existente y con evidencia |
| Dominio inmutable, long y Math.addExact | Conservar | Evita errores monetarios y de overflow |
| SQL parametrizado, cursores cerrados, transacciones y FK | Mantener y extender | La base ya los utiliza |
| Métodos pequeños para actualizar/borrar/confirmar | Adoptar en archivos tocados | Facilita pruebas y defensa del código |
| Mensajes y etiquetas en strings.xml | Aplicar a lo nuevo/modificado | Reutilización sin una limpieza masiva; recursos oficiales [S14] |
| ExecutorService + retorno al hilo principal | Aplicar gradualmente | Resuelve I/O sin nueva arquitectura; tratar lifecycle explícitamente [S2] |
| Room, Compose, Kotlin, MVVM, Hilt, RxJava | Fuera de alcance | La recomendación general de una guía no justifica una migración antes de mañana |
| RecyclerView solo para lograr swipe | Posponer | Reemplaza listas y obliga a revalidar más superficie |
| Más monedas, cambio online, informes nuevos | Posponer | No cierran los fallos urgentes |
| Pagos parciales y recordatorios de deuda | Posponer | Requieren reglas, esquema y pruebas adicionales |
| Cifrado propio, exportación propia, nuevo login | No introducir ahora | No resuelven CRUD ni saldo y amplían el riesgo |
| Reformatear todo el repositorio | Evitar | Corregir legibilidad solo donde se trabaja |

La guía de SQLite recomienda Room en términos generales; aquí prevalecen el stack aprobado, el nivel y el plazo. Sí se adoptan sus indicaciones aplicables a SQLiteOpenHelper y al trabajo fuera del hilo principal.

## 7. Roadmap realista hasta el 02/11

**Son propuestas de tareas, no tareas creadas ni código autorizado para fusionar.** Las estimaciones incluyen implementación asistida, pruebas y cierre indicados en cada fila, con el entorno preparado y el código ya conocido. La contingencia del 25% se calcula aparte; si la preparación de Android falla, los tiempos aumentan. No son una promesa de duración para un junior trabajando sin asistencia.

### Corte A — entrega prioritaria recomendada

| Tarea propuesta | Objetivo / alcance | Dependencias | Aceptación concreta | Esfuerzo efectivo |
|---|---|---|---|---:|
| PUL-00 | Comprobar baseline en equipo real; fijar reglas y ampliar filtros CI | Ninguna | Abre, autentica, conserva datos; SHA identificado; PR de Gradle activa CI | 0,75–1,25 h |
| PUL-01 | Protección de saldo en comandos, conservando lectura histórica | PUL-00 | Cero permitido; sobregiro rechazado sin escritura; cuenta negativa antigua legible y recuperable | 1,25–2 h |
| PUL-02 | Editar/eliminar movimientos manuales con saldo proyectado | PUL-01 | Sustitución y retirada exactas; borrar ingreso insuficiente rechazado; otra cuenta intacta | 1,75–2,75 h |
| PUL-03 | Editar/eliminar contactos y cuentas con referencias protegidas | PUL-01 | Cambios persisten; cuenta con movimientos y contacto utilizado no se borran | 1–1,5 h |
| PUL-04 | Editar/eliminar compras y detalles dentro del alcance protegido | PUL-03 | Total derivado, rollback completo, reparto existente intacto; sin cobro automático | 1,5–2,5 h |
| PUL-05 | Pulir UI de los flujos tocados y legibilidad del código | PUL-02/03/04 | Acciones visibles, errores humanos, etiquetas/teclado, doble toque protegido | 0,75–1,25 h |
| PUL-06 | Regresión final + APK + guía/versión/firmante de entrega | Incremento congelado | Recorrido completo real, modo avión, reinicio, CI exacta, datos conservados y docs coherentes | 2–3,25 h |

**Suma:** 9–14,5 horas efectivas. **Con reserva de aproximadamente 25%:** 11,25–18,25 horas. Esto puede no caber con trabajo, clases y descanso. No debe presentarse como “listo en unas pocas horas”.

Orden calendario recomendado:

- **01/11:** PUL-00 y, si existe disponibilidad, PUL-01. Detectar primero cualquier problema de apertura.
- **02/11, bloques de trabajo disponibles:** PUL-02, PUL-03, PUL-04 y PUL-05, en ese orden.
- **Últimas 2–3,25 horas efectivas antes de entregar:** PUL-06. Ese tiempo no se consume añadiendo funciones.

En el corte A **no se añade una transición de estado que parezca comprar sin efectuar el pago solicitado**. El pago vinculado queda pendiente de forma visible y documentada.

### Corte B — pago y reversión, condicionado a capacidad real

| Tarea propuesta | Alcance | Dependencias | Aceptación | Esfuerzo |
|---|---|---|---|---:|
| PUL-07A | Vínculo único y migración aditiva | PUL-00/01, CRUD estable | v5→v6 conserva filas; instalación nueva; FK y unicidad correctas | 1–1,5 h |
| PUL-07B | Comprar desde cuenta y crear REALIZADA con el mismo flujo | PUL-07A | Divisa igual, saldo suficiente, confirmación y exactamente un egreso; rollback inyectado | 2–3 h |
| PUL-07C | Deshacer/devolver y proteger edición/borrado de pagos | PUL-07B | Devolución única a cuenta original; bloquear reparto; cero e históricos sin cargo manejados | 1,5–2,5 h |
| PUL-07D | Regresión adicional de compra/pago/undo y migración Android | PUL-07C | Reintentos, reinicio y doble toque sin cargo/devolución duplicados | 1–1,5 h |

**Adicional:** 5,5–8,5 h. **A+B con reserva:** aproximadamente 18–29 h efectivas. No es una promesa realista para una sola persona con disponibilidad parcial antes del 02/11.

**Decisión de corte:** no empezar PUL-07A si no está reservado el bloque completo B y la validación final. Un esquema migrado y una compra “a medias” son peores para la entrega que una limitación explícita.

### Si quedan menos de 8 horas efectivas

Recortar a saldo + CRUD de movimientos/contactos/cuentas + acciones UI + comprobación final. No iniciar vínculo de compras, PAGADA, swipe, refactor de concurrencia general ni cartografía.

Si tampoco cabe ese conjunto, priorizar una demostración estable del subconjunto terminado y declarar las limitaciones. **No afirmar que el CRUD del producto completo quedó cerrado.**

### Después del 02/11

1. Completar B si se postergó.
2. Resolver edición/cancelación de reparto y cierre PAGADA.
3. Añadir swipe accesible solo si mejora el uso.
4. Optimizar consultas o ampliar mapas cuando exista necesidad observada.
5. Reconciliar el diagrama conceptual y la documentación con la implementación final.

## 8. Pruebas necesarias para aceptar las tareas

No escribir pruebas cosméticas de existencia de métodos o que simplemente reproduzcan cada línea de implementación. Probar resultados, aislamiento y rollback.

| Caso | Resultado esperado | Corte |
|---|---|---|
| Egreso igual al disponible | Saldo cero, una fila nueva | A |
| Egreso superior al disponible | Error; ninguna fila ni saldo cambian | A |
| Saldo inicial nuevo negativo | Rechazado | A |
| Cuenta antigua negativa | Se abre; ingreso mejora; egreso que empeora se rechaza | A |
| Editar egreso de 300 a 400 | Se aplica diferencia de 100, no cargo adicional de 400 | A |
| Eliminar ingreso necesario para cubrir gastos | Rechazado y registro intacto | A |
| Eliminar un egreso | Saldo aumenta exactamente su monto | A |
| ID inexistente / registro eliminado | Error controlado; no mensaje falso de éxito | A |
| Editar/borrar contacto referenciado | Renombrado permitido; borrado protegido | A |
| Eliminar cuenta con movimientos | Rechazado; no cascada financiera | A |
| Editar detalles de compra no repartida | Total exacto y persistente tras reinicio | A |
| Fallo a mitad de actualizar detalles | Compra original completa intacta | A |
| Compra con reparto | Operaciones monetarias bloqueadas sin destruir cuotas | A |
| CLP 2500; USD 25,50; precisión extra | Formato correcto; precisión extra rechazada; no float/double monetario | A |
| Repartos CLP/USD existentes | Conservan suma y asignación exactas | A |
| Rotación, Home→volver, huella cancelada | Estado razonable y ningún acceso financiero sin sesión | A |
| Teléfono en modo avión y reapertura | Consulta/persistencia, mapas dentro de cobertura y autenticación operativos | A |
| Fuente grande, teclado abierto, nombres largos | Acciones alcanzables y texto legible; sin certificar hasta verlo | A |
| Comprar dos veces / doble toque | Un solo egreso y vínculo | B |
| Cuenta de otra divisa / saldo insuficiente | No cambia compra, pago ni movimiento | B |
| Deshacer dos veces | Una devolución; segundo intento no altera saldo | B |
| REALIZADA antigua sin vínculo / compra de total cero | Sin cobro retroactivo ni devolución inventada | B |
| Fallo entre cargo y cambio de estado | Rollback integral | B |
| Migración de base poblada | IDs, importes, coordenadas y deudas intactos | B |
| APK actualizado con el mismo firmante | Datos conservados; no confundir con migración host | Cierre si aplica upgrade |

Reutilizar los runners del repositorio. Mantener smoke en API24 y API36 y pruebas Android del nuevo comportamiento. No prometer regresión completa API24 a partir del dispatcher actual. Si cambia la UI de cobro, actualizar las aserciones correspondientes: FlowChecks actualmente comprueba que las compras no crean movimientos.

## 9. Resultado de la segunda revisión

Esta segunda revisión no es una reauditoría de correcciones: **no se han hecho correcciones**. Es un contraste adicional de hallazgos contra código, casos de borde y fuentes oficiales.

| Afirmación revisada | Resultado después de contrastar |
|---|---|
| “Las divisas están pendientes” | Incorrecto como generalización. Catálogo, configuración inicial, formato y reparto ya existen. Conservarlos. |
| “Hay que redondear todos los valores” | No justificado. La precisión exacta vigente protege datos y ya pasa pruebas. |
| “La base de datos está mal” | No. Hay FK, restricciones y transacciones funcionales. Faltan comandos/reglas del incremento. |
| “No hay DAO porque se llaman Repository” | Incorrecto. La brecha son las operaciones ausentes. |
| “Todas las etiquetas carecen de asociación” | Descartado. Normalizar @id/@+id confirma que la mayoría sí la tiene; faltan especialmente los dos spinners. Cuotas tienen nombres asignados por Java. |
| “No existe CI para PR” | Descartado. s00-t06c cubre PR; el defecto concreto son filtros incompletos y cobertura de ramas/build. |
| “Saldo negativo es solo un riesgo hipotético” | Descartado. Se reprodujo en dominio y DDL SQLite y una prueba existente lo espera. |
| “PAGADA se arregla solo cambiando texto” | Descartado. El CHECK de la tabla lo impide. |
| “PR14 falló por falta de tokens” | No demostrado. Hay reporte de fallo de apertura y rollback; falta diagnóstico causal/logcat. |
| “Sin permiso INTERNET no hay backup cloud” | Incorrecto. El backup del sistema tiene una configuración independiente. |
| “Podemos certificar interfaz y teléfono con tests host” | No. Quedan pruebas reales explícitas. |

El enfoque de reducción de alcance se mantiene después de este contraste: no hay evidencia que justifique rehacer la arquitectura o las divisas.

## 10. Fuentes de investigación y aplicación

Documentación primaria consultada el 01/11/2026. Las reglas de saldo, borrado y compra propuestas son decisiones de este producto; no se presentan como obligaciones universales de Android.

| ID | Fuente oficial | Aplicación al plan |
|---|---|---|
| S1 | [Android: Save data using SQLite](https://developer.android.com/training/data-storage/sqlite) | CRUD parametrizado, filas afectadas y trabajo de BD fuera del hilo principal |
| S2 | [Android: Asynchronous work with Java threads](https://developer.android.com/develop/background-work/background-tasks/asynchronous/java-threads) | ExecutorService sencillo y retorno seguro al hilo de UI |
| S3 | [Android: Make apps more accessible — Views](https://developer.android.com/guide/topics/ui/accessibility/views/apps-views) | Objetivos táctiles de 48dp y controles comprensibles |
| S4 | [Android: Accessibility principles — Views](https://developer.android.com/guide/topics/ui/accessibility/views/principles-views) | Etiquetas y alternativa accesible a gestos |
| S5 | [Android: SQLiteDatabase](https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase) | Atomicidad y rollback de operaciones relacionadas |
| S6 | [Android: BigDecimal](https://developer.android.com/reference/java/math/BigDecimal) | Precisión y redondeo explícito cuando el producto lo defina |
| S7 | [Android: Sign your app](https://developer.android.com/studio/publish/app-signing) | Identidad de firma para actualizar APK y conservación del acceso a futuras actualizaciones |
| S8 | [Android: Auto Backup](https://developer.android.com/identity/data/autobackup) | Backup activado por defecto y política explícita para datos locales |
| S9 | [Android: Backup security recommendations](https://developer.android.com/privacy-and-security/risks/backup-best-practices) | Exclusiones por versión de Android y sensibilidad de los datos |
| S10 | [GitHub: Triggering a workflow](https://docs.github.com/en/actions/how-tos/write-workflows/choose-when-workflows-run/trigger-a-workflow) | Filtros de rutas y ramas que no omitan cambios del build |
| S11 | [SQLite: Foreign key support](https://www.sqlite.org/foreignkeys.html) | Integridad de referencias y semántica de RESTRICT |
| S12 | [Android 14: Nonlinear font scaling](https://developer.android.com/about/versions/14/features#non-linear-font-scaling) | Revisión visual con fuente hasta 200% |
| S13 | [Android: Save UI states](https://developer.android.com/topic/libraries/architecture/saving-states) | Recuperación de estado ante recreación |
| S14 | [Android: String resources](https://developer.android.com/guide/topics/resources/string-resource) | Etiquetas y mensajes reutilizables |

### Evidencia del repositorio

- [Snapshot auditado](https://github.com/aik1a/LocalCash/tree/9b8a651d66982eea3a41e1979342468fe4cad176).
- [Repositorios de datos](https://github.com/aik1a/LocalCash/tree/9b8a651d66982eea3a41e1979342468fe4cad176/app/src/main/java/com/aik1a/localcash/data).
- [MovimientoService](https://github.com/aik1a/LocalCash/blob/9b8a651d66982eea3a41e1979342468fe4cad176/app/src/main/java/com/aik1a/localcash/application/MovimientoService.java#L42).
- [Test que admite saldo negativo](https://github.com/aik1a/LocalCash/blob/9b8a651d66982eea3a41e1979342468fe4cad176/tests/DomainChecks.java#L20).
- [Esquema SQLite v5](https://github.com/aik1a/LocalCash/blob/9b8a651d66982eea3a41e1979342468fe4cad176/app/src/main/java/com/aik1a/localcash/data/LocalCashDbHelper.java).
- [Reglas vigentes](https://github.com/aik1a/LocalCash/blob/9b8a651d66982eea3a41e1979342468fe4cad176/docs/design/BUSINESS_RULES.md).
- [CI de divisas y regresión](https://github.com/aik1a/LocalCash/actions/runs/39178585250).
- [PR14 revertido: 45 archivos; no reutilizar aprobación como garantía de ejecución](https://github.com/aik1a/LocalCash/pull/14).
- [Workflow de PR existente](https://github.com/aik1a/LocalCash/blob/9b8a651d66982eea3a41e1979342468fe4cad176/.github/workflows/s00-t06c-build.yml).
- [Guía de entrega con referencias históricas](https://github.com/aik1a/LocalCash/blob/9b8a651d66982eea3a41e1979342468fe4cad176/docs/delivery/BUILD.md).

**Cierre del informe:** el siguiente paso es implementar una tarea acotada sobre la base restaurada y comprobarla antes de continuar. La aprobación de este plan no debe confundirse con aprobación de código, merge o APK que todavía no existen.

