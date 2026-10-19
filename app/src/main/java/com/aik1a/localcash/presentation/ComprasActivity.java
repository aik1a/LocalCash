package com.aik1a.localcash.presentation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.database.sqlite.SQLiteException;
import android.widget.*;
import com.aik1a.localcash.R;
import com.aik1a.localcash.application.CompraService;
import com.aik1a.localcash.data.LocalCashDbHelper;
import com.aik1a.localcash.domain.*;
import java.text.DateFormat;
import java.util.*;

public final class ComprasActivity extends ActividadProtegida {
    private LocalCashDbHelper helper;
    private CompraService service;
    private List<Compra> compras = new ArrayList<>();
    private ListView lista;
    @Override protected void crearContenido(Bundle state) {
         setContentView(R.layout.activity_compras);
        Pantalla.aplicarMargenes(findViewById(R.id.root));
        helper = new LocalCashDbHelper(this); service = new CompraService(helper);
        lista = findViewById(R.id.compras); lista.setEmptyView(findViewById(R.id.vacio));
        findViewById(R.id.volver).setOnClickListener(v -> finish());
        findViewById(R.id.crear_compra).setOnClickListener(v -> startActivity(new Intent(this, NuevaCompraActivity.class)));
        lista.setOnItemClickListener((parent, view, position, id) -> startActivity(new Intent(this, CompraDetalleActivity.class)
                .putExtra(CompraDetalleActivity.COMPRA_ID, compras.get(position).getId())));
    }
    @Override protected void actualizarContenido() { actualizar(); }
    private void actualizar() {
        try {
            compras = service.listarCompras();
            List<Map<String, String>> filas = new ArrayList<>();
            DateFormat formato = DateFormat.getDateInstance(DateFormat.SHORT);
            for (Compra compra : compras) {
                Map<String, String> fila = new HashMap<>(); fila.put("nombre", compra.getNombre());
                fila.put("detalle", (compra.getEstado() == Compra.Estado.PLANIFICADA ? "Planificada" : "Realizada") + " · " + formato.format(new Date(compra.getFecha()))
                        + " · Total: " + Dinero.formatear(compra.calcularTotal(), compra.getDivisa())); filas.add(fila);
            }
            lista.setAdapter(new SimpleAdapter(this, filas, android.R.layout.simple_list_item_2,
                    new String[]{"nombre", "detalle"}, new int[]{android.R.id.text1, android.R.id.text2}));
        } catch (SQLiteException | IllegalArgumentException | ArithmeticException ex) {
            Toast.makeText(this, "No se pudieron cargar las compras.", Toast.LENGTH_LONG).show();
        }
    }
    @Override protected void onDestroy() { if (helper != null) helper.close(); super.onDestroy(); }
}


