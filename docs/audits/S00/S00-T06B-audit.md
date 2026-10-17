# Auditoría independiente — S00-T06B

## Identificación

- **Rol:** Agente Auditor independiente, Work; sin participación en la implementación de esta iteración.
- **Fecha:** 2026-10-17, America/Santiago.
- **Tipo:** gate técnico de tarea; no gate integral ni cierre de S00.
- **Rama:** `task/S00-T06B-mapas`.
- **SHA funcional auditado:** `c289b0614d9d891c6ea4cfcdeec48dabc342c459`.
- **Baseline main / merge-base:** `9f13e3036aad9815020df3b59bea66a357f1fc9b`.
- **Documentación final revisada:** `41b77870dae10163df704299d1ba3a2e9db8e25d`, hijo directo del SHA funcional; comparación confirma cambios exclusivamente documentales/evidencia.
- **Task Brief:** [S00-T06B-mapas](../../tasks/active/S00-T06B-mapas.md).
- **Handoff:** [S00-T06B-coder](../../handoffs/S00-T06B-coder.md).
- **Resultado:** **PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE**.

## Fuentes y método

Se recuperaron desde GitHub PROJECT_ROUTER.md, PROJECT_STATE.md, AGENTS.md, rol AUDITOR, CONTEXT_PACKS, AUDIT_MATRIX y AUDIT_TEMPLATE. Se consultaron el Task Brief, ADR-002, ARCHITECTURE.md, DATABASE.md, PROJECT_SPEC.md, S00-foundation, S04-geolocation-maps, BUILD.md, pruebas T06B/T06C, ExecPlan completado, procedencia, avisos OSM y APK-layout.

Se leyó el PDF docente original: mapas locales interactivos, coordenadas opcionales, Java, SQLite y acceso biométrico. El atlas acotado sigue la decisión explícita ADR-002; no se interpreta como cobertura nacional.

Se revisaron el diff real main→SHA funcional (31 archivos, 3 commits), parches modificados y archivos nuevos de producción, pruebas y generación. El diff posterior al SHA funcional es documental. No hay cambios de dominio, servicios financieros, repositorios, esquema SQLite ni sesión biométrica productiva.

El clon HTTPS no pudo autenticarse en este entorno; GitHub se inspeccionó mediante el conector autorizado. Para las comprobaciones locales se reutilizaron copias disponibles solo después de contrastar sus hashes Git blob con el árbol remoto del SHA funcional: 86 blobs pertinentes coincidentes, incluidos código Android, tests, herramientas y binarios cartográficos. Cuatro documentos locales antiguos se descartaron como fuente y se leyeron sus versiones remotas. No se presenta esa copia parcial como un checkout limpio compilado.

Las evidencias locales de Actions se autenticaron comparando sus ZIP completos con los digests obtenidos directamente de GitHub; no se aceptaron solo por su nombre o por el handoff.

## Criterios de aceptación

| ID | Criterio | Evidencia verificada | Estado |
|---|---|---|---|
| AC-01 | Atlas real XYZ PNG, Santiago Centro, z14–16; pan/zoom | ZIP 64 PNG 256×256; 4/12/48 por zoom; cobertura recalculada independientemente; regeneración local idéntica; calles/parques inspeccionados visualmente; MapChecks y logs ambas APIs | CUMPLE |
| AC-02 | Primer mapa en instalación limpia sin red, biometría y punto SQLite | Workflow crea AVD sin snapshot e instala APK nuevo; seguridad previa no crea mapa; dumpsys sin red predeterminada; MapChecks comprueba ConnectivityManager, INTERNET denegado y archivo privado inexistente antes de abrir; fixture Service/SQLite (-33.45,-70.66), proveedor y render reales | CUMPLE |
| AC-03 | Solo movimientos georreferenciados de la cuenta; vacío y exterior | Consulta repository filtrada por cuenta; Ubicacion valida pares; fixture con/sin GPS y segunda cuenta; 2 marcadores exactos, cero en cuenta sin GPS, advertencia exterior y coordenadas originales conservadas | CUMPLE |
| AC-04 | Cancelación/error, background e intent directo no abren datos privados | MapaActivity hereda ActividadProtegida, no exportada; contenido/lectura/copia diferidos al éxito; 8 rutas internas, cancelación, dedo incorrecto, bloqueo, reentrada y proceso revisados en CI API24/API36; sin bypass de sesión | CUMPLE |
| AC-05 | Lifecycle, reapertura y atlas ausente/corrupto sin crash/red | onResume/onPause/onDetach; restauración de centro/zoom; MapChecks recrea, corrompe copia y comprueba error sin mapa/fallback; copia ausente se recupera del APK; archivo inexistente rechazado | CUMPLE |
| AC-06 | OSM/ODbL verificable, atribución permanente, sin tiles públicos | Extracto, IDs/versiones/referencias, URLs/fechas/hashes, avisos y licencia en fuentes/APK; render propio; TextView de atribución con enlace; adquisición vectorial separada de build/runtime | CUMPLE |
| AC-07 | Build exacto, dominio, SQLite y regresión T03–T06C | Actions run 39042930625: assembleDebug + assembleDebugAndroidTest retorno 0 en ambas APIs; 5483 checks de dominio por job; mapas/seguridad en ambas APIs, regresión financiera/GPS/migraciones en API36 | CUMPLE |
| AC-08 | APK, hashes, logs, pruebas y handoff | Artefactos autenticados por digest; APK y atlas contrastados; docs finales en 41b7787, handoff identifica SHA/run/limitaciones | CUMPLE |

La prueba offline exige desconexión antes de la primera apertura **del mapa**. El enrolamiento y otras comprobaciones previas del AVD no constituyen una visita cartográfica conectada. Los registros históricos de validación de red que conserva dumpsys no se confunden con una conexión activa.

## Revisión técnica

### Cartografía, procedencia y empaquetado

- `AtlasOffline` copia desde assets a filesDir mediante temporal y rename, valida SHA256, recuento y nombres antes del uso. Corrupción informa fallo local; no descarga ni elimina datos financieros.
- `MapaLocalView` entrega OfflineTileProvider al constructor de MapView y usa BitmapTileSourceBase LocalCashOSM. Esta adaptación evita crear un proveedor online temporal y conserva la decisión funcional del ADR. setUseDataConnection(false), zoom 14–16 y límites de cobertura.
- El renderizador lee únicamente XML OSM versionado; genera calles principales/secundarias, parques/agua y nombres. No hay petición de tiles en render/build/runtime. acquire_osm.py es mantenimiento separado.
- Fallback documentado: cuatro consultas pequeñas a la API oficial OSM tras fallos Overpass/límite de nodos. Se comprobaron 15023 nodos, 3279 ways, versiones e integridad de referencias. Las cuatro respuestas HTTP originales no están incluidas: sus hashes/fechas son registro de adquisición, no una recuperación histórica repetida por este Auditor. El extracto exacto conservado permite verificar íntegramente el render sin red.
- Datos declarados ODbL 1.0 y atribución concordante con la [fuente oficial OSM](https://www.openstreetmap.org/copyright), consultada el 17-10-2026. No se amplía esto a una certificación jurídica.
- El APK contiene el XML legible sin gzip; coincide exactamente con el extracto fuente descomprimido. APK-layout documenta la transformación de AAPT. El ZIP cartográfico conserva su ruta y bytes originales.

| Artefacto | Identidad comprobada |
|---|---|
| Atlas | 875101 bytes; SHA256 `f37bc75ae6e1c996d6527b411f805eb7bc304d898920a258d208d7acc605ab1d` |
| Extracto gzip | SHA256 `1aaa9a71b9453178345ac7ea47e273a66187a555368ecabe8ac1924408a3e27d` |
| XML fuente descomprimido / APK | SHA256 `8503f464f1fe48655a52b69eb75ca13a1a1069b5e79ee0798cfa65503e46f3b7` |
| APK API36 | SHA256 `c4161293f2b0f1bb7f145c5bb13ec7455b150ffff0d7867277a00a7a154384db` |

### Persistencia, biometría y alcance

MapaActivity obtiene cuenta/movimientos mediante MovimientoService; no contiene SQL ni escribe operaciones financieras. MovimientoRepository filtra por cuenta y reconstruye Ubicacion válida. SQLite permanece v4. No se modifica la captura GPS opcional.

ActividadProtegida crea bloqueo antes de repositorios/contenido. LocalCashApplication mantiene sesión efímera y la invalida al pasar a background; solo el callback productivo de BiometricPrompt autoriza acceso. FLAG_SECURE permanece activo. Las pruebas alimentan el sensor del AVD, sin reflexión, callback falso ni escritura de sesión. El mapa es interno no exportado y el extra authenticated=true no autoriza acceso.

Dependencias directas verificadas: AndroidX Biometric 1.1.0 y osmdroid 6.1.18; la única adición es osmdroid. minSdk24, Java/XML/Groovy intactos. Manifest elimina INTERNET y permisos de almacenamiento externos; ACCESS_NETWORK_STATE no permite descargar cartografía. No hay backend, Room, Compose, tracking ni geocoding.

## Evidencia ejecutada y límites

### GitHub Actions: ejecución remota revisada, no ejecución local del Auditor

[Run 39042930625](https://github.com/aik1a/LocalCash/actions/runs/39042930625), evento push, SHA funcional exacto, conclusión success. Se consultaron metadatos, jobs y logs originales de ambas APIs, además del workflow y las implementaciones de tests.

| Job | Resultado | Comprobaciones relevantes |
|---|---|---|
| validate (24), 117425906376 | PASS | Build 0; dominio 5483; BiometricPrompt compatible y 8 rutas; primer atlas offline; igualdad de píxeles z14/15/16; marcador dibujado; reapertura/rotación/cancelación |
| validate (36), 117425906567 | PASS | Lo anterior + T03 cuentas/saldos; T04 contactos/compras/atomicidad; T05 reparto/deudas; T06A permisos/GPS/timeout; migraciones v1/v2/v3→v4 y relanzamientos |

Los logs registran 587/505/516 colores en tiles centrales y 781 píxeles distintos al habilitar el marcador sobre MapView. Se inspeccionó visualmente el render autenticado API36 y el atlas regenerado: base cartográfica reconocible y marcador, sin sustituir FLAG_SECURE.

API24 registra retorno 137 de svc wifi disable. No se interpreta como éxito: el test exige después Active default network:none y ConnectivityManager desconectado. Ambos controles pasan. Los fallos transitorios de adb durante arranque del AVD no son fallos finales de build/pruebas.

Artefactos contrastados con los digests obtenidos de GitHub:

| Artefacto | Digest SHA256 del ZIP |
|---|---|
| [API24, 12104783307](https://github.com/aik1a/LocalCash/actions/runs/39042930625/artifacts/12104783307) | `21678a295baf3a94442b5dbcd94918aa2915e906cf5a962afb64621eb9f42557` |
| [API36, 12104643986](https://github.com/aik1a/LocalCash/actions/runs/39042930625/artifacts/12104643986) | `f82c1c7bc42f8f0e7c1795637ac62a8a3e8161143c7171a984f3d33ad1c5b058` |

Ambos contienen validated-sha exacto y exit-code 0. El APK API24 tiene hash distinto del API36; se identifica cada uno, sin afirmar intercambiabilidad de firmas.

### Validaciones independientes ejecutadas en Work

| Comando/comprobación | Resultado real |
|---|---|
| `python tools/maps/verify_atlas.py` sobre fuentes contrastadas | PASS, retorno 0 |
| `python tools/maps/generate_offline_tiles.py --output <temporal>/santiago_centro.zip --preview <temporal>/preview.png` con Pillow 12.3.0 | PASS, retorno 0 |
| `cmp` ZIP regenerado contra asset | PASS, retorno 0; identidad bit a bit |
| Comprobación independiente con zipfile/Pillow/XML, fórmula XYZ y hashes | PASS: 4/12/48 tiles, cobertura exacta, sin duplicados, 64 PNG válidos, nodos/ways/versiones/referencias completos y fuentes/assets iguales |
| Digest de ambos ZIP Actions contra API GitHub; lectura interna de SHA/retornos/APK | PASS |
| XML extraído del APK contra gzip descomprimido y atlas empaquetado contra fuente | PASS |
| Inspección visual del render autenticado y preview generado | PASS, cartografía reconocible y marcador visible |
| `bash tests/domain-checks.sh` | BLOCKED local: retorno 127, javac no instalado; no produjo resultado de pruebas Java |
| Build Gradle / emulador en Work | NO EJECUTADOS localmente: sin toolchain Android/adb disponible; no se atribuyen al Auditor los tests ejecutados por CI |

La evidencia CI exacta satisface el build/tests exigido; la limitación de repetición local no invalida resultados remotos verificables. No se ejecutaron fixtures destructivos sobre datos reales ni se probó un dispositivo físico.

## Matriz de calidad

Escala aplicada por dimensión; Q08 se marca parcial por H-01. No se usa el puntaje para compensar gates.

| ID | Dimensión | Peso | Estado | Puntos | Evidencia / observación |
|---|---|---:|---|---:|---|
| Q01 | Requisitos/aceptación | 25 | CUMPLE | 25 | AC-01–08; PDF, Task Brief y ADR |
| Q02 | Completitud funcional | 15 | CUMPLE | 15 | Flujo, vacío/exterior, error de atlas y lifecycle; sin stubs funcionales |
| Q03 | Arquitectura/POO | 15 | CUMPLE | 15 | UI sin SQL, Service/Repository existentes, entidades válidas, capas acotadas |
| Q04 | Calidad/mantenibilidad | 10 | CUMPLE | 10 | Clases de mapa separadas; validación/copia local; errores controlados; complejidad académica |
| Q05 | Persistencia/seguridad | 10 | CUMPLE | 10 | SQLite v4, sin escrituras del mapa; gate y FLAG_SECURE; offline explícito |
| Q06 | Build/tests/regresión | 15 | CUMPLE | 15 | CI exacta verificada y atlas repetido independientemente; límites locales explícitos |
| Q07 | Alcance/dependencias | 5 | CUMPLE | 5 | Solo osmdroid directo; sin cambio ADR/estado/reglas financieras |
| Q08 | Documentación/handoff | 5 | PARCIAL | 2,5 | Handoff/evidencia completos; documentación general desactualizada H-01 |

**Puntaje diagnóstico: 97,5 / 100.**

## Hallazgos

| ID | Severidad | Criterio | Evidencia | Tratamiento |
|---|---|---|---|---|
| H-01 | DOCUMENTATION, no bloqueante | Q08.2 | ARCHITECTURE.md conserva “siete Activities” y biometría como única dependencia nueva; ahora hay ocho rutas protegidas y osmdroid. BUILD.md sigue descrito como esqueleto T01 sin dependencias ni pruebas, aunque conserva su fecha histórica. | Actualizar guía técnica vigente y distinguirla del historial en la depuración documental de T07. Usar mientras tanto handoff/pruebas T06B y workflow exacto para compilación/evidencia. No requiere cambiar código ni repetir build. |

**BLOCKER: 0. MAJOR: 0. MINOR funcional confirmado: 0. OUT-OF-SCOPE: 0.**

No se reabre el hallazgo histórico homónimo H-01 de tareas anteriores; esta numeración es local a esta auditoría.

## Gates críticos

| Gate | Resultado | Base |
|---|---|---|
| Pilares obligatorios y requisitos pertinentes | PASS | Java, SQLite offline, biometría y mapa interactivo |
| Criterios esenciales T06B | PASS | AC-01–08 |
| Build requerido del SHA exacto | PASS | CI API24/API36, retorno 0 |
| Pruebas requeridas y regresiones | PASS | Mapas/biometría ambas APIs, T03–T06A API36, dominio ambos jobs |
| Arquitectura y ADR-002 | PASS | Proveedor/atlas local, fuentes/licencia, separación de responsabilidades |
| Integridad y seguridad | PASS | SQLite intacta, filtro por cuenta, gate sin bypass |
| Alcance/dependencias | PASS | Diff acotado, solo dependencia autorizada |
| Gate integral de sprint | N/A | No solicitado; S00 continúa abierto |

## Veredicto y entrega al Orquestador

**VEREDICTO: PASS WITH OBSERVATIONS.**

- **Commit funcional aceptable:** APPROVED_FOR_COMMIT.
- **Integración del SHA funcional auditado con documentación 41b7787:** **APPROVED_FOR_MERGE**.
- **Avance/cierre de sprint:** N/A; esta auditoría no lo autoriza.
- **Impedimentos técnicos conocidos para integrar T06B:** ninguno.
- **Correctivos obligatorios antes del merge:** ninguno.
- **Seguimiento no bloqueante:** H-01, documentación técnica general.
- **Evidencia principal:** run 39042930625, jobs/artefactos identificados arriba, evidencia versionada bajo docs/handoffs/evidence/S00-T06B/, reproducción independiente del atlas.
- **Alcance de la aprobación:** SHA `c289b0614d9d891c6ea4cfcdeec48dabc342c459`; cualquier cambio funcional posterior requiere evaluar el nuevo diff.

Persisten los límites ya aceptados: cobertura demo Santiago Centro z14–16, validación en AVD y no hardware físico, firma/actualización histórica preservando datos pendiente de T07. No se ejecutó una instalación de actualización sobre datos del usuario.

El único archivo que introduce el Auditor es este informe. No se modifica código, PROJECT_STATE.md ni ADR; no se hace merge, no se cierra T06B/S00 y no se inicia T07. El SHA del commit documental se entrega en la respuesta de cierre y se verifica contra su padre, para evitar una referencia autorreferente dentro del propio informe.
