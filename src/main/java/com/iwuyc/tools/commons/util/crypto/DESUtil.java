package com.iwuyc.tools.commons.util.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@SuppressWarnings("restriction")
public class DESUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DESUtil.class);

    /**
     * 加密
     *
     * @param str     待加密的字符串
     * @param key     加密串
     * @param charset 字符串编码
     * @return 加密后的字符串
     */
    public static String encrypt(String str, String key, String charset) {
        try {
            DESedeKeySpec dks = new DESedeKeySpec(key.getBytes(charset));
            Cipher cipher = getCipher(dks, Cipher.ENCRYPT_MODE);
            byte[] b = cipher.doFinal(str.getBytes(charset));
            Base64.Encoder encoder = Base64.getEncoder();
            return encoder.encodeToString(b);
        } catch (Exception e) {
            LOGGER.warn("DESUtil encrypt error:", e);
            return null;
        }
    }

    /**
     * 加密
     *
     * @param str 待加密的字符串
     * @param key 加密串
     * @return 加密后的字符串
     */
    public static String encrypt(String str, String key) {
        return encrypt(str, key, "UTF-8");
    }

    /**
     * 解密
     *
     * @param str     待解密的字符串
     * @param key     加密串
     * @param charset 字符串编码
     * @return 解密后的字符串
     */
    public static String decrypt(String str, String key, String charset) {
        try {
            // --通过base64,将字符串转成byte数组
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] bytesrc = decoder.decode(str);

            // --解密的key
            DESedeKeySpec dks = new DESedeKeySpec(key.getBytes(charset));
            Cipher cipher = getCipher(dks, Cipher.DECRYPT_MODE);
            byte[] retByte = cipher.doFinal(bytesrc);

            return new String(retByte, charset);
        } catch (Exception e) {
            LOGGER.warn("DESUtil decrypt error:", e);
            return null;
        }

    }

    /**
     * 解密
     *
     * @param str 待解密的字符串
     * @param key 加密串
     * @return 解密后的字符串
     */
    public static String decrypt(String str, String key) {
        return decrypt(str, key, "UTF-8");
    }

    private static Cipher getCipher(DESedeKeySpec dks, int decryptMode)
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        SecretKey securekey = keyFactory.generateSecret(dks);

        // --Chipher对象解密
        Cipher cipher = Cipher.getInstance("DESede");
        cipher.init(decryptMode, securekey);
        return cipher;
    }

}
