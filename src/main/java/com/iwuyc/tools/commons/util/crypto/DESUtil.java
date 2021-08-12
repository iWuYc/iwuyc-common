package com.iwuyc.tools.commons.util.crypto;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.io.Serializable;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DESUtil {
    private final static LoadingCache<DESKeyInfo, Cipher> CipherCache =
            CacheBuilder.newBuilder().expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<DESKeyInfo, Cipher>() {
                @Override
                public Cipher load(@Nonnull DESKeyInfo desKeyInfo) throws Exception {
                    String key = desKeyInfo.getKey();
                    String charset = desKeyInfo.getCharset();
                    DESedeKeySpec dks = new DESedeKeySpec(key.getBytes(charset));

                    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
                    SecretKey secureKey = keyFactory.generateSecret(dks);

                    // --Chipher对象解密
                    Cipher cipher = Cipher.getInstance("DESede");
                    cipher.init(desKeyInfo.getCipherMode(), secureKey);
                    return cipher;
                }
            });

    /**
     * 加密
     *
     * @param str     待加密的字符串
     * @param key     加密串
     * @param charset 字符串编码
     * @return 加密后的字符串
     */
    public static String encrypt(String str, String key, String charset) {
        DESKeyInfo desKeyInfo = new DESKeyInfo();
        desKeyInfo.setKey(key);
        desKeyInfo.setCharset(charset);
        return encrypt(str, desKeyInfo);
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

    public static String encrypt(String str, DESKeyInfo desKeyInfo) {
        try {
            desKeyInfo.setCipherMode(Cipher.ENCRYPT_MODE);
            String charset = desKeyInfo.getCharset();
            Cipher cipher = CipherCache.get(desKeyInfo);
            byte[] b = cipher.doFinal(str.getBytes(charset));
            Base64.Encoder encoder = Base64.getMimeEncoder();
            return encoder.encodeToString(b);
        } catch (Exception e) {
            log.warn("DESUtil encrypt error:", e);
            return null;
        }
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
        DESKeyInfo desKeyInfo = new DESKeyInfo();
        desKeyInfo.setKey(key);
        desKeyInfo.setCharset(charset);
        return decrypt(str, desKeyInfo);
    }

    public static String decrypt(String str, DESKeyInfo desKeyInfo) {
        try {
            desKeyInfo.setCipherMode(Cipher.DECRYPT_MODE);
            // --通过base64,将字符串转成byte数组
            Base64.Decoder decoder = Base64.getMimeDecoder();
            byte[] byteSrc = decoder.decode(str);

            // --解密的key
            Cipher cipher = CipherCache.get(desKeyInfo);
            byte[] retByte = cipher.doFinal(byteSrc);
            String charset = desKeyInfo.getCharset();
            return new String(retByte, charset);
        } catch (Exception e) {
            log.warn("DESUtil decrypt error:", e);
            e.printStackTrace();
            return null;
        }

    }

    @Data
    @EqualsAndHashCode
    public static class DESKeyInfo implements Serializable {
        private final static long serialVersionUID = 1L;
        private String key;
        private String charset;
        private int cipherMode;
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

}
