package com.aik1a.localcash.presentation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.*;
import org.osmdroid.util.GeoPoint;

/** A fresh, foreground-only fix; no stale position is labelled as current. */
final class UbicacionMapa implements LocationListener {
    interface Resultado { void recibido(GeoPoint punto); void error(String mensaje); }
    private final Activity activity;
    private final Resultado resultado;
    private final LocationManager manager;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean activa;
    private final Runnable timeout = () -> fallar("No se obtuvo señal GPS en 15 segundos. Puedes navegar por el mapa y reintentar Mi ubicación.");
    UbicacionMapa(Activity activity, Resultado resultado) {
        this.activity = activity; this.resultado = resultado;
        manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
    }
    void iniciar() {
        cancelar();
        boolean fine = activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean coarse = activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (!fine && !coarse) { resultado.error("Permiso de ubicación denegado. Puedes explorar el mapa sin GPS."); return; }
        try {
            boolean gps = manager != null && fine && manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean red = manager != null && manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!gps && !red) { resultado.error("Activa la ubicación del teléfono. Para GPS sin internet, permite ubicación precisa."); return; }
            activa = true;
            if (gps) manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this,Looper.getMainLooper());
            if (red) manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,this,Looper.getMainLooper());
            handler.postDelayed(timeout,15000);
        } catch (SecurityException | IllegalArgumentException ex) { fallar("No se pudo acceder a la ubicación. Revisa el permiso y reintenta."); }
    }
    @Override public void onLocationChanged(Location location) {
        if (!activa) return;
        long edad = SystemClock.elapsedRealtimeNanos() - location.getElapsedRealtimeNanos();
        if (edad < 0 || edad > 60_000_000_000L) return;
        double lat = location.getLatitude(), lon = location.getLongitude();
        if (Double.isNaN(lat) || Double.isNaN(lon) || Math.abs(lat)>90 || Math.abs(lon)>180) { fallar("El teléfono devolvió coordenadas inválidas."); return; }
        cancelar(); resultado.recibido(new GeoPoint(lat,lon));
    }
    private void fallar(String mensaje) { cancelar(); resultado.error(mensaje); }
    void cancelar() {
        activa = false; handler.removeCallbacks(timeout);
        if (manager != null) try { manager.removeUpdates(this); } catch (SecurityException ignored) { }
    }
    @Override public void onProviderDisabled(String provider) { }
    @Override public void onProviderEnabled(String provider) { }
    @Override public void onStatusChanged(String provider, int status, Bundle extras) { }
}
