package com.iwuyc.tools.commons.util.xml;

import org.junit.Test;

public class XmlAndJsonUtilTest {

    @Test
    public void xml2Json() {
        String xml = "<age>123</age>";
//        printXml2Json(xml);

        xml = "<nameList><name>Jack</name><name>Tom</name></nameList>";
//        printXml2Json(xml);

        xml = "<root><nameList><name>Jack</name><name>Tom</name></nameList><ageList><age>1</age><age>2</age><age>3</age></ageList></root>";
        printXml2Json(xml);
    }

    private void printXml2Json(String xml) {
        System.out.println(XmlAndJsonUtil.xml2JsonStr(xml));
    }

    @Test
    public void json2Xml() throws Exception {
        String xmlStr = XmlAndJsonUtil.json2XmlStr("jack");
        System.out.println(xmlStr);
    }
}
