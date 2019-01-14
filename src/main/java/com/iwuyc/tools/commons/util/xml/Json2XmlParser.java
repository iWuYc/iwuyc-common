package com.iwuyc.tools.commons.util.xml;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.iwuyc.tools.commons.util.PatternCacheUtils;
import org.dom4j.CharacterData;
import org.dom4j.*;

import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class Json2XmlParser implements Parser<JsonElement, Node> {
    private final Stack<Branch> documents = new Stack<>();
    private final AtomicBoolean rootCreated = new AtomicBoolean();
    private final Document document = DocumentHelper.createDocument();

    @Override
    public Node parser(JsonElement ele) {
        try {
            parserCalculator("root", ele);
            return document;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }

    private void parserCalculator(String name, JsonElement ele) {
        if (ele.isJsonPrimitive()) {
            leafCreate(name, (JsonPrimitive) ele);
            return;
        } else if (ele.isJsonObject()) {
            multiCreate(name, (JsonObject) ele);
            return;
        }

    }

    private void multiCreate(String name, JsonObject ele) {
        Set<Map.Entry<String, JsonElement>> childNodesJson = ele.entrySet();
        if (childNodesJson.size() == 1) {
            Map.Entry<String, JsonElement> rootEles = childNodesJson.iterator().next();
            String rootName = rootEles.getKey();
            JsonElement rootEle = rootEles.getValue();
            parserCalculator(rootName, rootEle);
            return;
        }
        createRoot(name);

        for (Map.Entry<String, JsonElement> item : childNodesJson) {
            String itemName = item.getKey();
            JsonElement itemVal = item.getValue();
        }
    }

    private void leafCreate(String name, JsonPrimitive ele) {
        createRoot(name);
        String val = ele.getAsString();
        boolean isCDATA = isCDATA(val);
        CharacterData xmlEle;
        if (isCDATA) {
            xmlEle = DocumentHelper.createCDATA(ele.getAsString());
        } else {
            xmlEle = DocumentHelper.createText(val);
        }
        documents.peek().add(xmlEle);
    }

    private boolean isCDATA(String val) {
        Pattern isCDATAPattern = PatternCacheUtils.getPattern("[<>{}&]+");
        return isCDATAPattern.matcher(val).find();
    }

    private void createRoot(String rootName) {
        if (documents.isEmpty()) {
            Element root = DocumentHelper.createElement(rootName);
            this.document.setRootElement(root);
            documents.push(root);
        } else {

        }
    }
}
