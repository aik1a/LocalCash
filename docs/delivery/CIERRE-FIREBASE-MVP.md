# Comprobaciones para cerrar el MVP Firebase

Fecha: 2026-11-23. Base: `736f748db92cff20c8caf24f8deca5136147d057`. MVP académico, sin importación histórica ni rediseño.

## Evidencia actual

- Corrección publicada: `d533b5e444b580e9124114b0dfad903fdd1ed056`, `task/FB-final-validation`. [CI 45105532009](https://github.com/aik1a/LocalCash/actions/runs/45105532009): SUCCESS (compilación app/instrumentación y unitarias; configuración sintética). Terra contrastó ambos Java publicados con los revisados: aptos como WIP. Pruebas Android/Firebase real y aceptación final pendientes.
- Aviso de rechazo: conserva por UID los rechazos cuyo callback llegó con proceso vivo; la bandera sobrevive a reapertura una vez guardada. No acredita rechazo ocurrido después de morir el proceso original ni aviso individual por cada fallo agrupado.

- Copia recibida contrastada con los 589 archivos del árbol GitHub: 588 idénticos; gradlew.bat solo cambia saltos de línea.
- [CI 45102302173](https://github.com/aik1a/LocalCash/actions/runs/45102302173): SUCCESS, app/instrumentación/unitarias con Firebase sintético. No prueba Google real.
- Ejecución local: seis clases JUnit de dominio, 29 pruebas, cero fallos. Compilación directa javac y ejecución JUnitCore; no cubre Android/repositorios/Firebase.
- Configuración cliente real localizada y validada para proyecto/app/OAuth esperados, sin versionarla. No demuestra acceso a Firebase.
- Build Android local BLOCKED: Google Services 4.6.0 no disponible en caché offline. Gradle9.8.2/JDK25/SDK36.1 disponibles; red del entorno no disponible. No es un fallo demostrado del código.
- El baseline y las pruebas vigentes permiten saldo negativo; se retiró esa sospecha como defecto. No cambiar comportamiento financiero sin decisión explícita.

## Tres bloques restantes

| Bloque | Trabajo mínimo | Aceptación |
|---|---|---|
| Código y compilación | Conservar avisos de rechazo al volver del segundo plano; revisar y compilar el cambio | Código revisado/compilado; aviso exclusivo del UID afectado |
| Firebase y recorrido | Reglas productivas, Google real, biometría, cuentas/movimientos/compras/repartos/deudas con modo avión, cierre y reconexión | Datos y saldos conservados, pendientes claros, confirmación y rechazo visibles |
| Integridad y entrega | A/B/anónimo, pago repetido y dos clientes pagando misma compra; APK con firma registrada y documentación final | Sin exposición entre usuarios ni doble egreso; evidencia del mismo SHA y revisión independiente |

## Guion de aceptación

1. En instalación de pruebas, entrar con Google y biometría; cancelar mantiene bloqueo. Esperar descarga completa.
2. Crear datos sintéticos CLP/USD y anotar resultados esperados. Pagar compra una vez; un solo egreso. Reparto/deuda no alteran saldo.
3. Modo avión: consultar y modificar módulos, forzar cierre y reabrir. Conservar datos/pendientes tras biometría.
4. Reconectar y contrastar remoto. Repetir pago desde otro cliente sin segundo egreso.
5. Rechazo controlado de reglas: reversión y aviso en primer plano, al volver del segundo plano y tras reinicio. Confirmar aviso; no repetirlo sin otro rechazo.
6. Cambiar UID: datos y avisos del anterior no aparecen en el siguiente. Probar denegación A/B/anónimo con reglas productivas de desarrollo/emulador.

Registrar SHA, APK/firma, dispositivo/API y resultado. Lo no ejecutado sigue NOT TESTED; el prototipo offline aislado no acredita el recorrido financiero. No introducir colaboración, pagos reales ni nuevas capas para estos checks.

