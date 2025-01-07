package com.klanting.signclick.utils;

import com.google.gson.*;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.Company;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Level;



public class Utils {
    /*
    * Basic utils used everywhere
    * */

    public static List<UUID> toUUIDList(List<String> stringList){
        /*
        * Convert a list of UUIDs as String type to a list of UUIDs
        * */
        List<UUID> UUIDList = new ArrayList<>();
        for (String s: stringList){
            UUIDList.add(UUID.fromString(s));
        }
        return UUIDList;
    }


    public static class UUIDDeserializer implements JsonDeserializer<UUID> {
        @Override
        public UUID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return UUID.fromString(json.getAsString());
        }

    }

    public static class CompanySerializer implements JsonSerializer<Company>, JsonDeserializer<Company> {

        @Override
        public JsonElement serialize(Company company, Type type, JsonSerializationContext context) {
            return company.toJson(context);
        }

        @Override
        public Company deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            return new Company(obj, context);
        }

    }

    public static <T> void writeSave(String name, T value){
        /*
        * Save object inside a json file
        * */

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Company.class, new CompanySerializer());
        Gson gson = builder.create();

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
        /*
        * Read object from a json file
        * */

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Company.class, new CompanySerializer());
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
        } catch (IOException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }

    }

    public static void setSign(SignChangeEvent sign, String[] content){
        /*
        * update a sign with the provided text
        * */

        assert content.length == 4;

        Sign s = (Sign) sign.getBlock().getState();

        for (int i=0; i<4; i++){
            sign.setLine(i, content[i]);
            s.setLine(i, content[i]);
        }

        s.update();


    }
}
