package com.klanting.signclick.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Function;

public class JsonTools {
    public static JsonObject toJson(Map<String, Pair<Type, Object>> fieldMap, Map<String, Function<JsonObject, JsonObject>> softLink,
                                    JsonSerializationContext context){
        JsonObject jsonObject = new JsonObject();

        for (String fieldName : fieldMap.keySet()) {
            Object fieldValue = fieldMap.get(fieldName).getRight();

            if (softLink.containsKey(fieldName)){
                jsonObject = softLink.get(fieldName).apply(jsonObject);
                continue;
            }

            jsonObject.add(fieldName, context.serialize(fieldValue, fieldMap.get(fieldName).getLeft()));

        }

        return jsonObject;
    }
}
