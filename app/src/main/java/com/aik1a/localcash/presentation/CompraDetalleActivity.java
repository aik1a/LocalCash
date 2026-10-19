package com.aik1a.localcash.presentation;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.database.sqlite.SQLiteException;
import android.widget.*;
import com.aik1a.localcash.R;
import com.aik1a.localcash.application.CompraService;
import com.aik1a.localcash.application.MovimientoService;
import com.aik1a.localcash.data.LocalCashDbHelper;
import com.aik1a.localcash.domain.*;
import java.text.DateFormat;
import java.util.Date;

public final class CompraDetalleActivity extends ActividadProtegida {
    public static final String COMPRA_ID = "compra_id";
    private LocalCashDbHelper helper;
    private CompraService service;
    private Compra compra;
    private long compraId;
    private AlertDialog selectorDivisa;
    @Override protected void crearContenido(Bundle state) {
        setContentView(R.layout.activity_compra_detalle);
        Pantalla.aplicarMargenes(findViewById(R.id.root));
        helper = new LocalCashDbHelper(this); service = new CompraService(helper);
        compraId = getIntent().getLongExtra(COMPRA_ID, -1);
        findViewById(R.id.volver).setOnClickListener(v -> finish());
        findViewById(R.id.abrir_reparto).setOnClickListener(v -> startActivity(new Intent(this, RepartoActivity.class).putExtra(COMPRA_ID, compraId)));
        findViewById(R.id.realizar_compra).setOnClickListener(v -> PagoCompraUI.mostrar(this, helper, compra, cuentaId -> {
            service.realizar(compraId, cuentaId); actualizar();
        }));
        findViewById(R.id.editar_compra).setOnClickListener(v -> {
            if (compra.getEstado() == Compra.Estado.PLANIFICADA) startActivity(new Intent(this, NuevaCompraActivity.class).putExtra(COMPRA_ID, compraId));
            else AccionesUI.nombre(this, "Editar nombre de compra", compra.getNombre(), nombre -> {
                service.renombrar(compraId, nombre); actualizar(); AccionesUI.exito(this, "Compra actualizada");
            });
        });
        findViewById(R.id.eliminar_compra).setOnClickListener(v -> confirmarEliminacion());
    }
    @Override protected void actualizarContenido() { actualizar(); }
    private void actualizar() {
        try {
            compra = service.buscarCompra(compraId);
            if (compra.getDivisa() == null && (selectorDivisa == null || !selectorDivisa.isShowing()))
                selectorDivisa = DivisasUI.compra(this, compraId,
                        new com.aik1a.localcash.data.ConfiguracionRepository(helper).exigirDivisa(), this::actualizar);
            boolean realizada = compra.getEstado() == Compra.Estado.REALIZADA;
            findViewById(R.id.abrir_reparto).setVisibility(realizada ? View.VISIBLE : View.GONE);
            findViewById(R.id.realizar_compra).setVisibility(realizada ? View.GONE : View.VISIBLE);
            ((Button)findViewById(R.id.editar_compra)).setText(realizada ? "Editar nombre" : "Editar compra");
            ((TextView)findViewById(R.id.compra_titulo)).setText(compra.getNombre());
            ((TextView)findViewById(R.id.compra_info)).setText((realizada ? "Realizada" : "Planificada") + " · "
                    + DateFormat.getDateInstance(DateFormat.SHORT).format(new Date(compra.getFecha())));
            ((TextView)findViewById(R.id.compra_contacto)).setText("Contacto: " + (compra.getContactoId() == null ? "Sin contacto"
                    : service.buscarContacto(compra.getContactoId()).getNombre()));
            ((TextView)findViewById(R.id.compra_total)).setText("Total: " + Dinero.formatear(compra.calcularTotal(), compra.getDivisa()));
            Long cuentaId = service.buscarCuentaPago(compraId);
            ((TextView)findViewById(R.id.estado_pago)).setText(cuentaId == null
                    ? (realizada ? "No hay un cargo registrado en una cuenta." : "El saldo se descontará cuando confirmes el pago.")
                    : "Pagada desde: " + new MovimientoService(helper).buscarCuenta(cuentaId).getNombre());
            LinearLayout detalles = findViewById(R.id.detalles); detalles.removeAllViews();
            for (DetalleCompra detalle : compra.getDetalles()) {
                TextView fila = (TextView)getLayoutInflater().inflate(R.layout.row_detalle, detalles, false);
                fila.setText(detalle.getDescripcion() + "\n" + detalle.getCantidad() + " × " + Dinero.formatear(detalle.getPrecioUnitario(), compra.getDivisa())
                        + " = " + Dinero.formatear(detalle.calcularSubtotal(), compra.getDivisa())); detalles.addView(fila);
            }
        } catch (IllegalArgumentException | ArithmeticException | SQLiteException ex) {
            Toast.makeText(this, "No se pudo abrir la compra.", Toast.LENGTH_LONG).show(); finish();
        }
    }
    private void confirmarEliminacion() {
        try {
            Long cuentaId = service.buscarCuentaPago(compraId);
            String mensaje = "¿Eliminar esta compra?";
            if (cuentaId != null) mensaje += " Se devolverá " + Dinero.formatear(compra.calcularTotal(), compra.getDivisa())
                    + " a " + new MovimientoService(helper).buscarCuenta(cuentaId).getNombre() + ".";
            AccionesUI.confirmar(this, "Eliminar compra", mensaje, () -> {
                service.eliminarCompra(compraId); AccionesUI.exito(this, "Compra eliminada"); finish();
            });
        } catch (IllegalArgumentException | ArithmeticException | SQLiteException ex) { AccionesUI.error(this, "No se pudo consultar la compra. Vuelve a abrirla."); }
    }
    @Override protected void onDestroy() { if (helper != null) helper.close(); super.onDestroy(); }
}
