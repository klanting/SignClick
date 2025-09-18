package com.klanting.signclick.utils.Serializers;

import com.google.gson.*;
import com.klanting.signclick.configs.ConfigManager;
import com.klanting.signclick.logicLayer.companyLogic.Company;

import java.lang.reflect.Type;

public class CompanySerializer implements JsonSerializer<Company>, JsonDeserializer<Company> {
    /*
     * SerializeCompany to Gson
     * */

    private final ConfigManager configManager;

    public CompanySerializer(ConfigManager configManager){
        this.configManager = configManager;
    }

    @Override
    public JsonElement serialize(Company company, Type type, JsonSerializationContext context) {
        JsonObject obj = company.toJson(context);
        obj.add("classType", JsonParser.parseString("company"));
        return obj;
    }

    @Override
    public Company deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        return new Company(obj, context, configManager);
    }

}