/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package infrastructure;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
/**
 *
 * @author Emesis
 */
public class ComprobanteGenerator {

    // Genera HMAC-SHA256 y lo devuelve en formato hexadecimal
    public static String generarHMAC(String mensaje, String claveSecreta) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(claveSecreta.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(keySpec);
        byte[] hmacBytes = mac.doFinal(mensaje.getBytes(StandardCharsets.UTF_8));

        // Convertir a hexadecimal
        StringBuilder hexString = new StringBuilder();
        for (byte b : hmacBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}