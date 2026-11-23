package com.aik1a.localcash.presentation;

import android.app.*;
import android.os.Bundle;
import android.content.SharedPreferences;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public final class LocalCashApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private boolean autenticada;
    private int visibles;
    private long generacion;
    private final String proceso = java.util.UUID.randomUUID().toString();
    private FirebaseAuth google;
    private SharedPreferences identidad;
    private String ultimoUid;

    @Override public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        identidad = getSharedPreferences("localcash_identidad", MODE_PRIVATE);
        if (FirebaseApp.initializeApp(this) != null) {
            google = FirebaseAuth.getInstance();
            ultimoUid = google.getCurrentUser() == null ? null : google.getCurrentUser().getUid();
            google.addAuthStateListener(auth -> {
                String uid = auth.getCurrentUser() == null ? null : auth.getCurrentUser().getUid();
                if (!java.util.Objects.equals(uid, ultimoUid)) {
                    if (autenticada) generacion++;
                    autenticada = false;
                    ultimoUid = uid;
                }
            });
        }
    }
    FirebaseAuth googleAuth() { return google; }
    boolean identidadGoogleValida() {
        FirebaseUser usuario = google == null ? null : google.getCurrentUser();
        return usuario != null && usuario.getUid().equals(identidad.getString("uid_local", null));
    }
    /** Guardia temporal: SQLite aún no está separado por UID en este bloque WIP. */
    boolean aceptarUsuarioGoogle() {
        FirebaseUser usuario = google == null ? null : google.getCurrentUser();
        if (usuario == null) return false;
        String propietario = identidad.getString("uid_local", null);
        if (propietario == null) {
            // No adjudicar datos de una instalación antigua al primer Google que acceda.
            if (getDatabasePath("localcash.db").exists()) return false;
            return identidad.edit().putString("uid_local", usuario.getUid()).commit();
        }
        return propietario.equals(usuario.getUid());
    }
    boolean autenticada() { return autenticada && identidadGoogleValida(); }
    long generacion() { return generacion; }
    String proceso() { return proceso; }
    void autenticarDesdePrompt() { autenticada = identidadGoogleValida(); }
    @Override public void onActivityStarted(Activity activity) { visibles++; }
    @Override public void onActivityStopped(Activity activity) {
        visibles--;
        if (visibles == 0 && !activity.isChangingConfigurations()) { autenticada = false; generacion++; }
    }
    @Override public void onActivityCreated(Activity a, Bundle b) { }
    @Override public void onActivityResumed(Activity a) { }
    @Override public void onActivityPaused(Activity a) { }
    @Override public void onActivitySaveInstanceState(Activity a, Bundle b) { }
    @Override public void onActivityDestroyed(Activity a) { }
}
