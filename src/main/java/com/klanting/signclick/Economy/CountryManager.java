package com.klanting.signclick.Economy;

import com.klanting.signclick.Economy.Policies.*;
import com.klanting.signclick.SignClick;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class CountryManager {
    private static final Map<String, Country> countries = new HashMap<String, Country>();

    private static final Map<UUID, Country> playerToCountryMap = new HashMap<UUID, Country>();

    public static Country getCountry(String countryName){
        /*
        * cet country by country name
        * */
        return countries.getOrDefault(countryName, null);
    }

    public static Country getCountry(OfflinePlayer offlinePlayer){
        /*
        * get country that the player is a part of
        * */
        return getCountry(offlinePlayer.getUniqueId());
    }

    public static Country getCountry(UUID uuid){
        /*
         * get country that the player UUID is a part of
         * */
        return playerToCountryMap.getOrDefault(uuid, null);
    }

    public static Country create(String countryName, Player player){
        if (!player.hasPermission("signclick.staff")){
            player.sendMessage("§bplayer does not have permission to create a country");
            return null;
        }

        if (countries.containsKey(countryName)){
            player.sendMessage("§bthis country already exists");
            return null;
        }

        Country newCountry = new Country(countryName, player);
        countries.put(countryName, newCountry);
        playerToCountryMap.put(player.getUniqueId(), newCountry);


        player.sendMessage("§bcountry has been succesfully created");
        return newCountry;
    }

    public static boolean delete(String countryName, Player player){
        if (!countries.containsKey(countryName)){
            player.sendMessage("this country does not exists");
            return false;
        }

        countries.remove(countryName);

        Map<UUID, Country> playerToCountryMapCopy = new HashMap<>(playerToCountryMap);

        playerToCountryMapCopy.keySet().forEach(key ->{
            Country playerCountry = playerToCountryMap.getOrDefault(key, null);
            if (playerCountry == null){
                return;
            }
            if (!playerCountry.getName().equals(countryName)){
                return;
            }

            playerToCountryMap.remove(key);
        });

        player.sendMessage("country removed");
        return true;

    }


    public static Integer countryCount(){
        return countries.size();
    }

    public static void clear(){
        countries.clear();
        playerToCountryMap.clear();
    }

    public static List<Country> getTop(){
        List<Country> start = new ArrayList<Country>(countries.values());
        List<Country> end = new ArrayList<Country>();
        for (Country s: start){
            int amount = s.getBalance();
            if (end.isEmpty()){
                end.add(s);
            }
            int index = end.size();
            for (Country old: end){
                int value = old.getBalance();
                if (value < amount){
                    index = end.indexOf(old);
                    break;
                }

            }
            if (!end.contains(s)){
                end.add(index ,s);
            }

        }
        return end;
    }

    public static void leaveCountry(OfflinePlayer offlinePlayer){
        leaveCountry(offlinePlayer.getUniqueId());
    }

    public static void leaveCountry(UUID uuid){
        playerToCountryMap.remove(uuid);
    }

    public static void joinCountry(Country country, UUID uuid){
        playerToCountryMap.put(uuid, country);
    }

    public static void saveData(){
        SignClick.getPlugin().getConfig().set("parties", null);
        SignClick.getPlugin().getConfig().set("elections", null);
        SignClick.getPlugin().getConfig().set("decision", null);

        countries.values().forEach(Country::save);

        /*
        * Save mapping from user to company
        * TODO remove this and load from countries restore
        * */
        for (Map.Entry<UUID, Country> entry : playerToCountryMap.entrySet()){
            SignClick.getPlugin().getConfig().set("country." + entry.getKey().toString(), entry.getValue().getName());
        }

        /*
        * save results
        * */
        SignClick.getPlugin().getConfig().options().copyDefaults(true);
        SignClick.getPlugin().saveConfig();

        getServer().getConsoleSender().sendMessage(Color.GREEN + "SignClick save banking completed!");
    }

    public static void restoreData(){
        if (!SignClick.getPlugin().getConfig().contains("bank")){
            return;
        }

        ConfigurationSection balanceSection = SignClick.getPlugin().getConfig().getConfigurationSection("bank");
        ConfigurationSection ownersSection = SignClick.getPlugin().getConfig().getConfigurationSection("owners");
        ConfigurationSection membersSection = SignClick.getPlugin().getConfig().getConfigurationSection("members");
        ConfigurationSection userCountrySection = SignClick.getPlugin().getConfig().getConfigurationSection("country");

        balanceSection.getKeys(true).forEach(key ->{


        });
    }
}
