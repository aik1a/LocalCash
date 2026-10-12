# Handoff — S00-T03

**Rol saliente:** Agente Codificador  
**Fecha:** 2026-10-12  
**Rama:** task/S00-T03-cuentas-movimientos  
**Base:** main, 905ac2fdafcf82ac0d21183e1e9fa893dd2d7d2a  
**Sprint:** S00

## Objetivo ejecutado

Crear/listar cuentas, abrir historial por cuenta, registrar ingresos/egresos y consultar saldo persistente con Java/XML y SQLiteOpenHelper.

## Estado

DONE (implementación y validación CI completadas). No implica gate del Auditor ni cierre del Orquestador.

## Cambios realizados

- Checkpoint A: b05cf6e5087b58c11d2a3e7c2acb0d8bcebc0dbf, dominio, SQLite v1, repositories y coordinación mínima.
- Checkpoint B inicial: ae63dcda5b49048f3aca2cadafec845babd848aa, pantallas/formularios Java/XML y pruebas/CI.
- Cuenta calcula saldo inicial + ingresos − egresos; Movimiento preserva monto positivo y tipo. Sin saldo duplicado en BD ni SQL en Activities.
- Pantalla principal muestra múltiples cuentas y saldo; cada cuenta abre historial propio con fecha, tipo, monto y descripción. Formularios mantienen abierto el diálogo si el dato es inválido.
- SQLite local con FK y transacción que rechaza overflow antes de persistir. Sin permiso de internet ni dependencias de runtime nuevas.

- Ajustes posteriores exclusivos de validación: selector por ID, diagnóstico de UI y esperas acotadas.

## Archivos modificados

`app/src/main/java/com/aik1a/localcash/` (domain/data/application/presentation), cuatro layouts XML, AndroidManifest.xml, app/build.gradle (runner de pruebas), FlowChecks.java, tests/, workflow S00-T03. Documentación: DATABASE.md, DOMAIN_MODEL.md, PACKAGE_STRUCTURE.md, docs/testing/S00-T03.md y este handoff/evidencia.

## Validación ejecutada

Resultado verificable y SHA se registran en `evidence/S00-T03/verification.json`. Run: https://github.com/aik1a/LocalCash/actions/runs/38283139884.

**SHA exacto validado:** eea0fbfc895f5d5e2cde6f3512407f3cc9c16cec. Los commits posteriores de handoff/evidencia solo cambian docs; no se presentan como nuevos builds.

| Verificación | Resultado |
|---|---|
| assembleDebug + assembleDebugAndroidTest | PASS, retorno 0, log completo en evidence/S00-T03/assembleDebug.log |
| Dominio/validación/dinero | PASS, 18 comprobaciones |
| Formularios Android, dos cuentas, ingreso/egreso | PASS, emulador API 36; 100 + 50 - 24,50 = 125,50 |
| Cuenta Banco independiente | PASS, saldo 300,00 e historial vacío |
| SQLite FK/rollback overflow y helper reabierto | PASS |
| Proceso cerrado y aplicación relanzada | PASS, saldo conservado; relaunch-window.xml |
| Dispositivo físico / Android Studio sync | NO EJECUTADO |

Artifact 11762057450 incluye APK, logs, captura y árbol de UI. APK SHA256: 840113ae00deaa046b2d02a62d8f021c1f6c468f26ff105d5da0c09c56dae603.

Se corrigió el test de accesibilidad para usar ID en el botón inicial y comparación sin distinguir mayúsculas. Se acotó la espera de UI; el run final ejecutó y aprobó el flujo completo. No se alteró el código funcional para resolver estos problemas del test.

## Decisiones tomadas

Solo las físicas autorizadas en Task Brief: centésimas long, fecha epoch milisegundos, descripción opcional, FK activa/restrict, índice por cuenta/fecha/ID. Nombre distingue visualmente cuentas; ID distingue identidad (sin imponer unicidad). Saldo inicial y saldo derivado pueden ser negativos; no se inventó restricción de fondos insuficientes. Catálogo de tipo de cuenta sigue pendiente. El esquema de módulos futuros sigue abierto.

## Hallazgos o riesgos

- Work tiene Java runtime 17 pero no javac/SDK; intento local de prueba devuelve 127 (javac ausente). CI usa JDK 25 y el stack original, sin actualizar AGP/Gradle.
- Pruebas humanas en Android Studio/dispositivo físico NO EJECUTADAS. La automatización del emulador no equivale a revisión humana del teclado o del diseño en todos los tamaños.
- No hay edición/borrado, mapas, biometría, compras ni deudas en este incremento, conforme al brief.
- UI y acceso local síncronos, apropiados al volumen de demo académico. No se midió desempeño con miles de movimientos.

## Pendientes y siguiente agente

Solicito una única auditoría independiente del incremento S00-T03 completo. Leer Task Brief, este handoff y evidencia; revisar diff desde la base indicada y SHA compilado. Criterios ACC-01/MOV-01/DB-01/BUILD-01; verificar aislamiento por cuenta, dinero exacto, invariantes, persistencia y límites de alcance.

El Codificador no modifica PROJECT_STATE.md ni TRACEABILITY.md para cerrar la tarea. Se entrega al Orquestador la evidencia de requisitos LC-P01, LC-P02, LC-TECH-01, LC-ACC-01 y LC-MOV-01 para su actualización tras el gate. No hubo merge ni cierre ni auditorías entre checkpoints.
