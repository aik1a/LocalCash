package com.aik1a.localcash.presentation;

import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.*;
import com.aik1a.localcash.R;
import java.util.*;

/** Todas las rutas privadas verifican sesión antes de crear su contenido/repositorios. */
public abstract class ActividadProtegida extends Activity {
    private boolean creada;
    private Bundle estadoInicial;
    private final List<Dialog> dialogos = new ArrayList<>();
    private View privado;
    @Override public final void onCreate(Bundle state) {
        super.onCreate(state); estadoInicial = state;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_bloqueo);
        findViewById(R.id.autenticar).setOnClickListener(v -> abrirGate());
        findViewById(R.id.salir).setOnClickListener(v -> finishAffinity());
    }
    protected abstract void crearContenido(Bundle state);
    protected void actualizarContenido() { }
    protected final boolean contenidoCreado() { return creada; }
    protected final <T extends Dialog> T protegerDialogo(T dialogo) {
        if (dialogo.getWindow() != null) dialogo.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        dialogos.add(dialogo); return dialogo;
    }
    private LocalCashApplication sesion() { return (LocalCashApplication)getApplication(); }
    @Override protected void onResume() {
        super.onResume();
        if (!sesion().autenticada()) { if (privado != null) privado.setVisibility(View.GONE); abrirGate(); return; }
        if (!creada) {
            crearContenido(estadoInicial); creada = true;
            ViewGroup content = findViewById(android.R.id.content);
            privado = content.getChildAt(0);
            if (privado != null && estadoInicial != null) {
                SparseArray<Parcelable> vistas = estadoInicial.getSparseParcelableArray("contenido_vistas");
                if (vistas != null) privado.restoreHierarchyState(vistas);
            }
            estadoInicial = null;
        }
        if (privado != null) privado.setVisibility(View.VISIBLE);
        if (!isFinishing()) actualizarContenido();
    }
    private void abrirGate() { startActivity(new Intent(this, AccesoActivity.class)); }
    @Override protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        if (privado != null) {
            SparseArray<Parcelable> vistas = new SparseArray<>();
            privado.saveHierarchyState(vistas); state.putSparseParcelableArray("contenido_vistas", vistas);
        }
    }
    @Override protected void onPause() {
        if (privado != null) privado.setVisibility(View.GONE);
        super.onPause();
    }
    @Override protected void onDestroy() {
        for (Dialog dialogo : dialogos) if (dialogo.isShowing()) dialogo.dismiss();
        dialogos.clear(); super.onDestroy();
    }
    @Override protected void onStop() {
        if (!isChangingConfigurations()) {
            for (Dialog dialogo : dialogos) if (dialogo.isShowing()) dialogo.dismiss();
            dialogos.clear();
        }
        super.onStop();
    }
}
