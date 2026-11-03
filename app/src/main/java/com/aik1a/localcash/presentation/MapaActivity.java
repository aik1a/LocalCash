package com.aik1a.localcash.presentation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.aik1a.localcash.R;
import com.aik1a.localcash.application.MovimientoService;
import com.aik1a.localcash.data.LocalCashDbHelper;
import com.aik1a.localcash.domain.*;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.*;
import org.osmdroid.views.overlay.Marker;
import java.io.*;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.*;

public final class MapaActivity extends ActividadProtegida {
    private static final int PERMISO_UBICACION = 620, IMPORTAR = 621;
    private static final ExecutorService ARCHIVOS = Executors.newSingleThreadExecutor();
    private final List<GeoPoint> puntos = new ArrayList<>();
    private MapaLocalView mapa;
    private LinearLayout contenedor;
    private UbicacionMapa captura;
    private Marker posicion;
    private boolean reanudado, visible, pedirInicial, buscando;
    private Bundle estadoMapa;
    @Override protected void crearContenido(Bundle state) {
        estadoMapa = state; pedirInicial = state == null;
        contenedor = new LinearLayout(this); contenedor.setOrientation(LinearLayout.VERTICAL);
        contenedor.setPadding(24,24,24,24);
        setContentView(contenedor);
        mostrarCarga("Preparando mapas offline. La primera apertura puede tardar mientras se instala la cartografía.");
        android.content.Context contexto = getApplicationContext();
        ARCHIVOS.execute(() -> {
            try {
                MapasVectoriales.preparar(contexto);
                runOnUiThread(() -> { if (!isDestroyed() && !isFinishing()) abrirMapa(); });
            } catch (IOException | RuntimeException ex) {
                runOnUiThread(() -> { if (!isDestroyed() && !isFinishing()) mostrarError("No se pudo preparar el mapa offline. " + ex.getMessage()); });
            }
        });
    }
    private void mostrarCarga(String mensaje) {
        contenedor.removeAllViews();
        TextView aviso = new TextView(this); aviso.setText(mensaje); contenedor.addView(aviso);
        contenedor.addView(new ProgressBar(this));
        Button volver = new Button(this); volver.setText("Volver a movimientos"); volver.setOnClickListener(v -> finish()); contenedor.addView(volver);
    }
    private void mostrarError(String mensaje) {
        contenedor.removeAllViews(); TextView aviso = new TextView(this); aviso.setText(mensaje); contenedor.addView(aviso);
        Button volver = new Button(this); volver.setText("Volver a movimientos"); volver.setOnClickListener(v -> finish()); contenedor.addView(volver);
    }
    private void abrirMapa() {
        try {
            long id = getIntent().getLongExtra(CuentaActivity.CUENTA_ID,-1);
            String nombre; Divisa divisa; List<Movimiento> movimientos;
            try (LocalCashDbHelper helper = new LocalCashDbHelper(this)) {
                MovimientoService service = new MovimientoService(helper);
                nombre = service.buscarCuenta(id).getNombre(); divisa = service.buscarCuenta(id).getDivisa(); movimientos = service.listarMovimientos(id);
            }
            Configuration.getInstance().setUserAgentValue(getPackageName());
            Configuration.getInstance().setOsmdroidBasePath(new File(getFilesDir(),"osmdroid"));
            Configuration.getInstance().setOsmdroidTileCache(new File(getCacheDir(),"osmdroid-vector"));
            contenedor.removeAllViews(); getLayoutInflater().inflate(R.layout.activity_mapa,contenedor,true);
            Pantalla.aplicarMargenes(findViewById(R.id.root));
            mapa = findViewById(R.id.map);
            mapa.setUseDataConnection(false); mapa.setMultiTouchControls(true);
            mapa.setMinZoomLevel(3.0); mapa.setMaxZoomLevel(20.0);
            mapa.setHorizontalMapRepetitionEnabled(false); mapa.setVerticalMapRepetitionEnabled(false);
            ((TextView)findViewById(R.id.titulo_mapa)).setText("Movimientos · " + nombre);
            puntos.clear();
            for (Movimiento movimiento : movimientos) {
                if (!movimiento.tieneUbicacion()) continue;
                Ubicacion u = movimiento.getUbicacion(); GeoPoint punto = new GeoPoint(u.getLatitud(),u.getLongitud()); puntos.add(punto);
                Marker marker = new Marker(mapa); marker.setPosition(punto); marker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);
                String detalle = movimiento.getTipo().name()+" · "+Dinero.formatear(movimiento.getMontoConSigno(),divisa)+" · "+movimiento.getDescripcion()+" · "+DateFormat.getDateTimeInstance().format(new Date(movimiento.getFecha()))+"\n"+u.getLatitud()+", "+u.getLongitud();
                marker.setTitle(detalle); marker.setSnippet("Movimiento");
                marker.setOnMarkerClickListener((m,v) -> { ((TextView)findViewById(R.id.detalle_marcador)).setText(detalle); return true; });
                mapa.getOverlays().add(marker);
            }
            ((TextView)findViewById(R.id.estado_mapa)).setText(puntos.isEmpty() ? "Sin movimientos con ubicación en esta cuenta." : puntos.size()+" movimiento(s) con ubicación.");
            TextView cobertura = findViewById(R.id.cobertura_mapa);
            cobertura.setText("Base mundial offline · Calles de Chile incluidas" + (MapasVectoriales.tieneAdicional(this) ? " · Mapa adicional importado" : "") + ". Para calles de otros países, importa su archivo .map.");
            findViewById(R.id.volver).setOnClickListener(v -> finish());
            findViewById(R.id.zoom_mas).setOnClickListener(v -> mapa.getController().setZoom(Math.min(20,mapa.getZoomLevelDouble()+1)));
            findViewById(R.id.zoom_menos).setOnClickListener(v -> mapa.getController().setZoom(Math.max(3,mapa.getZoomLevelDouble()-1)));
            findViewById(R.id.centrar_mapa).setOnClickListener(v -> solicitarUbicacion());
            findViewById(R.id.ver_movimientos_mapa).setOnClickListener(v -> { if(captura!=null)captura.cancelar(); buscando=false; verMovimientos(); });
            findViewById(R.id.importar_mapa).setOnClickListener(v -> startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT).setType("*/*").addCategory(Intent.CATEGORY_OPENABLE),IMPORTAR));
            TextView atribucion = findViewById(R.id.atribucion_mapa);
            android.text.SpannableString link = new android.text.SpannableString(atribucion.getText());
            link.setSpan(new android.text.style.URLSpan("https://www.openstreetmap.org/copyright"),0,link.length(),0);
            atribucion.setText(link); atribucion.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
            captura = new UbicacionMapa(this,new UbicacionMapa.Resultado() {
                @Override public void recibido(GeoPoint punto) {
                    buscando = false;
                    if (!visible || mapa == null) return;
                    if (posicion != null) mapa.getOverlays().remove(posicion);
                    posicion = new Marker(mapa); posicion.setPosition(punto); posicion.setTitle("Mi ubicación"); posicion.setSnippet("Posición actual del teléfono");
                    android.graphics.drawable.GradientDrawable puntoActual = new android.graphics.drawable.GradientDrawable();
                    int tamano = Math.round(18 * getResources().getDisplayMetrics().density);
                    puntoActual.setShape(android.graphics.drawable.GradientDrawable.OVAL); puntoActual.setColor(0xff1565c0);
                    puntoActual.setStroke(Math.max(2,tamano/8),0xffffffff); puntoActual.setSize(tamano,tamano); posicion.setIcon(puntoActual);
                    posicion.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_CENTER); mapa.getOverlays().add(0,posicion);
                    mapa.getController().setZoom(15.0); mapa.getController().setCenter(punto); mapa.invalidate();
                    estadoUbicacion("Mi ubicación: " + punto.getLatitude() + ", " + punto.getLongitude());
                }
                @Override public void error(String mensaje) { buscando=false; estadoUbicacion(mensaje); }
            });
            if (estadoMapa != null && estadoMapa.containsKey("map_lat")) {
                mapa.getController().setZoom(Math.max(3,Math.min(20,estadoMapa.getDouble("map_zoom",5))));
                mapa.getController().setCenter(new GeoPoint(estadoMapa.getDouble("map_lat"),estadoMapa.getDouble("map_lon")));
            } else verMovimientos();
            if (visible && ((LocalCashApplication)getApplication()).autenticada()) {
                mapa.onResume(); reanudado=true;
                if(pedirInicial) { pedirInicial=false; solicitarUbicacion(); }
            }
        } catch (IllegalArgumentException | SQLiteException | android.view.InflateException ex) {
            if(mapa!=null) { mapa.onDetach(); mapa=null; }
            mostrarError("No se pudo abrir el mapa offline. " + ex.getMessage());
        }
    }
    private void verMovimientos() {
        if(mapa==null)return;
        if(puntos.isEmpty()) { mapa.getController().setZoom(3.0); mapa.getController().setCenter(new GeoPoint(0,0)); return; }
        mapa.getController().setCenter(puntos.get(0)); mapa.getController().setZoom(15.0);
        if(puntos.size()>1) mapa.post(() -> { if(mapa!=null)mapa.zoomToBoundingBox(BoundingBox.fromGeoPoints(puntos),false,80,18.0,null); });
    }
    private void estadoUbicacion(String texto) { TextView estado=findViewById(R.id.detalle_marcador); if(estado!=null)estado.setText(texto); }
    private void solicitarUbicacion() {
        if(captura==null)return;
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISO_UBICACION);
        } else { buscando=true; estadoUbicacion("Buscando tu ubicación actual (máximo 15 segundos)…"); captura.iniciar(); }
    }
    @Override public void onRequestPermissionsResult(int code,String[] permisos,int[] resultados) {
        super.onRequestPermissionsResult(code,permisos,resultados);
        if(code!=PERMISO_UBICACION)return;
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED) { if(visible)solicitarUbicacion();else pedirInicial=true; }
        else estadoUbicacion("Permiso de ubicación denegado. Puedes explorar el mapa y ver tus movimientos.");
    }
    @Override protected void onActivityResult(int request,int result,Intent data) {
        super.onActivityResult(request,result,data);
        if(request!=IMPORTAR || result!=RESULT_OK || data==null || data.getData()==null)return;
        Uri uri=data.getData(); android.content.Context contexto=getApplicationContext();
        estadoUbicacion("Importando cartografía offline…");
        ARCHIVOS.execute(() -> {
            try { MapasVectoriales.importar(contexto,uri); runOnUiThread(() -> { if(!isDestroyed()&&!isFinishing())recreate(); }); }
            catch(IOException | RuntimeException ex) { runOnUiThread(() -> { if(!isDestroyed())estadoUbicacion("No se importó el mapa. "+ex.getMessage()); }); }
        });
    }
    @Override protected void onResume() {
        visible=true; super.onResume();
        if(mapa!=null && ((LocalCashApplication)getApplication()).autenticada()) {
            if(!reanudado){mapa.onResume();reanudado=true;}
            if(pedirInicial){pedirInicial=false;solicitarUbicacion();}
        }
    }
    @Override protected void onPause() {
        visible=false; if(captura!=null)captura.cancelar();
        if(buscando){buscando=false;estadoUbicacion("Búsqueda interrumpida. Pulsa Mi ubicación para reintentar.");}
        if(mapa!=null&&reanudado){mapa.onPause();reanudado=false;} super.onPause();
    }
    @Override protected void onDestroy() { if(captura!=null)captura.cancelar();if(mapa!=null){mapa.onDetach();mapa=null;}super.onDestroy(); }
    @Override protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        if(mapa!=null){state.putDouble("map_zoom",mapa.getZoomLevelDouble());state.putDouble("map_lat",mapa.getMapCenter().getLatitude());state.putDouble("map_lon",mapa.getMapCenter().getLongitude());}
    }
}
