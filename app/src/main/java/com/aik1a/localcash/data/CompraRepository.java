package com.aik1a.localcash.data;

import com.aik1a.localcash.domain.*;
import java.util.*;

public final class CompraRepository {
  private final LocalCashStore store;

  public CompraRepository(LocalCashStore store) {
    this.store = store;
  }

  public long crear(Compra compra) {
    long id = store.nuevoId();
    store.transaccion(
        () -> {
          store.guardar("compras", id, datos(compra, id));
          if (compra.getContactoId() != null)
            store.incrementarUso("contactos", compra.getContactoId(), 1);
        });
    return id;
  }

  private Map<String, Object> datos(Compra c, long id) {
    List<Map<String, Object>> detalles = new ArrayList<>();
    for (DetalleCompra d : c.getDetalles())
      detalles.add(
          LocalCashStore.mapa(
              "descripcion",
              d.getDescripcion(),
              "cantidad",
              d.getCantidad(),
              "precio_unitario",
              d.getPrecioUnitario()));
    return LocalCashStore.mapa(
        "id",
        id,
        "nombre",
        c.getNombre(),
        "estado",
        c.getEstado().name(),
        "fecha",
        c.getFecha(),
        "contacto_id",
        c.getContactoId(),
        "divisa",
        c.getDivisa() == null ? null : c.getDivisa().name(),
        "detalles",
        detalles,
        "pago_cuenta_id",
        null,
        "pago_movimiento_id",
        null,
        "reparto",
        null,
        "monto_usuario",
        null,
        "deuda_actualizada_id",
        null,
        "total",
        c.calcularTotal());
  }

  public void asignarDivisa(long id, Divisa divisa) {
    if (divisa == null) throw new IllegalArgumentException("Selecciona una divisa.");
    Compra c = buscar(id);
    if (c.getDivisa() != null) throw new IllegalArgumentException("La compra ya tiene divisa.");
    for (DetalleCompra d : c.getDetalles()) divisa.validar(d.getPrecioUnitario());
    for (Deuda d : new DeudaRepository(store).listarPorCompra(id)) divisa.validar(d.getMonto());
    Long propio = new DeudaRepository(store).buscarMontoUsuario(id);
    if (propio != null) divisa.validar(propio);
    Map<String, Object> m = store.buscar("compras", id);
    m.put("divisa", divisa.name());
    store.guardar("compras", id, m);
  }

  private Compra exigirEditable(long id) {
    Compra c = buscar(id);
    Map<String, Object> m = store.buscar("compras", id);
    if (c.getEstado() != Compra.Estado.PLANIFICADA
        || m.get("pago_cuenta_id") != null
        || m.get("reparto") != null)
      throw new IllegalArgumentException(
          "Solo puedes modificar compras planificadas sin pago ni reparto.");
    return c;
  }

  public void actualizarPlanificacion(Compra compra) {
    Compra anterior = exigirEditable(compra.getId());
    if (compra.getEstado() != Compra.Estado.PLANIFICADA
        || compra.getDivisa() == null
        || anterior.getDivisa() != compra.getDivisa())
      throw new IllegalArgumentException(
          "Conserva el estado planificado y la moneda original de la compra.");
    store.transaccion(
        () -> {
          store.guardar("compras", compra.getId(), datos(compra, compra.getId()));
          if (!Objects.equals(anterior.getContactoId(), compra.getContactoId())) {
            if (anterior.getContactoId() != null)
              store.incrementarUso("contactos", anterior.getContactoId(), -1);
            if (compra.getContactoId() != null)
              store.incrementarUso("contactos", compra.getContactoId(), 1);
          }
        });
  }

  public void eliminarPlanificacion(long id) {
    Compra c = exigirEditable(id);
    store.transaccion(
        () -> {
          store.borrar("compras", id);
          if (c.getContactoId() != null) store.incrementarUso("contactos", c.getContactoId(), -1);
        });
  }

  public Long buscarCuentaPago(long compraId) {
    return (Long) store.buscar("compras", compraId).get("pago_cuenta_id");
  }

  public void registrarPago(long compraId, long movimientoId) {
    Map<String, Object> compra = store.buscar("compras", compraId);
    if (compra.get("pago_cuenta_id") != null)
      throw new IllegalArgumentException("Esta compra ya tiene un pago registrado.");
    Map<String, Object> movimiento = store.buscar("movimientos", "p-" + movimientoId);
    compra.put("pago_cuenta_id", LocalCashStore.numero(movimiento, "cuenta_id"));
    compra.put("pago_movimiento_id", movimientoId);
    compra.put("estado", "REALIZADA");
    store.guardar("compras", compraId, compra);
  }

  public List<Compra> listar() {
    List<Compra> r = new ArrayList<>();
    for (Map<String, Object> m : store.listar("compras")) r.add(leer(m));
    r.sort(Comparator.comparingLong(Compra::getFecha).thenComparingLong(Compra::getId).reversed());
    return r;
  }

  public Compra buscar(long id) {
    return leer(store.buscar("compras", id));
  }

  @SuppressWarnings("unchecked")
  private Compra leer(Map<String, Object> m) {
    List<DetalleCompra> detalles = new ArrayList<>();
    for (Object item : (List<?>) m.get("detalles")) {
      Map<String, Object> d = (Map<String, Object>) item;
      detalles.add(
          new DetalleCompra(
              LocalCashStore.texto(d, "descripcion"),
              LocalCashStore.numero(d, "cantidad"),
              LocalCashStore.numero(d, "precio_unitario")));
    }
    return new Compra(
        LocalCashStore.numero(m, "id"),
        LocalCashStore.texto(m, "nombre"),
        Compra.Estado.valueOf(LocalCashStore.texto(m, "estado")),
        LocalCashStore.numero(m, "fecha"),
        (Long) m.get("contacto_id"),
        detalles,
        Divisa.desdeCodigo((String) m.get("divisa")));
  }
}
