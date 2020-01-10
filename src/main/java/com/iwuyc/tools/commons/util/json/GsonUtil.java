package com.iwuyc.tools.commons.util.json;

import com.google.gson.*;
import com.iwuyc.tools.commons.util.NumberUtils;
import com.iwuyc.tools.commons.util.string.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * Gson 的封装工具包
 *
 * @author Neil
 */
@Slf4j
public class GsonUtil {
    private static final Gson GSON = new Gson();

    /**
     * 将对象转换为json字符串
     *
     * @param obj 待转换的对象
     * @return json字符串
     */
    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    /**
     * 将json转换为Object对象
     *
     * @param json 待转换的json字符串
     * @return 转换后的对象
     */
    public static JsonElement toObject(String json) {
        return toObject(json, JsonElement.class);
    }

    /**
     * 将json转换为Object对象
     *
     * @param json        待转换的json字符串
     * @param targetClass 目标类型
     * @return 转换后的对象
     */
    public static <T> T toObject(String json, Class<T> targetClass) {
        return GSON.fromJson(json, targetClass);
    }

    /**
     * 将一个对象转换为另外一种类型
     *
     * @param data        源数据
     * @param targetClass 目标类型
     * @param <T>         目标类型的泛型
     * @return 转换后的实例
     */
    public static <T> T objectToAnotherType(Object data, Class<T> targetClass) {
        String json = toJson(data);
        return toObject(json, targetClass);
    }

    /**
     * 判断Json对象是否为空
     *
     * @param json 待判断的json对象
     * @return 如果为空，则返回true，否则返回false
     */
    public static boolean isEmpty(JsonElement json) {
        if (json == null || JsonNull.INSTANCE.equals(json)) {
            return true;
        }
        if (json instanceof JsonObject) {
            return ((JsonObject) json).size() == 0;
        }
        if (json instanceof JsonArray) {
            return ((JsonArray) json).size() == 0;
        }
        if (json instanceof JsonPrimitive) {
            return json.isJsonNull() || StringUtils.isEmpty(json.getAsString());
        }
        throw new IllegalArgumentException("Unknown Type:" + json.getClass().getName());
    }

    /**
     * 判断Json对象是否不为空
     *
     * @param json 待判断的json对象
     * @return 如果不为空，则返回true，否则返回false
     */

    public static boolean isNotEmpty(JsonElement json) {
        return !isEmpty(json);
    }

    public static <T> T deepCopy(T source) {
        String json = toJson(source);
        return (T) toObject(json, source.getClass());
    }

    public static Optional<JsonElement> findOutNode(JsonElement source, String targetXpath) {
        final String[] xpaths = targetXpath.split("[.]");
        JsonElement jsonElement = source;
        for (String xpath : xpaths) {
            final XpathInfo xPathInfo = extractArrIndex(xpath);
            if (jsonElement.isJsonArray() && !xPathInfo.isArray()) {
                jsonElement = jsonElement.getAsJsonArray().get(0);
            }
            if (xPathInfo.isArray()) {
                JsonArray jsonArr;
                if (StringUtils.isNotEmpty(xPathInfo.getNodeName()) && jsonElement.isJsonObject()) {
                    jsonArr = jsonElement.getAsJsonObject().getAsJsonArray(xPathInfo.getNodeName());
                } else if (StringUtils.isEmpty(xPathInfo.getNodeName()) && jsonElement.isJsonArray()) {
                    jsonArr = jsonElement.getAsJsonArray();
                } else {
                    log.warn("xpath路径为数组，但源数据对应节点并非数组，未能找到符合表达式的节点:{}", targetXpath);
                    return Optional.empty();
                }
                jsonElement = jsonArr.get(xPathInfo.getIndex());
            } else if (jsonElement.isJsonObject()) {
                jsonElement = jsonElement.getAsJsonObject().get(xPathInfo.getNodeName());
            } else {
                log.warn("非jsonObject节点:{}。未能找到符合表达式的节点:{},xpath:{}", jsonElement.getClass().getName(), targetXpath, xpath);
                return Optional.empty();
            }
        }


        return Optional.ofNullable(jsonElement);
    }

    public static XpathInfo extractArrIndex(String xpath) {
        final int indexStart = xpath.indexOf('[');
        XpathInfo xPathInfo = new XpathInfo();
        final String nodeName;
        if (indexStart < 0) {
            nodeName = xpath;
        } else {
            nodeName = xpath.substring(0, indexStart);
            final String indexStr = xpath.substring(indexStart + 1, xpath.lastIndexOf(']'));
            final int index = NumberUtils.parseInt(indexStr);
            xPathInfo.setIndex(index);
            xPathInfo.setArray(true);
        }
        xPathInfo.setNodeName(nodeName);

        return xPathInfo;
    }
}
