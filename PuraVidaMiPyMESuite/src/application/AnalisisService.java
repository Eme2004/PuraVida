/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application;
import domain.Cliente;
import domain.Producto;
import domain.Orden;
import domain.ItemOrden;
import javax.swing.SwingWorker;
import java.util.*;

/**
 *
 * @author TheJPlay2006
 */
/**
 * Servicio encargado del análisis de operaciones y generación de métricas.
 * Incluye dashboard textual y tarea pesada (fidelización) con SwingWorker.
 */
public class AnalisisService {
    private List<Producto> productos;
    private List<Cliente> clientes;
    private List<Orden> ordenes;

    public AnalisisService(List<Producto> productos, List<Cliente> clientes, List<Orden> ordenes) {
        this.productos = productos;
        this.clientes = clientes;
        this.ordenes = ordenes;
    }

    public String generarDashboard() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== DASHBOARD DE OPERACIONES ===\n\n");

        // Top 5 productos por ingresos
        sb.append("TOP 5 PRODUCTOS POR INGRESOS\n");
        Map<Producto, Double> ingresos = new HashMap<>();
        for (Orden o : ordenes) {
            for (ItemOrden i : o.getItems()) {
                ingresos.merge(i.getProducto(), i.getTotal(), Double::sum);
            }
        }
        ingresos.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .forEach(e -> sb.append("• ").append(e.getKey().getNombre()).append(": $").append(String.format("%.2f", e.getValue())).append("\n"));

        // Clientes frecuentes
        sb.append("\nCLIENTES FRECUENTES\n");
        Map<Cliente, Integer> compras = new HashMap<>();
        for (Orden o : ordenes) {
            compras.merge(o.getCliente(), 1, Integer::sum);
        }
        compras.entrySet().stream()
                .filter(e -> e.getValue() >= 3)
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(e -> sb.append("• ").append(e.getKey().getNombre()).append(": ").append(e.getValue()).append(" compras\n"));

        // Existencias críticas
        sb.append("\nEXISTENCIAS CRÍTICAS (stock < 5)\n");
        productos.stream()
                .filter(p -> p.getStock() < 5)
                .forEach(p -> sb.append("• ").append(p.getNombre()).append(" (").append(p.getCodigo()).append("): ").append(p.getStock()).append("\n"));

        return sb.toString();
    }

    public SwingWorker<Void, Integer> crearTareaRecalcularFidelizacion(ProgresoListener listener) {
        return new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                int total = clientes.size();
                for (int i = 0; i <= total; i++) {
                    if (isCancelled()) return null;
                    Thread.sleep(100);
                    publish((i * 100) / total);
                }
                return null;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                int progreso = chunks.get(chunks.size() - 1);
                listener.onProgreso(progreso);
            }

            @Override
            protected void done() {
                if (isCancelled()) {
                    listener.onCancelado();
                } else {
                    listener.onCompletado();
                }
            }
        };
    }

    @FunctionalInterface
    public interface ProgresoListener {
        void onProgreso(int porcentaje);
        default void onCompletado() {}
        default void onCancelado() {}
    }
}
