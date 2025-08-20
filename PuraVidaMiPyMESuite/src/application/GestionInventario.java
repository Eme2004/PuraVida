/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application;

import domain.Producto;
import infrastructure.ProductoRepositorioCSV;
import domain.ProductoDuplicadoException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Emesis
 */
public class GestionInventario {
    private ProductoRepositorioCSV repositorio = new ProductoRepositorioCSV();
    private List<Producto> productos = new ArrayList<>();

    public void cargarDatos() throws IOException {
        repositorio.cargar();
        productos.clear();
        productos.addAll(repositorio.getProductos());
    }

    public List<Producto> listarTodos() {
        return new ArrayList<>(productos);
    }

    public void agregarProducto(Producto producto) throws ProductoDuplicadoException {
        repositorio.agregar(producto);
        productos.add(producto);
        guardar();
    }

    public void actualizarProducto(Producto producto) {
        repositorio.actualizar(producto);
        int index = productos.indexOf(producto);
        if (index >= 0) productos.set(index, producto);
        guardar();
    }

    public void eliminarProducto(String codigo) {
        repositorio.eliminar(codigo);
        productos.removeIf(p -> p.getCodigo().equals(codigo));
        guardar();
    }

    public List<Producto> buscar(String texto) {
        return productos.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(texto.toLowerCase()) ||
                             p.getCodigo().toLowerCase().contains(texto.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Producto> filtrarPorCategoria(String categoria) {
        return productos.stream()
                .filter(p -> p.getCategoria().equals(categoria))
                .collect(Collectors.toList());
    }

    public List<Producto> ordenarPorPrecioDesc() {
        return productos.stream()
                .sorted((a, b) -> Double.compare(b.getPrecio(), a.getPrecio()))
                .collect(Collectors.toList());
    }

    public List<Producto> ordenarPorNombreAsc() {
        return productos.stream()
                .sorted(Comparator.comparing(Producto::getNombre))
                .collect(Collectors.toList());
    }

    public List<Producto> ordenarPorStockAsc() {
        return productos.stream()
                .sorted(Comparator.comparingInt(Producto::getStockActual))
                .collect(Collectors.toList());
    }

    private void guardar() {
        try {
            repositorio.guardarTodos(productos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Importación CSV con reporte
    public String importarDesdeCSV(String ruta) {
        StringBuilder reporte = new StringBuilder();
        reporte.append("REPORTE DE IMPORTACIÓN\n");
        reporte.append("**********************\n");

        int altas = 0, actualizaciones = 0, errores = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            int lineaNum = 0;
            while ((linea = br.readLine()) != null) {
                lineaNum++;
                if (linea.trim().isEmpty() || linea.startsWith("codigo,")) continue;

                try {
                    String[] campos = linea.split(",");
                    if (campos.length != 6) throw new IllegalArgumentException("Campos incompletos");

                    String codigo = campos[0];
                    Producto nuevo = new Producto(
                        codigo, campos[1], campos[2],
                        Double.parseDouble(campos[3]),
                        Integer.parseInt(campos[4]),
                        Integer.parseInt(campos[5])
                    );

                    if (repositorio.buscarPorCodigo(codigo) != null) {
                        repositorio.actualizar(nuevo);
                        productos.removeIf(p -> p.getCodigo().equals(codigo));
                        productos.add(nuevo);
                        actualizaciones++;
                        reporte.append("Línea ").append(lineaNum).append(": Actualizado - ").append(codigo).append("\n");
                    } else {
                        repositorio.agregar(nuevo);
                        productos.add(nuevo);
                        altas++;
                        reporte.append("Línea ").append(lineaNum).append(": Nuevo - ").append(codigo).append("\n");
                    }
                } catch (Exception e) {
                    errores++;
                    reporte.append("Línea ").append(lineaNum).append(": ERROR - ").append(e.getMessage()).append("\n");
                }
            }
            guardar();
        } catch (IOException e) {
            reporte.append("Error de lectura: ").append(e.getMessage());
        }

        reporte.append("\nResumen: Altas=").append(altas)
                .append(", Actualizaciones=").append(actualizaciones)
                .append(", Errores=").append(errores);

        // Guardar reporte
        try (PrintWriter pw = new PrintWriter("reporte-importacion-productos.txt")) {
            pw.println(reporte.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return reporte.toString();
    }
}