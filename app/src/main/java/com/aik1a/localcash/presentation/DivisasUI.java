package com.aik1a.localcash.presentation;

import android.app.AlertDialog;
import android.view.View;
import android.widget.*;
import android.database.sqlite.SQLiteException;
import com.aik1a.localcash.R;
import com.aik1a.localcash.data.*;
import com.aik1a.localcash.domain.Divisa;

final class DivisasUI {
    interface Guardar { void guardar(Divisa divisa); }
    private DivisasUI() { }
    static void preparar(Spinner selector, Divisa elegida) {
        selector.setAdapter(new ArrayAdapter<>(selector.getContext(), android.R.layout.simple_spinner_dropdown_item, Divisa.values()));
        selector.setSelection((elegida == null ? Divisa.CLP : elegida).ordinal());
    }
    static void ejemplo(EditText campo, Divisa divisa) {
        campo.setHint(divisa.getDecimales() == 0 ? "Ej.: 2500" : "Ej.: 25,50");
    }
    static AlertDialog solicitar(ActividadProtegida activity, String titulo, String mensaje,
                                Divisa inicial, boolean obligatoria, Guardar guardar, Runnable despues) {
        View form = activity.getLayoutInflater().cloneInContext(CuentasVisual.contexto(activity)).inflate(R.layout.dialog_divisa, null);
        Spinner selector = form.findViewById(R.id.divisa);
        TextView error = form.findViewById(R.id.error_divisa);
        preparar(selector, inicial);
        AlertDialog.Builder builder = new AlertDialog.Builder(CuentasVisual.contexto(activity)).setTitle(titulo).setMessage(mensaje)
                .setView(form).setPositiveButton("Guardar", null).setCancelable(!obligatoria);
        if (!obligatoria) builder.setNegativeButton("Más tarde", null);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            try {
                guardar.guardar((Divisa)selector.getSelectedItem());
                dialog.dismiss(); despues.run();
            } catch (IllegalArgumentException ex) { error.setText(ex.getMessage()); }
            catch (SQLiteException ex) { error.setText("No se pudo guardar. Intenta de nuevo."); }
        }));
        activity.protegerDialogo(dialog).show();
        return dialog;
    }
    static AlertDialog cuenta(ActividadProtegida activity, long id, Divisa inicial, Runnable despues) {
        return solicitar(activity, "Divisa de la cuenta", "Elige la moneda de estos importes. No se convertirán ni redondearán.",
                inicial, false, divisa -> {
                    try (LocalCashDbHelper helper = new LocalCashDbHelper(activity)) {
                        new CuentaRepository(helper).asignarDivisa(id, divisa);
                    }
                }, despues);
    }
    static AlertDialog compra(ActividadProtegida activity, long id, Divisa inicial, Runnable despues) {
        return solicitar(activity, "Divisa de la compra", "Elige la moneda de estos importes. No se convertirán ni redondearán.",
                inicial, false, divisa -> {
                    try (LocalCashDbHelper helper = new LocalCashDbHelper(activity)) {
                        new CompraRepository(helper).asignarDivisa(id, divisa);
                    }
                }, despues);
    }
}

