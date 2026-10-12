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

public final class ContactosActivity extends Activity {
    private LocalCashDbHelper helper;
    private CompraService service;
    private ListView lista;
    @Override public void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_contactos);
        Pantalla.aplicarMargenes(findViewById(R.id.root));
        helper = new LocalCashDbHelper(this); service = new CompraService(helper);
        lista = findViewById(R.id.contactos); lista.setEmptyView(findViewById(R.id.vacio));
        findViewById(R.id.volver).setOnClickListener(v -> finish());
        findViewById(R.id.crear_contacto).setOnClickListener(v -> formulario());
    }
    @Override protected void onResume() { super.onResume(); actualizar(); }
    private void actualizar() {
        try { lista.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, service.listarContactos())); }
        catch (SQLiteException ex) { Toast.makeText(this, "No se pudieron leer los contactos.", Toast.LENGTH_LONG).show(); }
    }
    private void formulario() {
        View view = getLayoutInflater().inflate(R.layout.dialog_contacto, null);
        EditText nombre = view.findViewById(R.id.nombre_contacto);
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Crear contacto").setView(view)
                .setPositiveButton("Guardar", null).setNegativeButton("Cancelar", null).create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            try { service.crearContacto(nombre.getText().toString()); dialog.dismiss(); actualizar(); }
            catch (IllegalArgumentException ex) { nombre.setError(ex.getMessage()); }
            catch (SQLiteException ex) { Toast.makeText(this, "No se guardó el contacto.", Toast.LENGTH_LONG).show(); }
        }));
        dialog.show();
    }
    @Override protected void onDestroy() { if (helper != null) helper.close(); super.onDestroy(); }
}
