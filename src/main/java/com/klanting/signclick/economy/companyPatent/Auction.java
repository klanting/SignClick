package com.klanting.signclick.economy.companyPatent;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.utils.Utils;
import org.bukkit.Bukkit;

import java.util.*;

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

        start_time = jsonObject.get("waitTime").getAsLong();
    }

    public JsonObject toJson(JsonSerializationContext context){
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("toBuy", context.serialize(toBuy));
        jsonObject.add("bits", context.serialize(bits));
        jsonObject.add("bitsOwner", context.serialize(bitsOwner));
        jsonObject.add("waitTime", context.serialize(time_end-(System.currentTimeMillis()/1000)));

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

        int defaultPrice = SignClick.getPlugin().getConfig().getInt("auctionStartPrice");

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
        time_end = start_time+(System.currentTimeMillis()/1000)+(60*60*24*7);
        Bukkit.getServer().getScheduler().runTaskTimer(SignClick.getPlugin(), new Runnable() {

            public void run() {
                for (int i=0; i<5; i++){
                    if (bitsOwner.get(i) == null){
                        continue;
                    }

                    Company comp = Market.getCompany(bitsOwner.get(i));
                    comp.patentUpgrades.add(toBuy.get(i));
                }

                init();
                time_end = (System.currentTimeMillis()/1000)+(60*60*24*7);

            }
        },start_time,60*60*24*7*20);

    }
}
