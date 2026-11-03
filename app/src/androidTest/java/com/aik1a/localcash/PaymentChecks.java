package com.aik1a.localcash;

import android.app.Instrumentation;
import android.os.Bundle;
import com.aik1a.localcash.application.*;
import com.aik1a.localcash.data.*;
import com.aik1a.localcash.domain.*;
import java.util.Collections;

public final class PaymentChecks {
    private static void require(boolean value) { if (!value) throw new AssertionError(); }
    public static void ejecutar(Instrumentation runner) {
        Bundle result = new Bundle();
        try (LocalCashDbHelper helper = new LocalCashDbHelper(runner.getTargetContext())) {
            MovimientoService movimientos = new MovimientoService(helper);
            CompraService compras = new CompraService(helper);
            long cuenta = movimientos.crearCuenta("Pago prueba", 100000, Divisa.CLP);
            long otra = movimientos.crearCuenta("USD prueba", 100000, Divisa.USD);
            long id = compras.registrar(new Compra(0, "Pan prueba", Compra.Estado.PLANIFICADA,
                    1, null, Collections.singletonList(new DetalleCompra("Pan", 1, 30000)), Divisa.CLP));
            require(movimientos.calcularSaldo(cuenta) == 100000);
            try { compras.pagar(id, otra); throw new AssertionError("Moneda incorrecta aceptada"); }
            catch (IllegalArgumentException expected) { }
            require(compras.buscarCuentaPago(id) == null);
            require(compras.buscarCompra(id).getEstado() == Compra.Estado.PLANIFICADA);
            compras.pagar(id, cuenta);
            require(movimientos.calcularSaldo(cuenta) == 70000);
            require(compras.buscarCuentaPago(id) == cuenta);
            require(compras.buscarCompra(id).getEstado() == Compra.Estado.REALIZADA);
            try { compras.pagar(id, cuenta); throw new AssertionError("Pago duplicado aceptado"); }
            catch (IllegalArgumentException expected) { }
            require(movimientos.listarMovimientos(cuenta).size() == 1);
            require(compras.listarComprasDeCuenta(cuenta).size() == 1);
            result.putString("stream", "PASS: pago completo, transición, saldo, moneda y duplicados.");
            runner.finish(-1, result);
        } catch (Throwable error) {
            result.putString("stream", "FAIL: " + error); runner.finish(0, result);
        }
    }
}
