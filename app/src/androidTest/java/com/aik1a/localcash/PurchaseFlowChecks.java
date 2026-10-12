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
public final class PurchaseFlowChecks extends Instrumentation {
    private final StringBuilder log = new StringBuilder();
    private Activity main;

    @Override public void onCreate(Bundle arguments) { super.onCreate(arguments); start(); }

    @Override public void onStart() {
        Bundle result = new Bundle();
        try {
            prepararV1();
            LocalCashDbHelper helper = new LocalCashDbHelper(getTargetContext());
            assertTrue(helper.getWritableDatabase().getVersion() == 2, "Migración a versión 2");
            MovimientoService contabilidad = new MovimientoService(helper);
            assertTrue(contabilidad.listarCuentas().size() == 2, "Dos cuentas preservadas");
            assertTrue(contabilidad.calcularSaldo(1) == 12550, "Saldo v1 preservado");
            assertTrue(contabilidad.calcularSaldo(2) == 30000, "Segunda cuenta preservada");
            assertTrue(contabilidad.listarMovimientos(1).size() == 2, "Historial v1 preservado");
            CompraService compras = new CompraService(helper);
            assertTrue(compras.listarCompras().isEmpty() && compras.listarContactos().isEmpty(), "Tablas nuevas vacías");
            probarRollback(helper);
            helper.close();
            log.append("PASS MIG-01: v1 real con dos cuentas y dos movimientos → v2, saldos 125,50 y 300,00 conservados.\n");
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
            clickId("fecha_compra"); clickText("Next month");
            java.util.Calendar futuro = java.util.Calendar.getInstance(); futuro.add(java.util.Calendar.MONTH, 1); futuro.set(java.util.Calendar.DAY_OF_MONTH, 15);
            clickText(new java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale.US).format(futuro.getTime()));
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
            assertTrue(new CompraService(helper).listarCompras().size() == 2 && new MovimientoService(helper).calcularSaldo(1) == 12550, "Reapertura v2");
            helper.close();
            log.append("PASS regresión contable, persistencia v2, validaciones y ausencia de movimientos automáticos.\n");
            result.putString("stream", "LOCALCASH_PURCHASE_PASS\n" + log); finish(Activity.RESULT_OK, result);
        } catch (Throwable failure) {
            dump(getUiAutomation().getRootInActiveWindow(), 0);
            result.putString("stream", "LOCALCASH_PURCHASE_FAIL\n" + log + android.util.Log.getStackTraceString(failure));
            finish(Activity.RESULT_CANCELED, result);
        }
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
