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
            if (compra.getDivisa() == null) throw new IllegalArgumentException("Define la divisa de la compra primero.");
            // Revalidar contra el total real dentro de la misma transacción.
            if (reparto.getMontoUsuario() == null)
                Reparto.personalizado(compra, new ArrayList<>(reparto.getMontos().keySet()), new ArrayList<>(reparto.getMontos().values()));
            else Reparto.personalizadoConUsuario(compra, new ArrayList<>(reparto.getMontos().keySet()),
                    new ArrayList<>(reparto.getMontos().values()), reparto.getMontoUsuario());
            if (!listarPorCompra(compra.getId()).isEmpty() || buscarMontoUsuario(compra.getId()) != null)
                throw new IllegalArgumentException("Esta compra ya tiene un reparto.");
            for (Map.Entry<Long, Long> entry : reparto.getMontos().entrySet()) {
                ContentValues values = new ContentValues();
                values.put("compra_id", compra.getId()); values.put("contacto_id", entry.getKey());
                values.put("monto", entry.getValue()); values.put("estado", "PENDIENTE");
                db.insertOrThrow("deudas", null, values);
            }
            if (reparto.getMontoUsuario() != null) {
                ContentValues propio = new ContentValues(); propio.put("compra_id", compra.getId()); propio.put("monto", reparto.getMontoUsuario());
                db.insertOrThrow("reparto_usuario", null, propio);
            }
            db.setTransactionSuccessful();
        } finally { db.endTransaction(); }
    }
    public void cambiarEstado(long compraId, long contactoId, boolean pagada) {
        ContentValues values = new ContentValues(); values.put("estado", pagada ? "PAGADA" : "PENDIENTE");
        if (helper.getWritableDatabase().update("deudas", values,
                "compra_id=? AND contacto_id=? AND monto>0", new String[]{Long.toString(compraId), Long.toString(contactoId)}) != 1) {
            try (Cursor cursor = helper.getReadableDatabase().query("deudas", new String[]{"monto"},
                    "compra_id=? AND contacto_id=?", new String[]{Long.toString(compraId), Long.toString(contactoId)}, null, null, null)) {
                if (!cursor.moveToFirst()) throw new IllegalArgumentException("La deuda ya no existe. Vuelve a abrir el reparto.");
                if (cursor.getLong(0) == 0) throw new IllegalArgumentException("Esta participación no tiene dinero pendiente de pago.");
            }
            throw new IllegalArgumentException("Los datos de la deuda cambiaron. Vuelve a abrir el reparto e intenta de nuevo.");
        }
    }
    public List<Deuda> listarPorContacto(long contactoId) {
        List<Deuda> resultado = new ArrayList<>();
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                "SELECT d.compra_id,c.nombre,d.monto,d.estado FROM deudas d JOIN contactos c ON c.id=d.contacto_id WHERE d.contacto_id=? ORDER BY d.compra_id DESC",
                new String[]{Long.toString(contactoId)})) {
            while (c.moveToNext()) resultado.add(new Deuda(c.getLong(0), new Contacto(contactoId, c.getString(1)), c.getLong(2), c.getString(3)));
        }
        return resultado;
    }
    public Long buscarMontoUsuario(long compraId) {
        try (Cursor c = helper.getReadableDatabase().query("reparto_usuario", new String[]{"monto"},
                "compra_id=?", new String[]{Long.toString(compraId)}, null, null, null)) {
            return c.moveToFirst() ? c.getLong(0) : null;
        }
    }
    public List<Deuda> listarPorCompra(long compraId) {
        List<Deuda> resultado = new ArrayList<>();
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                "SELECT d.compra_id, d.contacto_id, c.nombre, d.monto, d.estado FROM deudas d "
                + "JOIN contactos c ON c.id=d.contacto_id WHERE d.compra_id=? ORDER BY d.contacto_id ASC",
                new String[]{Long.toString(compraId)})) {
            while (c.moveToNext()) resultado.add(new Deuda(c.getLong(0),
                    new Contacto(c.getLong(1), c.getString(2)), c.getLong(3), c.getString(4)));
        }
        return resultado;
    }
}


