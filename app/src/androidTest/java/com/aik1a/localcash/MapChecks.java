package com.aik1a.localcash;

import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.view.accessibility.AccessibilityNodeInfo;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.widget.TextView;
import com.aik1a.localcash.presentation.*;
import com.aik1a.localcash.application.*;
import com.aik1a.localcash.data.*;
import com.aik1a.localcash.domain.*;
import org.osmdroid.views.*;
import org.osmdroid.views.overlay.*;
import org.osmdroid.tileprovider.modules.OfflineTileProvider;
import org.osmdroid.util.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;

/** Instrumentación destructiva solo en emulador desechable. Gate biométrico siempre activo. */
public final class MapChecks {
    private final Instrumentation ins;private final Context context;private final StringBuilder log=new StringBuilder();
    private Activity pantalla;private MapaLocalView mapa;
    private MapChecks(Instrumentation ins){this.ins=ins;context=ins.getTargetContext();}
    public static void ejecutar(Instrumentation ins){new MapChecks(ins).ejecutar();}
    private void ok(boolean value,String message){if(!value)throw new AssertionError(message);}
    private void ui(Runnable work){ins.runOnMainSync(work);}
    private void ejecutar(){Bundle result=new Bundle();try {
        AccessibilityServiceInfo info=ins.getUiAutomation().getServiceInfo();info.flags|=AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;ins.getUiAutomation().setServiceInfo(info);
        ConnectivityManager manager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo active=manager.getActiveNetworkInfo();
        long networkDeadline=SystemClock.uptimeMillis()+20000;
        while(active!=null && active.isConnected() && SystemClock.uptimeMillis()<networkDeadline){SystemClock.sleep(250);active=manager.getActiveNetworkInfo();}
        ok(active==null||!active.isConnected(),"Primera apertura exige red desactivada");
        ok(context.checkSelfPermission("android.permission.INTERNET")==PackageManager.PERMISSION_DENIED,"APK no debe tener permiso INTERNET");
        ok(!AtlasOffline.archivo(context).exists(),"Atlas ya copiado: primera apertura no limpia");
        log.append("PASS primera instalación: sin conectividad, sin permiso INTERNET y sin atlas/caché previo.\n");
        context.deleteDatabase("localcash.db");long cuenta,sinGps;
        try(LocalCashDbHelper helper=new LocalCashDbHelper(context)){
            new com.aik1a.localcash.data.ConfiguracionRepository(helper).configurarPrimeraVez(Divisa.USD);
            MovimientoService service=new MovimientoService(helper);cuenta=service.crearCuenta("Demo mapa",10000);sinGps=service.crearCuenta("Sin GPS",5000);
            service.registrar(cuenta,Movimiento.Tipo.INGRESO,100,"Centro",new Ubicacion(-33.450,-70.660));
            service.registrar(cuenta,Movimiento.Tipo.EGRESO,50,"Fuera",new Ubicacion(-33.50,-70.70));
            service.registrar(cuenta,Movimiento.Tipo.INGRESO,25,"Sin coordenadas");
            service.registrar(sinGps,Movimiento.Tipo.INGRESO,1,"Sin GPS");ok(helper.getReadableDatabase().getVersion()==6,"SQLite v6");
        }
        ins.startActivitySync(new Intent(context,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
        clickText("Demo mapa");
        Instrumentation.ActivityMonitor monitor=ins.addMonitor(MapaActivity.class.getName(),null,false);clickId("abrir_mapa");
        pantalla=ins.waitForMonitorWithTimeout(monitor,25000);ins.removeMonitor(monitor);ok(pantalla!=null,"Navegación a mapa");esperarMapa();
        ok((pantalla.getWindow().getAttributes().flags&WindowManager.LayoutParams.FLAG_SECURE)!=0,"FLAG_SECURE permanece");
        ok(mapa.getTileProvider() instanceof OfflineTileProvider,"OfflineTileProvider real");
        ok(!mapa.useDataConnection(),"Conectividad del mapa desactivada");
        ok(mapa.getTileProvider().getTileSource().name().equals("LocalCashOSM"),"Fuente exacta");
        List<Marker> markers=new ArrayList<>();for(Overlay o:mapa.getOverlays())if(o instanceof Marker)markers.add((Marker)o);
        ok(markers.size()==2,"Solo dos movimientos georreferenciados; sin GPS y otra cuenta excluidos");
        Marker centro=null;boolean exterior=false;
        for(Marker m:markers){if(Math.abs(m.getPosition().getLatitude()+33.45)<1e-8)centro=m;if(m.getPosition().getLatitude()==-33.50&&m.getPosition().getLongitude()==-70.70)exterior=true;}
        ok(centro!=null&&exterior,"Coordenadas originales intactas, incluso fuera del área");
        ok(((TextView)pantalla.findViewById(R.id.estado_mapa)).getText().toString().contains("fuera de cobertura"),"Aviso fuera de cobertura");
        for(int z:new int[]{14,15,16}){
            final int zoom=z;ui(()->mapa.getController().setZoom((double)zoom));SystemClock.sleep(700);
            verificarTile(z);
        }
        final Marker marcador=centro;ui(()->{Point p=mapa.getProjection().toPixels(marcador.getPosition(),null);android.view.MotionEvent event=android.view.MotionEvent.obtain(android.os.SystemClock.uptimeMillis(),android.os.SystemClock.uptimeMillis(),android.view.MotionEvent.ACTION_UP,p.x,p.y-8,0);marcador.onSingleTapConfirmed(event,mapa);event.recycle();});
        ok(((TextView)pantalla.findViewById(R.id.detalle_marcador)).getText().toString().contains("Centro"),"Detalle de marcador");
        ui(()->{mapa.getController().setCenter(new GeoPoint(-33.451,-70.662));});SystemClock.sleep(400);
        ok(Math.abs(mapa.getMapCenter().getLongitude()+70.662)<.002,"Desplazamiento geográfico");
        ui(()->{mapa.getController().setCenter(new GeoPoint(-33.450,-70.660));mapa.getController().setZoom(16.0);});SystemClock.sleep(1000);
        renderConMarcador(marcador);
        Instrumentation.ActivityMonitor recreate=ins.addMonitor(MapaActivity.class.getName(),null,false);
        ui(()->pantalla.recreate());Activity nueva=ins.waitForMonitorWithTimeout(recreate,25000);ins.removeMonitor(recreate);ok(nueva!=null,"Recreación de mapa");pantalla=nueva;esperarMapa();ok(Math.abs(mapa.getZoomLevelDouble()-16)<.01,"Zoom conservado en lifecycle");
        log.append("PASS proveedor local, tiles reales z14/15/16 decodificados idénticos al ZIP, base dibujada y marcador visible, pan/zoom/detalles/cobertura.\n");
        ui(()->pantalla.finish());SystemClock.sleep(500);
        abrirDirecto(sinGps);esperarMapa();ok(mapa.getOverlays().stream().noneMatch(o->o instanceof Marker),"Cuenta sin GPS sin marcadores");
        ok(((TextView)pantalla.findViewById(R.id.estado_mapa)).getText().toString().contains("Sin movimientos con ubicación"),"Estado vacío");
        verificarTile(15);ui(()->pantalla.finish());SystemClock.sleep(400);
        try(FileOutputStream out=new FileOutputStream(AtlasOffline.archivo(context))){out.write(new byte[]{1,2,3});}
        abrirDirecto(cuenta);esperarError();ok(pantalla.findViewById(R.id.map)==null,"Atlas corrupto no muestra mapa vacío ni fallback");ui(()->pantalla.finish());
        ok(AtlasOffline.archivo(context).delete(),"Eliminar copia corrupta de emulador");abrirDirecto(cuenta);esperarMapa();verificarTile(15);
        log.append("PASS sin coordenadas: cero marcadores/estado vacío; ZIP corrupto: error local controlado; copia ausente: recuperada exclusivamente desde APK.\n");
        try{AtlasOffline.validar(context,new File(context.getFilesDir(),"no-existe.zip"));throw new AssertionError("Archivo ausente aceptado");}catch(IOException esperado){}
        try(LocalCashDbHelper helper=new LocalCashDbHelper(context)){
            MovimientoService s=new MovimientoService(helper);ok(helper.getReadableDatabase().getVersion()==6,"No hay migración");ok(s.calcularSaldo(cuenta)==10075&&s.listarMovimientos(cuenta).size()==3,"Mapa no altera datos/saldo");
        }
        log.append("PASS reapertura/lifecycle y SQLite v6: datos/saldos/coordenadas intactos.\nLOCALCASH_MAPS_PASS\n");
        File evidence=new File(context.getFilesDir(),"map-evidence");evidence.mkdirs();try(FileWriter writer=new FileWriter(new File(evidence,"map-checks.log"))){writer.write(log.toString());}
        result.putString("stream",log.toString());ins.finish(Activity.RESULT_OK,result);
    }catch(Throwable ex){result.putString("stream",log+"\nLOCALCASH_MAPS_FAIL\n"+android.util.Log.getStackTraceString(ex));ins.finish(Activity.RESULT_CANCELED,result);}}
    private void abrirDirecto(long cuenta){pantalla=ins.startActivitySync(new Intent(context,MapaActivity.class).putExtra(CuentaActivity.CUENTA_ID,cuenta).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));}
    private void esperarMapa(){long end=SystemClock.uptimeMillis()+25000;while(SystemClock.uptimeMillis()<end){ui(()->mapa=pantalla.findViewById(R.id.map));if(mapa!=null&&mapa.getWidth()>0)return;SystemClock.sleep(150);}throw new AssertionError("Mapa no disponible tras biometría");}
    private void esperarError(){long end=SystemClock.uptimeMillis()+25000;while(SystemClock.uptimeMillis()<end){final boolean[] found={false};ui(()->{TextView t=pantalla.findViewById(R.id.estado_mapa);found[0]=t!=null&&t.getText().toString().contains("No se pudo abrir el mapa local");});if(found[0])return;SystemClock.sleep(100);}throw new AssertionError("No hay error de atlas controlado");}
    private void verificarTile(int z)throws Exception{
        int x=(int)((-70.660+180)/360*(1<<z));double lat=Math.toRadians(-33.450);int y=(int)((1-Math.log(Math.tan(lat)+1/Math.cos(lat))/Math.PI)/2*(1<<z));long index=MapTileIndex.getTileIndex(z,x,y);
        Drawable[] tile={null};long end=SystemClock.uptimeMillis()+15000;
        while(SystemClock.uptimeMillis()<end){ui(()->{mapa.invalidate();tile[0]=mapa.getTileProvider().getMapTile(index);});if(tile[0] instanceof BitmapDrawable)break;SystemClock.sleep(150);}
        ok(tile[0] instanceof BitmapDrawable,"Tile real no cargado z"+z);Bitmap actual=((BitmapDrawable)tile[0]).getBitmap();
        Bitmap expected;try(ZipFile zip=new ZipFile(AtlasOffline.archivo(context));InputStream in=zip.getInputStream(zip.getEntry("LocalCashOSM/"+z+"/"+x+"/"+y+".png"))){expected=BitmapFactory.decodeStream(in);}
        ok(actual.getWidth()==256&&expected!=null,"PNG 256px");int[] a=new int[65536],b=new int[65536];actual.getPixels(a,0,256,0,0,256,256);expected.getPixels(b,0,256,0,0,256,256);ok(Arrays.equals(a,b),"Tile visible idéntico al PNG versionado");
        HashSet<Integer> colors=new HashSet<>();for(int c:a)colors.add(c);ok(colors.size()>15,"Cartografía no vacía");
        File folder=new File(context.getFilesDir(),"map-evidence");folder.mkdirs();try(OutputStream out=new FileOutputStream(new File(folder,"tile-z"+z+".png"))){expected.compress(Bitmap.CompressFormat.PNG,100,out);}expected.recycle();
        log.append("PASS tile fuente LocalCashOSM z").append(z).append(" ").append(x).append("/").append(y).append(" colores=").append(colors.size()).append("\n");
    }
    private void renderConMarcador(Marker marker)throws Exception{
        Bitmap[] images=new Bitmap[2];ui(()->{
            for(int i=0;i<2;i++){marker.setEnabled(i==1);images[i]=Bitmap.createBitmap(mapa.getWidth(),mapa.getHeight(),Bitmap.Config.ARGB_8888);mapa.draw(new Canvas(images[i]));}marker.setEnabled(true);
        });int[] a=new int[images[0].getWidth()*images[0].getHeight()],b=new int[a.length];images[0].getPixels(a,0,images[0].getWidth(),0,0,images[0].getWidth(),images[0].getHeight());images[1].getPixels(b,0,images[1].getWidth(),0,0,images[1].getWidth(),images[1].getHeight());int changed=0;for(int i=0;i<a.length;i++)if(a[i]!=b[i])changed++;ok(changed>20,"El marcador no se dibujó sobre MapView");
        File folder=new File(context.getFilesDir(),"map-evidence");folder.mkdirs();try(OutputStream out=new FileOutputStream(new File(folder,"authenticated-map-render.png"))){images[1].compress(Bitmap.CompressFormat.PNG,100,out);}for(Bitmap bitmap:images)bitmap.recycle();log.append("PASS render MapView real: marcador cambia ").append(changed).append(" pixels; FLAG_SECURE intacto.\n");
    }
    private AccessibilityNodeInfo nodo(String id,String text){AccessibilityNodeInfo root=ins.getUiAutomation().getRootInActiveWindow();if(root==null)return null;List<AccessibilityNodeInfo> list=id!=null?root.findAccessibilityNodeInfosByViewId(context.getPackageName()+":id/"+id):root.findAccessibilityNodeInfosByText(text);for(AccessibilityNodeInfo n:list)if(n.isVisibleToUser())return n;return null;}
    private void clickId(String id){click(id,null);}private void clickText(String text){click(null,text);}
    private void click(String id,String text){long end=SystemClock.uptimeMillis()+25000;while(SystemClock.uptimeMillis()<end){AccessibilityNodeInfo n=nodo(id,text);if(n!=null){while(n!=null&&!n.isClickable())n=n.getParent();if(n!=null&&n.performAction(AccessibilityNodeInfo.ACTION_CLICK))return;}SystemClock.sleep(150);}throw new AssertionError("Control no disponible "+id+" "+text);}
}
