package com.klanting.signclick.utils.Serializers;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.klanting.signclick.economy.parties.Election;
import versionCompatibility.CompatibleLayer;

import java.lang.reflect.Type;
import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class ElectionSerializer implements JsonSerializer<Election>, JsonDeserializer<Election> {
    /*
     * Serialize Election to Gson
     * */

    @Override
    public JsonElement serialize(Election election, Type type, JsonSerializationContext context) {
        return election.toJson(context);
    }

    @Override
    public Election deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        long time = obj.get("to_wait").getAsLong();

        Map<String, Integer> voteDict = context.deserialize(obj.get("vote_dict"),
                new TypeToken<HashMap<String, Integer>>(){}.getType());

        List<UUID> alreadyVoted = context.deserialize(obj.get("voted"),
                new TypeToken<ArrayList<UUID>>(){}.getType());

        return new Election(obj.get("name").getAsString(), time+ CompatibleLayer.getCurrentTick(), voteDict, alreadyVoted);
    }

}