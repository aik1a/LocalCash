# CRUD-02 — Recorrido de prueba

Usar datos ficticios y la variante de prueba; conservar instalación actual con datos.

1. En una compra realizada, repartir con un contacto. Marcar pagada, cancelar la confirmación y comprobar que sigue pendiente. Confirmar después y reabrir: Pagada permanece. Volver a pendiente funciona. Ninguna acción modifica saldo. Cuota cero indica Sin deuda y no ofrece cobro.
2. En Contactos, tocar una persona, cambiar nombre y reabrir un reparto: mismo contacto y nuevo nombre. Nombre vacío no se acepta.
3. En una cuenta, Opciones de cuenta → Cambiar nombre. Comprobar saldo e historial intactos.
4. Crear contacto sin uso y cuenta sin movimientos; cancelar eliminación y comprobar que siguen. Confirmar eliminación y comprobar que desaparecen. Intentar eliminar persona con deudas o cuenta con movimientos: se conserva y explica el motivo.
5. Crear compra planificada, abrir Editar planificación, cambiar nombre/fecha/contacto y quitar/añadir productos. Guardar y reabrir: datos y total nuevos, mismo registro y saldo intacto. Moneda y estado se conservan. No permite guardar sin productos.
6. Eliminar una planificación: cancelar primero, confirmar después. Desaparece del historial sin afectar saldo. Una compra realizada/pagada no ofrece estas acciones.
7. Contactos → persona → Ver deudas: aparecen compras y estados. Pagadas no se suman al pendiente; CLP y USD se muestran separados. Tocar una fila abre la compra.
8. Repetir una edición rotando el dispositivo y con teclado abierto; comprobar selección de contacto, fecha e ítems. Revisar letra grande y desplazamiento de botones.

Registrar dispositivo/API, resultado y captura/log cuando corresponda. Pruebas automatizadas y límites en docs/handoffs/CRUD-02.md.
