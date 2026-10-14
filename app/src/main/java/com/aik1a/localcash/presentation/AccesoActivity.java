package com.aik1a.localcash.presentation;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.biometric.*;
import androidx.core.content.ContextCompat;
import com.aik1a.localcash.R;

/** Único desafío biométrico. Sin PIN, credenciales propias ni fallback abierto. */
public final class AccesoActivity extends FragmentActivity {
    private BiometricPrompt prompt;
    private boolean pendiente;
    private boolean primeraEntrada = true;
    private boolean visible;
    private long generacionInicial;
    private LocalCashApplication sesion() { return (LocalCashApplication)getApplication(); }
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_bloqueo);
        Pantalla.aplicarMargenes(findViewById(R.id.root));
        generacionInicial = sesion().generacion();
        if (state != null) { pendiente = state.getBoolean("pendiente"); primeraEntrada = false; }
        prompt = new BiometricPrompt(this, ContextCompat.getMainExecutor(this), new BiometricPrompt.AuthenticationCallback() {
            @Override public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                if (!visible || !pendiente || generacionInicial != sesion().generacion() || isFinishing()) return;
                pendiente = false; sesion().autenticarDesdePrompt(); finish();
            }
            @Override public void onAuthenticationFailed() { mensaje("Biometría no reconocida. El acceso sigue bloqueado."); }
            @Override public void onAuthenticationError(int code, CharSequence error) {
                pendiente = false;
                if (code == BiometricPrompt.ERROR_NEGATIVE_BUTTON || code == BiometricPrompt.ERROR_USER_CANCELED
                        || code == BiometricPrompt.ERROR_CANCELED)
                    mensaje("Autenticación cancelada. El acceso sigue bloqueado.");
                else if (code == BiometricPrompt.ERROR_LOCKOUT || code == BiometricPrompt.ERROR_LOCKOUT_PERMANENT)
                    mensaje("Biometría bloqueada por Android. Reintenta cuando esté disponible.");
                else mensaje("No se pudo autenticar: " + error + ". El acceso sigue bloqueado.");
            }
        });
        findViewById(R.id.autenticar).setOnClickListener(v -> autenticar());
        findViewById(R.id.salir).setOnClickListener(v -> finishAffinity());
    }
    @Override protected void onResume() {
        super.onResume(); visible = true;
        // Una instancia que salió a background no puede reutilizar un callback anterior.
        if (generacionInicial != sesion().generacion()) { finish(); return; }
        if (primeraEntrada) { primeraEntrada = false; autenticar(); }
    }
    private void autenticar() {
        if (pendiente || !visible || generacionInicial != sesion().generacion()) return;
        int estado = BiometricManager.from(this).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);
        if (estado != BiometricManager.BIOMETRIC_SUCCESS) {
            if (estado == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED)
                mensaje("No hay biometría registrada. Configúrala en Android para acceder.");
            else if (estado == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE)
                mensaje("Este dispositivo no dispone de biometría compatible. Acceso bloqueado.");
            else mensaje("Biometría no disponible. Reintenta cuando Android la habilite.");
            return;
        }
        pendiente = true; mensaje("Autentícate para acceder a LocalCash.");
        prompt.authenticate(new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Desbloquear LocalCash").setSubtitle("Autenticación biométrica")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .setNegativeButtonText("Cancelar").build());
    }
    private void mensaje(String text) { ((TextView)findViewById(R.id.estado_acceso)).setText(text); }
    @Override protected void onPause() { visible = false; super.onPause(); }
    @Override protected void onStop() {
        if (!isChangingConfigurations()) { pendiente = false; prompt.cancelAuthentication(); }
        super.onStop();
    }
    @Override protected void onSaveInstanceState(Bundle state) { super.onSaveInstanceState(state); state.putBoolean("pendiente", pendiente); }
    @Override public void onBackPressed() { moveTaskToBack(true); }
}
