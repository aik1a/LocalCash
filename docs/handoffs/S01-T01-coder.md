# Handoff — S01-T01

**Rol saliente:** Codificador
**Fecha:** 2026-10-19
**Rama/commit técnico:** task/S01-T01-divisas / 6b7813cb81a8742c1b6fd12f8259ec637852487f
**PR:** https://github.com/aik1a/LocalCash/pull/13
**Sprint:** incremento posterior a S00; no se reabre su cierre.

## Objetivo ejecutado
Moneda principal inicial persistida, diez divisas, moneda por cuenta/compra y formato/
validación según moneda. Incluir «Tú» en reparto con un contacto; quitar IDs visibles
y aclaraciones técnicas de etiquetas, sustituidas por ejemplos.

## Estado
DONE (codificación) — implementación y validación completas; listo para auditoría independiente.
No es auditoría independiente ni autorización de merge.

## Cambios realizados
- Divisa enum con diez códigos. CLP/JPY sin decimales; ocho restantes con dos.
- Dinero conserva escala histórica de centésimas, BigDecimal exacto, formato con
  código y agrupación visual. Entrada sin separador de miles y sin redondeo silencioso.
- SQLite v5 aditiva, configuracion única y columnas divisa; asignación explícita de
  moneda a registros anteriores validando también movimientos/precios/deudas.
- Configuración protegida por biometría, una vez mientras falte registro; selección
  inicial al crear cuenta/compra. Divisa fija; movimientos/deudas la heredan.
- Reparto con cuota propia separada, nunca contacto sintético ni deuda del usuario.
  Equitativo respeta unidad mínima y resto estable (Tú primero); personalizado suma
  cuota propia y contactos. Guardado atómico, rechazo de segundo reparto.
- Registros históricos no se reinterpretan ni reciben cuota propia retroactiva.
- Nombres sin #id, etiquetas simples y ejemplos; mapas muestran divisa también.

## Archivos modificados
Dominio Divisa/Dinero/Cuenta/Compra/Reparto; repositories y servicios correspondientes;
ActividadProtegida, DivisasUI y Activities monetarias; layouts de selección/formularios.
CurrencyDomainChecks, CurrencyChecks Android, sqlite-currency-checks.py y currency-ui.py;
regresión existente adaptada a moneda explícita, nombres limpios y participación propia.
Workflow .github/workflows/s00-t06c-build.yml.

## Validación ejecutada
| Verificación | Comando/escenario | Resultado |
|---|---|---|
| Dominio | bash tests/domain-checks.sh | PASS: 34.400 comprobaciones; 28.917 específicas nuevas |
| SQLite host | python tests/sqlite-currency-checks.py | PASS: 55; DDL Java real, v1–v4→v5 y constraints |
| XML/Python/diff | ElementTree, py_compile, git diff --check | PASS |
| Android API24 y API36 | CI 39178585250, SHA 6b7813cb | SUCCESS ambos jobs, build app/AndroidTest y artefactos |
| SQLite Android | CurrencyChecks en ambas APIs | PASS 76 checks por API; migraciones v1–v4→v5 |
| UI y seguridad | Configuración, CLP/USD, Tú + Valentina, reinicio, biometría y mapas | PASS ambas APIs |
| Regresión financiera/GPS | Workflow API36 | PASS cuentas, compras, reparto, ubicación y reaperturas |
| Recorrido humano/dispositivo físico | No ejecutado | NOT TESTED |

## Documentación actualizada
Task Brief y ExecPlan S01-T01; DATABASE, BUSINESS_RULES, DOMAIN_MODEL y sección adicional
TRACEABILITY. No se cambian veredictos ni cierre de S00. Resultado detallado en testing.

## Decisiones tomadas
Autorización expresa de Aikia en esta conversación. Sin conversión de divisas, total
multimoneda, cambios de dependencias/arquitectura/biometría, tipo de cuenta o backend.
Moneda previa desconocida se deja null hasta asignación; si CLP/JPY encuentra fracciones,
rechaza asignación sin alterar datos. Preferencia inicial no cambia registros previos.
Una única persona en agenda se preselecciona junto con Tú; varios requieren selección.

## Hallazgos o riesgos
- El entorno local resuelve descargas por curl, pero Gradle no pudo resolver su proxy.
  No se alteran versiones para ocultarlo; se usa CI con stack original.
- El host no dispone de /dev/kvm; no se declara emulación local realizada.
- FLAG_SECURE se conserva; evidencia UI mediante jerarquías XML y logs del emulador.
- Sigue vigente el riesgo de firma de APK histórico documentado en T07B: migración
  SQLite probada no acredita upgrade entre certificados incompatibles.

## Pendientes
Auditoría independiente antes de merge/cierre. No se modifica main ni se instala nada en el equipo del usuario.

## Contexto mínimo para el siguiente agente
Leer PROJECT_ROUTER, AGENTS, PROJECT_STATE, Task Brief/ExecPlan S01-T01 y diseño actualizado.
Revisar diff de PR13 y CI final https://github.com/aik1a/LocalCash/actions/runs/39178585250.
No usar el cierre S00 como aprobación de este incremento.

## Próxima acción sugerida
Auditar este incremento contra los criterios S01-T01 con la validación final disponible.

## Evidencia final
CI final: https://github.com/aik1a/LocalCash/actions/runs/39178585250.
Jobs 117826521050 (API24) y 117826521234 (API36): SUCCESS, incluidos artefactos.
Artefactos 12155761036 y 12154864015: APK y evidencia por API.
El commit posterior de cierre documental no modifica el SHA técnico validado.
