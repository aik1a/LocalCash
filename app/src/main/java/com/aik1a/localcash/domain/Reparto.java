package com.aik1a.localcash.domain;

import java.util.*;

/** Reparto inicial del total; importes exactos en centésimas y orden estable por ID. */
public final class Reparto {
    private final long compraId;
    private final Map<Long, Long> montos;
    private Reparto(Compra compra, Map<Long, Long> montos) {
        if (compra == null || compra.getId() <= 0 || compra.getEstado() != Compra.Estado.REALIZADA)
            throw new IllegalArgumentException("Solo se reparten compras realizadas existentes.");
        if (montos == null || montos.size() < 2)
            throw new IllegalArgumentException("Selecciona al menos dos contactos distintos.");
        long suma = 0;
        TreeMap<Long, Long> ordenados = new TreeMap<>();
        for (Map.Entry<Long, Long> entry : montos.entrySet()) {
            if (entry.getKey() == null || entry.getKey() <= 0 || entry.getValue() == null || entry.getValue() < 0)
                throw new IllegalArgumentException("Cada contacto debe tener un monto no negativo.");
            suma = Math.addExact(suma, entry.getValue());
            ordenados.put(entry.getKey(), entry.getValue());
        }
        if (suma != compra.calcularTotal())
            throw new IllegalArgumentException("La suma debe coincidir exactamente con el total.");
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
        long total = compra.calcularTotal(), base = total / ids.size(), resto = total % ids.size();
        Map<Long, Long> montos = new TreeMap<>();
        int posicion = 0;
        for (Long id : ids) montos.put(id, base + (posicion++ < resto ? 1 : 0));
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
    public long getCompraId() { return compraId; }
    public Map<Long, Long> getMontos() { return montos; }
}
