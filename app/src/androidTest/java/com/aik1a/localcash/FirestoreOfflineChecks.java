package com.aik1a.localcash;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.Bundle;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.PersistentCacheSettings;
import com.google.firebase.firestore.Source;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/** Prueba por fases; no usa SQLite, cuentas reales ni configuración Firebase del usuario. */
public final class FirestoreOfflineChecks {
    private static final int TIMEOUT_SECONDS = 30;

    public static void ejecutar(Instrumentation instrumentation, Bundle arguments) {
        Bundle result = new Bundle();
        try {
            String phase = arguments.getString("phase", "");
            String runId = arguments.getString("runId", "");
            require(runId.matches("[a-zA-Z0-9_-]{1,80}"), "runId sintético obligatorio");
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setProjectId("demo-localcash-f1")
                    .setApplicationId("1:123456789:android:localcashf1")
                    .setApiKey("demo-key-not-a-real-credential").build();
            FirebaseApp app = FirebaseApp.initializeApp(instrumentation.getTargetContext(), options, "localcash-f1-prototype");
            FirebaseFirestore db = FirebaseFirestore.getInstance(app);
            // Siempre emulador Android -> host. Sin parámetro para conectar a producción.
            db.setFirestoreSettings(new FirebaseFirestoreSettings.Builder()
                    .setHost("10.0.2.2:8080").setSslEnabled(false)
                    .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build()).build());
            // En cada proceso, detener la red ANTES de consultar o reanudar pendientes.
            await(db.disableNetwork());
            DocumentReference document = db.document("f1/prototype/items/" + runId);
            if ("prepare".equals(phase)) {
                await(db.enableNetwork());
                Map<String, Object> data = new HashMap<>();
                data.put("value", 1L);
                data.put("synthetic", true);
                await(document.set(data));
                check(await(document.get(Source.SERVER)), 1L, false);
                check(await(document.get(Source.CACHE)), 1L, false);
            } else if ("offline".equals(phase)) {
                check(await(document.get(Source.CACHE)), 1L, false);
                CountDownLatch savedLocally = new CountDownLatch(1);
                AtomicReference<Exception> listenerError = new AtomicReference<>();
                ListenerRegistration listener = document.addSnapshotListener(MetadataChanges.INCLUDE, (snapshot, error) -> {
                    if (error != null) { listenerError.set(error); savedLocally.countDown(); }
                    else if (snapshot != null && Long.valueOf(2L).equals(snapshot.getLong("value"))
                            && snapshot.getMetadata().hasPendingWrites()) savedLocally.countDown();
                });
                try {
                    Task<Void> remoteConfirmation = document.update("value", 2L);
                    require(savedLocally.await(TIMEOUT_SECONDS, TimeUnit.SECONDS), "No llegó snapshot local");
                    require(listenerError.get() == null, "Error listener: " + listenerError.get());
                    check(await(document.get(Source.CACHE)), 2L, true);
                    require(!remoteConfirmation.isComplete(), "No confundir guardado local con confirmación remota");
                } finally { listener.remove(); }
            } else if ("resumeOffline".equals(phase)) {
                check(await(document.get(Source.CACHE)), 2L, true);
            } else if ("reconnect".equals(phase)) {
                await(db.enableNetwork());
                await(db.waitForPendingWrites());
                check(await(document.get(Source.SERVER)), 2L, false);
                // Identidad estable: se actualiza el mismo documento, no se añade otro.
                require(await(db.collection("f1/prototype/items").whereEqualTo(FieldPath.documentId(), document.getId())
                        .get(Source.SERVER)).size() == 1, "Documento duplicado");
            } else if ("reject".equals(phase)) {
                await(db.enableNetwork());
                try {
                    await(document.update("value", 99L));
                    throw new AssertionError("Reglas aceptaron valor inválido");
                } catch (ExecutionException expected) {
                    require(expected.getCause() instanceof FirebaseFirestoreException, "Rechazo no Firestore");
                    FirebaseFirestoreException error = (FirebaseFirestoreException) expected.getCause();
                    require(error.getCode() == FirebaseFirestoreException.Code.PERMISSION_DENIED,
                            "Código esperado PERMISSION_DENIED: " + error.getCode());
                }
                check(await(document.get(Source.SERVER)), 2L, false);
                check(await(document.get(Source.CACHE)), 2L, false);
            } else throw new IllegalArgumentException("Fase desconocida: " + phase);
            result.putString("stream", "LOCALCASH_FIRESTORE_PASS " + phase + "\n");
            instrumentation.finish(Activity.RESULT_OK, result);
        } catch (Throwable error) {
            result.putString("stream", "LOCALCASH_FIRESTORE_FAIL\n" + android.util.Log.getStackTraceString(error));
            instrumentation.finish(Activity.RESULT_CANCELED, result);
        }
    }

    private static <T> T await(Task<T> task) throws Exception {
        return Tasks.await(task, TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    private static void check(DocumentSnapshot snapshot, long value, boolean pending) {
        require(snapshot.exists(), "Documento no preparado/disponible");
        require(Long.valueOf(value).equals(snapshot.getLong("value")), "Valor local/remoto incorrecto");
        require(Boolean.TRUE.equals(snapshot.getBoolean("synthetic")), "Datos no sintéticos");
        require(snapshot.getMetadata().hasPendingWrites() == pending, "Estado pendiente incorrecto");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
