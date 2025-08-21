/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package infrastructure;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
/**
 *
 * @author TheJPlay2006
 */


/**
 * Utilidad para cifrar y descifrar textos usando AES.
 * Ideal para proteger datos sensibles como cédulas.
 */
public class CifradorAES {
    private SecretKey clave;

    public CifradorAES(String claveFija) throws Exception {
        // Aseguramos que la clave tenga 16 o 32 bytes (AES-128 o AES-256)
        byte[] keyBytes = claveFija.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length == 16 || keyBytes.length == 32) {
            this.clave = new SecretKeySpec(keyBytes, "AES");
        } else {
            // Si no, generamos una clave basada en el hash
            byte[] derivedKey = java.security.MessageDigest.getInstance("SHA-256")
                    .digest(keyBytes);
            byte[] truncatedKey = java.util.Arrays.copyOf(derivedKey, 32); // AES-256
            this.clave = new SecretKeySpec(truncatedKey, "AES");
        }
    }

    public String cifrar(String texto) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, clave);
        byte[] bytesCifrados = cipher.doFinal(texto.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(bytesCifrados);
    }

    public String descifrar(String textoCifrado) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, clave);
        byte[] bytesCifrados = Base64.getDecoder().decode(textoCifrado);
        byte[] bytesDescifrados = cipher.doFinal(bytesCifrados);
        return new String(bytesDescifrados, StandardCharsets.UTF_8);
    }
}
