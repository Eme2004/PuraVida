package ui;

import application.ClienteService;
import domain.Cliente;
import infrastructure.CifradorAES;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ClientesPanel extends JPanel {
    private ClienteService clienteService;
    private JTable tabla;
    private DefaultTableModel modelo;
    private JTextField txtCedula, txtNombre, txtTelefono, txtEmail;
    private JButton btnRegistrar, btnActualizar, btnEliminar, btnRevelar;
    private JLabel lblCedulaVisible;

    public ClientesPanel(ClienteService clienteService) {
        this.clienteService = clienteService;
        setLayout(new BorderLayout());

        // Formulario
        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Cliente"));

        form.add(new JLabel("Cédula:"));
        txtCedula = new JTextField();
        form.add(txtCedula);

        form.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        form.add(txtNombre);

        form.add(new JLabel("Teléfono:"));
        txtTelefono = new JTextField();
        form.add(txtTelefono);

        form.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        form.add(txtEmail);

        btnRegistrar = new JButton("Registrar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnRevelar = new JButton("Revelar Cédulas");

        JPanel botones = new JPanel();
        botones.add(btnRegistrar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnRevelar);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(form, BorderLayout.CENTER);
        norte.add(botones, BorderLayout.SOUTH);

        // Tabla
        String[] columnas = {"Cédula", "Nombre", "Teléfono", "Email"};
        modelo = new DefaultTableModel(columnas, 0);
        tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);

        add(norte, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // Eventos
        btnRegistrar.addActionListener(this::registrar);
        btnActualizar.addActionListener(this::actualizar);
        btnEliminar.addActionListener(this::eliminar);
        btnRevelar.addActionListener(this::revelar);

        cargarTabla();
    }

    private void registrar(ActionEvent e) {
        try {
            Cliente c = new Cliente(
                txtCedula.getText(),
                txtNombre.getText(),
                txtTelefono.getText(),
                txtEmail.getText()
            );
            clienteService.registrarCliente(c);
            limpiar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void actualizar(ActionEvent e) {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        String cedula = (String) modelo.getValueAt(fila, 0);
        Cliente c = clienteService.buscarPorCedula(cedula);
        if (c != null) {
            c.setNombre(txtNombre.getText());
            c.setTelefono(txtTelefono.getText());
            c.setEmail(txtEmail.getText());
            clienteService.actualizarCliente(c);
            limpiar();
        }
    }

    private void eliminar(ActionEvent e) {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        String cedula = (String) modelo.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar cliente?");
        if (confirm == JOptionPane.YES_OPTION) {
            clienteService.eliminarCliente(cedula);
            limpiar();
        }
    }

    private void revelar(ActionEvent e) {
        String pass = JOptionPane.showInputDialog(this, "Contraseña de Admin:");
        if ("PuraVida-2025!".equals(pass)) {
            actualizarTabla(clienteService.obtenerTodos(), true);
            JOptionPane.showMessageDialog(this, "Cédulas reveladas temporalmente.");
        } else {
            JOptionPane.showMessageDialog(this, "Contraseña incorrecta.");
        }
    }

    private void cargarTabla() {
        actualizarTabla(clienteService.obtenerTodos(), false);
    }

    private void actualizarTabla(List<Cliente> clientes, boolean mostrarCedula) {
        modelo.setRowCount(0);
        for (Cliente c : clientes) {
            modelo.addRow(new Object[]{
                mostrarCedula ? c.getCedula() : "***********",
                c.getNombre(),
                c.getTelefono(),
                c.getEmail()
            });
        }
    }

    private void limpiar() {
        txtCedula.setText("");
        txtNombre.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        cargarTabla();
    }
}