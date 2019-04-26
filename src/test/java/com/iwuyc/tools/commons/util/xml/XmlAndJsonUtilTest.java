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

    @Test
    public void testXmlJsonParse() {
        String xmlStr = "<xml>\n" + "    <ToUserName><![CDATA[gh_893c3c0fca1b]]></ToUserName>\n"
            + "    <Encrypt><![CDATA[ruInbQbcGaWIMrOWqkH4KU+8kb/geAlYfV3UcauCn2EwErvC2NMeQrh4VB6URNnAactuqw3PUGOMzcfKQbsbWk7QnhfRJiv4MTPgxBj/23b64zyFy0KblWo9QCCEisFRvUe0Uk0cQyQYcdIKUeNIxCnUAH+38Z2pGUoJeJ9JAGstp+YceqwLUYqPK3rtXDrziBTV2Iw3vlBfAAGv5ESQ7GT6/ctoCBerIrwWbxv3sRCrOMmwGammo+ty25qMHxqvw9Dd4yTJphjoKXK+5vu5Hjj81gkeOhfNLlBR/bQTMUbr8jTMPcqWlR35ctJM21knCgZzD87P9DR8CsFsp/viUfnxVnfQcYYCKsPq63ideEYlhH2oV8FGWadUMbCADqKYYmbPr1sT5xlbKzFPMDtNWjiCozfnGjhatWAUGbKAVNo=]]></Encrypt>\n"
            + "</xml>";
        System.out.println(XmlAndJsonUtil.xml2JsonObj(xmlStr));
    }
}
