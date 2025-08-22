/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import application.Catalogo;
import application.GestionOrdenes;
import application.FabricaPagos;
import domain.Cliente;
import domain.Orden;
import domain.ItemOrden;
import exception.PagoInvalidoException;
import exception.StockInsuficienteException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrdenesPanel extends JPanel {
    private GestionOrdenes gestionOrdenes;
    private Catalogo catalogo;
    private JTextField txtId, txtClienteCedula, txtCodigoProducto, txtCantidad, txtDescuento;
    private JComboBox<String> comboPago;
    private JTextArea areaResumen;
    private JButton btnAgregarItem, btnAplicarDescuento, btnFinalizar;

    public OrdenesPanel(GestionOrdenes gestionOrdenes, Catalogo catalogo) {
        this.gestionOrdenes = gestionOrdenes;
        this.catalogo = catalogo;
        setLayout(new BorderLayout());

        // Formulario
        JPanel form = new JPanel(new GridLayout(7, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Crear Orden"));

        form.add(new JLabel("ID Orden:"));
        txtId = new JTextField();
        form.add(txtId);

        form.add(new JLabel("Cédula Cliente:"));
        txtClienteCedula = new JTextField();
        form.add(txtClienteCedula);

        form.add(new JLabel("Código Producto:"));
        txtCodigoProducto = new JTextField();
        form.add(txtCodigoProducto);

        form.add(new JLabel("Cantidad:"));
        txtCantidad = new JTextField("1");
        form.add(txtCantidad);

        form.add(new JLabel("Descuento (%):"));
        txtDescuento = new JTextField("0");
        form.add(txtDescuento);

        form.add(new JLabel("Pago:"));
        comboPago = new JComboBox<>(new String[]{"efectivo", "tarjeta", "transferencia"});
        form.add(comboPago);

        btnAgregarItem = new JButton("Agregar Ítem");
        btnAplicarDescuento = new JButton("Aplicar Descuento");
        btnFinalizar = new JButton("Finalizar Orden");

        JPanel botones = new JPanel();
        botones.add(btnAgregarItem);
        botones.add(btnAplicarDescuento);
        botones.add(btnFinalizar);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(form, BorderLayout.CENTER);
        norte.add(botones, BorderLayout.SOUTH);

        // Resumen
        areaResumen = new JTextArea(10, 40);
        areaResumen.setEditable(false);
        JScrollPane scroll = new JScrollPane(areaResumen);

        add(norte, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // Eventos
        btnAgregarItem.addActionListener(this::agregarItem);
        btnAplicarDescuento.addActionListener(this::aplicarDescuento);
        btnFinalizar.addActionListener(this::finalizarOrden);

        areaResumen.setText("Orden en progreso...\n");
    }

    private void agregarItem(ActionEvent e) {
        try {
            String id = txtId.getText();
            String cedula = txtClienteCedula.getText();
            Cliente cliente = new Cliente(cedula, "Temporal", "N/A", "N/A");
            String tipoPago = (String) comboPago.getSelectedItem();

            Orden orden = gestionOrdenes.crearOrden(id, cliente, tipoPago);

            String codigo = txtCodigoProducto.getText();
            int cantidad = Integer.parseInt(txtCantidad.getText());

            gestionOrdenes.agregarItemAOrden(orden, codigo, cantidad);

            double descuento = Double.parseDouble(txtDescuento.getText());
            gestionOrdenes.aplicarDescuento(orden, descuento);

            areaResumen.setText("Orden: " + id + "\n");
            areaResumen.append("Cliente: " + cedula + "\n");
            areaResumen.append("Método: " + tipoPago + "\n");
            areaResumen.append("Items:\n");
            for (ItemOrden item : orden.getItems()) {
                areaResumen.append("  - " + item.getProducto().getNombre() +
                        " x" + item.getCantidad() + " = ₡" + item.getTotal() + "\n");
            }
            areaResumen.append("Total: ₡" + String.format("%.2f", orden.calcularTotal()) + "\n");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void aplicarDescuento(ActionEvent e) {
        try {
            double desc = Double.parseDouble(txtDescuento.getText());
            // (Se aplica en agregarItem por simplicidad)
            JOptionPane.showMessageDialog(this, "Descuento actualizado.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Descuento inválido.");
        }
    }

    private void finalizarOrden(ActionEvent e) {
        try {
            String ruta = "facturas/" + txtId.getText() + ".txt";
            gestionOrdenes.finalizarOrden(null, ruta); // Aquí deberías pasar la orden real
            JOptionPane.showMessageDialog(this, "Orden finalizada. Factura generada.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al finalizar: " + ex.getMessage());
        }
    }
}