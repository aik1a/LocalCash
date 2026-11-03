# Revisión de código — validaciones y avisos

Fecha: 2026-11-03. Copia revisada: output/LocalCash-ux, CRUD-02 / SQLite v7. Inspección estática solicitada por el usuario; no se presenta como recorrido manual ni auditoría independiente de mi implementación anterior. No cambia código ni BD.

## Hallazgos prioritarios

| ID | Lugar y evidencia | Qué existe | Faltante o mejora |
|---|---|---|---|
| AV01 | NuevaCompraActivity.java:99–109,126–138; Compra.java:22; DetalleCompra.java:8–9 | Validación de dominio y error_compra | Nombre vacío, lista vacía o ítem inválido pueden producir una explicación conjunta. Validar cada caso en UI y marcar el campo exacto. Cantidad <=0 y descripción vacía merecen mensajes distintos. Precio cero está permitido; solo rechazar negativos o formato/precisión inválidos. |
| AV02 | activity_nueva_compra.xml:8; NuevaCompraActivity.java:107–109,136–138 | TextView único debajo de Añadir ítem y antes de lista de productos | No hay requestFocus ni desplazamiento hacia error; al guardar desde el final de una lista larga el mensaje puede quedar fuera de vista. Es riesgo deducido de la estructura, no fallo visual reproducido. error_compra tampoco tiene accessibilityLiveRegion. |
| AV03 | RepartoActivity.java:92–108; Dinero.parsear | Explica formato de moneda o monto negativo mediante error_reparto | El error no identifica de quién es la cuota incorrecta. Validar/mostrar error junto al importe de Tú o del contacto y llevar foco ahí. |
| AV04 | Reparto.java:27–28 y RepartoActivity.java:106 | La suma debe coincidir exactamente con el total | Mostrar total de compra, suma ingresada y diferencia: por ejemplo «Faltan CLP 5.000 para completar el total». No alterar la regla de suma exacta ni guardar importes diferentes. |
| AV05 | AccionesUI.java:24–30; ContactoRepository.java:24; CuentaRepository.java:47 | Confirmación de borrado y Toast con motivo al rechazarse | No está ausente el aviso. El diálogo se cierra y queda un Toast largo. Mostrar un aviso persistente «No se puede eliminar» con causa específica y siguiente paso. Distinguir registro inexistente de registro con referencias cuando sea posible. |
| AV06 | NuevaCompraActivity.java:75; CompraDetalleActivity.java:44–54,64–66; DivisasUI.java:28–30 | Estado deshabilitado al editar; botones de editar/pagar deshabilitados si falta moneda; asignación histórica permite Más tarde | Explicar al lado del control por qué está bloqueado: conservar planificación; completar moneda antes de editar/pagar. Total cero ya tiene explicación; divisa fija también tiene ayuda existente y no debe duplicarse. |
| AV07 | CompraDetalleActivity.java:74–75,95–96,109–112; RepartoActivity.java:78–80,142 | Mensajes genéricos para errores de lectura/escritura; algunos catch agrupan validación y SQLite | Preservar causas funcionales conocidas con texto útil. «No se pudo abrir» no distingue compra inexistente, estado incompatible o fallo de BD. No mostrar SQL, stacktraces ni detalle técnico al usuario ni inventar causa si no se conoce. |
| AV08 | CuentaActivity.java:129,146; Movimiento.java:21–22; Dinero.java:23–26 | Falta de tipo se explica; errores de monto aparecen en el campo | Al ingresar cero/negativo el mensaje de dominio también menciona cuenta/tipo aunque estén elegidos. Dar «El monto debe ser mayor que cero». Si está vacío, «Ingresa un monto» antes de explicar formato. Saldo inicial de cuenta puede ser cero/negativo; no imponer la misma regla allí. |

## Avisos que ya existen y conviene conservar

- Nombre vacío al crear cuenta/contacto o renombrar: errores en el campo.
- Precisión de CLP/JPY, decimales y formatos numéricos: validación existente.
- Falta de participantes: mensaje existente «Elige al menos un contacto para compartir contigo».
- No hay contactos: mensaje para añadir uno.
- No hay cuentas en la moneda de compra: AlertDialog existente que explica crear una cuenta compatible.
- Ubicación: permiso denegado, timeout, captura interrumpida, reintento y confirmación antes de guardar sin ubicación.
- Pago y cambios de estado de deuda: confirmación existente y explicación de efecto en saldo.
- Ítem escrito pero todavía no añadido: mensaje existente al intentar guardar compra.
- Moneda fija o bloqueada tras añadir productos: explicación contextual existente.

## Confirmaciones de éxito opcionales

Renombre, eliminación, edición de planificación y cambio de estado se reflejan en la interfaz, sin un aviso breve adicional en esos recorridos. Podrían añadirse «Nombre actualizado», «Compra actualizada» o «Contacto eliminado» cuando faciliten entender el resultado, evitando repetir un diálogo que el usuario acaba de confirmar. Prioridad menor que explicar un rechazo.

## Criterio propuesto para corregir

1. Error de entrada: junto al campo, mensaje específico, foco y visibilidad.
2. Acción rechazada o condición que necesita explicación: diálogo breve y persistente con motivo y salida posible.
3. Éxito simple: aviso breve opcional, sin pedir otra confirmación.
4. Error técnico: mensaje recuperable y prudente; no atribuirlo falsamente a lo ingresado.

Los Toast desaparecen automáticamente y, para el target de esta app, pueden limitarse a dos líneas; por eso no conviene usarlos como único soporte de explicaciones largas. [Android: Toasts](https://developer.android.com/guide/topics/ui/notifiers/toasts). AlertDialog ya está disponible en el stack: [Android: Dialogs](https://developer.android.com/develop/ui/views/components/dialogs).

Orden sugerido: AV01/AV02 (compra), AV03/AV04 (reparto), AV05 (borrado rechazado), AV06/AV08 (bloqueos e importes), AV07 y éxitos opcionales. Ninguna de estas mejoras requiere migración de BD ni cambiar reglas financieras.

## Validación de una futura corrección

Probar campos vacíos, cantidad cero, precio negativo, precisión inválida, cuota incorrecta de un contacto, suma menor/mayor, intento de borrar registro usado y moneda histórica pendiente. En listas largas/teclado abierto/letra grande, el mensaje debe verse y señalar cómo corregir. No se ejecutaron estos casos por UI en esta revisión.

## Seguimiento AV-03

Correcciones AV01–AV08 implementadas después de esta revisión y autorizadas por el usuario. Evidencia automatizada y límites en docs/handoffs/AV-03.md; no se transforma este diagnóstico en una aprobación independiente.
