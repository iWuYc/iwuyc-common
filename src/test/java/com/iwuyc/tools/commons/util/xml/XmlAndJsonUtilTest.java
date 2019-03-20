package com.iwuyc.tools.commons.util.xml;

import com.iwuyc.tools.commons.util.file.FileUtil;
import org.junit.Test;

public class XmlAndJsonUtilTest {

    @Test
    public void xml2Json() {
        String xml = "<age>123</age>";
        //        printXml2Json(xml);

        xml = "<root><nameList><name>Jack</name><name>Tom</name></nameList></root>";
        //        printXml2Json(xml);

        xml = "<names><nameList><name>Jack</name><name>Tom</name></nameList></names>";
        //        printXml2Json(xml);

        xml = "<root>name</root>";
        //        printXml2Json(xml);

        xml = FileUtil.readAll("classpath:/xmltest.xml");
        printXml2Json(xml);
        //        System.out.println(xml);
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
