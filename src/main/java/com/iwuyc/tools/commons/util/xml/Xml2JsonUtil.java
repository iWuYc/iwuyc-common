package com.iwuyc.tools.commons.util.xml;

import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.iwuyc.tools.commons.util.json.GsonUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;

import java.io.StringWriter;

/**
 * 将xml解析成json的工具类
 *
 * @author Neil
 */
public class Xml2JsonUtil {

    /**
     * 将xml转换为json字符串
     *
     * @param xml 待转换的xml字符串
     * @return 转换后的json字符串
     */
    public static String xml2JsonString(String xml) {
        try (StringWriter writer = new StringWriter(); JsonWriter jsonBuilder = new JsonWriter(writer);) {
            Document document = DocumentHelper.parseText(xml);

            XmlToJsonParser xmlToJsonParser = new XmlToJsonParser(jsonBuilder);
            xmlToJsonParser.parser(document.getRootElement());

            return writer.toString();
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
    public static JsonElement xml2JsonObject(String xml) {
        String json = xml2JsonString(xml);
        return GsonUtil.jsonToObj(json);
    }
}
