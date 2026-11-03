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
    public long crear(Compra compra) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("divisa", compra.getDivisa() == null ? null : compra.getDivisa().name());
            values.put("nombre", compra.getNombre()); values.put("estado", compra.getEstado().name()); values.put("fecha", compra.getFecha());
            if (compra.getContactoId() == null) values.putNull("contacto_id"); else values.put("contacto_id", compra.getContactoId());
            long id = db.insertOrThrow("compras", null, values);
            for (DetalleCompra detalle : compra.getDetalles()) {
                ContentValues item = new ContentValues(); item.put("compra_id", id); item.put("descripcion", detalle.getDescripcion());
                item.put("cantidad", detalle.getCantidad()); item.put("precio_unitario", detalle.getPrecioUnitario());
                db.insertOrThrow("detalle_compra", null, item);
            }
            db.setTransactionSuccessful(); return id;
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

    private Compra exigirPlanificacionEditable(long id) {
        Compra actual = buscar(id);
        DeudaRepository deudas = new DeudaRepository(helper);
        if (actual.getEstado() != Compra.Estado.PLANIFICADA || buscarCuentaPago(id) != null
                || !deudas.listarPorCompra(id).isEmpty() || deudas.buscarMontoUsuario(id) != null)
            throw new IllegalArgumentException("Solo puedes modificar compras planificadas sin pago ni reparto.");
        return actual;
    }
    public void actualizarPlanificacion(Compra compra) {
        SQLiteDatabase db = helper.getWritableDatabase(); db.beginTransaction();
        try {
            Compra actual = exigirPlanificacionEditable(compra.getId());
            if (compra.getEstado() != Compra.Estado.PLANIFICADA || compra.getDivisa() == null || actual.getDivisa() != compra.getDivisa())
                throw new IllegalArgumentException("Conserva el estado planificado y la moneda original de la compra.");
            ContentValues cabecera = new ContentValues(); cabecera.put("nombre", compra.getNombre()); cabecera.put("fecha", compra.getFecha());
            if (compra.getContactoId() == null) cabecera.putNull("contacto_id"); else cabecera.put("contacto_id", compra.getContactoId());
            db.update("compras", cabecera, "id=?", new String[]{Long.toString(compra.getId())});
            db.delete("detalle_compra", "compra_id=?", new String[]{Long.toString(compra.getId())});
            for (DetalleCompra detalle : compra.getDetalles()) {
                ContentValues item = new ContentValues(); item.put("compra_id", compra.getId()); item.put("descripcion", detalle.getDescripcion());
                item.put("cantidad", detalle.getCantidad()); item.put("precio_unitario", detalle.getPrecioUnitario());
                db.insertOrThrow("detalle_compra", null, item);
            }
            db.setTransactionSuccessful();
        } finally { db.endTransaction(); }
    }
    public void eliminarPlanificacion(long id) {
        SQLiteDatabase db = helper.getWritableDatabase(); db.beginTransaction();
        try {
            exigirPlanificacionEditable(id);
            db.delete("detalle_compra", "compra_id=?", new String[]{Long.toString(id)});
            if (db.delete("compras", "id=?", new String[]{Long.toString(id)}) != 1)
                throw new IllegalArgumentException("La compra ya no existe.");
            db.setTransactionSuccessful();
        } finally { db.endTransaction(); }
    }

    public Long buscarCuentaPago(long compraId) {
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                "SELECT m.cuenta_id FROM compra_movimiento cm JOIN movimientos m ON m.id=cm.movimiento_id WHERE cm.compra_id=?",
                new String[]{Long.toString(compraId)})) {
            return c.moveToFirst() ? c.getLong(0) : null;
        }
    }
    /** Invocado dentro de la transacción de pago del service. */
    public void registrarPago(long compraId, long movimientoId) {
        ContentValues enlace = new ContentValues();
        enlace.put("compra_id", compraId); enlace.put("movimiento_id", movimientoId);
        helper.getWritableDatabase().insertOrThrow("compra_movimiento", null, enlace);
        ContentValues estado = new ContentValues(); estado.put("estado", "REALIZADA");
        if (helper.getWritableDatabase().update("compras", estado, "id=?", new String[]{Long.toString(compraId)}) != 1)
            throw new IllegalArgumentException("La compra ya no existe.");
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
