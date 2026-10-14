package com.aik1a.localcash.domain;

/** Una asignación completa por compra/contacto. Sin pagos parciales en T05. */
public final class Deuda {
    private final long compraId;
    private final Contacto contacto;
    private final long monto;
    public Deuda(long compraId, Contacto contacto, long monto) {
        if (compraId <= 0 || contacto == null || contacto.getId() <= 0 || monto < 0)
            throw new IllegalArgumentException("Deuda inválida.");
        this.compraId = compraId; this.contacto = contacto; this.monto = monto;
    }
    public long getCompraId() { return compraId; }
    public Contacto getContacto() { return contacto; }
    public long getMonto() { return monto; }
    public String getEstado() { return "PENDIENTE"; }
}
