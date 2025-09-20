package com.klanting.signclick.migrations;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.klanting.signclick.SignClick;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Migrationv204v205 extends Migration{
    Migrationv204v205(){
        super("2.0.4", "2.0.5");
    }

    @Override
    public void migrate() {

        File file = new File(SignClick.getPlugin().getDataFolder()+"/companies.json");
        if (!file.exists()){
            return;
        }

        try {

            Reader reader = new FileReader(file);
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            for (String companyName: jsonObject.keySet()){
                JsonObject companyObject = jsonObject.get(companyName).getAsJsonObject();

                /*
                 * change company types
                 * */
                Map<String, String> typeMapping = new HashMap<>();
                typeMapping.put("Building", "Nature");
                typeMapping.put("Decoration", "Miscellaneous");
                typeMapping.put("Farmer", "Farming");


                String s = companyObject.get("type").getAsString();
                companyObject.add("type",
                        JsonParser.parseString(typeMapping.getOrDefault(s, s)));

                jsonObject.add(companyName, companyObject);
            }


            Writer writer = new FileWriter(file, false);
            writer.write(jsonObject.toString());

            writer.flush();
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        SignClick.getConfigManager().getConfig("general.yml").set("version", "2.0.5",
                "Latest updated version, don't change this, it will be done automatically");

        SignClick.getConfigManager().getConfig("general.yml").options().copyDefaults(true);
        SignClick.getConfigManager().save();
    }
}
