package com.aik1a.localcash.presentation;

import android.os.Bundle;
import android.os.CancellationSignal;
import android.view.View;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.CustomCredential;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.GetCredentialCancellationException;
import androidx.credentials.exceptions.NoCredentialException;
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.biometric.*;
import androidx.core.content.ContextCompat;
import com.aik1a.localcash.R;

public final class AccesoActivity extends FragmentActivity {
    private BiometricPrompt prompt;
    private boolean googleEnCurso;
    private CancellationSignal cancelacionGoogle;
    private boolean pendiente;
    private boolean visible;
    private long generacionInicial;
    private final com.google.firebase.auth.FirebaseAuth.AuthStateListener estadoGoogle = auth -> {
        if (visible && !googleEnCurso && auth.getCurrentUser() != null) continuar();
    };
    private LocalCashApplication sesion() { return (LocalCashApplication)getApplication(); }
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_bloqueo);
        Pantalla.aplicarMargenes(findViewById(R.id.raiz_bloqueo));
        generacionInicial = sesion().generacion();
        if (state != null && sesion().proceso().equals(state.getString("proceso"))
                && generacionInicial == state.getLong("generacion")) {
            pendiente = state.getBoolean("pendiente");
        }
        prompt = new BiometricPrompt(this, ContextCompat.getMainExecutor(this), new BiometricPrompt.AuthenticationCallback() {
            @Override public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                if (!visible || !pendiente || generacionInicial != sesion().generacion() || isFinishing()) return;
                pendiente = false; sesion().autenticarDesdePrompt();
                if (sesion().autenticada()) finish();
                else mensaje("La sesión Google ya no está disponible. Vuelve a iniciar sesión.");
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
        findViewById(R.id.google_acceso).setOnClickListener(v -> iniciarGoogle());
        findViewById(R.id.autenticar).setOnClickListener(v -> continuar());
        findViewById(R.id.salir).setOnClickListener(v -> finishAffinity());
    }
    @Override protected void onResume() {
        super.onResume(); visible = true;
        if (sesion().googleAuth() != null) sesion().googleAuth().addAuthStateListener(estadoGoogle);

        if (generacionInicial != sesion().generacion()) {
            pendiente = false;
            generacionInicial = sesion().generacion();
        }
        if (!googleEnCurso) {
            continuar();
        }
    }
    private void continuar() {
        if (sesion().googleAuth() == null) {
            mensaje("Falta configurar Firebase para esta aplicación.");
            findViewById(R.id.google_acceso).setEnabled(false);
            findViewById(R.id.autenticar).setVisibility(View.GONE);
            return;
        }
        if (sesion().googleAuth().getCurrentUser() == null) {
            findViewById(R.id.google_acceso).setVisibility(View.VISIBLE);
            findViewById(R.id.google_acceso).setEnabled(!googleEnCurso);
            findViewById(R.id.autenticar).setVisibility(View.GONE);
            mensaje("Inicia sesión con Google para continuar. El primer acceso necesita conexión.");
            return;
        }
        if (!sesion().aceptarUsuarioGoogle()) {
            sesion().googleAuth().signOut();
            findViewById(R.id.google_acceso).setVisibility(View.VISIBLE);
            findViewById(R.id.google_acceso).setEnabled(true);
            findViewById(R.id.autenticar).setVisibility(View.GONE);
            mensaje("Esta instalación pertenece a otra cuenta o conserva datos anteriores sin vincular. "
                    + "Usa la cuenta original o una instalación de prueba separada; no borres los datos.");
            return;
        }
        findViewById(R.id.google_acceso).setVisibility(View.GONE);
        findViewById(R.id.autenticar).setVisibility(View.VISIBLE);
        generacionInicial = sesion().generacion();
        autenticar();
    }
    private void iniciarGoogle() {
        if (googleEnCurso || sesion().googleAuth() == null) return;
        googleEnCurso = true;
        findViewById(R.id.google_acceso).setEnabled(false);
        mensaje("Abriendo Google…");
        cancelacionGoogle = new CancellationSignal();
        try {
        GetSignInWithGoogleOption opcion = new GetSignInWithGoogleOption.Builder(
                getString(R.string.default_web_client_id)).build();
        GetCredentialRequest solicitud = new GetCredentialRequest.Builder().addCredentialOption(opcion).build();
        CredentialManager.create(this).getCredentialAsync(this, solicitud, cancelacionGoogle,
                ContextCompat.getMainExecutor(this), new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
            @Override public void onResult(GetCredentialResponse respuesta) {
                if (isDestroyed()) return;
                try {
                    if (!(respuesta.getCredential() instanceof CustomCredential)
                            || !GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(respuesta.getCredential().getType())) {
                        errorGoogle("Google devolvió una respuesta no compatible. Reintenta."); return;
                    }
                    GoogleIdTokenCredential token = GoogleIdTokenCredential.createFrom(respuesta.getCredential().getData());
                    sesion().googleAuth().signInWithCredential(
                            com.google.firebase.auth.GoogleAuthProvider.getCredential(token.getIdToken(), null))
                            .addOnCompleteListener(tarea -> {
                                if (isDestroyed()) return;
                                googleEnCurso = false;
                                findViewById(R.id.google_acceso).setEnabled(true);
                                if (tarea.isSuccessful()) { if (visible) continuar(); }
                                else errorGoogle("No se pudo iniciar sesión. Comprueba conexión y configuración de Google, y reintenta.");
                            });
                } catch (Exception error) {
                    errorGoogle("No se pudo leer la respuesta de Google. Reintenta.");
                }
            }
            @Override public void onError(GetCredentialException error) {
                if (isDestroyed()) return;
                if (error instanceof GetCredentialCancellationException)
                    errorGoogle("Inicio de sesión cancelado. Puedes reintentar.");
                else if (error instanceof NoCredentialException)
                    errorGoogle("No se encontró una cuenta Google disponible. Añádela en Android y reintenta.");
                else errorGoogle("Google no está disponible. Comprueba conexión y servicios de Google, y reintenta.");
            }
        });
        } catch (RuntimeException error) {
            errorGoogle("No se pudo abrir Google. Comprueba su configuración y reintenta.");
        }
    }
    private void errorGoogle(String texto) {
        googleEnCurso = false;
        findViewById(R.id.google_acceso).setEnabled(true);
        mensaje(texto);
    }
    private void autenticar() {
        if (!sesion().identidadGoogleValida()) return;
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
    @Override protected void onPause() {
        visible = false;
        if (sesion().googleAuth() != null) sesion().googleAuth().removeAuthStateListener(estadoGoogle);
        super.onPause();
    }
    @Override protected void onStop() {
        if (!isChangingConfigurations()) { pendiente = false; prompt.cancelAuthentication(); }
        super.onStop();
    }
    @Override protected void onSaveInstanceState(Bundle state) { super.onSaveInstanceState(state); state.putBoolean("pendiente", pendiente);
        state.putString("proceso", sesion().proceso()); state.putLong("generacion", generacionInicial); }
    @Override protected void onDestroy() {
        if (cancelacionGoogle != null) cancelacionGoogle.cancel();
        super.onDestroy();
    }
    @Override public void onBackPressed() { moveTaskToBack(true); }
}

