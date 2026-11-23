package com.aik1a.localcash.presentation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import com.aik1a.localcash.R;
import com.aik1a.localcash.application.CompraService;
import com.aik1a.localcash.data.LocalCashStore;
import com.aik1a.localcash.domain.*;
import java.text.DateFormat;
import java.util.*;

public final class ComprasActivity extends ActividadProtegida {
    private LocalCashStore store;
    private CompraService service;
    private List<Compra> compras = new ArrayList<>();
    private ListView lista;
    @Override protected void crearContenido(Bundle state) {
         setContentView(R.layout.activity_compras);
        Pantalla.aplicarMargenes(findViewById(R.id.root));
        store = LocalCashStore.get(this); service = new CompraService(store);
        lista = findViewById(R.id.compras); lista.setEmptyView(findViewById(R.id.vacio));
        findViewById(R.id.volver).setOnClickListener(v -> finish());
        findViewById(R.id.crear_compra).setOnClickListener(v -> startActivity(new Intent(this, NuevaCompraActivity.class)));
        lista.setOnItemClickListener((parent, view, position, id) -> startActivity(new Intent(this, CompraDetalleActivity.class)
                .putExtra(CompraDetalleActivity.COMPRA_ID, compras.get(position).getId())));
    }
    @Override protected void actualizarContenido() { actualizar(); }
    private void actualizar() {
        try {
            long cuentaId = getIntent().getLongExtra(CuentaActivity.CUENTA_ID, -1);
            compras = cuentaId > 0 ? service.listarComprasDeCuenta(cuentaId) : service.listarCompras();
            if (cuentaId > 0) {
                findViewById(R.id.crear_compra).setVisibility(android.view.View.GONE);
                ((TextView)findViewById(R.id.vacio)).setText("Aún no hay compras pagadas desde esta cuenta.");
            }
            List<Map<String, String>> filas = new ArrayList<>();
            DateFormat formato = DateFormat.getDateInstance(DateFormat.SHORT);
            for (Compra compra : compras) {
                Map<String, String> fila = new HashMap<>(); fila.put("nombre", compra.getNombre());
                fila.put("detalle", (compra.getEstado() == Compra.Estado.PLANIFICADA ? "Planificada" : "Realizada") + " · " + formato.format(new Date(compra.getFecha()))
                        + " · Total: " + Dinero.formatear(compra.calcularTotal(), compra.getDivisa())); filas.add(fila);
            }
            lista.setAdapter(new SimpleAdapter(this, filas, R.layout.row_compra,
                    new String[]{"nombre", "detalle"}, new int[]{android.R.id.text1, android.R.id.text2}));
        } catch (IllegalStateException | IllegalArgumentException | ArithmeticException ex) {
            Toast.makeText(this, "No se pudieron cargar las compras.", Toast.LENGTH_LONG).show();
        }
    }
    @Override protected void onDestroy() { if (store != null) store.close(); super.onDestroy(); }
}
