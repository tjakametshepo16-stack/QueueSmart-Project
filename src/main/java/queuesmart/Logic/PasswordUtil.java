package queuesmart.Logic;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordUtil {

    private PasswordUtil() {
    }

    public static String hash(String plainPassword) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(plainPassword.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hashBytes);

        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("Could not hash password", e);
        }
    }

    public static boolean matches(String plainPassword, String storedHash) {
        return hash(plainPassword).equals(storedHash);
    }
}
