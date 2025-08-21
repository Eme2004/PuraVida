package application;
import domain.Producto;
import exception.ProductoDuplicadoException;
import exception.ProductoNoEncontradoException;
import infrastructure.RepositorioCSV;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
/**
 * Clase principal del módulo Catálogo & Inventario.
 * Gestiona el alta, edición, baja, búsqueda, ordenamiento y detección de duplicados.
 * Usa RepositorioCSV para persistir en disco.
 */
/**
 * Gestiona el catálogo de productos: alta, edición, baja, búsqueda y persistencia.
 * Notifica a observadores cuando hay cambios (para refrescar la UI).
 */
public class Catalogo {
    private List<Producto> productos;
    private RepositorioCSV repositorio;
    private List<ObservadorCatalogo> observadores;

    public Catalogo(RepositorioCSV repositorio) {
        this.repositorio = repositorio;
        this.productos = new ArrayList<>();
        this.observadores = new ArrayList<>();
        cargarDesdeCSV();
    }

    /**
     * Agrega un nuevo producto. Lanza excepción si ya existe.
     */
    public void agregarProducto(Producto producto) throws ProductoDuplicadoException {
        if (existeProducto(producto.getCodigo())) {
            throw new ProductoDuplicadoException("Producto duplicado: " + producto.getCodigo());
        }
        productos.add(producto);
        guardarEnCSV();
        notificarCambio();
    }

    /**
     * Actualiza un producto existente. Lanza excepción si no se encuentra.
     */
    public void editarProducto(Producto producto) throws ProductoNoEncontradoException {
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getCodigo().equals(producto.getCodigo())) {
                productos.set(i, producto);
                guardarEnCSV();
                notificarCambio();
                return;
            }
        }
        throw new ProductoNoEncontradoException("Producto no encontrado: " + producto.getCodigo());
    }

    /**
     * Elimina un producto por su código.
     */
    public void eliminarProducto(String codigo) {
        boolean eliminado = productos.removeIf(p -> p.getCodigo().equals(codigo));
        if (eliminado) {
            guardarEnCSV();
            notificarCambio();
        }
    }

    /**
     * Busca productos por nombre (búsqueda parcial, insensible a mayúsculas).
     */
    public List<Producto> buscarPorNombre(String nombre) {
        return productos.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Devuelve productos ordenados por precio ascendente.
     */
    public List<Producto> ordenarPorPrecioAsc() {
        return productos.stream()
                .sorted(Comparator.comparing(Producto::getPrecio))
                .collect(Collectors.toList());
    }

    /**
     * Devuelve productos ordenados por stock descendente.
     */
    public List<Producto> ordenarPorStockDesc() {
        return productos.stream()
                .sorted(Comparator.comparing(Producto::getStock).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Verifica si hay productos con códigos duplicados.
     */
    public boolean tieneDuplicados() {
        Set<String> codigos = new HashSet<>();
        for (Producto p : productos) {
            if (!codigos.add(p.getCodigo())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Carga los productos desde el archivo CSV.
     */
    private void cargarDesdeCSV() {
        try {
            productos = repositorio.leerProductos();
        } catch (IOException e) {
            System.err.println("Error de E/S al cargar inventario: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado al cargar inventario: " + e.getMessage());
        }
    }

    /**
     * Guarda todos los productos en el archivo CSV.
     */
    private void guardarEnCSV() {
        try {
            repositorio.guardarProductos(productos);
        } catch (IOException e) {
            System.err.println("Error de E/S al guardar inventario: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado al guardar inventario: " + e.getMessage());
        }
    }

    /**
     * Registra un observador para notificar cambios en el catálogo.
     */
    public void agregarObservador(ObservadorCatalogo obs) {
        observadores.add(obs);
    }

    /**
     * Notifica a todos los observadores que el catálogo cambió.
     */
    private void notificarCambio() {
        for (ObservadorCatalogo obs : observadores) {
            obs.catalogoActualizado();
        }
    }

    /**
     * Devuelve una copia de la lista de productos.
     */
    public List<Producto> getProductos() {
        return new ArrayList<>(productos);
    }

    /**
     * Verifica si existe un producto con el código dado.
     */
    public boolean existeProducto(String codigo) {
        return productos.stream().anyMatch(p -> p.getCodigo().equals(codigo));
    }

    /**
     * Obtiene un producto por su código, o null si no existe.
     */
    public Producto obtenerPorCodigo(String codigo) {
        return productos.stream()
                .filter(p -> p.getCodigo().equals(codigo))
                .findFirst()
                .orElse(null);
    }
}