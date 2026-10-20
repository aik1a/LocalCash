package com.aik1a.localcash;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.os.Bundle;
import com.aik1a.localcash.application.*;
import com.aik1a.localcash.data.*;
import com.aik1a.localcash.domain.*;
import java.util.*;

/** Ejecutar únicamente en emulador descartable: crea fixtures y elimina su base de pruebas. */
public final class CurrencyChecks {
    private static int checks;
    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message); checks++;
    }
    private static void reject(Runnable code) {
        try { code.run(); throw new AssertionError("Entrada inválida aceptada"); }
        catch (IllegalArgumentException | SQLiteException | ArithmeticException expected) { checks++; }
    }
    private static Compra compra(long monto, Divisa divisa) {
        return new Compra(0, "Compra", Compra.Estado.REALIZADA, 1234, null,
                Arrays.asList(new DetalleCompra("Pan", 1, monto)), divisa);
    }
    public static void ejecutar(Instrumentation test) {
        Bundle result = new Bundle();
        try {
            Context context = test.getTargetContext();
            for (int v = 1; v <= 4; v++) migrar(context, v);
            context.deleteDatabase("localcash.db");
            long cuentaClp, cuentaUsd, compraId, contacto;
            try (LocalCashDbHelper helper = new LocalCashDbHelper(context)) {
                check(helper.getWritableDatabase().getVersion() == 5, "Instalación v5");
                ConfiguracionRepository config = new ConfiguracionRepository(helper);
                check(config.buscarDivisa() == null, "Primera vez pendiente");
                config.configurarPrimeraVez(Divisa.CLP); config.configurarPrimeraVez(Divisa.CLP);
                reject(() -> config.configurarPrimeraVez(Divisa.USD));
                check(config.buscarDivisa() == Divisa.CLP, "Configuración única");
                MovimientoService movimientos = new MovimientoService(helper);
                cuentaClp = movimientos.crearCuenta("Efectivo", 10000); // Usa preferencia principal.
                cuentaUsd = movimientos.crearCuenta("Dólares", 2550, Divisa.USD);
                check(movimientos.buscarCuenta(cuentaClp).getDivisa() == Divisa.CLP, "Cuenta hereda principal");
                check(movimientos.buscarCuenta(cuentaUsd).getDivisa() == Divisa.USD, "Otra moneda por cuenta");
                movimientos.registrar(cuentaClp, Movimiento.Tipo.INGRESO, 5000, "Ingreso");
                reject(() -> movimientos.registrar(cuentaClp, Movimiento.Tipo.INGRESO, 1, "Fracción"));
                movimientos.registrar(cuentaUsd, Movimiento.Tipo.EGRESO, 1, "Centavo");
                check(movimientos.calcularSaldo(cuentaClp) == 15000, "Saldo CLP exacto");
                check(movimientos.calcularSaldo(cuentaUsd) == 2549, "Saldo USD separado");
                reject(() -> new CuentaRepository(helper).asignarDivisa(cuentaClp, Divisa.USD));
                CompraService compras = new CompraService(helper);
                contacto = compras.crearContacto("Valentina");
                compraId = compras.registrar(compra(10000, Divisa.CLP));
                DeudaService deudas = new DeudaService(helper);
                deudas.repartirEquitativoConUsuario(compraId, Arrays.asList(contacto));
                check(deudas.buscarMontoUsuario(compraId) == 5000, "Cuota Tú 50");
                check(deudas.listarDeudas(compraId).size() == 1 && deudas.listarDeudas(compraId).get(0).getMonto() == 5000,
                        "Solo contacto tiene deuda 50");
                check(compras.listarContactos().size() == 1, "No crea contacto fantasma");
                reject(() -> deudas.repartirEquitativoConUsuario(compraId, Arrays.asList(contacto)));
                long personalizada = compras.registrar(compra(10000, Divisa.CLP));
                reject(() -> deudas.repartirPersonalizadoConUsuario(personalizada, Arrays.asList(contacto), Arrays.asList(9900L), 50));
                check(deudas.buscarMontoUsuario(personalizada) == null && deudas.listarDeudas(personalizada).isEmpty(), "Error no escribe");
                deudas.repartirPersonalizadoConUsuario(personalizada, Arrays.asList(contacto), Arrays.asList(6000L), 4000);
                check(deudas.buscarMontoUsuario(personalizada) == 4000, "Personalizado conserva Tú");
                SQLiteDatabase db = helper.getWritableDatabase();
                long rollback = compras.registrar(compra(10000, Divisa.CLP));
                db.execSQL("CREATE TRIGGER falla_usuario BEFORE INSERT ON reparto_usuario BEGIN SELECT RAISE(ABORT,'test'); END");
                reject(() -> deudas.repartirEquitativoConUsuario(rollback, Arrays.asList(contacto)));
                check(deudas.listarDeudas(rollback).isEmpty() && deudas.buscarMontoUsuario(rollback) == null, "Rollback cuota propia y deudas");
                db.execSQL("DROP TRIGGER falla_usuario");
                reject(() -> db.execSQL("INSERT INTO movimientos(cuenta_id,tipo,monto,fecha) VALUES(" + cuentaClp + ",'INGRESO',1,0)"));
                reject(() -> db.execSQL("UPDATE cuentas SET divisa='USD' WHERE id=" + cuentaClp));
                reject(() -> db.execSQL("UPDATE deudas SET monto=1 WHERE compra_id=" + compraId));
                reject(() -> db.execSQL("UPDATE reparto_usuario SET monto=1 WHERE compra_id=" + compraId));
                reject(() -> db.execSQL("INSERT INTO cuentas(nombre,saldo_inicial,divisa) VALUES('X',0,'XXX')"));
                try (Cursor c = db.rawQuery("PRAGMA foreign_key_check", null)) { check(!c.moveToFirst(), "FK válidas"); }
            }
            try (LocalCashDbHelper helper = new LocalCashDbHelper(context)) {
                check(new ConfiguracionRepository(helper).buscarDivisa() == Divisa.CLP, "Preferencia tras reabrir");
                check(new CuentaRepository(helper).buscar(cuentaUsd).getDivisa() == Divisa.USD, "Divisa cuenta tras reabrir");
                check(new DeudaRepository(helper).buscarMontoUsuario(compraId) == 5000, "Cuota propia tras reabrir");
                check(new DeudaRepository(helper).listarPorCompra(compraId).get(0).getMonto() == 5000, "Deuda tras reabrir");
            }
            result.putString("stream", "LOCALCASH_CURRENCY_PASS\nPASS " + checks + " Android SQLite/repository checks; migrations v1-v4→v5.\n");
            test.finish(Activity.RESULT_OK, result);
        } catch (Throwable error) {
            result.putString("stream", "LOCALCASH_CURRENCY_FAIL\n" + android.util.Log.getStackTraceString(error));
            test.finish(Activity.RESULT_CANCELED, result);
        }
    }
    private static void migrar(Context context, int version) {
        context.deleteDatabase("localcash.db");
        String antes;
        try (SQLiteOpenHelper old = fixture(context, version)) { antes = snapshot(old.getWritableDatabase()); }
        try (LocalCashDbHelper helper = new LocalCashDbHelper(context)) {
            SQLiteDatabase db = helper.getWritableDatabase();
            check(db.getVersion() == 5, "Migración v" + version);
            check(antes.equals(snapshot(db)), "Conserva datos históricos v" + version);
            ConfiguracionRepository config = new ConfiguracionRepository(helper);
            check(config.buscarDivisa() == null, "Migración no inventa preferencia");
            CuentaRepository cuentas = new CuentaRepository(helper);
            check(cuentas.buscar(1).getDivisa() == null, "Cuenta histórica sin moneda asumida");
            reject(() -> new MovimientoService(helper).registrar(1, Movimiento.Tipo.INGRESO, 100, ""));
            reject(() -> cuentas.asignarDivisa(1, Divisa.CLP));
            check(cuentas.buscar(1).getDivisa() == null && cuentas.buscar(1).getSaldoInicial() == 10025, "Fracción antigua intacta");
            cuentas.asignarDivisa(1, Divisa.USD);
            check(cuentas.buscar(1).getDivisa() == Divisa.USD, "Asignación explícita USD");
            if (version >= 2) {
                CompraRepository compras = new CompraRepository(helper);
                reject(() -> compras.asignarDivisa(1, Divisa.CLP));
                compras.asignarDivisa(1, Divisa.USD);
                check(compras.buscar(1).calcularTotal() == (version == 2 ? 10025 : 10000), "Compra no cambia");
                if (version >= 3) {
                    check(new DeudaRepository(helper).buscarMontoUsuario(1) == null, "No agrega Tú retroactivo");
                    check(new DeudaRepository(helper).listarPorCompra(1).get(0).getMonto() == 5001, "Deuda previa intacta");
                }
            }
            if (version == 4) check(new MovimientoRepository(helper).listar(1).get(0).tieneUbicacion(), "Coordenadas conservadas");
            try (Cursor c = db.rawQuery("PRAGMA foreign_key_check", null)) { check(!c.moveToFirst(), "FK tras migración"); }
        }
    }
    private static String snapshot(SQLiteDatabase db) {
        StringBuilder out = new StringBuilder();
        String[][] queries = {{"cuentas","id,nombre,saldo_inicial"}, {"movimientos","id,cuenta_id,tipo,monto,descripcion,fecha"},
                {"contactos","id,nombre"}, {"compras","id,nombre,estado,fecha,contacto_id"},
                {"detalle_compra","id,compra_id,descripcion,cantidad,precio_unitario"}, {"deudas","compra_id,contacto_id,monto,estado"}};
        for (String[] query : queries) {
            try (Cursor exists = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{query[0]})) {
                if (!exists.moveToFirst()) continue;
            }
            try (Cursor c = db.rawQuery("SELECT " + query[1] + " FROM " + query[0] + " ORDER BY 1,2", null)) {
                while (c.moveToNext()) for (int col = 0; col < c.getColumnCount(); col++) out.append(c.getString(col)).append('|');
            }
        }
        return out.toString();
    }
    private static SQLiteOpenHelper fixture(Context context, final int version) {
        return new SQLiteOpenHelper(context, "localcash.db", null, version) {
            public void onConfigure(SQLiteDatabase db) { db.setForeignKeyConstraintsEnabled(true); }
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE cuentas(id INTEGER PRIMARY KEY,nombre TEXT NOT NULL,saldo_inicial INTEGER NOT NULL)");
                db.execSQL("CREATE TABLE movimientos(id INTEGER PRIMARY KEY,cuenta_id INTEGER NOT NULL REFERENCES cuentas(id),tipo TEXT NOT NULL,monto INTEGER NOT NULL,descripcion TEXT NOT NULL DEFAULT '',fecha INTEGER NOT NULL)");
                db.execSQL("INSERT INTO cuentas VALUES(1,'Efectivo',10025)");
                db.execSQL("INSERT INTO movimientos VALUES(1,1,'INGRESO',2550,'Anterior',1234)");
                if (version >= 2) {
                    db.execSQL("CREATE TABLE contactos(id INTEGER PRIMARY KEY,nombre TEXT NOT NULL)");
                    db.execSQL("CREATE TABLE compras(id INTEGER PRIMARY KEY,nombre TEXT NOT NULL,estado TEXT NOT NULL,fecha INTEGER NOT NULL,contacto_id INTEGER REFERENCES contactos(id))");
                    db.execSQL("CREATE TABLE detalle_compra(id INTEGER PRIMARY KEY,compra_id INTEGER NOT NULL REFERENCES compras(id),descripcion TEXT NOT NULL,cantidad INTEGER NOT NULL,precio_unitario INTEGER NOT NULL)");
                    db.execSQL("INSERT INTO contactos VALUES(1,'Valentina'),(2,'Ana')");
                    db.execSQL("INSERT INTO compras VALUES(1,'Anterior','REALIZADA',1234,1)");
                    db.execSQL("INSERT INTO detalle_compra VALUES(1,1,'Pan',1,10000)");
                    if (version == 2) db.execSQL("UPDATE detalle_compra SET precio_unitario=10025 WHERE id=1");
                }
                if (version >= 3) {
                    db.execSQL("CREATE TABLE deudas(compra_id INTEGER NOT NULL REFERENCES compras(id),contacto_id INTEGER NOT NULL REFERENCES contactos(id),monto INTEGER NOT NULL,estado TEXT NOT NULL,PRIMARY KEY(compra_id,contacto_id))");
                    db.execSQL("INSERT INTO deudas VALUES(1,1,5001,'PENDIENTE'),(1,2,4999,'PENDIENTE')");
                }
                if (version >= 4) {
                    db.execSQL("ALTER TABLE movimientos ADD COLUMN latitud REAL");
                    db.execSQL("ALTER TABLE movimientos ADD COLUMN longitud REAL");
                    db.execSQL("UPDATE movimientos SET latitud=-33.45,longitud=-70.66 WHERE id=1");
                }
            }
            public void onUpgrade(SQLiteDatabase db, int from, int to) { throw new AssertionError("Fixture inmutable"); }
        };
    }
}
