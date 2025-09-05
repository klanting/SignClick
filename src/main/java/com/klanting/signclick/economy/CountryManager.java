package com.klanting.signclick.economy;

import com.google.common.reflect.TypeToken;
import com.klanting.signclick.economy.decisions.*;
import com.klanting.signclick.economy.parties.Election;
import com.klanting.signclick.economy.parties.Party;
import com.klanting.signclick.economy.policies.*;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.utils.Serializers.CountrySerializer;
import com.klanting.signclick.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

import static com.klanting.signclick.economy.parties.ElectionTools.setupElectionDeadline;
import static org.bukkit.Bukkit.getServer;

public class CountryManager {
    private static Map<String, Country> countries = new HashMap<>();

    public static void addPlayerToCountryMap(UUID uuid, Country country) {
        CountryManager.playerToCountryMap.put(uuid, country);
    }

    private static final Map<UUID, Country> playerToCountryMap = new HashMap<>();

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
        return create(countryName, player, player);
    }

    public static Country create(String countryName, Player player, OfflinePlayer targetPlayer){
        if (!player.hasPermission("signclick.staff") && !player.hasPermission("signclick.create")){
            player.sendMessage("§bplayer does not have permission to create a country");
            return null;
        }

        if (countries.containsKey(countryName)){
            return null;
        }

        Country newCountry = new Country(countryName, targetPlayer);
        countries.put(countryName, newCountry);
        playerToCountryMap.put(targetPlayer.getUniqueId(), newCountry);


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
        Utils.writeSave("countries", countries);

        /*
        * save results
        * */
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "SignClick: save Countries completed!");
    }

    public static void restoreData(){

        countries = Utils.readSave("countries", new TypeToken<HashMap<String, Country>>(){}.getType(), new HashMap<>());

    }

    public static List<Country> getCountries(){
        return countries.values().stream().toList();
    }

    public static List<String> getCountriesString(){
        List<String> countriesString = new ArrayList<>();
        for (Country c: countries.values()){
            countriesString.add(c.getName());
        }
        return countriesString;
    }

    public static void runLawSalary(){
        for (Country country: countries.values()){
            for (UUID uuid: country.getLawEnforcement()){
                double base = 0;

                if (country.getStability() < 50){
                    base += 2000.0;
                }

                if (country.getStability() < 30){
                    base += 3000.0;
                }

                country.withdraw((int) country.getPolicyBonus("lawEnforcementSalary")+(int) base);
                SignClick.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(uuid), (int) country.getPolicyBonus("lawEnforcementSalary")+(int) base);
            }
        }
    }

    public static void runStability(){
        //no election
        for (Country country: countries.values()){

            double base = 1.0;
            base -= country.getPolicyBonus("electionPenaltyReduction");
            country.addStability(-base);

            if (country.isForbidParty()){
                country.addStability(-3.0);
            }

        }
    }
}
