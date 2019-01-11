package com.iwuyc.tools.commons.util.xml;

import com.google.gson.stream.JsonWriter;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.StringWriter;

public class Xml2JsonUtil {

    public static void xml2Json(String xml) {
        try (StringWriter writer = new StringWriter(); JsonWriter jsonBuilder = new JsonWriter(writer);) {
            Document document = DocumentHelper.parseText(xml);

            Element root = document.getRootElement();
            XmlToJsonObjectParser objParser = new XmlToJsonObjectParser(jsonBuilder);
            objParser.parser(root);
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void object() {
    }
}
