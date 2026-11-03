# LocalCash — Diagnóstico UX y corrección UX-01

Fecha: 2026-11-02. Base: aik1a/LocalCash main 9b8a651d66982eea3a41e1979342468fe4cad176.
Revisión de código Java/XML y documentación; reporte del usuario sobre teléfono físico. No es una auditoría independiente del cambio ni una prueba humana completa de pantallas. Se distinguen observaciones de código, inferencias y pendientes.

## Hallazgos priorizados

| Prioridad | Observación verificable | Efecto / diagnóstico | Acción |
|---|---|---|---|
| Alta | CompraService solo persistía compras; CuentaActivity no ofrecía relación con ellas. BUSINESS_RULES excluía cargos automáticos. | Separación reportada por el usuario respaldada por estructura del código. | Usuario autoriza pago explícito completo; vínculo persistente y navegación en ambos sentidos. |
| Alta | Compra.Estado se elegía al crear; CompraDetalleActivity no tenía acción para realizar una planificada. | Planificar no ofrecía continuación natural. | Realizar y pagar desde detalle; la transacción cambia estado y saldo juntos. |
| Alta | CapturaUbicacion escogía GPS si estaba habilitado, excluyendo red, con límite 15 s. | Causa plausible de latencia reportada; causa física exacta no demostrada. | Solicitar ambos proveedores permitidos. Primera respuesta cancela ambos; sin tracking ni caché antigua. |
| Media | Guardar movimiento aceptaba ubicacion null con casilla marcada mientras buscaba o había fallado. | El usuario podía creer que adjuntó ubicación. | Confirmación explícita para guardar sin ubicación; puede volver y esperar/reintentar. |
| Baja | Estado.name() y Tipo.name() se mostraban en listas/detalles y el selector usaba enums. | Texto técnico en mayúsculas para acciones habituales. | Etiquetas Planificada/Realizada e Ingreso/Egreso; almacenamiento intacto. |
| Baja | Cantidad usaba teclado decimal y con signo, pero Long.parseLong exigía entero positivo. | Teclado invitaba a entradas que el formulario rechazaba. | Teclado entero; validación de dominio se mantiene. |
| Baja | Selector de divisa se deshabilitaba al añadir productos sin explicar por qué. | Bloqueo poco comprensible; inferencia de revisión experta. | Ayuda contextual; no convertir ni redondear importes. |

## Divisas revisadas

Dinero usa enteros de centésimas persistidos, precisión por divisa y código CLP/USD/etc. visible. CLP/JPY sin decimales; otras dos; no suma entre divisas. No se encontró motivo para reemplazar estas reglas por símbolos ambiguos ni introducir conversión. Mantener código de moneda y exactitud. La compra solo admite cuenta de pago con moneda coincidente.

## Otros ajustes posibles, pendientes

- Formularios de creación: revisar cancelación/atrás con contenido escrito; puede perderse un borrador. No añadir persistencia de borradores ni confirmaciones a pantallas vacías por defecto.
- Errores genéricos de lectura: revisar cuándo conviene conservar pantalla y ofrecer reintento frente a cerrarla. No inventar recuperaciones sin escenario reproducible.
- Listas sin distinción visual fuerte entre compras planificadas/realizadas: evaluar un filtro sencillo cuando exista un volumen real; no cambiar navegación completa ahora.
- Probar textos largos, teclado abierto y tamaño de fuente grande en teléfono. XML no demuestra ausencia de recortes ni accesibilidad completa.
- Algunas consultas recorren compras y hacen lecturas adicionales. Sin lentitud medida, no introducir paginación, hilos o arquitectura nueva en este incremento.

## Límites funcionales explícitos

El incremento implementa el caso en que el usuario pagó el total desde una sola cuenta. Si pagó solo su parte, no debe confirmar ese pago completo; pagos parciales/múltiples pagadores requieren otra tarea. Reparto registra deudas y no vuelve a descontar. Las compras históricas no generan cargos retroactivos. Movimientos anotados manualmente antes del incremento no se identifican automáticamente como duplicados; el aviso exige comprobarlo antes de registrar el pago. Total cero no genera egreso.

## Evidencia externa

- [LocationManager](https://developer.android.com/reference/android/location/LocationManager): proveedores y actualizaciones disponibles; las ubicaciones almacenadas no garantizan actualidad. Se conserva estrategia sin caché.
- [Permisos de ubicación Android](https://developer.android.com/develop/sensors-and-location/location/permissions/runtime): permiso en contexto y soporte de ubicación aproximada. No exigir permiso preciso para seguir usando la aplicación.
- [Diálogos Android](https://developer.android.com/develop/ui/views/components/dialogs): confirmación de decisiones concretas mediante AlertDialog existente.

No atribuir estos defectos a la experiencia del autor: se revisaron comportamientos concretos, no estereotipos de desarrolladores junior.

## Gate

Implementación local pendiente de auditoría independiente y recorrido manual físico. No autoriza merge ni cierre de sprint. Validaciones exactas en docs/handoffs/UX-01.md.
