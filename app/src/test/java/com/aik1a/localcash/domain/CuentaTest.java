package com.aik1a.localcash.domain;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.*;

public class CuentaTest {
    private Movimiento movimiento(long cuentaId, Movimiento.Tipo tipo, long monto) {
        return new Movimiento(1, cuentaId, tipo, monto, "Prueba", 1);
    }

    @Test public void saldoIncluyeIngresosYRestaEgresos() {
        Cuenta cuenta = new Cuenta(1, "Efectivo", 10000, Divisa.CLP);
        assertEquals(11500L, cuenta.calcularSaldo(Arrays.asList(
                movimiento(1, Movimiento.Tipo.INGRESO, 2000),
                movimiento(1, Movimiento.Tipo.EGRESO, 500))));
    }

    @Test public void sinMovimientosConservaSaldoInicialNegativo() {
        assertEquals(-100L, new Cuenta(1, "Cuenta", -100, Divisa.CLP)
                .calcularSaldo(Collections.emptyList()));
    }

    @Test public void noMezclaMovimientosDeOtraCuenta() {
        Cuenta cuenta = new Cuenta(1, "Cuenta", 0, Divisa.CLP);
        assertThrows(IllegalArgumentException.class, () -> cuenta.calcularSaldo(
                Collections.singletonList(movimiento(2, Movimiento.Tipo.INGRESO, 100))));
    }

    @Test public void noAceptaMovimientosEnCeroONegativos() {
        assertThrows(IllegalArgumentException.class, () -> movimiento(1, Movimiento.Tipo.INGRESO, 0));
        assertThrows(IllegalArgumentException.class, () -> movimiento(1, Movimiento.Tipo.EGRESO, -100));
    }

    @Test public void desbordamientoNoSeConvierteEnSaldoNegativo() {
        Cuenta cuenta = new Cuenta(1, "Cuenta", Long.MAX_VALUE, Divisa.USD);
        assertThrows(ArithmeticException.class, () -> cuenta.calcularSaldo(
                Collections.singletonList(movimiento(1, Movimiento.Tipo.INGRESO, 1))));
    }
}
