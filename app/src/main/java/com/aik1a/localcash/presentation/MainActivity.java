package com.aik1a.localcash.presentation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.database.sqlite.SQLiteException;
import android.view.View;
import android.widget.*;
import com.aik1a.localcash.R;
import com.aik1a.localcash.application.MovimientoService;
import com.aik1a.localcash.data.LocalCashDbHelper;
import com.aik1a.localcash.domain.Cuenta;
import com.aik1a.localcash.domain.Dinero;
import java.util.*;

public final class MainActivity extends Activity {
    private LocalCashDbHelper helper;
    private MovimientoService service;
    private List<Cuenta> cuentas = new ArrayList<>();
    private ListView lista;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        Pantalla.aplicarMargenes(findViewById(R.id.root));
        helper = new LocalCashDbHelper(this);
        service = new MovimientoService(helper);
        findViewById(R.id.abrir_contactos).setOnClickListener(v -> startActivity(new Intent(this, ContactosActivity.class)));
        findViewById(R.id.abrir_compras).setOnClickListener(v -> startActivity(new Intent(this, ComprasActivity.class)));
        lista = findViewById(R.id.cuentas);
        lista.setEmptyView(findViewById(R.id.vacio));
        findViewById(R.id.crear_cuenta).setOnClickListener(v -> mostrarFormulario());
        lista.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, CuentaActivity.class);
            intent.putExtra(CuentaActivity.CUENTA_ID, cuentas.get(position).getId());
            startActivity(intent);
        });
    }
    @Override protected void onResume() { super.onResume(); actualizar(); }

    private void actualizar() {
        try {
            cuentas = service.listarCuentas();
            List<Map<String, String>> filas = new ArrayList<>();
            for (Cuenta cuenta : cuentas) {
                Map<String, String> fila = new HashMap<>();
                fila.put("nombre", cuenta.getNombre());
                fila.put("saldo", "Saldo: " + Dinero.formatear(service.calcularSaldo(cuenta.getId())));
                filas.add(fila);
            }
            lista.setAdapter(new SimpleAdapter(this, filas, android.R.layout.simple_list_item_2,
                    new String[]{"nombre", "saldo"}, new int[]{android.R.id.text1, android.R.id.text2}));
        } catch (SQLiteException | ArithmeticException ex) {
            Toast.makeText(this, "No se pudieron cargar las cuentas.", Toast.LENGTH_LONG).show();
        }
    }

    private void mostrarFormulario() {
        View form = getLayoutInflater().inflate(R.layout.dialog_cuenta, null);
        EditText nombre = form.findViewById(R.id.nombre);
        EditText saldo = form.findViewById(R.id.saldo_inicial);
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Crear cuenta")
                .setView(form).setNegativeButton("Cancelar", null).setPositiveButton("Guardar", null).create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (nombre.getText().toString().trim().isEmpty()) { nombre.setError("Escribe un nombre."); return; }
            try {
                service.crearCuenta(nombre.getText().toString(), Dinero.parsear(saldo.getText().toString()));
                dialog.dismiss();
                actualizar();
            } catch (IllegalArgumentException ex) { saldo.setError(ex.getMessage()); }
            catch (SQLiteException ex) {
                Toast.makeText(this, "No se guardó la cuenta. Intenta nuevamente.", Toast.LENGTH_LONG).show();
            }
        }));
        dialog.show();
    }
    @Override protected void onDestroy() { if (helper != null) helper.close(); super.onDestroy(); }
}
