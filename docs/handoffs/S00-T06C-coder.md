# Handoff — S00-T06C
**Rol saliente:** Codificador  
**Fecha:** 2026-10-14  
**Rama:** `task/S00-T06C-biometria`  
**SHA funcional compilado y probado:** `0f79e6c57be54144b1a756b3aff3a9644b025ca8`  
**Base:** `main`, `0e1d818d81ef1085f41780b03e84c65c0cdb0426`  
**Sprint:** S00  
**CI:** https://github.com/aik1a/LocalCash/actions/runs/38625086287  
**PR de trabajo:** https://github.com/aik1a/LocalCash/pull/7

## Objetivo ejecutado
Proteger cuentas, movimientos, contactos, compras y deudas mediante BiometricPrompt, incluida reentrada desde background y rutas internas, sin cambiar SQLite v4 ni ubicación.

## Estado
**DONE — implementación y validación; pendiente auditoría independiente**. Tarea abierta; sin merge, cierre ni modificación de PROJECT_STATE.md.

## Cambios realizados
- AccesoActivity usa AndroidX Biometric 1.1.0 y BIOMETRIC_STRONG; disponibilidad/enrolamiento, éxito, cancelación, fallos y bloqueo temporal con reintento.
- ActividadProtegida difiere contenido y repositorios de las siete Activities hasta autenticar; ninguna ruta interna acepta extras como credenciales.
- Sesión solo en memoria en LocalCashApplication; nuevo desafío tras background/reinicio. Un callback de otra generación no abre sesión; Bundle no persiste autorización.
- Contenido oculto al pausar, ventanas FLAG_SECURE y diálogos descartados al detener. Restauración de formularios solo tras éxito del prompt.
- Única dependencia directa nueva aprobada: androidx.biometric:biometric:1.1.0; AccesoActivity usa FragmentActivity/tema AppCompat transitivos. Java/XML/Groovy, minSdk24/target36 intactos.
- Sin cambios en dominio, servicios, repositorios ni helper SQLite; sin mapas, PIN propio, backend, credenciales guardadas ni bypass de pruebas.

## Archivos modificados
- `app/build.gradle`, `gradle.properties`, `app/src/main/AndroidManifest.xml`.
- `app/src/main/java/com/aik1a/localcash/presentation/AccesoActivity.java`, `ActividadProtegida.java`, `LocalCashApplication.java`.
- Las siete Activities existentes de presentación; `activity_bloqueo.xml`, `styles.xml`.
- `FlowChecks.java`, `BiometricChecks.java`, `tests/biometric-emulator.py`, `tests/biometric-regression-dispatch.py`.
- `.github/workflows/s00-t06c-build.yml`.
- Documentación técnica/plan/pruebas y esta entrega. Diff funcional: https://github.com/aik1a/LocalCash/compare/0e1d818d81ef1085f41780b03e84c65c0cdb0426...0f79e6c57be54144b1a756b3aff3a9644b025ca8

## Validación ejecutada
| Verificación | Comando/escenario | Resultado |
|---|---|---|
| Build exacto API24/API36 | `bash gradlew --no-daemon --console=plain :app:assembleDebug :app:assembleDebugAndroidTest` | PASS; exit-code 0 en ambos |
| Dominio | `bash tests/domain-checks.sh` | PASS; 5483 comprobaciones en cada job |
| Biometría API24/API36 | `python tests/biometric-emulator.py prepare/enroll/security` | PASS; huella enrolada y desafío real |
| Regresión T03–T06A API36 | `python tests/biometric-regression-dispatch.py 36` | PASS; flujos instrumentados y relanzamiento |
| Integridad de artefactos | ZIP descargados contra digest GitHub; APK contra SHA256 CI; validated-sha contra SHA funcional | PASS |
| Alcance/diff | Comparación desde base main | Sin cambios SQLite/dominio/ubicación/PROJECT_STATE |


### Biometría real del emulador
Enrolamiento por Ajustes Android y sensor `adb emu finger touch`; ningún test invoca callbacks de éxito, cambia campos de sesión o desactiva el gate. El PIN 1234 es exclusivamente el bloqueo de los emuladores desechables para enrolar una huella: LocalCash no lo solicita ni acepta. Las pruebas instrumentadas preparan datos y verifican rutas internas; la huella real del sensor emulado desbloquea la UI.

Escenarios: sin biometría registrada; cancelación; dedo no enrolado; bloqueo temporal/reintento; éxito; navegación cuentas/movimientos/contactos/compras/deudas; rotación; regreso desde background; force-stop; matar/restaurar proceso con prompt pendiente; extra authenticated=true; acceso directo a las siete Activities y rechazo externo de rutas no exportadas.

### Regresión T03–T06A
API36: cuentas/movimientos/saldos; contactos/compras realizadas y futuras/detalles; reparto exacto/personalizado/deudas; FK, CHECK y rollback; migraciones v1/v2/v3→v4 con snapshots anteriores intactos; permiso Android denegado; captura GPS por LocationManager; consentimiento retirado/GPS desactivado/timeout; coordenadas/NULL y datos recuperados tras relanzamiento. Cada desafío se autentica mediante sensor del emulador.

## Evidencia y APK
- APK seleccionado del job API36: `LocalCash-S00-T06C-debug.apk`.
- SHA256 APK: `aa136956836bf9ca8839bff6cf81e1e911e927f5f9394a76bd8fa9e48c9b6c1d`.
- Paquete: `LocalCash-S00-T06C-APK-evidencia.zip`, con APK, evidencia completa de ambas APIs, logs de Actions, verificación y documentación.
- Artefacto API24: https://github.com/aik1a/LocalCash/actions/runs/38625086287/artifacts/11924252922
- Artefacto API36: https://github.com/aik1a/LocalCash/actions/runs/38625086287/artifacts/11924053647
- Resumen y logs seleccionados versionados en `docs/handoffs/evidence/S00-T06C/`.
- API24: PID 5828 eliminado; PID restaurado 5892. Consultar `bio-process-death.log` de cada API para la evidencia del proceso real.

## Documentación actualizada
ARCHITECTURE.md; docs/testing/S00-T06C.md; ExecPlan completado; docs/handoffs/evidence/S00-T06C/; este handoff. El commit posterior a la validación contiene únicamente documentación/evidencia; no confundirlo con el SHA funcional compilado.

## Decisiones y descubrimientos
Se aplicó la dependencia de compatibilidad aprobada, sin rebajar minSdk ni usar fallback de credencial. API24 necesita navegar Ajustes de seguridad y desplazar la ilustración «Find the sensor» para acceder a NEXT; las ejecuciones anteriores detenidas allí no acreditaban autenticación. El comando de muerte de proceso también necesita `--user 0` en API24: la prueba anterior devolvía `Invalid packageName` sin terminar el proceso; ahora verifica ausencia y cambio de PID. La validación final corresponde exclusivamente al SHA/run indicados arriba.

## Hallazgos o riesgos
- Emuladores API24/API36; hardware físico y actualización de firma de APK siguen pendientes de T07.
- FLAG_SECURE puede dejar capturas negras; la evidencia son XML/logs, enrolamiento y desafío/sensor. No se desactiva para capturas.
- Los fixtures de Instrumentation borran/preparan SQLite en emuladores desechables. No ejecutar el APK de pruebas sobre datos reales.
- Mapas T06B no están autorizados ni implementados; no se inventa su regresión.
- Sin fallos bloqueantes conocidos tras la validación final; pendiente veredicto independiente.

## Contexto mínimo para el siguiente agente
Leer Task Brief, rol Auditor, arquitectura, documento de pruebas y este handoff. Revisar el diff funcional, CI y evidencia; comparar el APK entregado con su SHA256.

## Próxima acción sugerida
Una única auditoría independiente de S00-T06C. El Codificador no emite veredicto independiente, no hace merge ni cierra tarea/sprint.
