package com.aik1a.localcash;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityNodeInfo;
import com.aik1a.localcash.application.MovimientoService;
import com.aik1a.localcash.application.CompraService;
import com.aik1a.localcash.data.LocalCashDbHelper;
import com.aik1a.localcash.domain.*;
import com.aik1a.localcash.presentation.MainActivity;
import java.util.List;

/** Prueba de flujo en emulador desechable; sin framework ni dependencias adicionales. */
public final class FlowChecks extends Instrumentation {
    private final StringBuilder log = new StringBuilder();
    private Activity main;

    private boolean compras;
    private boolean compartidas;
    private boolean ubicaciones;
    private Bundle argumentosBiometricos;
    private boolean mapas;
    @Override public void onCreate(Bundle arguments) {
        super.onCreate(arguments);
        compras = arguments != null && "purchases".equals(arguments.getString("mode"));
        compartidas = arguments != null && "shared".equals(arguments.getString("mode"));
        ubicaciones = arguments != null && "location".equals(arguments.getString("mode"));
        if (arguments != null && arguments.getString("mode", "").startsWith("bio-")) argumentosBiometricos = arguments;
        mapas = arguments != null && "maps".equals(arguments.getString("mode"));
        start();
    }
    @Override public void onStart() { if (mapas) { MapChecks.ejecutar(this); return; } if (argumentosBiometricos != null) BiometricChecks.ejecutar(this, argumentosBiometricos); else if (ubicaciones) verificarUbicaciones(); else if (compartidas) verificarCompartidas(); else if (compras) verificarCompras(); else verificarCuentas(); }

    private void verificarCuentas() {
        Bundle result = new Bundle();
        try {
            getTargetContext().deleteDatabase("localcash.db");
            AccessibilityServiceInfo info = getUiAutomation().getServiceInfo();
            info.flags |= AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
            getUiAutomation().setServiceInfo(info);
            abrir();
            clickId("crear_cuenta");
            setText("nombre", " ");
            clickText("Guardar");
            assertTrue(nodeId("nombre") != null, "Nombre vacío mantiene formulario abierto");
            setText("nombre", "Efectivo");
            setText("saldo_inicial", "100,00");
            clickText("Guardar");
            nodeText("Efectivo");
            clickId("crear_cuenta");
            setText("nombre", "Banco");
            setText("saldo_inicial", "300");
            clickText("Guardar");
            nodeText("Banco");
            log.append("PASS ACC-01: dos cuentas creadas desde formularios XML.\n");
            clickText("Efectivo");
            nodeText("Saldo: 100,00");
            clickText("Registrar ingreso o egreso");
            clickText("Ingreso");
            setText("monto", "0");
            clickText("Guardar");
            nodeId("monto");
            setText("monto", "-10");
            clickText("Guardar");
            nodeId("monto");
            setText("monto", "50");
            setText("descripcion", "Sueldo");
            clickText("Guardar");
            nodeText("Saldo: 150,00");
            clickText("Registrar ingreso o egreso");
            clickText("Egreso");
            setText("monto", "24,50");
            setText("descripcion", "Transporte");
            clickText("Guardar");
            nodeText("Saldo: 125,50");
            log.append("PASS MOV-01: ingreso/egreso por UI; 100 + 50 - 24,50 = 125,50; monto 0/negativo rechazado.\n");
            clickText("Volver a cuentas");
            nodeText("Banco");
            clickText("Banco");
            nodeText("Saldo: 300,00");
            nodeText("Todavía no hay movimientos en esta cuenta.");
            clickText("Volver a cuentas");
            runOnMainSync(() -> main.finish());
            esperarUI();

            LocalCashDbHelper helper = new LocalCashDbHelper(getTargetContext());
            MovimientoService service = new MovimientoService(helper);
            List<Cuenta> cuentas = service.listarCuentas();
            assertTrue(cuentas.size() == 2, "Se conservan dos cuentas");
            long efectivo = cuentas.get(0).getId();
            assertTrue(service.calcularSaldo(efectivo) == 12550, "Saldo exacto SQLite");
            assertTrue(service.listarMovimientos(efectivo).size() == 2, "No se persistieron montos inválidos");
            assertTrue(service.listarMovimientos(cuentas.get(1).getId()).isEmpty(), "Historial aislado");
            try {
                service.registrar(999999, Movimiento.Tipo.INGRESO, 1, "");
                throw new AssertionError("Cuenta inexistente aceptada");
            } catch (IllegalArgumentException expected) { log.append("PASS cuenta inexistente rechazada.\n"); }
            long limite = service.crearCuenta("Límite", Long.MAX_VALUE);
            try {
                service.registrar(limite, Movimiento.Tipo.INGRESO, 1, "");
                throw new AssertionError("Overflow aceptado");
            } catch (ArithmeticException expected) {
                assertTrue(service.listarMovimientos(limite).isEmpty(), "Rollback de overflow");
                log.append("PASS overflow rechazado sin insertar.\n");
            }
            // Prueba FK real además de validación de aplicación.
            try {
                new com.aik1a.localcash.data.MovimientoRepository(helper).crear(
                        new Movimiento(0, 999999, Movimiento.Tipo.INGRESO, 1, "", 0));
                throw new AssertionError("FK desactivada");
            } catch (android.database.sqlite.SQLiteConstraintException expected) {
                log.append("PASS integridad referencial SQLite.\n");
            }
            helper.close();
            helper = new LocalCashDbHelper(getTargetContext());
            assertTrue(new MovimientoService(helper).calcularSaldo(efectivo) == 12550, "Reapertura helper");
            helper.close();
            abrir();
            nodeText("Saldo: 125,50");
            clickText("Efectivo");
            nodeText("Saldo: 125,50");
            log.append("PASS DB-01: reapertura BD y Activities; datos recuperados.\n");
            result.putString("stream", "LOCALCASH_FLOW_PASS\n" + log);
            finish(Activity.RESULT_OK, result);
        } catch (Throwable failure) {
            dump(getUiAutomation().getRootInActiveWindow(), 0);
            result.putString("stream", "LOCALCASH_FLOW_FAIL\n" + log + android.util.Log.getStackTraceString(failure));
            finish(Activity.RESULT_CANCELED, result);
        }
    }



    private void verificarCompras() {
        Bundle result = new Bundle();
        try {
            prepararV1();
            LocalCashDbHelper helper = new LocalCashDbHelper(getTargetContext());
            assertTrue(helper.getWritableDatabase().getVersion() == 4, "Migración a versión 4");
            MovimientoService contabilidad = new MovimientoService(helper);
            assertTrue(contabilidad.listarCuentas().size() == 2, "Dos cuentas preservadas");
            assertTrue(contabilidad.calcularSaldo(1) == 12550, "Saldo v1 preservado");
            assertTrue(contabilidad.calcularSaldo(2) == 30000, "Segunda cuenta preservada");
            assertTrue(contabilidad.listarMovimientos(1).size() == 2, "Historial v1 preservado");
            CompraService compras = new CompraService(helper);
            assertTrue(compras.listarCompras().isEmpty() && compras.listarContactos().isEmpty(), "Tablas nuevas vacías");
            probarRollback(helper);
            helper.close();
            log.append("PASS MIG-01: v1 real con dos cuentas y dos movimientos → v4, saldos 125,50 y 300,00 conservados.\n");
            AccessibilityServiceInfo info = getUiAutomation().getServiceInfo();
            info.flags |= AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
            getUiAutomation().setServiceInfo(info);
            abrir(); nodeText("Saldo: 125,50");
            clickId("abrir_contactos"); clickId("crear_contacto");
            setText("nombre_contacto", " "); clickText("Guardar"); nodeId("nombre_contacto");
            setText("nombre_contacto", "Ana"); clickText("Guardar"); nodeText("Ana");
            clickId("crear_contacto"); setText("nombre_contacto", "Luis"); clickText("Guardar"); nodeText("Luis");
            clickId("volver"); clickId("abrir_compras"); clickId("crear_compra");
            setText("nombre_compra", "Compra realizada");
            clickId("estado_compra"); clickText("REALIZADA");
            clickId("contacto_compra"); clickText("Ana (#1)");
            setText("item_descripcion", "Cuaderno"); setText("item_cantidad", "0"); setText("item_precio", "100,25");
            clickId("agregar_item"); nodeText("El ítem necesita descripción, cantidad entera positiva y precio no negativo.");
            setText("item_cantidad", "-1"); clickId("agregar_item");
            nodeText("El ítem necesita descripción, cantidad entera positiva y precio no negativo.");
            setText("item_cantidad", "1,5"); clickId("agregar_item");
            nodeText("La cantidad debe ser un entero positivo dentro del rango permitido.");
            setText("item_cantidad", "2"); clickId("agregar_item");
            setText("item_descripcion", "Servicio"); setText("item_cantidad", "3"); setText("item_precio", "10,50"); clickId("agregar_item");
            nodeText("Total: 232,00"); clickId("guardar_compra");
            nodeText("Total: 232,00"); nodeText("Contacto: Ana"); assertTrue(nodeId("compra_info").getText().toString().startsWith("REALIZADA"), "Estado realizada visible");
            clickId("volver"); nodeText("Compra realizada"); clickId("crear_compra");
            setText("nombre_compra", "Compra planificada");
            clickId("guardar_compra"); nodeText("La compra necesita nombre, estado, fecha y al menos un ítem válido.");
            setText("item_descripcion", "Producto futuro"); setText("item_cantidad", "1"); setText("item_precio", "50"); clickId("agregar_item");
            clickId("fecha_compra");
            int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
            clickText(Integer.toString(year));
            clickText(Integer.toString(year + 1));
            clickText("OK"); clickId("guardar_compra");
            assertTrue(nodeId("compra_info").getText().toString().startsWith("PLANIFICADA"), "Estado planificada visible"); nodeText("Contacto: Sin contacto"); nodeText("Total: 50,00");
            clickId("volver"); nodeText("Compra realizada"); nodeText("Compra planificada");
            clickText("Compra realizada"); nodeText("Total: 232,00"); nodeText("Contacto: Ana");
            log.append("PASS CON/BUY-01/02/03: dos contactos por UI, compra realizada con dos ítems y total 232,00 vinculada a Ana; planificada con fecha futura sin contacto; historial y detalles.\n");
            helper = new LocalCashDbHelper(getTargetContext()); compras = new CompraService(helper);
            assertTrue(compras.listarContactos().size() == 2 && compras.listarCompras().size() == 2, "Persistencia nueva");
            Compra plan = null, realizada = null;
            for (Compra c : compras.listarCompras()) { if (c.getEstado() == Compra.Estado.PLANIFICADA) plan = c; else realizada = c; }
            assertTrue(plan != null && plan.getFecha() > System.currentTimeMillis() && plan.getContactoId() == null, "Fecha futura y contacto opcional");
            assertTrue(realizada != null && realizada.getDetalles().size() == 2 && realizada.calcularTotal() == 23200 && realizada.getContactoId() == 1, "Compra reconstruida");
            assertTrue(new MovimientoService(helper).calcularSaldo(1) == 12550 && new MovimientoService(helper).listarMovimientos(1).size() == 2, "Compras no crean movimientos ni alteran saldo");
            try {
                compras.registrar(new Compra(0, "Inválida", Compra.Estado.REALIZADA, 0, 9999L, realizada.getDetalles()));
                throw new AssertionError("Contacto inexistente aceptado");
            } catch (IllegalArgumentException expected) { assertTrue(compras.listarCompras().size() == 2, "Compra inválida no persistida"); }
            helper.close();
            helper = new LocalCashDbHelper(getTargetContext());
            assertTrue(new CompraService(helper).listarCompras().size() == 2 && new MovimientoService(helper).calcularSaldo(1) == 12550, "Reapertura v4");
            helper.close();
            log.append("PASS regresión contable, persistencia v4, validaciones y ausencia de movimientos automáticos.\n");
            result.putString("stream", "LOCALCASH_PURCHASE_PASS\n" + log); finish(Activity.RESULT_OK, result);
        } catch (Throwable failure) {
            dump(getUiAutomation().getRootInActiveWindow(), 0);
            result.putString("stream", "LOCALCASH_PURCHASE_FAIL\n" + log + android.util.Log.getStackTraceString(failure));
            finish(Activity.RESULT_CANCELED, result);
        }
    }

    private void verificarCompartidas() {
        Bundle result = new Bundle();
        try {
            // El flujo T04 ha creado cuentas/movimientos/contactos/compras reales.
            // Retroceder solo el esquema de deudas en el emulador desechable: fixture v2.
            LocalCashDbHelper helper = new LocalCashDbHelper(getTargetContext());
            CompraService compras = new CompraService(helper);
            assertTrue(compras.listarCompras().size() == 2, "Fixture T04 existe");
            compras.crearContacto("Marta");
            long equitativa = compras.registrar(new Compra(0,"Compartida exacta",Compra.Estado.REALIZADA,3,1L,
                    java.util.Arrays.asList(new DetalleCompra("Producto",1,10001))));
            long custom = compras.registrar(new Compra(0,"Compartida personalizada",Compra.Estado.REALIZADA,4,null,
                    java.util.Arrays.asList(new DetalleCompra("Servicio",1,10000))));
            long prueba = compras.registrar(new Compra(0,"Prueba transacción",Compra.Estado.REALIZADA,5,null,
                    java.util.Arrays.asList(new DetalleCompra("Producto",1,10000))));
            android.database.sqlite.SQLiteDatabase db = helper.getWritableDatabase();
            // Fixture v2 genuino: retirar únicamente columnas de v4 en BD desechable.
            db.execSQL("ALTER TABLE movimientos RENAME TO movimientos_fixture_v4");
            db.execSQL("CREATE TABLE movimientos (id INTEGER PRIMARY KEY, cuenta_id INTEGER NOT NULL REFERENCES cuentas(id) ON DELETE RESTRICT, tipo TEXT NOT NULL CHECK(tipo IN ('INGRESO','EGRESO')), monto INTEGER NOT NULL CHECK(monto>0), descripcion TEXT NOT NULL DEFAULT '', fecha INTEGER NOT NULL CHECK(fecha>=0))");
            db.execSQL("INSERT INTO movimientos SELECT id,cuenta_id,tipo,monto,descripcion,fecha FROM movimientos_fixture_v4");
            db.execSQL("DROP TABLE movimientos_fixture_v4");
            db.execSQL("CREATE INDEX movimientos_cuenta_fecha ON movimientos(cuenta_id,fecha,id)");
            db.execSQL("DROP TABLE deudas"); db.setVersion(2);
            String previo = snapshot(db);
            assertTrue(db.getVersion() == 2, "Fixture v2 confirmado"); helper.close();
            helper = new LocalCashDbHelper(getTargetContext()); db = helper.getWritableDatabase();
            assertTrue(db.getVersion() == 4, "Migración v2→v4 real");
            assertTrue(previo.equals(snapshot(db)), "Todas las filas v2 intactas");
            assertTrue(new MovimientoService(helper).calcularSaldo(1) == 12550, "Saldo conservado");
            log.append("PASS MIG-02: fixture SQLite v2→v4; snapshot exacto de cuentas, movimientos, contactos, compras y detalles intacto.\n");
            com.aik1a.localcash.application.DeudaService service = new com.aik1a.localcash.application.DeudaService(helper);
            final com.aik1a.localcash.application.DeudaService svc = service;
            final long plan = comprasIdPlan(helper);
            rechazar(() -> svc.repartirEquitativo(plan, java.util.Arrays.asList(1L,2L)));
            rechazar(() -> svc.repartirEquitativo(prueba, java.util.Arrays.asList(1L,1L)));
            rechazar(() -> svc.repartirPersonalizado(prueba, java.util.Arrays.asList(1L,2L),java.util.Arrays.asList(-1L,10001L)));
            rechazar(() -> svc.repartirPersonalizado(prueba, java.util.Arrays.asList(1L,2L),java.util.Arrays.asList(20L,30L)));
            rechazar(() -> svc.repartirPersonalizado(prueba, java.util.Arrays.asList(1L,2L),java.util.Arrays.asList(Long.MAX_VALUE,1L)));
            rechazar(() -> svc.repartirEquitativo(prueba, java.util.Arrays.asList(1L,9999L)));
            assertTrue(service.listarDeudas(prueba).isEmpty(), "Entradas inválidas sin escritura");
            com.aik1a.localcash.data.DeudaRepository repo = new com.aik1a.localcash.data.DeudaRepository(helper);
            Compra compraPrueba = new CompraService(helper).buscarCompra(prueba);
            try {
                repo.registrar(Reparto.equitativo(compraPrueba,java.util.Arrays.asList(1L,9999L)));
                throw new AssertionError("FK desactivada");
            } catch (android.database.sqlite.SQLiteConstraintException expected) {
                assertTrue(repo.listarPorCompra(prueba).isEmpty(), "FK revierte primera deuda");
            }
            db.execSQL("CREATE TRIGGER test_falla_deuda BEFORE INSERT ON deudas WHEN NEW.contacto_id=2 BEGIN SELECT RAISE(ABORT,'fallo inducido'); END");
            try {
                service.repartirEquitativo(prueba,java.util.Arrays.asList(1L,2L));
                throw new AssertionError("Trigger no falló");
            } catch (android.database.sqlite.SQLiteException expected) {
                assertTrue(repo.listarPorCompra(prueba).isEmpty(), "Segundo insert fallido revierte todo");
            } finally { db.execSQL("DROP TRIGGER test_falla_deuda"); }
            Compra falsa = new Compra(prueba,"Falsa",Compra.Estado.REALIZADA,0,null,
                    java.util.Arrays.asList(new DetalleCompra("X",1,1)));
            rechazar(() -> repo.registrar(Reparto.equitativo(falsa,java.util.Arrays.asList(1L,2L))));
            assertTrue(repo.listarPorCompra(prueba).isEmpty(), "Total ficticio rechazado");
            service.repartirPersonalizado(prueba,java.util.Arrays.asList(1L,2L),java.util.Arrays.asList(0L,10000L));
            assertTrue(repo.listarPorCompra(prueba).get(0).getMonto() == 0, "Cuota cero persistida");
            rechazar(() -> svc.repartirEquitativo(prueba,java.util.Arrays.asList(2L,3L)));
            assertTrue(repo.listarPorCompra(prueba).size() == 2, "Reparto repetido con otros participantes rechazado");
            try {
                db.execSQL("INSERT INTO deudas(compra_id,contacto_id,monto,estado) VALUES("+prueba+",1,1,'PENDIENTE')");
                throw new AssertionError("PK duplicada permitida");
            } catch (android.database.sqlite.SQLiteConstraintException expected) { }
            try {
                db.execSQL("INSERT INTO deudas(compra_id,contacto_id,monto,estado) VALUES("+equitativa+",1,-1,'PENDIENTE')");
                throw new AssertionError("Monto negativo SQL aceptado");
            } catch (android.database.sqlite.SQLiteConstraintException expected) { }
            try {
                db.execSQL("INSERT INTO deudas(compra_id,contacto_id,monto,estado) VALUES(99999,1,1,'PENDIENTE')");
                throw new AssertionError("Compra FK inválida permitida");
            } catch (android.database.sqlite.SQLiteConstraintException expected) { }
            log.append("PASS SHARED-DB: planificada/duplicados/negativo/desigual/overflow/contacto inválido rechazados; FK, PK, CHECK y rollback de segundo insert; total real revalidado, cuota cero persistida.\n");
            helper.close();
            AccessibilityServiceInfo info = getUiAutomation().getServiceInfo();
            info.flags |= AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
            getUiAutomation().setServiceInfo(info);
            abrir(); clickId("abrir_compras"); clickText("Compartida exacta");
            nodeText("Total: 100,01"); clickId("abrir_reparto");
            assertTrue(!nodeText("Ana (#1)").isChecked(), "Contacto asociado no automático");
            clickId("guardar_reparto"); nodeText("Selecciona al menos dos contactos distintos.");
            clickText("Luis (#2)"); clickId("guardar_reparto"); nodeText("Selecciona al menos dos contactos distintos.");
            clickText("Marta (#3)"); clickText("Ana (#1)"); clickId("guardar_reparto");
            nodeText("Ana (#1): 33,34 · PENDIENTE"); nodeText("Luis (#2): 33,34 · PENDIENTE"); nodeText("Marta (#3): 33,33 · PENDIENTE");
            clickId("volver"); clickId("volver"); clickText("Compartida personalizada"); clickId("abrir_reparto");
            clickId("personalizado"); clickText("Ana (#1)"); clickText("Luis (#2)"); clickText("Marta (#3)");
            setCuota("Ana (#1)","-1"); setCuota("Luis (#2)","30"); setCuota("Marta (#3)","50"); clickId("guardar_reparto");
            nodeText("Cada contacto debe tener un monto no negativo.");
            setCuota("Ana (#1)","19"); clickId("guardar_reparto"); nodeText("La suma debe coincidir exactamente con el total.");
            setCuota("Ana (#1)","20,001"); clickId("guardar_reparto"); nodeText("Usa un número con hasta 2 decimales, sin separador de miles.");
            setCuota("Ana (#1)","20"); clickId("guardar_reparto");
            nodeText("Ana (#1): 20,00 · PENDIENTE"); nodeText("Luis (#2): 30,00 · PENDIENTE"); nodeText("Marta (#3): 50,00 · PENDIENTE");
            clickId("volver"); clickId("abrir_reparto"); nodeText("Ana (#1): 20,00 · PENDIENTE");
            helper = new LocalCashDbHelper(getTargetContext());
            service = new com.aik1a.localcash.application.DeudaService(helper);
            List<Deuda> partes = service.listarDeudas(equitativa);
            assertTrue(partes.size()==3 && partes.get(0).getMonto()==3334 && partes.get(1).getMonto()==3334 && partes.get(2).getMonto()==3333,"SQLite reparto exacto");
            assertTrue(service.listarDeudas(custom).size()==3 && service.listarDeudas(custom).get(0).getMonto()==2000,"SQLite personalizado");
            assertTrue(previo.equals(snapshot(helper.getReadableDatabase())),"Regresión de todas las tablas anteriores");
            try (android.database.Cursor c = helper.getReadableDatabase().rawQuery("PRAGMA foreign_key_check",null)) {
                assertTrue(!c.moveToFirst(), "Sin FK huérfanas");
            }
            helper.close();
            log.append("PASS SHARED-UI: selección explícita, validación de 2+, reparto 100,01/3 y personalizado 20+30+50; errores visibles; lectura al volver, persistencia/reapertura y regresión contable.\n");
            result.putString("stream","LOCALCASH_SHARED_PASS\n"+log); finish(Activity.RESULT_OK,result);
        } catch (Throwable failure) {
            dump(getUiAutomation().getRootInActiveWindow(),0);
            result.putString("stream","LOCALCASH_SHARED_FAIL\n"+log+android.util.Log.getStackTraceString(failure));
            finish(Activity.RESULT_CANCELED,result);
        }
    }
    private long comprasIdPlan(LocalCashDbHelper helper) {
        for (Compra compra : new CompraService(helper).listarCompras())
            if (compra.getEstado()==Compra.Estado.PLANIFICADA) return compra.getId();
        throw new AssertionError("No hay compra planificada");
    }
    private void rechazar(Runnable action) {
        try { action.run(); throw new AssertionError("Entrada inválida aceptada"); }
        catch (IllegalArgumentException | ArithmeticException expected) { }
    }
    private String snapshot(android.database.sqlite.SQLiteDatabase db) {
        StringBuilder rows = new StringBuilder();
        for (String tabla : new String[]{"cuentas","movimientos","contactos","compras","detalle_compra"}) {
            rows.append(tabla);
            try (android.database.Cursor c = db.rawQuery("SELECT "+(tabla.equals("movimientos") ? "id,cuenta_id,tipo,monto,descripcion,fecha" : "*")+" FROM "+tabla+" ORDER BY id",null)) {
                while (c.moveToNext()) for (int col=0;col<c.getColumnCount();col++) {
                    String value=c.isNull(col)?"<NULL>":c.getString(col);
                    rows.append(value.length()).append(':').append(value).append(';');
                }
            }
        }
        return rows.toString();
    }
    private void setCuota(String contacto, String texto) {
        Bundle args = new Bundle(); args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,texto);
        AccessibilityNodeInfo node=nodeText("Monto para "+contacto);
        node.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SHOW_ON_SCREEN.getId());
        assertTrue(nodeText("Monto para "+contacto).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT,args),"Monto "+contacto);
        esperarUI();
    }

    private void verificarUbicaciones() {
        Bundle result = new Bundle();
        android.location.LocationManager lm = null;
        boolean testProvider = false;
        try {
            for (int version=1;version<=3;version++) {
                prepararFixtureUbicacion(version);
                android.database.sqlite.SQLiteDatabase old = getTargetContext().openOrCreateDatabase("localcash.db",0,null);
                String antes=snapshotUbicacion(old,version); assertTrue(old.getVersion()==version,"Versión fixture"); old.close();
                try (LocalCashDbHelper helper=new LocalCashDbHelper(getTargetContext())) {
                    android.database.sqlite.SQLiteDatabase db=helper.getWritableDatabase();
                    assertTrue(db.getVersion()==4,"Migración a v4");
                    assertTrue(antes.equals(snapshotUbicacion(db,version)),"Todas las filas previas preservadas");
                    for (Movimiento m:new MovimientoService(helper).listarMovimientos(1)) assertTrue(!m.tieneUbicacion(),"Previos NULL");
                    assertTrue(new MovimientoService(helper).calcularSaldo(1)==12550,"Saldo anterior intacto");
                    try (android.database.Cursor c=db.rawQuery("PRAGMA foreign_key_check",null)) { assertTrue(!c.moveToFirst(),"FK intactas"); }
                    if (version==3) {
                        List<Deuda> deudas=new com.aik1a.localcash.application.DeudaService(helper).listarDeudas(1);
                        assertTrue(deudas.size()==3 && deudas.get(0).getMonto()==3334 && deudas.get(2).getMonto()==3333,"Deudas previas intactas");
                        probarParUbicacion(db);
                    }
                }
                log.append("PASS MIG-").append(version).append(": v").append(version).append("→v4, snapshot exacto, NULL antiguos, saldo y FK.\n");
            }
            AccessibilityServiceInfo info=getUiAutomation().getServiceInfo(); info.flags|=AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
            getUiAutomation().setServiceInfo(info);
            assertTrue(getTargetContext().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)!=android.content.pm.PackageManager.PERMISSION_GRANTED,"Sin permiso inicial");
            abrir(); clickText("Efectivo");
            nuevoMovimiento("Sin consentimiento");
            assertTrue(!nodeId("incluir_ubicacion").isChecked(),"Opt-in desmarcado"); clickText("Guardar");
            nodeText("Saldo: 126,50");
            assertTrue(getTargetContext().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)!=android.content.pm.PackageManager.PERMISSION_GRANTED,"No solicitó permiso por registrar");
            nuevoMovimiento("Permiso denegado"); clickId("incluir_ubicacion");
            clickSystemId("com.android.permissioncontroller:id/permission_deny_button");
            nodeText("Permiso denegado. Puedes guardar sin ubicación."); clickText("Guardar"); nodeText("Saldo: 127,50");
            log.append("PASS GEO-PERM: sin solicitud automática; diálogo Android real denegado, movimiento guardado con NULL.\n");
            shell("pm grant com.aik1a.localcash android.permission.ACCESS_COARSE_LOCATION");
            shell("pm grant com.aik1a.localcash android.permission.ACCESS_FINE_LOCATION");
            shell("cmd location set-location-enabled true");
            nuevoMovimiento("GPS simulado"); clickId("incluir_ubicacion");
            double[] capturada=esperarCaptura();
            log.append("GPS_EXPECTED: ").append(capturada[0]).append(", ").append(capturada[1]).append('\n');
            clickText("Guardar"); nodeText("Saldo: 128,50");
            log.append("PASS GEO-GPS: permiso concedido, callback LocationManager productivo, GPS inyectado por adb emu geo fix.\n");
            nuevoMovimiento("Consentimiento retirado"); clickId("incluir_ubicacion");
            esperarCaptura(); clickId("incluir_ubicacion");
            nodeText("Se guardará sin ubicación."); clickText("Guardar"); nodeText("Saldo: 129,50");
            shell("cmd location set-location-enabled false");
            nuevoMovimiento("GPS desactivado"); clickId("incluir_ubicacion");
            nodeText("Ubicación desactivada o proveedor no disponible. Puedes guardar sin ubicación.");
            clickText("Guardar"); nodeText("Saldo: 130,50");
            shell("cmd location set-location-enabled true");
            // Proveedor Android de pruebas sin fix; usa el listener y timeout productivos.
            shell("appops set com.aik1a.localcash android:mock_location allow");
            lm=(android.location.LocationManager)getTargetContext().getSystemService(android.content.Context.LOCATION_SERVICE);
            lm.addTestProvider(android.location.LocationManager.GPS_PROVIDER,false,false,false,false,true,true,true,
                    android.location.Criteria.POWER_LOW,android.location.Criteria.ACCURACY_FINE);
            testProvider=true; lm.setTestProviderEnabled(android.location.LocationManager.GPS_PROVIDER,true);
            nuevoMovimiento("Sin fix"); clickId("incluir_ubicacion");
            nodeText("Buscando ubicación (máximo 15 segundos). Puedes guardar sin ubicación.");
            SystemClock.sleep(15500);
            nodeText("No se obtuvo ubicación en 15 segundos. Puedes guardar sin ubicación.");
            clickText("Guardar"); nodeText("Saldo: 131,50");
            lm.removeTestProvider(android.location.LocationManager.GPS_PROVIDER); testProvider=false;
            log.append("PASS GEO-FALLBACK: consentimiento retirado, GPS desactivado y timeout real sin fix permiten guardar sin ubicación.\n");
            try (LocalCashDbHelper helper=new LocalCashDbHelper(getTargetContext())) {
                List<Movimiento> movimientos=new MovimientoService(helper).listarMovimientos(1);
                assertTrue(movimientos.size()==8,"Dos previos y seis registros UI");
                int geolocalizados=0;
                for (Movimiento m:movimientos) {
                    if (m.tieneUbicacion()) {
                        geolocalizados++; assertTrue(m.getDescripcion().equals("GPS simulado"),"Consentimiento explícito único");
                        assertTrue(m.getUbicacion().getLatitud()==capturada[0] && m.getUbicacion().getLongitud()==capturada[1],"Coordenadas exactas persistidas");
                    }
                }
                assertTrue(geolocalizados==1,"Todos los otros registros tienen ambos NULL");
                assertTrue(new MovimientoService(helper).calcularSaldo(1)==13150,"Saldo exacto");
                assertTrue(new com.aik1a.localcash.application.DeudaService(helper).listarDeudas(1).size()==3,"Deudas no alteradas");
                try (android.database.Cursor c=helper.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM movimientos WHERE latitud IS NULL AND longitud IS NULL",null)) {
                    c.moveToFirst(); assertTrue(c.getInt(0)==7,"NULL en ambas columnas");
                }
            }
            log.append("PASS GEO-DB: reapertura, un par exacto y siete pares NULL; saldo y deudas preservados.\n");
            result.putString("stream","LOCALCASH_LOCATION_PASS\n"+log); finish(Activity.RESULT_OK,result);
        } catch (Throwable failure) {
            dump(getUiAutomation().getRootInActiveWindow(),0);
            result.putString("stream","LOCALCASH_LOCATION_FAIL\n"+log+android.util.Log.getStackTraceString(failure));
            finish(Activity.RESULT_CANCELED,result);
        } finally {
            if (testProvider && lm!=null) lm.removeTestProvider(android.location.LocationManager.GPS_PROVIDER);
        }
    }
    private double[] esperarCaptura() {
        long deadline=SystemClock.uptimeMillis()+17000;
        do {
            String estado=nodeId("estado_ubicacion").getText().toString();
            if (estado.startsWith("Ubicación capturada: ")) {
                String[] par=estado.substring("Ubicación capturada: ".length()).split(", ");
                double lat=Double.parseDouble(par[0]),lon=Double.parseDouble(par[1]);
                assertTrue(Math.abs(lat+33.45)<0.00001 && Math.abs(lon+70.66)<0.00001,"Fix simulado dentro de 0,00001 grados");
                return new double[]{lat,lon};
            }
            SystemClock.sleep(100);
        } while(SystemClock.uptimeMillis()<deadline);
        throw new AssertionError("GPS no capturado: "+nodeId("estado_ubicacion").getText());
    }
    private void nuevoMovimiento(String descripcion) {
        clickText("Registrar ingreso o egreso"); clickText("Ingreso"); setText("monto","1"); setText("descripcion",descripcion);
    }
    private void shell(String command) throws java.io.IOException {
        try (android.os.ParcelFileDescriptor fd=getUiAutomation().executeShellCommand(command);
             java.io.InputStream stream=new android.os.ParcelFileDescriptor.AutoCloseInputStream(fd)) {
            byte[] buf=new byte[1024]; while (stream.read(buf)!=-1) { }
        }
    }
    private void clickSystemId(String id) {
        long deadline=SystemClock.uptimeMillis()+8000;
        do {
            AccessibilityNodeInfo root=getUiAutomation().getRootInActiveWindow();
            if (root!=null) for (AccessibilityNodeInfo n:root.findAccessibilityNodeInfosByViewId(id)) {
                assertTrue(n.performAction(AccessibilityNodeInfo.ACTION_CLICK),"Permiso Android"); esperarUI(); return;
            }
            SystemClock.sleep(100);
        } while (SystemClock.uptimeMillis()<deadline);
        throw new AssertionError("No se encontró permiso Android "+id);
    }
    private String snapshotUbicacion(android.database.sqlite.SQLiteDatabase db,int version) {
        StringBuilder rows=new StringBuilder();
        java.util.List<String> tablas=new java.util.ArrayList<>(java.util.Arrays.asList("cuentas","movimientos"));
        if (version>=2) tablas.addAll(java.util.Arrays.asList("contactos","compras","detalle_compra"));
        if (version>=3) tablas.add("deudas");
        for (String tabla:tablas) {
            rows.append(tabla);
            String cols=tabla.equals("movimientos")?"id,cuenta_id,tipo,monto,descripcion,fecha":"*";
            try (android.database.Cursor c=db.rawQuery("SELECT "+cols+" FROM "+tabla+" ORDER BY "+(tabla.equals("deudas")?"compra_id,contacto_id":"id"),null)) {
                while (c.moveToNext()) for (int col=0;col<c.getColumnCount();col++) {
                    String value=c.isNull(col)?"<NULL>":c.getString(col); rows.append(value.length()).append(':').append(value).append(';');
                }
            }
        }
        return rows.toString();
    }
    private void prepararFixtureUbicacion(final int version) {
        getTargetContext().deleteDatabase("localcash.db");
        android.database.sqlite.SQLiteOpenHelper fixture=new android.database.sqlite.SQLiteOpenHelper(getTargetContext(),"localcash.db",null,version) {
            @Override public void onCreate(android.database.sqlite.SQLiteDatabase db) {
                db.execSQL("CREATE TABLE cuentas(id INTEGER PRIMARY KEY,nombre TEXT NOT NULL CHECK(length(trim(nombre))>0),saldo_inicial INTEGER NOT NULL)");
                db.execSQL("CREATE TABLE movimientos(id INTEGER PRIMARY KEY,cuenta_id INTEGER NOT NULL REFERENCES cuentas(id) ON DELETE RESTRICT,tipo TEXT NOT NULL CHECK(tipo IN ('INGRESO','EGRESO')),monto INTEGER NOT NULL CHECK(monto>0),descripcion TEXT NOT NULL DEFAULT '',fecha INTEGER NOT NULL CHECK(fecha>=0))");
                db.execSQL("CREATE INDEX movimientos_cuenta_fecha ON movimientos(cuenta_id,fecha,id)");
                db.execSQL("INSERT INTO cuentas VALUES(1,'Efectivo',10000),(2,'Banco',30000)");
                db.execSQL("INSERT INTO movimientos VALUES(1,1,'INGRESO',5000,'Sueldo',1),(2,1,'EGRESO',2450,'Transporte',2)");
                if (version>=2) {
                    db.execSQL("CREATE TABLE contactos(id INTEGER PRIMARY KEY,nombre TEXT NOT NULL CHECK(length(trim(nombre))>0))");
                    db.execSQL("CREATE TABLE compras(id INTEGER PRIMARY KEY,nombre TEXT NOT NULL CHECK(length(trim(nombre))>0),estado TEXT NOT NULL CHECK(estado IN ('PLANIFICADA','REALIZADA')),fecha INTEGER NOT NULL CHECK(fecha>=0),contacto_id INTEGER REFERENCES contactos(id) ON DELETE RESTRICT)");
                    db.execSQL("CREATE TABLE detalle_compra(id INTEGER PRIMARY KEY,compra_id INTEGER NOT NULL REFERENCES compras(id) ON DELETE RESTRICT,descripcion TEXT NOT NULL CHECK(length(trim(descripcion))>0),cantidad INTEGER NOT NULL CHECK(cantidad>0),precio_unitario INTEGER NOT NULL CHECK(precio_unitario>=0))");
                    db.execSQL("CREATE INDEX detalle_por_compra ON detalle_compra(compra_id,id)");
                    db.execSQL("INSERT INTO contactos VALUES(1,'Ana'),(2,'Luis'),(3,'Marta')");
                    db.execSQL("INSERT INTO compras VALUES(1,'Compra previa','REALIZADA',0,1),(2,'Plan previa','PLANIFICADA',0,NULL)");
                    db.execSQL("INSERT INTO detalle_compra VALUES(1,1,'Producto',1,10001),(2,2,'Servicio',1,5000)");
                }
                if (version>=3) {
                    db.execSQL("CREATE TABLE deudas(compra_id INTEGER NOT NULL REFERENCES compras(id) ON DELETE RESTRICT,contacto_id INTEGER NOT NULL REFERENCES contactos(id) ON DELETE RESTRICT,monto INTEGER NOT NULL CHECK(typeof(monto)='integer' AND monto>=0),estado TEXT NOT NULL DEFAULT 'PENDIENTE' CHECK(estado='PENDIENTE'),PRIMARY KEY(compra_id,contacto_id))");
                    db.execSQL("INSERT INTO deudas VALUES(1,1,3334,'PENDIENTE'),(1,2,3334,'PENDIENTE'),(1,3,3333,'PENDIENTE')");
                }
            }
            @Override public void onConfigure(android.database.sqlite.SQLiteDatabase db) { db.setForeignKeyConstraintsEnabled(true); }
            @Override public void onUpgrade(android.database.sqlite.SQLiteDatabase db,int f,int t) { throw new AssertionError("Fixture inmutable"); }
        };
        fixture.getWritableDatabase(); fixture.close();
    }
    private void probarParUbicacion(android.database.sqlite.SQLiteDatabase db) {
        for (String coords:new String[]{"1,NULL","NULL,1","91,1","1,181","-91,1","1,-181","'texto',1"}) {
            try {
                db.execSQL("INSERT INTO movimientos(cuenta_id,tipo,monto,descripcion,fecha,latitud,longitud) VALUES(1,'INGRESO',1,'Inválido',0,"+coords+")");
                throw new AssertionError("CHECK no rechazó "+coords);
            } catch (android.database.sqlite.SQLiteConstraintException expected) { }
        }
        android.content.ContentValues value=new android.content.ContentValues();
        value.put("latitud",Double.POSITIVE_INFINITY); value.put("longitud",1);
        try { db.update("movimientos",value,"id=1",null); throw new AssertionError("Infinito SQL aceptado"); }
        catch (android.database.sqlite.SQLiteConstraintException expected) { }
        try { db.execSQL("UPDATE movimientos SET latitud=1 WHERE id=1"); throw new AssertionError("UPDATE par incompleto aceptado"); }
        catch (android.database.sqlite.SQLiteConstraintException expected) { }
        log.append("PASS GEO-SQL: INSERT/UPDATE de par incompleto, rango, texto e infinito rechazados sin alterar registros.\n");
    }

    private void prepararV1() {
        getTargetContext().deleteDatabase("localcash.db");
        android.database.sqlite.SQLiteOpenHelper v1 = new android.database.sqlite.SQLiteOpenHelper(getTargetContext(), "localcash.db", null, 1) {
            @Override public void onConfigure(android.database.sqlite.SQLiteDatabase db) { db.setForeignKeyConstraintsEnabled(true); }
            @Override public void onCreate(android.database.sqlite.SQLiteDatabase db) {
                db.execSQL("CREATE TABLE cuentas (id INTEGER PRIMARY KEY, nombre TEXT NOT NULL CHECK(length(trim(nombre)) > 0), saldo_inicial INTEGER NOT NULL)");
                db.execSQL("CREATE TABLE movimientos (id INTEGER PRIMARY KEY, cuenta_id INTEGER NOT NULL REFERENCES cuentas(id) ON DELETE RESTRICT, tipo TEXT NOT NULL CHECK(tipo IN ('INGRESO','EGRESO')), monto INTEGER NOT NULL CHECK(monto > 0), descripcion TEXT NOT NULL DEFAULT '', fecha INTEGER NOT NULL CHECK(fecha >= 0))");
                db.execSQL("CREATE INDEX movimientos_cuenta_fecha ON movimientos(cuenta_id, fecha, id)");
            }
            @Override public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int from, int to) { throw new AssertionError("Fixture v1"); }
        };
        android.database.sqlite.SQLiteDatabase db = v1.getWritableDatabase();
        db.execSQL("INSERT INTO cuentas(id,nombre,saldo_inicial) VALUES(1,'Efectivo',10000),(2,'Banco',30000)");
        db.execSQL("INSERT INTO movimientos(id,cuenta_id,tipo,monto,descripcion,fecha) VALUES(1,1,'INGRESO',5000,'Sueldo',1),(2,1,'EGRESO',2450,'Transporte',2)");
        assertTrue(db.getVersion() == 1, "Fixture es SQLite v1"); v1.close();
    }
    private void probarRollback(LocalCashDbHelper helper) {
        android.database.sqlite.SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("CREATE TRIGGER test_falla_detalle BEFORE INSERT ON detalle_compra WHEN NEW.descripcion = 'Fallar' BEGIN SELECT RAISE(ABORT, 'fallo inducido'); END");
        try {
            try {
                new com.aik1a.localcash.data.CompraRepository(helper).crear(new Compra(0, "Rollback", Compra.Estado.REALIZADA, 0, null,
                        java.util.Arrays.asList(new DetalleCompra("Primero",1,100),new DetalleCompra("Fallar",1,200))));
                throw new AssertionError("Falló la prueba de rollback");
            } catch (android.database.sqlite.SQLiteException expected) {
                try (android.database.Cursor c = db.rawQuery("SELECT (SELECT COUNT(*) FROM compras), (SELECT COUNT(*) FROM detalle_compra)", null)) {
                    c.moveToFirst(); assertTrue(c.getLong(0) == 0 && c.getLong(1) == 0, "Rollback sin cabecera ni detalle huérfano");
                }
                log.append("PASS compra atómica: fallo del segundo detalle revierte cabecera y primer detalle.\n");
            }
        } finally { db.execSQL("DROP TRIGGER test_falla_detalle"); }
    }

    private void esperarUI() {
        try { getUiAutomation().waitForIdle(100, 3000); }
        catch (java.util.concurrent.TimeoutException ignored) {
            // Cada búsqueda de nodo tiene su propio plazo; no bloquear esperando idle indefinidamente.
        }
    }
    private void abrir() {
        Intent intent = new Intent(getTargetContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        main = startActivitySync(intent);
        esperarUI();
        nodeId("crear_cuenta"); // Solo avanza tras desafío real, alimentado por sensor del emulador.
    }
    private void assertTrue(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
    private AccessibilityNodeInfo buscar(String value, boolean id) {
        long deadline = SystemClock.uptimeMillis() + 8000;
        do {
            AccessibilityNodeInfo root = getUiAutomation().getRootInActiveWindow();
            if (root != null) {
                List<AccessibilityNodeInfo> nodes = id ? root.findAccessibilityNodeInfosByViewId("com.aik1a.localcash:id/" + value)
                        : root.findAccessibilityNodeInfosByText(value);
                for (AccessibilityNodeInfo node : nodes) {
                    if (id || (value.equalsIgnoreCase(node.getText() == null ? "" : node.getText().toString()) || value.equalsIgnoreCase(node.getContentDescription() == null ? "" : node.getContentDescription().toString()))) return node;
                }
            }
            AccessibilityNodeInfo rootForScroll = getUiAutomation().getRootInActiveWindow();
            if (rootForScroll != null) {
                List<AccessibilityNodeInfo> scrolls = rootForScroll.findAccessibilityNodeInfosByViewId("com.aik1a.localcash:id/root");
                if (!scrolls.isEmpty()) scrolls.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            }
            SystemClock.sleep(100);
        } while (SystemClock.uptimeMillis() < deadline);
        throw new AssertionError("No se encontró " + value);
    }
    private AccessibilityNodeInfo nodeId(String id) { return buscar(id, true); }
    private AccessibilityNodeInfo nodeText(String text) { return buscar(text, false); }
    private void dump(AccessibilityNodeInfo node, int depth) {
        if (node == null || depth > 12) return;
        log.append("UI ").append(node.getViewIdResourceName()).append(" text=").append(node.getText()).append('\n');
        for (int i = 0; i < node.getChildCount(); i++) dump(node.getChild(i), depth + 1);
    }
    private void clickId(String id) {
        nodeId(id).performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SHOW_ON_SCREEN.getId());
        assertTrue(nodeId(id).performAction(AccessibilityNodeInfo.ACTION_CLICK), "Click " + id);
        esperarUI();
    }
    private void clickText(String text) {
        AccessibilityNodeInfo node = nodeText(text);
        while (!node.isClickable() && node.getParent() != null) node = node.getParent();
        assertTrue(node.performAction(AccessibilityNodeInfo.ACTION_CLICK), "Click " + text);
        esperarUI();
    }
    private void setText(String id, String text) {
        Bundle args = new Bundle();
        args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
        nodeId(id).performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SHOW_ON_SCREEN.getId());
        assertTrue(nodeId(id).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args), "Texto " + id);
        esperarUI();
    }
}



