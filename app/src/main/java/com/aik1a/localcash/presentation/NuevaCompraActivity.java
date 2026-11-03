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
    public static final String EDITAR_COMPRA_ID = "editar_compra_id";
    private long compraId;
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

    @Override protected void crearContenido(Bundle saved) {
         setContentView(R.layout.activity_nueva_compra);
        Pantalla.aplicarMargenes(findViewById(R.id.root));
        helper = new LocalCashDbHelper(this); service = new CompraService(helper);
        compraId = getIntent().getLongExtra(EDITAR_COMPRA_ID, -1);
        Compra existente = null;
        try {
            if (compraId > 0) {
                existente = service.buscarCompra(compraId);
                if (existente.getEstado() != Compra.Estado.PLANIFICADA || existente.getDivisa() == null)
                    throw new IllegalArgumentException(existente.getDivisa() == null ? "Define primero la moneda de la compra desde su detalle." : "Solo puedes editar compras planificadas que no tengan pago ni reparto.");
            }
        } catch (IllegalArgumentException ex) {
            String motivo = ex.getMessage();
            if (!"La compra no existe.".equals(motivo) && !"Define primero la moneda de la compra desde su detalle.".equals(motivo)
                    && !"Solo puedes editar compras planificadas que no tengan pago ni reparto.".equals(motivo))
                motivo = "Los datos de esta planificación no están disponibles. Vuelve al historial e intenta de nuevo.";
            AccionesUI.aviso(this, "No se puede editar la compra", motivo, this::finish); return;
        } catch (SQLiteException ex) {
            AccionesUI.aviso(this, "No se pudo abrir la planificación", "Vuelve al historial e intenta de nuevo. Tus datos se conservan.", this::finish); return;
        }
        lista = findViewById(R.id.detalles); error = findViewById(R.id.error_compra);
        nombre = findViewById(R.id.nombre_compra); descripcion = findViewById(R.id.item_descripcion);
        cantidad = findViewById(R.id.item_cantidad); precio = findViewById(R.id.item_precio);
        estado = findViewById(R.id.estado_compra); contacto = findViewById(R.id.contacto_compra);
        selectorDivisa = findViewById(R.id.divisa);
        divisa = saved == null ? (existente != null ? existente.getDivisa() : new com.aik1a.localcash.data.ConfiguracionRepository(helper).exigirDivisa())
                : Divisa.valueOf(saved.getString("divisa", "CLP"));
        DivisasUI.preparar(selectorDivisa, divisa);
        selectorDivisa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Divisa elegida = (Divisa)selectorDivisa.getSelectedItem();
                if ((compraId > 0 || !detalles.isEmpty()) && elegida != divisa) { selectorDivisa.setSelection(divisa.ordinal()); return; }
                divisa = elegida; DivisasUI.ejemplo(precio, divisa); mostrarDetalles();
            }
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        estado.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Planificada", "Realizada"}));
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
        if (existente != null) {
            estado.setEnabled(false);
            ((TextView)findViewById(R.id.ayuda_estado)).setText("Se conserva como planificada. Para realizarla, guarda los cambios y registra el pago desde su detalle.");
            ((Button)findViewById(R.id.guardar_compra)).setText("Guardar cambios");
            ((TextView)findViewById(R.id.titulo_formulario_compra)).setText("Editar planificación");
            if (saved == null) {
                nombre.setText(existente.getNombre()); fecha = existente.getFecha(); detalles.addAll(existente.getDetalles());
                if (existente.getContactoId() != null)
                    for (int i = 0; i < contactos.size(); i++)
                        if (contactos.get(i).getId() == existente.getContactoId()) contacto.setSelection(i + 1);
            }
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
        error.setText("");
        descripcion.setError(null); cantidad.setError(null); precio.setError(null);
        if (descripcion.getText().toString().trim().isEmpty()) {
            AccionesUI.errorCampo(descripcion, "Describe el producto o servicio."); return;
        }
        long unidades;
        if (cantidad.getText().toString().trim().isEmpty()) { AccionesUI.errorCampo(cantidad, "Ingresa la cantidad."); return; }
        try { unidades = Long.parseLong(cantidad.getText().toString().trim()); }
        catch (NumberFormatException ex) { AccionesUI.errorCampo(cantidad, "Usa una cantidad entera más pequeña, sin decimales."); return; }
        if (unidades <= 0) { AccionesUI.errorCampo(cantidad, "La cantidad debe ser mayor que cero."); return; }
        Long importe = AccionesUI.leerMonto(precio, divisa, true, "el precio unitario");
        if (importe == null) return;
        try {
            DetalleCompra detalle = new DetalleCompra(descripcion.getText().toString(), unidades, importe);
            List<DetalleCompra> nueva = new ArrayList<>(detalles); nueva.add(detalle);
            new Compra(0, "Borrador", Compra.Estado.PLANIFICADA, fecha, null, nueva, divisa).calcularTotal();
            detalles.add(detalle); descripcion.setText(""); cantidad.setText("1"); precio.setText("");
            mostrarDetalles();
        } catch (IllegalArgumentException ex) { AccionesUI.errorTexto(error, ex.getMessage()); }
        catch (ArithmeticException ex) { AccionesUI.errorTexto(error, "El total es demasiado grande. Reduce la cantidad o el precio e intenta de nuevo."); }
    }
    private void mostrarDetalles() {
        selectorDivisa.setEnabled(compraId <= 0 && detalles.isEmpty());
        ((TextView)findViewById(R.id.ayuda_divisa)).setText(compraId > 0 ? "Se conserva la moneda original de la compra." : detalles.isEmpty()
                ? "Elige la moneda antes de añadir productos."
                : "Para cambiar la moneda, quita primero los productos. Los importes no se convierten.");
        lista.removeAllViews();
        for (DetalleCompra d : detalles) {
            View fila = getLayoutInflater().inflate(R.layout.row_item_borrador, lista, false);
            ((TextView)fila.findViewById(R.id.item_texto)).setText(d.getDescripcion() + " · " + d.getCantidad() + " × "
                    + Dinero.formatear(d.getPrecioUnitario(), divisa) + " = " + Dinero.formatear(d.calcularSubtotal(), divisa));
            fila.findViewById(R.id.quitar_item).setOnClickListener(v -> { detalles.remove(d); mostrarDetalles(); }); lista.addView(fila);
        }
        long total = detalles.isEmpty() ? 0 : new Compra(0, "Borrador", Compra.Estado.PLANIFICADA, fecha, null, detalles, divisa).calcularTotal();
        ((TextView)findViewById(R.id.compra_total)).setText("Total: " + Dinero.formatear(total, divisa));
    }
    private void guardar() {
        error.setText("");
        if (nombre.getText().toString().trim().isEmpty()) { AccionesUI.errorCampo(nombre, "Escribe el nombre de la compra."); return; }
        if (!descripcion.getText().toString().trim().isEmpty() || !precio.getText().toString().trim().isEmpty()) {
            AccionesUI.errorTexto(error, "Hay un producto sin añadir. Pulsa Añadir ítem o vacía sus campos antes de guardar."); return;
        }
        if (detalles.isEmpty()) { AccionesUI.errorTexto(error, "Añade al menos un producto o servicio antes de guardar la compra."); return; }
        try {
            int elegido = contacto.getSelectedItemPosition(); Long idContacto = elegido == 0 ? null : contactos.get(elegido - 1).getId();
            Compra compra = new Compra(compraId > 0 ? compraId : 0, nombre.getText().toString(), compraId > 0 ? Compra.Estado.PLANIFICADA : Compra.Estado.values()[estado.getSelectedItemPosition()], fecha, idContacto, detalles, divisa);
            if (compraId > 0) { service.actualizarPlanificacion(compra); AccionesUI.exito(this, "Compra actualizada."); finish(); return; }
            long id = service.registrar(compra);
            AccionesUI.exito(this, "Compra guardada.");
            startActivity(new Intent(this, CompraDetalleActivity.class).putExtra(CompraDetalleActivity.COMPRA_ID, id)); finish();
        } catch (IllegalArgumentException ex) { AccionesUI.errorTexto(error, ex.getMessage()); }
        catch (ArithmeticException ex) { AccionesUI.errorTexto(error, "El total es demasiado grande. Reduce los importes e intenta de nuevo."); }
        catch (SQLiteException ex) { AccionesUI.errorTexto(error, "No se guardó la compra. Los datos del formulario se conservan; intenta de nuevo."); }
    }
    @Override protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out); if (!contenidoCreado() || divisa == null || nombre == null) return; out.putLong("fecha", fecha); out.putString("divisa", divisa.name());
        ArrayList<String> nombres = new ArrayList<>(); long[] cantidades = new long[detalles.size()], precios = new long[detalles.size()];
        for (int i = 0; i < detalles.size(); i++) { DetalleCompra d = detalles.get(i); nombres.add(d.getDescripcion()); cantidades[i] = d.getCantidad(); precios[i] = d.getPrecioUnitario(); }
        out.putStringArrayList("items", nombres); out.putLongArray("cantidades", cantidades); out.putLongArray("precios", precios);
    }
    @Override protected void onDestroy() { if (helper != null) helper.close(); super.onDestroy(); }
}
