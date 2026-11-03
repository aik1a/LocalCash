package com.aik1a.localcash.presentation;

import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.widget.*;
import android.view.*;
import android.database.sqlite.SQLiteException;
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

public final class MapaActivity extends ActividadProtegida {
    private MapaLocalView mapa;
    private boolean reanudado;
    @Override protected void crearContenido(Bundle state) {
        try {
            long id=getIntent().getLongExtra(CuentaActivity.CUENTA_ID,-1);
            String nombre;Divisa divisa;List<Movimiento> movimientos;
            try(LocalCashDbHelper helper=new LocalCashDbHelper(this)) {
                MovimientoService service=new MovimientoService(helper);
                nombre=service.buscarCuenta(id).getNombre();divisa=service.buscarCuenta(id).getDivisa();movimientos=service.listarMovimientos(id);
            }
            Configuration.getInstance().setUserAgentValue(getPackageName());
            Configuration.getInstance().setOsmdroidBasePath(new File(getFilesDir(),"osmdroid"));
            Configuration.getInstance().setOsmdroidTileCache(new File(getCacheDir(),"osmdroid"));
            AtlasOffline.preparar(this);
            setContentView(R.layout.activity_mapa);Pantalla.aplicarMargenes(findViewById(R.id.root));
            mapa=findViewById(R.id.map);
            mapa.setUseDataConnection(false);mapa.setMultiTouchControls(true);
            mapa.setMinZoomLevel(14.0);mapa.setMaxZoomLevel(16.0);
            mapa.setHorizontalMapRepetitionEnabled(false);mapa.setVerticalMapRepetitionEnabled(false);
            mapa.setScrollableAreaLimitDouble(new BoundingBox(AtlasOffline.NORTE,AtlasOffline.ESTE,AtlasOffline.SUR,AtlasOffline.OESTE));
            mapa.getController().setZoom(state==null?15.0:Math.max(14,Math.min(16,state.getDouble("map_zoom",15))));
            double lat=state==null?-33.450:state.getDouble("map_lat",-33.450),lon=state==null?-70.660:state.getDouble("map_lon",-70.660);
            mapa.getController().setCenter(new GeoPoint(lat,lon));
            ((TextView)findViewById(R.id.titulo_mapa)).setText("Movimientos · "+nombre);
            int puntos=0,fuera=0;StringBuilder exteriores=new StringBuilder();
            for(Movimiento movimiento:movimientos) {
                if(!movimiento.tieneUbicacion())continue;
                Ubicacion u=movimiento.getUbicacion();puntos++;
                Marker marker=new Marker(mapa);marker.setPosition(new GeoPoint(u.getLatitud(),u.getLongitud()));
                marker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);
                String detalle=movimiento.getTipo().name()+" · "+Dinero.formatear(movimiento.getMontoConSigno(),divisa)+" · "+movimiento.getDescripcion()+" · "+DateFormat.getDateTimeInstance().format(new Date(movimiento.getFecha()))+"\n"+u.getLatitud()+", "+u.getLongitud();
                marker.setTitle(detalle);marker.setSnippet("Movimiento");
                marker.setOnMarkerClickListener((m,v)->{((TextView)findViewById(R.id.detalle_marcador)).setText(detalle);return true;});
                mapa.getOverlays().add(marker);
                if(!AtlasOffline.cubre(u.getLatitud(),u.getLongitud())){fuera++;exteriores.append("\n").append(u.getLatitud()).append(", ").append(u.getLongitud());}
            }
            ((TextView)findViewById(R.id.estado_mapa)).setText(puntos==0?"Sin movimientos con ubicación en esta cuenta.":puntos+" movimiento(s) con ubicación."+(fuera==0?"":" "+fuera+" fuera de cobertura, sin cartografía base:"+exteriores));
            findViewById(R.id.volver).setOnClickListener(v->finish());
            findViewById(R.id.zoom_mas).setOnClickListener(v->mapa.getController().setZoom(Math.min(16,mapa.getZoomLevelDouble()+1)));
            findViewById(R.id.zoom_menos).setOnClickListener(v->mapa.getController().setZoom(Math.max(14,mapa.getZoomLevelDouble()-1)));
            findViewById(R.id.centrar_mapa).setOnClickListener(v->mapa.getController().setCenter(new GeoPoint(-33.450,-70.660)));
            TextView attribution=findViewById(R.id.atribucion_mapa);
            android.text.SpannableString link=new android.text.SpannableString(attribution.getText());
            link.setSpan(new android.text.style.URLSpan("https://www.openstreetmap.org/copyright"),0,link.length(),0);
            attribution.setText(link);attribution.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
        } catch(IOException|IllegalArgumentException|SQLiteException|android.view.InflateException ex) {
            if(mapa!=null){mapa.onDetach();mapa=null;}
            setContentView(R.layout.activity_mapa_error);
            ((TextView)findViewById(R.id.estado_mapa)).setText("No se pudo abrir el mapa local. No se usarán mapas de Internet. "+(ex instanceof IOException?ex.getMessage():"Comprueba la cuenta y los archivos del atlas."));
            findViewById(R.id.volver).setOnClickListener(v->finish());
            Pantalla.aplicarMargenes(findViewById(R.id.root));
        }
    }
    @Override protected void onResume(){super.onResume();if(mapa!=null){mapa.onResume();reanudado=true;}}
    @Override protected void onPause(){if(mapa!=null && reanudado){mapa.onPause();reanudado=false;}super.onPause();}
    @Override protected void onDestroy(){if(mapa!=null)mapa.onDetach();super.onDestroy();}
    @Override protected void onSaveInstanceState(Bundle out){super.onSaveInstanceState(out);if(mapa!=null){out.putDouble("map_zoom",mapa.getZoomLevelDouble());out.putDouble("map_lat",mapa.getMapCenter().getLatitude());out.putDouble("map_lon",mapa.getMapCenter().getLongitude());}}
}

