package com.klanting.signclick.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.contracts.ContractCTC;
import com.klanting.signclick.economy.parties.Election;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Level;

import static com.klanting.signclick.economy.parties.ElectionTools.setupElectionDeadline;


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

    public static class LocationSerializer implements JsonSerializer<Location>, JsonDeserializer<Location> {

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

            if (location.getWorld() == null){
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

    public static class CountrySerializer implements JsonSerializer<Country>, JsonDeserializer<Country> {

        @Override
        public JsonElement serialize(Country country, Type type, JsonSerializationContext context) {
            return country.toJson(context);
        }

        @Override
        public Country deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            Country country = new Country(obj, context);

            if (country.getCountryElection() != null){
                setupElectionDeadline(country, country.getCountryElection().getToWait()*20L);
            }


            return country;
        }

    }

    public static class ElectionSerializer implements JsonSerializer<Election>, JsonDeserializer<Election> {

        @Override
        public JsonElement serialize(Election election, Type type, JsonSerializationContext context) {
            return election.toJson(context);
        }

        @Override
        public Election deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();

            long time = obj.get("to_wait").getAsLong();

            Map<String, Integer> vote_dict = context.deserialize(obj.get("vote_dict"),
                    new TypeToken<HashMap<String, Integer>>(){}.getType());

            List<UUID> alreadyVoted = context.deserialize(obj.get("voted"),
                    new TypeToken<ArrayList<UUID>>(){}.getType());

            return new Election(obj.get("name").getAsString(), time+(System.currentTimeMillis()/1000), vote_dict, alreadyVoted);
        }

    }

    public static <T> void writeSave(String name, T value){
        /*
        * Save object inside a json file
        * */

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Company.class, new CompanySerializer());
        builder.registerTypeAdapter(Country.class, new CountrySerializer());
        builder.registerTypeAdapter(Location.class, new LocationSerializer());
        builder.registerTypeAdapter(Election.class, new ElectionSerializer());
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
        builder.registerTypeAdapter(Country.class, new CountrySerializer());
        builder.registerTypeAdapter(Location.class, new LocationSerializer());
        builder.registerTypeAdapter(UUID.class, new UUIDDeserializer());
        builder.registerTypeAdapter(Election.class, new ElectionSerializer());
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
