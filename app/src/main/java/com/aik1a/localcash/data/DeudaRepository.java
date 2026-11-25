package com.aik1a.localcash.data;

import com.aik1a.localcash.domain.*;
import java.util.*;

public final class DeudaRepository {
  private final LocalCashStore store;

  public DeudaRepository(LocalCashStore store) {
    this.store = store;
  }

  public void registrar(Reparto reparto) {
    Compra compra = new CompraRepository(store).buscar(reparto.getCompraId());
    if (compra.getDivisa() == null)
      throw new IllegalArgumentException("Define la divisa de la compra primero.");
    if (reparto.getMontoUsuario() == null)
      Reparto.personalizado(
          compra,
          new ArrayList<>(reparto.getMontos().keySet()),
          new ArrayList<>(reparto.getMontos().values()));
    else
      Reparto.personalizadoConUsuario(
          compra,
          new ArrayList<>(reparto.getMontos().keySet()),
          new ArrayList<>(reparto.getMontos().values()),
          reparto.getMontoUsuario());
    Map<String, Object> m = store.buscar("compras", compra.getId());
    if (m.get("reparto") != null)
      throw new IllegalArgumentException("Esta compra ya tiene un reparto.");
    Map<String, Object> participantes = new LinkedHashMap<>();
    for (Map.Entry<Long, Long> e : reparto.getMontos().entrySet())
      participantes.put(
          Long.toString(e.getKey()),
          LocalCashStore.mapa("monto", e.getValue(), "estado", "PENDIENTE"));
    m.put("reparto", participantes);
    m.put("monto_usuario", reparto.getMontoUsuario());
    store.transaccion(
        () -> {
          store.guardar("compras", compra.getId(), m);
          for (long id : reparto.getMontos().keySet()) store.incrementarUso("contactos", id, 1);
        });
  }

  @SuppressWarnings("unchecked")
  public void cambiarEstado(long compraId, long contactoId, boolean pagada) {
    Map<String, Object> m = store.buscar("compras", compraId);
    Map<String, Object> participantes = (Map<String, Object>) m.get("reparto");
    if (participantes == null || !participantes.containsKey(Long.toString(contactoId)))
      throw new IllegalArgumentException("La deuda ya no existe. Vuelve a abrir el reparto.");
    Map<String, Object> d = (Map<String, Object>) participantes.get(Long.toString(contactoId));
    if (LocalCashStore.numero(d, "monto") <= 0)
      throw new IllegalArgumentException("Esta participación no tiene dinero pendiente de pago.");
    d.put("estado", pagada ? "PAGADA" : "PENDIENTE");
    m.put("deuda_actualizada_id", Long.toString(contactoId));
    store.guardar("compras", compraId, m);
  }

  public List<Deuda> listarPorContacto(long contactoId) {
    List<Deuda> r = new ArrayList<>();
    for (Compra c : new CompraRepository(store).listar())
      for (Deuda d : listarPorCompra(c.getId()))
        if (d.getContacto().getId() == contactoId) r.add(d);
    r.sort(Comparator.comparingLong(Deuda::getCompraId).reversed());
    return r;
  }

  public Long buscarMontoUsuario(long compraId) {
    return (Long) store.buscar("compras", compraId).get("monto_usuario");
  }

  @SuppressWarnings("unchecked")
  public List<Deuda> listarPorCompra(long compraId) {
    List<Deuda> r = new ArrayList<>();
    Map<String, Object> participantes =
        (Map<String, Object>) store.buscar("compras", compraId).get("reparto");
    if (participantes != null)
      for (Map.Entry<String, Object> e : participantes.entrySet()) {
        Map<String, Object> d = (Map<String, Object>) e.getValue();
        long id = Long.parseLong(e.getKey());
        r.add(
            new Deuda(
                compraId,
                new ContactoRepository(store).buscar(id),
                LocalCashStore.numero(d, "monto"),
                LocalCashStore.texto(d, "estado")));
      }
    r.sort(Comparator.comparingLong(d -> d.getContacto().getId()));
    return r;
  }
}
