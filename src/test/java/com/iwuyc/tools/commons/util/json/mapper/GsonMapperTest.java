package com.iwuyc.tools.commons.util.json.mapper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.iwuyc.tools.commons.util.json.GsonUtil;
import org.junit.Test;

public class GsonMapperTest {

    @Test
    public void mapper() {
        final GsonMapper gsonMapper = new GsonMapper();
        JsonElement targetStruct = new JsonPrimitive("[0].age");
        JsonElement childrenArr = GsonUtil.toObject("[{'name':'jack','age':10},{'name':'tom','age':11}]");
        JsonElement result = gsonMapper.mapper(childrenArr, targetStruct);
        System.out.println(result);

        targetStruct = new JsonPrimitive("age");
        result = gsonMapper.mapper(childrenArr, targetStruct);
        System.out.println(result);

        result = gsonMapper.mapper(childrenArr, null);
        System.out.println(result);

        JsonObject srcObj = new JsonObject();
        srcObj.add("children", childrenArr);
        targetStruct = new JsonPrimitive("children[1].age");
        result = gsonMapper.mapper(srcObj, targetStruct);
        System.out.println(result);

        targetStruct = new JsonPrimitive("children.age");
        result = gsonMapper.mapper(srcObj, targetStruct);
        System.out.println(result);

        JsonObject targetStructObj = new JsonObject();
        targetStructObj.add("name", new JsonPrimitive("children[1].name"));
        targetStructObj.add("age", new JsonPrimitive("children[0].age"));
        result = gsonMapper.mapper(srcObj, targetStructObj);
        // {"age":10,"name":"tom"}
        System.out.println(result);

        JsonObject targetStructObj1 = new JsonObject();
        targetStructObj1.add("mergeInfo", targetStructObj);
        result = gsonMapper.mapper(srcObj, targetStructObj1);
        // {"age":10,"name":"tom"}
        System.out.println(result);

        JsonArray targetStructArr = new JsonArray();
        JsonObject jsonObj = new JsonObject();
        jsonObj.add("name", new JsonPrimitive("children[$].name"));
        targetStructArr.add(jsonObj);
        result = gsonMapper.mapper(srcObj, targetStructArr);
        System.out.println(result);
    }
}
