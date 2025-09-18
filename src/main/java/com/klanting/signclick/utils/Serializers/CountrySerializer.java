package com.klanting.signclick.utils.Serializers;

import com.google.gson.*;
import com.klanting.signclick.logicLayer.countryLogic.Country;
import com.klanting.signclick.logicLayer.countryLogic.CountryManager;

import java.lang.reflect.Type;
import java.util.UUID;

import static com.klanting.signclick.logicLayer.parties.ElectionTools.setupElectionDeadline;

public class CountrySerializer implements JsonSerializer<Country>, JsonDeserializer<Country> {
    /*
    * Serialize Country to Gson
    * */

    @Override
    public JsonElement serialize(Country country, Type type, JsonSerializationContext context) {
        return country.toJson(context);
    }

    @Override
    public Country deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        Country country = new Country(obj, context);

        if (country.getCountryElection() != null){
            setupElectionDeadline(country);
        }

        for (UUID uuid: country.getOwners()){
            CountryManager.addPlayerToCountryMap(uuid, country);
        }

        for (UUID uuid: country.getMembers()){
            CountryManager.addPlayerToCountryMap(uuid, country);
        }

        return country;
    }

}
