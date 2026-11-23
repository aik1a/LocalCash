package com.aik1a.localcash;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.Bundle;

/** Runner del incremento Firebase. Los recorridos SQLite se conservan como fuentes históricas. */
public final class FlowChecks extends Instrumentation {
    private Bundle arguments;

    @Override public void onCreate(Bundle supplied) {
        super.onCreate(supplied);
        arguments = supplied == null ? new Bundle() : new Bundle(supplied);
        start();
    }

    @Override public void onStart() {
        String mode = arguments.getString("mode", "");
        if ("firestore".equals(mode)) {
            FirestoreOfflineChecks.ejecutar(this, arguments);
            return;
        }
        Bundle result = new Bundle();
        result.putString("stream", "LOCALCASH_UNSUPPORTED_MODE " + mode
                + "\nEste runner admite mode=firestore. Los recorridos SQLite son históricos y no se ejecutaron.\n");
        finish(Activity.RESULT_CANCELED, result);
    }
}
