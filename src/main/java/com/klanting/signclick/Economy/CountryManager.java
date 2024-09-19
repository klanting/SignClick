package com.klanting.signclick.Economy;

import com.klanting.signclick.Economy.Policies.*;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

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
}
