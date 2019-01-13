package com.iwuyc.tools.commons.util.xml;

import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.iwuyc.tools.commons.util.NumberUtils;
import com.iwuyc.tools.commons.util.PatternCacheUtils;
import org.dom4j.Branch;
import org.dom4j.Node;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Xml 解析器
 *
 * @author Neil
 */
public class XmlToJsonParser implements Parser<Node, JsonElement> {

    private final JsonWriter jsonBuilder;

    public XmlToJsonParser(JsonWriter jsonBuilder) {
        this.jsonBuilder = jsonBuilder;
    }

    @Override
    public void parser(Node ele) {
        try {
            parserEle(ele);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parserEle(Node ele) throws IOException {
        short nodeType = ele.getNodeType();
        switch (nodeType) {
            case Node.ELEMENT_NODE:
            case Node.DOCUMENT_NODE:
                multiObject((Branch)ele);
                break;
            case Node.TEXT_NODE:
                leaf(ele);
                break;
            default:
                break;
        }
    }

    private void multiObject(Branch ele) throws IOException {
        if (!ele.hasContent()) {
            return;
        }
        String name = ele.getName();
        boolean isArray = isArray(name);
        if (isArray) {
            arrayParser(ele);
            return;
        }

        List<Node> eles = ele.content();
        this.jsonBuilder.beginObject();
        this.jsonBuilder.name(name);
        for (Node item : eles) {
            parser(item);
        }
        this.jsonBuilder.endObject();
    }

    private void arrayParser(Branch ele) {

    }

    private boolean isArray(String name) {
        String innerName = name.toLowerCase();
        Pattern isArr = PatternCacheUtils.getPattern("(arr|list|array|collection)+");
        return isArr.matcher(innerName).find();
    }

    private void leaf(Node ele) throws IOException {
        String val = ele.getStringValue();
        if (NumberUtils.isNumber(val)) {
            this.jsonBuilder.value(new BigInteger(val));
        } else {
            this.jsonBuilder.value(val);
        }
    }
}
