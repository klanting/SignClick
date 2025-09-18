package com.klanting.signclick.migrations;

import com.klanting.signclick.SignClick;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;
import com.klanting.signclick.logicLayer.companyLogic.logs.*;
import com.klanting.signclick.utils.Utils;

public class Migrationv100v101 extends Migration{

    Migrationv100v101(){
        super("1.0.0", "1.0.1");
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

                List<PluginLogs> pluginLogs = new ArrayList<>();
                pluginLogs.add(new ContractChange());
                pluginLogs.add(new ContractPayment());
                pluginLogs.add(new MoneyTransfer());
                pluginLogs.add(new ShareholderChange());

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

        SignClick.getConfigManager().getConfig("general.yml").set("version", "1.0.1");

        SignClick.getConfigManager().getConfig("general.yml").options().copyDefaults(true);
        SignClick.getConfigManager().save();
    }
}
