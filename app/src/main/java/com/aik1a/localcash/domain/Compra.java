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
    public Compra(long id, String nombre, Estado estado, long fecha, Long contactoId, List<DetalleCompra> detalles) {
        if (id < 0 || nombre == null || nombre.trim().isEmpty() || estado == null || fecha < 0
                || (contactoId != null && contactoId <= 0) || detalles == null || detalles.isEmpty()
                || detalles.contains(null)) throw new IllegalArgumentException("La compra necesita nombre, estado, fecha y al menos un ítem válido.");
        this.id = id;
        this.nombre = nombre.trim();
        this.estado = estado;
        this.fecha = fecha;
        this.contactoId = contactoId;
        this.detalles = Collections.unmodifiableList(new ArrayList<>(detalles));
        calcularTotal();
    }
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
