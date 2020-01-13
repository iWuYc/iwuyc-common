package com.iwuyc.tools.commons.util.json.mapper;

import com.google.gson.*;
import com.iwuyc.tools.commons.util.json.GsonUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;
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
            return source;
        }
        if (targetStruct.isJsonPrimitive()) {
            return jsonPrimitive();
        } else if (isJsonArr(targetStruct)) {
            return jsonArray();
        } else if (targetStruct.isJsonObject()) {
            return jsonObject();
        }
        return null;
    }

    private boolean isJsonArr(JsonElement targetStruct) {
        if (targetStruct.isJsonArray()) {
            return true;
        }
        return targetStruct.isJsonObject() && targetStruct.getAsJsonObject().has(JsonMapper.SOURCE_ARR);
    }

    private JsonElement jsonArray() {
        final CurrentNodeInfo currentNodeInfo = currentNodeStack.pop();
        final JsonElement targetStruct = currentNodeInfo.getCurrentNodeStruct();
        final JsonObject templates;
        if (targetStruct.isJsonObject()) {
            templates = targetStruct.getAsJsonObject();
        } else if (targetStruct.isJsonArray()) {
            final JsonArray targetStructArr = targetStruct.getAsJsonArray();
            if (targetStructArr.size() == 0) {
                log.warn("用于描述新结构的数组为空，无法计算新的结构，返回空数组。targetStruct:{}。", targetStruct);
                return new JsonArray();
            }
            templates = targetStructArr.get(0).getAsJsonObject();
        } else {
            throw new IllegalArgumentException();
        }
        JsonArray result = new JsonArray();
        final String sourceArrXpath = templates.getAsJsonPrimitive(JsonMapper.SOURCE_ARR).getAsString();
        final Optional<JsonElement> sourceArrOpt = GsonUtil.findOutNode(currentNodeInfo.getSource(), sourceArrXpath);
        if (!sourceArrOpt.isPresent() || !sourceArrOpt.get().isJsonArray()) {
            log.warn("未找到对应的数据源节点或目标节点非jsonArray.xpath:{};sourceArrOpt:{}", sourceArrXpath, sourceArrOpt);
            return result;
        }
        final JsonArray sourceArr = sourceArrOpt.get().getAsJsonArray();
        JsonPrimitive valXpath = templates.getAsJsonPrimitive(JsonMapper.VALUE_XPATH);
        if (valXpath != null) {
            for (JsonElement item : sourceArr) {
                CurrentNodeInfo itemNodeInfo = new CurrentNodeInfo();
                itemNodeInfo.setCurrentNodeStruct(valXpath);
                itemNodeInfo.setSource(item);
                itemNodeInfo.setParentNode(result);
                currentNodeStack.push(itemNodeInfo);
            }
        } else {
            for (JsonElement item : sourceArr) {
                JsonObject jsonObject = new JsonObject();
                for (Map.Entry<String, JsonElement> templateField : templates.entrySet()) {
                    String key = templateField.getKey();
                    if (JsonMapper.RESERVED_WORD.contains(key)) {
                        continue;
                    }
                    CurrentNodeInfo itemNodeInfo = new CurrentNodeInfo();
                    itemNodeInfo.setNodeName(key);
                    itemNodeInfo.setCurrentNodeStruct(templateField.getValue());
                    itemNodeInfo.setSource(item);
                    itemNodeInfo.setParentNode(jsonObject);
                    currentNodeStack.push(itemNodeInfo);
                }
                result.add(jsonObject);
            }
        }
        appendParent(currentNodeInfo, result);
        return result;
    }

    private JsonElement jsonObject() {
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
        return jsonObject;
    }

    private JsonElement jsonPrimitive() {
        CurrentNodeInfo currentNodeInfo = currentNodeStack.pop();
        JsonElement source = currentNodeInfo.getSource();
        if (source.isJsonPrimitive()) {
            return source;
        }
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
