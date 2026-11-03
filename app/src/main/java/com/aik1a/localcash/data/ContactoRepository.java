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
    public void renombrar(long id, String nombre) {
        Contacto contacto = new Contacto(id, nombre);
        ContentValues values = new ContentValues(); values.put("nombre", contacto.getNombre());
        if (helper.getWritableDatabase().update("contactos", values, "id=?", new String[]{Long.toString(id)}) != 1)
            throw new IllegalArgumentException("El contacto ya no existe.");
    }
    public void eliminarSinUso(long id) {
        if (helper.getWritableDatabase().delete("contactos", "id=? AND NOT EXISTS (SELECT 1 FROM compras WHERE contacto_id=contactos.id) AND NOT EXISTS (SELECT 1 FROM deudas WHERE contacto_id=contactos.id)", new String[]{Long.toString(id)}) != 1) {
            buscar(id);
            throw new IllegalArgumentException("No puedes eliminar este contacto porque tiene compras o deudas asociadas. Su historial se conserva.");
        }
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

