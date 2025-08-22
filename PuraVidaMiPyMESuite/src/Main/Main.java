package Main;

import ui.ClientesPanel;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

/**
 *
 * @author TheJPlay2006
 */

import application.*;
import infrastructure.*;
import ui.*;
import javax.swing.*;

public class Main extends JFrame {
    public Main() {
        setTitle("PuraVida MiPyME Suite");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);

        try {
            // Inicializar servicios
            RepositorioCSV repoCSV = new RepositorioCSV();
            ClienteRepositorio repoCliente = new ClienteRepositorio();
            Catalogo catalogo = new Catalogo(repoCSV);
            ClienteService clienteService = new ClienteService(repoCliente);
            FabricaPagos fabricaPagos = new FabricaPagos();
            GestionOrdenes gestionOrdenes = new GestionOrdenes(catalogo, fabricaPagos);
            AnalisisService analisisService = new AnalisisService(
                catalogo.getProductos(),
                clienteService.obtenerTodos(),
                new java.util.ArrayList<>() // Puedes cargar órdenes si las tienes
            );

            // Pestañas
            JTabbedPane tab = new JTabbedPane();
            tab.addTab("Catálogo", new CatalogoPanel(catalogo));
            tab.addTab("Clientes", new ClientesPanel(clienteService));
            tab.addTab("Órdenes", new OrdenesPanel(gestionOrdenes, catalogo));
            tab.addTab("Dashboard", new DashboardPanel(analisisService));

            add(tab);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al iniciar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}