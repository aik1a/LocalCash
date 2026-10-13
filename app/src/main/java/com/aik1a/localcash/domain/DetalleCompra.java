package com.aik1a.localcash.domain;

public final class DetalleCompra {
    private final String descripcion;
    private final long cantidad;
    private final long precioUnitario;
    public DetalleCompra(String descripcion, long cantidad, long precioUnitario) {
        if (descripcion == null || descripcion.trim().isEmpty() || cantidad <= 0 || precioUnitario < 0)
            throw new IllegalArgumentException("El ítem necesita descripción, cantidad entera positiva y precio no negativo.");
        Math.multiplyExact(cantidad, precioUnitario);
        this.descripcion = descripcion.trim();
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }
    public String getDescripcion() { return descripcion; }
    public long getCantidad() { return cantidad; }
    public long getPrecioUnitario() { return precioUnitario; }
    public long calcularSubtotal() { return Math.multiplyExact(cantidad, precioUnitario); }
}
