import com.aik1a.localcash.domain.*;
import java.util.*;

public final class FeedbackDomainChecks {
    private static int checks;
    private static void difference(Divisa divisa, long total, long propio, long contacto, String esperado) {
        Compra compra = new Compra(1, "Prueba", Compra.Estado.REALIZADA, 1, null,
                Collections.singletonList(new DetalleCompra("Producto", 1, total)), divisa);
        try {
            Reparto.personalizadoConUsuario(compra, Collections.singletonList(1L), Collections.singletonList(contacto), propio);
            throw new AssertionError("Debía rechazarse una suma distinta.");
        } catch (IllegalArgumentException error) {
            if (!error.getMessage().contains(esperado) || !error.getMessage().contains("Total:") || !error.getMessage().contains("Ingresado:"))
                throw new AssertionError(error.getMessage());
            checks++;
        }
    }
    public static void main(String[] args) {
        difference(Divisa.CLP, 10000, 3000, 5000, "Faltan CLP 20");
        difference(Divisa.CLP, 10000, 6000, 5000, "Sobran CLP 10");
        difference(Divisa.USD, 1000, 300, 500, "Faltan USD 2,00");
        difference(Divisa.USD, 0, 0, 1, "Sobran USD 0,01");
        Compra limite = new Compra(1, "Límite", Compra.Estado.REALIZADA, 1, null,
                Collections.singletonList(new DetalleCompra("X", 1, Long.MAX_VALUE)), Divisa.USD);
        try {
            Reparto.personalizadoConUsuario(limite, Collections.singletonList(1L), Collections.singletonList(1L), Long.MAX_VALUE);
            throw new AssertionError("Debía rechazar overflow.");
        } catch (ArithmeticException expected) { checks++; }
        System.out.println("PASS: " + checks + " mensajes de diferencia, monedas, cero y overflow.");
    }
}
