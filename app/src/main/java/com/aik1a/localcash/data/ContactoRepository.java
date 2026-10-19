package com.aik1a.localcash.data;

import android.content.ContentValues;
import android.database.Cursor;
import com.aik1a.localcash.domain.Contacto;
import java.util.ArrayList;
import java.util.List;

public final class ContactoRepository {
    private final LocalCashDbHelper helper;
    public ContactoRepository(LocalCashDbHelper helper) { this.helper = helper; }
    public long crear(Contacto contacto) {
        ContentValues values = new ContentValues(); values.put("nombre", contacto.getNombre());
        return helper.getWritableDatabase().insertOrThrow("contactos", null, values);
    }
    public void editar(long id, String nombre) {
        buscar(id); Contacto contacto = new Contacto(id, nombre);
        ContentValues values = new ContentValues(); values.put("nombre", contacto.getNombre());
        if (helper.getWritableDatabase().update("contactos", values, "id=?", new String[]{Long.toString(id)}) != 1)
            throw new IllegalArgumentException("El contacto no existe.");
    }
    public void eliminar(long id) {
        android.database.sqlite.SQLiteDatabase db = helper.getWritableDatabase(); db.beginTransaction();
        try {
            buscar(id);
            try (Cursor c = db.rawQuery("SELECT 1 FROM compras WHERE contacto_id=? UNION ALL SELECT 1 FROM deudas WHERE contacto_id=? LIMIT 1",
                    new String[]{Long.toString(id), Long.toString(id)})) {
                if (c.moveToFirst()) throw new IllegalArgumentException("Este contacto está vinculado a compras o deudas.");
            }
            db.delete("contactos", "id=?", new String[]{Long.toString(id)}); db.setTransactionSuccessful();
        } finally { db.endTransaction(); }
    }
    public List<Contacto> listar() {
        List<Contacto> resultado = new ArrayList<>();
        try (Cursor c = helper.getReadableDatabase().query("contactos", null, null, null, null, null, "id ASC")) {
            while (c.moveToNext()) resultado.add(new Contacto(c.getLong(c.getColumnIndexOrThrow("id")), c.getString(c.getColumnIndexOrThrow("nombre"))));
        }
        return resultado;
    }
    public Contacto buscar(long id) {
        try (Cursor c = helper.getReadableDatabase().query("contactos", null, "id = ?", new String[]{Long.toString(id)}, null, null, null)) {
            if (!c.moveToFirst()) throw new IllegalArgumentException("El contacto no existe.");
            return new Contacto(id, c.getString(c.getColumnIndexOrThrow("nombre")));
        }
    }
}

