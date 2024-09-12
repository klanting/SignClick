package com.klanting.signclick.Economy;

import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
        return playerToCountryMap.getOrDefault(offlinePlayer.getUniqueId(), null);
    }
}
