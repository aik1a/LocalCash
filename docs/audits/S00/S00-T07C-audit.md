# Auditoría — S00-T07C: documentación y paquete académico

## Identificación

- Rol: Agente Auditor independiente. No implementó T07C ni corrigió sus documentos.
- Fecha: 2026-10-18, America/Santiago.
- Repositorio: aik1a/LocalCash.
- Rama autorizada: task/S00-T07C-documentacion-entrega.
- SHA final auditado: `b73ceee93bf527ea8d2a1eaa1e733a4ffbc41c8f`.
- Main y merge-base consultados: `9084b573ebe055485e3cdda11ca8db2d8df6a3bc`.
- Task Brief: docs/tasks/active/S00-T07C-documentacion-entrega.md.
- Handoff: docs/handoffs/S00-T07C-coder.md.
- Tipo: única auditoría de tarea; no gate integral T07D ni cierre de S00.
- El SHA del único commit de publicación se entrega fuera de este documento para evitar autorreferencia.

## Alcance y método

Lectura directa desde GitHub del router, AGENTS, PROJECT_STATE, rol AUDITOR, WORKFLOW, TASK_LIFECYCLE, CONTEXT_PACKS, AUDIT_MATRIX, AUDIT_TEMPLATE, brief T07C, sprint S00, PROJECT_SPEC, TRACEABILITY, auditorías T07A/T07B y handoff T07C. Se revisaron los documentos de entrega y las cinco evidencias solicitadas: validate_documentation.py, validation-result.json, source-checks.json, tree-paths.json y S00-package-manifest.json.

Se consultó el PDF docente original local, 7 páginas, con extracción de texto y render/inspección de la página 6. Su SHA256 coincide con el declarado. La imagen arquitectónica adjunta no se usa como prueba de implementación: prevalecen el código y la arquitectura versionada.

Contraste estático de manifest, Gradle/wrapper, workflow T07A, helper SQLite, servicios/repositorios/dominio y Activities pertinentes, particularmente seguridad, compra/reparto y mapa. Recuperación independiente del árbol Git, metadatos originales de runs/artefactos y ZIP API36 de Actions. Materialización del ZIP académico entregado para inspeccionar bytes, contenido y hashes.

La clonación HTTPS sin credenciales locales no estuvo disponible; las lecturas y la publicación usan la conexión GitHub autorizada. No se ejecutó build, Android Studio, adb, emulador ni un recorrido humano nuevo. No se atribuye ejecución Android al SHA documental T07C.

## Diff y correspondencia de fuentes

La comparación main→SHA auditado da ahead=1, behind=0 y exactamente 13 archivos: arquitectura raíz/enlace canónico, BUILD, PRESENTATION, TRACEABILITY, índice/manifiesto de entrega, handoff y evidencia documental T07C. No modifica código productivo, tests Android, Gradle, PROJECT_SPEC, ADR ni PROJECT_STATE.

La rama apuntaba al SHA auditado antes de publicar este informe. Árbol recursivo Git completo: 540 entradas, truncated=false. Las 530 rutas del snapshot tree-paths.json existen en el árbol real; las diez entradas adicionales corresponden a archivos/directorio añadidos por T07C. El snapshot se identifica correctamente como árbol base.

Los ocho hashes de identidad de app, build.gradle, settings.gradle, gradle.properties, gradle, gradlew, gradlew.bat y tests coinciden entre manifiesto, árbol auditado y árbol del SHA compilado T07A `32dd6e0ff09143bfc55276d5084c18472769b4af`. La identidad de app cubre recursivamente fuentes y recursos Android. El manifest y app/build.gradle de source-checks.json coinciden íntegramente con los archivos recuperados directamente.

Las versiones históricas de ARCHITECTURE, BUILD y PRESENTATION se compararon con la base GitHub, no solamente con source-checks.json: las tres están preservadas íntegramente e identificadas como antecedentes.

## Criterios de aceptación

| ID | Criterio | Evidencia independiente | Estado |
|---|---|---|---|
| AC-01 | Arquitectura y BUILD actuales, historia conservada | Ocho Activities protegidas; helper v4; AndroidX/osmdroid; Gradle/manifest/workflow; tres originales íntegros | CUMPLE |
| AC-02 | Trazabilidad y requisitos docentes revisados | PDF original, PROJECT_SPEC y matriz actual: mismos 23 IDs, sin faltantes ni extras; contraste con código y evidencia original API36 | CUMPLE |
| AC-03 | Instalación y demo aplicables, límites transparentes | Guía de instalación nueva, detención si existe package, biometría y mapa local; guion propuesto, no ejecución manual acreditada | CUMPLE |
| AC-04 | Paquete académico verificable, APK/hash/evidencia | ZIP realmente inspeccionado; 93 hashes correctos, 23 archivos Git idénticos, APK/atlas/PDF/procedencia coincidentes | CUMPLE |
| AC-05 | Handoff documental completo | Rama/base, cambios, evidencia, origen del APK y limitaciones explícitas; sin cierre administrativo | CUMPLE |

ARCHITECTURE describe correctamente MainActivity, CuentaActivity, ContactosActivity, ComprasActivity, NuevaCompraActivity, CompraDetalleActivity, RepartoActivity y MapaActivity como subclases de ActividadProtegida. AccesoActivity es la novena Activity, con FragmentActivity y AndroidX BiometricPrompt/BIOMETRIC_STRONG. Se contrastaron creación diferida del contenido, sesión en memoria, invalidación al pasar a segundo plano y FLAG_SECURE.

SQLite v4 tiene seis tablas y coordenadas opcionales en movimientos. Servicios/repositorios sostienen las operaciones descritas; no se detectó SQL directo en presentation. Compra/reparto no generan movimientos automáticos; Deuda permanece PENDIENTE; no se documentan pagos, notificación remota ni reparto individual por ítem como funciones existentes.

MapaLocalView emplea OfflineTileProvider y desactiva conexión; AtlasOffline copia/valida el ZIP privado, sin fallback de red. Cobertura, z14–16 y hash son coherentes. La arquitectura distingue protección de UI de cifrado SQLite.

BUILD coincide con configuración real: AGP 9.3.2, wrapper 9.5.0, JDK CI 25/lenguaje 17, compileSdk 36.1, min/target 24/36 y dependencias AndroidX Biometric 1.1.0/osmdroid 6.1.18. Distingue reproducción del código de reproducción binaria/firma debug.

PRESENTATION propone resultados demostrables: saldo 125,50, total 100,01 y cuotas 33,34/33,34/33,33; GPS inyectado con longitud/latitud y saldo 126,50 posterior. La existencia del guion no acredita que alguien lo haya ejecutado. S00-final-delivery cubre los tres entregables del PDF p.6 y los cuatro pilares, sin convertir el APK en sustituto del código.

## Validación independiente y paquete

### Comprobador documental

Se ejecutó el script original sin modificarlo:

```sh
python review/docs/handoffs/evidence/S00-T07C/validate_documentation.py \
  --docs-root review \
  --apk /tmp/lc-artifact/app/build/outputs/apk/debug/app-debug.apk \
  --artifact /tmp/T07A-API36.zip \
  --pdf project_sources/01-LocalCash-Proyecto-Android.pdf \
  --output /tmp/lc-validator-result.json
```

Resultado reproducido: PASS, exit 0; siete Markdown, 55 enlaces internos sin roturas, 23 IDs, ocho rutas, tres antecedentes preservados. El JSON reproducido es igual al versionado.

El script usa snapshots aportados y assertions; por sí solo no autentica sus fuentes, no verifica cada ancla ni todos los HTTP externos, no inspecciona el ZIP académico ni prueba Android. Para no aceptar circularmente su PASS, se recalcularon los 55 destinos contra el árbol Git vivo, se compararon los 23 IDs directamente con PROJECT_SPEC y se contrastaron manifest/Gradle, árboles y CI originales. Todos concordaron.

### Procedencia y hashes

API original confirma completed/success y SHA exacto para:
- T06B: run 39042930625, c289b0614d9d891c6ea4cfcdeec48dabc342c459.
- T07A: run 39046939759, 32dd6e0ff09143bfc55276d5084c18472769b4af.
- T07B: run 39082526083, ece84d4cc7f3eee816f24626094597e43883e169.

El artefacto original T07A API36 12105649198 existe y no está expirado; metadatos de run/head/digest concuerdan. Caduca 2027-01-15T15:52:12Z.

| Objeto | SHA256 recalculado |
|---|---|
| ZIP académico LocalCash-S00-T07C-paquete-academico.zip | 7b0c4e443fc03bc4e8b7e3e62d1642d00d08a7454befd13c5231ff329badfda6 |
| ZIP original Actions API36 | 9ab61141e5c36e2770b9a870136c1b4941dd99c65cd6164ed29c7daa14d918ab |
| APK principal | f6c188ae06db78a6068b64b69157b4fcfe776b6009561a1a0e9269086e9a83d0 |
| Atlas dentro del APK | f37bc75ae6e1c996d6527b411f805eb7bc304d898920a258d208d7acc605ab1d |
| PDF docente | eea19ac075c5105b9a6c24f2c6d925ecb273c6092a2f97c8c01e58f800f7c199 |
| Certificado DER extraído del bloque APK v2 | 49d73d9652d1cbf9c9891fb90a3d01fc94a3f2a6dcb02aea8a20471b927f191a |

La extracción DER es comprobación de identidad del certificado, no nueva verificación criptográfica de la firma. Se contrastó con logs apksigner y auditoría T07B; no se ejecutó apksigner local.

### ZIP académico realmente inspeccionado

- 94 entradas, sin duplicados ni rutas absolutas/traversal; CRC correcto.
- SHA256SUMS.txt enumera los otros 93 archivos: todos existen y todos sus hashes coinciden; ningún archivo adicional sin declarar.
- 23 archivos con ruta versionada coinciden con el Git blob del SHA auditado. Incluyen documentos, manifiesto y evidencia T07C.
- LEEME y delivery-receipt identifican correctamente SHA documental, base y SHA compilado.
- LocalCash-debug.apk es idéntico al APK principal del ZIP Actions descargado independientemente. El API36.zip incluido también es idéntico al original.
- PDF empaquetado igual al original; atlas del APK coincide con manifiesto. Se verificaron además las 150 sumas internas de evidencia del ZIP original API36.
- Logs originales API36 contienen exit 0/SHA exacto, 5483 comprobaciones de dominio y marcadores financieros/compras/reparto/GPS/mapa. Respaldan lo descrito sin afirmar una ejecución nueva.
- El inventario T07B incluido tiene diez APK principales/diez certificados distintos y cero pares compatibles. Se recalcularon 90 pares dirigidos incompatibles y se contrastaron fingerprints/retorno 0 de los 14 logs apksigner empaquetados. No se vuelve a certificar aquí todo el inventario histórico por descarga de cada APK.
- Inspección recursiva: 13 archivos ZIP/APK, 2304 miembros, CRC correctos; búsqueda de archivos de claves/keystore/credenciales y patrones reconocibles de claves privadas/tokens: sin coincidencias. No se detectaron secretos; esta búsqueda no equivale a garantía exhaustiva de ausencia de cualquier dato sensible.

El paquete es complementario, no un checkout completo ni un sitio documental autónomo. Algunas referencias requieren el repositorio privado, tal como advierte LEEME. Acceso del docente y recepción efectiva: NOT VERIFIED. Eso no impide verificar el material para integración T07C, pero no debe declararse entrega recibida.

## Matriz de calidad

Aplicada al incremento documental, no a cobertura total del producto.

| ID | Dimensión | Peso | Estado | Evidencia |
|---|---|---:|---|---|
| Q01 | Requisitos/aceptación | 25 | CUMPLE | Cinco AC y PDF contrastados |
| Q02 | Completitud del incremento | 15 | CUMPLE | Guías, pauta, trazabilidad y paquete existente |
| Q03 | Arquitectura/POO documentada | 15 | CUMPLE | Código y ocho rutas/SQLite/mapa concordantes |
| Q04 | Mantenibilidad | 10 | CUMPLE | Validador reproducible, límites de su método explícitos |
| Q05 | Integridad/datos/seguridad aplicable | 10 | CUMPLE | Hashes, certificado, contenido y búsqueda de secretos; instrucciones conservan datos |
| Q06 | Validación requerida | 15 | CUMPLE | Script y comprobaciones independientes; build nuevo N/A |
| Q07 | Alcance/dependencias | 5 | CUMPLE | 13 archivos documentales; producción/configuración intactas |
| Q08 | Documentación/trazabilidad/handoff | 5 | CUMPLE | Correspondencia real y continuidad verificable |

Puntaje diagnóstico del alcance T07C: 100/100. No expresa cobertura Android, seguridad exhaustiva ni autorización de cierre S00; las observaciones heredadas y límites permanecen.

## Hallazgos clasificados

| ID | Severidad | Evidencia y efecto | Tratamiento |
|---|---|---|---|
| H-01 | MINOR, heredado T07A | Regresión financiera/GPS completa API24 no ejecutada; no defecto funcional demostrado. T07C lo mantiene correctamente | T07D debe valorar la cobertura pendiente; no reutilizar job verde como PASS completo |
| H-02 | MINOR, heredado T07A | Filtros push del workflow T07A omiten Gradle raíz y están ligados a su rama. BUILD lo advierte | Ejecutar dispatch para cambios pertinentes o asignar corrección autorizada; sin cambio aquí |

H-01 documental histórico de arquitectura/BUILD: resuelto en T07C; se distingue de H-01 de cobertura anterior. No se detectan nuevos defectos documentales bloqueantes. BLOCKER=0, MAJOR=0, OUT-OF-SCOPE=0. Sin correctivos obligatorios antes del merge T07C.

## Limitaciones obligatorias

| Escenario | Estado conservado |
|---|---|
| Regresión financiera/GPS completa API24 | NOT TESTED |
| Dispositivo físico y sensor físico | NOT TESTED |
| GPS exclusivamente aproximado | NOT TESTED |
| Android Studio Sync limpio | NOT TESTED |
| Recorrido manual humano/exposición | NOT TESTED |
| Actualización histórica entre APK inventariados | BLOCKED_SIGNER_MISMATCH |
| Conservación SQLite tras upgrade APK | NOT TESTED |

Las migraciones internas SQLite v1/v2/v3→v4 y la persistencia tras reinicio son evidencias diferentes de actualización entre APK. No hay instalación upgrade exitosa ni snapshot antes/después de ella. No desinstalar ni borrar datos para simular ese resultado.

## Gates y veredicto

| Gate | Resultado | Fundamento |
|---|---|---|
| Criterios esenciales T07C | PASS | AC-01 a AC-05 verificados |
| Build productivo nuevo | N/A | Sin cambio compilable |
| Comprobaciones documentales/paquete | PASS | Reproducción y contraste independiente de originales |
| Arquitectura/requisitos/ADR | PASS | Descripción concordante, sin cambios normativos |
| Integridad/seguridad aplicable | PASS con límites | Bytes y búsqueda de secretos contrastados |
| Alcance | PASS | Únicamente documentación/evidencia |
| Gate integral S00/T07D | Pendiente | No evaluado ni sustituido por esta auditoría |

**VEREDICTO: PASS WITH OBSERVATIONS.**

- **Commit del incremento auditado: APPROVED_FOR_COMMIT.**
- **Merge de T07C al SHA b73ceee93bf527ea8d2a1eaa1e733a4ffbc41c8f: APPROVED_FOR_MERGE.**
- **Cierre/avance de S00: NOT_APPROVED_FOR_SPRINT_ADVANCE.**

T07C puede integrarse y su material está suficientemente respaldado para presentarse al gate T07D. La aprobación no convierte limitaciones en pruebas realizadas ni acredita recepción docente.

## Recomendaciones concretas para T07D

1. Evaluar explícitamente los siete escenarios de la tabla anterior; cualquier aceptación con límites debe quedar razonada. No declarar upgrade/SQLite tras upgrade como PASS por migraciones internas.
2. Conservar el APK exacto y el ZIP académico con los hashes anteriores; verificar acceso del docente al código y a las evidencias antes de afirmar entrega completa recibida.
3. Si existe entorno disponible, ejecutar el recorrido humano del guion en AVD descartable; registrar escenario/resultado real. Equipo físico, GPS aproximado y Sync Studio requieren evidencia propia para cambiar NOT TESTED.
4. Valorar la necesidad de ampliar finanzas/GPS API24; si se exige, asignar tarea autorizada. No corregir infraestructura ni estrategia de firma silenciosamente.
5. Mantener cobertura cartográfica Santiago Centro z14–16, carácter debug del APK y ausencia de cifrado SQLite explícitos. No retirar FLAG_SECURE para obtener capturas.
6. El Orquestador formaliza integración/estado; el gate integral debe revisar el incremento completo y no inferirse de los PASS de T07A/T07B/T07C.

Solo se publica este informe bajo docs/audits. Sin correcciones del Codificador, cambios productivos, PROJECT_STATE, merge ni cierre de T07C/S00.
