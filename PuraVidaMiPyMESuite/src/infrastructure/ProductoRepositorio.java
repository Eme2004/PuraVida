/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package infrastructure;
import domain.Producto;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 *
 * @author Emesis
 */
/**
 * Repositorio especializado en la persistencia de Productos.
 * Gestiona lectura y escritura en archivo CSV.
 */
public class ProductoRepositorio {
    private static final String RUTA_ARCHIVO = "data/inventario.csv";

    /**
     * Guarda una lista completa de productos en el archivo CSV.
     */
    public void guardarProductos(List<Producto> productos) throws IOException {
        Path path = Paths.get(RUTA_ARCHIVO);
        Path dir = path.getParent();
        if (dir != null && !Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(RUTA_ARCHIVO), java.nio.charset.StandardCharsets.UTF_8))) {
            writer.println("codigo,nombre,precio,stock");

            for (Producto p : productos) {
                writer.printf("%s,%s,%.2f,%d%n",
                        p.getCodigo(),
                        p.getNombre(),
                        p.getPrecio(),
                        p.getStock());
            }
        }
    }

    /**
     * Lee todos los productos del archivo CSV.
     * Si el archivo no existe, devuelve una lista vacía.
     */
    public List<Producto> leerProductos() throws IOException {
        Path path = Paths.get(RUTA_ARCHIVO);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        List<Producto> productos = new ArrayList<>();
        List<String> lineas = Files.readAllLines(path, java.nio.charset.StandardCharsets.UTF_8);

        boolean esCabecera = true;
        for (String linea : lineas) {
            if (linea.trim().isEmpty()) continue;
            if (esCabecera) {
                esCabecera = false;
                continue; // Saltar cabecera
            }

            Producto producto = parsearLinea(linea);
            if (producto != null) {
                productos.add(producto);
            }
        }
        return productos;
    }

    /**
     * Convierte una línea CSV en un objeto Producto.
     */
    private Producto parsearLinea(String linea) {
        try {
            String[] campos = linea.split(",", 4);
            if (campos.length < 4) {
                System.err.println("Línea con campos insuficientes: " + linea);
                return null;
            }

            String codigo = campos[0].trim();
            String nombre = campos[1].trim();
            double precio = Double.parseDouble(campos[2].trim());
            int stock = Integer.parseInt(campos[3].trim());

            if (codigo.isEmpty()) {
                System.err.println("Código vacío en línea: " + linea);
                return null;
            }
            if (precio < 0) {
                System.err.println("Precio negativo ignorado: " + linea);
                return null;
            }
            if (stock < 0) {
                System.err.println("Stock negativo ignorado: " + linea);
                return null;
            }

            return new Producto(codigo, nombre, precio, stock);

        } catch (NumberFormatException e) {
            System.err.println("Error numérico en línea CSV: " + linea + " → " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Error inesperado al parsear línea: " + linea + " → " + e.getMessage());
            return null;
        }
    }

    /**
     * Busca un producto por su código.
     */
    public Producto buscarPorCodigo(String codigo) throws IOException {
        return leerProductos().stream()
                .filter(p -> p.getCodigo().equals(codigo))
                .findFirst()
                .orElse(null);
    }

    /**
     * Agrega un nuevo producto.
     */
    public void agregarProducto(Producto producto) throws IOException {
        List<Producto> productos = leerProductos();
        if (productos.stream().anyMatch(p -> p.getCodigo().equals(producto.getCodigo()))) {
            throw new IllegalArgumentException("Ya existe un producto con código: " + producto.getCodigo());
        }
        productos.add(producto);
        guardarProductos(productos);
    }

    /**
     * Actualiza un producto existente.
     */
    public void actualizarProducto(Producto producto) throws IOException {
        List<Producto> productos = leerProductos();
        boolean encontrado = false;
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getCodigo().equals(producto.getCodigo())) {
                productos.set(i, producto);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            throw new IllegalArgumentException("Producto no encontrado: " + producto.getCodigo());
        }
        guardarProductos(productos);
    }

    /**
     * Elimina un producto por código.
     */
    public void eliminarProducto(String codigo) throws IOException {
        List<Producto> productos = leerProductos();
        boolean eliminado = productos.removeIf(p -> p.getCodigo().equals(codigo));
        if (eliminado) {
            guardarProductos(productos);
        } else {
            throw new IllegalArgumentException("Producto no encontrado: " + codigo);
        }
    }
}