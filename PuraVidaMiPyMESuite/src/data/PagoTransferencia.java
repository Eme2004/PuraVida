/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data;

/**
 *
 * @author USER
 */
public class PagoTransferencia implements EstrategiaPago {
    @Override
    public boolean procesarPago(double monto) {
        System.out.println("Transferencia bancaria de $" + monto + " iniciada.");
        return Math.random() > 0.1; // 90% éxito
    }

    @Override
    public String getMetodo() {
        return "Transferencia";
    }
}
