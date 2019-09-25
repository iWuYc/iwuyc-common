package com.iwuyc.tools.commons.util.crypto;

import com.iwuyc.tools.commons.basic.StringUtils;
import com.iwuyc.tools.commons.util.file.FileUtil;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class DESUtilTest {
    static String src = "hello world.hello world.hello world.hello world.hello world.hello world.hello world.hello world.hello world.hello world.hello world.hello world.hello world.hello world.hello world.hello world.hello world.hello world.hello world.hello world.hello world.hello world.hello world.hello world.hello world.hello world.hello world.hello world.hello world.hello world.";

    @Test
    public void encryptMime() throws Exception {
        Base64.Encoder mimeEncoder = Base64.getMimeEncoder();
        String str = mimeEncoder.encodeToString(src.getBytes(StandardCharsets.UTF_8));
        System.out.println(str);

        Base64.Decoder mimeDecoder = Base64.getMimeDecoder();
        byte[] result = mimeDecoder.decode(str);
        System.out.println(new String(result));
    }

    @Test
    public void encrypt() throws Exception {
        Base64.Encoder encoder = Base64.getEncoder();
        String encodeResult = encoder.encodeToString(src.getBytes(StandardCharsets.UTF_8));

        Base64.Encoder encoderNew = Base64.getEncoder();
        String encodeResultNew = encoderNew.encodeToString(src.getBytes(StandardCharsets.UTF_8));
        System.out.println(encodeResult.equals(encodeResultNew));
        System.out.println(encodeResult);
        System.out.println(encodeResultNew);

        Base64.Decoder decoder = Base64.getDecoder();
        byte[] bytesrc = decoder.decode(encodeResultNew);

        Base64.Decoder decoderNew = Base64.getDecoder();
        byte[] bytesrcNew = decoderNew.decode(encodeResultNew);
        System.out.println(Arrays.equals(bytesrc, bytesrcNew));
        System.out.println(new String(bytesrcNew, FileUtil.DEFAULT_CHARSET_ENCODING));
    }

    @Test
    public void decrypt() {
        String str = "923c01b8c5ee9485758b82d0da07111743710147a72a963d7921cf1cc2dbd29fbff69339e008b1cdc4d4f591bc02050f5e196e5f3c5c9467c9db0be5b4cf2b3934e99587475e14b51b05ffc7d8ed4f851c5d7d4e0e91ea8bef7b07f6edc7ccf474dcb4ab557e95b391e76cf0785204eafdc229e777bf88f9038e1dc76806db24";
        String decrypt = DESUtil.decrypt(str, "19930826199308261993082619930826");
        System.out.println(decrypt);
    }

    @Test
    public void base64() {

        String key = "e1e9ff738e6db54782fbe828b2753163";
        String encryptStr = "JRcqm7EDXU6qTQ3pBlV6DAwv5rmmIJBXOvySYVrksDl82ejbTR4EjLh9Lzzj+M6ZdtcTrnuQKL5q"
                + "LBk2CuqVCRoer+Sb7vkpCxOdxzEqcNp21xOue5AovuHfwiVTjsk2/46Rb5xSao7JxMoH4wGsYAde"
                + "GlGffnS3S+Kv0KaN8y6iGBtJUHdUgjPzxDWrevGj";
        String decreyptStr = DESUtil.decrypt(encryptStr, key);
        System.out.println(decreyptStr);

        String encryptStrNew = DESUtil.encrypt(decreyptStr, key);
        System.out.println(encryptStr);
        System.out.println(encryptStrNew);
        System.out.println(encryptStr.equals(encryptStrNew));

    }

    @Test
    public void encryptTest() {
        String key = "19930826199308261993082619930826";
        String username = "root";
        String password = "19930826";
        String url = "jdbc:log4jdbc:mysql://127.0.0.1:3306/mock_server?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull";

        List<String> strs = Arrays.asList(username, password, url);
        for (String item : strs) {
            String encryptStr = DESUtil.encrypt(item, key).replaceAll(StringUtils.CR, "").replace(StringUtils.LF, "");
            System.out.println(encryptStr);
            String decryptStr = DESUtil.decrypt(encryptStr, key);
            System.out.println(decryptStr);
        }
    }
}
