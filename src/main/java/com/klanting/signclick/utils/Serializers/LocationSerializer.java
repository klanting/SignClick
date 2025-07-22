package com.klanting.signclick.utils.Serializers;

import com.google.gson.*;
import com.klanting.signclick.SignClick;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;

public class LocationSerializer implements JsonSerializer<Location>, JsonDeserializer<Location> {

    @Override
    public JsonElement serialize(Location location, Type type, JsonSerializationContext context) {
        JsonObject element = new JsonObject();

        if (location == null){
            element.add("null", new JsonPrimitive(true));
            return element;
        }
        element.add("null", new JsonPrimitive(false));
        element.add("x", new JsonPrimitive(location.getX()));
        element.add("y", new JsonPrimitive(location.getY()));
        element.add("z", new JsonPrimitive(location.getZ()));
        element.add("yaw", new JsonPrimitive(location.getYaw()));
        element.add("pitch", new JsonPrimitive(location.getPitch()));

        try{
            if (location.getWorld() == null){
                element.add("world", null);
                return element;
            }
        }catch (IllegalArgumentException e){
            element.add("world", null);
            return element;
        }


        element.add("world", new JsonPrimitive(location.getWorld().getName()));
        return element;
    }

    @Override
    public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        boolean isNull = obj.get("null").getAsBoolean();
        if (isNull){
            return null;
        }

        World world;
        if (obj.get("world") != null){
            world = SignClick.getPlugin().getServer().getWorld(obj.get("world").getAsString());
        }else{
            world = null;
        }

        double x = obj.get("x").getAsDouble();
        double y = obj.get("y").getAsDouble();
        double z = obj.get("z").getAsDouble();
        float yaw = obj.get("yaw").getAsFloat();
        float pitch = obj.get("pitch").getAsFloat();

        return new Location(world, x, y, z, yaw, pitch);
    }

}