package com.aik1a.localcash.presentation;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.database.sqlite.SQLiteException;
import android.view.View;
import android.widget.*;
import com.aik1a.localcash.R;
import com.aik1a.localcash.application.CompraService;
import com.aik1a.localcash.data.LocalCashDbHelper;
import com.aik1a.localcash.domain.Contacto;
import java.util.*;

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
        findViewById(R.id.volver).setOnClickListener(v -> finish());
        findViewById(R.id.crear_contacto).setOnClickListener(v -> formulario());
        lista.setOnItemClickListener((parent, view, position, id) -> opciones(contactos.get(position)));
    }
    @Override protected void actualizarContenido() { actualizar(); }
    private void actualizar() {
        try { contactos = service.listarContactos(); lista.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactos)); }
        catch (SQLiteException ex) { Toast.makeText(this, "No se pudieron leer los contactos.", Toast.LENGTH_LONG).show(); }
    }
    private void formulario() {
        View view = getLayoutInflater().inflate(R.layout.dialog_contacto, null);
        EditText nombre = view.findViewById(R.id.nombre_contacto);
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Crear contacto").setView(view)
                .setPositiveButton("Guardar", null).setNegativeButton("Cancelar", null).create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            try { service.crearContacto(nombre.getText().toString()); dialog.dismiss(); actualizar(); AccionesUI.exito(this, "Contacto creado"); }
            catch (IllegalArgumentException ex) { nombre.setError(ex.getMessage()); }
            catch (SQLiteException ex) { nombre.setError("No se guardó el contacto. Intenta nuevamente."); }
        }));
        protegerDialogo(dialog).show();
    }
    private void opciones(Contacto contacto) {
        protegerDialogo(new AlertDialog.Builder(this).setTitle(contacto.getNombre())
                .setItems(new String[]{"Editar contacto", "Eliminar contacto"}, (d, elegido) -> {
                    if (elegido == 0) AccionesUI.nombre(this, "Editar contacto", contacto.getNombre(), nombre -> {
                        service.editarContacto(contacto.getId(), nombre); actualizar(); AccionesUI.exito(this, "Contacto actualizado");
                    });
                    else AccionesUI.confirmar(this, "Eliminar contacto", "¿Eliminar a " + contacto.getNombre() + "?", () -> {
                        service.eliminarContacto(contacto.getId()); actualizar(); AccionesUI.exito(this, "Contacto eliminado");
                    });
                }).setNegativeButton("Cancelar", null).create()).show();
    }
    @Override protected void onDestroy() { if (helper != null) helper.close(); super.onDestroy(); }
}


