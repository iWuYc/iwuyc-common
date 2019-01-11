package com.iwuyc.tools.commons.util.xml;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import org.dom4j.Element;
import org.dom4j.Node;

import java.io.IOException;

public class XmlToJsonObjectParser implements Parser<Node, JsonObject> {
    private final JsonWriter writer;

    public XmlToJsonObjectParser(JsonWriter writer) {
        this.writer = writer;
    }

    public JsonObject parser(Node element) {
        JsonObject result = new JsonObject();
        Node nodes = ((Element)element).content().get(0);
        try {
            writer.beginObject();

            for (Node item : ((Element)nodes).content()) {
                if (null != item.getName()) {
                    writer.name(item.getName());
                }
                parserItem(item);
            }
            writer.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void parserItem(Node item) throws IOException {
        short nodeType = item.getNodeType();
        switch (nodeType) {
        case Node.ELEMENT_NODE:
            new XmlToJsonObjectParser(this.writer).parser(item);
            break;
        case Node.TEXT_NODE:
            this.writer.value(item.getStringValue());
            break;
        }
    }

}
