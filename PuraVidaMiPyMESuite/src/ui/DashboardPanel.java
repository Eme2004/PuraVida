/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import application.AnalisisService;
import javax.swing.*;
import java.awt.*;
import workers.FidelizacionWorker;

public class DashboardPanel extends JPanel {
    private JTextArea area;
    private JButton btnRecalcular;
    private JProgressBar barra;
    private AnalisisService analisisService;

    public DashboardPanel(AnalisisService analisisService) {
        this.analisisService = analisisService;
        setLayout(new BorderLayout());

        area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(area);

        barra = new JProgressBar(0, 100);
        barra.setStringPainted(true);

        btnRecalcular = new JButton("Recalcular Fidelización");

        JPanel sur = new JPanel(new BorderLayout());
        sur.add(barra, BorderLayout.CENTER);
        sur.add(btnRecalcular, BorderLayout.EAST);

        add(scroll, BorderLayout.CENTER);
        add(sur, BorderLayout.SOUTH);

        btnRecalcular.addActionListener(e -> {
            btnRecalcular.setEnabled(false);
            barra.setValue(0);

            FidelizacionWorker worker = analisisService.crearTareaRecalcularFidelizacion(porcentaje -> {
                barra.setValue(porcentaje);
            });

            worker.execute();
        });

        // Generar dashboard inicial
        area.setText(analisisService.generarDashboard());
    }
}