/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data;

/**
 *
 * @author USER
 */
public class PagoTarjeta implements EstrategiaPago {
    @Override
    public boolean procesarPago(double monto) {
        if (monto > 5000) {
            System.out.println("Pago rechazado: monto excesivo.");
            return false;
        }
        System.out.println("Pago con tarjeta de $" + monto + " autorizado.");
        return true;
    }

    @Override
    public String getMetodo() {
        return "Tarjeta";
    }
}

