package com.aik1a.localcash.domain;

import java.math.BigDecimal;

/** Conversión exacta entre el formulario decimal y centésimas persistidas. */
public final class Dinero {
    private Dinero() { }

    public static long parsear(String texto) {
        if (texto == null || !texto.trim().matches("-?[0-9]+([.,][0-9]{1,2})?")) {
            throw new IllegalArgumentException("Usa un número con hasta 2 decimales, sin separador de miles.");
        }
        try {
            return new BigDecimal(texto.trim().replace(',', '.')).movePointRight(2).longValueExact();
        } catch (ArithmeticException ex) {
            throw new IllegalArgumentException("El monto es demasiado grande.");
        }
    }

    public static String formatear(long centesimas) {
        return BigDecimal.valueOf(centesimas, 2).toPlainString().replace('.', ',');
    }
}
