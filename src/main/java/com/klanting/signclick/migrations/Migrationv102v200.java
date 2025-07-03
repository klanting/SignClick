package com.klanting.signclick.migrations;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.Board;
import com.klanting.signclick.economy.CompanyOwnerManager;
import com.klanting.signclick.economy.Research;
import com.klanting.signclick.economy.companyUpgrades.*;
import com.klanting.signclick.economy.logs.*;
import com.klanting.signclick.utils.Utils;

import java.io.*;

import java.util.List;
import java.util.UUID;

public class Migrationv102v200 extends Migration{
    Migrationv102v200(){
        super("1.0.2", "2.0.0");
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
                 * Add board
                 * */

                String owner;

                JsonArray owners = companyObject.getAsJsonObject("companyOwnerManager").getAsJsonArray("owners");
                if (owners.isEmpty()){
                    owner = companyObject.getAsJsonObject("companyOwnerManager").getAsJsonObject("shareHolders").keySet().iterator().next();
                }else{
                    owner = companyObject.getAsJsonObject("companyOwnerManager").getAsJsonArray("owners").get(0).getAsString();
                }

                UUID ownerUUID = UUID.fromString(owner);
                companyObject.getAsJsonObject("companyOwnerManager").add("board", JsonParser.parseString(
                        Utils.serialize(new Board(new CompanyOwnerManager(ownerUUID)),
                                new com.google.common.reflect.TypeToken<Board>(){}.getType())
                ));

                companyObject.add("$assertionsDisabled", JsonParser.parseString("true"));

                /*
                * add research
                * */
                companyObject.add("research", JsonParser.parseString(
                        Utils.serialize(new Research(companyObject.get("type").getAsString()),
                                new com.google.common.reflect.TypeToken<Research>(){}.getType()))

                );

                /*
                * Change updates
                * */
                JsonArray jsa = companyObject.getAsJsonArray("upgrades");
                JsonElement patentSlot = jsa.get(1);
                JsonElement patentUpgradeSlot = jsa.get(2);
                JsonElement investTime = jsa.get(4);

                JsonArray newJsa = new JsonArray();
                newJsa.add(patentSlot);
                newJsa.add(patentUpgradeSlot);
                newJsa.add(JsonParser.parseString(
                        Utils.serialize(new UpgradeProductSlot(0), new TypeToken<Upgrade>(){}.getType())
                ));
                newJsa.add(JsonParser.parseString(
                        Utils.serialize(new UpgradeBoardSize(0), new TypeToken<Upgrade>(){}.getType())
                ));
                newJsa.add(investTime);
                newJsa.add(JsonParser.parseString(
                        Utils.serialize(new UpgradeResearchModifier(0), new TypeToken<Upgrade>(){}.getType())
                ));
                newJsa.add(JsonParser.parseString(
                        Utils.serialize(new UpgradeProductModifier(0), new TypeToken<Upgrade>(){}.getType())
                ));
                companyObject.add("upgrades", newJsa);

                /*
                * Add type of reference
                * */
                companyObject.add("type", JsonParser.parseString("company"));
            }

            Writer writer = new FileWriter(file, false);
            writer.write(jsonObject.toString());
            writer.flush();
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        /*
        * Update contract copy of company to reference
        * */
        for (String contractPath: List.of("/contractServerToComp.json",
                "/contractCompToComp.json", "/contractCompToPlayer.json",
                "/contractPlayerToComp.json")){
            try {
                File file2 = new File(SignClick.getPlugin().getDataFolder()+contractPath);
                Reader reader2 = new FileReader(file2);
                JsonArray jsonArray = JsonParser.parseReader(reader2).getAsJsonArray();

                int counter = 0;
                for (JsonElement m: jsonArray){
                    for (String location: List.of("to", "from")){
                        JsonObject jsObj = m.getAsJsonObject().getAsJsonObject(location);
                        if (jsObj == null || !jsObj.has("stockName")){
                            continue;
                        }
                        String stockName = jsObj.get("stockName").getAsString();
                        jsObj = new JsonObject();
                        jsObj.getAsJsonObject().add("stockName", JsonParser.parseString(stockName));
                        jsObj.getAsJsonObject().add("type", JsonParser.parseString("companyRef"));

                        m.getAsJsonObject().add(location, jsObj);
                        jsonArray.set(counter, m);
                        counter ++;
                    }

                }

                Writer writer = new FileWriter(file2, false);
                writer.write(jsonArray.toString());
                writer.flush();
                writer.close();

            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }


        SignClick.getPlugin().getConfig().set("version", "2.0.0");

        SignClick.getPlugin().getConfig().options().copyDefaults(true);
        SignClick.getPlugin().saveConfig();
    }
}
