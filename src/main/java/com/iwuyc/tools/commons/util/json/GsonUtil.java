package com.iwuyc.tools.commons.util.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * Gson 的封装工具包
 * @author Neil
 */
public class GsonUtil {
    private static final Gson GSON = new Gson();

    /**
     * 将对象转换为json字符串
     * @param obj 待转换的对象
     * @return json字符串
     */
    public static String objToJson(Object obj) {
        return GSON.toJson(obj);
    }

    /**
     * 将json转换为Object对象
     * @param json 待转换的json字符串
     * @return 转换后的对象
     */
    public static JsonElement jsonToObj(String json) {
        return GSON.fromJson(json, JsonElement.class);
    }
}
