package com.aik1a.localcash.domain;

/** Catálogo acotado. Escala persistida histórica: centésimas para todas las monedas. */
public enum Divisa {
    CLP("Peso chileno", 0), USD("Dólar estadounidense", 2), EUR("Euro", 2),
    ARS("Peso argentino", 2), MXN("Peso mexicano", 2), COP("Peso colombiano", 2),
    PEN("Sol peruano", 2), BRL("Real brasileño", 2), GBP("Libra esterlina", 2), JPY("Yen japonés", 0);

    private final String nombre;
    private final int decimales;
    Divisa(String nombre, int decimales) { this.nombre = nombre; this.decimales = decimales; }
    public int getDecimales() { return decimales; }
    public long getUnidadMinima() { return decimales == 0 ? 100 : 1; }
    public void validar(long centesimas) {
        if (centesimas % getUnidadMinima() != 0)
            throw new IllegalArgumentException(name() + " no admite fracciones. El importe se conserva sin redondear; elige su divisa correcta.");
    }
    public static Divisa desdeCodigo(String codigo) { return codigo == null ? null : valueOf(codigo); }
    @Override public String toString() { return nombre + " (" + name() + ")"; }
}
