package com.aik1a.localcash.application;

import com.aik1a.localcash.data.*;
import com.aik1a.localcash.domain.*;
import java.util.List;

/** Coordina la compra existente, los contactos y el reparto. No altera cuentas. */
public final class DeudaService {
    private final CompraRepository compras;
    private final ContactoRepository contactos;
    private final DeudaRepository deudas;
    public DeudaService(LocalCashDbHelper helper) {
        compras = new CompraRepository(helper); contactos = new ContactoRepository(helper);
        deudas = new DeudaRepository(helper);
    }
    public Compra buscarCompra(long id) { return compras.buscar(id); }
    public List<Contacto> listarContactos() { return contactos.listar(); }
    public List<Deuda> listarDeudas(long compraId) { return deudas.listarPorCompra(compraId); }
    public void repartirEquitativo(long compraId, List<Long> ids) {
        guardar(Reparto.equitativo(compras.buscar(compraId), ids));
    }
    public void repartirPersonalizado(long compraId, List<Long> ids, List<Long> montos) {
        guardar(Reparto.personalizado(compras.buscar(compraId), ids, montos));
    }
    public Long buscarMontoUsuario(long compraId) { return deudas.buscarMontoUsuario(compraId); }
    public void repartirEquitativoConUsuario(long compraId, List<Long> ids) {
        guardar(Reparto.equitativoConUsuario(compras.buscar(compraId), ids));
    }
    public void repartirPersonalizadoConUsuario(long compraId, List<Long> ids, List<Long> montos, long propio) {
        guardar(Reparto.personalizadoConUsuario(compras.buscar(compraId), ids, montos, propio));
    }
    private void guardar(Reparto reparto) {
        for (long id : reparto.getMontos().keySet()) contactos.buscar(id);
        deudas.registrar(reparto);
    }
}

