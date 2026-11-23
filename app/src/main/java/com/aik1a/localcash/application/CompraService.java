package com.aik1a.localcash.application;

import com.aik1a.localcash.data.*;
import com.aik1a.localcash.domain.*;
import java.util.List;

public final class CompraService {
  private final LocalCashStore helper;
  private final ContactoRepository contactos;
  private final CompraRepository compras;

  public CompraService(LocalCashStore helper) {
    this.helper = helper;
    contactos = new ContactoRepository(helper);
    compras = new CompraRepository(helper);
  }

  public void renombrarContacto(long id, String nombre) {
    contactos.renombrar(id, nombre);
  }

  public void eliminarContacto(long id) {
    contactos.eliminarSinUso(id);
  }

  public void actualizarPlanificacion(Compra compra) {
    if (compra.getContactoId() != null) contactos.buscar(compra.getContactoId());
    compras.actualizarPlanificacion(compra);
  }

  public void eliminarPlanificacion(long id) {
    compras.eliminarPlanificacion(id);
  }

  public long crearContacto(String nombre) {
    return contactos.crear(new Contacto(0, nombre));
  }

  public List<Contacto> listarContactos() {
    return contactos.listar();
  }

  public Contacto buscarContacto(long id) {
    return contactos.buscar(id);
  }

  public long registrar(Compra compra) {
    if (compra.getDivisa() == null)
      throw new IllegalArgumentException("Selecciona la divisa de la compra.");
    if (compra.getContactoId() != null) contactos.buscar(compra.getContactoId());
    return compras.crear(compra);
  }

  public Long buscarCuentaPago(long compraId) {
    return compras.buscarCuentaPago(compraId);
  }

  public List<Cuenta> listarCuentasPago(long compraId) {
    Compra compra = compras.buscar(compraId);
    java.util.ArrayList<Cuenta> compatibles = new java.util.ArrayList<>();
    for (Cuenta cuenta : new CuentaRepository(helper).listar())
      if (compra.getDivisa() != null && cuenta.getDivisa() == compra.getDivisa())
        compatibles.add(cuenta);
    return compatibles;
  }

  public List<Compra> listarComprasDeCuenta(long cuentaId) {
    java.util.ArrayList<Compra> resultado = new java.util.ArrayList<>();
    for (Compra compra : compras.listar()) {
      Long pago = compras.buscarCuentaPago(compra.getId());
      if (pago != null && pago == cuentaId) resultado.add(compra);
    }
    return resultado;
  }

  public long pagar(long compraId, long cuentaId) {
    final long[] resultado = new long[1];
    helper.transaccion(
        () -> {
          Compra compra = compras.buscar(compraId);
          if (compras.buscarCuentaPago(compraId) != null)
            throw new IllegalArgumentException("Esta compra ya tiene un pago registrado.");
          Cuenta cuenta = new CuentaRepository(helper).buscar(cuentaId);
          if (compra.getDivisa() == null || cuenta.getDivisa() != compra.getDivisa())
            throw new IllegalArgumentException(
                "La cuenta debe usar la misma moneda que la compra.");
          long total = compra.calcularTotal();
          Movimiento egreso =
              new Movimiento(
                  compraId,
                  cuentaId,
                  Movimiento.Tipo.EGRESO,
                  total,
                  "Compra: " + compra.getNombre(),
                  System.currentTimeMillis());
          List<Movimiento> historial = new MovimientoRepository(helper).listar(cuentaId);
          historial.add(egreso);
          cuenta.calcularSaldo(historial);
          resultado[0] = new MovimientoRepository(helper).crear(egreso, compraId);
          compras.registrarPago(compraId, resultado[0]);
        });
    return resultado[0];
  }

  public List<Compra> listarCompras() {
    return compras.listar();
  }

  public Compra buscarCompra(long id) {
    return compras.buscar(id);
  }
}
