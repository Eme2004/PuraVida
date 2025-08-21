/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application;
import domain.Producto;
import domain.Cliente;
import domain.Orden;
import domain.ItemOrden;
import workers.FidelizacionWorker;  
import java.util.*;


/**
 *
 * @author Emesis
 */
/**
 * Servicio encargado del análisis de operaciones y generación de métricas.
 * Incluye dashboard textual y tarea pesada (fidelización) con SwingWorker.
 */
/**
 * Servicio de análisis para generar métricas de negocio y ejecutar tareas pesadas.
 * Proporciona un dashboard textual y permite recalcular fidelización en segundo plano.
 */
/**
 * Servicio de análisis para generar métricas de negocio y ejecutar tareas pesadas.
 * Proporciona un dashboard textual y permite recalcular fidelización en segundo plano.
 */
public class AnalisisService {
    private List<Producto> productos;
    private List<Cliente> clientes;
    private List<Orden> ordenes;

    /**
     * Crea un servicio de análisis con los datos actuales.
     * @param productos Lista de productos (puede ser null)
     * @param clientes Lista de clientes (puede ser null)
     * @param ordenes Lista de órdenes (puede ser null)
     */
    public AnalisisService(List<Producto> productos, List<Cliente> clientes, List<Orden> ordenes) {
        this.productos = new ArrayList<>(productos != null ? productos : new ArrayList<>());
        this.clientes = new ArrayList<>(clientes != null ? clientes : new ArrayList<>());
        this.ordenes = new ArrayList<>(ordenes != null ? ordenes : new ArrayList<>());
    }

    /**
     * Genera un dashboard textual con las métricas clave del negocio.
     * @return String con el resumen de análisis
     */
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
        List<Map.Entry<Producto, Double>> top5 = ingresos.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .toList();

        if (top5.isEmpty()) {
            sb.append("No hay ventas registradas.\n");
        } else {
            top5.forEach(e -> sb.append("• ").append(e.getKey().getNombre())
                    .append(": $").append(String.format("%.2f", e.getValue())).append("\n"));
        }

        // Clientes frecuentes
        sb.append("\nCLIENTES FRECUENTES (3+ compras)\n");
        Map<Cliente, Integer> compras = new HashMap<>();
        for (Orden o : ordenes) {
            Cliente c = o.getCliente();
            if (c != null) {
                compras.merge(c, 1, Integer::sum);
            }
        }
        List<Map.Entry<Cliente, Integer>> frecuentes = compras.entrySet().stream()
                .filter(e -> e.getValue() >= 3)
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .toList();

        if (frecuentes.isEmpty()) {
            sb.append("No hay clientes frecuentes.\n");
        } else {
            frecuentes.forEach(e -> sb.append("• ").append(e.getKey().getNombre())
                    .append(": ").append(e.getValue()).append(" compras\n"));
        }

        // Existencias críticas
        sb.append("\nEXISTENCIAS CRÍTICAS (stock < 5)\n");
        List<Producto> criticos = productos.stream()
                .filter(p -> p.getStock() < 5)
                .sorted(Comparator.comparing(Producto::getStock))
                .toList();

        if (criticos.isEmpty()) {
            sb.append("No hay productos con existencias críticas.\n");
        } else {
            criticos.forEach(p -> sb.append("• ").append(p.getNombre())
                    .append(" (").append(p.getCodigo()).append("): ")
                    .append(p.getStock()).append("\n"));
        }

        return sb.toString();
    }

    /**
     * Crea una tarea en segundo plano para recalcular puntos de fidelización.
     * Usa la clase FidelizacionWorker concreta.
     * @param listener Para recibir eventos de progreso, completado o cancelado
     * @return FidelizacionWorker configurado
     */
    public FidelizacionWorker crearTareaRecalcularFidelizacion(ProgresoListener listener) {
        return new FidelizacionWorker(listener);  
    }

    /**
     * Interfaz para recibir eventos de progreso de tareas pesadas.
     */
    @FunctionalInterface
    public interface ProgresoListener {
        void onProgreso(int porcentaje);
        default void onCompletado() {}
        default void onCancelado() {}
    }

    // —————————————————————— GETTERS ——————————————————————

    public List<Producto> getProductos() { return new ArrayList<>(productos); }
    public List<Cliente> getClientes() { return new ArrayList<>(clientes); }
    public List<Orden> getOrdenes() { return new ArrayList<>(ordenes); }
}