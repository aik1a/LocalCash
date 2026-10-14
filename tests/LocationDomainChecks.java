import com.aik1a.localcash.domain.*;
public final class LocationDomainChecks {
    private static int checks;
    private static void check(boolean value) { if (!value) throw new AssertionError(); checks++; }
    private static void invalid(double lat, double lon) {
        try { new Ubicacion(lat,lon); throw new AssertionError("Coordenadas aceptadas"); }
        catch (IllegalArgumentException expected) { checks++; }
    }
    public static void main(String[] args) {
        Ubicacion u = new Ubicacion(-33.45,-70.66);
        check(u.getLatitud()==-33.45 && u.getLongitud()==-70.66);
        for (double lat : new double[]{-90,0,90})
            for (double lon : new double[]{-180,0,180}) {
                Ubicacion limite = new Ubicacion(lat,lon);
                check(limite.getLatitud()==lat && limite.getLongitud()==lon);
            }
        for (double v : new double[]{Double.NaN,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY}) {
            invalid(v,0); invalid(0,v);
        }
        invalid(-90.0001,0); invalid(90.0001,0); invalid(0,-180.0001); invalid(0,180.0001);
        Movimiento original = new Movimiento(0,1,Movimiento.Tipo.INGRESO,100,"",0);
        check(!original.tieneUbicacion() && original.getUbicacion()==null);
        Movimiento geo = new Movimiento(0,1,Movimiento.Tipo.EGRESO,100,"",0,u);
        check(geo.tieneUbicacion() && geo.getUbicacion()==u && geo.getMontoConSigno()==-100);
        check(!new Movimiento(0,1,Movimiento.Tipo.INGRESO,100,"",0,null).tieneUbicacion());
        System.out.println("PASS: "+checks+" comprobaciones de ubicación y compatibilidad.");
    }
}
