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

