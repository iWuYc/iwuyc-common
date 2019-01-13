package com.iwuyc.tools.commons.util.xml;

import com.google.gson.stream.JsonWriter;
import org.junit.Test;

import java.io.StringWriter;

public class Xml2JsonUtilTest {
    private static final String xml = "<operation_in>123</operation_in>";

    @Test
    public void xml2Json() {
        Xml2JsonUtil.xml2JsonString(xml);
    }

    @Test
    public void name() throws Exception {
        try (StringWriter writer = new StringWriter(); JsonWriter jsonBuilder = new JsonWriter(writer);) {
            System.out.println(jsonBuilder.value("jack"));
            System.out.println(writer.toString());
        }
    }
}
