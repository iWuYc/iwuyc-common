package com.iwuyc.tools.commons.util.json.mapper;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.iwuyc.tools.commons.util.json.GsonUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Stack;

@Slf4j
public class GsonMapper implements JsonMapper<JsonElement, JsonElement> {
    private Stack<CurrentNodeInfo> currentNodeStack = new Stack<>();

    @Override
    public JsonElement mapper(JsonElement source, JsonElement targetStruct) {
        CurrentNodeInfo currentNodeInfo = new CurrentNodeInfo();
        currentNodeInfo.setCurrentNodeStruct(targetStruct);
        currentNodeInfo.setSource(source);
        currentNodeStack.push(currentNodeInfo);

        final JsonElement result = mapperDispatcher();
        while (!currentNodeStack.isEmpty()) {
            mapperDispatcher();
        }
        return result;
    }

    private JsonElement mapperDispatcher() {
        final CurrentNodeInfo currentNodeInfo = currentNodeStack.peek();
        JsonElement targetStruct = currentNodeInfo.getCurrentNodeStruct();
        JsonElement source = currentNodeInfo.getSource();
        if (null == targetStruct || targetStruct.isJsonNull()) {
            log.debug("targetStruct为空，无需做结构映射。直接返回source:{}。", source);
            currentNodeStack.pop();
            return currentNodeInfo.getSource();
        }
        if (targetStruct.isJsonPrimitive()) {
            return jsonPrimitive(source);
        } else if (targetStruct.isJsonObject()) {
            return jsonObject(source);
        } else if (targetStruct.isJsonArray()) {
            return jsonArray(source);
        }
        return null;
    }

    private JsonElement jsonArray(JsonElement source) {
        final CurrentNodeInfo currentNodeInfo = currentNodeStack.pop();

        return null;
    }

    private JsonElement jsonObject(JsonElement source) {
        final CurrentNodeInfo currentNodeInfo = currentNodeStack.pop();
        final JsonObject currentNodeStruct = currentNodeInfo.getCurrentNodeStruct().getAsJsonObject();

        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<String, JsonElement> item : currentNodeStruct.entrySet()) {
            CurrentNodeInfo child = new CurrentNodeInfo();
            child.setSource(currentNodeInfo.getSource());
            child.setCurrentNodeStruct(item.getValue());
            child.setNodeName(item.getKey());
            child.setParentNode(jsonObject);
            currentNodeStack.push(child);
        }
        appendParent(currentNodeInfo, jsonObject);
        System.out.println(currentNodeInfo);
        return jsonObject;
    }

    private JsonElement jsonPrimitive(JsonElement source) {
        if (source.isJsonPrimitive()) {
            return source;
        }
        CurrentNodeInfo currentNodeInfo = currentNodeStack.pop();
        JsonElement targetStruct = currentNodeInfo.getCurrentNodeStruct();
        String targetXpath = targetStruct.getAsString();
        final JsonElement leafNode = GsonUtil.findOutNode(source, targetXpath).orElse(JsonNull.INSTANCE);

        appendParent(currentNodeInfo, leafNode);
        return leafNode;
    }

    private void appendParent(CurrentNodeInfo currentNodeInfo, JsonElement child) {
        final JsonElement parentNode = currentNodeInfo.getParentNode();
        if (parentNode == null) {
            return;
        }
        if (parentNode.isJsonArray()) {
            parentNode.getAsJsonArray().add(child);
        } else if (parentNode.isJsonObject()) {
            parentNode.getAsJsonObject().add(currentNodeInfo.getNodeName(), child);
        }
    }

    @Data
    public static class CurrentNodeInfo {
        /**
         * 当前的结构
         */
        private JsonElement currentNodeStruct;
        /**
         * 数据源
         */
        private JsonElement source;
        /**
         * 父节点，用于存储当前结构
         */
        private JsonElement parentNode;

        private String nodeName;
    }


}
