package com.aik1a.localcash.domain;

/** Par geográfico válido e inmutable; no depende de Android ni de SQLite. */
public final class Ubicacion {
    private final double latitud;
    private final double longitud;
    public Ubicacion(double latitud, double longitud) {
        if (!Double.isFinite(latitud) || !Double.isFinite(longitud)
                || latitud < -90 || latitud > 90 || longitud < -180 || longitud > 180)
            throw new IllegalArgumentException("Coordenadas fuera de rango o no finitas.");
        this.latitud = latitud; this.longitud = longitud;
    }
    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
}
