package com.klanting.signclick.utils.Serializers;

import com.google.gson.*;
import com.klanting.signclick.logicLayer.companyLogic.patent.Auction;
import com.klanting.signclick.utils.BlockPosKey;

import java.lang.reflect.Type;

public class BlockPosKeySerializer  implements JsonSerializer<BlockPosKey>, JsonDeserializer<BlockPosKey> {
    @Override
    public BlockPosKey deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();
        return BlockPosKey.from(
                obj.get("world").getAsString(),
                obj.get("X").getAsInt(),
                obj.get("Y").getAsInt(),
                obj.get("Z").getAsInt()
        );

    }

    @Override
    public JsonElement serialize(BlockPosKey blockPosKey, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("world", jsonSerializationContext.serialize(blockPosKey.world()));
        jsonObject.add("X", jsonSerializationContext.serialize(blockPosKey.x()));
        jsonObject.add("Y", jsonSerializationContext.serialize(blockPosKey.y()));
        jsonObject.add("Z", jsonSerializationContext.serialize(blockPosKey.z()));

        return jsonObject;
    }
}
