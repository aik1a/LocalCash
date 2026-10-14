# Handoff — S00-T05

**Rol saliente:** Codificador · **Fecha:** 2026-10-13 · **Sprint:** S00  
**Rama:** task/S00-T05-gastos-compartidos  
**Base main:** df74f118a50aa044ef8ff3e17a83070de5a9f7e3  
**SHA funcional compilado y probado:** 45c70f7efa2bb2ffcdc2b7cb7af78c16ca1c1c50

## Objetivo ejecutado y estado
**DONE (implementación y validación del Codificador; auditoría pendiente).** Repartir el total de una compra REALIZADA entre dos o más contactos y consultar sus deudas persistidas. No se emite gate ni se cierra la tarea/sprint.

## Cambios realizados
- Reparto equitativo en centésimas long: IDs ascendentes, resto distribuido de a una centésima. Personalizado con montos >=0 y suma exacta; overflow rechazado.
- Una sola tabla deudas con PK compra/contacto, FK y restricciones. Migración v2→v3 aditiva y secuencial v1→v3.
- DeudaRepository registra todas las asignaciones atómicamente, revalida total/estado de compra persistida y rechaza cualquier reparto existente.
- Desde Compras → detalle REALIZADA → Repartir / consultar deudas: selección explícita, modo y montos; errores visibles. Tras guardar se consultan las deudas y se oculta el formulario.
- Contacto vinculado a compra no seleccionado automáticamente; cuentas/movimientos no se alteran. Sin pagos reales/parciales, backend, biometría, mapas ni dependencias nuevas.

## Archivos modificados
- Dominio: Reparto.java, Deuda.java.
- Persistencia/aplicación: LocalCashDbHelper.java, DeudaRepository.java, DeudaService.java.
- UI: RepartoActivity.java, CompraDetalleActivity.java, AndroidManifest.xml; activity_reparto.xml, row_participante.xml, activity_compra_detalle.xml.
- Validación: FlowChecks.java, SharedDomainChecks.java, domain-checks.sh, shared-relaunch-checks.py; workflow s00-t05-build.yml.
- Documentación: DATABASE.md, DOMAIN_MODEL.md, docs/testing/S00-T05.md y ExecPlan S00-T05.

## Validación ejecutada

[Actions run 38440658476](https://github.com/aik1a/LocalCash/actions/runs/38440658476), job 115446054645: **SUCCESS**, checkout exacto del SHA funcional.

| Verificación | Comando/escenario | Resultado |
|---|---|---|
| Build Android | bash gradlew --no-daemon --console=plain :app:assembleDebug :app:assembleDebugAndroidTest | PASS, retorno 0, BUILD SUCCESSFUL in 50s |
| Dominio anterior | bash tests/domain-checks.sh | PASS: 18 cuentas/dinero + 15 contactos/compras |
| Dominio reparto | SharedDomainChecks, mismo script | PASS: 5427 comprobaciones; ejemplos, máximo long, cero, errores y barrido de conservación |
| Regresión UI/SQLite | Instrumentation FlowChecks y mode purchases, API36 x86_64 | PASS cuentas, movimientos, contactos, compras y v1→v3 |
| Migración v2→v3 | mode shared; esquema v2 real, sin tabla deudas | PASS: snapshot exacto de cada fila de las cinco tablas anteriores |
| Integridad y atomicidad | FK compra/contacto, PK duplicada, CHECK negativo, fallo inducido en segundo insert | PASS; sin escritura parcial |
| Invariantes persistidas | Planificada, repetidos, inexistentes, suma/negativo/overflow, total ficticio y reparto repetido | PASS, rechazos sin deuda parcial; cuota cero válida |
| Flujo reparto | Formularios XML, 100,01/3; personalizado 20+30+50=100 | PASS; errores visibles y montos por ID |
| Persistencia real | force-stop + am start + shared-relaunch-checks.py | PASS ambos repartos y saldo 125,50; capturas/XML |

**APK:** app-debug.apk, 87011 bytes.  
**SHA256 APK:** da65e2f98e18c28dcbf670a2262df223ba30f304a377876ca8c938c289c87238  
**Artefacto CI:** ID 11836165654, S00-T05-45c70f7efa2bb2ffcdc2b7cb7af78c16ca1c1c50. ZIP original SHA256 2a6706391617e6763fc09ece1a465968781c9b40e5c12454ffc2d33a93bc8e18. Expira en CI el 2027-01-11; se conserva además paquete de entrega LocalCash-S00-T05-APK-evidencia.zip.

Logs completos de build, dominio y flujos, XML, capturas y APK están en el artefacto. Evidencia textual y verificación en docs/handoffs/evidence/S00-T05/. Capturas de ambos repartos revisadas visualmente: total, tres contactos/importes y PENDIENTE visibles sin recortes.

## Documentación y decisiones
DATABASE y DOMAIN_MODEL documentan expresamente la simplificación participante/asignación/deuda en una sola tabla. No total redundante. Estado PENDIENTE; PAGADA es opcional y no se implementó en T05. No nuevas decisiones funcionales respecto del Task Brief.

El commit posterior de entrega solo registra documentación/evidencia; no cambia el código compilado. PROJECT_STATE, Task Brief, sprint y auditorías anteriores no fueron modificados.

## Hallazgos, limitaciones y pendientes
- javac no disponible localmente; build/pruebas realizados realmente en GitHub Actions. No se confunde la comprobación local fallida con validación Android.
- Gradle conserva advertencias deprecadas para Gradle 10 y Java/Kotlin del entorno; build actual PASS. El arranque del emulador tuvo sondeos adb fallidos transitorios; luego arrancó y todos los flujos pasaron.
- No se validó dispositivo físico ni actualización APK T04→T05 con la misma firma. Advertencia de certificados previos sigue vigente para T07; no desinstalar instalaciones que contengan datos.
- Los fixtures de prueba solo deben ejecutarse en un emulador desechable; eliminan/preparan la BD de pruebas.
- La auditoría independiente y el gate del Orquestador siguen pendientes. Sin merge ni cierre.

## Contexto mínimo y próxima acción
Leer Task Brief S00-T05, BUSINESS_RULES, DATABASE, DOMAIN_MODEL y docs/testing/S00-T05.md. Auditar una sola vez el diff main base → SHA funcional, con el run y evidencia arriba. El Codificador solicita esa auditoría independiente; no emite su propio veredicto.
