package com.klanting.signclick.utils;

import com.google.gson.*;
import com.klanting.signclick.SignClick;

import java.io.*;
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


    public static class UUIDDeserializer implements JsonDeserializer<UUID> {
        @Override
        public UUID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            SignClick.getPlugin().getLogger().log(Level.SEVERE, "triggered");
            return UUID.fromString(json.getAsString());
        }

    }


    public static <T> void writeSave(String name, T value){
        Gson gson = new GsonBuilder().create();
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



    public static <T> T readSave(String name, Type token, T defaultValue){

        GsonBuilder builder = new GsonBuilder();

        builder.registerTypeAdapter(UUID.class, new UUIDDeserializer());



        Gson gson = builder.create();

        File file = new File(SignClick.getPlugin().getDataFolder()+"/"+name+".json");

        try {
            file.getParentFile().mkdir();
            file.createNewFile();
            Reader reader = new FileReader(file);

            T value = gson.fromJson(reader, token);

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
