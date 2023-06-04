package pt.hmsk.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

public class Security {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY_ALGORITHM = "AES";
    private static final byte[] IV = new byte[16];

    public static String getEncryptedString(String raw) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKey secretKey = generateKey();
        IvParameterSpec ivspec = new IvParameterSpec(IV);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
        byte[] encrypted = cipher.doFinal(raw.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String getDecryptedString(String encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKey secretKey = generateKey();
        IvParameterSpec ivspec = new IvParameterSpec(IV);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
        byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
        return new String(original);
    }

    private static SecretKey generateKey() throws Exception {
        byte[] key = (getSalt()).getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16);
        return new SecretKeySpec(key, KEY_ALGORITHM);
    }
    
    private static String getSalt() throws Exception {
        InetAddress localHost = InetAddress.getLocalHost();
        String hostname = localHost.getHostName();

        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
        byte[] macAddressBytes = networkInterface.getHardwareAddress();
        String macAddress = convertBytesToHex(macAddressBytes);

        String combined = hostname + macAddress;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(combined.getBytes());

        return Base64.getEncoder().encodeToString(hash);
    }

    private static String convertBytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte temp : bytes) {
            result.append(Integer.toString((temp & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }
    
}
