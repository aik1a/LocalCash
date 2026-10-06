# LocalCash — Especificación técnica canónica

**Estado:** CANONICAL DERIVED SPEC  
**Fuente primaria:** documento del docente `LocalCash Proyecto Android.pdf`.  
**Regla:** este archivo traduce el encargo a requisitos implementables; no reemplaza el original.

## Propósito

Aplicación móvil Android nativa en Java, orientada a la gestión financiera personal y diseñada para operar localmente/offline.

## Pilares obligatorios

- **LC-P01 — Java nativo:** desarrollar en Android Studio utilizando Java.
- **LC-P02 — SQLite offline:** la funcionalidad principal y sus datos deben operar localmente mediante SQLite, sin dependencia de servidor externo.
- **LC-P03 — Biometría:** proteger el acceso mediante autenticación biométrica usando capacidades Android.
- **LC-P04 — Mapas:** permitir interacción con mapas para movimientos geolocalizados.

## Requisitos funcionales

### Autenticación y contactos

- **LC-AUTH-01:** solicitar autenticación biométrica antes de permitir acceso al contenido financiero.
- **LC-CON-01:** mantener una agenda/directorio local de contactos utilizables por compras y gastos compartidos.

### Cuentas y movimientos

- **LC-ACC-01:** crear y administrar múltiples cuentas de dinero.
- **LC-MOV-01:** registrar movimientos de tipo ingreso y egreso asociados a una cuenta.
- **LC-MOV-02:** permitir que un movimiento tenga coordenadas geográficas opcionales.
- **LC-MAP-01:** visualizar/explorar movimientos geolocalizados mediante un mapa.

### Compras

- **LC-BUY-01:** registrar compras y consultar su historial/detalle.
- **LC-BUY-02:** permitir programar adquisiciones futuras.
- **LC-BUY-03:** permitir vincular compras con contactos cuando corresponda.

### Gastos compartidos y deudas

- **LC-SHARED-01:** registrar compras compartidas detallando productos o servicios adquiridos.
- **LC-SHARED-02:** seleccionar participantes desde la agenda local.
- **LC-DEBT-01:** distribuir el costo entre participantes y obtener cuánto debe pagar cada uno.
- **LC-DEBT-02:** admitir una distribución equitativa o personalizada según el diseño funcional aprobado.

## Requisitos técnicos

- **LC-TECH-01:** persistencia implementada con SQLite en Android Studio.
- **LC-TECH-02:** autenticación mediante `BiometricPrompt`.
- **LC-TECH-03:** mapas mediante osmdroid o alternativa compatible con el requisito; decisión actual: osmdroid 6.1.18.
- **LC-TECH-04:** repositorio Git con código fuente completo.
- **LC-TECH-05:** documentación técnica de arquitectura, estructura SQLite, dependencias y compilación/ejecución.
- **LC-TECH-06:** material/estructura para exposición y demostración de módulos offline.

## Decisiones de implementación ya aprobadas

Estas decisiones son del proyecto, no requisitos literales del documento docente:
- XML para UI.
- Gradle con Groovy DSL.
- `SQLiteOpenHelper` para acceso a SQLite.
- osmdroid 6.1.18.
- arquitectura simple Presentation -> Services/Domain -> Repositories -> SQLiteOpenHelper -> SQLite.
- Firebase y backend remoto fuera de alcance.
- Room fuera de alcance actual.
- POO con encapsulamiento real y comportamiento de dominio.

## No inferir automáticamente

El documento fuente no define por sí solo:
- nombres físicos de tablas/columnas;
- diseño exacto de pantallas;
- reglas de borrado/cascada;
- todos los campos de contactos;
- fórmula/UX exacta de reparto personalizado;
- estructura definitiva de paquetes Java.

Estas decisiones deben quedar documentadas antes o durante el sprint que las necesite.