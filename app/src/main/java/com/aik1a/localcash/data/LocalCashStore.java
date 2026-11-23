package com.aik1a.localcash.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.security.SecureRandom;
import java.util.*;

/** Datos de la sesión: Firestore es la única persistencia; estas listas sólo viven en memoria. */
public final class LocalCashStore implements AutoCloseable {
  public interface Listener {
    void alCambiar();

    void alError(String mensaje);
  }

  private static LocalCashStore actual;
  private static boolean configurada;
  private static final String[] COLECCIONES = {
    "configuracion", "cuentas", "movimientos", "contactos", "compras"
  };
  private final String uid;
  private final FirebaseFirestore db;
  private final SharedPreferences preferencias;
  private final Map<String, Map<String, Map<String, Object>>> datos = new HashMap<>();
  private final Map<String, Boolean> cache = new HashMap<>(), pendientes = new HashMap<>();
  private final Set<String> cargadas = new HashSet<>(), servidor = new HashSet<>();
  private final List<ListenerRegistration> registros = new ArrayList<>();
  private final Set<Listener> oyentes = new LinkedHashSet<>();
  private final SecureRandom azar = new SecureRandom();
  private WriteBatch lote;
  private int profundidad, enviosEnCurso;
  private boolean iniciada, invalida;

  private LocalCashStore(Context context, String uid) {
    this.uid = uid;
    db = FirebaseFirestore.getInstance();
    // Se consulta el historial completo; no expulsar parte de él para calcular saldos offline.
    if (!configurada)
      db.setFirestoreSettings(
          new FirebaseFirestoreSettings.Builder()
              .setPersistenceEnabled(true)
              .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
              .build());
    configurada = true;
    preferencias =
        context
            .getApplicationContext()
            .getSharedPreferences("firestore_preparacion", Context.MODE_PRIVATE);
    for (String c : COLECCIONES) datos.put(c, new LinkedHashMap<>());
  }

  public static synchronized LocalCashStore get(Context context) {
    if (FirebaseAuth.getInstance().getCurrentUser() == null)
      throw new IllegalStateException("Inicia sesión con Google.");
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    if (actual == null || !actual.uid.equals(uid)) {
      if (actual != null) actual.invalidar();
      actual = new LocalCashStore(context, uid);
    }
    return actual;
  }

  public String uid() {
    return uid;
  }

  private void comprobarUsuario() {
    if (invalida
        || FirebaseAuth.getInstance().getCurrentUser() == null
        || !uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
      throw new IllegalStateException("La sesión cambió. Vuelve a iniciar sesión.");
  }

  public void iniciar(Listener listener) {
    comprobarUsuario();
    oyentes.add(listener);
    if (iniciada) {
      if (estaListo()) listener.alCambiar();
      return;
    }
    iniciada = true;
    for (String c : COLECCIONES)
      registros.add(
          coleccion(c)
              .addSnapshotListener(
                  MetadataChanges.INCLUDE,
                  (snapshot, error) -> {
                    if (invalida) return;
                    if (error != null) {
                      for (Listener l : new ArrayList<>(oyentes))
                        l.alError(
                            "No se pudieron cargar los datos: " + error.getLocalizedMessage());
                      return;
                    }
                    if (snapshot == null) return;
                    Map<String, Map<String, Object>> filas = new LinkedHashMap<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments())
                      filas.put(doc.getId(), copiar(doc.getData()));
                    datos.put(c, filas);
                    cargadas.add(c);
                    cache.put(c, snapshot.getMetadata().isFromCache());
                    pendientes.put(c, snapshot.getMetadata().hasPendingWrites());
                    if (!snapshot.getMetadata().isFromCache()) servidor.add(c);
                    if (servidor.size() == COLECCIONES.length)
                      preferencias.edit().putBoolean(uid, true).apply();
                    if (estaListo()) notificar();
                  }));
  }

  public void quitarListener(Listener listener) {
    oyentes.remove(listener);
  }

  public boolean estaListo() {
    return !invalida
        && cargadas.size() == COLECCIONES.length
        && (servidor.size() == COLECCIONES.length || preferencias.getBoolean(uid, false));
  }

  public boolean hayPendientes() {
    return enviosEnCurso > 0 || pendientes.containsValue(true);
  }

  public boolean desdeCache() {
    return cache.containsValue(true);
  }

  private void notificar() {
    for (Listener l : new ArrayList<>(oyentes)) l.alCambiar();
  }

  private void invalidar() {
    invalida = true;
    for (ListenerRegistration r : registros) r.remove();
    registros.clear();
    oyentes.clear();
    datos.clear();
  }

  @Override
  public void close() {
    /* Cada Activity comparte la sesión. quitarListener libera su observador. */
  }

  private CollectionReference coleccion(String nombre) {
    return db.collection("users").document(uid).collection(nombre);
  }

  private DocumentReference referencia(String nombre, String id) {
    return coleccion(nombre).document(id);
  }

  private void exigirLista() {
    comprobarUsuario();
    if (!estaListo())
      throw new IllegalStateException(
          "Conecta para preparar todos tus datos antes de usar LocalCash offline.");
  }

  public long nuevoId() {
    exigirLista();
    long id;
    do {
      id = azar.nextLong() & Long.MAX_VALUE;
    } while (id == 0);
    return id;
  }

  static Map<String, Object> mapa(Object... pares) {
    Map<String, Object> m = new LinkedHashMap<>();
    for (int i = 0; i < pares.length; i += 2) m.put((String) pares[i], pares[i + 1]);
    return m;
  }

  @SuppressWarnings("unchecked")
  static Map<String, Object> copiar(Map<String, Object> original) {
    Map<String, Object> r = new LinkedHashMap<>();
    if (original == null) return r;
    for (Map.Entry<String, Object> e : original.entrySet()) {
      Object v = e.getValue();
      if (v instanceof Map) v = copiar((Map<String, Object>) v);
      else if (v instanceof List) {
        List<Object> a = new ArrayList<>();
        for (Object x : (List<?>) v) a.add(x instanceof Map ? copiar((Map<String, Object>) x) : x);
        v = a;
      }
      r.put(e.getKey(), v);
    }
    return r;
  }

  static long numero(Map<String, Object> m, String campo) {
    Object n = m.get(campo);
    if (!(n instanceof Long) && !(n instanceof Integer))
      throw new IllegalStateException("Dato inválido: " + campo);
    return ((Number) n).longValue();
  }

  static String texto(Map<String, Object> m, String campo) {
    Object v = m.get(campo);
    if (!(v instanceof String)) throw new IllegalStateException("Dato inválido: " + campo);
    return (String) v;
  }

  Map<String, Object> buscar(String nombre, long id) {
    return buscar(nombre, Long.toString(id));
  }

  Map<String, Object> buscar(String nombre, String id) {
    exigirLista();
    Map<String, Object> m = datos.get(nombre).get(id);
    if (m == null) throw new IllegalArgumentException("El registro ya no existe.");
    return copiar(m);
  }

  boolean existe(String nombre, String id) {
    exigirLista();
    return datos.get(nombre).containsKey(id);
  }

  List<Map<String, Object>> listar(String nombre) {
    exigirLista();
    List<Map<String, Object>> r = new ArrayList<>();
    for (Map<String, Object> m : datos.get(nombre).values()) r.add(copiar(m));
    return r;
  }

  void guardar(String nombre, long id, Map<String, Object> valores) {
    guardar(nombre, Long.toString(id), valores);
  }

  void guardar(String nombre, String id, Map<String, Object> valores) {
    if (lote == null) {
      transaccion(() -> guardar(nombre, id, valores));
      return;
    }
    datos.get(nombre).put(id, copiar(valores));
    lote.set(referencia(nombre, id), valores);
  }

  void actualizarCampo(String nombre, long id, String campo, Object valor) {
    if (lote == null) {
      transaccion(() -> actualizarCampo(nombre, id, campo, valor));
      return;
    }
    Map<String, Object> fila = buscar(nombre, id);
    fila.put(campo, valor);
    datos.get(nombre).put(Long.toString(id), fila);
    lote.update(referencia(nombre, Long.toString(id)), campo, valor);
  }

  void borrar(String nombre, long id) {
    if (lote == null) {
      transaccion(() -> borrar(nombre, id));
      return;
    }
    datos.get(nombre).remove(Long.toString(id));
    lote.delete(referencia(nombre, Long.toString(id)));
  }

  void incrementarUso(String nombre, long id, long delta) {
    Map<String, Object> m = buscar(nombre, id);
    long usos = numero(m, "usos");
    m.put("usos", Math.addExact(usos, delta));
    if (numero(m, "usos") < 0) throw new IllegalStateException("Referencias inconsistentes.");
    datos.get(nombre).put(Long.toString(id), m);
    lote.update(referencia(nombre, Long.toString(id)), "usos", FieldValue.increment(delta));
  }

  public void transaccion(Runnable operacion) {
    exigirLista();
    if (profundidad > 0) {
      profundidad++;
      try {
        operacion.run();
      } finally {
        profundidad--;
      }
      return;
    }
    Map<String, Map<String, Map<String, Object>>> anterior = new HashMap<>();
    for (String c : COLECCIONES) {
      Map<String, Map<String, Object>> copia = new LinkedHashMap<>();
      for (Map.Entry<String, Map<String, Object>> e : datos.get(c).entrySet())
        copia.put(e.getKey(), copiar(e.getValue()));
      anterior.put(c, copia);
    }
    lote = db.batch();
    profundidad = 1;
    try {
      operacion.run();
      WriteBatch listo = lote;
      enviosEnCurso++;
      listo
          .commit()
          .addOnCompleteListener(
              resultado -> {
                enviosEnCurso--;
                if (!invalida) notificar();
              })
          .addOnFailureListener(
              error -> {
                if (invalida) return;
                for (Listener l : new ArrayList<>(oyentes))
                  l.alError(
                      "Firebase rechazó el cambio; revisa el estado actualizado: "
                          + error.getLocalizedMessage());
              });
    } catch (RuntimeException error) {
      datos.clear();
      datos.putAll(anterior);
      throw error;
    } finally {
      lote = null;
      profundidad = 0;
    }
    notificar();
  }
}
