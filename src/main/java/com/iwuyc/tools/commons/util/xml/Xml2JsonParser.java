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

    private final JsonWriter jsonBuilder;
    private final StringWriter write;
    private boolean isBegin = true;
    private boolean isCreated = false;
    private String result;

    public Xml2JsonParser() {
        this.write = new StringWriter();
        this.jsonBuilder = new JsonWriter(write);
    }

    @Override
    public void parser(Node ele) {
        try {
            parserEle(ele);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String result() {
        if (!isCreated) {
            this.result = write.toString();
        }
        return this.result;
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
        isBegin = false;
        List<Node> eles = ele.content();
        this.jsonBuilder.beginObject();
        this.jsonBuilder.name(name);
        for (Node item : eles) {
            parser(item);
        }
        this.jsonBuilder.endObject();
    }

    private void arrayParser(Branch ele) throws IOException {
        String fieldName = ele.getName();
        if (!isBegin) {
            this.jsonBuilder.name(fieldName);
        } else {
            isBegin = false;
        }
        this.jsonBuilder.beginArray();
        List<Node> items = ele.content();
        for (Node item : items) {
            parserEle(item);
        }
        this.jsonBuilder.endArray();
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
