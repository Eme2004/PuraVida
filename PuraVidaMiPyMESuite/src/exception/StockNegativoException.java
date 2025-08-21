/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package exception;

/**
 *
 * @author Emesis
 */
public class StockNegativoException extends RuntimeException {
    public StockNegativoException(String message) {
        super(message);
    }
}