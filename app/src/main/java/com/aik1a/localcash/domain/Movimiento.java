package com.aik1a.localcash.domain;

public final class Movimiento {
    public enum Tipo { INGRESO, EGRESO }

    private final long id;
    private final long cuentaId;
    private final Tipo tipo;
    private final long monto;
    private final String descripcion;
    private final long fecha;
    private final Ubicacion ubicacion;

    public Movimiento(long id, long cuentaId, Tipo tipo, long monto,
                      String descripcion, long fecha) {
        this(id, cuentaId, tipo, monto, descripcion, fecha, null);
    }

    public Movimiento(long id, long cuentaId, Tipo tipo, long monto,
                      String descripcion, long fecha, Ubicacion ubicacion) {
        if (id < 0 || cuentaId <= 0 || tipo == null || monto <= 0 || fecha < 0) {
            throw new IllegalArgumentException("Selecciona una cuenta, un tipo y un monto positivo.");
        }
        this.id = id;
        this.cuentaId = cuentaId;
        this.tipo = tipo;
        this.monto = monto;
        this.descripcion = descripcion == null ? "" : descripcion.trim();
        this.fecha = fecha;
        this.ubicacion = ubicacion;
    }

    public long getId() { return id; }
    public long getCuentaId() { return cuentaId; }
    public Tipo getTipo() { return tipo; }
    public long getMonto() { return monto; }
    public String getDescripcion() { return descripcion; }
    public long getFecha() { return fecha; }
    public Ubicacion getUbicacion() { return ubicacion; }
    public boolean tieneUbicacion() { return ubicacion != null; }
    public long getMontoConSigno() { return tipo == Tipo.INGRESO ? monto : -monto; }
}

