package com.aik1a.localcash.presentation;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.database.sqlite.SQLiteException;
import android.view.View;
import android.widget.*;
import com.aik1a.localcash.R;
import com.aik1a.localcash.application.DeudaService;
import com.aik1a.localcash.data.LocalCashDbHelper;
import com.aik1a.localcash.domain.*;
import java.util.*;

public final class RepartoActivity extends ActividadProtegida {
    private long compraId;
    private Divisa divisa;
    private EditText cuotaUsuario;
    private Long montoUsuario;
    private final List<Contacto> contactos = new ArrayList<>();
    private final List<CheckBox> seleccion = new ArrayList<>();
    private final List<EditText> cuotas = new ArrayList<>();

    @Override protected void crearContenido(Bundle state) {
         setContentView(R.layout.activity_reparto);
        Pantalla.aplicarMargenes(findViewById(R.id.root));
        findViewById(R.id.volver).setOnClickListener(v -> finish());
        compraId = getIntent().getLongExtra(CompraDetalleActivity.COMPRA_ID, -1);
        try (LocalCashDbHelper helper = new LocalCashDbHelper(this)) {
            DeudaService service = new DeudaService(helper);
            Compra compra = service.buscarCompra(compraId);
            divisa = compra.getDivisa();
            if (divisa == null) {
                DivisasUI.compra(this, compraId, new com.aik1a.localcash.data.ConfiguracionRepository(helper).exigirDivisa(), this::recreate);
                findViewById(R.id.formulario_reparto).setVisibility(View.GONE); return;
            }
            montoUsuario = service.buscarMontoUsuario(compraId);
            if (compra.getEstado() != Compra.Estado.REALIZADA)
                throw new IllegalArgumentException("Solo se reparten compras realizadas.");
            ((TextView)findViewById(R.id.reparto_titulo)).setText(compra.getNombre());
            ((TextView)findViewById(R.id.reparto_total)).setText("Total: " + Dinero.formatear(compra.calcularTotal(), divisa));
            contactos.addAll(service.listarContactos());
            LinearLayout filas = findViewById(R.id.participantes);
            View usuario = getLayoutInflater().inflate(R.layout.row_participante, filas, false);
            CheckBox tu = usuario.findViewById(R.id.seleccionar_contacto);
            tu.setText("Tú"); tu.setChecked(true); tu.setEnabled(false);
            cuotaUsuario = usuario.findViewById(R.id.cuota);
            cuotaUsuario.setContentDescription("Monto para Tú"); DivisasUI.ejemplo(cuotaUsuario, divisa);
            if (state != null) cuotaUsuario.setText(state.getString("cuota_usuario", ""));
            filas.addView(usuario);
            for (Contacto contacto : contactos) {
                View fila = getLayoutInflater().inflate(R.layout.row_participante, filas, false);
                CheckBox check = fila.findViewById(R.id.seleccionar_contacto);
                EditText cuota = fila.findViewById(R.id.cuota);
                String nombre = contacto.getNombre();
                DivisasUI.ejemplo(cuota, divisa);
                check.setText(nombre); cuota.setContentDescription("Monto para " + nombre);
                check.setOnCheckedChangeListener((button, checked) -> actualizarCampos());
                seleccion.add(check); cuotas.add(cuota); filas.addView(fila);
            }
            ((RadioGroup)findViewById(R.id.modo_reparto)).setOnCheckedChangeListener((group, id) -> actualizarCampos());
            if (state != null) {
                long[] ids = state.getLongArray("ids");
                String[] valores = state.getStringArray("cuotas");
                long[] elegidos = state.getLongArray("elegidos");
                ((RadioGroup)findViewById(R.id.modo_reparto)).check(state.getBoolean("personalizado") ? R.id.personalizado : R.id.equitativo);
                if (ids != null && valores != null && elegidos != null)
                    for (int i = 0; i < contactos.size(); i++)
                        for (int j = 0; j < ids.length; j++) if (contactos.get(i).getId() == ids[j]) {
                            cuotas.get(i).setText(valores[j]); seleccion.get(i).setChecked(elegidos[j] == 1);
                        }
            }
            if (state == null && contactos.size() == 1) seleccion.get(0).setChecked(true);
            actualizarCampos();
            mostrarDeudas(service.listarDeudas(compraId));
            if (contactos.isEmpty() && service.listarDeudas(compraId).isEmpty())
                mostrarError("Añade un contacto para compartir la compra contigo.");
        } catch (IllegalArgumentException ex) {
            String motivo = "La compra no existe.".equals(ex.getMessage()) || "Solo se reparten compras realizadas.".equals(ex.getMessage())
                    ? ex.getMessage() : "Los datos del reparto no están disponibles. Vuelve a abrir la compra.";
            mostrarError(motivo); findViewById(R.id.formulario_reparto).setVisibility(View.GONE);
        } catch (ArithmeticException | SQLiteException ex) {
            mostrarError("No se pudo cargar el reparto. Vuelve a abrir la compra e intenta de nuevo.");
            findViewById(R.id.formulario_reparto).setVisibility(View.GONE);
        }
        findViewById(R.id.guardar_reparto).setOnClickListener(v -> guardar());
    }
    private boolean personalizado() { return ((RadioButton)findViewById(R.id.personalizado)).isChecked(); }
    private void actualizarCampos() {
        if (cuotaUsuario != null) cuotaUsuario.setVisibility(personalizado() ? View.VISIBLE : View.GONE);
        for (int i = 0; i < cuotas.size(); i++) {
            cuotas.get(i).setVisibility(personalizado() ? View.VISIBLE : View.GONE);
            cuotas.get(i).setEnabled(seleccion.get(i).isChecked());
        }
    }
    private void guardar() {
        mostrarError("");
        boolean alguno = false;
        for (CheckBox check : seleccion) if (check.isChecked()) alguno = true;
        if (!alguno) { mostrarError("Selecciona al menos un contacto para compartir la compra contigo."); return; }
        boolean guardado = false;
        try (LocalCashDbHelper helper = new LocalCashDbHelper(this)) {
            List<Long> ids = new ArrayList<>(), montos = new ArrayList<>();
            for (int i = 0; i < contactos.size(); i++) if (seleccion.get(i).isChecked()) {
                ids.add(contactos.get(i).getId());
                if (personalizado()) {
                    Long monto = AccionesUI.leerMonto(cuotas.get(i), divisa, true, "el monto de " + contactos.get(i).getNombre());
                    if (monto == null) return;
                    montos.add(monto);
                }
            }
            DeudaService service = new DeudaService(helper);
            if (personalizado()) {
                Long propio = AccionesUI.leerMonto(cuotaUsuario, divisa, true, "tu parte del reparto");
                if (propio == null) return;
                service.repartirPersonalizadoConUsuario(compraId, ids, montos, propio);
            } else service.repartirEquitativoConUsuario(compraId, ids);
            guardado = true;
            findViewById(R.id.guardar_reparto).setEnabled(false);
            AccionesUI.exito(this, "Reparto guardado.");
            montoUsuario = service.buscarMontoUsuario(compraId);
            mostrarDeudas(service.listarDeudas(compraId)); mostrarError("");
        } catch (IllegalArgumentException ex) {
            mostrarError(guardado ? "El reparto se guardó. Vuelve a abrir la compra para ver los datos actualizados." : ex.getMessage());
        } catch (ArithmeticException ex) {
            mostrarError(guardado ? "El reparto se guardó. Vuelve a abrir la compra para ver los datos actualizados." : "La suma es demasiado grande. Revisa y reduce los importes.");
        } catch (SQLiteException ex) {
            mostrarError(guardado ? "El reparto se guardó, pero no se pudo actualizar la pantalla. Vuelve a abrir la compra." : "No se guardó el reparto. Tus importes se conservan; intenta de nuevo.");
        }
    }
    private void mostrarDeudas(List<Deuda> deudas) {
        LinearLayout filas = findViewById(R.id.deudas);
        filas.removeAllViews();
        if (deudas.isEmpty()) return;
        if (montoUsuario != null) {
            TextView tu = (TextView)getLayoutInflater().inflate(R.layout.row_detalle, filas, false);
            tu.setText("Tú: " + Dinero.formatear(montoUsuario, divisa)); filas.addView(tu);
        }
        findViewById(R.id.formulario_reparto).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.reparto_info)).setText("Reparto registrado. Marcar una deuda pagada no modifica los saldos de tus cuentas.");
        for (Deuda deuda : deudas) {
            TextView fila = (TextView)getLayoutInflater().inflate(R.layout.row_detalle, filas, false);
            fila.setText(deuda.getContacto().getNombre() + ": "
                    + Dinero.formatear(deuda.getMonto(), divisa) + " · " + (deuda.getMonto() == 0 ? "Sin deuda" : "PAGADA".equals(deuda.getEstado()) ? "Pagada" : "Pendiente"));
            filas.addView(fila);
            if (deuda.getMonto() > 0) {
                Button accion = new Button(this);
                boolean pagada = "PAGADA".equals(deuda.getEstado());
                accion.setText(pagada ? "Volver a pendiente: " + deuda.getContacto().getNombre() : "Marcar como pagada: " + deuda.getContacto().getNombre());
                accion.setOnClickListener(v -> confirmarEstado(deuda, !pagada)); filas.addView(accion);
            }
        }
    }
    private void confirmarEstado(Deuda deuda, boolean pagada) {
        protegerDialogo(new AlertDialog.Builder(this).setTitle(pagada ? "Marcar deuda pagada" : "Volver a pendiente")
                .setMessage(deuda.getContacto().getNombre() + " · " + Dinero.formatear(deuda.getMonto(), divisa)
                        + ". Esta acción no modifica los saldos de tus cuentas.")
                .setNegativeButton("Cancelar", null).setPositiveButton("Confirmar", (dialog, which) -> {
                    boolean actualizado = false;
                    try (LocalCashDbHelper helper = new LocalCashDbHelper(this)) {
                        DeudaService service = new DeudaService(helper);
                        service.cambiarEstado(compraId, deuda.getContacto().getId(), pagada);
                        actualizado = true;
                        AccionesUI.exito(this, pagada ? "Deuda marcada como pagada." : "Deuda marcada pendiente.");
                        mostrarDeudas(service.listarDeudas(compraId)); mostrarError("");
                    } catch (IllegalArgumentException ex) {
                        AccionesUI.aviso(this, actualizado ? "Estado guardado" : "No se puede cambiar el estado", actualizado
                                ? "El cambio se guardó. Vuelve a abrir el reparto para actualizar la pantalla." : ex.getMessage());
                    } catch (SQLiteException ex) {
                        AccionesUI.aviso(this, actualizado ? "Estado guardado" : "No se pudo cambiar el estado", actualizado
                                ? "El cambio se guardó. Vuelve a abrir el reparto para actualizar la pantalla."
                                : "No se completó el cambio. Vuelve a abrir el reparto e intenta de nuevo.");
                    }
                }).create()).show();
    }
    private void mostrarError(String mensaje) { AccionesUI.errorTexto((TextView)findViewById(R.id.error_reparto), mensaje); }
    @Override public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        if (!contenidoCreado()) return;
        long[] ids = new long[contactos.size()], elegidos = new long[contactos.size()];
        String[] valores = new String[contactos.size()];
        for (int i = 0; i < contactos.size(); i++) {
            ids[i] = contactos.get(i).getId(); elegidos[i] = seleccion.get(i).isChecked() ? 1 : 0;
            valores[i] = cuotas.get(i).getText().toString();
        }
        if (cuotaUsuario != null) state.putString("cuota_usuario", cuotaUsuario.getText().toString());
        state.putLongArray("ids", ids); state.putLongArray("elegidos", elegidos);
        state.putStringArray("cuotas", valores); state.putBoolean("personalizado", personalizado());
    }
}
