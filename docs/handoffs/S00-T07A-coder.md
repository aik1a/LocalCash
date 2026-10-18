# Handoff — S00-T07A

**Rol saliente:** Agente Codificador  
**Fecha:** 2026-10-17, America/Santiago  
**Rama:** `task/S00-T07A-integracion`  
**Base:** `cc77afc2c676770329eb89e367ea44f8c11c9e1a`  
**SHA ejecutado en CI:** `32dd6e0ff09143bfc55276d5084c18472769b4af`  
**Sprint:** S00, sin cierre por este agente.

## Objetivo ejecutado

Validar el estado integrado mediante build de app/instrumentación, instalación nueva en AVD y suites existentes de dominio, biometría, cartografía offline y regresión financiera/GPS. Identificar los APK y conservar evidencia reproducible para auditoría independiente.

## Estado

`PARTIAL` en cobertura de plataformas: build y regresión automatizada ejecutados satisfactoriamente en el entorno disponible; se entregan explícitamente escenarios NOT TESTED (recorrido financiero/GPS completo API24, físico, aproximado exclusivo y Studio). Entrega preparada para revisión independiente. No se declara cerrada T07A.

Este handoff es un informe del Codificador, no una aprobación de integración ni una declaración de cierre de T07A.

## Cambios realizados

- Workflow T07A derivado del workflow T06B inspeccionado: checkout y comprobación del SHA exacto; API24/API36; ausencia de paquete antes de instalación; instalación sin `-r`; hashes de APK principal y de pruebas; manifiesto SHA256 de evidencia; subida de logs/APK incluso ante fallo.
- Reutilización de las suites existentes sin alterar sus gates ni el comportamiento productivo.
- Informe de pruebas y evidencia local/CI identificada por SHA, run y job.
- Sin defectos funcionales concretos demostrados que requirieran cambiar producción.

## Archivos modificados

- `.github/workflows/s00-t07a-build.yml`
- `docs/testing/S00-T07A.md`
- `docs/handoffs/S00-T07A-coder.md`
- `docs/handoffs/evidence/S00-T07A/`

No se modifica `PROJECT_STATE.md`, requisitos, ADR, dependencias principales ni documentación general. No se interviene en T07B/T07C, no se fusiona ni se declara el cierre del sprint.

## Validación ejecutada

| Verificación | Resultado |
|---|---|
| CI nueva [39046939759](https://github.com/aik1a/LocalCash/actions/runs/39046939759) sobre SHA exacto | PASS; jobs API24/API36 success |
| Ambos assemble + APK/hash | PASS, exit 0 en ambos jobs |
| Dominio | PASS, 5483 comprobaciones por job |
| Instalación nueva, biometría real del emulador y primer mapa offline | PASS API24/API36 |
| Regresión completa cuentas/movimientos/contactos/compras/repartos/deudas/GPS/migraciones y relanzamiento | PASS API36; NOT TESTED completa API24 |
| Digest ZIP / hashes de evidencia y APK / atlas y XML OSM dentro del APK | PASS, verificación local de bytes descargados; 118 y 152 entradas hash |
| Work: verificación/regeneración exacta del atlas, parsing Python/XML | PASS |
| Work: pruebas Java / build / emuladores | BLOCKED o NOT TESTED por toolchain/red; logs conservados |

APK debug principal API36: SHA256 `f6c188ae06db78a6068b64b69157b4fcfe776b6009561a1a0e9269086e9a83d0`, artefacto `12105649198`. API24: `90c177bd383da6d7d85f0fa048f29d8997d32298b5095dd86f550d873cd606ac`, artefacto `12106073736`. Ver tabla completa ZIP/APK/tests en el informe de pruebas.

Detalle por escenario, plataforma, método y límites en [informe de pruebas](../testing/S00-T07A.md). Los recorridos de UI son automatizados; no hubo prueba manual humana ni dispositivo físico. Los bloqueos locales no se contabilizan como pruebas Android ejecutadas.

## Documentación actualizada

Informe T07A y este handoff. La trazabilidad de cierre se entrega al Orquestador; no se cambian estados canónicos. H-01 documental general permanece asignado a T07C.

## Decisiones tomadas

Ninguna decisión funcional/arquitectónica nueva. Se conserva la cobertura existente: mapas y seguridad biométrica en ambas APIs; recorrido financiero/GPS completo en API36. Las diferencias de certificados debug entre jobs no se interpretan como compatibilidad de upgrade.

## Hallazgos o riesgos

- No hay toolchain Android/AVD local; Java local tiene runtime, sin compilador. Gradle local no superó la descarga de distribución por red restringida.
- Los fixtures borran/recrean SQLite de prueba en AVD descartable; no ejecutar estos scripts contra datos reales.
- Cartografía limitada a Santiago Centro z14–16; `FLAG_SECURE` permanece activo.
- Cobertura completa API24, ubicación exclusivamente aproximada, equipo físico y Sync Android Studio: NOT TESTED.
- Firma estable y upgrade APK histórico conservando datos: T07B, no demostrado mediante migraciones SQLite.

## Pendientes

- Auditoría independiente de los commits y las evidencias T07A, solicitada por el Orquestador.
- Evaluación de los escenarios NOT TESTED y autorización de integración por el Orquestador.
- Mantener T07B/T07C/T07D en sus responsabilidades y sin afirmar que este trabajo los completa.

## Contexto mínimo para el siguiente agente

Leer router, AGENTS, rol Auditor, Task Brief T07A y `docs/testing/S00-T07A.md`. Revisar el diff desde la base indicada; el único archivo de ejecución nuevo es el workflow. Consultar `docs/handoffs/evidence/S00-T07A/`, la CI y los artefactos referidos por sus IDs/digests.

La CI T06B `39042930625` es evidencia heredada del SHA `c289b0614d9d891c6ea4cfcdeec48dabc342c459`; no es el build integrado T07A. La CI nueva es [39046939759](https://github.com/aik1a/LocalCash/actions/runs/39046939759), SHA `32dd6e0ff09143bfc55276d5084c18472769b4af`.

## Próxima acción sugerida

El Orquestador puede solicitar auditoría independiente de T07A con este handoff y las limitaciones explícitas. La autorización para integrar o cerrar corresponde al Orquestador tras el gate correspondiente.
