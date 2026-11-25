# Requisitos de LocalCash

Aplicación Android de finanzas personales en Java y XML. La versión actual sustituye la persistencia SQLite de la primera entrega por Firebase y mantiene la biometría como protección local.

## Funciones

- Identificación mediante Firebase Authentication con Google.
- Autorización biométrica antes de acceder a contenido financiero.
- Cuentas con nombre, divisa y saldo inicial.
- Movimientos de ingreso y egreso por cuenta, con ubicación opcional.
- Compras con productos, cantidades y precios, en estado planificado o realizado.
- Pago explícito de compra desde una cuenta de moneda compatible.
- Contactos para gastos compartidos y reparto equitativo o personalizado.
- Seguimiento del estado de deudas sin ejecutar transferencias bancarias.
- Consulta de movimientos geolocalizados, ubicación actual y cartografía offline.
- Persistencia Firestore por UID, caché y sincronización tras recuperar conexión.

## Tecnología

Java, vistas XML, Gradle Groovy, Firebase Authentication, Cloud Firestore, AndroidX Biometric y osmdroid con Mapsforge. Android mínimo API 24. El código financiero utiliza importes enteros y entidades independientes de la interfaz.

La cartografía básica cubre el mundo. El APK incorpora calles de Chile y permite importar mapas de otros países. El primer acceso y la preparación de datos financieros requieren conexión. El proyecto no ejecuta transferencias, cobros ni conversiones automáticas de divisa.
