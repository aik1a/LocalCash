package com.aik1a.localcash.presentation;

import android.content.Context;
import android.util.AttributeSet;
import org.osmdroid.views.MapView;
import org.osmdroid.tileprovider.modules.OfflineTileProvider;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.tileprovider.tilesource.BitmapTileSourceBase;
import java.io.File;

public final class MapaLocalView extends MapView {
    public MapaLocalView(Context context,AttributeSet attrs) {
        super(context,new OfflineTileProvider(new SimpleRegisterReceiver(context),new File[]{AtlasOffline.archivo(context)}),null,attrs);
        setUseDataConnection(false);
        setTileSource(new BitmapTileSourceBase(AtlasOffline.FUENTE,14,16,256,".png","© OpenStreetMap contributors · ODbL") { });
    }
}

