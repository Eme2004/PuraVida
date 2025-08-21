/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package exception;

/**
 *
 * @author Emesis
 */
/**
 * Excepción personalizada que se lanza cuando ocurre un problema
 * relacionado con el procesamiento de un pago.
 * 
 * Casos comunes:
 * - Método de pago no soportado (ej: "bitcoin", "paypal").
 * - Monto inválido (cero o negativo).
 * - Pago rechazado por reglas de negocio (ej: tarjeta con límite).
 * - Fallo en la estrategia de pago.
 */
public class PagoInvalidoException extends Exception {

    /**
     * Constructor que recibe un mensaje descriptivo.
     *
     * @param mensaje Explicación del error.
     */
    public PagoInvalidoException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor que recibe un mensaje y la causa subyacente.
     *
     * Útil para encapsular excepciones internas (ej: problemas de red, criptografía).
     *
     * @param mensaje Explicación del error.
     * @param causa   La excepción original que provocó este error.
     */
    public PagoInvalidoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    /**
     * Constructor que envuelve una excepción existente con contexto adicional.
     *
     * @param causa La excepción original (se usa el mensaje de esta).
     */
    public PagoInvalidoException(Throwable causa) {
        super(causa);
    }

    /**
     * Permite personalizar el mensaje al encadenar con otra excepción.
     *
     * @param mensaje Mensaje personalizado.
     * @param causa   Excepción original.
     * @param habilitarSupresion Si se permite la supresión de excepciones.
     * @param habilitarSeguimiento Si se incluye el stack trace.
     */
    public PagoInvalidoException(String mensaje, Throwable causa,
                                 boolean habilitarSupresion,
                                 boolean habilitarSeguimiento) {
        super(mensaje, causa, habilitarSupresion, habilitarSeguimiento);
    }

    // Métodos adicionales (opcionales):
    // No se necesitan sobreescribir: getMessage(), getCause(), etc. ya están en Exception.
}