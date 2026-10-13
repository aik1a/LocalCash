# S00-T04 — Contactos y compras locales

**Estado:** CLOSED — 2026-10-13 (PASS WITH OBSERVATIONS) · **Rol:** Codificador · **Sprint:** S00 acelerado
**Rama:** `task/S00-T04-contactos-compras`, desde `main` vigente.
**Objetivo:** app Java/XML funcional para crear/listar contactos, registrar compras (realizadas o planificadas) con varios detalles, calcular totales, mostrar historial y vincular opcionalmente un contacto.

## Context Pack

Leer `PROJECT_ROUTER.md`, `AGENTS.md`, `PROJECT_STATE.md`, `docs/agent/roles/CODER.md`, este brief, `ARCHITECTURE.md`, `docs/design/{BUSINESS_RULES,DOMAIN_MODEL,DATABASE}.md`, `docs/requirements/PROJECT_SPEC.md`; inspeccionar el código Android real.

**Requisitos:** LC-CON-01, LC-BUY-01, LC-BUY-02, LC-BUY-03, LC-P02 y LC-TECH-01.

## Alcance

1. Añadir tablas SQLite mínimas de contactos, compras y detalles a `LocalCashDbHelper`. **Incrementar versión e implementar migración aditiva desde BD v1**, sin borrar cuentas/movimientos ni saldos. FK y validaciones pertinentes. Documentar solo decisiones físicas de estas nuevas tablas.
2. Dominio `Contacto`, `Compra`, `DetalleCompra` con invariantes simples. Total derivado desde cantidades/precios en enteros y aritmética exacta. Repositorios concretos; SQL fuera de Activities. No crear capas vacías.
3. Interfaz XML sencilla integrada con la navegación existente: directorio con alta/lista de contactos; alta y consulta de compras con al menos un detalle de producto/servicio, cantidad/precio, total, estado `PLANIFICADA`/`REALIZADA`, fecha/historial y vínculo **opcional** a contacto existente.
4. Implementar flujo offline, sin Firebase/Room/backend. Conservar funcionamiento S00-T03, sin modificar su dominio contable.
5. Testear en CI/emulador o Android funcional: 2 contactos, compra con 2 detalles y total correcto, compra planificada distinguible, asociación a contacto y persistencia al relanzar. **Verificar migración v1→v2 sobre BD con cuentas y movimientos previos**, y comprobar que sus saldos sobreviven. Compilar el SHA nuevo, registrar log, exit-code y APK.
6. Documentación mínima (`DATABASE.md`, handoff y evidencia). Una auditoría al finalizar, no entre checkpoints.

## Decisiones pendientes y límites

- `BUSINESS_RULES.md` no decide si realizar compra crea movimiento financiero. **No implementar automatismo** ni dar por cerrada esa decisión; registrar explícitamente su estado pendiente y escalar si es necesario. No inventar cuenta de cargo.
- No reparto, participantes ni deudas (S00-T05); no biometría, mapas ni ubicación (S00-T06).
- No añadir teléfono/email obligatorios, pagos parciales, sincronización ni otras funcionalidades fuera del encargo.
- Mantener Java, XML, Groovy, `SQLiteOpenHelper` y arquitectura sencilla. No reescribir el proyecto ni cambiar versiones/dependencias.
- No editar `PROJECT_STATE.md`, hacer merge ni cerrar tarea/sprint.

## Aceptación

- [ ] Contactos creados, listados y persistentes.
- [ ] Compras con detalles consultables, estados distintos y total exacto derivado.
- [ ] Vínculo opcional con contacto y validaciones de cantidad/precio.
- [ ] Migración aditiva verificada preservando datos previos de cuentas/movimientos.
- [ ] Build del SHA exacto PASS; tests funcionales y regresión en emulador con evidencia.
- [ ] Handoff con commits, archivos, resultados y una solicitud de auditoría independiente.

**Entrega:** DONE/PARTIAL/BLOCKED, SHA, evidencia y handoff. Objetivo académico: resultado demostrable con mínima complejidad.

## Cierre administrativo — 2026-10-13

- SHA funcional validado `76480f7639a4ef2e2ec3121817307605ff9843d2`.
- CI/emulador [38289787487](https://github.com/aik1a/LocalCash/actions/runs/38289787487): BUILD PASS, 33 comprobaciones de dominio, flujo compras/contactos y migración SQLite PASS.
- Gate `docs/audits/S00/S00-T04-audit.md`: PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE.
- [PR #4](https://github.com/aik1a/LocalCash/pull/4), merge `93d52298319a5cb57fd902d7d52b513fee7918c4`.
- Riesgo firma APK distinto T03/T04, tratado como observación de entrega T07; no atribuir a SQLite. Sprint S00 permanece abierto.
