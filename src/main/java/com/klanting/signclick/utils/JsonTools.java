package com.klanting.signclick.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.bukkit.ChatColor;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.bukkit.Bukkit.getServer;

public class JsonTools {
    public static JsonObject toJson(Map<String, Object> fieldMap, Map<String, Function<JsonObject, JsonObject>> softLink,
                                    JsonSerializationContext context){
        JsonObject jsonObject = new JsonObject();

        for (String fieldName : fieldMap.keySet()) {
            Object fieldValue = fieldMap.get(fieldName);

            if (softLink.containsKey(fieldName)){
                jsonObject = softLink.get(fieldName).apply(jsonObject);
                continue;
            }
            jsonObject.add(fieldName, context.serialize(fieldValue));

        }

        return jsonObject;
    }
}
