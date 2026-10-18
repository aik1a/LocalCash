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

    public static long parsear(String texto, Divisa divisa) {
        if (divisa == null) throw new IllegalArgumentException("Define la divisa primero.");
        String patron = divisa.getDecimales() == 0 ? "-?[0-9]+" : "-?[0-9]+([.,][0-9]{1,2})?";
        if (texto == null || !texto.trim().matches(patron))
            throw new IllegalArgumentException(divisa.name() + (divisa.getDecimales() == 0
                    ? ": usa un número entero, sin separador de miles."
                    : ": usa hasta 2 decimales, sin separador de miles."));
        long valor = parsear(texto);
        divisa.validar(valor);
        return valor;
    }

    /** Nunca oculta fracciones históricas ni convierte divisas. */
    public static String formatear(long centesimas, Divisa divisa) {
        if (divisa == null) return "Sin divisa · " + formatear(centesimas);
        divisa.validar(centesimas);
        java.text.DecimalFormat formato = new java.text.DecimalFormat(
                divisa.getDecimales() == 0 ? "#,##0" : "#,##0.00",
                java.text.DecimalFormatSymbols.getInstance(new java.util.Locale("es", "CL")));
        formato.setRoundingMode(java.math.RoundingMode.UNNECESSARY);
        return divisa.name() + " " + formato.format(BigDecimal.valueOf(centesimas, 2));
    }

    public static String formatear(long centesimas) {
        return BigDecimal.valueOf(centesimas, 2).toPlainString().replace('.', ',');
    }
}

