/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package domain;

/**
 *
 * @author Emesis
 */
public class ItemOrden {
    private Producto producto;
    private int cantidad;

    public ItemOrden(Producto producto, int cantidad) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        this.producto = producto;
        this.cantidad = cantidad;
    }

    // Getters
    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    // Setters (opcional)
    public void setCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        this.cantidad = cantidad;
    }

    // Precio total del item
    public double getTotal() {
        return producto.getPrecio() * cantidad;
    }

    @Override
    public String toString() {
        return "ItemOrden{" +
                "producto=" + producto.getNombre() +
                ", cantidad=" + cantidad +
                ", total=$" + String.format("%.2f", getTotal()) +
                '}';
    }
}