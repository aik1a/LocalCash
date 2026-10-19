package com.aik1a.localcash.presentation;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.view.View;
import android.widget.*;
import com.aik1a.localcash.R;
import com.aik1a.localcash.application.MovimientoService;
import com.aik1a.localcash.data.LocalCashDbHelper;
import com.aik1a.localcash.domain.*;
import java.util.*;

/** El usuario confirma cuenta e importe; el servicio revalida el saldo al guardar. */
final class PagoCompraUI {
    interface Pago { void confirmar(long cuentaId); }
    private PagoCompraUI() { }
    static void mostrar(ActividadProtegida activity, LocalCashDbHelper helper, Compra compra, Pago pago) {
        if (compra.getDivisa() == null) { AccionesUI.error(activity, "Define la divisa de la compra primero."); return; }
        try {
            MovimientoService cuentasService = new MovimientoService(helper);
            List<Cuenta> cuentas = new ArrayList<>(); List<String> opciones = new ArrayList<>();
            for (Cuenta cuenta : cuentasService.listarCuentas()) {
                if (cuenta.getDivisa() == compra.getDivisa()) {
                    cuentas.add(cuenta);
                    opciones.add(cuenta.getNombre() + " · " + Dinero.formatear(cuentasService.calcularSaldo(cuenta.getId()), cuenta.getDivisa()));
                }
            }
            if (cuentas.isEmpty()) {
                activity.protegerDialogo(new AlertDialog.Builder(activity).setTitle("Necesitas una cuenta en " + compra.getDivisa().name())
                        .setMessage("Crea una cuenta en esta divisa y vuelve para pagar la compra.")
                        .setNegativeButton("Volver", null).setPositiveButton("Ir a cuentas", (d, w) ->
                                activity.startActivity(new Intent(activity, MainActivity.class))).create()).show(); return;
            }
            View form = activity.getLayoutInflater().inflate(R.layout.dialog_pago_compra, null);
            Spinner selector = form.findViewById(R.id.cuenta_pago);
            TextView saldo = form.findViewById(R.id.saldo_pago), error = form.findViewById(R.id.error_pago);
            ((TextView)form.findViewById(R.id.total_pago)).setText("Total a pagar: " + Dinero.formatear(compra.calcularTotal(), compra.getDivisa()));
            selector.setAdapter(new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, opciones));
            selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try { saldo.setText("Saldo disponible: " + Dinero.formatear(cuentasService.calcularSaldo(cuentas.get(position).getId()), compra.getDivisa())); error.setText(""); }
                    catch (IllegalArgumentException | ArithmeticException | SQLiteException ex) { error.setText("No se pudo consultar el saldo. Vuelve a abrir el pago."); }
                }
                public void onNothingSelected(AdapterView<?> parent) { }
            });
            AlertDialog dialog = new AlertDialog.Builder(activity).setTitle("Pagar compra").setView(form)
                    .setNegativeButton("Cancelar", null).setPositiveButton("Confirmar pago", null).create();
            dialog.setOnShowListener(x -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                try {
                    pago.confirmar(cuentas.get(selector.getSelectedItemPosition()).getId());
                    dialog.dismiss(); AccionesUI.exito(activity, "Compra pagada");
                } catch (IllegalArgumentException ex) { error.setText(ex.getMessage()); }
                catch (ArithmeticException ex) { error.setText("El importe está fuera de rango."); }
                catch (SQLiteException ex) { error.setText("No se guardó el pago. Intenta nuevamente."); }
            }));
            activity.protegerDialogo(dialog).show();
        } catch (IllegalArgumentException | ArithmeticException | SQLiteException ex) { AccionesUI.error(activity, "No se pudieron cargar las cuentas para pagar."); }
    }
}
