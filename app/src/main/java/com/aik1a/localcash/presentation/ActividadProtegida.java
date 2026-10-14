package com.aik1a.localcash.presentation;

import android.app.*;
import android.content.Intent;
import android.os.Bundle;
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
    protected final <T extends Dialog> T protegerDialogo(T dialogo) { dialogos.add(dialogo); return dialogo; }
    private LocalCashApplication sesion() { return (LocalCashApplication)getApplication(); }
    @Override protected void onResume() {
        super.onResume();
        if (!sesion().autenticada()) { if (privado != null) privado.setVisibility(View.GONE); abrirGate(); return; }
        if (!creada) {
            crearContenido(estadoInicial); creada = true; estadoInicial = null;
            ViewGroup content = findViewById(android.R.id.content);
            privado = content.getChildAt(0);
        }
        if (privado != null) privado.setVisibility(View.VISIBLE);
        if (!isFinishing()) actualizarContenido();
    }
    private void abrirGate() { startActivity(new Intent(this, AccesoActivity.class)); }
    @Override protected void onPause() {
        if (privado != null) privado.setVisibility(View.GONE);
        super.onPause();
    }
    @Override protected void onStop() {
        if (!isChangingConfigurations()) {
            for (Dialog dialogo : dialogos) if (dialogo.isShowing()) dialogo.dismiss();
            dialogos.clear();
        }
        super.onStop();
    }
}
