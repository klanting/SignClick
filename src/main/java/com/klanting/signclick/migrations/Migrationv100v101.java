package com.klanting.signclick.migrations;

import com.google.gson.reflect.TypeToken;
import com.klanting.signclick.SignClick;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.gson.*;
import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.companyPatent.Auction;
import com.klanting.signclick.economy.contracts.ContractCTC;
import com.klanting.signclick.economy.logs.*;
import com.klanting.signclick.economy.parties.Election;
import com.klanting.signclick.utils.Serializers.*;
import com.klanting.signclick.utils.Utils;
import org.bukkit.Location;

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

        SignClick.getPlugin().getConfig().set("version", "1.0.1");

        SignClick.getPlugin().getConfig().options().copyDefaults(true);
        SignClick.getPlugin().saveConfig();
    }
}
