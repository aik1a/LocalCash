package com.aik1a.localcash.domain;

/** Una asignación completa por compra/contacto. Sin pagos parciales en T05. */
public final class Deuda {
    private final long compraId;
    private final Contacto contacto;
    private final long monto;
    private final String estado;
    public Deuda(long compraId, Contacto contacto, long monto) {
        this(compraId, contacto, monto, "PENDIENTE");
    }
    public Deuda(long compraId, Contacto contacto, long monto, String estado) {
        if (!"PENDIENTE".equals(estado) && !"PAGADA".equals(estado))
            throw new IllegalArgumentException("Estado de deuda inválido.");
        this.estado = estado;
        if (compraId <= 0 || contacto == null || contacto.getId() <= 0 || monto < 0)
            throw new IllegalArgumentException("Deuda inválida.");
        this.compraId = compraId; this.contacto = contacto; this.monto = monto;
    }
    public long getCompraId() { return compraId; }
    public Contacto getContacto() { return contacto; }
    public long getMonto() { return monto; }
    public String getEstado() { return estado; }
}

