# Task Brief — S00-T03: primer flujo funcional — cuentas y movimientos

**Sprint:** S00 — Entrega acelerada
**Estado:** CLOSED — 2026-10-12 (PASS WITH OBSERVATIONS)
**Responsable:** Agente Codificador
**Repositorio:** `aik1a/LocalCash`
**Rama nueva:** `task/S00-T03-cuentas-movimientos`, a partir del main vigente.
**Prioridad:** ALTA (primer resultado de uso real).

> Replanificado por el Orquestador el 2026-10-12 tras solicitud del usuario de priorizar resultados antes del 19 de octubre. Este brief **sustituye** el anterior S00-T03 de dependencias aisladas; el historial Git conserva el texto antiguo.

## Objetivo observable

Que el usuario pueda **crear cuentas, registrar ingresos y egresos, consultar movimientos y ver un saldo correcto**, cerrar y abrir la aplicación y conservar los datos mediante **SQLite local**. Todo con una interfaz Android Java/XML sencilla.

## Requisitos

`LC-P01`, `LC-P02`, `LC-TECH-01`, `LC-ACC-01`, `LC-MOV-01`; evidencia `ACC-01`, `MOV-01` y `BUILD-01`.

## Context Pack mínimo

`PROJECT_ROUTER.md`, `AGENTS.md`, `PROJECT_STATE.md`, `docs/agent/roles/CODER.md`, este brief, `ARCHITECTURE.md`, `docs/design/DOMAIN_MODEL.md`, `docs/design/BUSINESS_RULES.md`, `docs/design/DATABASE.md`, `docs/testing/TEST_PLAN.md` y código Android real.

No cargar todo el repositorio. Inspeccionar primero main y su SHA.

## Alcance implementable

1. Definir en `DATABASE.md` **solo** las decisiones físicas indispensables para `cuentas` y `movimientos` (campos/tipos, representación de montos/fecha y relación FK), indicando expresamente las decisiones tomadas y sin congelar el esquema de compras/deudas. Conservar: monto positivo, tipo `INGRESO`/`EGRESO`, saldo derivado = saldo inicial + ingresos − egresos. Ante ambigüedad funcional sustancial, escalar al Orquestador; no inventar más reglas.
2. Entidades sencillas `Cuenta` y `Movimiento` con estado privado y validación mínima; `SQLiteOpenHelper` y repositories concretos para persistirlas. Ningún SQL en Activities ni saldo almacenado redundantemente.
3. Interfaz XML clara y básica, alcanzable desde la pantalla principal: listar/crear **varias cuentas**, seleccionar una, listar/registrar ingreso y egreso y mostrar saldo. Formularios con validación de nombre, cuenta, tipo y monto positivo. No priorizar diseño visual avanzado.
4. Los datos sobreviven al cierre/apertura: SQLite, **sin servidor ni conexión a internet** para operaciones financieras.
5. Registrar evidencia de al menos estos escenarios: dos cuentas distintas; ingreso y egreso sobre una cuenta con saldo correcto; persistencia después de reabrir (si hay emulador); rechazo de monto inválido. Pruebas automatizadas de cálculo/validación donde resulten sencillas y ejecutables.
6. Compilar el **SHA que incluye el código nuevo** con `:app:assembleDebug`. Usar Android Studio si está disponible o Work/GitHub Actions; un CI genérico que use el SHA del push es preferible al workflow S00-T02 que fija un commit antiguo. Conservar log, retorno y APK. No considerar un build histórico válido para el nuevo código.
7. Añadir un handoff conciso con SHA, cambios, validaciones y bloqueos. Solicitar **una** auditoría independiente del incremento terminado.

## Fuera de alcance

- Compras, contactos, reparto de deudas (S00-T04/T05).
- Mapas, osmdroid, ubicación, biometría y permisos (S00-T06).
- Firebase, Room, backend, sincronización, multiusuario, reportes, analítica, cifrado propio, interfaces elaboradas o abstracciones empresariales.
- Crear entidades/repositorios/services para módulos futuros, migraciones complejas sin necesidad, refactor del stack o nuevas versiones de Gradle/AGP.
- Merge, cierre de tarea/sprint, documentación extensa que no respalde implementación.

## Criterios de aceptación

- [ ] Dos cuentas diferenciadas que se pueden crear y consultar.
- [ ] Un ingreso aumenta el saldo y un egreso lo disminuye, según `BUSINESS_RULES.md`.
- [ ] El historial está asociado a la cuenta correcta y se consulta desde la UI.
- [ ] Persistencia SQLite local y ninguna dependencia de backend.
- [ ] Código Java/XML; mínimo `SQLiteOpenHelper`, dominio, repositories y UI, sin sobreingeniería.
- [ ] Formularios rechazan montos no positivos y nombres inválidos.
- [ ] Build exitoso de SHA exacto con log/exit code y APK; pruebas reales claramente separadas de NO EJECUTADO.
- [ ] Documentación técnica mínima y handoff UTF-8 con diff y SHA.

## Forma de trabajar

Avanzar en **dos checkpoints internos**, dentro de esta misma tarea: (A) BD/cuentas, (B) movimientos/saldos/UI/CI. No solicitar auditoría entre checkpoints. Plan breve previo a implementar; ExecPlan únicamente si surge una decisión o bloqueo que realmente lo necesite.

Entrega: DONE / PARTIAL / BLOCKED, SHA final, evidencia, handoff y solicitud de auditoría. El Auditor decide el gate; el Orquestador integra.

## Cierre administrativo

- Implementación comprobada: `eea0fbfc895f5d5e2cde6f3512407f3cc9c16cec`.
- [Run de validación](https://github.com/aik1a/LocalCash/actions/runs/38283139884): build PASS, 18 pruebas de dominio PASS, UI y persistencia SQLite en emulador PASS.
- `docs/audits/S00/S00-T03-audit.md`: PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE.
- Merge [PR #3](https://github.com/aik1a/LocalCash/pull/3): `f484642361c20246eb07dfc030a0e41e01b76647`. No cierra sprint S00.
