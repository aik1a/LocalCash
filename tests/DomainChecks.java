import com.aik1a.localcash.domain.*;
import java.util.List;

public final class DomainChecks {
    private static int checks;
    private static void equal(long expected, long actual) {
        if (expected != actual) throw new AssertionError(expected + " != " + actual);
        checks++;
    }
    private static void rejects(Runnable action) {
        try { action.run(); } catch (IllegalArgumentException | ArithmeticException expected) { checks++; return; }
        throw new AssertionError("Se aceptó un valor inválido");
    }
    public static void main(String[] args) {
        Cuenta cuenta = new Cuenta(1, " Efectivo ", 10000);
        equal(12550, cuenta.calcularSaldo(List.of(
                new Movimiento(1, 1, Movimiento.Tipo.INGRESO, 5000, "", 1),
                new Movimiento(2, 1, Movimiento.Tipo.EGRESO, 2450, "", 2))));
        equal(30000, new Cuenta(2, "Banco", 30000).calcularSaldo(List.of()));
        equal(-5000, new Cuenta(1, "Efectivo", 0).calcularSaldo(List.of(
                new Movimiento(1, 1, Movimiento.Tipo.EGRESO, 5000, "", 0))));
        equal(10025, Dinero.parsear("100,25"));
        equal(10025, Dinero.parsear("100.25"));
        equal(-125, Dinero.parsear("-1,25"));
        equal(Long.MIN_VALUE, Dinero.parsear(Dinero.formatear(Long.MIN_VALUE)));
        rejects(() -> new Cuenta(0, "  ", 0));
        rejects(() -> new Movimiento(0, 1, Movimiento.Tipo.INGRESO, 0, "", 0));
        rejects(() -> new Movimiento(0, 1, Movimiento.Tipo.EGRESO, -1, "", 0));
        rejects(() -> new Movimiento(0, 0, Movimiento.Tipo.INGRESO, 1, "", 0));
        rejects(() -> new Movimiento(0, 1, null, 1, "", 0));
        rejects(() -> Dinero.parsear("1,234"));
        rejects(() -> Dinero.parsear("NaN"));
        rejects(() -> Dinero.parsear(""));
        rejects(() -> Dinero.parsear("92233720368547758,08"));
        rejects(() -> cuenta.calcularSaldo(List.of(new Movimiento(1, 2, Movimiento.Tipo.INGRESO, 1, "", 0))));
        rejects(() -> new Cuenta(1, "Límite", Long.MAX_VALUE).calcularSaldo(List.of(
                new Movimiento(0, 1, Movimiento.Tipo.INGRESO, 1, "", 0))));
        System.out.println("PASS: " + checks + " comprobaciones de dominio, validación y dinero exacto.");
    }
}
