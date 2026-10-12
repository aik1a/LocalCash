package com.aik1a.localcash;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityNodeInfo;
import com.aik1a.localcash.application.MovimientoService;
import com.aik1a.localcash.data.LocalCashDbHelper;
import com.aik1a.localcash.domain.*;
import com.aik1a.localcash.presentation.MainActivity;
import java.util.List;

/** Prueba de flujo en emulador desechable; sin framework ni dependencias adicionales. */
public final class FlowChecks extends Instrumentation {
    private final StringBuilder log = new StringBuilder();
    private Activity main;

    @Override public void onCreate(Bundle arguments) { super.onCreate(arguments); start(); }

    @Override public void onStart() {
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
                    if (id || value.equalsIgnoreCase(node.getText() == null ? "" : node.getText().toString())) return node;
                }
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
        assertTrue(nodeId(id).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args), "Texto " + id);
        esperarUI();
    }
}
