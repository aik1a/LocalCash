package com.aik1a.localcash.data;

import android.content.ContentValues;
import android.database.Cursor;
import com.aik1a.localcash.domain.Movimiento;
import com.aik1a.localcash.domain.Ubicacion;
import java.util.ArrayList;
import java.util.List;

public final class MovimientoRepository {
    private final LocalCashDbHelper helper;
    public MovimientoRepository(LocalCashDbHelper helper) { this.helper = helper; }

    public long crear(Movimiento movimiento) {
        android.database.sqlite.SQLiteDatabase db = helper.getWritableDatabase(); db.beginTransaction();
        try {
            validarSaldo(movimiento.getCuentaId(), null, movimiento);
            long id = insertar(movimiento);
            db.setTransactionSuccessful(); return id;
        } finally { db.endTransaction(); }
    }

    private long insertar(Movimiento movimiento) {
        ContentValues values = new ContentValues();
        values.put("cuenta_id", movimiento.getCuentaId());
        values.put("tipo", movimiento.getTipo().name());
        values.put("monto", movimiento.getMonto());
        values.put("descripcion", movimiento.getDescripcion());
        values.put("fecha", movimiento.getFecha());
        if (movimiento.tieneUbicacion()) {
            values.put("latitud", movimiento.getUbicacion().getLatitud());
            values.put("longitud", movimiento.getUbicacion().getLongitud());
        } else {
            values.putNull("latitud"); values.putNull("longitud");
        }
        return helper.getWritableDatabase().insertOrThrow("movimientos", null, values);
    }

    public Movimiento buscar(long id) {
        try (Cursor c = helper.getReadableDatabase().query("movimientos", new String[]{"cuenta_id"},
                "id=?", new String[]{Long.toString(id)}, null, null, null)) {
            if (!c.moveToFirst()) throw new IllegalArgumentException("El movimiento no existe.");
            for (Movimiento m : listar(c.getLong(0))) if (m.getId() == id) return m;
        }
        throw new IllegalArgumentException("El movimiento no existe.");
    }
    public boolean esPagoCompra(long id) {
        try (Cursor c = helper.getReadableDatabase().query("pago_compra", new String[]{"compra_id"},
                "movimiento_id=?", new String[]{Long.toString(id)}, null, null, null)) { return c.moveToFirst(); }
    }
    public void editar(long id, Movimiento.Tipo tipo, long monto, String descripcion) {
        android.database.sqlite.SQLiteDatabase db = helper.getWritableDatabase(); db.beginTransaction();
        try {
            if (esPagoCompra(id)) throw new IllegalArgumentException("Este movimiento pertenece a una compra. Modifica la compra.");
            Movimiento original = buscar(id);
            Movimiento nuevo = new Movimiento(id, original.getCuentaId(), tipo, monto, descripcion,
                    original.getFecha(), original.getUbicacion());
            validarSaldo(original.getCuentaId(), id, nuevo);
            ContentValues values = new ContentValues(); values.put("tipo", tipo.name()); values.put("monto", monto);
            values.put("descripcion", nuevo.getDescripcion());
            db.update("movimientos", values, "id=?", new String[]{Long.toString(id)});
            db.setTransactionSuccessful();
        } finally { db.endTransaction(); }
    }
    public void eliminar(long id) {
        android.database.sqlite.SQLiteDatabase db = helper.getWritableDatabase(); db.beginTransaction();
        try {
            if (esPagoCompra(id)) throw new IllegalArgumentException("Este movimiento pertenece a una compra. Elimina la compra para revertirlo.");
            Movimiento original = buscar(id);
            validarSaldo(original.getCuentaId(), id, null);
            db.delete("movimientos", "id=?", new String[]{Long.toString(id)});
            db.setTransactionSuccessful();
        } finally { db.endTransaction(); }
    }
    private void validarSaldo(long cuentaId, Long reemplazado, Movimiento nuevo) {
        com.aik1a.localcash.domain.Cuenta cuenta = new CuentaRepository(helper).buscar(cuentaId);
        if (nuevo != null) {
            if (cuenta.getDivisa() == null) throw new IllegalArgumentException("Define la divisa de esta cuenta primero.");
            cuenta.getDivisa().validar(nuevo.getMonto());
        }
        List<Movimiento> actual = listar(cuentaId);
        long antes = cuenta.calcularSaldo(actual);
        List<Movimiento> propuesta = new ArrayList<>();
        for (Movimiento m : actual) {
            if (reemplazado == null || m.getId() != reemplazado.longValue()) propuesta.add(m);
            else if (nuevo != null) propuesta.add(nuevo);
        }
        if (reemplazado == null && nuevo != null) propuesta.add(nuevo);
        long despues = cuenta.calcularSaldo(propuesta);
        // Los datos negativos anteriores se conservan, pero ninguna operación puede empeorarlos.
        if (despues < 0 && (antes >= 0 || despues < antes))
            throw new IllegalArgumentException("No hay dinero suficiente en esta cuenta.");
    }

    public List<Movimiento> listar(long cuentaId) {
        List<Movimiento> movimientos = new ArrayList<>();
        try (Cursor cursor = helper.getReadableDatabase().query("movimientos", null,
                "cuenta_id = ?", new String[]{Long.toString(cuentaId)},
                null, null, "fecha ASC, id ASC")) {
            while (cursor.moveToNext()) {
                int lat = cursor.getColumnIndexOrThrow("latitud"), lon = cursor.getColumnIndexOrThrow("longitud");
                if (cursor.isNull(lat) != cursor.isNull(lon))
                    throw new IllegalArgumentException("El movimiento tiene un par de coordenadas incompleto.");
                Ubicacion ubicacion = cursor.isNull(lat) ? null : new Ubicacion(cursor.getDouble(lat), cursor.getDouble(lon));
                movimientos.add(new Movimiento(cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("cuenta_id")),
                        Movimiento.Tipo.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("tipo"))),
                        cursor.getLong(cursor.getColumnIndexOrThrow("monto")),
                        cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("fecha")), ubicacion));
            }
        }
        return movimientos;
    }
}


