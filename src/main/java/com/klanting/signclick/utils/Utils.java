package com.klanting.signclick.utils;

import com.google.gson.*;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.companyPatent.Auction;
import com.klanting.signclick.economy.logs.ContractChange;
import com.klanting.signclick.economy.logs.ContractPayment;
import com.klanting.signclick.economy.logs.PluginLogs;
import com.klanting.signclick.economy.parties.Election;
import com.klanting.signclick.utils.Serializers.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.*;


public class Utils {
    /*
    * Basic utils used everywhere
    * */

    static RuntimeTypeAdapterFactory<PluginLogs> pluginLogTypes =
            RuntimeTypeAdapterFactory.of(PluginLogs.class, "type")
                    .registerSubtype(ContractChange.class, "ContractChange")
                    .registerSubtype(ContractPayment.class, "ContractPayment");

    public static <T> void writeSave(String name, T value){
        /*
        * Save object inside a json file
        * */

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Company.class, new CompanySerializer());
        builder.registerTypeAdapter(Country.class, new CountrySerializer());
        builder.registerTypeAdapter(Location.class, new LocationSerializer());
        builder.registerTypeAdapter(Election.class, new ElectionSerializer());
        builder.registerTypeAdapter(Auction.class, new AuctionSerializer());
        builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
        builder.registerTypeAdapterFactory(pluginLogTypes);
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
        builder.registerTypeAdapter(Auction.class, new AuctionSerializer());
        builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
        builder.registerTypeAdapterFactory(pluginLogTypes);
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

    public static Field[] getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) { // Traverse up the hierarchy
            for (Field field : clazz.getDeclaredFields()) {
                fields.add(field);
            }
            clazz = clazz.getSuperclass(); // Move to the superclass
        }
        return fields.toArray(new Field[0]);
    }

    public static Material getCompanyTypeMaterial(String type){
        Map<String, Material> materialMap = new HashMap<>();
        materialMap.put("bank", Material.GOLD_INGOT);
        materialMap.put("transport", Material.MINECART);
        materialMap.put("product", Material.IRON_CHESTPLATE);
        materialMap.put("real estate", Material.QUARTZ_BLOCK);
        materialMap.put("military", Material.BOW);
        materialMap.put("building", Material.BRICKS);
        materialMap.put("other", Material.SUNFLOWER);

        return materialMap.get(type);
    }
}
