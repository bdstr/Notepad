package notepad;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class EncryptedText {


    private static byte[] getRandomBytes(int numBytes) {
        byte[] bytes = new byte[numBytes];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }

    private static SecretKey generateSecretKey(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

    }

    public static boolean isTextEncrypted(String text) {
        try {
            byte[] decode = Base64.getDecoder().decode(text.trim().getBytes(StandardCharsets.UTF_8));
            ByteBuffer byteBuffer = ByteBuffer.wrap(decode);
            int byteSize = byteBuffer.getInt();
            return byteSize >= 12 && byteSize < 16;
        } catch (Exception e) {
            return false;
        }
    }

    public static String encrypt(String text, String password) throws Exception {
        byte[] salt = getRandomBytes(16);
        byte[] iv = getRandomBytes(12);

        SecretKey aesKeyFromPassword = generateSecretKey(password, salt);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, aesKeyFromPassword, new GCMParameterSpec(128, iv));
        byte[] cipherText = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));

        byte[] cipherTextWithIvSalt = ByteBuffer.allocate(4 + iv.length + salt.length + cipherText.length)
                .putInt(iv.length)
                .put(iv)
                .put(salt)
                .put(cipherText)
                .array();

        return Base64.getEncoder().encodeToString(cipherTextWithIvSalt);

    }

    public static String decrypt(String encryptedText, String password) throws Exception {
        byte[] decode = Base64.getDecoder().decode(encryptedText.trim().getBytes(StandardCharsets.UTF_8));
        ByteBuffer bb = ByteBuffer.wrap(decode);

        byte[] ivLength = new byte[4];
        bb.get(ivLength);

        byte[] iv = new byte[12];
        bb.get(iv);

        byte[] salt = new byte[16];
        bb.get(salt);

        byte[] cipherText = new byte[bb.remaining()];
        bb.get(cipherText);

        SecretKey aesKeyFromPassword = generateSecretKey(password, salt);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, aesKeyFromPassword, new GCMParameterSpec(128, iv));
        byte[] plainText = cipher.doFinal(cipherText);

        return new String(plainText, StandardCharsets.UTF_8);
    }
}
