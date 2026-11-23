package com.aik1a.localcash.presentation;

import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import com.aik1a.localcash.R;
import com.aik1a.localcash.data.ConfiguracionRepository;
import com.aik1a.localcash.data.LocalCashStore;
import java.util.*;

public abstract class ActividadProtegida extends Activity {
    private boolean creada, visible, gateAbierto, actualizando;
    private Bundle estadoInicial;
    private final List<Dialog> dialogos = new ArrayList<>();
    private View privado, bloqueo;
    private AlertDialog configuracion;
    private LocalCashStore store;
    private String uidContenido;
    private final LocalCashStore.Listener datos = new LocalCashStore.Listener() {
        @Override public void alCambiar() { procesarDatos(); }
        @Override public void alError(String mensaje) {
            if (!visible || !sesion().autenticada()) return;
            if (store != null && store.estaListo()) {
                android.widget.Toast.makeText(ActividadProtegida.this, mensaje, android.widget.Toast.LENGTH_LONG).show();
                procesarDatos();
            } else mostrarBloqueo(mensaje + " Comprueba la conexión y reintenta.");
        }
    };
    private final com.google.firebase.auth.FirebaseAuth.AuthStateListener estadoGoogle = auth -> {
        if (visible && !sesion().autenticada()) {
            mostrarBloqueo("Inicia sesión con Google para continuar.");
            abrirGate();
        }
    };

    @Override public final void onCreate(Bundle state) {
        super.onCreate(state);
        estadoInicial = state;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_bloqueo);
        bloqueo = ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);
        findViewById(R.id.autenticar).setOnClickListener(v -> reintentar());
        findViewById(R.id.google_acceso).setOnClickListener(v -> abrirGate());
        findViewById(R.id.salir).setOnClickListener(v -> finishAffinity());
    }
    protected abstract void crearContenido(Bundle state);
    protected void actualizarContenido() { }
    protected final boolean contenidoCreado() { return creada; }
    protected final <T extends Dialog> T protegerDialogo(T dialogo) {
        if (dialogo.getWindow() != null) dialogo.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        dialogos.add(dialogo);
        return dialogo;
    }
    private LocalCashApplication sesion() { return (LocalCashApplication)getApplication(); }

    @Override protected void onResume() {
        super.onResume();
        visible = true;
        gateAbierto = false;
        if (sesion().googleAuth() != null) sesion().googleAuth().addAuthStateListener(estadoGoogle);
        if (!sesion().autenticada()) {
            mostrarBloqueo("Autentícate para acceder a LocalCash.");
            abrirGate();
            return;
        }
        cargarDatos();
    }
    private void cargarDatos() {
        try {
            LocalCashStore siguiente = LocalCashStore.get(this);
            // Nunca reutilizar vistas/servicios de otro UID, aunque la Activity siga en la pila.
            if (creada && !siguiente.uid().equals(uidContenido)) {
                startActivity(new Intent(this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
                return;
            }
            if (store != null && store != siguiente) store.quitarListener(datos);
            store = siguiente;
            if (estadoInicial != null && !store.uid().equals(estadoInicial.getString("contenido_uid")))
                estadoInicial = null;
            if (!store.estaListo()) mostrarBloqueo("Preparando tus datos. Conecta para completar la primera descarga; "
                    + "todavía no están disponibles para uso offline.");
            store.iniciar(datos);
        } catch (IllegalStateException error) {
            mostrarBloqueo("No se pudieron preparar los datos. Revisa la sesión y la conexión.");
        }
    }
    private void reintentar() {
        if (!sesion().autenticada()) { abrirGate(); return; }
        if (store == null) cargarDatos();
        else {
            mostrarBloqueo("Reintentando la carga de tus datos…");
            try { store.reintentar(); }
            catch (IllegalStateException error) { mostrarBloqueo("La sesión cambió. Vuelve a iniciar sesión."); }
        }
    }
    private void procesarDatos() {
        if (!visible || isFinishing() || !sesion().autenticada() || store == null
                || !store.estaListo() || actualizando) return;
        actualizando = true;
        try {
            if (asegurarConfiguracion()) mostrarContenido();
        } catch (IllegalArgumentException | IllegalStateException | ArithmeticException error) {
            mostrarBloqueo("No se pudieron abrir los datos. Comprueba la conexión y reintenta.");
        } finally { actualizando = false; }
    }
    private boolean asegurarConfiguracion() {
        if (new ConfiguracionRepository(store).buscarDivisa() != null) return true;
        if (configuracion == null || !configuracion.isShowing()) {
            mostrarBloqueo("Elige tu moneda principal para continuar.");
            configuracion = DivisasUI.solicitar(this, "Tu moneda principal",
                    "Se usará al crear cuentas y compras. Puedes elegir otra moneda en cada una.",
                    com.aik1a.localcash.domain.Divisa.CLP, true,
                    divisa -> new ConfiguracionRepository(store).configurarPrimeraVez(divisa),
                    () -> { if (sesion().autenticada() && !isFinishing()) procesarDatos(); });
        }
        return false;
    }
    private void mostrarContenido() {
        if (!creada) {
            crearContenido(estadoInicial);
            creada = true;
            uidContenido = store.uid();
            ViewGroup content = findViewById(android.R.id.content);
            privado = content.getChildAt(0);
            if (privado != null && estadoInicial != null) {
                SparseArray<Parcelable> vistas = estadoInicial.getSparseParcelableArray("contenido_vistas");
                if (vistas != null) privado.restoreHierarchyState(vistas);
            }
            estadoInicial = null;
        } else if (privado != null && privado.getParent() == null) {
            ViewGroup content = findViewById(android.R.id.content);
            content.removeAllViews();
            content.addView(privado);
        }
        if (privado != null) privado.setVisibility(View.VISIBLE);
        if (!isFinishing()) actualizarContenido();
    }
    private void mostrarBloqueo(String mensaje) {
        if (privado != null) privado.setVisibility(View.GONE);
        for (Dialog dialogo : dialogos) if (dialogo.isShowing()) dialogo.dismiss();
        if (configuracion != null && configuracion.isShowing()) configuracion.dismiss();
        if (bloqueo.getParent() == null) {
            ViewGroup content = findViewById(android.R.id.content);
            content.removeAllViews();
            content.addView(bloqueo);
        }
        ((TextView)bloqueo.findViewById(R.id.estado_acceso)).setText(mensaje);
        bloqueo.findViewById(R.id.google_acceso).setVisibility(sesion().autenticada() ? View.GONE : View.VISIBLE);
        ((Button)bloqueo.findViewById(R.id.autenticar)).setText(sesion().autenticada()
                ? "Reintentar carga" : "Reintentar autenticación");
    }
    private void abrirGate() {
        if (gateAbierto || !visible) return;
        gateAbierto = true;
        startActivity(new Intent(this, AccesoActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
    }
    @Override protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        if (privado != null) {
            SparseArray<Parcelable> vistas = new SparseArray<>();
            privado.saveHierarchyState(vistas);
            state.putSparseParcelableArray("contenido_vistas", vistas);
            state.putString("contenido_uid", uidContenido);
        }
    }
    @Override protected void onPause() {
        visible = false;
        if (store != null) store.quitarListener(datos);
        if (sesion().googleAuth() != null) sesion().googleAuth().removeAuthStateListener(estadoGoogle);
        if (privado != null) privado.setVisibility(View.GONE);
        super.onPause();
    }
    @Override protected void onDestroy() {
        if (store != null) store.quitarListener(datos);
        for (Dialog dialogo : dialogos) if (dialogo.isShowing()) dialogo.dismiss();
        dialogos.clear();
        super.onDestroy();
    }
    @Override protected void onStop() {
        if (!isChangingConfigurations()) {
            for (Dialog dialogo : dialogos) if (dialogo.isShowing()) dialogo.dismiss();
            dialogos.clear();
        }
        super.onStop();
    }
}
