package com.aik1a.localcash.application;

import com.aik1a.localcash.data.*;
import com.aik1a.localcash.domain.*;
import java.util.List;

public final class CompraService {
    private final LocalCashDbHelper helper;
    private final ContactoRepository contactos;
    private final CompraRepository compras;
    public CompraService(LocalCashDbHelper helper) {
        this.helper = helper;
        contactos = new ContactoRepository(helper); compras = new CompraRepository(helper);
    }
    public void renombrarContacto(long id, String nombre) { contactos.renombrar(id, nombre); }
    public void eliminarContacto(long id) { contactos.eliminarSinUso(id); }
    public void actualizarPlanificacion(Compra compra) {
        if (compra.getContactoId() != null) contactos.buscar(compra.getContactoId());
        compras.actualizarPlanificacion(compra);
    }
    public void eliminarPlanificacion(long id) { compras.eliminarPlanificacion(id); }

    public long crearContacto(String nombre) { return contactos.crear(new Contacto(0, nombre)); }
    public List<Contacto> listarContactos() { return contactos.listar(); }
    public Contacto buscarContacto(long id) { return contactos.buscar(id); }
    public long registrar(Compra compra) {
        if (compra.getDivisa() == null) throw new IllegalArgumentException("Selecciona la divisa de la compra.");
        if (compra.getContactoId() != null) contactos.buscar(compra.getContactoId());
        return compras.crear(compra);
    }
    public Long buscarCuentaPago(long compraId) { return compras.buscarCuentaPago(compraId); }
    public List<Cuenta> listarCuentasPago(long compraId) {
        Compra compra = compras.buscar(compraId);
        java.util.ArrayList<Cuenta> compatibles = new java.util.ArrayList<>();
        for (Cuenta cuenta : new CuentaRepository(helper).listar())
            if (compra.getDivisa() != null && cuenta.getDivisa() == compra.getDivisa()) compatibles.add(cuenta);
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
        android.database.sqlite.SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            Compra compra = compras.buscar(compraId);
            if (compras.buscarCuentaPago(compraId) != null)
                throw new IllegalArgumentException("Esta compra ya tiene un pago registrado.");
            Cuenta cuenta = new CuentaRepository(helper).buscar(cuentaId);
            if (compra.getDivisa() == null || cuenta.getDivisa() != compra.getDivisa())
                throw new IllegalArgumentException("La cuenta debe usar la misma moneda que la compra.");
            long movimientoId = new MovimientoService(helper).registrar(cuentaId, Movimiento.Tipo.EGRESO,
                    compra.calcularTotal(), "Compra: " + compra.getNombre());
            compras.registrarPago(compraId, movimientoId);
            db.setTransactionSuccessful();
            return movimientoId;
        } finally { db.endTransaction(); }
    }
    public List<Compra> listarCompras() { return compras.listar(); }
    public Compra buscarCompra(long id) { return compras.buscar(id); }
}
