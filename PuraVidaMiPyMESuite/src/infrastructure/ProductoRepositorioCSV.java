/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package infrastructure;
import domain.Producto;
import domain.ProductoDuplicadoException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
/**
 *
 * @author Emesis
 */
public class ProductoRepositorioCSV {
    private static final String ARCHIVO = "data/productos.csv";
    private Map<String, Producto> productosPorCodigo = new HashMap<>();

    public void cargar() throws IOException {
        if (!Files.exists(Paths.get(ARCHIVO))) return;

        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty() || linea.startsWith("codigo,")) continue;
                String[] campos = linea.split(",");
                if (campos.length == 6) {
                    String codigo = campos[0];
                    Producto p = new Producto(
                        codigo,
                        campos[1],
                        campos[2],
                        Double.parseDouble(campos[3]),
                        Integer.parseInt(campos[4]),
                        Integer.parseInt(campos[5])
                    );
                    productosPorCodigo.put(codigo, p);
                }
            }
        }
    }

    public void guardarTodos(List<Producto> productos) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO))) {
            pw.println("codigo,nombre,categoria,precio,stockMin,stockActual");
            for (Producto p : productos) {
                pw.println(p.toString());
            }
        }
    }

    public Collection<Producto> getProductos() {
        return productosPorCodigo.values();
    }

    public Producto buscarPorCodigo(String codigo) {
        return productosPorCodigo.get(codigo);
    }

    public void agregar(Producto p) throws ProductoDuplicadoException {
        if (productosPorCodigo.containsKey(p.getCodigo())) {
            throw new ProductoDuplicadoException("Código duplicado: " + p.getCodigo());
        }
        productosPorCodigo.put(p.getCodigo(), p);
    }

    public void actualizar(Producto p) {
        productosPorCodigo.put(p.getCodigo(), p);
    }

    public void eliminar(String codigo) {
        productosPorCodigo.remove(codigo);
    }
}

