package com.aik1a.localcash.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class LocalCashDbHelper extends SQLiteOpenHelper {
    public LocalCashDbHelper(Context context) {
        super(context.getApplicationContext(), "localcash.db", null, 1);
    }

    @Override public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE cuentas (id INTEGER PRIMARY KEY, "
                + "nombre TEXT NOT NULL CHECK(length(trim(nombre)) > 0), "
                + "saldo_inicial INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE movimientos (id INTEGER PRIMARY KEY, "
                + "cuenta_id INTEGER NOT NULL REFERENCES cuentas(id) ON DELETE RESTRICT, "
                + "tipo TEXT NOT NULL CHECK(tipo IN ('INGRESO','EGRESO')), "
                + "monto INTEGER NOT NULL CHECK(monto > 0), "
                + "descripcion TEXT NOT NULL DEFAULT '', "
                + "fecha INTEGER NOT NULL CHECK(fecha >= 0))");
        db.execSQL("CREATE INDEX movimientos_cuenta_fecha ON movimientos(cuenta_id, fecha, id)");
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // La versión 1 es el primer esquema. Nunca eliminar datos como migración implícita.
        throw new IllegalStateException("Falta implementar la migración de SQLite.");
    }
}
