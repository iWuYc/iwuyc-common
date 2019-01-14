package com.iwuyc.tools.commons.util.xml;

import com.google.gson.stream.JsonWriter;
import com.iwuyc.tools.commons.util.NumberUtils;
import com.iwuyc.tools.commons.util.PatternCacheUtils;
import org.dom4j.Branch;
import org.dom4j.Node;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Xml 解析器
 *
 * @author Neil
 */
public class Xml2JsonParser implements Parser<Node, String> {

    private boolean isBegin = true;

    public Xml2JsonParser() {
    }

    @Override
    public String parser(Node ele) {
        try (StringWriter write = new StringWriter(); JsonWriter jsonBuilder = new JsonWriter(write);) {
            parserEle(jsonBuilder, ele);
            return write.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void parserEle(JsonWriter jsonBuilder, Node ele) throws IOException {
        short nodeType = ele.getNodeType();
        switch (nodeType) {
        case Node.ELEMENT_NODE:
        case Node.DOCUMENT_NODE:
            multiObject(jsonBuilder, (Branch) ele);
            break;
        case Node.TEXT_NODE:
            leaf(jsonBuilder, ele);
            break;
        default:
            break;
        }
    }

    private void multiObject(JsonWriter jsonBuilder, Branch ele) throws IOException {
        if (!ele.hasContent()) {
            return;
        }
        String name = ele.getName();
        boolean isArray = isArray(name);
        if (isArray) {
            arrayParser(jsonBuilder, ele);
            return;
        }
        isBegin = false;
        List<Node> eles = ele.content();
        jsonBuilder.beginObject();
        jsonBuilder.name(name);
        for (Node item : eles) {
            parser(item);
        }
        jsonBuilder.endObject();
    }

    private void arrayParser(JsonWriter jsonBuilder, Branch ele) throws IOException {
        String fieldName = ele.getName();
        if (!isBegin) {
            jsonBuilder.name(fieldName);
        } else {
            isBegin = false;
        }
        jsonBuilder.beginArray();
        List<Node> items = ele.content();
        for (Node item : items) {
            parserEle(jsonBuilder, item);
        }
        jsonBuilder.endArray();
    }

    private boolean isArray(String name) {
        String innerName = name.toLowerCase();
        Pattern isArr = PatternCacheUtils.getPattern("(arr|list|array|collection)+");
        return isArr.matcher(innerName).find();
    }

    private void leaf(JsonWriter jsonBuilder, Node ele) throws IOException {
        String val = ele.getStringValue();
        if (NumberUtils.isNumber(val)) {
            jsonBuilder.value(new BigInteger(val));
        } else {
            jsonBuilder.value(val);
        }
    }
}
