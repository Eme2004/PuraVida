/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application;

import data.EstrategiaPago;
import domain.Cliente;
import domain.ItemOrden;
import domain.Orden;
import domain.Producto;
import exception.PagoInvalidoException;
import exception.StockInsuficienteException;
import java.util.Objects;

/**
 *
 * @author Emesis
 */
/**
 * Servicio encargado de gestionar el ciclo de vida de las órdenes.
 * - Creación de órdenes
 * - Validación de stock
 * - Procesamiento de pago
 * - Reducción de inventario
 * - Generación de facturas
 * 
 * Usa el patrón Strategy (pago) y colabora con Catalogo y ClienteService.
 */
public class GestionOrdenes {
    private Catalogo catalogo;
    private FabricaPagos fabricaPagos;

    public GestionOrdenes(Catalogo catalogo, FabricaPagos fabricaPagos) {
        this.catalogo = Objects.requireNonNull(catalogo, "El catálogo no puede ser nulo");
        this.fabricaPagos = Objects.requireNonNull(fabricaPagos, "La fábrica de pagos no puede ser nula");
    }

    /**
     * Crea una nueva orden con cliente y método de pago.
     * 
     * @param id Identificador único de la orden
     * @param cliente Cliente que realiza la compra
     * @param metodoPagoStr Tipo de pago: "efectivo", "tarjeta", "transferencia"
     * @return Nueva orden lista para agregar ítems
     * @throws PagoInvalidoException Si el método de pago no es válido
     */
    public Orden crearOrden(String id, Cliente cliente, String metodoPagoStr) throws PagoInvalidoException {
        Objects.requireNonNull(id, "El ID de la orden no puede ser nulo");
        Objects.requireNonNull(cliente, "El cliente no puede ser nulo");
        if (id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de la orden no puede estar vacío");
        }

        EstrategiaPago metodoPago = fabricaPagos.crearPago(metodoPagoStr);
        return new Orden(id, cliente, metodoPago);
    }

    /**
     * Agrega un ítem a la orden verificando stock disponible.
     * 
     * @param orden Orden a la que se agregará el ítem
     * @param codigoProducto Código del producto (buscado en el catálogo)
     * @param cantidad Cantidad a agregar
     * @throws StockInsuficienteException Si no hay suficiente stock
     * @throws IllegalArgumentException Si el producto no existe
     */
    public void agregarItemAOrden(Orden orden, String codigoProducto, int cantidad) 
            throws StockInsuficienteException, IllegalArgumentException {
        Objects.requireNonNull(orden, "La orden no puede ser nula");
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }

        Producto producto = catalogo.obtenerPorCodigo(codigoProducto);
        if (producto == null) {
            throw new IllegalArgumentException("Producto no encontrado: " + codigoProducto);
        }

        orden.agregarItem(producto, cantidad);
    }

    /**
     * Aplica un descuento porcentual a la orden.
     * 
     * @param orden Orden a modificar
     * @param porcentaje Descuento (0 a 100)
     * @throws IllegalArgumentException Si el porcentaje es inválido
     */
    public void aplicarDescuento(Orden orden, double porcentaje) {
        if (porcentaje < 0 || porcentaje > 100) {
            throw new IllegalArgumentException("El descuento debe estar entre 0% y 100%");
        }
        orden.setDescuento(porcentaje);
    }

    /**
     * Procesa el pago de la orden.
     * 
     * @param orden Orden a pagar
     * @return true si el pago fue exitoso
     * @throws PagoInvalidoException Si el pago falla o el monto es inválido
     */
    public boolean procesarPago(Orden orden) throws PagoInvalidoException {
        return orden.procesarPago(); // Ya maneja la excepción
    }

    /**
     * Finaliza la orden: procesa pago, reduce stock y genera factura.
     * 
     * @param orden Orden a finalizar
     * @param rutaFactura Ruta donde se guardará la factura (ej: "facturas/ORD-001.txt")
     * @throws Exception Si ocurre cualquier error (pago, stock, escritura)
     */
    public void finalizarOrden(Orden orden, String rutaFactura) throws Exception {
        Objects.requireNonNull(orden, "La orden no puede ser nula");
        if (rutaFactura == null || rutaFactura.trim().isEmpty()) {
            throw new IllegalArgumentException("La ruta de la factura no puede estar vacía");
        }

        // 1. Verificar que tenga ítems
        if (orden.getItems().isEmpty()) {
            throw new IllegalStateException("La orden no tiene ítems");
        }

        // 2. Procesar pago
        if (!procesarPago(orden)) {
            throw new PagoInvalidoException("El pago no pudo procesarse");
        }

        // 3. Reducir stock en el catálogo
        for (ItemOrden item : orden.getItems()) {
            Producto p = item.getProducto();
            p.setStock(p.getStock() - item.getCantidad());
            catalogo.editarProducto(p);
        }

        // 4. Generar factura
        orden.generarFactura(rutaFactura);
    }

    // —————————————————————— GETTERS ——————————————————————

    public Catalogo getCatalogo() {
        return catalogo;
    }

    public FabricaPagos getFabricaPagos() {
        return fabricaPagos;
    }
}
