package com.klanting.signclick.utils.Serializers;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.klanting.signclick.economy.companyPatent.Auction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.lang.reflect.Type;
import java.util.Map;

public class BlockSerializer implements JsonSerializer<Block>, JsonDeserializer<Block> {
    @Override
    public JsonElement serialize(Block block, Type type, JsonSerializationContext context) {
        return context.serialize(block.getLocation());
    }

    @Override
    public Block deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        Location loc = context.deserialize(obj, new TypeToken<Location>(){}.getType());
        return loc.getBlock();
    }

}