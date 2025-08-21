/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application;
import domain.Cliente;
import infrastructure.ClienteRepositorio;import java.util.*;
import java.util.stream.Collectors;
/**
 *
 * @author TheJPlay2006
 */
/**
 * Servicio para gestionar la lógica de negocio de clientes.
 * Garantiza que la cédula se almacene cifrada y solo se muestre en texto claro bajo ciertas condiciones.
 */
public class ClienteService {
    private ClienteRepositorio repositorio;

    public ClienteService(ClienteRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Registra un nuevo cliente. La cédula se almacena cifrada en disco.
     *
     * @param cliente Cliente a registrar
     * @throws IllegalArgumentException si la cédula ya existe
     */
    public void registrarCliente(Cliente cliente) {
        Objects.requireNonNull(cliente, "El cliente no puede ser nulo");

        // Validar cédula
        if (cliente.getCedula() == null || cliente.getCedula().trim().isEmpty()) {
            throw new IllegalArgumentException("La cédula no puede estar vacía");
        }

        // Evitar duplicados
        try {
            Cliente existente = repositorio.buscarPorCedula(cliente.getCedula());
            if (existente != null) {
                throw new IllegalArgumentException("Ya existe un cliente con cédula: " + cliente.getCedula());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar duplicados", e);
        }

        try {
            repositorio.guardarCliente(cliente);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar cliente", e);
        }
    }

    /**
     * Actualiza un cliente existente.
     *
     * @param cliente Cliente con datos actualizados
     */
    public void actualizarCliente(Cliente cliente) {
        Objects.requireNonNull(cliente, "El cliente no puede ser nulo");

        try {
            Cliente actual = repositorio.buscarPorCedula(cliente.getCedula());
            if (actual == null) {
                throw new IllegalArgumentException("Cliente no encontrado: " + cliente.getCedula());
            }
            repositorio.guardarCliente(cliente);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar cliente", e);
        }
    }

    /**
     * Elimina un cliente por su cédula.
     *
     * @param cedula Cédula del cliente a eliminar
     */
    public void eliminarCliente(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) {
            throw new IllegalArgumentException("La cédula no puede estar vacía");
        }
        try {
            repositorio.eliminarCliente(cedula);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar cliente", e);
        }
    }

    /**
     * Busca un cliente por su cédula.
     *
     * @param cedula Cédula a buscar
     * @return Cliente encontrado o null si no existe
     */
    public Cliente buscarPorCedula(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) return null;
        try {
            return repositorio.buscarPorCedula(cedula);
        } catch (Exception e) {
            System.err.println("Error al buscar cliente por cédula: " + e.getMessage());
            return null;
        }
    }

    /**
     * Busca clientes por nombre (búsqueda parcial, insensible a mayúsculas).
     *
     * @param nombre Nombre o parte del nombre
     * @return Lista de clientes que coinciden
     */
    public List<Cliente> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return repositorio.leerTodos().stream()
                    .filter(c -> c.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error al buscar clientes por nombre: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Devuelve todos los clientes registrados.
     *
     * @return Lista de todos los clientes
     */
    public List<Cliente> obtenerTodos() {
        try {
            return new ArrayList<>(repositorio.leerTodos());
        } catch (Exception e) {
            System.err.println("Error al cargar clientes: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Verifica si existe un cliente con la cédula dada.
     *
     * @param cedula Cédula a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existeCliente(String cedula) {
        return buscarPorCedula(cedula) != null;
    }
}