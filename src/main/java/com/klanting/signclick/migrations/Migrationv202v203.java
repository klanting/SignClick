package com.klanting.signclick.migrations;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.companyLogic.logs.MachineProduction;
import com.klanting.signclick.logicLayer.companyLogic.logs.PluginLogs;
import com.klanting.signclick.logicLayer.companyLogic.logs.ShopLogs;
import com.klanting.signclick.utils.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Migrationv202v203 extends Migration{
    Migrationv202v203(){
        super("2.0.2", "2.0.3");
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
                 * Add log observer field
                 * */
                List<PluginLogs> pluginLogs = Utils.deserialize(companyObject.get("logObservers"),
                        new TypeToken<List<PluginLogs>>(){}.getType(),
                        new ArrayList<>());
                pluginLogs.add(new ShopLogs());
                pluginLogs.add(new MachineProduction());

                companyObject.add("logObservers", JsonParser.parseString(
                        Utils.serialize(pluginLogs, new com.google.common.reflect.TypeToken<List<PluginLogs>>(){}.getType())
                ));

                /*
                * change company types
                * */
                Map<String, String> typeMapping = new HashMap<>();
                typeMapping.put("transport", "Redstone");
                typeMapping.put("product", "Decoration");
                typeMapping.put("real estate", "Decoration");
                typeMapping.put("military", "Fighter");
                typeMapping.put("building", "Building");
                typeMapping.put("enchantment", "Enchantment");
                typeMapping.put("brewery", "Brewery");
                typeMapping.put("other", "Decoration");
                typeMapping.put("bank", "Mining");


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


        SignClick.getConfigManager().getConfig("general.yml").set("version", "2.0.3",
                "Latest updated version, don't change this, it will be done automatically");

        SignClick.getConfigManager().getConfig("general.yml").options().copyDefaults(true);
        SignClick.getConfigManager().save();
    }
}
