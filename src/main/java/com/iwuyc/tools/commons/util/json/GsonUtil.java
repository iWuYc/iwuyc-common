package com.iwuyc.tools.commons.util.json;

import com.google.gson.*;
import com.iwuyc.tools.commons.util.string.StringUtils;

/**
 * Gson 的封装工具包
 *
 * @author Neil
 */
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
}
