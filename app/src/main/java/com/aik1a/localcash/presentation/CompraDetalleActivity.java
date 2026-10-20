package com.aik1a.localcash.presentation;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.database.sqlite.SQLiteException;
import android.widget.*;
import com.aik1a.localcash.R;
import com.aik1a.localcash.application.CompraService;
import com.aik1a.localcash.data.LocalCashDbHelper;
import com.aik1a.localcash.domain.*;
import java.text.DateFormat;
import java.util.Date;

public final class CompraDetalleActivity extends ActividadProtegida {
    public static final String COMPRA_ID = "compra_id";
    @Override protected void crearContenido(Bundle state) {
         setContentView(R.layout.activity_compra_detalle);
        Pantalla.aplicarMargenes(findViewById(R.id.root));
        findViewById(R.id.volver).setOnClickListener(v -> finish());
        try (LocalCashDbHelper helper = new LocalCashDbHelper(this)) {
            CompraService service = new CompraService(helper);
            Compra compra = service.buscarCompra(getIntent().getLongExtra(COMPRA_ID, -1));
            if (compra.getDivisa() == null) DivisasUI.compra(this, compra.getId(),
                    new com.aik1a.localcash.data.ConfiguracionRepository(helper).exigirDivisa(), this::recreate);
            findViewById(R.id.abrir_reparto).setVisibility(compra.getEstado() == Compra.Estado.REALIZADA ? View.VISIBLE : View.GONE);
            findViewById(R.id.abrir_reparto).setOnClickListener(v -> startActivity(
                    new Intent(this, RepartoActivity.class).putExtra(COMPRA_ID, compra.getId())));
            ((TextView)findViewById(R.id.compra_titulo)).setText(compra.getNombre());
            ((TextView)findViewById(R.id.compra_info)).setText(compra.getEstado().name() + " · "
                    + DateFormat.getDateInstance(DateFormat.SHORT).format(new Date(compra.getFecha())));
            ((TextView)findViewById(R.id.compra_contacto)).setText("Contacto: " + (compra.getContactoId() == null ? "Sin contacto"
                    : service.buscarContacto(compra.getContactoId()).getNombre()));
            ((TextView)findViewById(R.id.compra_total)).setText("Total: " + Dinero.formatear(compra.calcularTotal(), compra.getDivisa()));
            LinearLayout detalles = findViewById(R.id.detalles);
            for (DetalleCompra detalle : compra.getDetalles()) {
                TextView fila = (TextView)getLayoutInflater().inflate(R.layout.row_detalle, detalles, false);
                fila.setText(detalle.getDescripcion() + "\n" + detalle.getCantidad() + " × " + Dinero.formatear(detalle.getPrecioUnitario(), compra.getDivisa())
                        + " = " + Dinero.formatear(detalle.calcularSubtotal(), compra.getDivisa())); detalles.addView(fila);
            }
        } catch (IllegalArgumentException | ArithmeticException | SQLiteException ex) {
            Toast.makeText(this, "No se pudo abrir la compra.", Toast.LENGTH_LONG).show(); finish();
        }
    }
}



