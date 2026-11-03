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


    @Override protected void crearContenido(Bundle state) {
        
        setContentView(R.layout.activity_cuenta);
        Pantalla.aplicarMargenes(findViewById(R.id.root));
        cuentaId = getIntent().getLongExtra(CUENTA_ID, -1);
        helper = new LocalCashDbHelper(this);
        service = new MovimientoService(helper);
        findViewById(R.id.abrir_mapa).setOnClickListener(v -> startActivity(new android.content.Intent(this, MapaActivity.class).putExtra(CUENTA_ID, cuentaId)));
        findViewById(R.id.ver_compras_cuenta).setOnClickListener(v -> startActivity(
                new android.content.Intent(this, ComprasActivity.class).putExtra(CUENTA_ID, cuentaId)));
        findViewById(R.id.opciones_cuenta).setOnClickListener(v -> opcionesCuenta());
        lista = findViewById(R.id.movimientos);
        lista.setEmptyView(findViewById(R.id.vacio));
        findViewById(R.id.volver).setOnClickListener(v -> finish());
        findViewById(R.id.registrar_movimiento).setOnClickListener(v -> mostrarFormulario());
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
            List<Movimiento> historial = service.listarMovimientos(cuentaId);
            Collections.reverse(historial);
            List<Map<String, String>> filas = new ArrayList<>();
            DateFormat fecha = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
            for (Movimiento movimiento : historial) {
                Map<String, String> fila = new HashMap<>();
                String tipo = movimiento.getTipo() == Movimiento.Tipo.INGRESO ? "Ingreso" : "Egreso";
                fila.put("titulo", movimiento.getDescripcion().isEmpty() ? tipo : movimiento.getDescripcion());
                fila.put("fecha", tipo + " · " + fecha.format(new Date(movimiento.getFecha())));
                fila.put("monto", Dinero.formatear(movimiento.getMontoConSigno(), divisa));
                fila.put("detalle", movimiento.tieneUbicacion() ? "Ubicación: " + movimiento.getUbicacion().getLatitud()
                        + ", " + movimiento.getUbicacion().getLongitud() : "Sin ubicación");
                filas.add(fila);
            }
            lista.setAdapter(new SimpleAdapter(this, filas, R.layout.row_movimiento,
                    new String[]{"monto", "detalle", "titulo", "fecha"},
                    new int[]{android.R.id.text1, android.R.id.text2, R.id.titulo_movimiento, R.id.fecha_movimiento}) {
                @Override public View getView(int position, View convertView, android.view.ViewGroup parent) {
                    View fila = super.getView(position, convertView, parent);
                    boolean ingreso = historial.get(position).getTipo() == Movimiento.Tipo.INGRESO;
                    ((TextView) fila.findViewById(android.R.id.text1)).setTextColor(getColor(
                            ingreso ? R.color.cuentas_ingreso : R.color.cuentas_error));
                    ((ImageView) fila.findViewById(R.id.icono_movimiento)).setImageResource(
                            ingreso ? R.drawable.cuentas_ic_ingreso : R.drawable.cuentas_ic_egreso);
                    return fila;
                }
            });
        } catch (IllegalArgumentException | SQLiteException | ArithmeticException ex) {
            Toast.makeText(this, "No se pudo abrir la cuenta.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void opcionesCuenta() {
        try {
            Cuenta cuenta = service.buscarCuenta(cuentaId);
            protegerDialogo(new AlertDialog.Builder(CuentasVisual.contexto(this)).setTitle(cuenta.getNombre())
                    .setItems(new String[]{"Cambiar nombre", "Eliminar cuenta"}, (dialog, opcion) -> {
                        if (opcion == 0) AccionesUI.nombre(this, "Cambiar nombre de la cuenta", cuenta.getNombre(),
                                nombre -> service.renombrarCuenta(cuentaId, nombre), this::actualizar);
                        else AccionesUI.eliminar(this, "¿Eliminar " + cuenta.getNombre() + "? Solo se eliminará si no tiene movimientos.",
                                () -> service.eliminarCuenta(cuentaId), this::finish);
                    }).setNegativeButton("Cancelar", null).create()).show();
        } catch (IllegalArgumentException | SQLiteException ex) {
            Toast.makeText(this, "No se pudo abrir la cuenta.", Toast.LENGTH_LONG).show();
        }
    }
    private void mostrarFormulario() {
        if (formulario != null && formulario.isShowing()) return;
        if (divisa == null) { actualizar(); return; }
        View form = getLayoutInflater().cloneInContext(CuentasVisual.contexto(this)).inflate(R.layout.dialog_movimiento, null);
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
        AlertDialog dialog = new AlertDialog.Builder(CuentasVisual.contexto(this)).setTitle("Registrar movimiento")
                .setView(form).setNegativeButton("Cancelar", null).setPositiveButton("Guardar", null).create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            int elegido = tipo.getCheckedRadioButtonId();
            if (elegido != R.id.ingreso && elegido != R.id.egreso) {
                AccionesUI.aviso(this, "Selecciona el tipo", "Elige Ingreso o Egreso para indicar cómo afecta este movimiento al saldo."); return;
            }
            Long montoIngresado = AccionesUI.leerMonto(monto, divisa, false, "el monto del movimiento");
            if (montoIngresado == null) return;
            if (incluir.isChecked() && ubicacion == null) {
                protegerDialogo(new AlertDialog.Builder(CuentasVisual.contexto(this)).setTitle("Aún no hay ubicación")
                        .setMessage("Puedes seguir esperando o guardar el movimiento sin ubicación.")
                        .setNegativeButton("Volver", null)
                        .setPositiveButton("Guardar sin ubicación", (aviso, opcion) -> {
                            incluir.setChecked(false);
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
                        }).create()).show();
                return;
            }
            try {
                service.registrar(cuentaId, elegido == R.id.ingreso ? Movimiento.Tipo.INGRESO : Movimiento.Tipo.EGRESO,
                        montoIngresado, descripcion.getText().toString(),
                        incluir.isChecked() ? ubicacion : null);
                dialog.dismiss(); AccionesUI.exito(this, "Movimiento guardado."); actualizar();
            } catch (IllegalArgumentException ex) {
                String motivo = "La cuenta no existe.".equals(ex.getMessage()) || "Define la divisa de esta cuenta primero.".equals(ex.getMessage())
                        ? ex.getMessage() : "Revisa la cuenta y vuelve a intentar. Los datos del formulario se conservan.";
                AccionesUI.aviso(this, "No se pudo guardar el movimiento", motivo);
            } catch (ArithmeticException ex) { AccionesUI.errorCampo(monto, "El importe supera el límite admitido por la cuenta. Usa otro monto."); }
            catch (SQLiteException ex) {
                AccionesUI.aviso(this, "No se guardó el movimiento", "No se completó la operación. Los datos del formulario se conservan; intenta de nuevo.");
            }
        }));
        formulario = dialog;
        dialog.setOnDismissListener(ignored -> {
            captura.cancelar(); capturando = false; ubicacion = null; campos = null; formulario = null;
        });
        protegerDialogo(dialog).show();
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
