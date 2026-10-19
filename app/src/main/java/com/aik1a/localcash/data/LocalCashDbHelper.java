package com.aik1a.localcash.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class LocalCashDbHelper extends SQLiteOpenHelper {
    public LocalCashDbHelper(Context context) {
        super(context.getApplicationContext(), "localcash.db", null, 6);
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
        crearDeudas(db);
        crearUbicacion(db);
        crearDivisas(db);
        crearPagos(db);
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

    private void crearDeudas(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE deudas (compra_id INTEGER NOT NULL REFERENCES compras(id) ON DELETE RESTRICT, "
                + "contacto_id INTEGER NOT NULL REFERENCES contactos(id) ON DELETE RESTRICT, "
                + "monto INTEGER NOT NULL CHECK(typeof(monto)='integer' AND monto >= 0), "
                + "estado TEXT NOT NULL DEFAULT 'PENDIENTE' CHECK(estado = 'PENDIENTE'), "
                + "PRIMARY KEY(compra_id, contacto_id))");
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion >= 1 && oldVersion < newVersion && newVersion <= 6) {
            if (oldVersion < 2 && newVersion >= 2) crearCompras(db);
            if (oldVersion < 3 && newVersion >= 3) crearDeudas(db);
            if (oldVersion < 4 && newVersion >= 4) crearUbicacion(db);
            if (oldVersion < 5 && newVersion >= 5) crearDivisas(db);
            if (oldVersion < 6 && newVersion >= 6) crearPagos(db);
            return;
        }
        throw new IllegalStateException("Migración no soportada: " + oldVersion + " → " + newVersion);
    }

    private void crearUbicacion(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE movimientos ADD COLUMN latitud REAL "
                + "CHECK(latitud IS NULL OR (typeof(latitud) IN ('real','integer') AND latitud BETWEEN -90 AND 90))");
        db.execSQL("ALTER TABLE movimientos ADD COLUMN longitud REAL CHECK("
                + "(latitud IS NULL AND longitud IS NULL) OR "
                + "(latitud IS NOT NULL AND longitud IS NOT NULL AND typeof(longitud) IN ('real','integer') "
                + "AND longitud BETWEEN -180 AND 180))");
    }
    private void crearPagos(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pago_compra (compra_id INTEGER PRIMARY KEY REFERENCES compras(id) ON DELETE RESTRICT, "
                + "movimiento_id INTEGER NOT NULL UNIQUE REFERENCES movimientos(id) ON DELETE RESTRICT)");
        db.execSQL("CREATE TRIGGER cuentas_saldo_inicial_insert BEFORE INSERT ON cuentas "
                + "WHEN NEW.saldo_inicial<0 BEGIN SELECT RAISE(ABORT,'El saldo inicial no puede ser negativo'); END");
        db.execSQL("CREATE TRIGGER cuentas_saldo_inicial_fijo BEFORE UPDATE OF saldo_inicial ON cuentas "
                + "WHEN NEW.saldo_inicial<>OLD.saldo_inicial BEGIN SELECT RAISE(ABORT,'El saldo inicial es fijo'); END");
    }

    private void crearDivisas(SQLiteDatabase db) {
        String catalogo = "'CLP','USD','EUR','ARS','MXN','COP','PEN','BRL','GBP','JPY'";
        db.execSQL("CREATE TABLE configuracion (id INTEGER PRIMARY KEY CHECK(id=1), "
                + "divisa TEXT NOT NULL CHECK(divisa IN (" + catalogo + ")))");
        db.execSQL("CREATE TABLE reparto_usuario (compra_id INTEGER PRIMARY KEY REFERENCES compras(id) ON DELETE RESTRICT, "
                + "monto INTEGER NOT NULL CHECK(typeof(monto)='integer' AND monto>=0))");
        for (String tabla : new String[]{"cuentas", "compras"}) {
            db.execSQL("ALTER TABLE " + tabla + " ADD COLUMN divisa TEXT CHECK(divisa IS NULL OR divisa IN (" + catalogo + "))");
            db.execSQL("CREATE TRIGGER " + tabla + "_divisa_fija BEFORE UPDATE OF divisa ON " + tabla
                    + " WHEN OLD.divisa IS NOT NULL AND (NEW.divisa IS NULL OR NEW.divisa<>OLD.divisa) "
                    + "BEGIN SELECT RAISE(ABORT,'La divisa asignada es fija'); END");
        }
        db.execSQL("CREATE TRIGGER cuentas_precision_insert BEFORE INSERT ON cuentas "
                + "WHEN NEW.divisa IN ('CLP','JPY') AND NEW.saldo_inicial%100<>0 "
                + "BEGIN SELECT RAISE(ABORT,'La divisa no admite fracciones'); END");
        db.execSQL("CREATE TRIGGER cuentas_precision_update BEFORE UPDATE ON cuentas "
                + "WHEN NEW.divisa IN ('CLP','JPY') AND (NEW.saldo_inicial%100<>0 OR EXISTS "
                + "(SELECT 1 FROM movimientos WHERE cuenta_id=OLD.id AND monto%100<>0)) "
                + "BEGIN SELECT RAISE(ABORT,'El historial contiene fracciones'); END");
        db.execSQL("CREATE TRIGGER compras_precision_update BEFORE UPDATE OF divisa ON compras "
                + "WHEN NEW.divisa IN ('CLP','JPY') AND (EXISTS "
                + "(SELECT 1 FROM detalle_compra WHERE compra_id=OLD.id AND precio_unitario%100<>0) OR EXISTS "
                + "(SELECT 1 FROM deudas WHERE compra_id=OLD.id AND monto%100<>0) OR EXISTS "
                + "(SELECT 1 FROM reparto_usuario WHERE compra_id=OLD.id AND monto%100<>0)) "
                + "BEGIN SELECT RAISE(ABORT,'La compra contiene fracciones'); END");
        for (String evento : new String[]{"INSERT", "UPDATE"}) {
            db.execSQL("CREATE TRIGGER usuario_precision_" + evento + " BEFORE " + evento + " ON reparto_usuario "
                    + "WHEN (SELECT divisa FROM compras WHERE id=NEW.compra_id) IN ('CLP','JPY') AND NEW.monto%100<>0 "
                    + "BEGIN SELECT RAISE(ABORT,'La divisa no admite fracciones'); END");
            db.execSQL("CREATE TRIGGER movimientos_precision_" + evento + " BEFORE " + evento + " ON movimientos "
                    + "WHEN (SELECT divisa FROM cuentas WHERE id=NEW.cuenta_id) IN ('CLP','JPY') AND NEW.monto%100<>0 "
                    + "BEGIN SELECT RAISE(ABORT,'La divisa no admite fracciones'); END");
            db.execSQL("CREATE TRIGGER detalles_precision_" + evento + " BEFORE " + evento + " ON detalle_compra "
                    + "WHEN (SELECT divisa FROM compras WHERE id=NEW.compra_id) IN ('CLP','JPY') AND NEW.precio_unitario%100<>0 "
                    + "BEGIN SELECT RAISE(ABORT,'La divisa no admite fracciones'); END");
            db.execSQL("CREATE TRIGGER deudas_precision_" + evento + " BEFORE " + evento + " ON deudas "
                    + "WHEN (SELECT divisa FROM compras WHERE id=NEW.compra_id) IN ('CLP','JPY') AND NEW.monto%100<>0 "
                    + "BEGIN SELECT RAISE(ABORT,'La divisa no admite fracciones'); END");
        }
    }

}



