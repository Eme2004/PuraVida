/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

/**
 *
 * @author TheJPlay2006
 */

import application.Catalogo;
import application.ObservadorCatalogo;
import domain.Producto;
import exception.ProductoNoEncontradoException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class CatalogoPanel extends JPanel implements ObservadorCatalogo {
    private Catalogo catalogo;
    private JTable tabla;
    private DefaultTableModel modelo;
    private JTextField txtCodigo, txtNombre, txtPrecio, txtStock;
    private JButton btnAgregar, btnEditar, btnEliminar, btnBuscar;

    public CatalogoPanel(Catalogo catalogo) {
        this.catalogo = catalogo;
        this.catalogo.agregarObservador(this);
        setLayout(new BorderLayout());

        // Formulario
        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Producto"));
        form.add(new JLabel("Código:"));
        txtCodigo = new JTextField();
        form.add(txtCodigo);

        form.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        form.add(txtNombre);

        form.add(new JLabel("Precio:"));
        txtPrecio = new JTextField();
        form.add(txtPrecio);

        form.add(new JLabel("Stock:"));
        txtStock = new JTextField();
        form.add(txtStock);

        btnAgregar = new JButton("Agregar");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        btnBuscar = new JButton("Buscar");

        JPanel botones = new JPanel();
        botones.add(btnAgregar);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        botones.add(btnBuscar);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(form, BorderLayout.CENTER);
        norte.add(botones, BorderLayout.SOUTH);

        // Tabla
        String[] columnas = {"Código", "Nombre", "Precio", "Stock"};
        modelo = new DefaultTableModel(columnas, 0);
        tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);

        add(norte, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // Eventos
        btnAgregar.addActionListener(this::agregar);
        btnEditar.addActionListener(this::editar);
        btnEliminar.addActionListener(this::eliminar);
        btnBuscar.addActionListener(this::buscar);

        cargarTabla();
    }

    private void agregar(ActionEvent e) {
        try {
            Producto p = new Producto(
                txtCodigo.getText(),
                txtNombre.getText(),
                Double.parseDouble(txtPrecio.getText()),
                Integer.parseInt(txtStock.getText())
            );
            catalogo.agregarProducto(p);
            limpiar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void editar(ActionEvent e) {
    int fila = tabla.getSelectedRow();
    if (fila < 0) {
        JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla.");
        return;
    }

    String codigo = (String) modelo.getValueAt(fila, 0);

    try {
        // Actualizar los datos del producto desde el formulario
        Producto productoActualizado = new Producto(
            txtCodigo.getText(),
            txtNombre.getText(),
            Double.parseDouble(txtPrecio.getText()),
            Integer.parseInt(txtStock.getText())
        );

        // Intentar editar (puede lanzar ProductoNoEncontradoException)
        catalogo.editarProducto(productoActualizado);
        JOptionPane.showMessageDialog(this, "Producto actualizado correctamente.");
        limpiar();

    } catch (ProductoNoEncontradoException ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Precio o stock inválido.", "Error de formato", JOptionPane.ERROR_MESSAGE);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private void eliminar(ActionEvent e) {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        String codigo = (String) modelo.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar producto?");
        if (confirm == JOptionPane.YES_OPTION) {
            catalogo.eliminarProducto(codigo);
        }
    }

    private void buscar(ActionEvent e) {
        String nombre = JOptionPane.showInputDialog(this, "Buscar por nombre:");
        if (nombre != null && !nombre.isEmpty()) {
            List<Producto> resultados = catalogo.buscarPorNombre(nombre);
            actualizarTabla(resultados);
        }
    }

    private void cargarTabla() {
        actualizarTabla(catalogo.getProductos());
    }

    private void actualizarTabla(List<Producto> productos) {
        modelo.setRowCount(0);
        for (Producto p : productos) {
            modelo.addRow(new Object[]{p.getCodigo(), p.getNombre(), p.getPrecio(), p.getStock()});
        }
    }

    private void limpiar() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        cargarTabla();
    }

    @Override
    public void catalogoActualizado() {
        SwingUtilities.invokeLater(this::cargarTabla);
    }
}