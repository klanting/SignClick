package com.klanting.signclick.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.klanting.signclick.economy.CountryManager;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class JsonTools {
    public static <T> JsonObject toJson(Map<String, Object> fieldMap, Map<String, BiFunction<String, JsonObject, JsonObject>> softLink,
                                        JsonSerializationContext context){
        JsonObject jsonObject = new JsonObject();

        for (String fieldName : fieldMap.keySet()) {
            Object fieldValue = fieldMap.get(fieldName);

            if (softLink.containsKey(fieldName)){
                jsonObject = softLink.get(fieldName).apply(fieldName, jsonObject);
                continue;
            }
            jsonObject.add(fieldName, context.serialize(fieldValue));

        }

        return jsonObject;
    }
}
