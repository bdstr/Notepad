package notepad;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

public class EncryptedText {
    private final byte[] encryptedText;

    public static boolean isTextEncrypted(String text) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(text.getBytes());
        int noonceSize = byteBuffer.getInt();
        return noonceSize >= 12 && noonceSize < 16;
    }

    public EncryptedText(String clearTextToEncrypt, String password) throws Exception {
        this.encryptedText = encrypt(clearTextToEncrypt, password);
    }

    public EncryptedText(String encryptedText) {
        this.encryptedText = encryptedText.getBytes();
    }

    public String getEncryptedText() {
        return new String(encryptedText);
    }

    private byte[] encrypt(String text, String password) throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[12];
        secureRandom.nextBytes(iv);
        SecretKey secretKey = generateSecretKey(password, iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
        byte[] encryptedData = cipher.doFinal(text.getBytes());
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + iv.length + encryptedData.length);
        byteBuffer.putInt(iv.length);
        byteBuffer.put(iv);
        byteBuffer.put(encryptedData);
        return byteBuffer.array();

    }

    public String decrypt(String password) throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(encryptedText);
        int noonceSize = byteBuffer.getInt();

        if (noonceSize < 12 || noonceSize >= 16) {
            throw new IllegalArgumentException("Text is not encrypted!");
        }

        byte[] iv = new byte[noonceSize];
        byteBuffer.get(iv);
        SecretKey secretKey = generateSecretKey(password, iv);
        byte[] cipherBytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherBytes);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

        return new String(cipher.doFinal(cipherBytes));
    }

    private static SecretKey generateSecretKey(String password, byte[] iv) throws Exception {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), iv, 65536, 128);
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] key = secretKeyFactory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(key, "AES");
    }
}
