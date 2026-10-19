package com.aik1a.localcash.data;

import android.content.ContentValues;
import android.database.Cursor;
import com.aik1a.localcash.domain.Cuenta;
import com.aik1a.localcash.domain.Divisa;
import java.util.ArrayList;
import java.util.List;

public final class CuentaRepository {
    private final LocalCashDbHelper helper;
    public CuentaRepository(LocalCashDbHelper helper) { this.helper = helper; }

    public long crear(Cuenta cuenta) {
        if (cuenta.getSaldoInicial() < 0) throw new IllegalArgumentException("El saldo inicial no puede ser negativo.");
        ContentValues values = new ContentValues();
        values.put("nombre", cuenta.getNombre());
        values.put("saldo_inicial", cuenta.getSaldoInicial());
        values.put("divisa", cuenta.getDivisa() == null ? null : cuenta.getDivisa().name());
        return helper.getWritableDatabase().insertOrThrow("cuentas", null, values);
    }

    public void editar(long id, String nombre) {
        Cuenta actual = buscar(id);
        Cuenta editada = new Cuenta(id, nombre, actual.getSaldoInicial(), actual.getDivisa());
        ContentValues values = new ContentValues(); values.put("nombre", editada.getNombre());
        if (helper.getWritableDatabase().update("cuentas", values, "id=?", new String[]{Long.toString(id)}) != 1)
            throw new IllegalArgumentException("La cuenta no existe.");
    }

    public void eliminar(long id) {
        android.database.sqlite.SQLiteDatabase db = helper.getWritableDatabase(); db.beginTransaction();
        try {
            buscar(id);
            if (!new MovimientoRepository(helper).listar(id).isEmpty())
                throw new IllegalArgumentException("Solo puedes eliminar cuentas sin movimientos.");
            if (db.delete("cuentas", "id=?", new String[]{Long.toString(id)}) != 1)
                throw new IllegalArgumentException("La cuenta no existe.");
            db.setTransactionSuccessful();
        } finally { db.endTransaction(); }
    }

    public void asignarDivisa(long id, Divisa divisa) {
        if (divisa == null) throw new IllegalArgumentException("Selecciona una divisa.");
        android.database.sqlite.SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            Cuenta cuenta = buscar(id);
            if (cuenta.getDivisa() != null) throw new IllegalArgumentException("La cuenta ya tiene divisa.");
            divisa.validar(cuenta.getSaldoInicial());
            for (com.aik1a.localcash.domain.Movimiento m : new MovimientoRepository(helper).listar(id)) divisa.validar(m.getMonto());
            ContentValues values = new ContentValues(); values.put("divisa", divisa.name());
            if (db.update("cuentas", values, "id=? AND divisa IS NULL", new String[]{Long.toString(id)}) != 1)
                throw new IllegalArgumentException("La cuenta cambió. Vuelve a abrirla.");
            db.setTransactionSuccessful();
        } finally { db.endTransaction(); }
    }

    public List<Cuenta> listar() {
        List<Cuenta> cuentas = new ArrayList<>();
        try (Cursor cursor = helper.getReadableDatabase().query("cuentas", null,
                null, null, null, null, "id ASC")) {
            while (cursor.moveToNext()) cuentas.add(leer(cursor));
        }
        return cuentas;
    }

    public Cuenta buscar(long id) {
        try (Cursor cursor = helper.getReadableDatabase().query("cuentas", null,
                "id = ?", new String[]{Long.toString(id)}, null, null, null)) {
            if (!cursor.moveToFirst()) throw new IllegalArgumentException("La cuenta no existe.");
            return leer(cursor);
        }
    }

    private Cuenta leer(Cursor cursor) {
        return new Cuenta(cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                cursor.getLong(cursor.getColumnIndexOrThrow("saldo_inicial")),
                Divisa.desdeCodigo(cursor.getString(cursor.getColumnIndexOrThrow("divisa"))));
    }
}

