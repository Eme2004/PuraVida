/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application;

import data.EstrategiaPago;
import data.PagoEfectivo;
import data.PagoTarjeta;
import data.PagoTransferencia;
import exception.PagoInvalidoException;

/**
 *
 * @author USER
 */
public class FabricaPagos {
    public static EstrategiaPago crearPago(String tipo) throws PagoInvalidoException {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new PagoInvalidoException("El tipo de pago no puede ser nulo o vacío.");
        }

        return switch (tipo.toLowerCase().trim()) {
            case "efectivo" -> new PagoEfectivo();
            case "tarjeta" -> new PagoTarjeta();
            case "transferencia" -> new PagoTransferencia();
            default -> throw new PagoInvalidoException(
                "Método de pago no válido: '" + tipo + "'. " +
                "Opciones permitidas: efectivo, tarjeta, transferencia."
            );
        };
    }
}
