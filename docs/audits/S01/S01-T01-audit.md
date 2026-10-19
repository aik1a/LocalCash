# Auditoría — S01-T01 Divisas

## Identificación

- **Tipo:** auditoría independiente de tarea; no es gate integral de sprint.
- **Auditor:** agente Auditor independiente, separado del Codificador de esta iteración.
- **Fecha:** 2026-10-19.
- **Repositorio/PR:** aik1a/LocalCash, [PR #13](https://github.com/aik1a/LocalCash/pull/13).
- **Rama:** task/S01-T01-divisas.
- **Baseline:** `171aacfb15f8a4974ce228e33fef08e841f6bc46`.
- **HEAD documental auditado:** `97344918d706718477d771aec9e843ead0b5bd59`.
- **SHA técnico con CI:** `6b7813cb81a8742c1b6fd12f8259ec637852487f`.
- **Task Brief:** docs/tasks/active/S01-T01-divisas.md.
- **Handoff:** docs/handoffs/S01-T01-coder.md.

El informe no modifica implementación, pruebas, requisitos, PROJECT_STATE ni auditorías históricas; no ejecuta merge ni cierra la tarea.

## Fuentes consultadas

Leídos directamente de GitHub al HEAD auditado: PROJECT_ROUTER, AGENTS, PROJECT_STATE,
rol AUDITOR, WORKFLOW, CONTEXT_PACKS, AUDIT_MATRIX y AUDIT_TEMPLATE; Task Brief,
handoff, ExecPlan y testing S01-T01; ARCHITECTURE, PROJECT_SPEC, TRACEABILITY,
DOMAIN_MODEL, DATABASE, BUSINESS_RULES, ADR-001, TEST_PLAN y roadmap S01.

Se obtuvo el diff remoto completo del PR (49 archivos) y la comparación baseline→HEAD.
Se inspeccionaron dominio, servicios, repositorios, migración, Activities/XML y pruebas
afectadas. Los 41 archivos no documentales modificados del snapshot local coinciden
por SHA de blob Git con GitHub al HEAD auditado. Los hashes de commits reconstruidos
localmente no se usan como identidad remota. La comparación técnico→HEAD confirma
únicamente cuatro documentos modificados: ExecPlan, handoff, trazabilidad y testing;
por tanto, el código probado en CI coincide con el código auditado.

El Task Brief registra esta ampliación expresamente autorizada después de S00.
El roadmap S01 anterior no se interpreta como autorización para cerrar todo ese sprint.

## Criterios de aceptación del Task Brief

| ID | Criterio | Evidencia contrastada | Estado |
|---|---|---|---|
| AC-01 | Configuración inicial después de autenticación, persistente | ActividadProtegida comprueba sesión antes de solicitar configuración y crear contenido; ConfiguracionRepository usa SQLite/fila única; CurrencyChecks y currency-ui con force-stop | CUMPLE |
| AC-02 | Diez divisas, selección por cuenta/compra y código visible | Divisa contiene CLP/USD/EUR/ARS/MXN/COP/PEN/BRL/GBP/JPY; Dinero valida y formatea; selectores XML; cuenta CLP y cuenta USD independientes en UI ambas APIs | CUMPLE |
| AC-03 | Precisión CLP/JPY entera y restantes dos decimales, sin redondeo | Parseo exacto BigDecimal/long; unidad mínima y validación de entidades; triggers; 28.917 checks específicos, incluidos bordes/entradas inválidas | CUMPLE |
| AC-04 | Reparto CLP/JPY exacto y resto estable | Reparto divide por unidad mínima; Tú primero, luego IDs ordenados; 100 CLP entre tres produce 34/33/33; conservación comprobada para diez divisas y distintos participantes | CUMPLE |
| AC-05 | Migraciones v1–v4→v5 aditivas; conservar IDs/importes/FK/ubicación | LocalCashDbHelper conserva tablas/columnas previas; snapshot antes/después en SQLite host y Android, verificación FK y coordenadas v4 | CUMPLE |
| AC-06 | Registros anteriores requieren asignación explícita; rechazar fracciones incluso deudas | CuentaRepository y CompraRepository validan importes relacionados dentro de transacción; triggers complementarios; CurrencyChecks conserva fracciones/deudas y rechaza CLP sin modificar datos | CUMPLE |
| AC-07 | Moneda asignada fija y nuevos movimientos sin moneda rechazados | Triggers de divisa fija, asignación condicionada a NULL; MovimientoService valida moneda/precisión; DeudaRepository exige moneda de compra; pruebas de rechazo | CUMPLE |
| AC-08 | Tú + al menos un contacto, cuota propia persistida sin deuda ficticia | Reparto ConUsuario, reparto_usuario separado, guardado con deudas en una transacción; rollback inyectado al insertar cuota propia; UI Tú/Valentina 50/50 y reapertura | CUMPLE |
| AC-09 | Personalizado suma Tú + contactos; reparto previo conservado | Dominio y repository revalidan suma contra compra persistida; rechazo de segundo reparto; migración no agrega cuota propia; deudas históricas conservadas | CUMPLE |
| AC-10 | Nombres sin IDs y etiquetas simples con ejemplos | NuevaCompraActivity/RepartoActivity/MapaActivity y XML; smoke valida Valentina sin #id, Cantidad/Precio unitario y ausencia de aclaraciones técnicas retiradas | CUMPLE |
| AC-11 | Dominio, SQLite/migración y build Android; límites declarados | Ejecución independiente local y logs CI exactos detallados abajo | CUMPLE |
| AC-12 | Handoff/PR sin alterar cierre S00 | PR #13 y documentos de tarea disponibles; diff no modifica cierres ni auditorías S00 | CUMPLE |

Los constructores y operaciones históricas se conservan para compatibilidad. Los nuevos
formularios usan moneda explícita y reparto ConUsuario. No se interpreta una compra
como movimiento automático ni se suman divisas distintas.

## Matriz de calidad

Aplicada la matriz base Q01–Q08 y sus subcriterios, con el foco S01 de entidades,
repositories, SQLiteOpenHelper, integridad y separación de UI. Se usa valoración por
dimensión; Q08 es PARCIAL por H-01/H-02.

| ID | Dimensión | Peso | Estado | Evidencia / observación |
|---|---|---:|---|---|
| Q01 | Requisitos y aceptación | 25 | CUMPLE | AC-01–12, requisitos vinculados en trazabilidad y autorización expresa del incremento |
| Q02 | Completitud funcional | 15 | CUMPLE | Onboarding→cuentas/compras→reparto→reinicio; errores de precisión/suma, datos previos y segundo reparto considerados |
| Q03 | Arquitectura y POO | 15 | CUMPLE | Estado privado/final e invariantes; dominio sin Android/SQLite; SQL en repositories/helper, cálculo en dominio, coordinación transaccional |
| Q04 | Calidad/mantenibilidad | 10 | CUMPLE | Enum acotado y selector compartido; sin capa o dependencia nueva; código proporcionado al proyecto académico |
| Q05 | Persistencia/datos/seguridad | 10 | CUMPLE | Migración aditiva, FK/triggers, transacciones y rollback; biometría/FLAG_SECURE conservados y comprobados |
| Q06 | Build/tests/regresión | 15 | CUMPLE | Build y checks exactos en CI ambas APIs; local independiente PASS; cobertura parcial API24 explícita, sin presentarla como completa |
| Q07 | Alcance/dependencias | 5 | CUMPLE | Diff corresponde a divisas/Tú/UX autorizados; sin Room/Compose/backend/DI ni nuevas dependencias principales |
| Q08 | Docs/trazabilidad/handoff | 5 | PARCIAL | Diseño v5 y evidencia suficientes; encabezado DATABASE/roadmap S01 requieren aclaración administrativa no bloqueante |

**Puntaje diagnóstico:** 97,5 / 100. El puntaje no compensa gates críticos.

## Evidencia de validación

### Ejecución independiente del Auditor

| Verificación | Resultado |
|---|---|
| `PATH=/tmp/localcash-auditor-bin:$PATH bash tests/domain-checks.sh` | PASS: 18 + 15 + 5427 + 23 + 28.917 = **34.400** comprobaciones |
| `python tests/sqlite-currency-checks.py` | PASS: **55** comprobaciones, DDL Java real emitido y ejecutado sobre SQLite host |
| Parseo de layouts con ElementTree y scripts tests Python con ast.parse | PASS |

El host dispone del módulo jdk.compiler pero no de ejecutable javac en PATH. El
primer intento de dominio terminó con «javac: command not found»; se resolvió con un
wrapper temporal fuera del repositorio que invoca
`java -m jdk.compiler/com.sun.tools.javac.Main`. La ejecución posterior completa pasó.
No se modificaron archivos de implementación o tests para obtener ese resultado.

El ensayo SQLite host no sustituye Android. No se ejecutó emulador local: este host
carece de KVM. El build Android se valida mediante evidencia CI remota, no se declara
recompilado por el Auditor en este host.

### CI contrastada directamente

[Run 39178585250](https://github.com/aik1a/LocalCash/actions/runs/39178585250).
Se consultaron jobs y logs completos, comprobando checkout de
`6b7813cb81a8742c1b6fd12f8259ec637852487f`, resultados y marcadores de cada suite.

| Evidencia | API24, job 117826521050 | API36, job 117826521234 |
|---|---|---|
| assembleDebug + assembleDebugAndroidTest | BUILD SUCCESSFUL, 1m33s | BUILD SUCCESSFUL, 1m29s |
| Dominio / SQLite host | 34.400 / 55 PASS | 34.400 / 55 PASS |
| SQLite/repositorios Android | LOCALCASH_CURRENCY_PASS; 76 checks | LOCALCASH_CURRENCY_PASS; 76 checks |
| Biometría, rutas directas y lifecycle | LOCALCASH_BIOMETRIC_PASS | LOCALCASH_BIOMETRIC_PASS |
| Mapas offline/reapertura | LOCALCASH_MAPS_PASS / MAPS_RELAUNCH_PASS | LOCALCASH_MAPS_PASS / MAPS_RELAUNCH_PASS |
| Primer acceso, divisas, Tú + Valentina y reinicio | LOCALCASH_CURRENCY_UI_PASS | LOCALCASH_CURRENCY_UI_PASS |
| Regresión financiera/compras/reparto/GPS y reaperturas | No ejecutada completa: dispatcher declara cobertura limitada API24 | FLOW/PURCHASE/RESTART/SHARED/SHARED_RESTART/LOCATION/LOCATION_RESTART y BIOMETRIC_REGRESSION PASS |
| Resultado job / subida artefactos | SUCCESS / artefacto 12155761036 | SUCCESS / artefacto 12154864015 |

Los logs API36 conservan algún texto descriptivo histórico de reparto «100,01/3» y
«validación de 2+». Las aserciones actuales de FlowChecks sí verifican Tú 25,01 +
tres contactos de 25,00 y el mínimo de un contacto; para resultados se han revisado
esas aserciones, no solo el texto descriptivo.

### Validación manual y alcance de la evidencia

El recorrido automatizado usa sensor de emulador/UIAutomator y reinicio de proceso.
Recorrido humano, dispositivo físico y Android Studio Sync limpio: **NOT TESTED**.
FLAG_SECURE sigue vigente; no se exige deshabilitarlo para producir capturas.
La migración lógica SQLite probada no acredita actualización in-place desde APK con
firmante histórico diferente: **BLOCKED_SIGNER_MISMATCH** histórico; conservación
tras ese upgrade: **NOT TESTED**. No se instaló nada en el equipo del usuario.

## Hallazgos

**0 BLOCKER, 0 MAJOR.** Dos observaciones DOCUMENTATION no bloqueantes, sin defecto
de ejecución demostrado.

| ID | Severidad | Criterio | Evidencia precisa al HEAD auditado | Acción recomendada |
|---|---|---|---|---|
| H-01 | DOCUMENTATION | Q08.2 | docs/design/DATABASE.md, línea 3, aún declara «esquema v4»; sección S01-T01 de líneas 137–177 y helper real describen/implementan v5 | Al integrar, actualizar encabezado o explicitar que v4 corresponde al estado integrado anterior y v5 al incremento revisado; conservar historia |
| H-02 | DOCUMENTATION | Q08.5 | docs/sprints/S01-persistence-domain.md, líneas 5 y 9–10, mantiene roadmap de esquema v1 y otro significado de S01-T01; Task Brief activo identifica inequívocamente divisas posteriores a S00 | En cierre administrativo, aclarar relación entre roadmap histórico e incremento actual; no cerrar todo S01 a partir de esta auditoría |

No se requieren correcciones de runtime/tests antes del merge. Estas aclaraciones
documentales pueden coordinarse en el cierre del Orquestador sin alterar el veredicto
técnico ni reescribir evidencia histórica.

## Gates críticos

| Gate | Resultado | Justificación |
|---|---|---|
| Requisitos obligatorios | PASS | Stack/pilares respetados; ampliación registrada, sin contradicción funcional identificada |
| Criterios esenciales | PASS | AC-01–12 contrastados con código y pruebas suficientes para la tarea |
| Build requerido | PASS | App y AndroidTest compilados sobre SHA técnico exacto en ambas APIs |
| Tests requeridos | PASS | Dominio, SQLite host/Android, UI y regresión relacionada; límites API24 declarados |
| Arquitectura/decisiones canónicas | PASS | Java/XML/SQLiteOpenHelper, dominio/servicios/repositories; ADR-001 |
| Integridad/seguridad | PASS | No conversión silenciosa; migración aditiva y rollback; guardia biométrica/FLAG_SECURE |
| Alcance/dependencias | PASS | Sin cambios funcionales no autorizados ni nuevas dependencias principales |

## Veredicto

**PASS WITH OBSERVATIONS**

- **Commit:** APPROVED_FOR_COMMIT.
- **Merge:** APPROVED_FOR_MERGE.
- **Avance de sprint:** N/A — esta auditoría no habilita ni certifica el cierre integral de S01.

La aprobación corresponde al diff baseline→HEAD indicado y al mismo código técnico
probado en CI. Un cambio funcional posterior requiere revisar su alcance y evidencia
antes de reutilizar este gate. El commit que incorpora únicamente este informe no
altera la implementación auditada.

## Riesgos residuales y límites de integración

API24 no acredita toda la regresión financiera/GPS; ambas APIs sí acreditan los checks
específicos de este incremento. Las pruebas físicas/humanas/Studio y upgrade con
firmantes históricos siguen pendientes. Esos límites están documentados y no impiden
integrar este cambio funcional conforme al Task Brief, que permite declarar límites
de entorno; impiden presentarlo como validación exhaustiva o actualización segura
de cualquier instalación histórica. No desinstalar instalaciones con datos.

El Orquestador conserva la decisión de merge y el cierre administrativo, junto con
la actualización de estado/trazabilidad. Esta auditoría autoriza técnicamente la
integración de S01-T01, sin ejecutarla.
