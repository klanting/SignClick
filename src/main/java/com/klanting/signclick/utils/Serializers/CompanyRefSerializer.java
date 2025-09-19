package com.klanting.signclick.utils.Serializers;

import com.google.gson.*;
import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.logicLayer.companyLogic.CompanyRef;
import com.klanting.signclick.logicLayer.companyLogic.Market;

import java.lang.reflect.Type;

public class CompanyRefSerializer implements JsonSerializer<CompanyRef>, JsonDeserializer<CompanyRef> {
    /*
     * SerializeCompany to Gson
     * */

    @Override
    public JsonElement serialize(CompanyRef company, Type type, JsonSerializationContext context) {
        JsonObject element = new JsonObject();
        element.add("stockName", JsonParser.parseString(company.getStockName()));
        element.add("classType", JsonParser.parseString("companyRef"));
        return element;
    }

    @Override
    public CompanyRef deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        CompanyI comp = Market.getCompany(obj.get("stockName").getAsString());

        if(comp != null){
            return new CompanyRef(comp);
        }

        return new CompanyRef(obj.get("stockName").getAsString());

    }
}
