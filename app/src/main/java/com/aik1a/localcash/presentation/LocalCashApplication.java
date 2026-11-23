package com.aik1a.localcash.presentation;

import android.app.*;
import android.os.Bundle;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public final class LocalCashApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private boolean autenticada;
    private int visibles;
    private long generacion;
    private final String proceso = java.util.UUID.randomUUID().toString();
    private FirebaseAuth google;
    private String uidAutenticado;
    private String ultimoUid;

    @Override public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        if (FirebaseApp.initializeApp(this) != null) {
            google = FirebaseAuth.getInstance();
            ultimoUid = google.getCurrentUser() == null ? null : google.getCurrentUser().getUid();
            google.addAuthStateListener(auth -> {
                String uid = auth.getCurrentUser() == null ? null : auth.getCurrentUser().getUid();
                if (!java.util.Objects.equals(uid, ultimoUid)) {
                    if (autenticada) generacion++;
                    autenticada = false;
                    uidAutenticado = null;
                    ultimoUid = uid;
                }
            });
        }
    }
    FirebaseAuth googleAuth() { return google; }
    boolean identidadGoogleValida() {
        FirebaseUser usuario = google == null ? null : google.getCurrentUser();
        if (usuario == null) return false;
        for (com.google.firebase.auth.UserInfo proveedor : usuario.getProviderData())
            if ("google.com".equals(proveedor.getProviderId())) return true;
        return false;
    }
    boolean aceptarUsuarioGoogle() { return identidadGoogleValida(); }
    boolean autenticada() {
        FirebaseUser usuario = google == null ? null : google.getCurrentUser();
        return autenticada && identidadGoogleValida() && usuario.getUid().equals(uidAutenticado);
    }
    long generacion() { return generacion; }
    String proceso() { return proceso; }
    void autenticarDesdePrompt() {
        autenticada = identidadGoogleValida();
        uidAutenticado = autenticada ? google.getCurrentUser().getUid() : null;
    }
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
