package com.aik1a.localcash.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Compra {
    public enum Estado { PLANIFICADA, REALIZADA }
    private final long id;
    private final String nombre;
    private final Estado estado;
    private final long fecha;
    private final Long contactoId;
    private final List<DetalleCompra> detalles;
    private final Divisa divisa;
    /** Reconstrucción histórica; los nuevos formularios siempre proporcionan divisa. */
    public Compra(long id, String nombre, Estado estado, long fecha, Long contactoId, List<DetalleCompra> detalles) {
        this(id, nombre, estado, fecha, contactoId, detalles, null);
    }
    public Compra(long id, String nombre, Estado estado, long fecha, Long contactoId, List<DetalleCompra> detalles, Divisa divisa) {
        if (id < 0 || nombre == null || nombre.trim().isEmpty() || estado == null || fecha < 0
                || (contactoId != null && contactoId <= 0) || detalles == null || detalles.isEmpty()) throw new IllegalArgumentException("La compra necesita nombre, estado, fecha y al menos un ítem válido.");
        this.divisa = divisa;
        this.id = id;
        this.nombre = nombre.trim();
        this.estado = estado;
        this.fecha = fecha;
        this.contactoId = contactoId;
        this.detalles = Collections.unmodifiableList(new ArrayList<>(detalles));
        for (DetalleCompra detalle : this.detalles) {
            if (detalle == null) throw new IllegalArgumentException("La compra no admite ítems nulos.");
            if (divisa != null) divisa.validar(detalle.getPrecioUnitario());
        }
        calcularTotal();
    }
    public Divisa getDivisa() { return divisa; }
    public long getId() { return id; }
    public String getNombre() { return nombre; }
    public Estado getEstado() { return estado; }
    public long getFecha() { return fecha; }
    public Long getContactoId() { return contactoId; }
    public List<DetalleCompra> getDetalles() { return detalles; }
    public long calcularTotal() {
        long total = 0;
        for (DetalleCompra detalle : detalles) total = Math.addExact(total, detalle.calcularSubtotal());
        return total;
    }
}

