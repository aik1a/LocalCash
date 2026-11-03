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
import java.util.*;

public final class CompraDetalleActivity extends ActividadProtegida {
    public static final String COMPRA_ID = "compra_id";
    @Override protected void crearContenido(Bundle state) {
        setContentView(R.layout.activity_compra_detalle);
        Pantalla.aplicarMargenes(findViewById(R.id.root));
        findViewById(R.id.volver).setOnClickListener(v -> finish());
        findViewById(R.id.pagar_compra).setOnClickListener(v -> elegirCuenta());
    }
    private long compraId() { return getIntent().getLongExtra(COMPRA_ID, -1); }
    @Override protected void actualizarContenido() {
        try (LocalCashDbHelper helper = new LocalCashDbHelper(this)) {
            CompraService service = new CompraService(helper);
            Compra compra = service.buscarCompra(compraId());
            if (compra.getDivisa() == null) DivisasUI.compra(this, compra.getId(),
                    new com.aik1a.localcash.data.ConfiguracionRepository(helper).exigirDivisa(), this::recreate);
            findViewById(R.id.abrir_reparto).setVisibility(compra.getEstado() == Compra.Estado.REALIZADA ? View.VISIBLE : View.GONE);
            findViewById(R.id.abrir_reparto).setOnClickListener(v -> startActivity(
                    new Intent(this, RepartoActivity.class).putExtra(COMPRA_ID, compra.getId())));
            ((TextView)findViewById(R.id.compra_titulo)).setText(compra.getNombre());
            ((TextView)findViewById(R.id.compra_info)).setText(
                    (compra.getEstado() == Compra.Estado.PLANIFICADA ? "Planificada" : "Realizada") + " · "
                    + DateFormat.getDateInstance(DateFormat.SHORT).format(new Date(compra.getFecha())));
            ((TextView)findViewById(R.id.compra_contacto)).setText("Contacto: " + (compra.getContactoId() == null ? "Sin contacto"
                    : service.buscarContacto(compra.getContactoId()).getNombre()));
            ((TextView)findViewById(R.id.compra_total)).setText("Total: " + Dinero.formatear(compra.calcularTotal(), compra.getDivisa()));
            Long cuentaId = service.buscarCuentaPago(compra.getId());
            boolean planificada = compra.getEstado() == Compra.Estado.PLANIFICADA && cuentaId == null;
            ((TextView)findViewById(R.id.ayuda_acciones_compra)).setText(planificada
                    ? (compra.getDivisa() == null ? "Define primero la moneda para poder editar esta planificación." : "")
                    : "Esta compra ya está realizada. La edición y eliminación de planificaciones no están disponibles.");
            findViewById(R.id.editar_planificacion).setVisibility(planificada ? View.VISIBLE : View.GONE);
            findViewById(R.id.editar_planificacion).setEnabled(compra.getDivisa() != null);
            findViewById(R.id.editar_planificacion).setOnClickListener(v -> startActivity(new Intent(this, NuevaCompraActivity.class)
                    .putExtra(NuevaCompraActivity.EDITAR_COMPRA_ID, compra.getId())));
            findViewById(R.id.eliminar_planificacion).setVisibility(planificada ? View.VISIBLE : View.GONE);
            findViewById(R.id.eliminar_planificacion).setOnClickListener(v -> AccionesUI.eliminar(this,
                    "¿Eliminar la planificación " + compra.getNombre() + " y sus productos? No cambiará ningún saldo.",
                    () -> { try (LocalCashDbHelper db = new LocalCashDbHelper(this)) { new CompraService(db).eliminarPlanificacion(compra.getId()); } }, this::finish));
            Button pagar = findViewById(R.id.pagar_compra);
            pagar.setVisibility(cuentaId == null ? View.VISIBLE : View.GONE);
            pagar.setEnabled(compra.getDivisa() != null && compra.calcularTotal() > 0);
            pagar.setText(compra.getEstado() == Compra.Estado.PLANIFICADA ? "Realizar y pagar desde una cuenta" : "Registrar pago desde una cuenta");
            Button abrir = findViewById(R.id.ver_cuenta_pago);
            abrir.setVisibility(cuentaId == null ? View.GONE : View.VISIBLE);
            if (cuentaId != null) {
                Cuenta cuenta = new MovimientoService(helper).buscarCuenta(cuentaId);
                abrir.setText("Ver cuenta: " + cuenta.getNombre());
                abrir.setOnClickListener(v -> startActivity(new Intent(this, CuentaActivity.class)
                        .putExtra(CuentaActivity.CUENTA_ID, cuentaId)));
            }
            ((TextView)findViewById(R.id.pago_info)).setText(cuentaId != null ? "Pago registrado. El reparto no vuelve a descontar dinero."
                    : compra.getDivisa() == null ? "Para pagar o editar debes definir la moneda. Vuelve a abrir esta compra para elegirla."
                    : compra.calcularTotal() == 0 ? "Esta compra no tiene un importe para descontar."
                    : "Sin pago asociado. El saldo solo cambia cuando confirmas el pago.");
            LinearLayout detalles = findViewById(R.id.detalles);
            detalles.removeAllViews();
            for (DetalleCompra detalle : compra.getDetalles()) {
                TextView fila = (TextView)getLayoutInflater().inflate(R.layout.row_detalle, detalles, false);
                fila.setText(detalle.getDescripcion() + "\n" + detalle.getCantidad() + " × " + Dinero.formatear(detalle.getPrecioUnitario(), compra.getDivisa())
                        + " = " + Dinero.formatear(detalle.calcularSubtotal(), compra.getDivisa())); detalles.addView(fila);
            }
        } catch (IllegalArgumentException ex) {
            AccionesUI.aviso(this, "No se pudo abrir la compra", "La compra no existe.".equals(ex.getMessage())
                    ? "La compra ya no existe. Vuelve al historial y selecciona otra."
                    : "Los datos de esta compra no están disponibles. Vuelve al historial e intenta de nuevo.", this::finish);
        } catch (ArithmeticException | SQLiteException ex) {
            AccionesUI.aviso(this, "No se pudo abrir la compra", "No se pudieron cargar sus datos. Vuelve al historial e intenta de nuevo.", this::finish);
        }
    }
    private void elegirCuenta() {
        try (LocalCashDbHelper helper = new LocalCashDbHelper(this)) {
            CompraService service = new CompraService(helper);
            Compra compra = service.buscarCompra(compraId());
            List<Cuenta> cuentas = service.listarCuentasPago(compraId());
            if (cuentas.isEmpty()) {
                protegerDialogo(new AlertDialog.Builder(this).setTitle("Necesitas una cuenta en " + compra.getDivisa().name())
                        .setMessage("Crea una cuenta con la misma moneda desde la pantalla de cuentas y vuelve a esta compra.")
                        .setPositiveButton("Entendido", null).create()).show();
                return;
            }
            String[] nombres = new String[cuentas.size()];
            for (int i = 0; i < cuentas.size(); i++) nombres[i] = cuentas.get(i).getNombre()
                    + " · " + Dinero.formatear(new MovimientoService(helper).calcularSaldo(cuentas.get(i).getId()), compra.getDivisa());
            protegerDialogo(new AlertDialog.Builder(this).setTitle("¿Desde qué cuenta pagaste?")
                    .setItems(nombres, (dialog, posicion) -> confirmarPago(compra, cuentas.get(posicion)))
                    .setNegativeButton("Cancelar", null).create()).show();
        } catch (IllegalArgumentException | ArithmeticException | SQLiteException ex) {
            AccionesUI.aviso(this, "No se pudieron cargar las cuentas", "No se registró ningún pago. Vuelve a abrir la compra e intenta de nuevo.");
        }
    }
    private void confirmarPago(Compra compra, Cuenta cuenta) {
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Confirmar pago")
                .setMessage("Se descontará " + Dinero.formatear(compra.calcularTotal(), compra.getDivisa())
                        + " de " + cuenta.getNombre() + ". Confirma solo si pagaste el total y aún no registraste este gasto como movimiento. "
                        + "Las deudas del reparto quedan aparte.")
                .setNegativeButton("Cancelar", null).setPositiveButton("Registrar pago", null).create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            try (LocalCashDbHelper helper = new LocalCashDbHelper(this)) {
                new CompraService(helper).pagar(compra.getId(), cuenta.getId());
                dialog.dismiss(); AccionesUI.exito(this, "Pago registrado."); actualizarContenido();
            } catch (IllegalArgumentException ex) {
                AccionesUI.aviso(this, "No se puede registrar el pago", ex.getMessage());
            } catch (ArithmeticException | SQLiteException ex) {
                AccionesUI.aviso(this, "No se registró el pago", "No se completó la operación. El saldo no cambió por este intento. Vuelve a abrir la compra e intenta de nuevo.");
            }
        }));
        protegerDialogo(dialog).show();
    }
}
