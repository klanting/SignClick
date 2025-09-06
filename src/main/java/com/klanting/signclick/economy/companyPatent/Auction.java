package com.klanting.signclick.economy.companyPatent;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.utils.Utils;
import org.bukkit.Bukkit;
import versionCompatibility.CompatibleLayer;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class Auction {

    private static Auction instance = null;

    public static Auction getInstance(){
        if (instance == null){
            instance = new Auction();
        }

        return instance;
    }

    public static void clear(){
        instance = null;
    }

    public final ArrayList<PatentUpgrade> toBuy;

    private final Map<Integer, Integer> bits;
    public final Map<Integer, String> bitsOwner;

    private final long auctionCycle = SignClick.getConfigManager().getConfig("companies.yml").getLong("auctionCycle");

    public int getBit(int index){
        return bits.getOrDefault(index, 0);
    }

    public void setBit(int index, int value, String comp_name){
        bits.put(index, value);
        bitsOwner.put(index, comp_name);
    }

    public Auction(){
        /*
        * Only allow this outside the getInstance for test purposes
        * */
        bits = new HashMap<>();
        toBuy = new ArrayList<>();
        bitsOwner = new HashMap<>();

        init();
    }

    public Auction(JsonObject jsonObject, JsonDeserializationContext context){
        toBuy = context.deserialize(jsonObject.get("toBuy"), new TypeToken<ArrayList<PatentUpgrade>>(){}.getType());
        bits = context.deserialize(jsonObject.get("bits"), new TypeToken<Map<Integer, Integer>>(){}.getType());
        bitsOwner = context.deserialize(jsonObject.get("bitsOwner"), new TypeToken<Map<Integer, String>>(){}.getType());

        start_time = jsonObject.get("waitTime").getAsLong() % (auctionCycle*20L);
    }

    public JsonObject toJson(JsonSerializationContext context){
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("toBuy", context.serialize(toBuy, new TypeToken<ArrayList<PatentUpgrade>>(){}.getType()));
        jsonObject.add("bits", context.serialize(bits));
        jsonObject.add("bitsOwner", context.serialize(bitsOwner));
        jsonObject.add("waitTime", context.serialize(time_end- CompatibleLayer.getCurrentTick()));


        return jsonObject;
    }


    public static PatentUpgrade getRandom(){

        Random rand = new Random();
        int id = rand.nextInt(4);
        PatentUpgrade up;
        if (id == 0) {
            up = new PatentUpgradeJumper();
        }else if (id == 1){
            up = new PatentUpgradeEvade();
        }else if (id == 2){
            up = new PatentUpgradeRefill();
        }else if (id == 3){
            up = new PatentUpgradeCunning();
        }else{
            up = new PatentUpgradeJumper();
        }

        int l1 = rand.nextInt(6)+1;
        int l2 = rand.nextInt(6)+1;

        int level = Math.min(l1, l2);

        up.level = level;
        return up;
    }

    private void init(){
        toBuy.clear();

        int defaultPrice = SignClick.getConfigManager().getConfig("companies.yml").getInt("auctionStartPrice");

        for(int i=0; i<5; i++){

            PatentUpgrade p = getRandom();
            toBuy.add(p);
            bits.put(i, defaultPrice*p.level);
            bitsOwner.put(i, null);

        }
    }

    public static void Save(){
        Utils.writeSave("auction", instance);
    }

    public static void Restore(){
        instance = Utils.readSave("auction", new TypeToken<Auction>(){}.getType(), new Auction());
    }

    public long start_time = 0;
    public long time_end;
    public void check(){
        time_end = CompatibleLayer.getCurrentTick()+auctionCycle*20L;

        Bukkit.getServer().getScheduler().runTaskTimer(SignClick.getPlugin(), new Runnable() {

            public void run() {
                for (int i=0; i<5; i++){
                    if (bitsOwner.get(i) == null){
                        continue;
                    }

                    CompanyI comp = Market.getCompany(bitsOwner.get(i));
                    comp.getPatentUpgrades().add(toBuy.get(i));
                }
                init();
                time_end = CompatibleLayer.getCurrentTick()+auctionCycle*20L;

            }
        },start_time,auctionCycle*20L);

    }

    public long getWaitTime(){
        return time_end - CompatibleLayer.getCurrentTick();
    }
}
