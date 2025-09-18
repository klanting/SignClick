package com.klanting.signclick.migrations;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.*;
import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.logicLayer.companyLogic.CompanyOwnerManager;
import com.klanting.signclick.logicLayer.companyLogic.upgrades.*;
import com.klanting.signclick.migrations.mocks.CompanyIv102v200;
import com.klanting.signclick.utils.Utils;

import java.io.*;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Migrationv102v200beta extends Migration{
    Migrationv102v200beta(){
        super("1.0.2", "2.0.0-beta");
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
                        Utils.serialize(new Research(new CompanyIv102v200("Decoration")),
                                new com.google.common.reflect.TypeToken<Research>(){}.getType()))

                );

                /*
                * Change updates
                * */
                JsonArray jsa = companyObject.getAsJsonArray("upgrades");
                JsonObject patentSlot = jsa.get(1).getAsJsonObject();
                patentSlot.add("classType", JsonParser.parseString("patentSlot"));
                JsonObject patentUpgradeSlot = jsa.get(2).getAsJsonObject();
                patentUpgradeSlot.add("classType", JsonParser.parseString("patentUpgradeSlot"));
                JsonObject investTime = jsa.get(4).getAsJsonObject();
                investTime.add("classType", JsonParser.parseString("investReturnTime"));

                /*
                 * Add type of reference
                 * */
                companyObject.add("classType", JsonParser.parseString("company"));
                companyObject.add("upgrades", new JsonArray());
                companyObject.add("type", JsonParser.parseString("Decoration"));

                JsonArray newJsa = new JsonArray();
                newJsa.add(patentSlot);
                newJsa.add(patentUpgradeSlot);
                newJsa.add(JsonParser.parseString(
                        Utils.serialize(new UpgradeProductSlot(0), new TypeToken<Upgrade>(){}.getType())
                ));
                newJsa.add(JsonParser.parseString(
                        Utils.serialize(new UpgradeBoardSize(0,
                                        Utils.deserialize(companyObject.deepCopy(), new TypeToken<CompanyI>(){}.getType(), null)),
                                new TypeToken<Upgrade>(){}.getType())
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
                * Update patent upgrades
                * */
                JsonArray jsapu = companyObject.getAsJsonArray("patentUpgrades");

                HashMap<String, String> nameToClassType = new HashMap<>();
                nameToClassType.put("§6Cunning", "cunning");
                nameToClassType.put("§6Evade", "evade");
                nameToClassType.put("§6Jumper", "jumper");
                nameToClassType.put("§6Refill", "refill");

                for (int i=0; i<jsapu.size(); i++){
                    JsonObject jo = jsapu.get(i).getAsJsonObject();
                    String name = jo.get("name").getAsString();
                    jo.add("classType", JsonParser.parseString(nameToClassType.get(name)));
                    jsapu.set(i, jo);
                }
                companyObject.add("patentUpgrades", jsapu);
            }


            Writer writer = new FileWriter(file, false);
            writer.write(jsonObject.toString());
            writer.flush();
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        try {

            Reader reader = new FileReader(SignClick.getPlugin().getDataFolder()+"/auction.json");
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

            JsonArray toBuy = jsonObject.getAsJsonArray("toBuy");

            for (int i=0; i<toBuy.size(); i++){
                JsonObject jo = toBuy.get(i).getAsJsonObject();
                String name = jo.get("name").getAsString();
                jo.add("classType", JsonParser.parseString(name.substring(name.indexOf("6")+1).toLowerCase()));
                toBuy.set(i, jo);
            }
            jsonObject.add("patentUpgrades", toBuy);

            Writer writer = new FileWriter(SignClick.getPlugin().getDataFolder()+"/auction.json", false);

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
                        jsObj.getAsJsonObject().add("classType", JsonParser.parseString("companyRef"));

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


        SignClick.getConfigManager().getConfig("general.yml").set("version", "2.0.0-beta");

        SignClick.getConfigManager().getConfig("general.yml").options().copyDefaults(true);
        SignClick.getConfigManager().save();
    }
}
