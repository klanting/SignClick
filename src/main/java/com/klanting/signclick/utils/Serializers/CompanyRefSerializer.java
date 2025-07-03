package com.klanting.signclick.utils.Serializers;

import com.google.gson.*;
import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.CompanyRef;
import com.klanting.signclick.economy.Market;

import java.lang.reflect.Type;

public class CompanyRefSerializer implements JsonSerializer<CompanyRef>, JsonDeserializer<CompanyRef> {
    /*
     * SerializeCompany to Gson
     * */

    @Override
    public JsonElement serialize(CompanyRef company, Type type, JsonSerializationContext context) {
        JsonObject element = new JsonObject();
        element.add("stockName", JsonParser.parseString(company.getStockName()));
        element.add("type", JsonParser.parseString("companyRef"));
        return element;
    }

    @Override
    public CompanyRef deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        return new CompanyRef(Market.getCompany(obj.get("stockName").getAsString()));
    }
}
