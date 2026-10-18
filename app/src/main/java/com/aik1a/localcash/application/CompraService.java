package com.aik1a.localcash.application;

import com.aik1a.localcash.data.*;
import com.aik1a.localcash.domain.*;
import java.util.List;

/** Coordina compras y su contacto opcional. No genera movimientos financieros. */
public final class CompraService {
    private final ContactoRepository contactos;
    private final CompraRepository compras;
    public CompraService(LocalCashDbHelper helper) {
        contactos = new ContactoRepository(helper); compras = new CompraRepository(helper);
    }
    public long crearContacto(String nombre) { return contactos.crear(new Contacto(0, nombre)); }
    public List<Contacto> listarContactos() { return contactos.listar(); }
    public Contacto buscarContacto(long id) { return contactos.buscar(id); }
    public long registrar(Compra compra) {
        if (compra.getDivisa() == null) throw new IllegalArgumentException("Selecciona la divisa de la compra.");
        if (compra.getContactoId() != null) contactos.buscar(compra.getContactoId());
        return compras.crear(compra);
    }
    public List<Compra> listarCompras() { return compras.listar(); }
    public Compra buscarCompra(long id) { return compras.buscar(id); }
}

