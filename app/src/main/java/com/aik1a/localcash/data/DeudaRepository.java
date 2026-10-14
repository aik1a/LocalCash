package com.aik1a.localcash.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.aik1a.localcash.domain.*;
import java.util.*;

public final class DeudaRepository {
    private final LocalCashDbHelper helper;
    public DeudaRepository(LocalCashDbHelper helper) { this.helper = helper; }
    public void registrar(Reparto reparto) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            Compra compra = new CompraRepository(helper).buscar(reparto.getCompraId());
            // Revalidar contra el total real dentro de la misma transacción.
            Reparto.personalizado(compra, new ArrayList<>(reparto.getMontos().keySet()),
                    new ArrayList<>(reparto.getMontos().values()));
            if (!listarPorCompra(compra.getId()).isEmpty())
                throw new IllegalArgumentException("Esta compra ya tiene un reparto.");
            for (Map.Entry<Long, Long> entry : reparto.getMontos().entrySet()) {
                ContentValues values = new ContentValues();
                values.put("compra_id", compra.getId()); values.put("contacto_id", entry.getKey());
                values.put("monto", entry.getValue()); values.put("estado", "PENDIENTE");
                db.insertOrThrow("deudas", null, values);
            }
            db.setTransactionSuccessful();
        } finally { db.endTransaction(); }
    }
    public List<Deuda> listarPorCompra(long compraId) {
        List<Deuda> resultado = new ArrayList<>();
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                "SELECT d.compra_id, d.contacto_id, c.nombre, d.monto FROM deudas d "
                + "JOIN contactos c ON c.id=d.contacto_id WHERE d.compra_id=? ORDER BY d.contacto_id ASC",
                new String[]{Long.toString(compraId)})) {
            while (c.moveToNext()) resultado.add(new Deuda(c.getLong(0),
                    new Contacto(c.getLong(1), c.getString(2)), c.getLong(3)));
        }
        return resultado;
    }
}
