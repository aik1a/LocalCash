# LocalCash Firebase MVP — código integrado

Conserva Java/XML, pantallas, navegación, monedas, importes enteros, biometría y mapa offline. Google identifica al propietario; Firestore guarda configuración, cuentas, movimientos, contactos y compras con detalles/reparto/deudas. No importa historial SQLite ni borra su archivo anterior.

## Abrir y compilar

Abrir la carpeta del proyecto en Android Studio. Colocar la configuración cliente real del proyecto `localcash-f6298` en `app/google-services.json`; está ignorada por Git. La SHA del APK debe estar registrada para Google. La configuración cliente no contiene claves administrativas; no añadir keystores o tokens al repositorio.

Comando: `gradlew.bat :app:assembleDebug :app:assembleDebugAndroidTest :app:testDebugUnitTest` en Windows. La compilación integrada y 29 unitarias se comprobaron en el entorno de desarrollo; no equivale a prueba real de Google o del recorrido offline.

## Preparación Firebase pendiente

Crear Cloud Firestore en el proyecto de desarrollo, mantener plan sin costo y publicar las reglas de `firestore.rules` antes de probar la app. `firebase.json` identifica esas reglas. No desplegar las reglas anónimas de `tests/firebase/`, exclusivas del prototipo local.

Primero entrar conectado con Google y biometría; esperar descarga completa. Después probar con datos sintéticos creación, saldo, compra, pago y reparto; desconectar, cerrar/reabrir, reconectar y contrastar. Cambios pendientes se distinguen de confirmación remota. No probar reemplazando/desinstalando una app con datos.

## Límites pendientes de validación final

Google real, navegación/biometría en dispositivo, reinicio offline, rechazos y concurrencia financiera no están acreditados. Reglas aíslan UID y validan formas principales; sumas/participantes de mapas variables de reparto se validan en dominio Java y no se certifica protección completa ante escrituras arbitrarias. No es una certificación de distribución.

GitHub Actions compila y ejecuta unitarias con configuración **sintética**: su APK sirve para verificar compilación y no para entrar con Google real. Para ejecución usar la configuración cliente real y firma registrada. La instrumentación SQLite histórica se conserva fuera del runner Firebase; no se reporta como aprobada para el backend nuevo.
