# LocalCash — Project State

**Estado del documento:** operativo  
**Responsable principal:** Agente Orquestador  
**Regla:** actualizar cuando cambie el estado real del proyecto. No usar este archivo para reemplazar requisitos, ADRs ni historial de auditorías.

## Estado actual

- **Repositorio:** `aik1a/LocalCash`, `main`. Fecha objetivo de entrega académica: **19 de octubre de 2026**.
- **Sprint:** S00 — entrega acelerada, **IN_PROGRESS**.
- **Tareas cerradas:** S00-T01, T02, T03, T04 y **T05 (gastos compartidos/deudas)**.
- **Funcionalidad aceptada:** cuentas, movimientos y saldos; contactos; compras planificadas/realizadas con ítems; reparto equitativo/personalizado exacto; deudas por contacto. Persistencia SQLite **v3**, Java/XML offline.
- **Gate T05:** `docs/audits/S00/S00-T05-audit.md` — **PASS WITH OBSERVATIONS**, `APPROVED_FOR_MERGE`, sin BLOCKER/MAJOR; H-01 documental pendiente para T07.
- **Último merge funcional:** [PR #5](https://github.com/aik1a/LocalCash/pull/5), `5c9e9c86c9913f6079b684a150c86d783d48c189`.
- **Validación T05:** [Actions run 38440658476](https://github.com/aik1a/LocalCash/actions/runs/38440658476), SHA funcional `45c70f7efa2bb2ffcdc2b7cb7af78c16ca1c1c50`: Gradle exit 0, 5.460 comprobaciones de dominio PASS, migración v2→v3, pruebas de UI/regresión y relanzamiento en emulador PASS.
- **Trabajo siguiente:** [plan T06](docs/tasks/active/S00-T06-plan.md); primer incremento [T06A ubicación](docs/tasks/active/S00-T06A-ubicacion.md), READY.

## Próxima acción autorizada

**Codificador — S00-T06A: ubicación opcional de movimientos**, Task Brief `docs/tasks/active/S00-T06A-ubicacion.md`. Basarse en `main` aceptado tras T05 y usar rama `task/S00-T06A-ubicacion`; avanzar con SQLite v3→v4 aditiva, registro de movimiento opcionalmente geolocalizado y pruebas de regresión. No editar otros incrementos, no merge ni cierre. Build/CI/emulador del SHA nuevo, handoff y una auditoría independiente antes del merge.

**T06B (mapas)** y **T06C (biometría)** están planificadas en briefs separados; T06B conserva una decisión de cartografía offline a documentar antes de autorizar implementación. T06C puede adelantarse si mapas está bloqueado. El sprint S00 **no está cerrado**; S00-T07 sigue pendiente.

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

- **T06A:** representar coordenadas como par opcional válido; migración SQLite v3→v4 aditiva. No cambiar movimientos previos ni crear geolocalización obligatoria.
- **T06B:** elegir soporte, cobertura, licencia/atribución y empaquetado de tiles de mapa **disponibles sin conexión en primera apertura**. No asumir que caché en línea cumple el PDF. Documentar decisión antes de pasar T06B a READY.
- **T06C:** integración `BiometricPrompt` compatible con minSdk24 actual, protección de todas las Activities y validación en emulador. No introducir bypass para facilitar pruebas.
- **T07:** compatibilidad de certificados de firma de APK para actualización preservando datos, cierre documental H-01 de T05, build/validaciones integrales y entrega.
- Otras decisiones no necesarias para T06 siguen sin resolverse (tipo de cuenta, política compra→movimiento, campos extra de contacto, pagos parciales excluidos).

## Bloqueadores y riesgos

- **Sin bloqueo para comenzar T06A**. Para T06B está **pendiente decisión de tiles offline** y prueba reproducible desde instalación limpia sin red, no un fallo del Codificador.
- El APK de T03 y el de T04 tenían firmas distintas; **no desinstalar instalaciones con datos** para probar actualización. La solución/backup pertenece a T07. La migración SQLite interna de T05 sí se validó en CI.
- T06A debe demostrar que permisos de ubicación denegados o GPS ausente no impiden crear un movimiento sin coordenadas; ningún tracking en segundo plano.
- Dispositivo físico y sync Android Studio local todavía sin evidencia; las pruebas CI/emulador anteriores no equivalen a prueba en dispositivo físico.
- Gradle muestra advertencias heredadas. No bloquear cambios funcionales por warnings sin impacto probado.

## Última auditoría

`docs/audits/S00/S00-T05-audit.md`: PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE, 97,5/100; [PR #5](https://github.com/aik1a/LocalCash/pull/5) fusionado. H-01 DOCUMENTATION para T07. Sprint S00 sigue en curso.

## Seguimiento pendiente

- S00-T06A (READY): ubicación opcional, persistencia SQLite v4, permisos y pruebas.
- S00-T06B (DRAFT): osmdroid 6.1.18, marcadores y cartografía offline reproducible; decidir tiles/área de demo antes de codificar.
- S00-T06C (DRAFT): gate biométrico seguro, compatibilidad SDK, regresión de UI; potencialmente adelantar si T06B bloqueado.
- S00-T07: firma APK/datos, prueba final integral, documentación, presentación y cierre del sprint.
- Una auditoría independiente por incremento verificable y cierre administrativo posterior por el Orquestador; no reabrir T01–T05 sin regresión concreta.

## Reglas de actualización

El Orquestador debe actualizar este archivo cuando:
- cambia el sprint activo;
- se valida o invalida un build;
- aparece o se resuelve un bloqueador;
- cambia la próxima acción autorizada;
- una decisión pendiente pasa a aceptada mediante documentación correspondiente.

No registrar aquí detalles extensos de implementación; usar handoffs, auditorías, ADRs y documentos del sprint.