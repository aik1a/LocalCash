package com.aik1a.localcash.presentation;

import android.app.Application;
import android.content.Context;
import android.util.AttributeSet;
import org.osmdroid.views.MapView;
import org.osmdroid.mapsforge.MapsForgeTileProvider;
import org.osmdroid.mapsforge.MapsForgeTileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;

public final class MapaLocalView extends MapView {
    public MapaLocalView(Context context, AttributeSet attrs) {
        super(context, proveedor(context), null, attrs);
        setUseDataConnection(false);
    }
    private static MapsForgeTileProvider proveedor(Context context) {
        MapsForgeTileSource.createInstance((Application) context.getApplicationContext());
        MapsForgeTileSource fuente = MapsForgeTileSource.createFromFiles(
                MapasVectoriales.archivos(context), null,
                "LocalCashVector-" + MapasVectoriales.archivo(context,"importado.map").lastModified());
        MapsForgeTileProvider proveedor = new MapsForgeTileProvider(new SimpleRegisterReceiver(context), fuente, null);
        proveedor.setUseDataConnection(false);
        return proveedor;
    }
    @Override public void onDetach() {
        MapsForgeTileSource fuente = (MapsForgeTileSource) getTileProvider().getTileSource();
        super.onDetach();
        fuente.dispose();
    }
}
