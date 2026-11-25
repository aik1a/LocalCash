package com.aik1a.localcash.data;

import com.aik1a.localcash.domain.*;
import java.util.*;

public final class ContactoRepository {
  private final LocalCashStore store;

  public ContactoRepository(LocalCashStore store) {
    this.store = store;
  }

  public long crear(Contacto contacto) {
    long id = store.nuevoId();
    store.guardar(
        "contactos", id, LocalCashStore.mapa("id", id, "nombre", contacto.getNombre(), "usos", 0L));
    return id;
  }

  public void renombrar(long id, String nombre) {
    Contacto c = new Contacto(id, nombre);
    Map<String, Object> m = store.buscar("contactos", id);
    store.actualizarCampo("contactos", id, "nombre", c.getNombre());
  }

  public void eliminarSinUso(long id) {
    buscar(id);
    for (Compra c : new CompraRepository(store).listar())
      if (c.getContactoId() != null && c.getContactoId() == id)
        throw new IllegalArgumentException("Este contacto tiene compras asociadas.");
    if (!new DeudaRepository(store).listarPorContacto(id).isEmpty()
        || LocalCashStore.numero(store.buscar("contactos", id), "usos") != 0)
      throw new IllegalArgumentException(
          "No puedes eliminar este contacto porque tiene compras o deudas asociadas. Su historial"
              + " se conserva.");
    store.borrar("contactos", id);
  }

  public List<Contacto> listar() {
    List<Contacto> r = new ArrayList<>();
    for (Map<String, Object> m : store.listar("contactos"))
      r.add(new Contacto(LocalCashStore.numero(m, "id"), LocalCashStore.texto(m, "nombre")));
    r.sort(Comparator.comparingLong(Contacto::getId));
    return r;
  }

  public Contacto buscar(long id) {
    Map<String, Object> m = store.buscar("contactos", id);
    return new Contacto(id, LocalCashStore.texto(m, "nombre"));
  }
}
