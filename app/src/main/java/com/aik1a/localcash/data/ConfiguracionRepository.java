package com.aik1a.localcash.data;

import com.aik1a.localcash.domain.Divisa;

public final class ConfiguracionRepository {
  private final LocalCashStore store;

  public ConfiguracionRepository(LocalCashStore store) {
    this.store = store;
  }

  public Divisa buscarDivisa() {
    return store.existe("configuracion", "principal")
        ? Divisa.valueOf(LocalCashStore.texto(store.buscar("configuracion", "principal"), "divisa"))
        : null;
  }

  public void configurarPrimeraVez(Divisa divisa) {
    if (divisa == null) throw new IllegalArgumentException("Selecciona una divisa.");
    Divisa actual = buscarDivisa();
    if (actual != null && actual != divisa)
      throw new IllegalArgumentException("La moneda principal ya fue configurada.");
    if (actual == null)
      store.guardar("configuracion", "principal", LocalCashStore.mapa("divisa", divisa.name()));
  }

  public Divisa exigirDivisa() {
    Divisa d = buscarDivisa();
    if (d == null) throw new IllegalArgumentException("Configura tu moneda principal primero.");
    return d;
  }
}
