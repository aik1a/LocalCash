package com.aik1a.localcash.presentation;

import android.app.*;
import android.os.Bundle;

public final class LocalCashApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private boolean autenticada;
    private int visibles;
    private long generacion;
    private final String proceso = java.util.UUID.randomUUID().toString();
    @Override public void onCreate() { super.onCreate(); registerActivityLifecycleCallbacks(this); }
    boolean autenticada() { return autenticada; }
    long generacion() { return generacion; }
    String proceso() { return proceso; }
    void autenticarDesdePrompt() { autenticada = true; }
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

