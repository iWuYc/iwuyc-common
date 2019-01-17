package com.iwuyc.tools.commons.util.crypto;

import org.junit.Test;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public class DESUtilTest {

    @Test
    public void encrypt() throws Exception {
        String src = "hello world.";

        BASE64Encoder encoder = new BASE64Encoder();
        String encodeResult = encoder.encode(src.getBytes(StandardCharsets.UTF_8));

        Base64.Encoder encoderNew = Base64.getEncoder();
        String encodeResultNew = encoderNew.encodeToString(src.getBytes(StandardCharsets.UTF_8));
        System.out.println(encodeResult.equals(encodeResultNew));
        System.out.println(encodeResult);
        System.out.println(encodeResultNew);

        BASE64Decoder decoder = new BASE64Decoder();
        byte[] bytesrc = decoder.decodeBuffer(encodeResultNew);

        Base64.Decoder decoderNew = Base64.getDecoder();
        byte[] bytesrcNew = decoderNew.decode(encodeResultNew);
        System.out.println(Arrays.equals(bytesrc, bytesrcNew));
        System.out.println(new String(bytesrcNew));
    }

    @Test
    public void decrypt() {
        String str = "ceFw43yk+ODb7n2TRJNpamKVo+Wj33YXpoVeQX+DAoxX0nZBeVafOjWCxGDb+FWPx4Unh/8KjSy3U+cKUOcltqxd++0BTkqvMtc4TdDZI5dkfT2vTWbxb0Hmfz3MIK3vJQ3zjEcwiquUwcHCYwQZuUzrbMEtC2VxFMzGF8+njjs=";
        String decrypt = DESUtil.decrypt(str,
                "1234567890abcdefgijklmnopqrstsdfsdfsdfsdfsdjfljldajlkjaslfjaSLKDAHSLKFHALKSFJLKASDHAJKSLKjlkjslkdjfaklsjdlkajl");
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
}
