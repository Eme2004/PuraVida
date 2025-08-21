/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package infrastructure;
import domain.Cliente;
import domain.Producto;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
/**
 *
 * @author Emesis
 */
/**
 * Repositorio para gestionar la persistencia de Clientes.
 * La cédula se almacena cifrada en disco.
 * Solo se descripta cuando se carga en memoria (y solo se muestra en UI bajo rol Admin).
 */
public class ClienteRepositorio {
    private static final String RUTA_ARCHIVO = "data/clientes.csv";
    private static final String CLAVE_CIFRADO = "clave-16-o-32-caracteres-puravida"; // Debe ser 16 o 32 chars

    private CifradorAES cifrador;

    public ClienteRepositorio() throws Exception {
        this.cifrador = new CifradorAES(CLAVE_CIFRADO);
    }

    /**
     * Guarda un cliente en el archivo CSV con cédula cifrada.
     */
    public void guardarCliente(Cliente cliente) throws Exception {
        List<Cliente> clientes = leerTodos();
        
        // Si ya existe, lo removemos para actualizarlo
        clientes.removeIf(c -> c.getCedula().equals(cliente.getCedula()));
        clientes.add(cliente);

        guardarLista(clientes);
    }

    /**
     * Guarda una lista completa de clientes (usado internamente).
     */
    private void guardarLista(List<Cliente> clientes) throws Exception {
        Path path = Paths.get(RUTA_ARCHIVO);
        Path dir = path.getParent();
        if (dir != null && !Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(RUTA_ARCHIVO), java.nio.charset.StandardCharsets.UTF_8))) {
            writer.println("cedula_cifrada,nombre,telefono,email");

            for (Cliente c : clientes) {
                String cedulaCifrada = cifrador.cifrar(c.getCedula());
                writer.printf("%s,%s,%s,%s%n",
                        cedulaCifrada,
                        c.getNombre(),
                        c.getTelefono(),
                        c.getEmail());
            }
        }
    }

    /**
     * Lee todos los clientes del archivo CSV.
     * Las cédulas se cargan descifradas en memoria.
     */
    public List<Cliente> leerTodos() throws Exception {
        Path path = Paths.get(RUTA_ARCHIVO);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        List<Cliente> clientes = new ArrayList<>();
        List<String> lineas = Files.readAllLines(path, java.nio.charset.StandardCharsets.UTF_8);

        boolean esCabecera = true;
        for (String linea : lineas) {
            if (linea.trim().isEmpty()) continue;
            if (esCabecera) {
                esCabecera = false;
                continue; // Saltar cabecera
            }

            Cliente cliente = parsearLinea(linea);
            if (cliente != null) {
                clientes.add(cliente);
            }
        }
        return clientes;
    }

    /**
     * Convierte una línea CSV en un objeto Cliente (descifrando la cédula).
     */
    private Cliente parsearLinea(String linea) {
        try {
            String[] campos = linea.split(",", 4);
            if (campos.length < 4) return null;

            String cedulaCifrada = campos[0].trim();
            String nombre = campos[1].trim();
            String telefono = campos[2].trim();
            String email = campos[3].trim();

            String cedula = cifrador.descifrar(cedulaCifrada);

            return new Cliente(cedula, nombre, telefono, email);

        } catch (Exception e) {
            System.err.println("Error al leer cliente de CSV: " + linea + " → " + e.getMessage());
            return null;
        }
    }

    /**
     * Busca un cliente por cédula (en texto claro).
     */
    public Cliente buscarPorCedula(String cedula) throws Exception {
        return leerTodos().stream()
                .filter(c -> c.getCedula().equals(cedula))
                .findFirst()
                .orElse(null);
    }

    /**
     * Actualiza un cliente existente.
     */
    public void actualizarCliente(Cliente cliente) throws Exception {
        guardarCliente(cliente); // Reusa lógica de guardado
    }

    /**
     * Elimina un cliente por cédula.
     */
    public void eliminarCliente(String cedula) throws Exception {
        List<Cliente> clientes = leerTodos();
        clientes.removeIf(c -> c.getCedula().equals(cedula));
        guardarLista(clientes);
    }
}