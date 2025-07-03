package com.klanting.signclick.utils.Serializers;

import com.google.gson.*;
import com.klanting.signclick.economy.LicenseSingleton;

import java.lang.reflect.Type;

public class LicenseSingletonSerializer implements JsonSerializer<LicenseSingleton>, JsonDeserializer<LicenseSingleton> {
    @Override
    public JsonElement serialize(LicenseSingleton ls, Type type, JsonSerializationContext context) {
        return ls.toJson(context);
    }

    @Override
    public LicenseSingleton deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        return new LicenseSingleton(obj, context);
    }

}