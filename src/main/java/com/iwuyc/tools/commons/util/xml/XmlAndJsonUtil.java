package com.iwuyc.tools.commons.util.xml;

import com.google.gson.JsonElement;
import com.iwuyc.tools.commons.util.json.GsonUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

/**
 * 将xml解析成json的工具类
 *
 * @author Neil
 */
public class XmlAndJsonUtil {
    private final static Xml2JsonParser xmlToJsonParser = new Xml2JsonParser();

    /**
     * 将xml转换为json字符串
     *
     * @param xml 待转换的xml字符串
     * @return 转换后的json字符串
     */
    public static String xml2JsonStr(String xml) {
        try {
            Document document = DocumentHelper.parseText(xml);
            return xmlToJsonParser.parser(document.getRootElement());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 将xml转换为 {@link JsonElement} 对象
     *
     * @param xml 待转换的xml字符串
     * @return 转换后的 {@link JsonElement} 实例
     */
    public static JsonElement xml2JsonObj(String xml) {
        String json = xml2JsonStr(xml);
        return GsonUtil.jsonToObj(json);
    }

    public static Node json2XmlObj(String json) {
        return json2XmlObj(GsonUtil.jsonToObj(json));
    }

    private static Node json2XmlObj(JsonElement json) {
        Json2XmlParser parser = new Json2XmlParser();

        Node result = parser.parser(json);
        return result;
    }

    public static String json2XmlStr(String json) {
        return json2XmlObj(json).asXML();
    }
}
