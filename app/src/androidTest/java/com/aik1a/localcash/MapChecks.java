package com.aik1a.localcash;

import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.graphics.drawable.*;
import android.location.*;
import android.net.ConnectivityManager;
import android.os.*;
import android.view.WindowManager;
import android.widget.TextView;
import com.aik1a.localcash.presentation.*;
import com.aik1a.localcash.application.*;
import com.aik1a.localcash.data.*;
import com.aik1a.localcash.domain.*;
import org.osmdroid.views.overlay.*;
import org.osmdroid.mapsforge.MapsForgeTileProvider;
import org.osmdroid.util.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.*;

/** Disposable emulator fixtures. Exercises the production location callback and vector renderer. */
public final class MapChecks {
    private final Instrumentation ins; private final Context context;
    private final StringBuilder log=new StringBuilder();
    private Activity pantalla; private MapaLocalView mapa;
    private MapChecks(Instrumentation ins){this.ins=ins;context=ins.getTargetContext();}
    public static void ejecutar(Instrumentation ins){new MapChecks(ins).ejecutar();}
    private void ok(boolean condition,String message){if(!condition)throw new AssertionError(message);log.append("PASS ").append(message).append('\n');android.util.Log.i("LocalCashMaps",message);Bundle progress=new Bundle();progress.putString("stream","PASS "+message+"\n");ins.sendStatus(0,progress);}
    private void ui(Runnable work){ins.runOnMainSync(work);}
    private void ejecutar(){
        Bundle result=new Bundle(); LocationManager lm=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        AtomicBoolean feeding=new AtomicBoolean(true); AtomicReference<GeoPoint> fix=new AtomicReference<>(new GeoPoint(-33.55,-70.58)); Thread feed=null;
        try {
            ConnectivityManager cm=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            ok(cm.getActiveNetworkInfo()==null||!cm.getActiveNetworkInfo().isConnected(),"Primera apertura sin red");
            ok(context.checkSelfPermission("android.permission.INTERNET")==PackageManager.PERMISSION_DENIED,"APK sin permiso INTERNET");
            ok(!MapasVectoriales.archivo(context,"world.map").exists(),"Instalación nueva sin mapas copiados ni caché");
            lm.addTestProvider(LocationManager.GPS_PROVIDER,false,false,false,false,true,true,true,Criteria.POWER_LOW,Criteria.ACCURACY_FINE);
            lm.setTestProviderEnabled(LocationManager.GPS_PROVIDER,true);
            feed=new Thread(() -> {while(feeding.get()){
                try {GeoPoint p=fix.get();Location location=new Location(LocationManager.GPS_PROVIDER);location.setLatitude(p.getLatitude());location.setLongitude(p.getLongitude());location.setAccuracy(5);location.setTime(System.currentTimeMillis());location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());lm.setTestProviderLocation(LocationManager.GPS_PROVIDER,location);Thread.sleep(250);}catch(Exception ignored){}
            }});feed.start();
            context.deleteDatabase("localcash.db");long cuenta;
            try(LocalCashDbHelper helper=new LocalCashDbHelper(context)){
                new ConfiguracionRepository(helper).configurarPrimeraVez(Divisa.USD);
                MovimientoService service=new MovimientoService(helper);cuenta=service.crearCuenta("Demo mapa",10000);
                service.registrar(cuenta,Movimiento.Tipo.INGRESO,100,"La Florida",new Ubicacion(-33.55,-70.58));
                service.registrar(cuenta,Movimiento.Tipo.EGRESO,50,"París",new Ubicacion(48.8566,2.3522));
                service.registrar(cuenta,Movimiento.Tipo.INGRESO,25,"Sin coordenadas");
            }
            abrir(cuenta);esperarMapa();esperarPosicion(-33.55,-70.58);
            ok((pantalla.getWindow().getAttributes().flags&WindowManager.LayoutParams.FLAG_SECURE)!=0,"Biometría y FLAG_SECURE intactos");
            ok(mapa.getTileProvider() instanceof MapsForgeTileProvider&&!mapa.useDataConnection(),"Render vectorial local sin conexión");
            ok(((TextView)pantalla.findViewById(R.id.centrar_mapa)).getText().toString().equals("Mi ubicación"),"Botón Mi ubicación visible");
            ok(!((TextView)pantalla.findViewById(R.id.estado_mapa)).getText().toString().contains("fuera de cobertura"),"Sin rechazo territorial de movimientos");
            long movimientos=mapa.getOverlays().stream().filter(o->o instanceof Marker&&"Movimiento".equals(((Marker)o).getSnippet())).count();
            ok(movimientos==2,"Dos movimientos geolocalizados de la cuenta; sin GPS excluido");
            verificarTile(-33.55,-70.58,15,"la-florida-callles",15);
            verificarTile(35.6762,139.6503,3,"mundo-tokio",3);
            verificarTile(48.8566,2.3522,3,"mundo-europa",3);
            fix.set(new GeoPoint(48.8566,2.3522));ui(()->pantalla.findViewById(R.id.centrar_mapa).performClick());esperarPosicion(48.8566,2.3522);
            ok(true,"Mi ubicación se centra en París mediante callback GPS, fuera de Chile");
            ui(()->{mapa.getController().setZoom(5.0);mapa.getController().setCenter(new GeoPoint(35.6762,139.6503));});SystemClock.sleep(500);
            ok(Math.abs(mapa.getMapCenter().getLongitude()-139.6503)<.01,"Navegación mundial sin rectángulo de Santiago");
            ui(()->pantalla.findViewById(R.id.ver_movimientos_mapa).performClick());SystemClock.sleep(800);
            ok(mapa.getZoomLevelDouble()<10,"Ver movimientos encuadra continentes distintos");
            // Invalid imports must not replace the existing map or touch SQLite.
            File invalid=new File(context.getCacheDir(),"invalid.map");try(FileOutputStream out=new FileOutputStream(invalid)){out.write(new byte[]{1,2,3});}
            try{MapasVectoriales.importar(context,android.net.Uri.fromFile(invalid));throw new AssertionError("Mapa inválido aceptado");}catch(IOException expected){ok(!MapasVectoriales.tieneAdicional(context),"Importación inválida rechazada de forma atómica");}
            // A valid local map may be imported without internet.
            MapasVectoriales.importar(context,android.net.Uri.fromFile(MapasVectoriales.archivo(context,"world.map")));
            ok(MapasVectoriales.tieneAdicional(context),"Importación local .map válida sin red");
            MapasVectoriales.archivo(context,"importado.map").delete();
            try(LocalCashDbHelper helper=new LocalCashDbHelper(context)){
                MovimientoService service=new MovimientoService(helper);
                ok(helper.getReadableDatabase().getVersion()==7&&service.calcularSaldo(cuenta)==10075&&service.listarMovimientos(cuenta).size()==3,"SQLite v7, saldos y coordenadas preservados");
            }
            ui(()->{mapa.getController().setZoom(5.0);mapa.getController().setCenter(new GeoPoint(35.6762,139.6503));});
            Instrumentation.ActivityMonitor monitor=ins.addMonitor(MapaActivity.class.getName(),null,false);ui(()->pantalla.recreate());
            Activity nueva=ins.waitForMonitorWithTimeout(monitor,30000);ins.removeMonitor(monitor);pantalla=nueva;esperarMapa();SystemClock.sleep(600);
            ok(Math.abs(mapa.getMapCenter().getLongitude()-139.6503)<.01&&Math.abs(mapa.getZoomLevelDouble()-5)<.01,"Rotación/recreación conserva vista mundial");
            log.append("LOCALCASH_MAPS_PASS\n");
            File folder=new File(context.getFilesDir(),"map-evidence");folder.mkdirs();try(FileWriter writer=new FileWriter(new File(folder,"map-checks.log"))){writer.write(log.toString());}
            result.putString("stream",log.toString());ins.finish(Activity.RESULT_OK,result);
        }catch(Throwable ex){result.putString("stream",log+"\nLOCALCASH_MAPS_FAIL\n"+android.util.Log.getStackTraceString(ex));ins.finish(Activity.RESULT_CANCELED,result);}
        finally{feeding.set(false);if(feed!=null)feed.interrupt();try{lm.removeTestProvider(LocationManager.GPS_PROVIDER);}catch(Exception ignored){}}
    }
    private void abrir(long id){
        Instrumentation.ActivityMonitor monitor=ins.addMonitor(MapaActivity.class.getName(),null,false);
        ui(()->context.startActivity(new Intent(context,MapaActivity.class).putExtra(CuentaActivity.CUENTA_ID,id).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK)));
        pantalla=ins.waitForMonitorWithTimeout(monitor,30000);ins.removeMonitor(monitor);
        ok(pantalla!=null,"Activity del mapa iniciada sin esperar al render continuo");
    }
    private void esperarMapa(){long end=SystemClock.uptimeMillis()+90000;while(SystemClock.uptimeMillis()<end){ui(()->mapa=pantalla.findViewById(R.id.map));if(mapa!=null&&mapa.getWidth()>0)return;SystemClock.sleep(200);}throw new AssertionError("Mapa no disponible tras biometría/preparación");}
    private void esperarPosicion(double lat,double lon){long end=SystemClock.uptimeMillis()+22000;while(SystemClock.uptimeMillis()<end){final boolean[] ready={false};ui(()->{TextView t=pantalla.findViewById(R.id.detalle_marcador);ready[0]=t!=null&&t.getText().toString().startsWith("Mi ubicación:")&&Math.abs(mapa.getMapCenter().getLatitude()-lat)<.001&&Math.abs(mapa.getMapCenter().getLongitude()-lon)<.001;});if(ready[0]){ok(true,"GPS centra "+lat+", "+lon);return;}SystemClock.sleep(200);}throw new AssertionError("La posición actual no centró el mapa");}
    private void verificarTile(double lat,double lon,int z,String name,int minColors)throws Exception{
        int x=(int)((lon+180)/360*(1<<z)),y=(int)((1-Math.log(Math.tan(Math.toRadians(lat))+1/Math.cos(Math.toRadians(lat)))/Math.PI)/2*(1<<z));
        long index=MapTileIndex.getTileIndex(z,x,y);Drawable[] tile={null};long end=SystemClock.uptimeMillis()+30000;
        while(SystemClock.uptimeMillis()<end){ui(()->tile[0]=mapa.getTileProvider().getMapTile(index));if(tile[0] instanceof BitmapDrawable)break;SystemClock.sleep(200);}
        ok(tile[0] instanceof BitmapDrawable,"Tile offline renderizado "+name);
        Bitmap bitmap=((BitmapDrawable)tile[0]).getBitmap();HashSet<Integer> colors=new HashSet<>();int[] pixels=new int[bitmap.getWidth()*bitmap.getHeight()];bitmap.getPixels(pixels,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());for(int p:pixels)colors.add(p);
        ok(colors.size()>minColors,"Cartografía real no vacía "+name+" colores="+colors.size());
        File folder=new File(context.getFilesDir(),"map-evidence");folder.mkdirs();try(OutputStream out=new FileOutputStream(new File(folder,name+".png"))){bitmap.compress(Bitmap.CompressFormat.PNG,100,out);}
    }
}
