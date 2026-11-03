package com.aik1a.localcash.presentation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.*;
import com.aik1a.localcash.domain.Ubicacion;

/** Captura acotada mientras el formulario está visible; sin caché ni tracking. */
final class CapturaUbicacion implements LocationListener {
    interface Resultado {
        void recibido(Ubicacion ubicacion);
        void noDisponible(String motivo);
    }
    private final Activity activity;
    private final LocationManager manager;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Resultado resultado;
    private boolean activa;
    private final Runnable timeout = () -> fallar("No se obtuvo ubicación en 15 segundos. Puedes guardar sin ubicación.");
    CapturaUbicacion(Activity activity, Resultado resultado) {
        this.activity = activity; this.resultado = resultado;
        manager = (LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);
    }
    void iniciar() {
        cancelar();
        boolean fine = activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean coarse = activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (!fine && !coarse) { resultado.noDisponible("Permiso denegado. Puedes guardar sin ubicación."); return; }
        if (manager == null) { resultado.noDisponible("Ubicación no disponible. Puedes guardar sin ubicación."); return; }
        try {
            boolean red = (fine || coarse) && manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            boolean gps = fine && manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!red && !gps) { resultado.noDisponible("Activa la ubicación del teléfono y reintenta. Puedes guardar sin ubicación."); return; }
            activa = true;
            // El primer proveedor que responda termina la captura; no esperar solo al GPS en interiores.
            if (red) manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this, Looper.getMainLooper());
            if (gps) manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this, Looper.getMainLooper());
            handler.postDelayed(timeout, 15000);
        } catch (SecurityException ex) { fallar("Permiso no disponible. Puedes guardar sin ubicación."); }
        catch (IllegalArgumentException ex) { fallar("Proveedor no disponible. Puedes guardar sin ubicación."); }
    }
    @Override public void onLocationChanged(Location location) {
        if (!activa) return;
        try {
            Ubicacion ubicacion = new Ubicacion(location.getLatitude(), location.getLongitude());
            cancelar(); resultado.recibido(ubicacion);
        } catch (IllegalArgumentException ex) { fallar("Coordenadas inválidas. Puedes guardar sin ubicación."); }
    }
    @Override public void onProviderDisabled(String provider) {
        // Otro proveedor puede seguir activo. El plazo acotado resuelve la falta de señal.
    }
    @Override public void onProviderEnabled(String provider) { }
    @Override public void onStatusChanged(String provider, int status, Bundle extras) { }
    private void fallar(String motivo) { cancelar(); resultado.noDisponible(motivo); }
    void cancelar() {
        activa = false; handler.removeCallbacks(timeout);
        if (manager != null) {
            try { manager.removeUpdates(this); }
            catch (SecurityException ex) { /* El permiso puede revocarse durante la captura; ya no está activa. */ }
        }
    }
}
