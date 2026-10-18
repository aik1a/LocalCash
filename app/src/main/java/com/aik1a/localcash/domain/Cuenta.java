package com.aik1a.localcash.domain;

import java.util.List;

public final class Cuenta {
    private final long id;
    private final String nombre;
    private final long saldoInicial;
    private final Divisa divisa;

    /** Reconstrucción de registros históricos aún sin divisa. */
    public Cuenta(long id, String nombre, long saldoInicial) { this(id, nombre, saldoInicial, null); }

    public Cuenta(long id, String nombre, long saldoInicial, Divisa divisa) {
        if (id < 0 || nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("La cuenta necesita un nombre válido.");
        }
        this.id = id;
        this.nombre = nombre.trim();
        if (divisa != null) divisa.validar(saldoInicial);
        this.saldoInicial = saldoInicial;
        this.divisa = divisa;
    }

    public Divisa getDivisa() { return divisa; }
    public long getId() { return id; }
    public String getNombre() { return nombre; }
    public long getSaldoInicial() { return saldoInicial; }

    public long calcularSaldo(List<Movimiento> movimientos) {
        long saldo = saldoInicial;
        for (Movimiento movimiento : movimientos) {
            if (movimiento.getCuentaId() != id) {
                throw new IllegalArgumentException("El movimiento pertenece a otra cuenta.");
            }
            saldo = Math.addExact(saldo, movimiento.getMontoConSigno());
        }
        return saldo;
    }
}

