package com.aik1a.localcash.domain;

public final class Contacto {
    private final long id;
    private final String nombre;
    public Contacto(long id, String nombre) {
        if (id < 0 || nombre == null || nombre.trim().isEmpty())
            throw new IllegalArgumentException("Escribe el nombre del contacto.");
        this.id = id;
        this.nombre = nombre.trim();
    }
    public long getId() { return id; }
    public String getNombre() { return nombre; }
    @Override public String toString() { return nombre; }
}
