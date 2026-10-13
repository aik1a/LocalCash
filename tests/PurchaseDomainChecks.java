import com.aik1a.localcash.domain.*;
import java.util.*;

public final class PurchaseDomainChecks {
    private static int checks;
    private static void equal(long a, long b) { if (a != b) throw new AssertionError(a + " != " + b); checks++; }
    private static void rejects(Runnable action) {
        try { action.run(); } catch (IllegalArgumentException | ArithmeticException | UnsupportedOperationException expected) { checks++; return; }
        throw new AssertionError("Valor inválido aceptado");
    }
    public static void main(String[] args) {
        List<DetalleCompra> items = new ArrayList<>();
        items.add(new DetalleCompra("Producto", 2, 10025)); items.add(new DetalleCompra("Servicio", 3, 1050));
        Compra compra = new Compra(0, "Compra", Compra.Estado.REALIZADA, 0, 1L, items);
        equal(23200, compra.calcularTotal());
        items.clear(); equal(23200, compra.calcularTotal());
        equal(0, new DetalleCompra("Gratis", 1, 0).calcularSubtotal());
        rejects(() -> compra.getDetalles().clear());
        rejects(() -> new Contacto(0, " "));
        rejects(() -> new DetalleCompra("", 1, 1));
        rejects(() -> new DetalleCompra("Producto", 0, 1));
        rejects(() -> new DetalleCompra("Producto", -1, 1));
        rejects(() -> new DetalleCompra("Producto", 1, -1));
        rejects(() -> new DetalleCompra("Producto", 2, Long.MAX_VALUE));
        rejects(() -> new Compra(0, "Compra", Compra.Estado.PLANIFICADA, 0, null, List.of()));
        rejects(() -> new Compra(0, "Compra", null, 0, null, compra.getDetalles()));
        rejects(() -> new Compra(0, "Compra", Compra.Estado.PLANIFICADA, 0, 0L, compra.getDetalles()));
        rejects(() -> new Compra(0, "Compra", Compra.Estado.REALIZADA, 0, null, List.of(new DetalleCompra("A", 1, Long.MAX_VALUE), new DetalleCompra("B", 1, 1))));
        rejects(() -> new Compra(0, "Compra", Compra.Estado.REALIZADA, 0, null, Arrays.asList((DetalleCompra)null)));
        System.out.println("PASS: " + checks + " comprobaciones de contactos/compras, total exacto, inmutabilidad y overflow.");
    }
}
