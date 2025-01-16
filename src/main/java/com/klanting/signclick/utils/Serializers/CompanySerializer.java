package com.klanting.signclick.utils.Serializers;

import com.google.gson.*;
import com.klanting.signclick.economy.Company;

import java.lang.reflect.Type;

public class CompanySerializer implements JsonSerializer<Company>, JsonDeserializer<Company> {
    /*
     * SerializeCompany to Gson
     * */

    @Override
    public JsonElement serialize(Company company, Type type, JsonSerializationContext context) {
        return company.toJson(context);
    }

    @Override
    public Company deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        return new Company(obj, context);
    }

}