/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package infrastructure;

import domain.Producto;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Emesis
 */
/**
 * Repositorio encargado de leer y guardar productos en un archivo CSV.
 * Proporciona persistencia básica para el módulo de Catálogo & Inventario.
 */
public class RepositorioCSV {
    private static final String RUTA_ARCHIVO = "data/inventario.csv";

    /**
     * Lee todos los productos desde el archivo CSV.
     * Si el archivo no existe, devuelve una lista vacía.
     *
     * @return Lista de productos leídos.
     * @throws IOException Si ocurre un error de lectura.
     */
    public List<Producto> leerProductos() throws IOException {
        List<Producto> productos = new ArrayList<>();
        Path path = Paths.get(RUTA_ARCHIVO);

        // Si el archivo no existe, crearlo con cabecera
        if (!Files.exists(path)) {
            crearArchivoInicial();
            return productos; // Devuelve lista vacía
        }

        // Leer líneas del archivo
        List<String> lineas = Files.readAllLines(path, StandardCharsets.UTF_8);
        boolean esPrimeraLinea = true;

        for (String linea : lineas) {
            if (linea.trim().isEmpty()) continue;

            if (esPrimeraLinea) {
                esPrimeraLinea = false;
                // Validar o ignorar cabecera
                if (!validarCabecera(linea)) {
                    System.out.println("Advertencia: Cabecera no estándar en CSV.");
                }
                continue;
            }

            Producto producto = parsearLinea(linea);
            if (producto != null) {
                productos.add(producto);
            }
        }

        return productos;
    }

    /**
     * Guarda la lista de productos en el archivo CSV, sobrescribiendo el contenido.
     *
     * @param productos Lista de productos a guardar.
     * @throws IOException Si ocurre un error de escritura.
     */
    public void guardarProductos(List<Producto> productos) throws IOException {
        Path path = Paths.get(RUTA_ARCHIVO);
        Path directorio = path.getParent();

        // Crear directorio si no existe
        if (directorio != null && !Files.exists(directorio)) {
            Files.createDirectories(directorio);
        }

        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(
                        new FileOutputStream(RUTA_ARCHIVO), StandardCharsets.UTF_8), true)) {

            // Escribir cabecera
            writer.println("codigo,nombre,precio,stock");

            // Escribir cada producto
            for (Producto p : productos) {
                writer.printf("%s,%s,%.2f,%d%n",
                        p.getCodigo(),
                        p.getNombre(),
                        p.getPrecio(),
                        p.getStock());
            }
        }
    }

    // —————————————————————— MÉTODOS AUXILIARES ——————————————————————

    private boolean validarCabecera(String linea) {
        String[] headers = linea.trim().split(",");
        if (headers.length != 4) return false;
        return headers[0].trim().equalsIgnoreCase("codigo") &&
               headers[1].trim().equalsIgnoreCase("nombre") &&
               headers[2].trim().equalsIgnoreCase("precio") &&
               headers[3].trim().equalsIgnoreCase("stock");
    }

    private Producto parsearLinea(String linea) {
        try {
            String[] campos = linea.split(",");
            if (campos.length < 4) {
                System.err.println("Línea CSV inválida (campos insuficientes): " + linea);
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
            System.err.println("Error al parsear número en línea CSV: " + linea);
            return null;
        } catch (Exception e) {
            System.err.println("Error inesperado al procesar línea CSV: " + linea);
            return null;
        }
    }

    private void crearArchivoInicial() throws IOException {
        Path path = Paths.get(RUTA_ARCHIVO);
        Path directorio = path.getParent();
        if (directorio != null) {
            Files.createDirectories(directorio);
        }
        Files.createFile(path);
        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(
                        new FileOutputStream(RUTA_ARCHIVO), StandardCharsets.UTF_8))) {
            writer.println("codigo,nombre,precio,stock");
        }
    }
}
