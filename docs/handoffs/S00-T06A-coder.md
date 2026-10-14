# Handoff — S00-T06A

**Rol saliente:** Codificador · **Fecha local:** 2026-10-13 · **Sprint:** S00  
**Rama:** task/S00-T06A-ubicacion  
**Base main:** e9117e8999045c4cb5f6fd7a4aaed200520b3b6f  
**SHA funcional compilado y probado:** ebc985dedfd61797bcf104b73b24ca9c6c926a1b

## Objetivo ejecutado y estado
**DONE del Codificador; auditoría independiente pendiente.** Registrar y consultar ubicación opcional de movimientos con captura Android explícita y SQLite v4, preservando datos anteriores. No se emite gate ni se cierra tarea/sprint.

## Cambios realizados
- Ubicacion inmutable, double finitos en rangos geográficos. Movimiento y MovimientoService conservan API anterior; overload con ubicación opcional.
- MovimientoRepository persiste/lee ambos valores o ambos NULL. SQLite v4 añade solamente latitud/longitud a movimientos, con restricciones de tipo, rango y par; migración aditiva v3→v4 y cadenas v1/v2→v4.
- CuentaActivity ofrece consentimiento por movimiento, permiso FINE/COARSE en tiempo de uso, estado de captura y reintento. No se pide permiso al abrir o registrar sin optar por ubicación.
- CapturaUbicacion usa LocationManager: GPS con permiso preciso o proveedor de red cuando solo está disponible el aproximado; listener acotado a formulario/foreground, timeout de 15 segundos y cancelación al salir/cerrar. No lee lastKnownLocation ni hace tracking.
- Ausencia/denegación de permiso, GPS apagado, falta de fix o consentimiento retirado permiten guardar sin coordenadas. Historial muestra par o Sin ubicación.
- Sin mapas, biometría, backend, geocoding, nueva dependencia ni cambios de firma.

## Archivos modificados
- Dominio: Ubicacion.java, Movimiento.java.
- Persistencia/aplicación: LocalCashDbHelper.java, MovimientoRepository.java, MovimientoService.java.
- UI: CuentaActivity.java, CapturaUbicacion.java, dialog_movimiento.xml, AndroidManifest.xml.
- Pruebas: FlowChecks.java, LocationDomainChecks.java, domain-checks.sh, location-gps-feed.sh, gps-feed.py, location-relaunch-checks.py; workflow s00-t06a-build.yml.
- Documentación: DATABASE.md, DOMAIN_MODEL.md, docs/testing/S00-T06A.md, ExecPlan S00-T06A y evidencia/handoff.

## Validación ejecutada

[Actions run 38498296734](https://github.com/aik1a/LocalCash/actions/runs/38498296734): **SUCCESS** sobre el SHA funcional exacto.

| Verificación | Comando/escenario | Resultado |
|---|---|---|
| Build Android | bash gradlew --no-daemon --console=plain :app:assembleDebug :app:assembleDebugAndroidTest | PASS, retorno 0, BUILD SUCCESSFUL in 49s |
| Dominio | bash tests/domain-checks.sh | PASS: 5483 comprobaciones = 18 cuentas/dinero + 15 contactos/compras + 5427 reparto + 23 ubicación |
| Regresión T03–T05 | FlowChecks: cuentas, compras, compartidas; relanzamientos previos | PASS UI, saldo, compras, contactos, deudas, FK y rollback |
| Migraciones | Fixtures originales v1/v2/v3 → v4 | PASS snapshots de todas las filas/columnas originales correspondientes, NULL antiguos, saldos y FK; v3 incluye seis tablas/deudas |
| Entradas SQLite | INSERT/UPDATE par incompleto, rango, texto e infinito | PASS rechazos sin alterar registros |
| Sin consentimiento | Formulario no selecciona ubicación ni pide permiso | PASS registro con NULL |
| Permiso denegado | Diálogo Android real, permission_deny_button | PASS estado visible, sin crash, registro con NULL |
| Captura GPS | Permisos Android concedidos con pm grant y adb emu geo fix -70.66 -33.45 | PASS callback/UI productivos y persistencia exacta del fix recibido |
| Fallbacks | Retirar consentimiento, cmd location desactivado, proveedor Android de pruebas sin fix | PASS registro sin ubicación; timeout real de 15 segundos |
| Persistencia real | Reapertura helper, force-stop, am start, location-relaunch-checks.py | PASS un par nuevo y cinco movimientos nuevos sin ubicación; saldo 131,50, deudas anteriores intactas |
| Inspección visual | Capturas location-restarted-0/1.png | PASS coordenadas completas y Sin ubicación legibles en historial navegable |

**Par GPS recibido y conservado exactamente:** -33.45, -70.65999833333333. El emulador cuantiza el valor de inyección; se verifica proximidad al punto esperado con tolerancia de 0,00001 grados y después igualdad exacta del valor capturado/persistido/visible. No se redondean coordenadas productivas.

**APK:** app-debug.apk, 92963 bytes.  
**SHA256 APK:** e2c9c934ddd285e4dabc5587f3751c35d93070eba92a2077437489aeae8c8117  
**Artefacto CI:** ID 11863884007, S00-T06A-ebc985dedfd61797bcf104b73b24ca9c6c926a1b.  
**SHA256 ZIP original:** e69534724d8a45411ab943d202054e5f694e73430816ec5906cfb8e9ac4d9968.

Salida completa de Gradle, retorno, SHA, pruebas, logcat, GPS, XML/capturas y APK en el artefacto CI; paquete conservado LocalCash-S00-T06A-APK-evidencia.zip. Evidencia textual y verification.json bajo docs/handoffs/evidence/S00-T06A/.

## Documentación y decisiones
DATABASE y DOMAIN_MODEL actualizados solo para ubicación/v4. El test de fixture v2 de T05 reconstruye las columnas contables originales únicamente en el emulador desechable para no fingir una BD v2 con columnas v4. No se modifica la persistencia productiva de compras/deudas.

Primer intento: [run 38497503997](https://github.com/aik1a/LocalCash/actions/runs/38497503997), SHA 4c331d8326fa0ff55a994be81ec1f159e1c6a800. Build/dominio/regresión/migraciones/denegación PASS; falló una expectativa de texto GPS que exigía -70.66 exacto pese a captura válida. Se corrigió la prueba para evaluar el fix y su conservación exacta; el nuevo run completo PASS. Evidencia de causa/corrección conservada; no se oculta el intento fallido.

El commit posterior de entrega solo registra documentación/evidencia; no cambia el código compilado. PROJECT_STATE.md, Task Brief, auditorías y sprint no fueron modificados.

## Riesgos, limitaciones y pendientes
- Validación API36 x86_64 en emulador; no equivale a GPS/dispositivo físico ni prueba específica de permiso solo aproximado en equipo físico.
- GPS es local; proveedor aproximado puede depender de disponibilidad del sistema. Ninguno es obligatorio para registrar movimientos.
- Firma/actualización APK con certificado previo sigue para T07; no desinstalar instalaciones con datos para probar una actualización.
- Advertencias Gradle heredadas permanecen; build actual PASS.
- Fixtures eliminan/preparan la BD exclusivamente en emulador desechable. No ejecutar estas pruebas sobre datos de usuario.
- Mapas y biometría siguen fuera de T06A. Auditoría y autorización de merge corresponden a agentes independientes.

## Contexto mínimo y siguiente acción
Leer Task Brief S00-T06A, BR-GEO-01, DATABASE, DOMAIN_MODEL y docs/testing/S00-T06A.md. Revisar diff base main → SHA funcional, CI y evidencia.

**Solicito una única auditoría independiente de S00-T06A.** Sin merge, cierre ni actualización de PROJECT_STATE.md.
