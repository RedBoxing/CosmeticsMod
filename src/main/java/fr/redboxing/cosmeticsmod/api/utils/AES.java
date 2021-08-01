package fr.redboxing.cosmeticsmod.api.utils;


import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

public class AES {
    protected static final byte[] KEY = { 74, -58, -122, -57, -35, 54, -80, 61, -15, 36, 77, -20, -55, 27, 64, 50, 3, 25, -126, 124, -40, 40, 111, -45, 113, -50, -3, 34, -123, -65, -107, 86};
    protected static final byte[] IV = { 65, 2, 7, -44, -121, 98, -115, -84, -65, -55, -104, -118, -21, 105, -61, 112 };

    public static String encrypt(String in) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        SecretKeySpec key = new SecretKeySpec(KEY, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV));

        byte[] bytesToEncrypt = in.getBytes();
        byte[] encryptedBytes = Base64.encodeBase64(cipher.doFinal(bytesToEncrypt));

        return new String(encryptedBytes);
    }

    public static String decrypt(String in) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        SecretKeySpec key = new SecretKeySpec(KEY, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV));

        byte[] encryptedBytes = in.getBytes();
        byte[] decryptedBytes = cipher.doFinal(Base64.decodeBase64(encryptedBytes));

        return new String(decryptedBytes);
    }

    public static String SHA256(String in) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encoded = digest.digest(in.getBytes());
        return new String(encoded);
    }
}
