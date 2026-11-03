# AV-03 — Prueba breve de avisos

Usar la variante de prueba y datos ficticios.

1. Guardar compra sin nombre: error junto al nombre y foco ahí.
2. Nombre válido pero sin productos: aviso visible para añadir al menos uno.
3. Añadir producto sin descripción, cantidad vacía/cero o precio negativo: cada error señala su campo y motivo. Precio cero sí se acepta.
4. Con varios productos y pantalla desplazada al final, provocar un error al guardar: debe llevarte al aviso/campo.
5. Reparto personalizado: dejar vacía o escribir negativa la cuota de una persona. Debe identificarla en su campo.
6. Escribir una suma menor/mayor: debe mostrar total, ingresado y cuánto falta/sobra en la moneda correcta.
7. Intentar eliminar cuenta con movimientos o contacto con compras/deudas: alerta persistente con motivo; datos conservados.
8. Movimiento sin tipo: aviso de selección. Con tipo elegido, monto vacío/cero/negativo: error de monto. No confundirlo con saldo inicial, que admite cero/negativo.
9. Editar planificación: explicación del estado fijo. Compra antigua sin moneda: explicación de por qué no puede editarse/pagarse.
10. Renombrar o guardar correctamente: confirmación breve de éxito.

Repetir errores con teclado abierto y letra grande. Cancelar alertas no debe guardar/borrar. Registro de pruebas automatizadas y límites en docs/handoffs/AV-03.md.
