package com.iwuyc.tools.commons.util.xml;

import org.junit.Test;

public class Xml2JsonUtilTest {
    private static final String xml = "<operation_in>123</operation_in>";

    @Test
    public void xml2Json() {
        Xml2JsonUtil.xml2Json(xml);
    }
}
