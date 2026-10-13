# Auditoría S00-T04 — Contactos, compras y migración SQLite

**Fecha:** 2026-10-13 · **Rol:** Auditor (gate F4) · **Sprint:** S00 acelerado
**Task Brief:** `docs/tasks/active/S00-T04-contactos-compras.md`
**Base de implementación:** `main` `e1e42caa1a76af2c264cb316262c4ca5d092de7a`
**SHA funcional compilado:** `76480f7639a4ef2e2ec3121817307605ff9843d2`
**Head de handoff inspeccionado:** `7ae285f4fbe8682b0a03e92b4ada4668018faf3f`
**Handoff:** `docs/handoffs/S00-T04-coder.md`

## Fuentes y metodología

Se revisaron Task Brief, `PROJECT_ROUTER.md`, `PROJECT_STATE.md`, `AGENTS.md`, rol AUDITOR, `AUDIT_MATRIX.md`, `PROJECT_SPEC.md`, `BUSINESS_RULES.md`, `DOMAIN_MODEL.md`, `DATABASE.md`, diff y fuentes Java/XML/Gradle, pruebas de dominio/Instrumentation, handoff y evidencias. Se comparó el SHA compilado contra el head entregado: **únicamente un commit posterior documental**, sin modificar código ni build.

Se comprobó independientemente el **log original de GitHub Actions** run `38289787487`, job `114946828619`, y los archivos versionados de resultados. No se declara ejecución local propia de Android Studio/dispositivo.

## Criterios de aceptación

| ID | Criterio | Evidencia | Estado |
|---|---|---|---|
| AC-01 / LC-CON-01 | Crear/listar contactos persistentes | `Contacto`, `ContactoRepository`, `ContactosActivity`; Ana y Luis creados por UI, datos persistentes tras reinicio | CUMPLE |
| AC-02 / LC-BUY-01 | Compras con múltiples detalles, total exacto e historial | `Compra`, `DetalleCompra`, `CompraRepository`; 2 × 100,25 + 3 × 10,50 = 232,00; vista de detalle e historial | CUMPLE |
| AC-03 / LC-BUY-02 | Compra planificada diferenciada | Estados `PLANIFICADA/REALIZADA`, DatePicker y prueba con fecha futura | CUMPLE |
| AC-04 / LC-BUY-03 | Contacto opcional | Compra realizada vinculada a Ana, planificada sin contacto; FK SQLite | CUMPLE |
| AC-05 / LC-P02 | Persistencia, FK, atomicidad | `SQLiteOpenHelper` v2, FK activadas, transacción de cabecera/ítems, rollback inducido en segundo detalle | CUMPLE |
| AC-06 | **Migración aditiva v1→v2 preservando cuentas/movimientos** | `onUpgrade(1,2)` solo crea tablas nuevas, sin DROP; Instrumentation prepara fixture DDL v1 con 2 cuentas/2 movimientos, migra y verifica saldos 125,50 y 300,00, historiales previos | CUMPLE (migración de BD comprobada) |
| AC-07 | Regresión de S00-T03 | `LOCALCASH_FLOW_PASS` y `LOCALCASH_PURCHASE_PASS` en job original; saldo no se altera al registrar compras | CUMPLE |
| AC-08 | Build SHA exacto y pruebas | Checkout `76480f7...`; Gradle `:app:assembleDebug :app:assembleDebugAndroidTest`: `BUILD SUCCESSFUL in 1m 12s`, retorno **0**; dominio 18+15 = 33; reinicio `LOCALCASH_RESTART_PASS` | CUMPLE |
| AC-09 | Handoff/docs/alcance | Handoff, `DATABASE.md`, `DOMAIN_MODEL.md`, evidencia y APK; sin Room/Firebase/backend ni módulos futuros | CUMPLE |

## Matriz Q01–Q08

| ID | Peso | Estado | Evaluación |
|---|---:|---|---|
| Q01 Requisitos y aceptación | 25 | CUMPLE | AC-01 a AC-09 |
| Q02 Flujo funcional | 15 | CUMPLE | Contactos, compras, detalle, historial, persistencia |
| Q03 Arquitectura/POO | 15 | CUMPLE | Dominio privado/inmutable, total derivado, SQL encapsulado |
| Q04 Calidad | 10 | CUMPLE | Implementación académica acotada |
| Q05 Datos/seguridad | 10 | CUMPLE | FK, transacción, migración aditiva, checks SQL |
| Q06 Build/tests/regresión | 15 | CUMPLE | CI verificada, UI/emulador, 33 comprobaciones, restart |
| Q07 Alcance/dependencias | 5 | CUMPLE | Sin tecnologías ni funciones no autorizadas |
| Q08 Handoff/docs | 5 | CUMPLE | Evidencia suficiente con advertencia de firma |

**Puntaje diagnóstico:** 100/100 dentro del alcance auditado; no certifica un ciclo de actualización APK entre T03 y T04 ni el sprint completo.

## Evidencia original corroborada

- [GitHub Actions run 38289787487](https://github.com/aik1a/LocalCash/actions/runs/38289787487), job `114946828619`, conclusión SUCCESS.
- Checkout exacto `76480f7639a4ef2e2ec3121817307605ff9843d2`, `BUILD SUCCESSFUL in 1m 12s`; Gradle return **0**.
- Dominio: **18 cuentas/movimientos + 15 contactos/compras = 33 comprobaciones**.
- Emulador API 36: `LOCALCASH_FLOW_PASS`, `LOCALCASH_PURCHASE_PASS`, `LOCALCASH_RESTART_PASS`.
- Preservación v1: cuentas y movimientos con saldos 12550/30000 centésimas, conservados después de `onUpgrade`.
- Atomicidad: cabecera e ítems revertidos ante error inducido en segundo detalle.
- [Artefacto 11766425002](https://github.com/aik1a/LocalCash/actions/runs/38289787487/artifacts/11766425002), APK SHA256 `d15f90d9780fb9c073f3b5dcf0c1179c72db5e6ca832118080417c7a81cdf5fe`.

## Hallazgos y observaciones

| ID | Grado | Detalle / acción |
|---|---|---|
| OBS-01 | **RIESGO DE ENTREGA — no bloquea merge del código** | Certificados de firma **diferentes** para APK T03 y T04: `663ca35...` vs `9c8254...` reportados en evidencia. **El APK T04 no puede instalarse como actualización directa sobre el APK T03 distribuido sin perder/alterar instalación**. No desinstalar T03 si contiene datos. Para demostrar migración sobre instalación real, compilar futuras versiones con la **misma clave de firma** de T03 o preparar respaldo/instalación compatible. Esta observación debe resolverse antes de afirmar validación de actualización de APK con datos del usuario. El fixture v1→v2 de CI sí prueba correctamente la migración interna. |
| OBS-02 | MENOR | Sync visual Android Studio y prueba en dispositivo físico NO EJECUTADOS; prueba de emulador/CI sí realizada |
| OBS-03 | CONTINUIDAD | Migración de SQLite para tablas T05 deberá avanzar desde v2 sin borrar las tablas actuales; no inventar compra→movimiento (decisión pendiente) |
| OBS-04 | MENOR | Las compras tienen estado al crear, sin transición posterior; edición/borrado fuera de alcance de T04 |

**Sin BLOCKER ni MAJOR de código dentro de los criterios autorizados.**

## Gates y veredicto

| Gate | Resultado |
|---|---|
| Requisitos funcionales T04 | PASS |
| Build SHA/Android UI/regresión | PASS |
| Migración SQLite v1→v2 | PASS |
| Persistencia/integridad | PASS |
| Alcance/POO | PASS |
| Actualización de APK con firma incompatible | **NO VALIDADA — observación operacional de entrega** |
| Sprint completo | NO EVALUADO |

**VEREDICTO: PASS WITH OBSERVATIONS.**

**APPROVED_FOR_MERGE** de SHA funcional `76480f7639a4ef2e2ec3121817307605ff9843d2`, head documental `7ae285f4fbe8682b0a03e92b4ada4668018faf3f` y este informe de auditoría exclusivamente documental.

**NOT_APPROVED_FOR_SPRINT_ADVANCE**: faltan S00-T05, T06 y T07.

El Orquestador puede integrar y cerrar administrativamente **S00-T04**, manteniendo el riesgo de firmas en planificación de entrega T07. No se requieren cambios funcionales de T04.
