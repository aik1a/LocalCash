package com.aik1a.localcash.presentation;

import android.app.AlertDialog;
import android.database.sqlite.SQLiteException;
import android.widget.EditText;
import android.widget.Toast;

/** Confirmaciones y mensajes compartidos; las operaciones quedan en los servicios. */
final class AccionesUI {
    interface Accion { void ejecutar(); }
    interface Nombre { void guardar(String nombre); }
    private AccionesUI() { }
    static void exito(ActividadProtegida activity, String texto) {
        Toast.makeText(activity, texto, Toast.LENGTH_SHORT).show();
    }
    static void error(ActividadProtegida activity, String texto) {
        activity.protegerDialogo(new AlertDialog.Builder(activity).setTitle("No se pudo completar")
                .setMessage(texto).setPositiveButton("Entendido", null).create()).show();
    }
    static void confirmar(ActividadProtegida activity, String titulo, String mensaje, Accion accion) {
        AlertDialog dialog = new AlertDialog.Builder(activity).setTitle(titulo).setMessage(mensaje)
                .setNegativeButton("Cancelar", null).setPositiveButton("Eliminar", null).create();
        dialog.setOnShowListener(x -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            try { accion.ejecutar(); dialog.dismiss(); }
            catch (IllegalArgumentException ex) { dialog.setMessage(ex.getMessage()); }
            catch (ArithmeticException ex) { dialog.setMessage("El importe está fuera de rango."); }
            catch (SQLiteException ex) { dialog.setMessage("No se pudo guardar el cambio. Intenta nuevamente."); }
        }));
        activity.protegerDialogo(dialog).show();
    }
    static void nombre(ActividadProtegida activity, String titulo, String actual, Nombre accion) {
        EditText campo = new EditText(activity); campo.setSingleLine(true); campo.setText(actual);
        campo.setHint("Nombre"); campo.setSelectAllOnFocus(true);
        int padding = (int)(24 * activity.getResources().getDisplayMetrics().density);
        android.widget.LinearLayout contenido = new android.widget.LinearLayout(activity);
        contenido.setPadding(padding, 0, padding, 0); contenido.addView(campo,
                new android.widget.LinearLayout.LayoutParams(-1, -2));
        AlertDialog dialog = new AlertDialog.Builder(activity).setTitle(titulo).setView(contenido)
                .setNegativeButton("Cancelar", null).setPositiveButton("Guardar", null).create();
        dialog.setOnShowListener(x -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            try { accion.guardar(campo.getText().toString()); dialog.dismiss(); }
            catch (IllegalArgumentException ex) { campo.setError(ex.getMessage()); }
            catch (SQLiteException ex) { campo.setError("No se pudo guardar. Intenta nuevamente."); }
        }));
        activity.protegerDialogo(dialog).show();
    }
}
