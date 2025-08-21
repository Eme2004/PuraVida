package application;
import domain.Producto;
import exception.ProductoDuplicadoException;
import infrastructure.ProductoRepositorio;  
import java.util.*;
import java.io.*;
import java.util.stream.Collectors;

public class GestionInventario {
    private List<Producto> productos;
    private ProductoRepositorio repositorio;  

    public GestionInventario(ProductoRepositorio repositorio) {  
        this.repositorio = repositorio;
        this.productos = new ArrayList<>();
        cargarDesdeCSV();
    }

    private void cargarDesdeCSV() {
        try {
            productos = repositorio.leerProductos();
        } catch (IOException e) {
            System.err.println("Error cargando inventario: " + e.getMessage());
        }
    }

    // Alta
    public void agregarProducto(Producto producto) throws ProductoDuplicadoException {
        if (productos.contains(producto)) {
            throw new ProductoDuplicadoException("Producto duplicado: " + producto.getCodigo());
        }
        productos.add(producto);
        guardarEnCSV();
    }

    // Edición
    public void editarProducto(Producto producto) {
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getCodigo().equals(producto.getCodigo())) {
                productos.set(i, producto);
                guardarEnCSV();
                return;
            }
        }
        throw new RuntimeException("Producto no encontrado: " + producto.getCodigo());
    }

    // Baja
    public void eliminarProducto(String codigo) {
        boolean eliminado = productos.removeIf(p -> p.getCodigo().equals(codigo));
        if (eliminado) {
            guardarEnCSV();
        }
    }

    // Búsqueda por nombre
    public List<Producto> buscarPorNombre(String nombre) {
        return productos.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .collect(Collectors.toList());
    }

    // Ordenar por precio ascendente
    public List<Producto> ordenarPorPrecioAsc() {
        return productos.stream()
                .sorted(Comparator.comparing(Producto::getPrecio))
                .collect(Collectors.toList());
    }

    // Ordenar por stock descendente
    public List<Producto> ordenarPorStockDesc() {
        return productos.stream()
                .sorted(Comparator.comparing(Producto::getStock).reversed())
                .collect(Collectors.toList());
    }

    // Detectar duplicados
    public boolean tieneDuplicados() {
        Set<String> codigos = new HashSet<>();
        for (Producto p : productos) {
            if (!codigos.add(p.getCodigo())) {
                return true;
            }
        }
        return false;
    }

    // Guardar en CSV
    private void guardarEnCSV() {
        try {
            repositorio.guardarProductos(productos);  
        } catch (IOException e) {
            System.err.println("Error guardando inventario: " + e.getMessage());
        }
    }

    public List<Producto> getProductos() {
        return new ArrayList<>(productos);
    }
}