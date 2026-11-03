package com.aik1a.localcash.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class CompraYDeudaTest {
    private Compra compra(List<DetalleCompra> detalles) {
        return new Compra(1, "Compra", Compra.Estado.PLANIFICADA, 1, null, detalles, Divisa.USD);
    }

    @Test public void totalConsideraCantidadesYArticulosGratuitos() {
        Compra compra = compra(Arrays.asList(new DetalleCompra("Pan", 3, 125),
                new DetalleCompra("Regalo", 1, 0), new DetalleCompra("Leche", 2, 250)));
        assertEquals(875L, compra.calcularTotal());
    }

    @Test public void noPermiteCompraVaciaNiCantidadCero() {
        assertThrows(IllegalArgumentException.class, () -> compra(Collections.emptyList()));
        assertThrows(IllegalArgumentException.class, () -> new DetalleCompra("Pan", 0, 100));
    }

    @Test public void cambiosExternosNoAlteranElTotalDeUnaCompra() {
        List<DetalleCompra> detalles = new ArrayList<>();
        detalles.add(new DetalleCompra("Pan", 1, 100));
        Compra compra = compra(detalles);
        detalles.clear();
        assertEquals(100L, compra.calcularTotal());
        assertThrows(UnsupportedOperationException.class, () -> compra.getDetalles().clear());
    }

    @Test public void totalFueraDeRangoSeRechazaSinEnvolverElImporte() {
        assertThrows(ArithmeticException.class, () -> new DetalleCompra("Grande", 2, Long.MAX_VALUE));
        assertThrows(ArithmeticException.class, () -> compra(Arrays.asList(
                new DetalleCompra("A", 1, Long.MAX_VALUE), new DetalleCompra("B", 1, 1))));
    }

    @Test public void deudaHistoricaSiguePendienteYPermiteParteCero() {
        Deuda deuda = new Deuda(1, new Contacto(2, "Ana"), 0);
        assertEquals("PENDIENTE", deuda.getEstado());
        assertEquals(0L, deuda.getMonto());
    }

    @Test public void deudaPagadaConservaImporteYParticipante() {
        Deuda deuda = new Deuda(1, new Contacto(2, "Ana"), 1500, "PAGADA");
        assertEquals("PAGADA", deuda.getEstado());
        assertEquals(1500L, deuda.getMonto());
        assertEquals(2L, deuda.getContacto().getId());
    }

    @Test public void deudaRechazaEstadosDesconocidosEImportesNegativos() {
        assertThrows(IllegalArgumentException.class,
                () -> new Deuda(1, new Contacto(2, "Ana"), 100, "ACEPTADA"));
        assertThrows(IllegalArgumentException.class,
                () -> new Deuda(1, new Contacto(2, "Ana"), -100, "PENDIENTE"));
    }
}
