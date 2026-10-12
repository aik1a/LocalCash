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
                c.getLong(c.getColumnIndexOrThrow("fecha")), c.isNull(contact) ? null : c.getLong(contact), detalles);
    }
}
