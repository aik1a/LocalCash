package com.aik1a.localcash.presentation;

import android.app.Activity;
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
            cuotaUsuario.setContentDescription("Tu parte"); DivisasUI.ejemplo(cuotaUsuario, divisa);
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
        } catch (IllegalArgumentException | ArithmeticException | SQLiteException ex) {
            mostrarError("No se pudo abrir el reparto.");
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
        try (LocalCashDbHelper helper = new LocalCashDbHelper(this)) {
            List<Long> ids = new ArrayList<>(), montos = new ArrayList<>();
            for (int i = 0; i < contactos.size(); i++) if (seleccion.get(i).isChecked()) {
                ids.add(contactos.get(i).getId());
                if (personalizado()) montos.add(Dinero.parsear(cuotas.get(i).getText().toString(), divisa));
            }
            DeudaService service = new DeudaService(helper);
            if (personalizado()) service.repartirPersonalizadoConUsuario(compraId, ids, montos,
                    Dinero.parsear(cuotaUsuario.getText().toString(), divisa));
            else service.repartirEquitativoConUsuario(compraId, ids);
            montoUsuario = service.buscarMontoUsuario(compraId);
            mostrarDeudas(service.listarDeudas(compraId));
            mostrarError("");
        } catch (IllegalArgumentException ex) { mostrarError(ex.getMessage()); }
        catch (ArithmeticException ex) { mostrarError("La suma supera el rango permitido."); }
        catch (SQLiteException ex) { mostrarError("No se guardó el reparto. Revisa los datos e intenta de nuevo."); }
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
        ((TextView)findViewById(R.id.reparto_info)).setText("Reparto registrado · deuda pendiente por contacto");
        for (Deuda deuda : deudas) {
            TextView fila = (TextView)getLayoutInflater().inflate(R.layout.row_detalle, filas, false);
            fila.setText(deuda.getContacto().getNombre() + ": "
                    + Dinero.formatear(deuda.getMonto(), divisa) + " · " + deuda.getEstado());
            filas.addView(fila);
        }
    }
    private void mostrarError(String mensaje) { ((TextView)findViewById(R.id.error_reparto)).setText(mensaje); }
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


