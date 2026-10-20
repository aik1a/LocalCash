# Arquitectura de LocalCash

**Vigente: 2026-10-18, T07C.** Contraste sobre main `9084b573ebe055485e3cdda11ca8db2d8df6a3bc`, posterior a PR #10. Este documento describe lo implementado; no cambia decisiones ni declara cerrado S00. [Entrega académica](docs/delivery/S00-final-delivery.md).

## Estructura y responsabilidades

Aplicación Android nativa en Java con interfaz XML y Gradle/Groovy. No usa Firebase, backend, Room, Compose ni DI. Los datos financieros son locales; el mapa usa recursos incluidos en el APK.

| Paquete / componente | Responsabilidad real |
|---|---|
| presentation | Activities, XML, formularios, navegación, gate biométrico, captura opcional de ubicación y visualización del mapa |
| application | MovimientoService, CompraService y DeudaService coordinan operaciones de dominio y repositorios |
| domain | Cuenta, Movimiento, Compra, DetalleCompra, Contacto, Deuda, Reparto, Dinero y Ubicacion; estado privado e invariantes |
| data | CuentaRepository, MovimientoRepository, ContactoRepository, CompraRepository y DeudaRepository; SQL y mapeo |
| data.LocalCashDbHelper | SQLiteOpenHelper; creación/migraciones y claves foráneas; base localcash.db, versión 4 |

El flujo de una operación financiera es UI → servicio → dominio/repositorios → SQLiteOpenHelper → SQLite. La UI instancia helper/servicio, pero no ejecuta SQL ni calcula saldos/repartos. Los servicios usan el dominio y los repositorios según la operación; no es una cadena obligatoria de clases ni una arquitectura empresarial.

El mapa es una lectura adicional de movimientos por cuenta: MovimientoService → movimientos con Ubicacion → marcadores de osmdroid. CapturaUbicacion usa LocationManager antes de registrar un movimiento; abrir el mapa no solicita GPS.

## Ocho rutas protegidas

Todas estas Activities heredan de ActividadProtegida y difieren la creación del contenido financiero hasta una sesión autenticada:

| Activity | Contenido | Exportada |
|---|---|---|
| MainActivity | Cuentas, saldo y acceso a contactos/compras | Sí, entrada MAIN/LAUNCHER |
| CuentaActivity | Historial, registro de ingresos/egresos y entrada al mapa | No |
| ContactosActivity | Agenda local | No |
| ComprasActivity | Historial de compras | No |
| NuevaCompraActivity | Compra realizada o planificada e ítems | No |
| CompraDetalleActivity | Detalles y acceso al reparto | No |
| RepartoActivity | Participantes, distribución y deudas | No |
| MapaActivity | Movimientos georreferenciados de la cuenta | No |

AccesoActivity es la novena Activity declarada y contiene el desafío: **no es una novena ruta financiera**. Extiende FragmentActivity y usa AndroidX Biometric 1.1.0, BiometricPrompt y BIOMETRIC_STRONG. La compatibilidad minSdk 24 se obtiene mediante AndroidX; las dependencias transitivas incluyen soporte Fragment/AppCompat.

LocalCashApplication conserva la sesión solo en memoria; la invalida al quedar sin Activities visibles fuera de un cambio de configuración. Segundo plano y muerte de proceso exigen nuevo desafío. La rotación no otorga una sesión por sí misma. Cancelación, error, lockout, biometría no registrada o hardware incompatible mantienen el acceso cerrado. No hay PIN propio, credencial alternativa, callback de éxito de pruebas ni almacenamiento de huellas.

FLAG_SECURE se mantiene en ventanas y diálogos privados. El contenido se oculta al pausar; los diálogos se descartan al detener la Activity. Los intents internos también pasan por el gate, aunque la entrada principal sea exportada. Esto protege el acceso de UI; **no implica cifrado de SQLite ni una auditoría de seguridad exhaustiva**. [Pruebas biométricas/integración](docs/testing/S00-T07A.md).

## SQLite v4 y reglas implementadas

SQLite es la única fuente persistente financiera. Seis tablas: cuentas, movimientos, contactos, compras, detalle_compra y deudas. Las coordenadas son dos columnas nullable de movimientos; no una tabla adicional. [DDL, constraints y migraciones](docs/design/DATABASE.md).

- FK activas; RESTRICT conserva relaciones. Compra con ítems y reparto de deudas usan transacciones.
- Dinero en centésimas enteras long; saldo derivado por Cuenta.calcularSaldo y total por Compra.calcularTotal; no saldo/total modificable redundante.
- Movimiento es inmutable: ubicación opcional en constructor, tieneUbicacion y getMontoConSigno. No existe asignarUbicacion como operación vigente.
- Reparto equitativo distribuye resto por ID ascendente; personalizado exige suma exacta. Mínimo dos contactos distintos seleccionados explícitamente.
- Deuda.getEstado devuelve PENDIENTE; no se implementa marcarComoPagada ni pagos parciales.
- Compras realizadas/planificadas y repartos no generan movimientos automáticos. No hay notificaciones ni edición/borrado general.

Las migraciones internas v1/v2/v3→v4 fueron probadas en API36. No equivalen a una actualización de APK con otro certificado.

## Ubicación y cartografía offline

La ubicación es opt-in por movimiento. Puede denegarse permiso, retirarse consentimiento o guardarse sin fix. El timeout es de 15 segundos. El mapa no depende de que el movimiento tenga ubicación; los movimientos sin coordenadas no producen marcador.

MapaLocalView extiende MapView y recibe OfflineTileProvider desde el constructor XML, sin crear un proveedor descargador. setUseDataConnection(false), fuente LocalCashOSM y ZIP privado validado por SHA256. AtlasOffline copia exclusivamente desde assets/maps/santiago_centro.zip a files/maps/santiago_centro.zip. Un archivo corrupto produce error local, sin fallback online.

| Propiedad | Valor |
|---|---|
| Dependencia | org.osmdroid:osmdroid-android:6.1.18 |
| Cobertura | Santiago Centro, sur -33.465 / oeste -70.675 / norte -33.435 / este -70.645 |
| Centro / zoom | -33.450, -70.660 / niveles 14–16 |
| Atlas | 64 PNG RGB 256×256; LocalCashOSM/{z}/{x}/{y}.png, XYZ |
| SHA256 ZIP | f37bc75ae6e1c996d6527b411f805eb7bc304d898920a258d208d7acc605ab1d |
| Datos/licencia | © OpenStreetMap contributors, ODbL 1.0; render propio |

Marcadores filtrados por cuenta; coordenadas exteriores no se alteran y generan aviso, sin prometer cartografía fuera de cobertura. Se implementan zoom, desplazamiento, detalle al tocar, centrado, vacío y lifecycle. La atribución permanece visible; el enlace de licencia abre una aplicación externa si se pulsa y no es necesario para operar offline.

No se descargaron tiles de tile.openstreetmap.org. Fuente OSM vectorial, fallback aprobado y reproducción en [procedencia](docs/maps/S00-T06B-data-provenance.md) y [ADR-002](docs/design/decisions/ADR-002-osmdroid-cartografia-offline.md). El extracto y los avisos se incluyen en el APK; [disposición exacta de assets](docs/maps/APK-layout.md).

## Dependencias, permisos y límites

Dependencias directas: AndroidX Biometric 1.1.0 y osmdroid 6.1.18. El manifest declara USE_BIOMETRIC, USE_FINGERPRINT hasta API27, FINE/COARSE_LOCATION y ACCESS_NETWORK_STATE. INTERNET y permisos de almacenamiento externo se eliminan del manifest combinado; mapa y SQLite usan almacenamiento privado.

[Build y ejecución](docs/delivery/BUILD.md) documenta versiones exactas. Build, biometría y mapa offline PASS en emuladores API24/API36; regresión financiera/GPS completa solo API36. Dispositivo físico, GPS exclusivamente aproximado y Sync Studio limpio: NOT TESTED.

[T07B](docs/testing/S00-T07B.md): diez certificados principales históricos diferentes, ningún par compatible, actualización in-place BLOCKED_SIGNER_MISMATCH y conservación SQLite tras upgrade NOT TESTED. No desinstalar instalaciones con datos para superar ese bloqueo.

## Antecedente histórico preservado

El siguiente texto es la versión anterior a T07C. Sus candidatos, siete rutas, métodos esperados y dependencias futuras **no describen el estado vigente**; se conserva para continuidad, sin convertir propuestas en funciones implementadas.

<details>
<summary>Arquitectura anterior a T07C — fuente histórica íntegra</summary>

# Arquitectura de LocalCash

**Estado:** aprobada como arquitectura base; detalles físicos de BD sujetos a cierre en `docs/design/DATABASE.md`.

## Objetivo arquitectónico

Mantener una aplicación Android Java sencilla, demostrable y mantenible, con responsabilidades separadas sin introducir una arquitectura empresarial innecesaria.

## Capas

```text
Presentation
Activities / Fragments / XML / RecyclerView
        |
        v
Application / Services
(coordinación de operaciones cuando sea necesaria)
        |
        v
Domain
(entidades, value objects, invariantes y comportamiento)
        |
        v
Repositories
(CRUD y consultas de persistencia)
        |
        v
SQLiteOpenHelper
        |
        v
SQLite
```

Componentes transversales:

```text
BiometricPrompt -> controla acceso a la aplicación
Android Location -> Ubicacion -> Movimiento -> osmdroid
```

## Dependencias permitidas

| Desde | Hacia | Regla |
|---|---|---|
| UI | Services/Domain | Permitido según flujo |
| UI | Repository | Evitar; usar lógica de aplicación cuando corresponda |
| UI | SQLite | Prohibido |
| Service | Domain | Permitido |
| Service | Repository | Permitido |
| Domain | SQLite | Prohibido |
| Domain | UI Android | Prohibido |
| Repository | SQLiteOpenHelper | Permitido |

## Presentación

Responsabilidades:
- renderizar información;
- capturar interacción del usuario;
- validación superficial de formulario;
- invocar operaciones de aplicación/dominio;
- mostrar errores/resultados.

No debe ejecutar SQL, calcular saldos/deudas ni modificar internamente entidades saltándose su API pública.

## Dominio y POO

El encapsulamiento no se considera satisfecho solo por usar `private`. Las entidades protegen invariantes y exponen operaciones con significado de negocio.

Ejemplos esperados:
- `Cuenta.calcularSaldo(...)`
- `Movimiento.getMontoConSigno()`
- `Movimiento.asignarUbicacion(...)`
- `Compra.calcularTotal()`
- `Deuda.marcarComoPagada()`

Evitar setters genéricos como `setSaldo`, `setTotal` o `setEstado` cuando permitan saltarse reglas.

## Services

Crear Services solo si coordinan varias entidades/repositorios o una operación de negocio que no pertenece naturalmente a una sola entidad. No crear un Service por cada clase por simetría.

Candidatos actuales:
- `MovimientoService`
- `CompraService`
- `DeudaService`

Su necesidad exacta debe confirmarse durante implementación.

## Repositories

Centralizan persistencia y mapeo entre SQLite y objetos Java. SQL no debe dispersarse en Activities/Fragments.

Candidatos:
- `CuentaRepository`
- `MovimientoRepository`
- `ContactoRepository`
- `CompraRepository`
- `DeudaRepository`

## SQLite

Un `LocalCashDbHelper` basado en `SQLiteOpenHelper` administra creación y versión de la base. No contiene reglas de negocio.

SQLite es la única fuente persistente de verdad en esta versión. Véase `docs/design/decisions/ADR-001-local-sqlite.md`.

## Biometría

`AccesoActivity` utiliza `androidx.biometric:biometric:1.1.0` (`BiometricPrompt`, `BIOMETRIC_STRONG`) para mantener compatibilidad con minSdk24. Es la única dependencia directa nueva; `FragmentActivity` y el tema AppCompat proceden de sus dependencias transitivas.

Las siete Activities de datos heredan de `ActividadProtegida`: muestran bloqueo y difieren la creación de contenido/repositorios hasta autenticar. Las rutas internas también verifican sesión; solo la entrada principal es exportada. `LocalCashApplication` mantiene la sesión exclusivamente en memoria y la invalida al quedar toda la app en segundo plano. Reiniciar el proceso exige otro desafío. La rotación conserva el desafío del SDK sin conceder acceso.

Cancelación, errores, bloqueo del sensor o ausencia de biometría mantienen el acceso cerrado y permiten reintentar. No hay PIN, credencial propia ni fallback abierto. LocalCash no almacena huellas ni plantillas; SQLite continúa en v4. Las ventanas usan `FLAG_SECURE`, el contenido privado se oculta al pausar y los diálogos se descartan al detener la pantalla. Las pruebas usan enrolamiento Android y el sensor del emulador, sin desactivar el gate.

## Mapas y ubicación

La ubicación de un movimiento es opcional. En dominio se representa mediante `Ubicacion`; en SQLite puede persistirse como latitud/longitud nullable dentro de `movimientos`. osmdroid representa espacialmente esas coordenadas, no es una capa de persistencia.

## Principio de simplicidad

No introducir patrones o capas únicamente para demostrar conocimiento. Cada abstracción debe resolver un problema observable del proyecto.
</details>
