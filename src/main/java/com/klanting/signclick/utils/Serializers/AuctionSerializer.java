package com.klanting.signclick.utils.Serializers;

import com.google.gson.*;
import com.klanting.signclick.logicLayer.companyPatent.Auction;

import java.lang.reflect.Type;

public class AuctionSerializer implements JsonSerializer<Auction>, JsonDeserializer<Auction> {
    @Override
    public JsonElement serialize(Auction auction, Type type, JsonSerializationContext context) {
        return auction.toJson(context);
    }

    @Override
    public Auction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        return new Auction(obj, context);
    }

}
