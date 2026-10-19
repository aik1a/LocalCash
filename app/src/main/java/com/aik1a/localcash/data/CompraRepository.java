package com.aik1a.localcash.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.aik1a.localcash.domain.*;
import java.util.ArrayList;
import java.util.List;

public final class CompraRepository {
    private final LocalCashDbHelper helper;
    public CompraRepository(LocalCashDbHelper helper) { this.helper = helper; }
    public long crear(Compra compra) { return crear(compra, null); }

    public long crear(Compra compra, Long cuentaId) {
        if (compra.getDivisa() == null) throw new IllegalArgumentException("Selecciona la divisa de la compra.");
        if (compra.getEstado() == Compra.Estado.REALIZADA && cuentaId == null)
            throw new IllegalArgumentException("Selecciona una cuenta para pagar la compra.");
        if (compra.getEstado() == Compra.Estado.PLANIFICADA && cuentaId != null)
            throw new IllegalArgumentException("Una compra planificada no se paga todavía.");
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("divisa", compra.getDivisa() == null ? null : compra.getDivisa().name());
            values.put("nombre", compra.getNombre()); values.put("estado", Compra.Estado.PLANIFICADA.name()); values.put("fecha", compra.getFecha());
            if (compra.getContactoId() == null) values.putNull("contacto_id"); else values.put("contacto_id", compra.getContactoId());
            long id = db.insertOrThrow("compras", null, values);
            for (DetalleCompra detalle : compra.getDetalles()) {
                ContentValues item = new ContentValues(); item.put("compra_id", id); item.put("descripcion", detalle.getDescripcion());
                item.put("cantidad", detalle.getCantidad()); item.put("precio_unitario", detalle.getPrecioUnitario());
                db.insertOrThrow("detalle_compra", null, item);
            }
            if (compra.getEstado() == Compra.Estado.REALIZADA) realizar(id, cuentaId);
            db.setTransactionSuccessful(); return id;
        } finally { db.endTransaction(); }
    }
    public Long buscarCuentaPago(long compraId) {
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                "SELECT m.cuenta_id FROM pago_compra p JOIN movimientos m ON m.id=p.movimiento_id WHERE p.compra_id=?",
                new String[]{Long.toString(compraId)})) { return c.moveToFirst() ? c.getLong(0) : null; }
    }
    public void realizar(long compraId, long cuentaId) {
        SQLiteDatabase db = helper.getWritableDatabase(); db.beginTransaction();
        try {
            Compra compra = buscar(compraId);
            Long pagadaDesde = buscarCuentaPago(compraId);
            if (compra.getEstado() == Compra.Estado.REALIZADA) {
                if (pagadaDesde != null && pagadaDesde.longValue() == cuentaId) { db.setTransactionSuccessful(); return; }
                throw new IllegalArgumentException("La compra ya está realizada; no se volverá a cobrar.");
            }
            if (pagadaDesde != null) throw new IllegalArgumentException("La compra ya tiene un pago.");
            Cuenta cuenta = new CuentaRepository(helper).buscar(cuentaId);
            if (compra.getDivisa() == null || cuenta.getDivisa() != compra.getDivisa())
                throw new IllegalArgumentException("Selecciona una cuenta con la misma divisa de la compra.");
            long total = compra.calcularTotal();
            if (total <= 0) throw new IllegalArgumentException("La compra necesita un total mayor que cero para pagarla.");
            MovimientoRepository movimientos = new MovimientoRepository(helper);
            long saldo = cuenta.calcularSaldo(movimientos.listar(cuentaId));
            if (saldo < total) throw new IllegalArgumentException("No hay dinero suficiente en esta cuenta.");
            long movimientoId = movimientos.crear(new Movimiento(0, cuentaId, Movimiento.Tipo.EGRESO, total,
                    "Compra: " + compra.getNombre(), System.currentTimeMillis()));
            ContentValues pago = new ContentValues(); pago.put("compra_id", compraId); pago.put("movimiento_id", movimientoId);
            db.insertOrThrow("pago_compra", null, pago);
            ContentValues values = new ContentValues(); values.put("estado", Compra.Estado.REALIZADA.name());
            if (db.update("compras", values, "id=? AND estado='PLANIFICADA'", new String[]{Long.toString(compraId)}) != 1)
                throw new IllegalArgumentException("La compra cambió. Vuelve a abrirla.");
            db.setTransactionSuccessful();
        } finally { db.endTransaction(); }
    }
    private void exigirSinReparto(long id) {
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                "SELECT 1 FROM deudas WHERE compra_id=? UNION ALL SELECT 1 FROM reparto_usuario WHERE compra_id=? LIMIT 1",
                new String[]{Long.toString(id), Long.toString(id)})) {
            if (c.moveToFirst()) throw new IllegalArgumentException("La compra tiene un reparto y no se puede eliminar ni cambiar sus ítems.");
        }
    }
    public void editarPlanificada(Compra nueva) {
        SQLiteDatabase db = helper.getWritableDatabase(); db.beginTransaction();
        try {
            Compra actual = buscar(nueva.getId()); exigirSinReparto(nueva.getId());
            if (actual.getEstado() != Compra.Estado.PLANIFICADA || nueva.getEstado() != Compra.Estado.PLANIFICADA)
                throw new IllegalArgumentException("Solo puedes editar los ítems de una compra planificada.");
            if (actual.getDivisa() == null || nueva.getDivisa() == null)
                throw new IllegalArgumentException("Define la divisa de esta compra antes de editar sus ítems.");
            if (actual.getDivisa() != nueva.getDivisa()) throw new IllegalArgumentException("La divisa de la compra es fija.");
            if (buscarCuentaPago(nueva.getId()) != null) throw new IllegalArgumentException("La compra ya tiene un pago.");
            ContentValues values = new ContentValues(); values.put("nombre", nueva.getNombre()); values.put("fecha", nueva.getFecha());
            if (nueva.getContactoId() == null) values.putNull("contacto_id"); else values.put("contacto_id", nueva.getContactoId());
            db.update("compras", values, "id=?", new String[]{Long.toString(nueva.getId())});
            db.delete("detalle_compra", "compra_id=?", new String[]{Long.toString(nueva.getId())});
            for (DetalleCompra d : nueva.getDetalles()) {
                ContentValues item = new ContentValues(); item.put("compra_id", nueva.getId()); item.put("descripcion", d.getDescripcion());
                item.put("cantidad", d.getCantidad()); item.put("precio_unitario", d.getPrecioUnitario());
                db.insertOrThrow("detalle_compra", null, item);
            }
            db.setTransactionSuccessful();
        } finally { db.endTransaction(); }
    }
    public void renombrar(long id, String nombre) {
        Compra actual = buscar(id);
        Compra nueva = new Compra(id, nombre, actual.getEstado(), actual.getFecha(), actual.getContactoId(), actual.getDetalles(), actual.getDivisa());
        ContentValues values = new ContentValues(); values.put("nombre", nueva.getNombre());
        if (helper.getWritableDatabase().update("compras", values, "id=?", new String[]{Long.toString(id)}) != 1)
            throw new IllegalArgumentException("La compra no existe.");
    }
    public void eliminar(long id) {
        SQLiteDatabase db = helper.getWritableDatabase(); db.beginTransaction();
        try {
            buscar(id); exigirSinReparto(id);
            Long movimientoId = null;
            try (Cursor c = db.query("pago_compra", new String[]{"movimiento_id"}, "compra_id=?",
                    new String[]{Long.toString(id)}, null, null, null)) { if (c.moveToFirst()) movimientoId = c.getLong(0); }
            db.delete("pago_compra", "compra_id=?", new String[]{Long.toString(id)});
            if (movimientoId != null) new MovimientoRepository(helper).eliminar(movimientoId);
            db.delete("detalle_compra", "compra_id=?", new String[]{Long.toString(id)});
            db.delete("compras", "id=?", new String[]{Long.toString(id)});
            db.setTransactionSuccessful();
        } finally { db.endTransaction(); }
    }

    public void asignarDivisa(long id, Divisa divisa) {
        if (divisa == null) throw new IllegalArgumentException("Selecciona una divisa.");
        SQLiteDatabase db = helper.getWritableDatabase(); db.beginTransaction();
        try {
            Compra compra = buscar(id);
            if (compra.getDivisa() != null) throw new IllegalArgumentException("La compra ya tiene divisa.");
            for (DetalleCompra d : compra.getDetalles()) divisa.validar(d.getPrecioUnitario());
            DeudaRepository deudas = new DeudaRepository(helper);
            for (Deuda d : deudas.listarPorCompra(id)) divisa.validar(d.getMonto());
            Long propio = deudas.buscarMontoUsuario(id);
            if (propio != null) divisa.validar(propio);
            ContentValues values = new ContentValues(); values.put("divisa", divisa.name());
            if (db.update("compras", values, "id=? AND divisa IS NULL", new String[]{Long.toString(id)}) != 1)
                throw new IllegalArgumentException("La compra cambió. Vuelve a abrirla.");
            db.setTransactionSuccessful();
        } finally { db.endTransaction(); }
    }

    public List<Compra> listar() {
        List<Compra> resultado = new ArrayList<>();
        try (Cursor c = helper.getReadableDatabase().query("compras", null, null, null, null, null, "fecha DESC, id DESC")) {
            while (c.moveToNext()) resultado.add(leer(c));
        }
        return resultado;
    }
    public Compra buscar(long id) {
        try (Cursor c = helper.getReadableDatabase().query("compras", null, "id = ?", new String[]{Long.toString(id)}, null, null, null)) {
            if (!c.moveToFirst()) throw new IllegalArgumentException("La compra no existe.");
            return leer(c);
        }
    }
    private Compra leer(Cursor c) {
        long id = c.getLong(c.getColumnIndexOrThrow("id"));
        int contact = c.getColumnIndexOrThrow("contacto_id");
        List<DetalleCompra> detalles = new ArrayList<>();
        try (Cursor d = helper.getReadableDatabase().query("detalle_compra", null, "compra_id = ?", new String[]{Long.toString(id)}, null, null, "id ASC")) {
            while (d.moveToNext()) detalles.add(new DetalleCompra(d.getString(d.getColumnIndexOrThrow("descripcion")),
                    d.getLong(d.getColumnIndexOrThrow("cantidad")), d.getLong(d.getColumnIndexOrThrow("precio_unitario"))));
        }
        return new Compra(id, c.getString(c.getColumnIndexOrThrow("nombre")), Compra.Estado.valueOf(c.getString(c.getColumnIndexOrThrow("estado"))),
                c.getLong(c.getColumnIndexOrThrow("fecha")), c.isNull(contact) ? null : c.getLong(contact), detalles, Divisa.desdeCodigo(c.getString(c.getColumnIndexOrThrow("divisa"))));
    }
}

