package com.example.dangjian_spring.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.Security;

public class SM4Utils {
    static {
        if (null == Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    private static final String ALGORITHM_NAME = "SM4";
    private static final String ALGORITHM_CBC_PADDING = "SM4/CBC/PKCS5Padding";
    private static final int DEFAULT_KEY_SIZE = 128;
    private static final int BLOCK_SIZE = 16;

    public static byte[] generateKey() throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM_NAME, BouncyCastleProvider.PROVIDER_NAME);
        kg.init(DEFAULT_KEY_SIZE);
        SecretKey secretKey = kg.generateKey();
        byte[] key = secretKey.getEncoded();
        return ensurePositiveBytes(key);
    }

    public static byte[] generateIV() {
        byte[] iv = new byte[BLOCK_SIZE];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        return ensurePositiveBytes(iv);
    }

    private static byte[] ensurePositiveBytes(byte[] bytes) {
        byte[] result = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            // 将负数转换为正数
            result[i] = (byte) (bytes[i] & 0x7F);
        }
        return result;
    }

    public static byte[] encrypt(byte[] plainText, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM_CBC_PADDING, BouncyCastleProvider.PROVIDER_NAME);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM_NAME);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        return cipher.doFinal(plainText);
    }

    public static byte[] decrypt(byte[] cipherText, byte[] key, byte[] iv) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_CBC_PADDING, BouncyCastleProvider.PROVIDER_NAME);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM_NAME);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            return cipher.doFinal(cipherText);
        } catch (Exception e) {
            System.err.println("Decryption error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}