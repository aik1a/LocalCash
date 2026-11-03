package com.aik1a.localcash.domain;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.*;

public class RepartoTest {
    private Compra compra(long total, Divisa divisa, Compra.Estado estado) {
        return new Compra(1, "Cena", estado, 1, null,
                Collections.singletonList(new DetalleCompra("Cena", 1, total)), divisa);
    }

    @Test public void repartoEnPesosConservaTotalSinFracciones() {
        Reparto reparto = Reparto.equitativoConUsuario(
                compra(100000, Divisa.CLP, Compra.Estado.REALIZADA), Arrays.asList(8L, 2L));
        assertEquals(Long.valueOf(33400), reparto.getMontoUsuario());
        assertEquals(Long.valueOf(33300), reparto.getMontos().get(2L));
        assertEquals(Long.valueOf(33300), reparto.getMontos().get(8L));
    }

    @Test public void centavoSobranteSigueOrdenEstableDeContactos() {
        Compra compra = compra(1001, Divisa.USD, Compra.Estado.REALIZADA);
        Reparto a = Reparto.equitativo(compra, Arrays.asList(8L, 2L));
        Reparto b = Reparto.equitativo(compra, Arrays.asList(2L, 8L));
        assertEquals(Long.valueOf(501), a.getMontos().get(2L));
        assertEquals(Long.valueOf(500), a.getMontos().get(8L));
        assertEquals(a.getMontos(), b.getMontos());
    }

    @Test public void compraPlanificadaNoGeneraReparto() {
        assertThrows(IllegalArgumentException.class, () -> Reparto.equitativoConUsuario(
                compra(1000, Divisa.CLP, Compra.Estado.PLANIFICADA), Collections.singletonList(2L)));
    }

    @Test public void contactoDuplicadoNoSeCuentaDosVeces() {
        assertThrows(IllegalArgumentException.class, () -> Reparto.equitativoConUsuario(
                compra(1000, Divisa.CLP, Compra.Estado.REALIZADA), Arrays.asList(2L, 2L)));
    }

    @Test public void repartoPersonalizadoAdmiteParteCeroConTotalExacto() {
        Reparto reparto = Reparto.personalizadoConUsuario(
                compra(1000, Divisa.CLP, Compra.Estado.REALIZADA),
                Collections.singletonList(2L), Collections.singletonList(1000L), 0);
        assertEquals(Long.valueOf(0), reparto.getMontoUsuario());
        assertEquals(Long.valueOf(1000), reparto.getMontos().get(2L));
    }

    @Test public void repartoIncompletoExplicaCuantoFalta() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> Reparto.personalizadoConUsuario(
                        compra(1000, Divisa.CLP, Compra.Estado.REALIZADA),
                        Collections.singletonList(2L), Collections.singletonList(400L), 500));
        assertTrue(error.getMessage(), error.getMessage().contains("Faltan CLP 1"));
    }

    @Test public void repartoExcedidoExplicaCuantoSobra() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> Reparto.personalizadoConUsuario(
                        compra(1000, Divisa.CLP, Compra.Estado.REALIZADA),
                        Collections.singletonList(2L), Collections.singletonList(600L), 500));
        assertTrue(error.getMessage(), error.getMessage().contains("Sobran CLP 1"));
    }

    @Test public void repartoEnPesosRechazaFraccionesAunqueLaSumaCuadre() {
        assertThrows(IllegalArgumentException.class, () -> Reparto.personalizadoConUsuario(
                compra(1000, Divisa.CLP, Compra.Estado.REALIZADA),
                Collections.singletonList(2L), Collections.singletonList(450L), 550));
    }
}
