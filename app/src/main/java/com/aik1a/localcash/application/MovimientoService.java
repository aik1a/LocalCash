package com.aik1a.localcash.application;

import com.aik1a.localcash.data.CuentaRepository;
import com.aik1a.localcash.data.LocalCashDbHelper;
import com.aik1a.localcash.data.MovimientoRepository;
import com.aik1a.localcash.domain.Cuenta;
import com.aik1a.localcash.domain.Divisa;
import com.aik1a.localcash.domain.Movimiento;
import com.aik1a.localcash.domain.Ubicacion;
import java.util.List;
import android.database.sqlite.SQLiteDatabase;

/** Coordina cuentas y movimientos; el dominio calcula el saldo. */
public final class MovimientoService {
    private final LocalCashDbHelper helper;
    private final CuentaRepository cuentas;
    private final MovimientoRepository movimientos;

    public MovimientoService(LocalCashDbHelper helper) {
        this.helper = helper;
        cuentas = new CuentaRepository(helper);
        movimientos = new MovimientoRepository(helper);
    }

    public long crearCuenta(String nombre, long saldoInicial) {
        return crearCuenta(nombre, saldoInicial, new com.aik1a.localcash.data.ConfiguracionRepository(helper).exigirDivisa());
    }
    public long crearCuenta(String nombre, long saldoInicial, Divisa divisa) {
        if (divisa == null) throw new IllegalArgumentException("Selecciona una divisa.");
        return cuentas.crear(new Cuenta(0, nombre, saldoInicial, divisa));
    }
    public void editarCuenta(long id, String nombre) { cuentas.editar(id, nombre); }
    public void eliminarCuenta(long id) { cuentas.eliminar(id); }
    public void editarMovimiento(long id, Movimiento.Tipo tipo, long monto, String descripcion) {
        movimientos.editar(id, tipo, monto, descripcion);
    }
    public void eliminarMovimiento(long id) { movimientos.eliminar(id); }
    public boolean esPagoCompra(long id) { return movimientos.esPagoCompra(id); }
    public List<Cuenta> listarCuentas() { return cuentas.listar(); }
    public Cuenta buscarCuenta(long id) { return cuentas.buscar(id); }
    public List<Movimiento> listarMovimientos(long cuentaId) { return movimientos.listar(cuentaId); }
    public long calcularSaldo(long cuentaId) {
        return cuentas.buscar(cuentaId).calcularSaldo(movimientos.listar(cuentaId));
    }

    public long registrar(long cuentaId, Movimiento.Tipo tipo, long monto, String descripcion) {
        return registrar(cuentaId, tipo, monto, descripcion, null);
    }
    public long registrar(long cuentaId, Movimiento.Tipo tipo, long monto, String descripcion, Ubicacion ubicacion) {
        Movimiento nuevo = new Movimiento(0, cuentaId, tipo, monto, descripcion, System.currentTimeMillis(), ubicacion);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            Cuenta cuenta = cuentas.buscar(cuentaId);
            if (cuenta.getDivisa() == null) throw new IllegalArgumentException("Define la divisa de esta cuenta primero.");
            cuenta.getDivisa().validar(monto);
            List<Movimiento> historial = movimientos.listar(cuentaId);
            historial.add(nuevo);
            cuenta.calcularSaldo(historial); // Rechazar desbordamiento antes de persistir.
            long id = movimientos.crear(nuevo);
            db.setTransactionSuccessful();
            return id;
        } finally {
            db.endTransaction();
        }
    }
}


