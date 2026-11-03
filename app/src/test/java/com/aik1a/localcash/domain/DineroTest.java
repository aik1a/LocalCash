package com.aik1a.localcash.domain;

import org.junit.Test;
import static org.junit.Assert.*;

public class DineroTest {
    @Test public void aceptaComaYPuntoSinPerderCentavos() {
        assertEquals(1029L, Dinero.parsear("10,29", Divisa.USD));
        assertEquals(1029L, Dinero.parsear("10.29", Divisa.USD));
    }

    @Test public void pesosSeGuardanComoCentesimasEnteras() {
        assertEquals(150000L, Dinero.parsear("1500", Divisa.CLP));
        assertEquals("CLP 1.500", Dinero.formatear(150000L, Divisa.CLP));
    }

    @Test public void rechazaFraccionesEnPesosSinRedondearlas() {
        assertThrows(IllegalArgumentException.class, () -> Dinero.parsear("1500,50", Divisa.CLP));
        assertThrows(IllegalArgumentException.class, () -> Dinero.formatear(150050L, Divisa.CLP));
    }

    @Test public void rechazaAmbiguedadDeMilesYExcesoDeDecimales() {
        assertThrows(IllegalArgumentException.class, () -> Dinero.parsear("1.000,50", Divisa.USD));
        assertThrows(IllegalArgumentException.class, () -> Dinero.parsear("1,005", Divisa.USD));
        assertThrows(IllegalArgumentException.class, () -> Dinero.parsear("", Divisa.USD));
    }

    @Test public void rechazaMontosFueraDeRango() {
        assertThrows(IllegalArgumentException.class,
                () -> Dinero.parsear("92233720368547758.08", Divisa.USD));
    }

    @Test public void montoHistoricoSinDivisaNoSePresentaComoPesos() {
        assertEquals("Sin divisa · 10,29", Dinero.formatear(1029L, null));
    }
}
