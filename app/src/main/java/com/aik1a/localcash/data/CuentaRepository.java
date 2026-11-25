package com.aik1a.localcash.data;

import com.aik1a.localcash.domain.*;
import java.util.*;

public final class CuentaRepository {
  private final LocalCashStore store;

  public CuentaRepository(LocalCashStore store) {
    this.store = store;
  }

  public long crear(Cuenta cuenta) {
    long id = store.nuevoId();
    store.guardar(
        "cuentas",
        id,
        LocalCashStore.mapa(
            "id",
            id,
            "nombre",
            cuenta.getNombre(),
            "saldo_inicial",
            cuenta.getSaldoInicial(),
            "divisa",
            cuenta.getDivisa() == null ? null : cuenta.getDivisa().name(),
            "usos",
            0L));
    return id;
  }

  public void asignarDivisa(long id, Divisa divisa) {
    if (divisa == null) throw new IllegalArgumentException("Selecciona una divisa.");
    Cuenta actual = buscar(id);
    if (actual.getDivisa() != null)
      throw new IllegalArgumentException("La cuenta ya tiene divisa.");
    divisa.validar(actual.getSaldoInicial());
    for (Movimiento m : new MovimientoRepository(store).listar(id)) divisa.validar(m.getMonto());
    Map<String, Object> m = store.buscar("cuentas", id);
    m.put("divisa", divisa.name());
    store.guardar("cuentas", id, m);
  }

  public void renombrar(long id, String nombre) {
    Cuenta c = buscar(id);
    Cuenta validada = new Cuenta(id, nombre, c.getSaldoInicial(), c.getDivisa());
    Map<String, Object> m = store.buscar("cuentas", id);
    store.actualizarCampo("cuentas", id, "nombre", validada.getNombre());
  }

  public void eliminarSinUso(long id) {
    buscar(id);
    if (!new MovimientoRepository(store).listar(id).isEmpty()
        || LocalCashStore.numero(store.buscar("cuentas", id), "usos") != 0)
      throw new IllegalArgumentException(
          "No puedes eliminar esta cuenta porque tiene movimientos registrados. Su historial se"
              + " conserva.");
    store.borrar("cuentas", id);
  }

  public List<Cuenta> listar() {
    List<Cuenta> r = new ArrayList<>();
    for (Map<String, Object> m : store.listar("cuentas")) r.add(leer(m));
    r.sort(Comparator.comparingLong(Cuenta::getId));
    return r;
  }

  public Cuenta buscar(long id) {
    return leer(store.buscar("cuentas", id));
  }

  private Cuenta leer(Map<String, Object> m) {
    return new Cuenta(
        LocalCashStore.numero(m, "id"),
        LocalCashStore.texto(m, "nombre"),
        LocalCashStore.numero(m, "saldo_inicial"),
        Divisa.desdeCodigo((String) m.get("divisa")));
  }
}
