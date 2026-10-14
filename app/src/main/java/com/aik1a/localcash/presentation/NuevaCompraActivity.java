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
    private Spinner estado, contacto;
    private LinearLayout lista;
    private TextView error;

    @Override protected void crearContenido(Bundle saved) {
         setContentView(R.layout.activity_nueva_compra);
        Pantalla.aplicarMargenes(findViewById(R.id.root));
        helper = new LocalCashDbHelper(this); service = new CompraService(helper);
        nombre = findViewById(R.id.nombre_compra); descripcion = findViewById(R.id.item_descripcion);
        cantidad = findViewById(R.id.item_cantidad); precio = findViewById(R.id.item_precio);
        estado = findViewById(R.id.estado_compra); contacto = findViewById(R.id.contacto_compra);
        lista = findViewById(R.id.detalles); error = findViewById(R.id.error_compra);
        estado.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Compra.Estado.values()));
        try {
            contactos = service.listarContactos(); List<String> opciones = new ArrayList<>(); opciones.add("Sin contacto");
            for (Contacto c : contactos) opciones.add(c.getNombre() + " (#" + c.getId() + ")");
            contacto.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opciones));
        } catch (SQLiteException ex) { Toast.makeText(this, "No se pudieron cargar los contactos.", Toast.LENGTH_LONG).show(); finish(); return; }
        fecha = saved == null ? System.currentTimeMillis() : saved.getLong("fecha");
        if (saved != null) {
            ArrayList<String> nombres = saved.getStringArrayList("items");
            long[] cantidades = saved.getLongArray("cantidades"), precios = saved.getLongArray("precios");
            if (nombres != null) for (int i = 0; i < nombres.size(); i++) detalles.add(new DetalleCompra(nombres.get(i), cantidades[i], precios[i]));
        }
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
            DetalleCompra detalle = new DetalleCompra(descripcion.getText().toString(), unidades, Dinero.parsear(precio.getText().toString()));
            List<DetalleCompra> nueva = new ArrayList<>(detalles); nueva.add(detalle);
            new Compra(0, "Borrador", Compra.Estado.PLANIFICADA, fecha, null, nueva).calcularTotal();
            detalles.add(detalle); descripcion.setText(""); cantidad.setText("1"); precio.setText("");
            error.setText(""); mostrarDetalles();
        } catch (NumberFormatException ex) { error.setText("La cantidad debe ser un entero positivo dentro del rango permitido."); }
        catch (IllegalArgumentException ex) { error.setText(ex.getMessage()); }
        catch (ArithmeticException ex) { error.setText("Cantidad, precio o total fuera de rango."); }
    }
    private void mostrarDetalles() {
        lista.removeAllViews();
        for (DetalleCompra d : detalles) {
            View fila = getLayoutInflater().inflate(R.layout.row_item_borrador, lista, false);
            ((TextView)fila.findViewById(R.id.item_texto)).setText(d.getDescripcion() + " · " + d.getCantidad() + " × "
                    + Dinero.formatear(d.getPrecioUnitario()) + " = " + Dinero.formatear(d.calcularSubtotal()));
            fila.findViewById(R.id.quitar_item).setOnClickListener(v -> { detalles.remove(d); mostrarDetalles(); }); lista.addView(fila);
        }
        long total = detalles.isEmpty() ? 0 : new Compra(0, "Borrador", Compra.Estado.PLANIFICADA, fecha, null, detalles).calcularTotal();
        ((TextView)findViewById(R.id.compra_total)).setText("Total: " + Dinero.formatear(total));
    }
    private void guardar() {
        if (!descripcion.getText().toString().trim().isEmpty() || !precio.getText().toString().trim().isEmpty()) {
            error.setText("Hay un ítem sin añadir. Pulsa Añadir ítem o vacía sus campos antes de guardar."); return;
        }
        try {
            int elegido = contacto.getSelectedItemPosition(); Long idContacto = elegido == 0 ? null : contactos.get(elegido - 1).getId();
            Compra compra = new Compra(0, nombre.getText().toString(), (Compra.Estado)estado.getSelectedItem(), fecha, idContacto, detalles);
            long id = service.registrar(compra);
            startActivity(new Intent(this, CompraDetalleActivity.class).putExtra(CompraDetalleActivity.COMPRA_ID, id)); finish();
        } catch (IllegalArgumentException ex) { error.setText(ex.getMessage()); }
        catch (ArithmeticException ex) { error.setText("Total fuera de rango."); }
        catch (SQLiteException ex) { error.setText("No se guardó la compra. Los datos del formulario se conservan."); }
    }
    @Override protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out); if (!contenidoCreado()) return; out.putLong("fecha", fecha);
        ArrayList<String> nombres = new ArrayList<>(); long[] cantidades = new long[detalles.size()], precios = new long[detalles.size()];
        for (int i = 0; i < detalles.size(); i++) { DetalleCompra d = detalles.get(i); nombres.add(d.getDescripcion()); cantidades[i] = d.getCantidad(); precios[i] = d.getPrecioUnitario(); }
        out.putStringArrayList("items", nombres); out.putLongArray("cantidades", cantidades); out.putLongArray("precios", precios);
    }
    @Override protected void onDestroy() { if (helper != null) helper.close(); super.onDestroy(); }
}

