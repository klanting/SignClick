package com.klanting.signclick.migrations;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.logs.*;
import com.klanting.signclick.utils.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Migrationv200v201 extends Migration{
    Migrationv200v201(){
        super("2.0.0", "2.0.1");
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
                pluginLogs.add(new ResearchUpdate());

                companyObject.add("logObservers", JsonParser.parseString(
                        Utils.serialize(pluginLogs, new com.google.common.reflect.TypeToken<List<PluginLogs>>(){}.getType())
                ));
            }

            Writer writer = new FileWriter(file, false);
            writer.write(jsonObject.toString());

            writer.flush();
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        SignClick.getConfigManager().getConfig("general.yml").set("version", "2.0.1",
                "Latest updated version, don't change this, it will be done automatically");

        SignClick.getConfigManager().getConfig("general.yml").options().copyDefaults(true);
        SignClick.getConfigManager().save();
    }
}
