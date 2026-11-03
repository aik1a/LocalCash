package com.aik1a.localcash.domain;

import org.junit.Test;
import static org.junit.Assert.*;

public class UbicacionTest {
    @Test public void aceptaLimitesGeograficosYCoordenadaCero() {
        assertEquals(90.0, new Ubicacion(90, 180).getLatitud(), 0.0);
        assertEquals(-180.0, new Ubicacion(-90, -180).getLongitud(), 0.0);
        assertEquals(0.0, new Ubicacion(0, 0).getLatitud(), 0.0);
    }

    @Test public void rechazaCoordenadasFueraDelPlanetaONoFinitas() {
        assertThrows(IllegalArgumentException.class, () -> new Ubicacion(90.01, 0));
        assertThrows(IllegalArgumentException.class, () -> new Ubicacion(0, -180.01));
        assertThrows(IllegalArgumentException.class, () -> new Ubicacion(Double.NaN, 0));
        assertThrows(IllegalArgumentException.class, () -> new Ubicacion(0, Double.POSITIVE_INFINITY));
    }

    @Test public void movimientoSinUbicacionConservaImporte() {
        Movimiento movimiento = new Movimiento(1, 1, Movimiento.Tipo.EGRESO, 100, "Pan", 1);
        assertFalse(movimiento.tieneUbicacion());
        assertNull(movimiento.getUbicacion());
        assertEquals(-100L, movimiento.getMontoConSigno());
    }
}
