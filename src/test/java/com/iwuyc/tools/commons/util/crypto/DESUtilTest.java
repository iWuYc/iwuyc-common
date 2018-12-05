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
    }
}
