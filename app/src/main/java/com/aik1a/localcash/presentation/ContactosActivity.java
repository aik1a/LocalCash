package com.aik1a.localcash.presentation;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.database.sqlite.SQLiteException;
import android.view.View;
import android.widget.*;
import com.aik1a.localcash.R;
import com.aik1a.localcash.application.CompraService;
import com.aik1a.localcash.application.DeudaService;
import com.aik1a.localcash.domain.*;
import java.util.*;
import com.aik1a.localcash.data.LocalCashDbHelper;

public final class ContactosActivity extends ActividadProtegida {
    private LocalCashDbHelper helper;
    private CompraService service;
    private ListView lista;
    private List<Contacto> contactos = new ArrayList<>();
    @Override protected void crearContenido(Bundle state) {
         setContentView(R.layout.activity_contactos);
        Pantalla.aplicarMargenes(findViewById(R.id.root));
        helper = new LocalCashDbHelper(this); service = new CompraService(helper);
        lista = findViewById(R.id.contactos); lista.setEmptyView(findViewById(R.id.vacio));
        lista.setOnItemClickListener((parent, view, position, id) -> opciones(contactos.get(position)));
        findViewById(R.id.volver).setOnClickListener(v -> finish());
        findViewById(R.id.crear_contacto).setOnClickListener(v -> formulario());
    }
    @Override protected void actualizarContenido() { actualizar(); }
    private void actualizar() {
        try { contactos = service.listarContactos(); lista.setAdapter(new ArrayAdapter<>(this, R.layout.row_contacto, contactos)); }
        catch (SQLiteException ex) { Toast.makeText(this, "No se pudieron leer los contactos.", Toast.LENGTH_LONG).show(); }
    }
    private void opciones(Contacto contacto) {
        protegerDialogo(new AlertDialog.Builder(CuentasVisual.contexto(this)).setTitle(contacto.getNombre())
                .setItems(new String[]{"Ver deudas", "Cambiar nombre", "Eliminar contacto"}, (dialog, opcion) -> {
                    if (opcion == 0) mostrarDeudas(contacto);
                    else if (opcion == 1) AccionesUI.nombre(this, "Cambiar nombre del contacto", contacto.getNombre(),
                            nombre -> service.renombrarContacto(contacto.getId(), nombre), this::actualizar);
                    else AccionesUI.eliminar(this, "¿Eliminar a " + contacto.getNombre() + "? Solo se eliminará si no tiene compras ni deudas asociadas.",
                            () -> service.eliminarContacto(contacto.getId()), this::actualizar);
                }).setNegativeButton("Cerrar", null).create()).show();
    }
    private void mostrarDeudas(Contacto contacto) {
        try {
            DeudaService deudas = new DeudaService(helper);
            LinearLayout contenido = new LinearLayout(this); contenido.setOrientation(LinearLayout.VERTICAL);
            int espacio = Math.round(16 * getResources().getDisplayMetrics().density); contenido.setPadding(espacio, espacio, espacio, espacio);
            TextView resumen = new TextView(CuentasVisual.contexto(this));
            StringBuilder texto = new StringBuilder("Pendiente por moneda:");
            Map<Divisa, Long> totales = deudas.totalesPendientes(contacto.getId());
            if (totales.isEmpty()) texto.append("\nNo hay importes pendientes con moneda definida.");
            for (Map.Entry<Divisa, Long> total : totales.entrySet()) texto.append("\n").append(Dinero.formatear(total.getValue(), total.getKey()));
            texto.append("\nLos importes sin moneda definida se muestran por compra y no se suman.");
            resumen.setText(texto.toString()); contenido.addView(resumen);
            List<Deuda> listaDeudas = deudas.listarPorContacto(contacto.getId());
            if (listaDeudas.isEmpty()) { TextView vacio = new TextView(CuentasVisual.contexto(this)); vacio.setText("Este contacto no tiene deudas registradas."); contenido.addView(vacio); }
            for (Deuda deuda : listaDeudas) {
                Compra compra = deudas.buscarCompra(deuda.getCompraId()); Button fila = (Button)getLayoutInflater().inflate(R.layout.row_accion, contenido, false);
                fila.setText(compra.getNombre() + " · " + Dinero.formatear(deuda.getMonto(), compra.getDivisa()) + " · "
                        + (deuda.getMonto() == 0 ? "Sin deuda" : "PAGADA".equals(deuda.getEstado()) ? "Pagada" : "Pendiente"));
                fila.setOnClickListener(v -> startActivity(new android.content.Intent(this, CompraDetalleActivity.class)
                        .putExtra(CompraDetalleActivity.COMPRA_ID, compra.getId()))); contenido.addView(fila);
            }
            ScrollView scroll = new ScrollView(this); scroll.addView(contenido);
            protegerDialogo(new AlertDialog.Builder(CuentasVisual.contexto(this)).setTitle("Deudas de " + contacto.getNombre()).setView(scroll)
                    .setPositiveButton("Cerrar", null).create()).show();
        } catch (IllegalArgumentException | ArithmeticException | SQLiteException ex) {
            Toast.makeText(this, "No se pudieron cargar las deudas del contacto.", Toast.LENGTH_LONG).show();
        }
    }
    private void formulario() {
        View view = getLayoutInflater().cloneInContext(CuentasVisual.contexto(this)).inflate(R.layout.dialog_contacto, null);
        EditText nombre = view.findViewById(R.id.nombre_contacto);
        AlertDialog dialog = new AlertDialog.Builder(CuentasVisual.contexto(this)).setTitle("Crear contacto").setView(view)
                .setPositiveButton("Guardar", null).setNegativeButton("Cancelar", null).create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            try { service.crearContacto(nombre.getText().toString()); dialog.dismiss(); AccionesUI.exito(this, "Contacto creado."); actualizar(); }
            catch (IllegalArgumentException ex) { AccionesUI.errorCampo(nombre, ex.getMessage()); }
            catch (SQLiteException ex) { Toast.makeText(this, "No se guardó el contacto.", Toast.LENGTH_LONG).show(); }
        }));
        protegerDialogo(dialog).show();
    }
    @Override protected void onDestroy() { if (helper != null) helper.close(); super.onDestroy(); }
}


