/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package infrastructure;
import domain.Producto;
import domain.Cliente;
import java.util.List;
import java.util.stream.Collectors;
/**
 *
 * @author Emesis
 */
public class JsonManualSerializer {

    /**
     * Convierte una lista de productos a JSON manualmente.
     */
    public static String productosToJson(List<Producto> productos) {
        return productos.stream()
                .map(p -> String.format(
                    "  {\"codigo\": \"%s\", \"nombre\": \"%s\", \"precio\": %.2f, \"stock\": %d}",
                    p.getCodigo(), p.getNombre(), p.getPrecio(), p.getStock()))
                .collect(Collectors.joining(",\n", "[\n", "\n]"));
    }

    /**
     * Convierte una lista de clientes a JSON (sin cédula en claro).
     * Solo exporta nombre, teléfono y email (la cédula no se incluye por seguridad).
     */
    public static String clientesToJson(List<Cliente> clientes) {
        return clientes.stream()
                .map(c -> String.format(
                    "  {\"nombre\": \"%s\", \"telefono\": \"%s\", \"email\": \"%s\"}",
                    c.getNombre(), c.getTelefono(), c.getEmail()))
                .collect(Collectors.joining(",\n", "[\n", "\n]"));
    }
}