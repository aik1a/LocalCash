# LocalCash — paquete académico de entrega

**Preparado por Codificador T07C, 2026-10-18. Fecha objetivo: 19/10/2026.** Índice de entrega y checklist verificable; no es gate T07D, cierre S00 ni afirmación de pruebas físicas.

## Qué entregar

| Elemento | Contenido y ubicación |
|---|---|
| Código completo | [Repositorio GitHub](https://github.com/aik1a/LocalCash); main base 9084b573ebe055485e3cdda11ca8db2d8df6a3bc posterior PR #10. El código queda en Git, no se sustituye por el APK |
| Arquitectura | [Documento canónico](../../ARCHITECTURE.md), con ocho rutas protegidas y antecedentes |
| SQLite | [DDL y migraciones v4](../design/DATABASE.md); resumen debajo |
| Compilación y ejecución | [BUILD](BUILD.md), versiones, wrapper, dependencias, APK e instalación segura |
| Exposición | [Guion por módulos](PRESENTATION.md), datos de ejemplo y expectativas, sin afirmar presentación realizada |
| Trazabilidad | [Requisitos → código → prueba](../requirements/TRACEABILITY.md) y checklist docente debajo |
| Pruebas | [T07A](../testing/S00-T07A.md), [T07B](../testing/S00-T07B.md) y evidencias originales CI |
| Licencia cartográfica | [Procedencia/reproducción OSM](../maps/S00-T06B-data-provenance.md), [assets exactos](../maps/APK-layout.md) y avisos incluidos en el APK |
| Continuidad | [Handoff T07C](../handoffs/S00-T07C-coder.md); auditoría independiente aún pendiente |

El repositorio es privado. Dar acceso autorizado al docente o facilitar una copia del código desde GitHub; un enlace sin acceso no equivale a entrega recibida. El paquete descargable complementario contiene APK, guías, PDF fuente y evidencia seleccionada; no sustituye el repositorio de código completo ni certifica recepción/exposición.

## APK principal seleccionado

| Dato | Valor |
|---|---|
| Procedencia | T07A CI [39046939759](https://github.com/aik1a/LocalCash/actions/runs/39046939759), job API36 117437427132 |
| SHA fuente compilado | 32dd6e0ff09143bfc55276d5084c18472769b4af |
| Descarga | [Artefacto 12105649198](https://github.com/aik1a/LocalCash/actions/runs/39046939759/artifacts/12105649198) |
| Archivo dentro del ZIP | app/build/outputs/apk/debug/app-debug.apk |
| SHA256 APK | f6c188ae06db78a6068b64b69157b4fcfe776b6009561a1a0e9269086e9a83d0 |
| Certificado SHA256 | 49d73d9652d1cbf9c9891fb90a3d01fc94a3f2a6dcb02aea8a20471b927f191a |
| Package / versionCode / versionName | com.aik1a.localcash / 1 / 1.0 |
| SDK mínimo / objetivo | 24 / 36 |
| Tipo | Debug, no release de distribución |
| ZIP Actions SHA256 | 9ab61141e5c36e2770b9a870136c1b4941dd99c65cd6164ed29c7daa14d918ab |

API36 identifica el emulador de validación; no restringe la instalación del APK a API36. **No entregar app-debug-androidTest.apk como aplicación.** T07C conserva este APK sin recompilar/re-firmar. Una nueva compilación debug puede tener otro certificado y hash.

Conservar los artefactos antes de su caducidad: ZIP T07A hasta enero de 2027; consolidado T07B hasta 2027-01-16 UTC. El ZIP académico adjunto se identifica mediante su SHA256 en el mensaje de entrega; no se inventa una URL permanente a un binario que no está en Git.

## Instalación segura

Seguir [BUILD, instalación y firma](BUILD.md) y [T07B](../testing/S00-T07B.md). Nueva instalación: Android ≥24 y biometría fuerte enrolada en Ajustes; verificar hash y ausencia del package antes de `adb install app-debug.apk`. El APK académico complementario tiene nombre LocalCash-debug.apk, pero sus bytes son los mismos.

Una instalación existente requiere contrastar su certificado. **Diez certificados principales históricos diferentes; ningún par histórico compatible entre los inventariados. Actualización in-place: BLOCKED_SIGNER_MISMATCH. Conservación SQLite después de actualizar: NOT TESTED.** No desinstalar ni borrar datos para instalar esta entrega sobre una app con datos. No se añade firma estable, clave privada, exportación/importación o migración alternativa.

El desafío es biométrico Android. Sin biometría compatible registrada el contenido permanece bloqueado; no hay acceso por PIN propio. Con el APK instalado, cuentas, compras, deudas y mapa no necesitan red. El mapa incluye cartografía Santiago Centro z14–16. GPS es opcional; si no se captura ubicación, el movimiento se guarda sin marcador. La preparación del SDK/Gradle sí puede necesitar red.

## SQLite y arquitectura para el informe técnico

Presentation Java/XML → servicios de aplicación → dominio/repositorios → SQLiteOpenHelper → SQLite. UI sin SQL, repositorios con persistencia, entidades con invariantes. [Detalle y reglas de dependencia](../../ARCHITECTURE.md).

| Tabla v4 | Propósito y relaciones |
|---|---|
| cuentas | Nombre y saldo inicial; saldo actual derivado de movimientos |
| movimientos | FK cuenta; ingreso/egreso, monto, descripción, fecha; latitud/longitud opcionales en par válido |
| contactos | Directorio local; nombres e IDs |
| compras | Realizada/planificada, fecha y contacto opcional |
| detalle_compra | FK compra; producto/servicio, cantidad y precio; total derivado |
| deudas | PK compra/contacto, FK a ambos, monto no negativo, estado PENDIENTE |

Datos monetarios en centésimas enteras, FK activas y transacciones para compra/ítems y reparto. Migraciones v1→compras/contactos v2→deudas v3→coordenadas v4 son aditivas y probadas internamente en API36. No confundir esa validación con un upgrade de APK.

El mapa usa OfflineTileProvider y ZIP de 64 PNG. SHA256 del atlas: f37bc75ae6e1c996d6527b411f805eb7bc304d898920a258d208d7acc605ab1d. Datos © OpenStreetMap contributors, ODbL 1.0, avisos y extracto XML incluidos. No hay descarga de tiles/fallback online. Fuera de cobertura se advierte y se conservan coordenadas.

## Evidencias que acompañan el paquete

| Evidencia | Resultado y alcance |
|---|---|
| [CI T06B 39042930625](https://github.com/aik1a/LocalCash/actions/runs/39042930625) | Base cartográfica real, marcadores y biometría API24/API36; [auditoría](../audits/S00/S00-T06B-audit.md) |
| [CI T07A 39046939759](https://github.com/aik1a/LocalCash/actions/runs/39046939759) | Ambos assemble PASS, retorno 0; 5483 checks por plataforma; instalación limpia, biometría/mapas ambas APIs; finanzas/GPS/migraciones completas API36 |
| [CI T07B 39082526083](https://github.com/aik1a/LocalCash/actions/runs/39082526083) | apksigner/aapt: 14 APK, 28 retornos 0; diez firmantes principales distintos, 90 pares incompatibles |
| [Auditoría T07A](../audits/S00/S00-T07A-audit.md) | PASS WITH OBSERVATIONS; alcance de gate T07A |
| [Auditoría T07B](../audits/S00/S00-T07B-audit.md) | PASS WITH OBSERVATIONS del diagnóstico; no afirma actualización exitosa |
| T07C | Validación de documentos/rutas/hashes y correspondencia; no nueva ejecución Android ni autoauditoría |

Los PNG de tiles y el render de MapView autenticado son evidencia alternativa conservando FLAG_SECURE. No equivalen a prueba física ni a capturas de todas las pantallas.

## Checklist contrastado con el PDF docente

Fuente: **LocalCash Proyecto Android.pdf**, 7 páginas físicas, Benjamín Mora Torres. SHA256 del original consultado: `eea19ac075c5105b9a6c24f2c6d925ecb273c6092a2f97c8c01e58f800f7c199`. Páginas 3–4: resumen/objetivos; 5–6: módulos; 6: entregables y cuatro pilares. Las descripciones siguientes son correspondencias resumidas, no nuevos requisitos.

| Comprobación docente | Código/material contrastado | Estado real |
|---|---|---|
| Java nativo Android Studio | app Java, XML, Gradle; BUILD | Implementado; build CLI PASS. Sync Studio limpio NOT TESTED |
| SQLite offline | Helper v4, seis tablas, sin servidor | Implementado y probado AVD API36; mapas/biometría ambas APIs |
| Autenticación biométrica | AndroidX BiometricPrompt y ocho rutas | PASS en sensores de emulador; físico NOT TESTED |
| Interacción con mapas local | osmdroid, atlas OSM, zoom/pan/detalles | PASS AVD API24/API36; cobertura Santiago Centro z14–16 |
| Agenda de contactos | Crear/listar, vínculo de compra y selección de reparto | PASS automatizado API36 |
| Cuentas, ingresos/egresos y ubicación opcional | Dos cuentas, saldo derivado, opt-in GPS/NULL | PASS completo API36; completo API24 NOT TESTED |
| Historial y compras futuras | Detalles, estado/fecha y total | PASS API36; sin recordatorio/ejecución automática |
| Compra con contacto | Asociación local opcional | Implementado; no envío de notificaciones |
| Gastos compartidos y deudas | Ítems, participantes, total equitativo/personalizado | PASS API36; sin pagos parciales ni marcar pagada |
| Repositorio GitHub completo | app, fuentes/recursos, wrapper y tests | Disponible con permisos de acceso; recepción docente no comprobada |
| Documento técnico: arquitectura/SQLite/dependencias/build | Guías enlazadas e historia preservada | Preparado T07C; auditoría pendiente |
| Pauta de exposición y demo offline | PRESENTATION y datos de ejemplo | Preparada; exposición y recorrido humano NOT TESTED |

El PDF no exige específicamente un APK release, una firma histórica común ni pagos parciales; T07B sí documenta el riesgo operativo real de actualización. No se deduce privacidad absoluta o cifrado a partir del resumen docente. El registro conjunto de compra con contacto es local y no constituye sincronización remota.

## Pendientes explícitos para T07D

| Escenario | Estado |
|---|---|
| Regresión financiera/GPS completa API24 | NOT TESTED |
| Dispositivo y sensor biométrico físicos | NOT TESTED |
| GPS exclusivamente aproximado | NOT TESTED |
| Sync Android Studio desde entorno limpio/caché vacía | NOT TESTED |
| Upgrade histórico conservando datos | NOT TESTED; bloqueo de firma diagnosticado |
| Exposición/recorrido UI manual humano | NOT TESTED |
| Auditoría T07C / gate integral T07D | Pendientes; este documento no los sustituye |

S00 permanece abierto. El Orquestador decide auditoría, integración, cierre administrativo y gate posterior.
