package com.klanting.signclick.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.klanting.signclick.Economy.Account;
import com.klanting.signclick.SignClick;
import org.bukkit.Color;

import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Level;

public class Utils {

    public static List<UUID> toUUIDList(List<String> stringList){
        List<UUID> UUIDList = new ArrayList<>();
        for (String s: stringList){
            UUIDList.add(UUID.fromString(s));
        }
        return UUIDList;
    }

    public static class UUIDKeyMapDeserializer<T> implements JsonDeserializer<Map<UUID, T>> {

        @Override
        public Map<UUID, T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            SignClick.getPlugin().getLogger().log(Level.SEVERE, "triggered");
            Map<UUID, T> map = new HashMap<>();
            JsonObject jsonObject = json.getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                UUID key = UUID.fromString(entry.getKey());
                T value = context.deserialize(entry.getValue(), ((Class<T>) ((ParameterizedType) typeOfT).getActualTypeArguments()[1])); // Deserializes the value
                map.put(key, value);
            }

            return map;
        }
    }

    public static class UUIDDeserializer implements JsonSerializer<UUID>, JsonDeserializer<UUID> {
        @Override
        public UUID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            SignClick.getPlugin().getLogger().log(Level.SEVERE, "triggered");
            return UUID.fromString(json.getAsString());
        }

        @Override
        public JsonElement serialize(UUID uuid, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(uuid.toString());
        }
    }

    public static <T> void writeSave(String name, T value){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(UUID.class, new UUIDDeserializer())
                .create();
        File file = new File(SignClick.getPlugin().getDataFolder()+"/"+name+".json");

        try {
            file.getParentFile().mkdir();
            file.createNewFile();
            Writer writer = new FileWriter(file, false);
            gson.toJson(value, writer);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static <T> T readSave(String name, T defaultValue){

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(UUID.class, new UUIDDeserializer())
                .registerTypeAdapter(new TypeToken<Map<UUID, Account>>(){}.getType(), new UUIDKeyMapDeserializer<Account>())
                .create();

        File file = new File(SignClick.getPlugin().getDataFolder()+"/"+name+".json");

        try {
            file.getParentFile().mkdir();
            file.createNewFile();
            Reader reader = new FileReader(file);

            Type type = new TypeToken<T>() {}.getType();
            T value = gson.fromJson(reader, type);

            if (value == null){
                value = defaultValue;
            }

            reader.close();
            return value;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
