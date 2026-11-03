package com.aik1a.localcash.presentation;

import android.app.AlertDialog;
import android.graphics.Rect;
import android.view.View;
import android.widget.TextView;
import com.aik1a.localcash.R;
import com.aik1a.localcash.domain.Dinero;
import com.aik1a.localcash.domain.Divisa;
import android.database.sqlite.SQLiteException;
import android.widget.EditText;
import android.widget.Toast;

final class AccionesUI {
    interface GuardarNombre { void guardar(String nombre); }
    private AccionesUI() { }
    static void visible(View vista) {
        vista.post(() -> vista.requestRectangleOnScreen(new Rect(0, 0, vista.getWidth(), vista.getHeight()), false));
    }
    static void errorCampo(EditText campo, String mensaje) {
        campo.setError(mensaje); campo.requestFocus(); visible(campo);
    }
    static void errorTexto(TextView texto, String mensaje) {
        texto.setText(mensaje);
        if (!mensaje.isEmpty()) visible(texto);
    }
    static void aviso(ActividadProtegida activity, String titulo, String mensaje) {
        aviso(activity, titulo, mensaje, () -> { });
    }
    static void aviso(ActividadProtegida activity, String titulo, String mensaje, Runnable alCerrar) {
        AlertDialog dialog = new AlertDialog.Builder(CuentasVisual.contexto(activity)).setTitle(titulo).setMessage(mensaje)
                .setPositiveButton("Entendido", null).create();
        dialog.setOnDismissListener(ignored -> alCerrar.run());
        activity.protegerDialogo(dialog).show();
    }
    static void exito(android.content.Context contexto, String mensaje) {
        Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show();
    }

    static Long leerMonto(EditText campo, Divisa divisa, boolean permiteCero, String descripcion) {
        campo.setError(null);
        if (campo.getText().toString().trim().isEmpty()) { errorCampo(campo, "Ingresa " + descripcion + "."); return null; }
        try {
            long monto = Dinero.parsear(campo.getText().toString(), divisa);
            if (monto < 0 || (!permiteCero && monto == 0)) {
                errorCampo(campo, "Revisa " + descripcion + ": " + (permiteCero ? "no puede ser negativo." : "debe ser mayor que cero."));
                return null;
            }
            return monto;
        } catch (IllegalArgumentException ex) { errorCampo(campo, "Revisa " + descripcion + ": " + ex.getMessage()); return null; }
    }
    static void nombre(ActividadProtegida activity, String titulo, String actual, GuardarNombre guardar, Runnable despues) {
        EditText campo = new EditText(CuentasVisual.contexto(activity)); campo.setSingleLine(true); campo.setText(actual);
        campo.setContentDescription("Nombre"); campo.setSelectAllOnFocus(true);
        AlertDialog dialog = new AlertDialog.Builder(CuentasVisual.contexto(activity)).setTitle(titulo).setView(CuentasVisual.enmarcarNombre(activity, campo))
                .setNegativeButton("Cancelar", null).setPositiveButton("Guardar", null).create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            try { guardar.guardar(campo.getText().toString()); dialog.dismiss(); exito(activity, "Nombre actualizado."); despues.run(); }
            catch (IllegalArgumentException ex) { errorCampo(campo, ex.getMessage()); }
            catch (SQLiteException ex) { errorCampo(campo, "No se guardó el nombre. Intenta de nuevo."); }
        }));
        activity.protegerDialogo(dialog).show();
    }
    static void eliminar(ActividadProtegida activity, String mensaje, Runnable eliminar, Runnable despues) {
        activity.protegerDialogo(new AlertDialog.Builder(CuentasVisual.contexto(activity), R.style.Cuentas_DialogoPeligro).setTitle("Confirmar eliminación")
                .setMessage(mensaje).setNegativeButton("Cancelar", null).setPositiveButton("Eliminar", (dialog, which) -> {
                    try { eliminar.run(); exito(activity, "Eliminado correctamente."); despues.run(); }
                    catch (IllegalArgumentException ex) { aviso(activity, "No se puede eliminar", ex.getMessage()); }
                    catch (SQLiteException ex) { aviso(activity, "No se pudo eliminar", "No se completó la operación. Tus datos se conservan. Vuelve a abrir el registro e intenta de nuevo."); }
                }).create()).show();
    }
}
