package com.aik1a.localcash.presentation;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.database.sqlite.SQLiteException;
import android.view.View;
import android.widget.*;
import com.aik1a.localcash.R;
import com.aik1a.localcash.application.MovimientoService;
import com.aik1a.localcash.data.LocalCashDbHelper;
import com.aik1a.localcash.domain.*;
import java.text.DateFormat;
import java.util.*;

public final class CuentaActivity extends Activity {
    public static final String CUENTA_ID = "cuenta_id";
    private long cuentaId;
    private LocalCashDbHelper helper;
    private MovimientoService service;
    private ListView lista;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_cuenta);
        Pantalla.aplicarMargenes(findViewById(R.id.root));
        cuentaId = getIntent().getLongExtra(CUENTA_ID, -1);
        helper = new LocalCashDbHelper(this);
        service = new MovimientoService(helper);
        lista = findViewById(R.id.movimientos);
        lista.setEmptyView(findViewById(R.id.vacio));
        findViewById(R.id.volver).setOnClickListener(v -> finish());
        findViewById(R.id.registrar_movimiento).setOnClickListener(v -> mostrarFormulario());
    }
    @Override protected void onResume() { super.onResume(); actualizar(); }

    private void actualizar() {
        try {
            Cuenta cuenta = service.buscarCuenta(cuentaId);
            ((TextView) findViewById(R.id.nombre_cuenta)).setText(cuenta.getNombre());
            ((TextView) findViewById(R.id.saldo)).setText("Saldo: " + Dinero.formatear(service.calcularSaldo(cuentaId)));
            List<Movimiento> historial = service.listarMovimientos(cuentaId);
            Collections.reverse(historial);
            List<Map<String, String>> filas = new ArrayList<>();
            DateFormat fecha = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
            for (Movimiento movimiento : historial) {
                Map<String, String> fila = new HashMap<>();
                fila.put("monto", movimiento.getTipo().name() + " · " + Dinero.formatear(movimiento.getMontoConSigno()));
                fila.put("detalle", fecha.format(new Date(movimiento.getFecha()))
                        + (movimiento.getDescripcion().isEmpty() ? "" : " · " + movimiento.getDescripcion()));
                filas.add(fila);
            }
            lista.setAdapter(new SimpleAdapter(this, filas, android.R.layout.simple_list_item_2,
                    new String[]{"monto", "detalle"}, new int[]{android.R.id.text1, android.R.id.text2}));
        } catch (IllegalArgumentException | SQLiteException | ArithmeticException ex) {
            Toast.makeText(this, "No se pudo abrir la cuenta.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void mostrarFormulario() {
        View form = getLayoutInflater().inflate(R.layout.dialog_movimiento, null);
        EditText monto = form.findViewById(R.id.monto);
        EditText descripcion = form.findViewById(R.id.descripcion);
        RadioGroup tipo = form.findViewById(R.id.tipo);
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Registrar movimiento")
                .setView(form).setNegativeButton("Cancelar", null).setPositiveButton("Guardar", null).create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            int elegido = tipo.getCheckedRadioButtonId();
            if (elegido != R.id.ingreso && elegido != R.id.egreso) {
                Toast.makeText(this, "Selecciona ingreso o egreso.", Toast.LENGTH_SHORT).show(); return;
            }
            try {
                service.registrar(cuentaId, elegido == R.id.ingreso ? Movimiento.Tipo.INGRESO : Movimiento.Tipo.EGRESO,
                        Dinero.parsear(monto.getText().toString()), descripcion.getText().toString());
                dialog.dismiss(); actualizar();
            } catch (IllegalArgumentException ex) { monto.setError(ex.getMessage()); }
            catch (ArithmeticException ex) { monto.setError("El movimiento excede el rango del saldo."); }
            catch (SQLiteException ex) {
                Toast.makeText(this, "No se guardó el movimiento. Intenta nuevamente.", Toast.LENGTH_LONG).show();
            }
        }));
        dialog.show();
    }
    @Override protected void onDestroy() { if (helper != null) helper.close(); super.onDestroy(); }
}
