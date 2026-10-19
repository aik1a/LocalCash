package com.aik1a.localcash.application;

import com.aik1a.localcash.data.*;
import com.aik1a.localcash.domain.*;
import java.util.List;

/** Coordina compras, contactos y el pago explícito desde una cuenta. */
public final class CompraService {
    private final ContactoRepository contactos;
    private final CompraRepository compras;
    public CompraService(LocalCashDbHelper helper) {
        contactos = new ContactoRepository(helper); compras = new CompraRepository(helper);
    }
    public long crearContacto(String nombre) { return contactos.crear(new Contacto(0, nombre)); }
    public List<Contacto> listarContactos() { return contactos.listar(); }
    public Contacto buscarContacto(long id) { return contactos.buscar(id); }
    public void editarContacto(long id, String nombre) { contactos.editar(id, nombre); }
    public void eliminarContacto(long id) { contactos.eliminar(id); }
    public long registrar(Compra compra) { return registrar(compra, null); }
    public long registrar(Compra compra, Long cuentaId) {
        if (compra.getDivisa() == null) throw new IllegalArgumentException("Selecciona la divisa de la compra.");
        if (compra.getContactoId() != null) contactos.buscar(compra.getContactoId());
        return compras.crear(compra, cuentaId);
    }
    public void realizar(long compraId, long cuentaId) { compras.realizar(compraId, cuentaId); }
    public void editarPlanificada(Compra compra) { compras.editarPlanificada(compra); }
    public void renombrar(long compraId, String nombre) { compras.renombrar(compraId, nombre); }
    public void eliminarCompra(long compraId) { compras.eliminar(compraId); }
    public Long buscarCuentaPago(long compraId) { return compras.buscarCuentaPago(compraId); }
    public List<Compra> listarCompras() { return compras.listar(); }
    public Compra buscarCompra(long id) { return compras.buscar(id); }
}

