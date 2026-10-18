# Guía de demostración académica

**T07C, 2026-10-18.** Guion propuesto para exposición; no afirma que se haya realizado una presentación o recorrido manual. La evidencia existente es automatizada en emulador. [Índice de entrega](S00-final-delivery.md).

## Preparación

Usar un AVD descartable API36 con el APK principal T07A seleccionado en [BUILD](BUILD.md), instalado como paquete nuevo. Nunca borrar una instalación con datos para preparar la demo. Enrolar huella en Ajustes Android; el sensor del emulador autentica mediante AndroidX, sin bypass. No mostrar datos personales.

Antes de abrir por primera vez el mapa, apagar Wi-Fi/datos del AVD. No confundir el host que descarga herramientas con la aplicación que funciona offline. No ejecutar los fixtures destructivos de tests en el dispositivo de una persona.

## Recorrido sugerido, 10–12 minutos

| Paso | Acción en la app | Resultado esperado / explicación |
|---|---|---|
| 1. Problema y pilares | Presentar objetivo y arquitectura local | Java/XML, SQLite, biometría y mapas; sin backend |
| 2. Seguridad | Abrir, cancelar desafío y luego reintentar con huella | Sin datos financieros tras cancelar; acceso solo al autenticar |
| 3. Cuentas | Crear Efectivo con 100,00 y Ahorros con 300,00 | Dos saldos separados; los nombres no son un catálogo de tipos |
| 4. Movimientos | En Efectivo registrar ingreso 50,00 y egreso 24,50 sin ubicación | Saldo 125,50 e historial de la cuenta; monto cero/negativo rechazado |
| 5. Agenda | Desde inicio, crear Ana, Luis y Marta | Contactos locales seleccionables por compras/repartos |
| 6. Compra futura | Crear PLANIFICADA, fecha futura, un ítem de 50,00 y Sin contacto | Aparece en historial/detalle; no genera movimiento ni notificación |
| 7. Compra compartida | Crear REALIZADA: ítem 1×50,01 e ítem 2×25,00; pulsar Añadir ítem en cada uno | Total 100,01; vínculo de compra con Ana es opcional y no selecciona participantes automáticamente |
| 8. Deudas | En el detalle abrir reparto, seleccionar los tres contactos y elegir Equitativo | 33,34 + 33,34 + 33,33 por ID ascendente; estado PENDIENTE |
| 9. Personalizado | Crear otra compra REALIZADA total 100,00, seleccionar los tres y asignar 20,00/30,00/50,00 | Suma exacta; no se cambia un reparto ya guardado; no hay pagos parciales ni marcar pagada |
| 10. GPS/mapa | Seguir el procedimiento siguiente | Marcador solo con coordenadas; cartografía incluida y zoom14–16 |
| 11. Persistencia/lifecycle | Volver a inicio, enviar a segundo plano y abrir; luego force-stop y relanzar | Nueva biometría; cuentas, movimientos, compras y deudas siguen en SQLite |
| 12. Evidencia y límites | Abrir informe T07A/T07B y mostrar SHA del APK | Separar PASS automatizado, NOT TESTED y bloqueo de upgrade; no afirmar cierre S00 |

Para GPS/mapa en AVD: en Efectivo iniciar un nuevo movimiento ingreso de 1,00 y activar la casilla de ubicación; aceptar el permiso Android y, mientras busca, enviar el punto Santiago Centro desde Extended Controls → Location o:
```sh
adb emu geo fix -70.660 -33.450
```
El comando del emulador recibe **longitud, latitud**. Esperar el mensaje de ubicación capturada antes de guardar; si expira el timeout, reintentar y volver a enviar el punto. Guardar: saldo de esta demo 126,50. Abrir mapa desde la cuenta, tocar marcador, usar zoom, desplazar y centrar. Una cuenta sin coordenadas muestra base y estado vacío, sin marcadores.

Un movimiento con ubicación exterior conserva coordenadas y muestra aviso, sin extender la cobertura. No hay campo de coordenadas manuales en la app ni se debe inventar uno para la exposición. En dispositivo físico la ubicación corresponde al sensor real y puede quedar fuera de Santiago Centro; **la prueba física sigue NOT TESTED**.

No intentar quitar FLAG_SECURE para capturar pantallas. Los renders autenticados, PNG de tiles y XML/logs de CI ya aportan evidencia. No presentar el render a Canvas como una captura del sistema.

## Explicación breve de persistencia y POO

Seis tablas, SQLite v4: cuentas, movimientos, contactos, compras, detalle_compra y deudas. Saldo y total son derivados; centésimas enteras evitan errores decimales. Repositories concentran SQL y transacciones; dominio protege invariantes. [Arquitectura](../../ARCHITECTURE.md) y [DDL](../design/DATABASE.md).

No mostrar compras como cargos automáticos ni deudas como movimientos; esos comportamientos no están implementados. La suma de cuotas valida el reparto del total de la compra; los ítems siguen detallados, pero no se reparten individualmente por producto.

## Evidencias para acompañar la exposición

- [T07A](../testing/S00-T07A.md): build, biometría/mapas API24/API36 y regresión completa API36.
- [T07B](../testing/S00-T07B.md): firmas de APK, matriz incompatible y procedimiento seguro.
- [Procedencia cartográfica](../maps/S00-T06B-data-provenance.md): datos OSM/ODbL, atlas y reproducción.

Decir expresamente: API24 financiero/GPS completo, físico, GPS exclusivamente aproximado, Sync Studio limpio y conservación tras upgrade histórico están NOT TESTED. Upgrade in-place: BLOCKED_SIGNER_MISMATCH. Un nuevo build debug no garantiza el mismo certificado.

## Antecedente histórico

La pauta anterior era un esquema preparatorio sin evidencia de exposición. Se conserva íntegra:

<details>
<summary>Pauta anterior a T07C</summary>

# Pauta de exposición

## Objetivo

Demostrar que LocalCash resuelve el problema propuesto y cumple los pilares técnicos obligatorios.

## Flujo recomendado

1. Problema y objetivo de LocalCash.
2. Arquitectura y por qué se mantuvo local/offline.
3. Autenticación biométrica.
4. Cuentas y movimientos.
5. Movimiento geolocalizado + mapa.
6. Compras y planificación.
7. Gasto compartido y deuda por contacto.
8. SQLite y persistencia.
9. Pruebas y limitaciones conocidas.

La pauta final debe usar capturas/datos de la implementación real; no inventar evidencia antes de S07.
</details>
