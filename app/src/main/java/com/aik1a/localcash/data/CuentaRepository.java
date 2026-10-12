package com.aik1a.localcash.data;

import android.content.ContentValues;
import android.database.Cursor;
import com.aik1a.localcash.domain.Cuenta;
import java.util.ArrayList;
import java.util.List;

public final class CuentaRepository {
    private final LocalCashDbHelper helper;
    public CuentaRepository(LocalCashDbHelper helper) { this.helper = helper; }

    public long crear(Cuenta cuenta) {
        ContentValues values = new ContentValues();
        values.put("nombre", cuenta.getNombre());
        values.put("saldo_inicial", cuenta.getSaldoInicial());
        return helper.getWritableDatabase().insertOrThrow("cuentas", null, values);
    }

    public List<Cuenta> listar() {
        List<Cuenta> cuentas = new ArrayList<>();
        try (Cursor cursor = helper.getReadableDatabase().query("cuentas", null,
                null, null, null, null, "id ASC")) {
            while (cursor.moveToNext()) cuentas.add(leer(cursor));
        }
        return cuentas;
    }

    public Cuenta buscar(long id) {
        try (Cursor cursor = helper.getReadableDatabase().query("cuentas", null,
                "id = ?", new String[]{Long.toString(id)}, null, null, null)) {
            if (!cursor.moveToFirst()) throw new IllegalArgumentException("La cuenta no existe.");
            return leer(cursor);
        }
    }

    private Cuenta leer(Cursor cursor) {
        return new Cuenta(cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                cursor.getLong(cursor.getColumnIndexOrThrow("saldo_inicial")));
    }
}
