package com.aik1a.localcash;

import android.app.Instrumentation;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.*;
import android.os.Bundle;
import com.aik1a.localcash.application.*;
import com.aik1a.localcash.data.*;
import com.aik1a.localcash.domain.*;
import java.io.File;
import java.util.*;

/** Fixture con nombre independiente: no borra ni sustituye localcash.db. */
public final class CrudChecks {
    private static int checks;
    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
        checks++;
    }
    private static void reject(Runnable action) {
        try { action.run(); } catch (IllegalArgumentException | SQLiteConstraintException expected) { checks++; return; }
        throw new AssertionError("Se aceptó una operación inválida.");
    }
    private static void rejectReason(Runnable action, String expected) {
        try { action.run(); } catch (IllegalArgumentException error) {
            check(error.getMessage().contains(expected), "Motivo del rechazo: " + error.getMessage()); return;
        }
        throw new AssertionError("Se aceptó una operación que debía rechazarse.");
    }
    private static final class FixtureContext extends ContextWrapper {
        private final String database;
        FixtureContext(Context base, String name) { super(base); database = name; }
        @Override public Context getApplicationContext() { return this; }
        @Override public File getDatabasePath(String name) { return super.getDatabasePath(database); }
        @Override public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
            return super.openOrCreateDatabase(database, mode, factory);
        }
        @Override public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler handler) {
            return super.openOrCreateDatabase(database, mode, factory, handler);
        }
    }
    private static String snapshot(SQLiteDatabase db) {
        StringBuilder out = new StringBuilder();
        for (String table : new String[]{"cuentas","movimientos","contactos","compras","detalle_compra","deudas","reparto_usuario","configuracion","compra_movimiento"}) {
            out.append(table);
            try (Cursor cursor = db.rawQuery("SELECT * FROM " + table + " ORDER BY 1,2", null)) {
                while (cursor.moveToNext()) {
                    for (int i = 0; i < cursor.getColumnCount(); i++) out.append('|').append(cursor.isNull(i) ? "<NULL>" : cursor.getString(i));
                    out.append(';');
                }
            }
        }
        return out.toString();
    }
    private static Compra plan(long id, String nombre, Long contacto, long precio) {
        return new Compra(id, nombre, Compra.Estado.PLANIFICADA, 456, contacto,
                Collections.singletonList(new DetalleCompra("Pan", 2, precio)), Divisa.CLP);
    }
    public static void verificarActualizacionInstalada(Instrumentation runner) {
        Bundle result = new Bundle();
        try (LocalCashDbHelper helper = new LocalCashDbHelper(runner.getTargetContext())) {
            check(helper.getWritableDatabase().getVersion() == 7, "Actualización instalada v7");
            try (Cursor cursor = helper.getReadableDatabase().rawQuery("PRAGMA foreign_key_check", null)) {
                check(!cursor.moveToFirst(), "FK de instalación actualizada");
            }
            result.putString("stream", "CRUD02_UPGRADE_PASS v7 sin insertar, borrar ni editar datos de negocio."); runner.finish(-1, result);
        } catch (Throwable error) { result.putString("stream", "CRUD02_UPGRADE_FAIL " + error); runner.finish(0, result); }
    }
    public static void ejecutar(Instrumentation runner) {
        Bundle result = new Bundle();
        FixtureContext context = new FixtureContext(runner.getTargetContext(), "crud02-" + System.nanoTime() + ".db");
        try {
            String previo;
            try (LegacyV6DbHelper legacy = new LegacyV6DbHelper(context)) {
                SQLiteDatabase db = legacy.getWritableDatabase();
                check(db.getVersion() == 6, "Fixture v6 real");
                db.execSQL("INSERT INTO configuracion VALUES(1,'CLP')");
                db.execSQL("INSERT INTO cuentas VALUES(1,'Efectivo',100000,'CLP')");
                db.execSQL("INSERT INTO movimientos(id,cuenta_id,tipo,monto,descripcion,fecha,latitud,longitud) VALUES(1,1,'EGRESO',10000,'Compra previa',123,-33.45,-70.66)");
                db.execSQL("INSERT INTO contactos VALUES(1,'Ana'),(2,'Bea'),(3,'Sin deuda')");
                db.execSQL("INSERT INTO compras VALUES(1,'Realizada','REALIZADA',123,1,'CLP')");
                db.execSQL("INSERT INTO detalle_compra VALUES(1,1,'Compra',1,10000)");
                db.execSQL("INSERT INTO deudas VALUES(1,1,3000,'PENDIENTE'),(1,2,3000,'PENDIENTE'),(1,3,0,'PENDIENTE')");
                db.execSQL("INSERT INTO reparto_usuario VALUES(1,4000)");
                db.execSQL("INSERT INTO compra_movimiento VALUES(1,1)");
                previo = snapshot(db);
                reject(() -> db.execSQL("UPDATE deudas SET estado='PAGADA'"));
            }
            try (LocalCashDbHelper helper = new LocalCashDbHelper(context)) {
                SQLiteDatabase db = helper.getWritableDatabase();
                check(db.getVersion() == 7, "Migración v6→v7");
                check(previo.equals(snapshot(db)), "Todas las filas, monedas, coordenadas y vínculos intactos");
                try (Cursor c = db.rawQuery("PRAGMA foreign_key_check", null)) { check(!c.moveToFirst(), "FK tras upgrade"); }
                try (Cursor c = db.rawQuery("SELECT count(*) FROM sqlite_master WHERE type='trigger' AND name IN ('compras_precision_update','deudas_precision_INSERT','deudas_precision_UPDATE')", null)) {
                    c.moveToFirst(); check(c.getInt(0) == 3, "Triggers conservados");
                }
                reject(() -> db.execSQL("UPDATE deudas SET monto=1 WHERE contacto_id=1"));
                reject(() -> db.execSQL("UPDATE deudas SET estado='OTRO'"));
                CompraService compras = new CompraService(helper);
                MovimientoService cuentas = new MovimientoService(helper);
                DeudaService deudas = new DeudaService(helper);
                deudas.cambiarEstado(1, 1, true);
                deudas.cambiarEstado(1, 1, true);
                check("PAGADA".equals(deudas.listarDeudas(1).get(0).getEstado()), "Estado pagado real e idempotente");
                check(cuentas.calcularSaldo(1) == 90000 && cuentas.listarMovimientos(1).size() == 1, "Sin ingreso automático");
                check(deudas.totalesPendientes(1).isEmpty(), "Pagadas excluidas del total");
                deudas.cambiarEstado(1, 1, false);
                check(deudas.totalesPendientes(1).get(Divisa.CLP) == 3000, "Deshacer marca");
                rejectReason(() -> deudas.cambiarEstado(1, 3, true), "no tiene dinero");
                rejectReason(() -> deudas.cambiarEstado(9999, 1, true), "ya no existe");
                compras.renombrarContacto(1, "Ana corregida");
                cuentas.renombrarCuenta(1, "Billetera");
                check(deudas.listarDeudas(1).get(0).getContacto().getNombre().equals("Ana corregida"), "Nombre nuevo conserva deudas");
                check(cuentas.buscarCuenta(1).getNombre().equals("Billetera") && cuentas.calcularSaldo(1) == 90000, "Cuenta renombrada sin alterar saldo");
                reject(() -> compras.renombrarContacto(1, " "));
                reject(() -> cuentas.renombrarCuenta(1, ""));
                rejectReason(() -> compras.eliminarContacto(1), "tiene compras o deudas");
                rejectReason(() -> compras.eliminarContacto(2), "tiene compras o deudas");
                rejectReason(() -> cuentas.eliminarCuenta(1), "tiene movimientos");
                long libre = compras.crearContacto("Libre");
                compras.eliminarContacto(libre);
                reject(() -> compras.buscarContacto(libre));
                rejectReason(() -> compras.eliminarContacto(libre), "no existe");
                long vacia = cuentas.crearCuenta("Vacía", 0, Divisa.CLP);
                cuentas.eliminarCuenta(vacia);
                reject(() -> cuentas.buscarCuenta(vacia));
                long id = compras.registrar(plan(0, "Plan", null, 1000));
                compras.actualizarPlanificacion(plan(id, "Plan corregido", 1L, 3000));
                check(compras.buscarCompra(id).calcularTotal() == 6000 && compras.buscarCompra(id).getNombre().equals("Plan corregido"), "Edición conserva identidad y actualiza total");
                reject(() -> compras.actualizarPlanificacion(new Compra(id,"Otra",Compra.Estado.PLANIFICADA,456,null,
                        Collections.singletonList(new DetalleCompra("X",1,100)),Divisa.USD)));
                String antesFallo = snapshot(db);
                db.execSQL("CREATE TRIGGER fallo_item BEFORE INSERT ON detalle_compra WHEN NEW.descripcion='Fallar' BEGIN SELECT RAISE(ABORT,'Prueba rollback'); END");
                reject(() -> compras.actualizarPlanificacion(new Compra(id,"No guardar",Compra.Estado.PLANIFICADA,456,null,
                        Collections.singletonList(new DetalleCompra("Fallar",1,100)),Divisa.CLP)));
                check(antesFallo.equals(snapshot(db)), "Rollback de cabecera y detalles");
                db.execSQL("DROP TRIGGER fallo_item");
                db.execSQL("CREATE TRIGGER fallo_borrado BEFORE DELETE ON compras BEGIN SELECT RAISE(ABORT,'Prueba rollback'); END");
                reject(() -> compras.eliminarPlanificacion(id));
                check(antesFallo.equals(snapshot(db)), "Rollback de borrado de detalles");
                db.execSQL("DROP TRIGGER fallo_borrado");
                compras.eliminarPlanificacion(id);
                reject(() -> compras.buscarCompra(id));
                reject(() -> compras.eliminarPlanificacion(1));
                reject(() -> compras.actualizarPlanificacion(plan(1, "No", null, 1000)));
                check(cuentas.calcularSaldo(1) == 90000, "CRUD de planificaciones no cambia saldo");
                long compraUsd = compras.registrar(new Compra(0,"USD",Compra.Estado.REALIZADA,123,null,
                        Collections.singletonList(new DetalleCompra("X",1,1000)),Divisa.USD));
                deudas.repartirEquitativoConUsuario(compraUsd, Collections.singletonList(1L));
                Map<Divisa,Long> totales = deudas.totalesPendientes(1);
                check(totales.get(Divisa.CLP) == 3000 && totales.get(Divisa.USD) == 500, "Totales separados por divisa");
                deudas.cambiarEstado(1, 1, true);
            }
            try (LocalCashDbHelper helper = new LocalCashDbHelper(context)) {
                check("PAGADA".equals(new DeudaService(helper).listarDeudas(1).get(0).getEstado()), "Estado persiste tras reabrir");
                try (Cursor c = helper.getReadableDatabase().rawQuery("PRAGMA integrity_check", null)) { c.moveToFirst(); check("ok".equals(c.getString(0)), "Integridad final"); }
            }
            try (LocalCashDbHelper helper = new LocalCashDbHelper(new FixtureContext(runner.getTargetContext(), "crud02-fresh-" + System.nanoTime() + ".db"))) {
                check(helper.getWritableDatabase().getVersion() == 7, "Instalación limpia v7");
            }
            result.putString("stream", "CRUD02_PASS " + checks + " comprobaciones Android: migración real v6→v7, estados, nombres, restricciones, edición, rollback y monedas.");
            runner.finish(-1, result);
        } catch (Throwable error) {
            result.putString("stream", "CRUD02_FAIL " + error);
            android.util.Log.e("CRUD02", "Prueba fallida", error);
            runner.finish(0, result);
        }
    }
}
