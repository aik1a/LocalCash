package com.aik1a.localcash.presentation;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import com.aik1a.localcash.R;

final class CuentasVisual {
    private CuentasVisual() { }
    static Context contexto(ActividadProtegida activity) {
        return new ContextThemeWrapper(activity, R.style.Cuentas_Dialogo);
    }
    static View enmarcarNombre(ActividadProtegida activity, EditText campo) {
        ScrollView contenedor = new ScrollView(campo.getContext());
        int margen = Math.round(20 * activity.getResources().getDisplayMetrics().density);
        contenedor.setPadding(margen, margen, margen, margen);
        contenedor.addView(campo, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return contenedor;
    }
}
