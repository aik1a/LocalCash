package com.aik1a.localcash.presentation;

import android.app.Instrumentation;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.widget.EditText;
import android.widget.TextView;
import com.aik1a.localcash.domain.Divisa;

/** Vistas en memoria: no abre pantallas privadas ni altera la sesión biométrica. */
public final class FeedbackChecks {
    private static int checks;
    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
        checks++;
    }
    public static void ejecutar(Instrumentation runner) {
        Bundle result = new Bundle();
        Throwable[] failure = new Throwable[1];
        runner.runOnMainSync(() -> {
            try {
                ContextThemeWrapper context = new ContextThemeWrapper(runner.getTargetContext(), android.R.style.Theme_Material_Light);
                EditText campo = new EditText(context);
                check(AccionesUI.leerMonto(campo, Divisa.CLP, false, "el monto") == null, "Vacío rechazado");
                check(campo.getError().toString().contains("Ingresa el monto"), "Motivo de vacío");
                campo.setText("0");
                check(AccionesUI.leerMonto(campo, Divisa.CLP, false, "el monto") == null, "Movimiento cero rechazado");
                check(campo.getError().toString().contains("mayor que cero"), "Motivo de cero");
                check(AccionesUI.leerMonto(campo, Divisa.CLP, true, "el precio unitario") == 0, "Precio cero admitido");
                check(campo.getError() == null, "Error anterior limpiado");
                campo.setText("-1");
                check(AccionesUI.leerMonto(campo, Divisa.CLP, true, "el monto de Ana") == null, "Cuota negativa rechazada");
                check(campo.getError().toString().contains("Ana") && campo.getError().toString().contains("negativo"), "Persona y motivo");
                campo.setText("1,5");
                check(AccionesUI.leerMonto(campo, Divisa.CLP, true, "el precio") == null, "CLP fraccionario rechazado");
                check(campo.getError().toString().contains("entero"), "Precisión explicada");
                campo.setText("10,50");
                check(AccionesUI.leerMonto(campo, Divisa.USD, true, "el precio") == 1050, "USD exacto");
                campo.setText("100");
                check(AccionesUI.leerMonto(campo, Divisa.JPY, false, "el monto") == 10000, "JPY exacto");
                campo.setText("99999999999999999999999999999999");
                check(AccionesUI.leerMonto(campo, Divisa.USD, false, "el monto") == null, "Fuera de capacidad");
                check(campo.getError().toString().contains("grande"), "Límite explicado");
                TextView aviso = new TextView(context);
                AccionesUI.errorTexto(aviso, "Falta un producto");
                check("Falta un producto".contentEquals(aviso.getText()), "Aviso general");
                AccionesUI.errorTexto(aviso, "");
                check(aviso.getText().length() == 0, "Aviso limpiado");
            } catch (Throwable error) { failure[0] = error; }
        });
        if (failure[0] != null) {
            result.putString("stream", "FEEDBACK_FAIL " + failure[0]); runner.finish(0, result);
        } else {
            result.putString("stream", "FEEDBACK_PASS " + checks + " comprobaciones de validación y mensajes sobre vistas Android en memoria.");
            runner.finish(-1, result);
        }
    }
}
