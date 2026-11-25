# Arquitectura de LocalCash

Android nativo con Java, vistas XML y Gradle Groovy. La interfaz se organiza en Activities protegidas por la sesión Google y la autenticación biométrica.

| Componente | Responsabilidad |
|---|---|
| `presentation` | Pantallas, formularios, navegación, biometría, ubicación y mapas |
| `application` | Coordinación de movimientos, compras, pagos y deudas |
| `domain` | Entidades financieras, importes y reglas de negocio independientes de Android |
| `data` | Repositorios y `LocalCashStore`, acceso a Firestore y observación de cambios |

## Identidad y datos

`AccesoActivity` obtiene la credencial Google y autentica en Firebase. `LocalCashApplication` conserva la autorización biométrica de la sesión local. `ActividadProtegida` espera la carga inicial, oculta las vistas al bloquearse la sesión y evita reutilizar contenido de otro UID.

`LocalCashStore` escucha las cinco colecciones bajo `users/{uid}` y utiliza la caché persistente del SDK. Los repositorios reconstruyen las entidades del dominio. El almacén diferencia datos locales y escrituras confirmadas por el servidor. Los rechazos observados se notifican mediante un aviso conservado por UID.

## Operaciones financieras

`CompraService.pagar` vincula la compra a su egreso mediante un lote Firestore. El identificador del movimiento de pago es estable para la compra. Los saldos se calculan desde el saldo inicial y el historial de movimientos. Los contactos y repartos pertenecen al propietario de los datos; no conceden acceso a otras cuentas Google.

## Mapas

`MapaActivity` obtiene los movimientos del mismo `LocalCashStore` de la sesión y actualiza sus marcadores al recibir cambios. `MapasVectoriales` prepara los archivos Mapsforge incluidos en el APK y permite importar otro mapa mediante el selector de documentos Android. `UbicacionMapa` solicita una posición actual con consentimiento. La cartografía mundial es básica; Chile incluye detalle de calles. El mapa no descarga teselas durante el uso.

## Código de pruebas

`app/src/test` contiene pruebas JUnit del dominio. `app/src/androidTest` contiene el runner de comprobaciones Firestore. `app/src/legacySqliteAndroidTest` conserva fuentes de pruebas de las versiones antiguas, fuera del runner Firebase actual.
