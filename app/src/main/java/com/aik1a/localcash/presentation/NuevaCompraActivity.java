package com.aik1a.localcash.presentation;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.database.sqlite.SQLiteException;
import android.view.View;
import android.widget.*;
import com.aik1a.localcash.R;
import com.aik1a.localcash.application.CompraService;
import com.aik1a.localcash.data.LocalCashDbHelper;
import com.aik1a.localcash.domain.*;
import java.text.DateFormat;
import java.util.*;

public final class NuevaCompraActivity extends ActividadProtegida {
    private LocalCashDbHelper helper;
    private CompraService service;
    private final List<DetalleCompra> detalles = new ArrayList<>();
    private List<Contacto> contactos;
    private long fecha;
    private EditText nombre, descripcion, cantidad, precio;
    private Spinner estado, contacto, selectorDivisa;
    private Divisa divisa;
    private LinearLayout lista;
    private TextView error;
    private long compraId;
    private Compra original;

    @Override protected void crearContenido(Bundle saved) {
         setContentView(R.layout.activity_nueva_compra);
        Pantalla.aplicarMargenes(findViewById(R.id.root));
        helper = new LocalCashDbHelper(this); service = new CompraService(helper);
        compraId = getIntent().getLongExtra(CompraDetalleActivity.COMPRA_ID, 0);
        if (compraId > 0) {
            try {
                original = service.buscarCompra(compraId);
                if (original.getEstado() != Compra.Estado.PLANIFICADA) throw new IllegalArgumentException("Solo puedes editar los productos de una compra planificada.");
                if (original.getDivisa() == null) throw new IllegalArgumentException("Define la divisa de la compra primero.");
            } catch (IllegalArgumentException | SQLiteException ex) { Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show(); finish(); return; }
        }
        nombre = findViewById(R.id.nombre_compra); descripcion = findViewById(R.id.item_descripcion);
        cantidad = findViewById(R.id.item_cantidad); precio = findViewById(R.id.item_precio);
        estado = findViewById(R.id.estado_compra); contacto = findViewById(R.id.contacto_compra);
        selectorDivisa = findViewById(R.id.divisa);
        divisa = saved == null ? new com.aik1a.localcash.data.ConfiguracionRepository(helper).exigirDivisa()
                : Divisa.valueOf(saved.getString("divisa", "CLP"));
        if (original != null) divisa = original.getDivisa();
        DivisasUI.preparar(selectorDivisa, divisa);
        selectorDivisa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Divisa elegida = (Divisa)selectorDivisa.getSelectedItem();
                if (!detalles.isEmpty() && elegida != divisa) { selectorDivisa.setSelection(divisa.ordinal()); return; }
                divisa = elegida; DivisasUI.ejemplo(precio, divisa); mostrarDetalles();
            }
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        lista = findViewById(R.id.detalles); error = findViewById(R.id.error_compra);
        estado.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Planificada", "Realizada"}));
        estado.setEnabled(compraId == 0);
        try {
            contactos = service.listarContactos(); List<String> opciones = new ArrayList<>(); opciones.add("Sin contacto");
            for (Contacto c : contactos) opciones.add(c.getNombre());
            contacto.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opciones));
        } catch (SQLiteException ex) { Toast.makeText(this, "No se pudieron cargar los contactos.", Toast.LENGTH_LONG).show(); finish(); return; }
        fecha = saved == null ? System.currentTimeMillis() : saved.getLong("fecha");
        if (saved != null) {
            ArrayList<String> nombres = saved.getStringArrayList("items");
            long[] cantidades = saved.getLongArray("cantidades"), precios = saved.getLongArray("precios");
            if (nombres != null) for (int i = 0; i < nombres.size(); i++) detalles.add(new DetalleCompra(nombres.get(i), cantidades[i], precios[i]));
        }
        if (original != null && saved == null) {
            nombre.setText(original.getNombre()); fecha = original.getFecha(); detalles.addAll(original.getDetalles());
            for (int i = 0; i < contactos.size(); i++) if (original.getContactoId() != null && contactos.get(i).getId() == original.getContactoId()) contacto.setSelection(i + 1);
        }
        ((TextView)findViewById(R.id.titulo_formulario_compra)).setText(compraId == 0 ? "Registrar compra" : "Editar compra");
        findViewById(R.id.volver).setOnClickListener(v -> finish());
        findViewById(R.id.fecha_compra).setOnClickListener(v -> elegirFecha());
        findViewById(R.id.agregar_item).setOnClickListener(v -> agregarItem());
        findViewById(R.id.guardar_compra).setOnClickListener(v -> guardar());
        mostrarDetalles(); mostrarFecha();
    }
    private void elegirFecha() {
        Calendar c = Calendar.getInstance(); c.setTimeInMillis(fecha);
        protegerDialogo(new DatePickerDialog(this, (view, year, month, day) -> {
            Calendar elegida = Calendar.getInstance(); elegida.clear(); elegida.set(year, month, day);
            fecha = elegida.getTimeInMillis(); mostrarFecha();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))).show();
    }
    private void mostrarFecha() { ((Button)findViewById(R.id.fecha_compra)).setText("Fecha: " + DateFormat.getDateInstance(DateFormat.SHORT).format(new Date(fecha))); }
    private void agregarItem() {
        try {
            long unidades = Long.parseLong(cantidad.getText().toString().trim());
            DetalleCompra detalle = new DetalleCompra(descripcion.getText().toString(), unidades, Dinero.parsear(precio.getText().toString(), divisa));
            List<DetalleCompra> nueva = new ArrayList<>(detalles); nueva.add(detalle);
            new Compra(0, "Borrador", Compra.Estado.PLANIFICADA, fecha, null, nueva, divisa).calcularTotal();
            detalles.add(detalle); descripcion.setText(""); cantidad.setText("1"); precio.setText("");
            error.setText(""); mostrarDetalles();
        } catch (NumberFormatException ex) { error.setText("Escribe una cantidad válida. Ej.: 2"); }
        catch (IllegalArgumentException ex) { error.setText(ex.getMessage()); }
        catch (ArithmeticException ex) { error.setText("Cantidad, precio o total fuera de rango."); }
    }
    private void mostrarDetalles() {
        selectorDivisa.setEnabled(compraId == 0 && detalles.isEmpty());
        lista.removeAllViews();
        for (DetalleCompra d : detalles) {
            View fila = getLayoutInflater().inflate(R.layout.row_item_borrador, lista, false);
            ((TextView)fila.findViewById(R.id.item_texto)).setText(d.getDescripcion() + " · " + d.getCantidad() + " × "
                    + Dinero.formatear(d.getPrecioUnitario(), divisa) + " = " + Dinero.formatear(d.calcularSubtotal(), divisa));
            fila.findViewById(R.id.editar_item).setOnClickListener(v -> editarItem(d));
            fila.findViewById(R.id.quitar_item).setOnClickListener(v -> { detalles.remove(d); mostrarDetalles(); }); lista.addView(fila);
        }
        long total = detalles.isEmpty() ? 0 : new Compra(0, "Borrador", Compra.Estado.PLANIFICADA, fecha, null, detalles, divisa).calcularTotal();
        ((TextView)findViewById(R.id.compra_total)).setText("Total: " + Dinero.formatear(total, divisa));
    }
    private void guardar() {
        if (!descripcion.getText().toString().trim().isEmpty() || !precio.getText().toString().trim().isEmpty()) {
            error.setText("Hay un ítem sin añadir. Pulsa Añadir ítem o vacía sus campos antes de guardar."); return;
        }
        try {
            int elegido = contacto.getSelectedItemPosition(); Long idContacto = elegido == 0 ? null : contactos.get(elegido - 1).getId();
            Compra compra = new Compra(compraId, nombre.getText().toString(), estado.getSelectedItemPosition() == 0 ? Compra.Estado.PLANIFICADA : Compra.Estado.REALIZADA, fecha, idContacto, detalles, divisa);
            if (compraId > 0) {
                service.editarPlanificada(compra); AccionesUI.exito(this, "Compra actualizada"); finish();
            } else if (compra.getEstado() == Compra.Estado.REALIZADA) {
                PagoCompraUI.mostrar(this, helper, compra, cuentaId -> abrirCompra(service.registrar(compra, cuentaId)));
            } else {
                long id = service.registrar(compra); AccionesUI.exito(this, "Compra planificada"); abrirCompra(id);
            }
        } catch (IllegalArgumentException ex) { error.setText(ex.getMessage()); }
        catch (ArithmeticException ex) { error.setText("Total fuera de rango."); }
        catch (SQLiteException ex) { error.setText("No se guardó la compra. Los datos del formulario se conservan."); }
    }
    private void abrirCompra(long id) {
        startActivity(new Intent(this, CompraDetalleActivity.class).putExtra(CompraDetalleActivity.COMPRA_ID, id)); finish();
    }
    private void editarItem(DetalleCompra detalle) {
        View form = getLayoutInflater().inflate(R.layout.dialog_item_compra, null);
        EditText texto = form.findViewById(R.id.item_descripcion), unidades = form.findViewById(R.id.item_cantidad), valor = form.findViewById(R.id.item_precio);
        texto.setText(detalle.getDescripcion()); unidades.setText(Long.toString(detalle.getCantidad()));
        valor.setText(java.math.BigDecimal.valueOf(detalle.getPrecioUnitario(), 2).stripTrailingZeros().toPlainString()); DivisasUI.ejemplo(valor, divisa);
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this).setTitle("Editar ítem").setView(form)
                .setNegativeButton("Cancelar", null).setPositiveButton("Guardar", null).create();
        dialog.setOnShowListener(x -> dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            try {
                DetalleCompra nuevo = new DetalleCompra(texto.getText().toString(), Long.parseLong(unidades.getText().toString().trim()), Dinero.parsear(valor.getText().toString(), divisa));
                List<DetalleCompra> copia = new ArrayList<>(detalles); int posicion = detalles.indexOf(detalle); copia.set(posicion, nuevo);
                new Compra(0, "Borrador", Compra.Estado.PLANIFICADA, fecha, null, copia, divisa).calcularTotal();
                detalles.set(posicion, nuevo); mostrarDetalles(); dialog.dismiss();
            } catch (NumberFormatException ex) { unidades.setError("Escribe una cantidad válida. Ej.: 2"); }
            catch (IllegalArgumentException ex) { valor.setError(ex.getMessage()); }
            catch (ArithmeticException ex) { valor.setError("Cantidad o precio fuera de rango."); }
        }));
        protegerDialogo(dialog).show();
    }
    @Override protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out); if (!contenidoCreado()) return; out.putLong("fecha", fecha); out.putString("divisa", divisa.name());
        ArrayList<String> nombres = new ArrayList<>(); long[] cantidades = new long[detalles.size()], precios = new long[detalles.size()];
        for (int i = 0; i < detalles.size(); i++) { DetalleCompra d = detalles.get(i); nombres.add(d.getDescripcion()); cantidades[i] = d.getCantidad(); precios[i] = d.getPrecioUnitario(); }
        out.putStringArrayList("items", nombres); out.putLongArray("cantidades", cantidades); out.putLongArray("precios", precios);
    }
    @Override protected void onDestroy() { if (helper != null) helper.close(); super.onDestroy(); }
}


