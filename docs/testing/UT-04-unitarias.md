# UT-04 — Pruebas unitarias automáticas

Solicitud: añadir pruebas automáticas apropiadas para un MVP académico.

## Alcance y aceptación

Agregar JUnit 4 en `app/src/test/java`, ejecutable desde Gradle y Android Studio sin
emulador, sin cambiar reglas funcionales, esquema SQLite ni dependencias de ejecución.
Comprobar resultados concretos y rechazos de datos inválidos, incluyendo límites financieros.

## Ejecución

Desde la raíz de esta copia de trabajo, con JDK y SDK configurados:

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

En Android Studio: abrir `app/src/test/java/com/aik1a/localcash/domain`, hacer clic
derecho en el paquete y elegir **Run Tests**. También se puede ejecutar una clase
o un método usando el icono de ejecución junto a su nombre.

Informe generado: `app/build/reports/tests/testDebugUnitTest/index.html`.

## Cobertura

| Clase | Casos | Riesgos comprobados |
| --- | ---: | --- |
| DineroTest | 6 | Centavos exactos, formato CLP, precisión, entradas ambiguas, desbordamiento y moneda histórica desconocida |
| CuentaTest | 5 | Saldo con ingresos/egresos, saldo negativo, cuenta equivocada, movimiento cero/negativo y desbordamiento |
| RepartoTest | 8 | Resto determinista, precisión de moneda, compra planificada, duplicados, cuota cero y avisos de diferencia |
| CompraYDeudaTest | 7 | Cantidades, total, artículo gratuito, copia inmutable, desbordamiento y estados de deuda |
| UbicacionTest | 3 | Coordenadas límite, datos inválidos y movimiento sin ubicación |

Total: **29 pruebas**. No usan Android, SQLite, reloj, red ni aleatoriedad.

## Límites

Son pruebas de reglas de dominio. No acreditan captura GPS, visualización de avisos,
transacciones SQLite, persistencia del estado PAGADA ni prevención de pago duplicado.
Esas rutas mantienen sus pruebas de integración existentes en `app/src/androidTest`.
La migración directa v5→v7 sigue pendiente de una prueba de integración específica.
No se agrega infraestructura ni se cambia el APK por este incremento.

## Entrega al revisor

Archivos: cinco clases nuevas de pruebas, una dependencia `testImplementation` JUnit
4.13.2 en `app/build.gradle` y este documento. Implementación local sin push/merge
ni cierre de sprint. Verificación: `:app:testDebugUnitTest` PASS, 29 pruebas,
0 fallos y 0 omitidas (2026-11-03). Revisión independiente pendiente.
