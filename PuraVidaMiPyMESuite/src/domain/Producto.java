/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package domain;

/**
 *
 * @author Emesis
 */

//Variables
public class Producto {
    private String codigo;
    private String nombre;
    private String categoria;
    private double precio;
    private int stockMin;
    private int stockActual;

    // Constructor
    public Producto(String codigo, String nombre, String categoria, double precio, int stockMin, int stockActual) {
        if (precio <= 0) throw new PrecioInvalidoException("Precio debe ser mayor a 0");
        if (stockMin < 0 || stockActual < 0) throw new StockNegativoException("Stock no puede ser negativo");
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.stockMin = stockMin;
        this.stockActual = stockActual;
    }

    // Getters y setters
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public double getPrecio() { return precio; }
    public int getStockMin() { return stockMin; }
    public int getStockActual() { return stockActual; }

    public void setPrecio(double precio) {
        if (precio <= 0) throw new PrecioInvalidoException("Precio inválido: " + precio);
        this.precio = precio;
    }

    public void setStockActual(int stockActual) {
        if (stockActual < 0) throw new StockNegativoException("Stock actual no puede ser negativo");
        this.stockActual = stockActual;
    }

    @Override
    public String toString() {
        return codigo + "," + nombre + "," + categoria + "," + precio + "," + stockMin + "," + stockActual;
    }
}
