package com.klanting.signclick.utils.Serializers;

import com.google.gson.*;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.policies.*;

import java.lang.reflect.Type;
import java.util.UUID;

import static com.klanting.signclick.economy.parties.ElectionTools.setupElectionDeadline;

public class PolicySerializer implements JsonSerializer<Policy>, JsonDeserializer<Policy>{
    @Override
    public JsonElement serialize(Policy policy, Type type, JsonSerializationContext context) {
        return context.serialize(policy);
    }

    @Override
    public Policy deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        int id = json.getAsJsonObject().get("id").getAsInt();
        int level = json.getAsJsonObject().get("level").getAsInt();

        return switch (id) {
            case 0 -> new PolicyEconomics(level);
            case 1 -> new PolicyMarket(level);
            case 2 -> new PolicyMilitary(level);
            case 3 -> new PolicyTourist(level);
            case 4 -> new PolicyTaxation(level);
            default -> null;
        };
    }
}
