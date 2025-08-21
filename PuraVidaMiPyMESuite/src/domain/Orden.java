/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package domain;
import data.EstrategiaPago;
import exception.PagoInvalidoException;
import exception.StockInsuficienteException;
import infrastructure.ComprobanteGenerator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 *
 * @author Emesis
 */
/**
 * Representa una orden de venta con ítems, descuentos, impuestos y método de pago.
 * Genera una factura con código de control (HMAC-SHA256).
 */
public class Orden {
    private String id;
    private Cliente cliente;
    private List<ItemOrden> items;
    private double descuento; // %
    private EstrategiaPago metodoPago;
    private double impuesto; // % (13% por defecto)

    /**
     * Crea una nueva orden.
     * @param id Identificador único de la orden
     * @param cliente Cliente que realiza la compra
     * @param metodoPago Estrategia de pago (efectivo, tarjeta, etc.)
     */
    public Orden(String id, Cliente cliente, EstrategiaPago metodoPago) {
        this.id = id;
        this.cliente = cliente;
        this.items = new ArrayList<>();
        this.metodoPago = metodoPago;
        this.descuento = 0;
        this.impuesto = 0.13; // 13%
    }

    /**
     * Agrega un ítem a la orden. Verifica stock.
     */
    public void agregarItem(Producto producto, int cantidad) throws StockInsuficienteException {
        if (producto.getStock() < cantidad) {
            throw new StockInsuficienteException(
                "Stock insuficiente para " + producto.getNombre() +
                ". Disponible: " + producto.getStock() + ", solicitado: " + cantidad
            );
        }
        items.add(new ItemOrden(producto, cantidad));
    }

    /**
     * Calcula el subtotal (sin descuentos ni impuestos).
     */
    public double calcularSubtotal() {
        return items.stream()
                .mapToDouble(item -> item.getProducto().getPrecio() * item.getCantidad())
                .sum();
    }

    /**
     * Calcula el total final (con descuento e impuesto).
     */
    public double calcularTotal() {
        double subtotal = calcularSubtotal();
        double conDescuento = subtotal * (1 - descuento / 100);
        double conImpuesto = conDescuento * (1 + impuesto);
        return conImpuesto;
    }

    /**
     * Procesa el pago. Lanza excepción si falla.
     * @return true si el pago fue exitoso
     * @throws PagoInvalidoException si el pago no se puede procesar
     */
    public boolean procesarPago() throws PagoInvalidoException {
        double monto = calcularTotal();
        if (monto <= 0) {
            throw new PagoInvalidoException("El monto a pagar no puede ser cero o negativo.");
        }
        boolean exito = metodoPago.procesarPago(monto);
        if (!exito) {
            throw new PagoInvalidoException("El pago fue rechazado por el método: " + metodoPago.getMetodo());
        }
        return true;
    }

    /**
     * Genera una factura en formato .txt con código de control (HMAC-SHA256).
     * @param rutaArchivo Ruta donde se guardará la factura
     * @throws IOException Si ocurre un error al escribir el archivo
     */
    public void generarFactura(String rutaArchivo) throws IOException, Exception {
        StringBuilder factura = new StringBuilder();
        factura.append("=== FACTURA ===\n");
        factura.append("ID: ").append(id).append("\n");
        factura.append("Cliente: ").append(cliente.getNombre()).append("\n");
        factura.append("Items:\n");
        for (ItemOrden item : items) {
            Producto p = item.getProducto();
            factura.append("- ").append(p.getNombre())
                   .append(" x").append(item.getCantidad())
                   .append(" = $").append(String.format("%.2f", p.getPrecio() * item.getCantidad()))
                   .append("\n");
        }
        factura.append("Subtotal: $").append(String.format("%.2f", calcularSubtotal())).append("\n");
        factura.append("Descuento: ").append(descuento).append("%\n");
        factura.append("Impuesto: ").append(String.format("%.2f", impuesto * 100)).append("%\n");
        factura.append("Total: $").append(String.format("%.2f", calcularTotal())).append("\n");
        factura.append("Método de pago: ").append(metodoPago.getMetodo()).append("\n");

        String contenido = factura.toString();
        String codigoControl = ComprobanteGenerator.generarHMAC(contenido, "llave-secreta-puravida");

        factura.append("Código de control: ").append(codigoControl).append("\n");

        Files.write(Paths.get(rutaArchivo), factura.toString().getBytes());
    }

    // —————————————————————— GETTERS ——————————————————————

    public String getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public List<ItemOrden> getItems() {
        return new ArrayList<>(items);
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        if (descuento < 0) {
            throw new IllegalArgumentException("El descuento no puede ser negativo");
        }
        this.descuento = descuento;
    }

    public double getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(double impuesto) {
        if (impuesto < 0) {
            throw new IllegalArgumentException("El impuesto no puede ser negativo");
        }
        this.impuesto = impuesto;
    }

    public EstrategiaPago getMetodoPago() {
        return metodoPago;
    }
}