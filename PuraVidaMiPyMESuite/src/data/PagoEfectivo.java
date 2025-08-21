/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data;

/**
 *
 * @author USER
 */
public class PagoEfectivo implements EstrategiaPago {
    @Override
    public boolean procesarPago(double monto) {
        System.out.println("Pago en efectivo de $" + monto + " realizado.");
        return true;
    }

    @Override
    public String getMetodo() {
        return "Efectivo";
    }
}
