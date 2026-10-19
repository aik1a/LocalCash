package com.aik1a.localcash.presentation;

import android.app.Activity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.app.AlertDialog;
import android.os.Bundle;
import android.database.sqlite.SQLiteException;
import android.view.View;
import android.widget.*;
import com.aik1a.localcash.R;
import com.aik1a.localcash.application.MovimientoService;
import com.aik1a.localcash.data.LocalCashDbHelper;
import com.aik1a.localcash.domain.*;
import java.text.DateFormat;
import java.util.*;

public final class CuentaActivity extends ActividadProtegida {
    public static final String CUENTA_ID = "cuenta_id";
    private long cuentaId;
    private LocalCashDbHelper helper;
    private MovimientoService service;
    private ListView lista;
    private static final int PERMISO_UBICACION = 60;
    private AlertDialog formulario;
    private View campos;
    private CapturaUbicacion captura;
    private Ubicacion ubicacion;
    private boolean capturando;
    private Divisa divisa;
    private AlertDialog selectorDivisa;
    private List<Movimiento> historial = new ArrayList<>();


    @Override protected void crearContenido(Bundle state) {
        
        setContentView(R.layout.activity_cuenta);
        Pantalla.aplicarMargenes(findViewById(R.id.root));
        cuentaId = getIntent().getLongExtra(CUENTA_ID, -1);
        helper = new LocalCashDbHelper(this);
        service = new MovimientoService(helper);
        findViewById(R.id.abrir_mapa).setOnClickListener(v -> startActivity(new android.content.Intent(this, MapaActivity.class).putExtra(CUENTA_ID, cuentaId)));
        lista = findViewById(R.id.movimientos);
        lista.setEmptyView(findViewById(R.id.vacio));
        findViewById(R.id.volver).setOnClickListener(v -> finish());
        findViewById(R.id.registrar_movimiento).setOnClickListener(v -> mostrarFormulario());
        findViewById(R.id.editar_cuenta).setOnClickListener(v -> AccionesUI.nombre(this, "Editar cuenta",
                service.buscarCuenta(cuentaId).getNombre(), nombre -> {
                    service.editarCuenta(cuentaId, nombre); actualizar(); AccionesUI.exito(this, "Cuenta actualizada");
                }));
        findViewById(R.id.eliminar_cuenta).setOnClickListener(v -> AccionesUI.confirmar(this, "Eliminar cuenta",
                "Solo se puede eliminar una cuenta sin movimientos. ¿Eliminar esta cuenta?", () -> {
                    service.eliminarCuenta(cuentaId); AccionesUI.exito(this, "Cuenta eliminada"); finish();
                }));
        lista.setOnItemClickListener((parent, view, position, id) -> opcionesMovimiento(historial.get(position)));
    }
    @Override protected void actualizarContenido() { actualizar(); }

    private void actualizar() {
        try {
            Cuenta cuenta = service.buscarCuenta(cuentaId);
            divisa = cuenta.getDivisa();
            if (divisa == null && (selectorDivisa == null || !selectorDivisa.isShowing()))
                selectorDivisa = DivisasUI.cuenta(this, cuentaId,
                        new com.aik1a.localcash.data.ConfiguracionRepository(helper).exigirDivisa(), this::actualizar);
            ((TextView) findViewById(R.id.nombre_cuenta)).setText(cuenta.getNombre());
            ((TextView) findViewById(R.id.saldo)).setText("Saldo: " + Dinero.formatear(service.calcularSaldo(cuentaId), divisa));
            historial = service.listarMovimientos(cuentaId);
            Collections.reverse(historial);
            List<Map<String, String>> filas = new ArrayList<>();
            DateFormat fecha = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
            for (Movimiento movimiento : historial) {
                Map<String, String> fila = new HashMap<>();
                fila.put("monto", movimiento.getTipo().name() + " · " + Dinero.formatear(movimiento.getMontoConSigno(), divisa));
                fila.put("detalle", fecha.format(new Date(movimiento.getFecha()))
                        + (movimiento.getDescripcion().isEmpty() ? "" : " · " + movimiento.getDescripcion())
                        + (movimiento.tieneUbicacion() ? "\nUbicación: " + movimiento.getUbicacion().getLatitud()
                        + ", " + movimiento.getUbicacion().getLongitud() : "\nSin ubicación"));
                filas.add(fila);
            }
            lista.setAdapter(new SimpleAdapter(this, filas, android.R.layout.simple_list_item_2,
                    new String[]{"monto", "detalle"}, new int[]{android.R.id.text1, android.R.id.text2}));
        } catch (IllegalArgumentException | SQLiteException | ArithmeticException ex) {
            Toast.makeText(this, "No se pudo abrir la cuenta.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void mostrarFormulario() {
        if (formulario != null && formulario.isShowing()) return;
        if (divisa == null) { actualizar(); return; }
        View form = getLayoutInflater().inflate(R.layout.dialog_movimiento, null);
        campos = form; ubicacion = null;
        CheckBox incluir = form.findViewById(R.id.incluir_ubicacion);
        captura = new CapturaUbicacion(this, new CapturaUbicacion.Resultado() {
            @Override public void recibido(Ubicacion valor) {
                capturando = false;
                if (formulario == null || !formulario.isShowing() || !incluir.isChecked()) return;
                ubicacion = valor;
                estadoUbicacion("Ubicación capturada: " + valor.getLatitud() + ", " + valor.getLongitud());
            }
            @Override public void noDisponible(String motivo) {
                capturando = false; ubicacion = null; estadoUbicacion(motivo);
            }
        });
        incluir.setOnCheckedChangeListener((button, checked) -> {
            captura.cancelar(); capturando = false; ubicacion = null;
            if (checked) solicitarUbicacion(); else estadoUbicacion("Se guardará sin ubicación.");
        });
        form.findViewById(R.id.reintentar_ubicacion).setOnClickListener(v -> {
            if (incluir.isChecked()) solicitarUbicacion(); else incluir.setChecked(true);
        });
        EditText monto = form.findViewById(R.id.monto);
        DivisasUI.ejemplo(monto, divisa);
        EditText descripcion = form.findViewById(R.id.descripcion);
        RadioGroup tipo = form.findViewById(R.id.tipo);
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Registrar movimiento")
                .setView(form).setNegativeButton("Cancelar", null).setPositiveButton("Guardar", null).create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            int elegido = tipo.getCheckedRadioButtonId();
            if (elegido != R.id.ingreso && elegido != R.id.egreso) {
                errorMovimiento(form, "Selecciona ingreso o egreso."); return;
            }
            try {
                service.registrar(cuentaId, elegido == R.id.ingreso ? Movimiento.Tipo.INGRESO : Movimiento.Tipo.EGRESO,
                        Dinero.parsear(monto.getText().toString(), divisa), descripcion.getText().toString(),
                        incluir.isChecked() ? ubicacion : null);
                dialog.dismiss(); actualizar();
                AccionesUI.exito(this, "Movimiento guardado");
            } catch (IllegalArgumentException ex) { errorMovimiento(form, ex.getMessage()); }
            catch (ArithmeticException ex) { errorMovimiento(form, "El movimiento excede el rango del saldo."); }
            catch (SQLiteException ex) {
                errorMovimiento(form, "No se guardó el movimiento. Intenta nuevamente.");
            }
        }));
        formulario = dialog;
        dialog.setOnDismissListener(ignored -> {
            captura.cancelar(); capturando = false; ubicacion = null; campos = null; formulario = null;
        });
        protegerDialogo(dialog).show();
    }
    private void opcionesMovimiento(Movimiento movimiento) {
        if (service.esPagoCompra(movimiento.getId())) {
            AccionesUI.error(this, "Este movimiento corresponde a una compra. Gestiona el pago desde su compra."); return;
        }
        protegerDialogo(new AlertDialog.Builder(this).setTitle("Movimiento")
                .setItems(new String[]{"Editar movimiento", "Eliminar movimiento"}, (d, elegido) -> {
                    if (elegido == 0) editarMovimiento(movimiento);
                    else AccionesUI.confirmar(this, "Eliminar movimiento", "El saldo se actualizará al eliminarlo. ¿Continuar?", () -> {
                        service.eliminarMovimiento(movimiento.getId()); actualizar(); AccionesUI.exito(this, "Movimiento eliminado");
                    });
                }).setNegativeButton("Cancelar", null).create()).show();
    }
    private void editarMovimiento(Movimiento movimiento) {
        View form = getLayoutInflater().inflate(R.layout.dialog_movimiento, null);
        form.findViewById(R.id.incluir_ubicacion).setVisibility(View.GONE);
        form.findViewById(R.id.reintentar_ubicacion).setVisibility(View.GONE);
        ((TextView)form.findViewById(R.id.estado_ubicacion)).setText(movimiento.tieneUbicacion() ? "Se conserva la ubicación." : "");
        EditText monto = form.findViewById(R.id.monto), descripcion = form.findViewById(R.id.descripcion);
        monto.setText(java.math.BigDecimal.valueOf(movimiento.getMonto(), 2).stripTrailingZeros().toPlainString());
        DivisasUI.ejemplo(monto, divisa); descripcion.setText(movimiento.getDescripcion());
        RadioGroup tipo = form.findViewById(R.id.tipo);
        tipo.check(movimiento.getTipo() == Movimiento.Tipo.INGRESO ? R.id.ingreso : R.id.egreso);
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Editar movimiento").setView(form)
                .setNegativeButton("Cancelar", null).setPositiveButton("Guardar", null).create();
        dialog.setOnShowListener(x -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            try {
                service.editarMovimiento(movimiento.getId(), tipo.getCheckedRadioButtonId() == R.id.ingreso ? Movimiento.Tipo.INGRESO : Movimiento.Tipo.EGRESO,
                        Dinero.parsear(monto.getText().toString(), divisa), descripcion.getText().toString());
                dialog.dismiss(); actualizar(); AccionesUI.exito(this, "Movimiento actualizado");
            } catch (IllegalArgumentException ex) { errorMovimiento(form, ex.getMessage()); }
            catch (ArithmeticException ex) { errorMovimiento(form, "El importe está fuera de rango."); }
            catch (SQLiteException ex) { errorMovimiento(form, "No se pudo guardar. Intenta nuevamente."); }
        }));
        protegerDialogo(dialog).show();
    }
    private void errorMovimiento(View form, String mensaje) {
        TextView error = form.findViewById(R.id.error_movimiento);
        error.setText(mensaje); error.setVisibility(View.VISIBLE);
    }
    private void estadoUbicacion(String mensaje) {
        if (campos != null) ((TextView)campos.findViewById(R.id.estado_ubicacion)).setText(mensaje);
    }
    private void solicitarUbicacion() {
        ubicacion = null;
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            estadoUbicacion("Solicitando permiso. Puedes guardar sin ubicación.");
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISO_UBICACION);
        } else iniciarCaptura();
    }
    private void iniciarCaptura() {
        if (campos == null || formulario == null || !formulario.isShowing()
                || !((CheckBox)campos.findViewById(R.id.incluir_ubicacion)).isChecked()) return;
        capturando = true; estadoUbicacion("Buscando ubicación (máximo 15 segundos). Puedes guardar sin ubicación.");
        captura.iniciar();
    }
    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (requestCode != PERMISO_UBICACION || campos == null || formulario == null || !formulario.isShowing()) return;
        if (!((CheckBox)campos.findViewById(R.id.incluir_ubicacion)).isChecked()) return;
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) iniciarCaptura();
        else { ubicacion = null; estadoUbicacion("Permiso denegado. Puedes guardar sin ubicación."); }
    }
    @Override protected void onPause() {
        if (captura != null) captura.cancelar();
        if (capturando) { capturando = false; estadoUbicacion("Captura interrumpida. Puedes reintentar o guardar sin ubicación."); }
        super.onPause();
    }
    @Override protected void onDestroy() { if (formulario != null) formulario.dismiss(); if (captura != null) captura.cancelar();
        if (helper != null) helper.close(); super.onDestroy(); }
}



