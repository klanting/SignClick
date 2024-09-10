package com.klanting.signclick.Economy.CompanyPatent;

import com.klanting.signclick.Calculate.WeeklyAuction;
import com.klanting.signclick.SignClick;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class Auction {
    public static ArrayList<PatentUpgrade> to_buy = new ArrayList<>();

    public static Map<Integer, Integer> bits = new HashMap<Integer, Integer>();
    public static Map<Integer, String> bits_owner = new HashMap<Integer, String>();

    public static int getBit(int index){
        return bits.getOrDefault(index, 0);
    }

    public static void setBit(int index, int value, String comp_name){
        bits.put(index, value);
        bits_owner.put(index, comp_name);
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

    public static void init(){
        to_buy.clear();
        for(int i=0; i<5; i++){
            PatentUpgrade p = getRandom();
            to_buy.add(p);

            bits.put(i, 100000*p.level);
            bits_owner.put(i, null);

        }
    }

    public static void Save(){

        for (int i=0; i<to_buy.size(); i++){
            PatentUpgrade up = to_buy.get(i);
            up.saveAuction(i);
        }

        String path = "Auction.bit.";
        String path2 = "Auction.bit_owner.";
        for (int i=0; i<to_buy.size(); i++){
            PatentUpgrade up = to_buy.get(i);
            up.saveAuction(i);
            SignClick.getPlugin().getConfig().set(path+i, bits.get(i));
            String name = bits_owner.get(i);
            if (name == null){
                name = "null";
            }
            SignClick.getPlugin().getConfig().set(path2+i, name);
        }

        //do later
        SignClick.getPlugin().getConfig().set("Auction.to_wait", WeeklyAuction.time_end-(System.currentTimeMillis()/1000));
    }

    public static void Restore(){

        if (SignClick.getPlugin().getConfig().contains("Auction.to_wait")){
            int v = (int) SignClick.getPlugin().getConfig().get("Auction.to_wait");
            WeeklyAuction.start_time = v;
        }

        String path = "Auction.patent_up";
        if (SignClick.getPlugin().getConfig().contains(path)){
            Integer counter = 0;
            while(SignClick.getPlugin().getConfig().contains(path+"."+counter)){
                int id = (Integer) SignClick.getPlugin().getConfig().get(path+"."+counter+".id");
                int level = (Integer) SignClick.getPlugin().getConfig().get(path+"."+counter+".level");
                String name = (String) SignClick.getPlugin().getConfig().get(path+"."+counter+".name");

                if (id == 4){
                    Material texture_item = Material.valueOf((String) SignClick.getPlugin().getConfig().get(path+"."+counter+".applied_item"));
                    PatentUpgrade up = new PatentUpgradeCustom(name,texture_item);
                    up.level = level;
                    Auction.to_buy.add(up);
                }else if (id == 0){
                    PatentUpgrade up = new PatentUpgradeJumper();
                    up.level = level;
                    Auction.to_buy.add(up);
                }else if (id == 1){
                    PatentUpgrade up = new PatentUpgradeEvade();
                    up.level = level;
                    Auction.to_buy.add(up);
                }else if (id == 2){
                    PatentUpgrade up = new PatentUpgradeRefill();
                    up.level = level;
                    Auction.to_buy.add(up);
                }else if (id == 3){
                    PatentUpgrade up = new PatentUpgradeCunning();
                    up.level = level;
                    Auction.to_buy.add(up);
                }

                counter++;
            }
        }

        if (SignClick.getPlugin().getConfig().contains("Auction.bit") && SignClick.getPlugin().getConfig().get("Auction.bit") != null) {
            SignClick.getPlugin().getConfig().getConfigurationSection("Auction.bit").getKeys(false).forEach(index -> {
                int bet = (int) SignClick.getPlugin().getConfig().get("Auction.bit."+index);
                bits.put(Integer.valueOf(index), bet);
            });
        }

        if (SignClick.getPlugin().getConfig().contains("Auction.bit_owner") && SignClick.getPlugin().getConfig().get("Auction.bit_owner") != null) {
            SignClick.getPlugin().getConfig().getConfigurationSection("Auction.bit_owner").getKeys(false).forEach(index -> {
                String bet_owner = (String) SignClick.getPlugin().getConfig().get("Auction.bit_owner."+index);
                if (Objects.equals(bet_owner, "null")){
                    bet_owner = null;
                }
                bits_owner.put(Integer.valueOf(index), bet_owner);
            });
        }


    }
}
