package com.aik1a.localcash.data;

import com.aik1a.localcash.domain.*;
import java.util.*;

public final class MovimientoRepository {
  private final LocalCashStore store;

  public MovimientoRepository(LocalCashStore store) {
    this.store = store;
  }

  public long crear(Movimiento movimiento) {
    return crear(movimiento, null);
  }

  public long crear(Movimiento movimiento, Long compraId) {
    long id = compraId == null ? store.nuevoId() : compraId;
    String doc = compraId == null ? Long.toString(id) : "p-" + id;
    if (store.existe("movimientos", doc))
      throw new IllegalArgumentException("Esta compra ya tiene un pago registrado.");
    store.transaccion(
        () -> {
          store.guardar(
              "movimientos",
              doc,
              LocalCashStore.mapa(
                  "id",
                  id,
                  "cuenta_id",
                  movimiento.getCuentaId(),
                  "tipo",
                  movimiento.getTipo().name(),
                  "monto",
                  movimiento.getMonto(),
                  "descripcion",
                  movimiento.getDescripcion(),
                  "fecha",
                  movimiento.getFecha(),
                  "latitud",
                  movimiento.tieneUbicacion() ? movimiento.getUbicacion().getLatitud() : null,
                  "longitud",
                  movimiento.tieneUbicacion() ? movimiento.getUbicacion().getLongitud() : null,
                  "compra_id",
                  compraId));
          store.incrementarUso("cuentas", movimiento.getCuentaId(), 1);
        });
    return id;
  }

  public List<Movimiento> listar(long cuentaId) {
    List<Movimiento> r = new ArrayList<>();
    for (Map<String, Object> m : store.listar("movimientos")) {
      if (LocalCashStore.numero(m, "cuenta_id") != cuentaId) continue;
      Object lat = m.get("latitud"), lon = m.get("longitud");
      if ((lat == null) != (lon == null))
        throw new IllegalStateException("Coordenadas incompletas.");
      Ubicacion u =
          lat == null
              ? null
              : new Ubicacion(((Number) lat).doubleValue(), ((Number) lon).doubleValue());
      r.add(
          new Movimiento(
              LocalCashStore.numero(m, "id"),
              cuentaId,
              Movimiento.Tipo.valueOf(LocalCashStore.texto(m, "tipo")),
              LocalCashStore.numero(m, "monto"),
              LocalCashStore.texto(m, "descripcion"),
              LocalCashStore.numero(m, "fecha"),
              u));
    }
    r.sort(Comparator.comparingLong(Movimiento::getFecha).thenComparingLong(Movimiento::getId));
    return r;
  }
}
