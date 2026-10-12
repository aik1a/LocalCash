# Auditoría S00-T03 — Cuentas y movimientos SQLite

**Fecha:** 2026-10-12 · **Rol:** Auditor · **Tipo:** auditoría única de incremento funcional
**Tarea:** `docs/tasks/active/S00-T03-cuentas-movimientos.md`
**Base:** `main` `905ac2fdafcf82ac0d21183e1e9fa893dd2d7d2a`
**Commit compilado y probado:** `eea0fbfc895f5d5e2cde6f3512407f3cc9c16cec`
**Handoff de implementación:** `docs/handoffs/S00-T03-coder.md`
**Head documental recibido:** `578e7c190e470e840601eaf0750270b3ac5fe699`

## Fuentes, alcance y verificación

`PROJECT_ROUTER.md`, `AGENTS.md`, `PROJECT_STATE.md`, `ARCHITECTURE.md`, `BUSINESS_RULES.md`, `DATABASE.md`, `DOMAIN_MODEL.md`, `AUDIT_MATRIX.md`, Task Brief, código Java/XML/Gradle, pruebas `DomainChecks` y `FlowChecks`, GitHub diff, handoff y **logs originales de GitHub Actions**.

El Auditor inspeccionó directamente entidades, repositories, helper SQLite, transacción de registro, pantallas/formularios, manifiesto y pruebas. También verificó GitHub Actions run `38283139884`, job `114925004298`, incluido el checkout del SHA exacto, build, pruebas y emulador; no se atribuyen como pruebas propias.

## Criterios de aceptación

| ID | Criterio | Evidencia independiente | Estado |
|---|---|---|---|
| AC-01 | Crear/listar cuentas múltiples | `CuentaRepository`, MainActivity y prueba UI de Efectivo/Banco | CUMPLE |
| AC-02 | Ingreso, egreso y saldo derivado | `Movimiento`, `Cuenta.calcularSaldo` con `Math.addExact`, `MovimientoService`; saldo 100 + 50 − 24,50 = 125,50 | CUMPLE |
| AC-03 | Historial aislado por cuenta | `MovimientoRepository.listar(cuentaId)`; UI; Banco permanece con saldo 300 y sin movimientos | CUMPLE |
| AC-04 | Persistencia local offline | `SQLiteOpenHelper`, FK activa, `cuentas`/`movimientos`; sin permiso INTERNET ni servidor; reapertura BD y proceso | CUMPLE |
| AC-05 | Java/XML, separación razonable sin sobreingeniería | Dominio sin Android; repositorios contienen SQL; service coordina saldo/registro; Activities solo orquestan formularios | CUMPLE |
| AC-06 | Validar entradas | Nombre vacío, monto cero/negativo y formato inválido rechazados; transacción cancela overflow; prueba de FK | CUMPLE |
| AC-07 | Build del SHA exacto / evidencia | Run `38283139884`, `:app:assembleDebug :app:assembleDebugAndroidTest`, `BUILD SUCCESSFUL in 46s`, código 0, APK artifact `11762057450`; 18 comprobaciones de dominio y `LOCALCASH_FLOW_PASS` | CUMPLE |
| AC-08 | Documentación y handoff | `DATABASE.md`, `DOMAIN_MODEL.md`, `docs/testing/S00-T03.md`, handoff y evidencias, UTF-8 legible | CUMPLE |

**Integridad SHA:** GitHub compare del commit probado a HEAD de entrega muestra exclusivamente documentación/evidencia; ningún cambio posterior de código/Gradle. No se reusa build de S00-T02.

## Matriz Q01–Q08

| Criterio | Peso | Resultado | Fundamento |
|---|---:|---|---|
| Q01 Requisitos | 25 | CUMPLE | AC-01 a AC-08 |
| Q02 Funcionalidad | 15 | CUMPLE | Recorrido cuentas → movimiento → saldo → reinicio |
| Q03 Arquitectura/POO | 15 | CUMPLE | Invariantes, saldo derivado, SQLite fuera de UI |
| Q04 Calidad | 10 | CUMPLE | Implementación acotada y legible |
| Q05 Datos/integridad | 10 | CUMPLE | FK, checks SQL, transacción/overflow y persistencia |
| Q06 Pruebas/regresión | 15 | CUMPLE | CI y emulador comprobados |
| Q07 Alcance | 5 | CUMPLE | Sin Firebase/Room/backend, mapas o módulos futuros |
| Q08 Handoff/docs | 5 | CUMPLE | Evidencia suficiente y documentación coherente |

**Diagnóstico:** 100/100 para este incremento; no equivale a certificación integral del proyecto.

## Evidencia de validación

- **CI verificado:** https://github.com/aik1a/LocalCash/actions/runs/38283139884
- **Job:** `114925004298`, completado con conclusión `success`.
- **Checkout:** `eea0fbfc895f5d5e2cde6f3512407f3cc9c16cec`, corroborado en log.
- **Build:** `bash gradlew --no-daemon --console=plain :app:assembleDebug :app:assembleDebugAndroidTest`; `BUILD SUCCESSFUL in 46s`, salida 0.
- **Dominio:** `PASS: 18 comprobaciones...` en job original.
- **Emulador:** Instrumentation registra `LOCALCASH_FLOW_PASS`, incluyendo formularios, saldo, dos cuentas, rechazo de montos inválidos, FK, overflow, reapertura de BD y Activities.
- **Reinicio real:** `adb shell am force-stop`, `am start -W` retorna `Status: ok`; UI contiene `Saldo: 125,50`.
- **Artefacto:** https://github.com/aik1a/LocalCash/actions/runs/38283139884/artifacts/11762057450 (APK y evidencias), SHA-256 APK `840113ae00deaa046b2d02a62d8f021c1f6c468f26ff105d5da0c09c56dae603`.

## Observaciones y riesgos residuales

| ID | Severidad | Observación |
|---|---|---|
| OBS-01 | MINOR | Uso físico y sincronización gráfica de Android Studio NO EJECUTADO; el emulador automatizado sí validó lanzamiento/persistencia del cambio |
| OBS-02 | MINOR / continuidad | Esquema SQLite v1; `onUpgrade` lanza excepción. Al añadir nuevas tablas en T04/T05 se debe implementar una **migración aditiva sin destruir cuentas ni movimientos**, versionando la BD y comprobando persistencia existente |
| OBS-03 | MINOR | No se incluyen editar/borrar cuentas o movimientos; no están en el alcance del primer flujo |

**No hay BLOCKER, MAJOR ni violación de alcance observables** en el incremento.

## Gates

| Gate | Resultado |
|---|---|
| Requisitos esenciales | PASS |
| Build del SHA nuevo | PASS |
| Pruebas de dominio y Android | PASS |
| Arquitectura/POO y persistencia | PASS |
| Alcance/dependencias | PASS |
| Uso en dispositivo físico / Studio | PENDIENTE condicional |
| Cierre integral del sprint | NO EVALUADO |

**VEREDICTO: PASS WITH OBSERVATIONS.**

**APPROVED_FOR_MERGE** del código validado `eea0fbfc895f5d5e2cde6f3512407f3cc9c16cec` y el head documental `578e7c190e470e840601eaf0750270b3ac5fe699`, más un commit adicional únicamente de este informe bajo `docs/audits/`.

**NOT APPROVED FOR SPRINT ADVANCE:** todavía faltan módulos del encargo. El Orquestador puede fusionar y cerrar **solo S00-T03**. Mantener observación de migración aditiva para la siguiente tarea y no repetir auditoría del código ya aprobado.
