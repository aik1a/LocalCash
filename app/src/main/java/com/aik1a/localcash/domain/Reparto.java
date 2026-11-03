package com.aik1a.localcash.domain;

import java.util.*;

/** Reparto inicial del total; importes exactos en centésimas y orden estable por ID. */
public final class Reparto {
    private final long compraId;
    private final Map<Long, Long> montos;
    private final Long montoUsuario;
    private Reparto(Compra compra, Map<Long, Long> montos) { this(compra, montos, null); }
    private Reparto(Compra compra, Map<Long, Long> montos, Long montoUsuario) {
        if (compra == null || compra.getId() <= 0 || compra.getEstado() != Compra.Estado.REALIZADA)
            throw new IllegalArgumentException("Solo se reparten compras realizadas existentes.");
        if (montos == null || montos.size() + (montoUsuario == null ? 0 : 1) < 2)
            throw new IllegalArgumentException(montoUsuario == null ? "Selecciona al menos dos contactos distintos." : "Elige al menos un contacto para compartir contigo.");
        if (montoUsuario != null && montoUsuario < 0) throw new IllegalArgumentException("Tu parte no puede ser negativa.");
        if (montoUsuario != null && compra.getDivisa() != null) compra.getDivisa().validar(montoUsuario);
        long suma = montoUsuario == null ? 0 : montoUsuario;
        TreeMap<Long, Long> ordenados = new TreeMap<>();
        for (Map.Entry<Long, Long> entry : montos.entrySet()) {
            if (entry.getKey() == null || entry.getKey() <= 0 || entry.getValue() == null || entry.getValue() < 0)
                throw new IllegalArgumentException("Cada contacto debe tener un monto no negativo.");
            if (compra.getDivisa() != null) compra.getDivisa().validar(entry.getValue());
            suma = Math.addExact(suma, entry.getValue());
            ordenados.put(entry.getKey(), entry.getValue());
        }
        if (suma != compra.calcularTotal()) {
            long diferencia = Math.subtractExact(compra.calcularTotal(), suma);
            String ajuste = diferencia > 0 ? "Faltan " + Dinero.formatear(diferencia, compra.getDivisa())
                    : "Sobran " + Dinero.formatear(Math.negateExact(diferencia), compra.getDivisa());
            throw new IllegalArgumentException("Total: " + Dinero.formatear(compra.calcularTotal(), compra.getDivisa())
                    + ". Ingresado: " + Dinero.formatear(suma, compra.getDivisa()) + ". " + ajuste + ". Ajusta los importes para que coincidan.");
        }
        this.montoUsuario = montoUsuario;
        this.compraId = compra.getId();
        this.montos = Collections.unmodifiableMap(ordenados);
    }
    public static Reparto equitativo(Compra compra, List<Long> contactos) {
        if (compra == null || contactos == null || contactos.size() < 2)
            throw new IllegalArgumentException("Selecciona al menos dos contactos distintos.");
        TreeSet<Long> ids = new TreeSet<>();
        for (Long id : contactos) {
            if (id == null || id <= 0 || !ids.add(id))
                throw new IllegalArgumentException("Los participantes deben ser contactos distintos.");
        }
        long unidad = compra.getDivisa() == null ? 1 : compra.getDivisa().getUnidadMinima();
        long total = compra.calcularTotal() / unidad, base = total / ids.size(), resto = total % ids.size();
        Map<Long, Long> montos = new TreeMap<>();
        int posicion = 0;
        for (Long id : ids) montos.put(id, Math.multiplyExact(base + (posicion++ < resto ? 1 : 0), unidad));
        return new Reparto(compra, montos);
    }
    public static Reparto personalizado(Compra compra, List<Long> contactos, List<Long> importes) {
        if (contactos == null || importes == null || contactos.size() != importes.size())
            throw new IllegalArgumentException("Falta el monto de un participante.");
        Map<Long, Long> montos = new HashMap<>();
        for (int i = 0; i < contactos.size(); i++) {
            Long id = contactos.get(i);
            if (montos.containsKey(id)) throw new IllegalArgumentException("Los participantes deben ser contactos distintos.");
            montos.put(id, importes.get(i));
        }
        return new Reparto(compra, montos);
    }
    /** El resto se asigna primero a Tú, luego a contactos por ID; suma exacta. */
    public static Reparto equitativoConUsuario(Compra compra, List<Long> contactos) {
        if (compra == null || contactos == null || contactos.isEmpty())
            throw new IllegalArgumentException("Elige al menos un contacto para compartir contigo.");
        TreeSet<Long> ids = new TreeSet<>();
        for (Long id : contactos)
            if (id == null || id <= 0 || !ids.add(id))
                throw new IllegalArgumentException("Elige contactos distintos.");
        long unidad = compra.getDivisa() == null ? 1 : compra.getDivisa().getUnidadMinima();
        long total = compra.calcularTotal() / unidad;
        long personas = ids.size() + 1L, base = total / personas, resto = total % personas;
        long propio = Math.multiplyExact(base + (resto > 0 ? 1 : 0), unidad);
        Map<Long, Long> montos = new TreeMap<>();
        long posicion = 1;
        for (long id : ids) montos.put(id, Math.multiplyExact(base + (posicion++ < resto ? 1 : 0), unidad));
        return new Reparto(compra, montos, propio);
    }
    public static Reparto personalizadoConUsuario(Compra compra, List<Long> contactos, List<Long> importes, long propio) {
        if (contactos == null || importes == null || contactos.size() != importes.size())
            throw new IllegalArgumentException("Falta el monto de un participante.");
        Map<Long, Long> montos = new HashMap<>();
        for (int i = 0; i < contactos.size(); i++) {
            Long id = contactos.get(i);
            if (montos.containsKey(id)) throw new IllegalArgumentException("Elige contactos distintos.");
            montos.put(id, importes.get(i));
        }
        return new Reparto(compra, montos, propio);
    }
    public Long getMontoUsuario() { return montoUsuario; }
    public long getCompraId() { return compraId; }
    public Map<Long, Long> getMontos() { return montos; }
}


