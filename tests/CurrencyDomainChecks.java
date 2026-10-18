import com.aik1a.localcash.domain.*;
import java.util.*;

public final class CurrencyDomainChecks {
    private static int checks;
    private static void check(boolean value) { if (!value) throw new AssertionError("Check " + checks); checks++; }
    private static void invalid(Runnable code) {
        try { code.run(); throw new AssertionError("Aceptó datos inválidos"); }
        catch (IllegalArgumentException | ArithmeticException expected) { checks++; }
    }
    private static Compra compra(long monto, Divisa divisa) {
        return new Compra(1, "Compra", Compra.Estado.REALIZADA, 0, null,
                Arrays.asList(new DetalleCompra("Pan", 1, monto)), divisa);
    }
    public static void main(String[] args) {
        check(Divisa.values().length == 10);
        for (Divisa d : Divisa.values()) {
            check(d.getDecimales() == Currency.getInstance(d.name()).getDefaultFractionDigits());
            check(Dinero.parsear("2500", d) == 250000);
            check(Dinero.formatear(250000, d).startsWith(d.name() + " "));
            if (d.getDecimales() == 0) {
                check(Dinero.formatear(1500000, d).equals(d.name() + " 15.000"));
                invalid(() -> Dinero.parsear("2,50", d));
                invalid(() -> Dinero.parsear("2.00", d));
                invalid(() -> Dinero.formatear(250, d));
                invalid(() -> new Cuenta(0, "Caja", 1, d));
                invalid(() -> compra(1, d));
            } else {
                check(Dinero.parsear("25,50", d) == 2550);
                check(Dinero.parsear("25.50", d) == 2550);
                check(Dinero.formatear(2550, d).equals(d.name() + " 25,50"));
                invalid(() -> Dinero.parsear("1,234", d));
            }
            invalid(() -> Dinero.parsear("1.000,50", d));
            invalid(() -> Dinero.parsear("NaN", d));
            invalid(() -> Dinero.parsear("9223372036854775807", d));
            for (long unidades = 0; unidades < 120; unidades++) {
                long monto = unidades * d.getUnidadMinima();
                for (int n = 1; n < 9; n++) {
                    List<Long> ids = new ArrayList<>(); for (long id = n; id > 0; id--) ids.add(id);
                    Reparto reparto = Reparto.equitativoConUsuario(compra(monto, d), ids);
                    long suma = reparto.getMontoUsuario(), min = suma, max = suma;
                    check(reparto.getMontos().size() == n);
                    for (long cuota : reparto.getMontos().values()) {
                        suma = Math.addExact(suma, cuota); d.validar(cuota);
                        min = Math.min(min, cuota); max = Math.max(max, cuota);
                    }
                    check(suma == monto); check(max - min <= d.getUnidadMinima());
                }
            }
        }
        Reparto dos = Reparto.equitativoConUsuario(compra(10000, Divisa.CLP), Arrays.asList(1L));
        check(dos.getMontoUsuario() == 5000 && dos.getMontos().get(1L) == 5000);
        Reparto tres = Reparto.equitativoConUsuario(compra(10000, Divisa.CLP), Arrays.asList(9L, 2L));
        check(tres.getMontoUsuario() == 3400 && tres.getMontos().get(2L) == 3300 && tres.getMontos().get(9L) == 3300);
        check(Reparto.personalizadoConUsuario(compra(10000, Divisa.CLP), Arrays.asList(1L), Arrays.asList(6000L), 4000).getMontoUsuario() == 4000);
        invalid(() -> Reparto.personalizadoConUsuario(compra(10000, Divisa.CLP), Arrays.asList(1L), Arrays.asList(9950L), 50));
        invalid(() -> Reparto.personalizadoConUsuario(compra(10000, Divisa.USD), Arrays.asList(1L), Arrays.asList(6000L), 3000));
        invalid(() -> Reparto.personalizadoConUsuario(compra(0, Divisa.USD), Arrays.asList(1L), Arrays.asList(1L), -1));
        invalid(() -> Reparto.equitativoConUsuario(compra(10000, Divisa.CLP), Collections.emptyList()));
        invalid(() -> Reparto.equitativoConUsuario(compra(10000, Divisa.CLP), Arrays.asList(1L, 1L)));
        invalid(() -> Reparto.equitativoConUsuario(compra(10000, Divisa.CLP), Arrays.asList(0L)));
        invalid(() -> Reparto.equitativoConUsuario(compra(10000, Divisa.CLP), Arrays.asList((Long)null)));
        check(Dinero.formatear(125, null).equals("Sin divisa · 1,25"));
        check(Dinero.parsear(Dinero.formatear(Long.MIN_VALUE)) == Long.MIN_VALUE);
        System.out.println("PASS: " + checks + " currency/owner checks.");
    }
}
