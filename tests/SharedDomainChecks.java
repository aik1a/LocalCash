import com.aik1a.localcash.domain.*;
import java.util.*;

public final class SharedDomainChecks {
    private static int checks;
    private static Compra compra(long total) {
        return new Compra(1, "Compra", Compra.Estado.REALIZADA, 0, 7L,
                Arrays.asList(new DetalleCompra("Producto", 1, total)));
    }
    private static void check(boolean condition) {
        if (!condition) throw new AssertionError("Check " + checks);
        checks++;
    }
    private static void invalid(Runnable action) {
        try { action.run(); throw new AssertionError("Aceptó entrada inválida"); }
        catch (IllegalArgumentException | ArithmeticException expected) { checks++; }
    }
    public static void main(String[] args) {
        List<Long> ids = Arrays.asList(9L, 2L, 5L);
        Reparto reparto = Reparto.equitativo(compra(10001), ids);
        check(reparto.getMontos().get(2L) == 3334);
        check(reparto.getMontos().get(5L) == 3334);
        check(reparto.getMontos().get(9L) == 3333);
        check(new ArrayList<>(reparto.getMontos().keySet()).equals(Arrays.asList(2L, 5L, 9L)));
        check(!reparto.getMontos().containsKey(7L)); // Contacto asociado no es participante.
        Reparto custom = Reparto.personalizado(compra(10000), ids, Arrays.asList(2000L,3000L,5000L));
        check(custom.getMontos().get(9L) == 2000);
        check(custom.getMontos().get(2L) == 3000);
        check(custom.getMontos().get(5L) == 5000);
        check(Reparto.personalizado(compra(10000), Arrays.asList(1L,2L), Arrays.asList(0L,10000L)).getMontos().get(1L) == 0);
        check(Reparto.equitativo(compra(0), ids).getMontos().values().stream().allMatch(v -> v == 0));
        check(Reparto.equitativo(compra(1), ids).getMontos().get(2L) == 1);
        check(Reparto.equitativo(compra(1), ids).getMontos().get(5L) == 0);
        check(Reparto.equitativo(compra(Long.MAX_VALUE), ids).getMontos().values().stream().reduce(0L, Math::addExact) == Long.MAX_VALUE);
        invalid(() -> Reparto.equitativo(compra(100), Arrays.asList(1L)));
        invalid(() -> Reparto.equitativo(compra(100), Arrays.asList(1L,1L)));
        invalid(() -> Reparto.equitativo(compra(100), Arrays.asList(0L,1L)));
        invalid(() -> Reparto.equitativo(compra(100), Arrays.asList(null,1L)));
        invalid(() -> Reparto.equitativo(null, ids));
        invalid(() -> Reparto.personalizado(compra(100), Arrays.asList(1L,1L), Arrays.asList(50L,50L)));
        invalid(() -> Reparto.personalizado(compra(100), Arrays.asList(1L,2L), Arrays.asList(-1L,101L)));
        invalid(() -> Reparto.personalizado(compra(100), Arrays.asList(1L,2L), Arrays.asList(49L,50L)));
        invalid(() -> Reparto.personalizado(compra(100), Arrays.asList(1L,2L), Arrays.asList(Long.MAX_VALUE,1L)));
        invalid(() -> Reparto.personalizado(compra(100), Arrays.asList(1L,2L), Arrays.asList(100L)));
        invalid(() -> Reparto.personalizado(compra(100), Arrays.asList(1L,2L), Arrays.asList(50L,null)));
        invalid(() -> Reparto.equitativo(new Compra(1,"Plan",Compra.Estado.PLANIFICADA,0,null,compra(100).getDetalles()), ids));
        invalid(() -> Reparto.equitativo(new Compra(0,"Nueva",Compra.Estado.REALIZADA,0,null,compra(100).getDetalles()), ids));
        try { reparto.getMontos().put(2L, 0L); throw new AssertionError("Mutable"); }
        catch (UnsupportedOperationException expected) { checks++; }
        for (long total = 0; total < 300; total++)
            for (int n = 2; n <= 10; n++) {
                List<Long> participantes = new ArrayList<>();
                for (long id = n; id > 0; id--) participantes.add(id);
                Map<Long,Long> partes = Reparto.equitativo(compra(total), participantes).getMontos();
                check(partes.values().stream().reduce(0L, Math::addExact) == total);
                check(Collections.max(partes.values()) - Collections.min(partes.values()) <= 1);
            }
        System.out.println("PASS: " + checks + " shared domain checks (exactitud, bordes y conservación).");
    }
}
