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

/** En emulador descartable; valida transacciones reales, no mocks de repositorios. */
public final class PaymentCrudChecks {
    private static int checks;
    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message); checks++;
    }
    private static void reject(Runnable code) {
        try { code.run(); throw new AssertionError("Operación inválida aceptada"); }
        catch (IllegalArgumentException | IllegalStateException | SQLiteException | ArithmeticException expected) { checks++; }
    }
    private static Compra compra(long id, String nombre, Compra.Estado estado, long monto, Divisa divisa, Long contacto) {
        return new Compra(id,nombre,estado,1234,contacto,Arrays.asList(new DetalleCompra("Producto",1,monto)),divisa);
    }
    private static String snapshot(SQLiteDatabase db) {
        StringBuilder out=new StringBuilder();
        for(String table:new String[]{"cuentas","movimientos","contactos","compras","detalle_compra","deudas","configuracion","reparto_usuario","pago_compra"}) {
            out.append(table);
            try(Cursor c=db.rawQuery("SELECT * FROM "+table+" ORDER BY 1,2",null)) {
                while(c.moveToNext()) for(int i=0;i<c.getColumnCount();i++) {
                    String v=c.isNull(i)?"<NULL>":c.getString(i);out.append(v.length()).append(':').append(v);
                }
            }
        }
        return out.toString();
    }
    private static void rejectUnchanged(SQLiteDatabase db,Runnable code) {
        String before=snapshot(db);reject(code);check(before.equals(snapshot(db)),"Rechazo sin escrituras parciales");
    }
    public static void ejecutar(Instrumentation test) {
        Bundle result=new Bundle();
        try {
            Context context=test.getTargetContext();migrarV5(context);
            context.deleteDatabase("localcash.db");
            long persistida,cuentaPersistida;
            try(LocalCashDbHelper helper=new LocalCashDbHelper(context)) {
                SQLiteDatabase db=helper.getWritableDatabase();check(db.getVersion()==6,"Instalación v6");
                new ConfiguracionRepository(helper).configurarPrimeraVez(Divisa.USD);
                MovimientoService m=new MovimientoService(helper);CompraService c=new CompraService(helper);
                rejectUnchanged(db,()->m.crearCuenta("Negativa",-1,Divisa.USD));
                long cuenta=m.crearCuenta("Banco",10000,Divisa.USD);
                long clp=m.crearCuenta("Efectivo",10000,Divisa.CLP);
                long insuficiente=m.crearCuenta("Pequeña",5000,Divisa.USD);
                m.editarCuenta(cuenta,"Banco principal");check(m.buscarCuenta(cuenta).getNombre().equals("Banco principal"),"Editar cuenta");
                rejectUnchanged(db,()->m.editarCuenta(cuenta," "));
                long vacia=m.crearCuenta("Vacía",0,Divisa.USD);m.eliminarCuenta(vacia);reject(()->m.buscarCuenta(vacia));
                long contacto=c.crearContacto("Valentina");c.editarContacto(contacto,"Vale");check(c.buscarContacto(contacto).getNombre().equals("Vale"),"Editar contacto");
                rejectUnchanged(db,()->c.editarContacto(contacto," "));
                long libre=c.crearContacto("Libre");c.eliminarContacto(libre);reject(()->c.buscarContacto(libre));
                long plan=c.registrar(compra(0,"Plan",Compra.Estado.PLANIFICADA,10000,Divisa.USD,contacto));
                check(m.calcularSaldo(cuenta)==10000&&m.listarMovimientos(cuenta).isEmpty(),"Plan no descuenta");
                check(c.buscarCuentaPago(plan)==null,"Plan sin vínculo");
                rejectUnchanged(db,()->c.eliminarContacto(contacto));
                rejectUnchanged(db,()->c.realizar(plan,clp));
                rejectUnchanged(db,()->c.realizar(plan,insuficiente));
                check(c.buscarCompra(plan).getEstado()==Compra.Estado.PLANIFICADA,"Fondos insuficientes mantiene plan");
                c.editarPlanificada(compra(plan,"Plan editado",Compra.Estado.PLANIFICADA,9000,Divisa.USD,null));
                check(c.buscarCompra(plan).calcularTotal()==9000&&c.buscarCompra(plan).getContactoId()==null,"Editar plan e ítems");
                rejectUnchanged(db,()->c.editarPlanificada(compra(plan,"Otro",Compra.Estado.PLANIFICADA,9000,Divisa.EUR,null)));
                c.editarPlanificada(compra(plan,"Plan exacto",Compra.Estado.PLANIFICADA,10000,Divisa.USD,contacto));
                db.execSQL("CREATE TRIGGER falla_pago BEFORE INSERT ON pago_compra BEGIN SELECT RAISE(ABORT,'test'); END");
                rejectUnchanged(db,()->c.realizar(plan,cuenta));db.execSQL("DROP TRIGGER falla_pago");
                c.realizar(plan,cuenta);
                check(m.calcularSaldo(cuenta)==0,"Fondos exactos deja cero");
                check(c.buscarCompra(plan).getEstado()==Compra.Estado.REALIZADA,"Estado realizado atómico");
                check(c.buscarCuentaPago(plan)==cuenta,"Cuenta de pago vinculada");
                check(m.listarMovimientos(cuenta).size()==1,"Un egreso por compra");
                long pago=m.listarMovimientos(cuenta).get(0).getId();
                check(m.esPagoCompra(pago),"Movimiento protegido identificado");
                String antesReintento=snapshot(db);c.realizar(plan,cuenta);check(antesReintento.equals(snapshot(db)),"Reintento no duplica cobro");
                rejectUnchanged(db,()->m.editarMovimiento(pago,Movimiento.Tipo.INGRESO,10000,"Cambio"));
                rejectUnchanged(db,()->m.eliminarMovimiento(pago));
                rejectUnchanged(db,()->m.registrar(cuenta,Movimiento.Tipo.EGRESO,1,"Sin dinero"));
                rejectUnchanged(db,()->m.eliminarCuenta(cuenta));
                rejectUnchanged(db,()->c.editarPlanificada(compra(plan,"Reescrita",Compra.Estado.PLANIFICADA,1,Divisa.USD,null)));
                c.renombrar(plan,"Pagada");check(c.buscarCompra(plan).getNombre().equals("Pagada"),"Renombrar realizada");
                db.execSQL("CREATE TRIGGER falla_borrar_compra BEFORE DELETE ON compras BEGIN SELECT RAISE(ABORT,'test'); END");
                rejectUnchanged(db,()->c.eliminarCompra(plan));db.execSQL("DROP TRIGGER falla_borrar_compra");
                c.eliminarCompra(plan);check(m.calcularSaldo(cuenta)==10000&&m.listarMovimientos(cuenta).isEmpty(),"Borrar compra pagada revierte egreso");
                reject(()->c.buscarCompra(plan));
                Compra realizada=compra(0,"Nueva realizada",Compra.Estado.REALIZADA,10000,Divisa.USD,null);
                rejectUnchanged(db,()->c.registrar(realizada));
                rejectUnchanged(db,()->c.registrar(realizada,insuficiente));
                rejectUnchanged(db,()->c.registrar(realizada,clp));
                db.execSQL("CREATE TRIGGER falla_pago BEFORE INSERT ON pago_compra BEGIN SELECT RAISE(ABORT,'test'); END");
                rejectUnchanged(db,()->c.registrar(realizada,cuenta));db.execSQL("DROP TRIGGER falla_pago");
                long nueva=c.registrar(realizada,cuenta);check(m.calcularSaldo(cuenta)==0&&c.buscarCuentaPago(nueva)==cuenta,"Crear realizada paga exactamente una vez");
                c.eliminarCompra(nueva);check(m.calcularSaldo(cuenta)==10000,"Reembolso compra nueva");
                long ingreso=m.registrar(cuenta,Movimiento.Tipo.INGRESO,2000,"Sueldo");
                long egreso=m.registrar(cuenta,Movimiento.Tipo.EGRESO,11000,"Manual");
                check(m.calcularSaldo(cuenta)==1000,"Egreso manual permitido");
                rejectUnchanged(db,()->m.eliminarMovimiento(ingreso));
                rejectUnchanged(db,()->m.editarMovimiento(ingreso,Movimiento.Tipo.INGRESO,500,"Reducir"));
                rejectUnchanged(db,()->m.editarMovimiento(egreso,Movimiento.Tipo.EGRESO,12001,"Aumentar"));
                m.editarMovimiento(egreso,Movimiento.Tipo.EGRESO,12000,"Exacto");check(m.calcularSaldo(cuenta)==0,"Editar egreso a saldo exacto");
                m.eliminarMovimiento(egreso);check(m.calcularSaldo(cuenta)==12000,"Borrar egreso recupera saldo");
                m.editarMovimiento(ingreso,Movimiento.Tipo.EGRESO,2000,"Cambio tipo");check(m.calcularSaldo(cuenta)==8000,"Editar tipo manual");
                m.eliminarMovimiento(ingreso);check(m.calcularSaldo(cuenta)==10000,"Borrar manual");
                long borrable=c.registrar(compra(0,"Temporal",Compra.Estado.PLANIFICADA,0,Divisa.USD,null));
                c.eliminarCompra(borrable);reject(()->c.buscarCompra(borrable));
                long cero=c.registrar(compra(0,"Cero",Compra.Estado.PLANIFICADA,0,Divisa.USD,null));
                rejectUnchanged(db,()->c.realizar(cero,cuenta));
                persistida=c.registrar(compra(0,"Persistida",Compra.Estado.REALIZADA,2500,Divisa.USD,contacto),cuenta);cuentaPersistida=cuenta;
                new DeudaService(helper).repartirEquitativoConUsuario(persistida,Arrays.asList(contacto));
                rejectUnchanged(db,()->c.eliminarCompra(persistida));rejectUnchanged(db,()->c.eliminarContacto(contacto));
                check(m.calcularSaldo(cuenta)==7500,"Repartir no vuelve a cobrar");
                try(Cursor f=db.rawQuery("PRAGMA foreign_key_check",null)){check(!f.moveToFirst(),"FK válidas");}
            }
            try(LocalCashDbHelper helper=new LocalCashDbHelper(context)) {
                CompraService c=new CompraService(helper);MovimientoService m=new MovimientoService(helper);
                check(c.buscarCuentaPago(persistida)==cuentaPersistida,"Vínculo tras reapertura");
                check(c.buscarCompra(persistida).getEstado()==Compra.Estado.REALIZADA&&m.calcularSaldo(cuentaPersistida)==7500,"Estado y saldo persistentes");
                String antesReintento=snapshot(helper.getWritableDatabase());c.realizar(persistida,cuentaPersistida);
                check(antesReintento.equals(snapshot(helper.getWritableDatabase())),"Reintento tras reapertura no cobra");
            }
            result.putString("stream","LOCALCASH_PAYMENTS_PASS\nPASS "+checks+" native Android payment/CRUD/migration checks.\n");test.finish(Activity.RESULT_OK,result);
        } catch(Throwable error) {
            result.putString("stream","LOCALCASH_PAYMENTS_FAIL\n"+android.util.Log.getStackTraceString(error));test.finish(Activity.RESULT_CANCELED,result);
        }
    }
    private static void migrarV5(Context context) {
        context.deleteDatabase("localcash.db");String before;
        try(SQLiteOpenHelper old=new SQLiteOpenHelper(context,"localcash.db",null,5) {
            public void onConfigure(SQLiteDatabase db){db.setForeignKeyConstraintsEnabled(true);}
            public void onCreate(SQLiteDatabase db){
                db.execSQL("CREATE TABLE cuentas(id INTEGER PRIMARY KEY,nombre TEXT NOT NULL,saldo_inicial INTEGER NOT NULL)");
                db.execSQL("CREATE TABLE movimientos(id INTEGER PRIMARY KEY,cuenta_id INTEGER NOT NULL REFERENCES cuentas(id),tipo TEXT NOT NULL,monto INTEGER NOT NULL,descripcion TEXT NOT NULL DEFAULT '',fecha INTEGER NOT NULL)");
                // Reproduce el esquema aditivo v2-v5 y conserva los datos anteriores al pago.
                new LocalCashDbHelper(context).onUpgrade(db,1,5);
                db.execSQL("INSERT INTO configuracion VALUES(1,'USD')");
                db.execSQL("INSERT INTO cuentas VALUES(1,'Histórica',-500,'USD')");
                db.execSQL("INSERT INTO contactos VALUES(1,'Ana')");
                db.execSQL("INSERT INTO compras VALUES(1,'Anterior','REALIZADA',123,1,'USD')");
                db.execSQL("INSERT INTO detalle_compra VALUES(1,1,'Pan',1,10000)");
                db.execSQL("INSERT INTO deudas VALUES(1,1,5000,'PENDIENTE')");
                db.execSQL("INSERT INTO reparto_usuario VALUES(1,5000)");
                db.execSQL("INSERT INTO movimientos VALUES(1,1,'INGRESO',100,'GPS',123,-33.45,-70.66)");
            }
            public void onUpgrade(SQLiteDatabase db,int from,int to){throw new AssertionError("Fixture v5");}
        }) {
            SQLiteDatabase db=old.getWritableDatabase();
            // snapshot() incluye la tabla nueva: crear vacía solo para comparar el resto por separado.
            before=legacySnapshot(db);check(db.getVersion()==5,"Fixture real versión5");
        }
        try(LocalCashDbHelper helper=new LocalCashDbHelper(context)) {
            SQLiteDatabase db=helper.getWritableDatabase();check(db.getVersion()==6,"Migración5 a6");
            check(before.equals(legacySnapshot(db)),"Todas las filas v5 intactas");
            CompraService c=new CompraService(helper);MovimientoService m=new MovimientoService(helper);
            check(c.buscarCuentaPago(1)==null,"Compra antigua sin cargo retroactivo");
            check(m.calcularSaldo(1)==-400,"Saldo negativo legado conservado");
            rejectUnchanged(db,()->m.registrar(1,Movimiento.Tipo.EGRESO,1,"Empeorar"));
            m.registrar(1,Movimiento.Tipo.INGRESO,100,"Recuperar");check(m.calcularSaldo(1)==-300,"Ingreso mejora saldo legado");
            m.registrar(1,Movimiento.Tipo.INGRESO,300,"Recuperar");check(m.calcularSaldo(1)==0,"Ingreso recupera saldo legado");
            rejectUnchanged(db,()->c.realizar(1,1));
            try(Cursor f=db.rawQuery("PRAGMA foreign_key_check",null)){check(!f.moveToFirst(),"FK migración5");}
        }
    }
    private static String legacySnapshot(SQLiteDatabase db) {
        StringBuilder out=new StringBuilder();
        for(String table:new String[]{"cuentas","movimientos","contactos","compras","detalle_compra","deudas","configuracion","reparto_usuario"}) {
            try(Cursor c=db.rawQuery("SELECT * FROM "+table+" ORDER BY 1,2",null)) {
                out.append(table);while(c.moveToNext())for(int i=0;i<c.getColumnCount();i++)out.append(c.getString(i)).append('|');
            }
        }
        return out.toString();
    }
}
