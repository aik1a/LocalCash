# Auditoría integral S00-T07D — gate final de LocalCash

## Identificación y resultado

- **Rol:** Auditor independiente. Una auditoría integral; sin implementación ni correctivos.
- **Fecha:** 18 de octubre de 2026, America/Santiago.
- **Repositorio:** aik1a/LocalCash.
- **SHA exacto auditado de main:** `c149132fda19bb07bfd7ff9170781e1bde99f781`.
- **Rama documental autorizada:** `audit/S00-T07D-integral`, inicialmente en ese mismo SHA.
- **Base aceptada:** PR #11 fusionado, merge `4d1848a150f83973c31859f418c6ef475cc44c39`; cierre administrativo T07C confirmado en PROJECT_STATE y commit auditado.
- **Task Brief:** docs/tasks/active/S00-T07D-gate-integral.md.
- **Tipo:** gate del incremento completo y de suficiencia académica S00.
- **Veredicto:** **PASS WITH OBSERVATIONS**.
- **Gate:** **APPROVED_FOR_SPRINT_ADVANCE**.

Existe evidencia suficiente de los cuatro pilares y los módulos docentes para autorizar técnicamente el cierre académico de S00 y preparar su entrega como instalación nueva compatible. No se certifica cobertura técnica exhaustiva, actualización histórica, recepción docente ni exposición realizada. El Orquestador conserva la decisión y ejecución exclusivas del cierre, integración documental y entrega.

## Método, fuentes y límites de independencia

Lectura directa desde GitHub al SHA fijado de todos los documentos solicitados: PROJECT_ROUTER, AGENTS, PROJECT_STATE, rol AUDITOR, WORKFLOW, TASK_LIFECYCLE, brief T07D, sprint S00, PROJECT_SPEC, TRACEABILITY, ARCHITECTURE raíz, DATABASE, BUILD, PRESENTATION, S00-final-delivery, manifiesto del paquete y auditorías T07A/B/C. Se añadieron CONTEXT_PACKS, AUDIT_MATRIX, AUDIT_TEMPLATE, ADR-001/002, handoff T07C, TEST_PLAN y MANUAL_TESTS.

Se inspeccionaron fuentes Java de presentation/application/domain/data, manifest, XML, Gradle/wrapper, FlowChecks, BiometricChecks, MapChecks, harness de dominio/biometría/mapas/GPS y workflows GitHub Actions. Las comprobaciones revisan el producto integrado, no solo el diff documental de T07C. Este Auditor no implementó los componentes evaluados en esta actuación; los informes previos se usan como antecedentes, contrastados con fuentes y artefactos originales.

**Fuente normativa primaria:** PDF docente adjunto, siete páginas físicas; extracción completa del texto e inspección visual de p.6. SHA256:
`eea19ac075c5105b9a6c24f2c6d925ecb273c6092a2f97c8c01e58f800f7c199`.
Páginas 3–4 definen objetivos, 5–6 módulos y p.6 entregables/cuatro pilares. No se sustituyó el PDF por la especificación derivada.

**Referencia gráfica adjunta:** arquitectura-localcash-generada.png fue inspeccionada. Es conceptual y discrepa del producto: muestra usuario/password_hash/huella almacenada, tipo/color de cuenta, saldo persistido, tablas de participantes/servicios y montos pagados que no existen. No es fuente normativa ni prueba de implementación; no debe presentarse como diagrama físico vigente. El modelo efectivo es el helper v4 y la arquitectura canónica. No se exige implementar esos campos inventados por la imagen.

La clonación HTTPS directa no obtuvo credenciales; se recuperaron fuentes mediante el conector autenticado GitHub con ref inmutable. La descarga es una copia para inspección, no un checkout local completo certificado. Se intentó `bash tests/domain-checks.sh`: detención en línea 5, **exit 127, javac no disponible**. No hay Gradle/SDK/adb/emulator/apksigner disponibles para nueva ejecución Android; Android Studio/dispositivo físico tampoco estuvieron disponibles. **Build y suites Android nuevos locales: NOT TESTED.** Se verificaron en su lugar CI original, fuentes exactas y bytes. No se repitió CI ni se tocó una instalación con datos.

## Identidad del código y CI original

API GitHub confirmó PR #11 merged y main posterior al cierre administrativo. Se comparó el árbol Git del SHA T07A compilado `32dd6e0ff09143bfc55276d5084c18472769b4af` con el SHA auditado: identidad exacta de las siguientes raíces. No se atribuye un nuevo build a main documental.

| Raíz | Git tree/blob idéntico en T07A y main |
|---|---|
| app | e1014d8f3a47856363fd72b413902c6ef8dea279 |
| build.gradle | 3c54a514a2aa19c1781e14e3ba59cb6b2f6ae46f |
| settings.gradle | 082ee732d83004a03dfa9596bf16427492fcdd64 |
| gradle.properties | 1f4ad0a15ad2a27b0a30d246d4ed6b5124ccb6fd |
| gradle | afd6a019245454b82c42f438d0e2670f5d32a678 |
| gradlew | 32abfec07b608014a095f72d8807aebc99c5d06f |
| gradlew.bat | 24c62d56f2d4a91975bd1aa72103d2ec628e449f |
| tests | 1fc4ddbbfa7e03a348527acca2628e10eb32f276 |

Metadatos originales de runs, jobs, steps y artefactos consultados nuevamente:

| Incremento / run | SHA exacto | Resultado y alcance |
|---|---|---|
| [T06B 39042930625](https://github.com/aik1a/LocalCash/actions/runs/39042930625) | c289b0614d9d891c6ea4cfcdeec48dabc342c459 | completed/success; jobs 117425906376 API24 y 117425906567 API36; build, atlas y flujo Android exitosos |
| [T07A 39046939759](https://github.com/aik1a/LocalCash/actions/runs/39046939759) | 32dd6e0ff09143bfc55276d5084c18472769b4af | completed/success; jobs 117437427146 API24 y 117437427132 API36; ambos assemble y pruebas según matriz inferior |
| [T07B 39082526083](https://github.com/aik1a/LocalCash/actions/runs/39082526083) | ece84d4cc7f3eee816f24626094597e43883e169 | completed/success; job 117542262210; inventario y verificación de firmas, no build/regresión nuevos |

T07A hace checkout del SHA del evento y comprueba identidad antes de compilar:
`bash gradlew --no-daemon --console=plain :app:assembleDebug :app:assembleDebugAndroidTest`.
Ambos artefactos originales fueron descargados: exit-code 0, validated-sha exacto e instalación Success para app e instrumentación. Logs registran BUILD SUCCESSFUL, API24 1m4s/API36 1m35s. Los 5483 checks por job se desglosan en 18 + 15 + 5427 + 23; no son 5483 tests instrumentados distintos.

El dispatcher ejecuta la regresión completa solo si API=36. El harness exige marcadores de éxito además del retorno adb; las assertions de FlowChecks/MapChecks/BiometricChecks se revisaron antes de aceptar sus logs. Los fixtures borran bases en AVD de prueba: nunca deben ejecutarse sobre datos personales.

## Matriz de requisitos, implementación y evidencia

PASS se limita al escenario comprobado. Los límites de plataforma/dispositivo se conservan en la sección siguiente. Clases bajo app/src/main/java/com/aik1a/localcash/.

| Requisito / PDF | Implementación real contrastada | Evidencia verificable | Estado |
|---|---|---|---|
| LC-P01; Java nativo, p.3/6 | Java/XML, plugin Android y Groovy; minSdk24/target36, lenguaje Java17 | Gradle y ambos assemble T07A | PASS; Sync Studio separado |
| LC-P02 / TECH-01; SQLite offline | LocalCashDbHelper localcash.db v4, seis tablas; repositories; sin backend | Manifest sin INTERNET; código; migraciones/relanzamientos T07A API36 | PASS |
| LC-P03 / AUTH-01 / TECH-02; biometría | AccesoActivity AndroidX BiometricPrompt BIOMETRIC_STRONG; sesión solo RAM | T07A biometric-checks.log ambas APIs: cancelación/error/lockout/éxito/rotación/background/proceso/intents | PASS en AVD |
| Protección de ocho rutas | Main, Cuenta, Contactos, Compras, NuevaCompra, CompraDetalle, Reparto y Mapa heredan ActividadProtegida | Creación diferida de contenido; ocho intents directos; extra authenticated=true ignorado; internas no exportadas | PASS en AVD24/36 |
| LC-ACC-01; cuentas múltiples | Cuenta/Repository/MovimientoService/MainActivity; nombres y saldo inicial; saldo derivado | android-flow.log API36: dos cuentas aisladas, saldo 125,50 | PASS |
| LC-MOV-01; ingresos/egresos | Movimiento inmutable; registrar valida cuenta, monto y overflow antes de insertar | Formularios, cero/negativo/overflow/FK y helper reabierto API36 | PASS |
| LC-CON-01; agenda local | Contacto, ContactoRepository y ContactosActivity; crear/listar/buscar | purchase-flow.log y relanzamientos API36 | PASS |
| LC-BUY-01/02/03; compras/historial/futuras/contacto | Compra, DetalleCompra, CompraService/Repository y Activities; estado/fecha/contacto opcional | API36: realizada con ítems total 232,00, planificada futura 50,00 sin contacto; persistencia/rollback | PASS |
| LC-SHARED-01/02; detalle y participantes | Compra con ítems y selección explícita de 2+ contactos; RepartoActivity | shared-flow.log API36: contacto de compra no se añade automáticamente; errores y detalle | PASS |
| LC-DEBT-01/02; equitativo/personalizado | Reparto exacto por centésimas; resto por ID; DeudaService/Repository | 100,01/3 = 33,34+33,34+33,33; 20+30+50; suma/duplicados/overflow/rollback/reinicio | PASS API36 |
| LC-MOV-02; ubicación opcional | CapturaUbicacion opt-in, FINE/COARSE, timeout15s; Ubicacion; par nullable SQLite | location-flow.log API36: permiso denegado/fix emulado/retirada/proveedor apagado/timeout/NULL | PASS escenario preciso |
| LC-P04 / MAP-01 / TECH-03; mapa local | osmdroid6.1.18, MapaLocalView OfflineTileProvider, atlas64 PNG, marcadores SQLite por cuenta | MapChecks y logs T07A24/36: instalación sin caché, sin red, fuente real, marcador, detalle/pan/zoom/vacío/corrupción/reapertura | PASS área declarada |
| Persistencia al cerrar/reabrir | SQLite como fuente única; sesión no persiste | Force-stop/relanzamientos reales de harness; saldos/compras/deudas/coordenadas conservados API36 | PASS; no equivale a upgrade |
| Integridad y migraciones | FK activas, PK/CHECK, transacciones compra/ítems y reparto; v1/v2/v3→v4 aditivas | Snapshots previos, rechazo de coordenadas inválidas, rollback segundo insert, sin huérfanos API36 | PASS internas |
| LC-TECH-04; código completo GitHub | app, recursos, wrapper, tests y scripts presentes en árbol Git | Repositorio privado accesible al auditor autenticado | PASS preparado; acceso docente NOT TESTED |
| LC-TECH-05; arquitectura/BD/dependencias/build | ARCHITECTURE, DATABASE y BUILD contrastados con fuentes | Documentos empaquetados idénticos a main; configuración/DDL comprobados | PASS con MINOR documental |
| LC-TECH-06; pauta de exposición | PRESENTATION y S00-final-delivery con pasos y resultados esperados | Guion suficiente para cuatro pilares y módulos; no evidencia de ejecución humana | PASS material preparado |

**Límites funcionales reales:** no hay transferencias bancarias/reales, sincronización remota, notificaciones a contactos, cargos automáticos de compras, pagos parciales ni marcar deuda pagada. Deuda permanece PENDIENTE; no se implementa cobro. Se reparte el total de compra, conservando ítems, no cada producto por separado. No hay edición/borrado general ni transición de planificada a realizada; una adquisición futura se registra con estado y fecha. El PDF permite vínculo local con contacto y no obliga a recordatorios ni estos comportamientos adicionales. Las cuentas no tienen catálogo de tipos. Las simplificaciones no impiden demostrar los módulos exigidos y no deben ocultarse en exposición.

## Comprobaciones independientes de artefactos y cartografía

Se ejecutaron comprobaciones Python/zipfile/hashlib/struct/Pillow, exit 0:

| Objeto | SHA256 recalculado |
|---|---|
| ZIP T07A API24, artefacto 12106073736 | 101943ef20858a287cca8dc019902714d50fc7b1d7f38d60607cd35a34f1653b |
| ZIP T07A API36, artefacto 12105649198 | 9ab61141e5c36e2770b9a870136c1b4941dd99c65cd6164ed29c7daa14d918ab |
| APK académico principal, T07A API36 | f6c188ae06db78a6068b64b69157b4fcfe776b6009561a1a0e9269086e9a83d0 |
| APK instrumentación API36 | 025c89976efe19a17266bf8a21d0ea577b7ce2db8bfe8692cf994f704873c14d |
| APK principal API24 | 90c177bd383da6d7d85f0fa048f29d8997d32298b5095dd86f550d873cd606ac |
| APK instrumentación API24 | 567520c34f872b0798069810529e17bc8a3b605ba485cd9932972cf8620ee304 |
| ZIP T07B original12119172237 | 73d1f7d482de6f64d6a48a6e7dbdcb47494d4ff39643c91e02b287b2dc498483 |
| ZIP académico T07C | 7b0c4e443fc03bc4e8b7e3e62d1642d00d08a7454befd13c5231ff329badfda6 |
| Atlas offline | f37bc75ae6e1c996d6527b411f805eb7bc304d898920a258d208d7acc605ab1d |
| Certificado DER del APK principal, extraído del bloque v2 | 49d73d9652d1cbf9c9891fb90a3d01fc94a3f2a6dcb02aea8a20471b927f191a |

Los digests ZIP coinciden con la API original. CRC correctos, sin entradas duplicadas ni rutas inseguras. Se verificaron 116 sumas de evidencia API24 y 150 API36. APK/hash/SHA/instalación y logs concordantes en ambos originales.

Paquete académico: 94 entradas; los otros 93 archivos verifican SHA256SUMS; APK idéntico al original API36, ambos ZIP T07A empaquetados idénticos a los descargados independientemente y 23 archivos versionados idénticos por Git blob a main. El PDF coincide con el original. No contiene un checkout completo: requiere acceso al repositorio o copia autorizada del código, advertido expresamente.

T07B: se descargó nuevamente el consolidado original; 14 APK coinciden con hashes de inventario y 28 retornos apksigner/aapt son 0. Logs empaquetados coinciden con los originales; diez APK principales tienen diez certificados diferentes: 90 pares dirigidos incompatibles. El APK seleccionado está identificado como com.aik1a.localcash, versionCode1/versionName1.0, SDK24/36 y certificado Android Debug. La extracción DER local corrobora identidad, **no es nueva verificación criptográfica de firma**: la firma v2 PASS procede del apksigner original de CI. No se ejecutó adb install -r ni se inventa un rechazo observado del instalador.

Atlas: se ejecutó sin alterar el script original `python3 review/tools/maps/verify_atlas.py`, **PASS/0**, 64 PNG XYZ 256×256, z14–16, bbox/licencia/hash y vías reales. Se ejecutó `generate_offline_tiles.py --output .../reproduced/santiago_centro.zip`; **PASS/0**, y `cmp` contra el atlas empaquetado dio **0**. Reproducción binaria exacta con Pillow 12.3.0; ZIP de 875101 bytes. Extracto gzip SHA256 `1aaa9a71b9453178345ac7ea47e273a66187a555368ecabe8ac1924408a3e27d`; XML `8503f464f1fe48655a52b69eb75ca13a1a1069b5e79ee0798cfa65503e46f3b7`. Procedencia OSM API 0.6 en cuatro cuadrantes, render propio, avisos ODbL y extracto incluidos. Se verificó cadena documental y hashes; no se repitió la adquisición web ni se descargaron tiles públicos. Android usa solo archivos privados y no tiene INTERNET.

## Cobertura y decisión explícita sobre cada riesgo

| Escenario / límite | Resultado real | Efecto en gate académico |
|---|---|---|
| Compilar app e instrumentación desde CI T07A | PASS API24/API36 | Evidencia suficiente de build del código idéntico; no build nuevo local |
| Dominio | PASS, 5483 checks por job | Cálculos/invariantes contrastados |
| Biometría y mapa automatizados | PASS API24/API36 | Demuestra pilares en AVD, no en sensor físico |
| Regresión financiera/GPS completa API24 | NOT TESTED | MINOR de cobertura; completa API36 y compatibilidad parcial API24 bastan para entrega académica; no prometer paridad exhaustiva |
| Dispositivo físico y sensor biométrico físico | NOT TESTED | Riesgo aceptable para demo AVD; no habilita promesas de compatibilidad de un teléfono concreto |
| GPS exclusivamente aproximado | NOT TESTED | Riesgo aceptable: captura opcional, fallback sin ubicación; no garantizar fix aproximado offline |
| Android Studio Sync limpio/caché vacía | NOT TESTED | Riesgo aceptable con wrapper/fuentes y build CLI real en CI; no declarar Sync certificado |
| Recorrido manual humano/exposición | NOT TESTED | Riesgo de usabilidad/demostración; existe guion y recorrido automatizado. PDF pide pauta preparada; exposición realizada no se acredita |
| Actualización entre APK históricos inventariados | BLOCKED_SIGNER_MISMATCH | Bloquea esa actualización, no instalación nueva ni pilares docentes; T07B cerrado por diagnóstico explícito |
| Conservación SQLite tras upgrade APK | NOT TESTED | No se infiere de migraciones internas. No se aprueba ruta de upgrade; no borrar datos |
| Santiago Centro z14–16 | Límite implementado, PASS dentro del área | Aceptado ADR-002; avisar y usar punto dentro de cobertura |
| APK debug, no release | Confirmado | Aceptable para instalación/exposición académica; sin garantía de firma estable ni distribución |
| SQLite sin cifrado adicional | Confirmado | Biometría protege UI, no archivo frente a acceso privilegiado/debug. No se exige cifrado en PDF; usar datos ficticios, sin promesa de privacidad absoluta |
| Filtros Actions omiten Gradle raíz; workflow T07A ligado a su rama | Confirmado | MINOR futuro; no invalida build exacto contrastado. Nuevos cambios requieren dispatch o tarea autorizada |
| Acceso y recepción por docente | NOT TESTED | No bloquea preparar/cerrar técnicamente el incremento; sí impide afirmar entrega efectivamente recibida |

El resumen del sprint todavía menciona upgrade preservando datos como objetivo global. La fuente operativa posterior especifica T07B como diagnóstico cerrado, acepta expresamente bloqueo por firma y T07D exige evaluar su efecto. Esta auditoría **no declara cumplido ese escenario original**. La aprobación es de suficiencia académica para instalación nueva; no de mantenimiento/actualización de instalaciones históricas. Si la entrega concreta dependiera de actualizar una app ya instalada con datos, esta ruta permanece BLOCKED y esta aprobación no la cubre.

## Entrega preparada frente a recepción

| Necesidad del docente | Material comprobado | Alcance |
|---|---|---|
| Acceder a fuentes | Repositorio privado y archivo fuente por SHA referenciado | Completo en Git; acceso del destinatario no demostrado |
| Compilar | BUILD, Gradle/wrapper, versiones/dependencias reales | CI CLI PASS; primera preparación de herramientas puede necesitar red |
| Instalar | APK principal exacto, hashes, minSdk24, instrucciones y biometría fuerte enrolada | Instalación limpia automatizada PASS; no sobreescribir instalación con datos/firma incompatible |
| Comprender arquitectura/SQLite | ARCHITECTURE/DATABASE, seis tablas, relaciones, migraciones | Contrastados con Java/DDL; no usar imagen conceptual como modelo vigente |
| Seguir demostración | PRESENTATION, datos y resultados esperados | Preparado; ensayo humano/exposición NOT TESTED |
| Consultar pruebas | T07A/B, originales empaquetados y referencias Actions | Archivos verificables; enlaces GitHub pueden requerir permisos |
| Reconocer límites | BUILD, índice/manifiesto, LEEME y presente auditoría | Límites declarados; algunos textos de estado documental anteriores requieren actualización administrativa |

**Material preparado: PASS WITH OBSERVATIONS. Entrega efectivamente recibida: NOT TESTED.** No se envió nada a docentes ni se modificaron permisos. Antes de afirmar recepción, el Orquestador debe disponer de evidencia de entrega y acceso al código. Preservar binarios/evidencias antes de caducidad Actions de enero de 2027; el paquete comprobado ya conserva ambas ejecuciones T07A.

## Hallazgos

| ID | Severidad | Evidencia / impacto | Recomendación concreta |
|---|---|---|---|
| H-01 | MINOR, cobertura heredada | Dispatcher API24 omite regresión financiera/GPS completa y migraciones; verde no significa cobertura completa | Mantener NOT TESTED; ampliar solo mediante tarea autorizada si se necesita compatibilidad API24 completa |
| H-02 | MINOR, infraestructura heredada | paths/branches de s00-t07a-build.yml no cubren automáticamente cambios Gradle raíz/main | Para cambios futuros ejecutar dispatch en ref exacta o asignar ajuste de disparadores; no reutilizar run antiguo |
| H-03 | MINOR, documentación administrativa | TRACEABILITY/S00-final-delivery/manifiesto conservan base 9084b573 y expresiones de auditoría T07C pendiente; sprint aún resume T07 como PENDING. PROJECT_STATE actual distingue correctamente T07A/B/C cerradas y T07D | Orquestador debe registrar este gate y reconciliar estados al cerrar; conservar historia/identidad del paquete sin atribuirle un nuevo build |

**BLOCKER=0; MAJOR=0; MINOR=3.** Sin defecto funcional bloqueante demostrado. Upgrade bloqueado se registra como riesgo operativo explícito, no como PASS ni como defecto docente obligatorio. La imagen adjunta discrepante queda excluida como arquitectura vigente, sin modificarla ni convertir sus propuestas en requisitos.

No hay correctivo de código obligatorio previo al cierre académico. Las acciones administrativas de acceso/recepción y actualización de estado pertenecen al Orquestador; no se realizaron en esta auditoría.

## Matriz canónica de calidad y criterios de aceptación

Aplicación de Q01–Q08 al incremento integral; incluye focos S00 y de integración/entrega. El puntaje no compensa gates críticos.

| Dimensión | Peso | Estado | Evidencia / razón |
|---|---:|---|---|
| Q01 Requisitos/aceptación | 25 | CUMPLE | PDF, 23 IDs y matriz contrastados; sin atribuciones ficticias |
| Q02 Completitud funcional | 15 | CUMPLE | Flujos docentes y errores centrales implementados y probados API36 |
| Q03 Arquitectura/POO | 15 | CUMPLE | Entidades privadas/inmutables, invariantes, SQL en data, services acotados |
| Q04 Calidad/mantenibilidad | 10 | PARCIAL | Código académico legible; disparadores CI incompletos H-02 |
| Q05 Persistencia/datos/seguridad | 10 | CUMPLE | Transacciones/FK/cálculos y gate biométrico; límites de cifrado/upgrade explícitos |
| Q06 Build/tests/regresión | 15 | PARCIAL | Ambos assemble y regresión verificable; cobertura API24/hardware/manual/Studio pendientes |
| Q07 Alcance/dependencias | 5 | CUMPLE | Java/XML/SQLite/BiometricPrompt/osmdroid; sin Firebase/Room/Compose/backend/DI |
| Q08 Documentación/trazabilidad | 5 | PARCIAL | Guías/paquete/handoff suficientes; estado administrativo puntual desfasado H-03 |

**Puntaje diagnóstico: 85/100.** No representa porcentaje de compatibilidad, cobertura ni seguridad.

| AC T07D | Resultado |
|---|---|
| Informe independiente con SHA, método, evidencia, límites y hallazgos | CUMPLE |
| Veredicto explícito | CUMPLE: PASS WITH OBSERVATIONS |
| Gate integral y riesgos razonados | CUMPLE: APPROVED_FOR_SPRINT_ADVANCE académico |
| Informe versionable y handoff al Orquestador | Este documento y SHA final de publicación entregado fuera del informe |

## Gates y handoff al Orquestador

| Gate | Resultado | Justificación |
|---|---|---|
| Cuatro pilares y módulos docentes | PASS | Código real, pruebas originales y atlas exacto |
| Build requerido | PASS | T07A ambos assemble, fuentes idénticas; ejecución local nueva NOT TESTED |
| Regresión aplicable para suficiencia académica | PASS con observaciones | Completa API36, biometría/mapas/dominio API24; vacíos expresos |
| Arquitectura/decisiones/alcance | PASS | Sin stack fuera de alcance ni discrepancia normativa invalidante |
| Integridad/seguridad académica | PASS con límites | SQLite/transacciones/biometría comprobadas; sin certificación exhaustiva ni cifrado adicional |
| Documentación/paquete | PASS con observaciones | Integridad y correspondencia verificadas; H-03 administrativo |
| Actualización histórica | BLOCKED | SIGNER_MISMATCH, fuera de ruta aprobada de instalación nueva |
| Recepción/exposición | NOT TESTED | No se deduce de material preparado |
| Gate de avance académico S00 | **APPROVED_FOR_SPRINT_ADVANCE** | Sin BLOCKER/MAJOR contra el objetivo académico; riesgos aceptables delimitados |

**VEREDICTO GLOBAL: PASS WITH OBSERVATIONS.**

- **APPROVED_FOR_COMMIT:** informe documental de esta auditoría.
- **APPROVED_FOR_SPRINT_ADVANCE:** S00 puede cerrarse técnicamente y entregarse para evaluación académica en el alcance comprobado.
- **Merge documental:** corresponde al Orquestador revisar/integrar el informe publicado; no se ejecuta merge.
- **No se autoriza:** afirmar cobertura exhaustiva API24, validación física/Studio/manual, upgrade exitoso, conservación SQLite tras upgrade, release ni recepción docente.

S00 es cerrable porque los entregables docentes están preparados y los pilares/módulos tienen implementación y evidencia independiente suficiente. No es necesario convertir escenarios pendientes en PASS ni añadir funciones ajenas al encargo para justificar esa suficiencia. La actualización de instalaciones históricas sigue bloqueada; la entrega debe usar instalación nueva compatible y datos de demostración.

**Handoff:** el Orquestador decide el cierre administrativo, registra los riesgos y este gate, mantiene acceso efectivo a fuentes/evidencias y distingue preparación de recepción. Si cambia app/Gradle/tests/atlas, esta conclusión no sustituye la validación del nuevo SHA. No se modificó PROJECT_STATE, código productivo, arquitectura, requisitos, dependencias o ADR; no hubo merge ni cierre de S00. Solo se publica este informe en la rama autorizada. El SHA final de publicación se comunica aparte para evitar autorreferencia.
