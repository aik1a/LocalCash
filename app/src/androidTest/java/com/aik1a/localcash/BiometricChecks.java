package com.aik1a.localcash;

import android.app.*;
import android.content.Intent;
import android.os.*;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityNodeInfo;
import com.aik1a.localcash.application.*;
import com.aik1a.localcash.data.*;
import com.aik1a.localcash.domain.*;
import java.util.*;

public final class BiometricChecks {
    private final Instrumentation instrumentation;
    private String mode, activity;
    private BiometricChecks(Instrumentation instrumentation, Bundle args) {
        this.instrumentation=instrumentation;
        mode=args.getString("mode").replace("bio-",""); activity=args.getString("activity");
    }
    public static void ejecutar(Instrumentation instrumentation,Bundle args) {
        new BiometricChecks(instrumentation,args).verificar();
    }
    private void verificar() {
        Bundle result=new Bundle();
        try {
            if ("seed".equals(mode)) {
                instrumentation.getTargetContext().deleteDatabase("localcash.db");
                try (LocalCashDbHelper helper=new LocalCashDbHelper(instrumentation.getTargetContext())) {
                    new ConfiguracionRepository(helper).configurarPrimeraVez(Divisa.USD);
                    MovimientoService cuentas=new MovimientoService(helper);
                    long cuenta=cuentas.crearCuenta("Efectivo",10000);
                    cuentas.registrar(cuenta,Movimiento.Tipo.INGRESO,2550,"Previo",new Ubicacion(-33.45,-70.66));
                    CompraService compras=new CompraService(helper);
                    long ana=compras.crearContacto("Ana"),luis=compras.crearContacto("Luis");
                    long compra=compras.registrar(new Compra(0,"Compra previa",Compra.Estado.REALIZADA,0,ana,
                            Arrays.asList(new DetalleCompra("Producto",1,10000)), Divisa.USD));
                    new DeudaService(helper).repartirEquitativo(compra,Arrays.asList(ana,luis));
                }
                result.putString("stream","LOCALCASH_BIO_SEED_PASS");
            } else if ("direct".equals(mode)) {
                AccessibilityServiceInfo info=instrumentation.getUiAutomation().getServiceInfo();
                info.flags|=AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS; instrumentation.getUiAutomation().setServiceInfo(info);
                Intent intent=new Intent().setClassName(instrumentation.getTargetContext(),"com.aik1a.localcash.presentation."+activity);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("cuenta_id",1L); intent.putExtra("compra_id",1L); intent.putExtra("authenticated",true);
                instrumentation.startActivitySync(intent);
                long deadline=SystemClock.uptimeMillis()+8000;
                boolean gate=false;
                do {
                    AccessibilityNodeInfo root=instrumentation.getUiAutomation().getRootInActiveWindow();
                    if (root!=null) {
                        for(String id:new String[]{"cuentas","movimientos","contactos","compras","deudas","nombre_compra","compra_total","map"}) {
                            if(!root.findAccessibilityNodeInfosByViewId("com.aik1a.localcash:id/"+id).isEmpty())
                                throw new AssertionError("Contenido expuesto: "+activity+" "+id);
                        }
                        gate=!root.findAccessibilityNodeInfosByText("Desbloquear LocalCash").isEmpty()
                                || !root.findAccessibilityNodeInfosByText("LocalCash bloqueado").isEmpty();
                    }
                    if(!gate) SystemClock.sleep(100);
                } while(!gate && SystemClock.uptimeMillis()<deadline);
                if(!gate) throw new AssertionError("Gate no visible: "+activity);
                result.putString("stream","LOCALCASH_BIO_DIRECT_PASS "+activity);
            } else throw new IllegalArgumentException("Modo de prueba desconocido");
            instrumentation.finish(Activity.RESULT_OK,result);
        } catch(Throwable failure) {
            result.putString("stream","LOCALCASH_BIO_FAIL\n"+android.util.Log.getStackTraceString(failure));
            instrumentation.finish(Activity.RESULT_CANCELED,result);
        }
    }
}

