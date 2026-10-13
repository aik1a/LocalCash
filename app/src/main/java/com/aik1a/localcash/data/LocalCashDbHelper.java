package com.aik1a.localcash.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class LocalCashDbHelper extends SQLiteOpenHelper {
    public LocalCashDbHelper(Context context) {
        super(context.getApplicationContext(), "localcash.db", null, 2);
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
        crearCompras(db);
    }

    private void crearCompras(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE contactos (id INTEGER PRIMARY KEY, nombre TEXT NOT NULL CHECK(length(trim(nombre)) > 0))");
        db.execSQL("CREATE TABLE compras (id INTEGER PRIMARY KEY, nombre TEXT NOT NULL CHECK(length(trim(nombre)) > 0), "
                + "estado TEXT NOT NULL CHECK(estado IN ('PLANIFICADA','REALIZADA')), fecha INTEGER NOT NULL CHECK(fecha >= 0), "
                + "contacto_id INTEGER REFERENCES contactos(id) ON DELETE RESTRICT)");
        db.execSQL("CREATE TABLE detalle_compra (id INTEGER PRIMARY KEY, compra_id INTEGER NOT NULL REFERENCES compras(id) ON DELETE RESTRICT, "
                + "descripcion TEXT NOT NULL CHECK(length(trim(descripcion)) > 0), cantidad INTEGER NOT NULL CHECK(cantidad > 0), "
                + "precio_unitario INTEGER NOT NULL CHECK(precio_unitario >= 0))");
        db.execSQL("CREATE INDEX detalle_por_compra ON detalle_compra(compra_id, id)");
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2) { crearCompras(db); return; }
        throw new IllegalStateException("Migración no soportada: " + oldVersion + " → " + newVersion);
    }
}
