package com.aik1a.localcash.data;

import android.content.ContentValues;
import android.database.Cursor;
import com.aik1a.localcash.domain.Divisa;

public final class ConfiguracionRepository {
    private final LocalCashDbHelper helper;
    public ConfiguracionRepository(LocalCashDbHelper helper) { this.helper = helper; }
    public Divisa buscarDivisa() {
        try (Cursor c = helper.getReadableDatabase().query("configuracion", new String[]{"divisa"},
                "id=1", null, null, null, null)) {
            return c.moveToFirst() ? Divisa.valueOf(c.getString(0)) : null;
        }
    }
    public void configurarPrimeraVez(Divisa divisa) {
        if (divisa == null) throw new IllegalArgumentException("Selecciona una divisa.");
        ContentValues values = new ContentValues(); values.put("id", 1); values.put("divisa", divisa.name());
        helper.getWritableDatabase().insertWithOnConflict("configuracion", null, values,
                android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE);
        if (buscarDivisa() != divisa) throw new IllegalArgumentException("La moneda principal ya fue configurada.");
    }
    public Divisa exigirDivisa() {
        Divisa divisa = buscarDivisa();
        if (divisa == null) throw new IllegalArgumentException("Configura tu moneda principal primero.");
        return divisa;
    }
}

