# Handoff — S00-T07B

**Rol saliente:** Agente Codificador  
**Fecha:** 2026-10-17, America/Santiago  
**Rama:** task/S00-T07B-firma-upgrade  
**Base:** 68e853fc4cc726500bb03dcbf9877a3a1e1936e2 (main posterior a PR #9)  
**SHA técnico validado:** ece84d4cc7f3eee816f24626094597e43883e169  
**Sprint:** S00, sin cierre.

## Objetivo ejecutado

Determinar la compatibilidad de actualización de las entregas históricas disponibles, inventariando firmas y documentando instalación segura.

## Estado

**PARTIAL:** inventario y procedimiento completos; actualización histórica **BLOCKED_SIGNER_MISMATCH** y conservación SQLite tras upgrade **NOT TESTED**. Se cumple la alternativa de bloqueo explícito prevista por el brief; no se declara cerrada la tarea ni se emite gate propio.

## Cambios realizados

- Workflow y script que descargan diez artefactos originales aceptados T03–T07A y verifican 14 APK con apksigner/aapt.
- Matriz de 90 pares entre diez APK principales, todos bloqueados por certificados distintos. Cuatro APK de instrumentación inventariados por separado.
- Evidencia de fingerprints, metadatos, SHA256 y códigos de retorno.
- Procedimiento para instalación nueva y diagnóstico de actualización, con detención ante firma distinta y sin eliminar datos.
- Corrección comprobada del parser de versionCode vacío de instrumentación y propagación del fallo del pipeline; repetición completa exitosa.

## Archivos modificados

- .github/workflows/s00-t07b-signatures.yml
- tools/apk/inventory.py
- tools/apk/source-artifacts.json
- docs/testing/S00-T07B.md
- docs/handoffs/S00-T07B-coder.md
- docs/handoffs/evidence/S00-T07B/

## Validación ejecutada

| Verificación | Resultado |
|---|---|
| [CI 39082526083](https://github.com/aik1a/LocalCash/actions/runs/39082526083), job 117542262210 | PASS/success, SHA exacto |
| 14 apksigner verify y 14 aapt dump badging | PASS, 28 retornos 0 |
| SHA256/CRC de diez artefactos originales y APK | PASS |
| Contraste propio local de 14 hashes/certificados DER | PASS, retorno 0; no es verificación criptográfica local ni sesión Android |
| Pares históricos compatibles | 0 de 90; diez certificados principales únicos |
| adb install -r / datos antes-después | NO EJECUTADO; bloqueado por firma distinta |
| Conservación SQLite después de actualización | NOT TESTED |
| Nuevo build de aplicación | N/A, no hay cambio de producción ni APK nuevo |
| Primera CI 39082443456 | FAIL de herramienta; corregido y repetido, no se presenta como PASS |

[Artefacto completo 12119172237](https://github.com/aik1a/LocalCash/actions/runs/39082526083/artifacts/12119172237): SHA256 73d1f7d482de6f64d6a48a6e7dbdcb47494d4ff39643c91e02b287b2dc498483, contiene originales y logs.

APK actual conservado de T07A API36: SHA256 f6c188ae06db78a6068b64b69157b4fcfe776b6009561a1a0e9269086e9a83d0; certificado SHA256 49d73d9652d1cbf9c9891fb90a3d01fc94a3f2a6dcb02aea8a20471b927f191a. Package com.aik1a.localcash, versionCode 1.

## Documentación actualizada

Informe de pruebas T07B, este handoff y evidencia de firmas. BUILD.md histórico no se reescribe: su actualización general corresponde a T07C. Trazabilidad/estados administrativos se entregan al Orquestador.

## Decisiones tomadas

No se crea ni recupera una clave; no se cambia signingConfig, versionCode, esquema ni autenticación. No se implementa exportación/importación. No se instala un APK con firma incompatible ni se ensaya reinstalación idéntica como sustituto de actualización histórica.

## Hallazgos o riesgos

- Los diez APK principales de entregas aceptadas usan firmantes distintos, incluso los dos jobs del mismo run. El DN Android Debug y versionCode 1 compartidos no acreditan compatibilidad.
- El inventario se limita a entregas aceptadas T03–T07A; no cubre todo build temporal ni un APK real de usuario no aportado.
- Sin el firmante autorizado de la instalación histórica, no se ha demostrado una ruta de upgrade que conserve sus datos. La decisión de estrategia futura corresponde al Orquestador.
- No hay evidencia antes/después de SQLite porque no existió actualización compatible. Las migraciones internas de T07A no sustituyen esa evidencia.
- No desinstalar ni borrar SQLite para superar una incompatibilidad. Ninguna instalación con datos reales fue intervenida.
- Artefactos Actions tienen caducidad. Preservar binarios antes de enero de 2027.
- Persisten límites físicos/Studio/cobertura API24 de T07A, fuera de esta tarea.

## Pendientes

- Una auditoría independiente T07B sobre SHA técnico, diff documental final, CI y artefactos.
- Orquestador: aceptar el diagnóstico y decidir el tratamiento futuro de la firma histórica/estable si procede; este agente no cambia la estrategia.
- T07C/T07D: conservar instrucciones y límites reales; no anunciar actualización con datos garantizada.

## Contexto mínimo para el siguiente agente

Leer router, AGENTS, rol Auditor, Task Brief T07B, docs/testing/S00-T07B.md y evidence/S00-T07B/inventory.json. Revisar logs apksigner/badging por ID de APK y los dos runs de CI, distinguiendo fallo de herramienta corregido y bloqueo de compatibilidad persistente.

## Próxima acción sugerida

El Orquestador solicita una única auditoría independiente de T07B con esta entrega. No se ha hecho merge, cierre, autoauditoría ni modificación de PROJECT_STATE.md.
