package com.klanting.signclick.economy;

import com.google.common.reflect.TypeToken;
import com.klanting.signclick.economy.decisions.*;
import com.klanting.signclick.economy.parties.Election;
import com.klanting.signclick.economy.parties.Party;
import com.klanting.signclick.economy.policies.*;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

import static com.klanting.signclick.economy.parties.ElectionTools.setupElectionDeadline;
import static org.bukkit.Bukkit.getServer;

public class CountryManager {
    private static Map<String, Country> countries = new HashMap<String, Country>();

    private static Map<UUID, Country> playerToCountryMap = new HashMap<UUID, Country>();

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
        if (!player.hasPermission("signclick.staff")){
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


        //countries.values().forEach(Country::save);

        /*
        * Save mapping from user to company
        * TODO remove this and load from countries restore
        * */

        Utils.writeSave("playerToCountryMap", playerToCountryMap);

        /*
        * save results
        * */

        getServer().getConsoleSender().sendMessage(Color.GREEN + "SignClick save banking completed!");
    }

    public static void restoreData(){

        countries = Utils.readSave("countries", new TypeToken<HashMap<String, Country>>(){}.getType(), new HashMap<>());

        playerToCountryMap = Utils.readSave("playerToCountryMap", new TypeToken<Map<UUID, Country>>(){}.getType(), new HashMap<>());


        if (!SignClick.getPlugin().getConfig().contains("bank") || true){
            return;
        }

        ConfigurationSection balanceSection = SignClick.getPlugin().getConfig().getConfigurationSection("bank");
        ConfigurationSection ownersSection = SignClick.getPlugin().getConfig().getConfigurationSection("owners");
        ConfigurationSection membersSection = SignClick.getPlugin().getConfig().getConfigurationSection("members");
        ConfigurationSection taxRateSection = SignClick.getPlugin().getConfig().getConfigurationSection("pct");
        ConfigurationSection colorSection = SignClick.getPlugin().getConfig().getConfigurationSection("color");
        ConfigurationSection spawnSection = SignClick.getPlugin().getConfig().getConfigurationSection("spawn");
        ConfigurationSection policiesSection = SignClick.getPlugin().getConfig().getConfigurationSection("policies");
        ConfigurationSection partiesSection = SignClick.getPlugin().getConfig().getConfigurationSection("parties");
        ConfigurationSection lawEnforcementSection = SignClick.getPlugin().getConfig().getConfigurationSection("law_enforcement");
        ConfigurationSection stabilitySection = SignClick.getPlugin().getConfig().getConfigurationSection("stability_map");
        ConfigurationSection forbidPartySection = SignClick.getPlugin().getConfig().getConfigurationSection("forbid_party");
        ConfigurationSection aboardMilitarySection = SignClick.getPlugin().getConfig().getConfigurationSection("aboard_military");
        ConfigurationSection decisionsSection = SignClick.getPlugin().getConfig().getConfigurationSection("decision");
        ConfigurationSection electionSection = SignClick.getPlugin().getConfig().getConfigurationSection("election");



        balanceSection.getKeys(true).forEach(name ->{

            List<UUID> ownerList = Utils.toUUIDList((List<String>) ownersSection.get(name));
            List<UUID> memberList = Utils.toUUIDList((List<String>) membersSection.get(name));
            List<UUID> lawEnforcementList = Utils.toUUIDList((List<String>) lawEnforcementSection.get(name));

            /*
            * Load policies
            * */
            List<Policy> policyList = new ArrayList<>();
            for (int i=0; i<5; i++){
                int level = (int) policiesSection.getConfigurationSection(name).get(String.valueOf(i));
                if (i == 0){
                    policyList.add(new PolicyEconomics(level));
                }
                if (i == 1){
                    policyList.add(new PolicyMarket(level));
                }
                if (i == 2){
                    policyList.add(new PolicyMilitary(level));
                }
                if (i == 3){
                    policyList.add(new PolicyTourist(level));
                }
                if (i == 4){
                    policyList.add(new PolicyTaxation(level));
                }
            }

            /*
            * Load parties
            * */
            List<Party> partiesList = new ArrayList<>();
            ConfigurationSection countryPartiesSection = partiesSection.getConfigurationSection(name);
            countryPartiesSection.getKeys(false).forEach(party -> {
                double pct = (double) countryPartiesSection.get(party+".PCT");
                List<UUID> partyOwnerList = Utils.toUUIDList((List<String>) countryPartiesSection.get(party+".owners"));
                List<UUID> partyMemberList = Utils.toUUIDList((List<String>) countryPartiesSection.get(party+".members"));

                Party p = new Party(party, name, pct, partyOwnerList, partyMemberList);
                partiesList.add(p);

            });

            /*
            * Load decisions
            * */
            ConfigurationSection countryDecisionsSection = decisionsSection.getConfigurationSection(name);
            List<Decision> decisionList = new ArrayList<>();
            countryDecisionsSection.getKeys(false).forEach(index -> {
                String decisionName = (String) countryDecisionsSection.get(index+".name");
                double needed = (double) countryDecisionsSection.get(index+".needed");
                int id = (int) countryDecisionsSection.get(index+".id");

                List<String> approvedIndex = (List<String>) countryDecisionsSection.get(index+".approved_index");
                List<Party> approved = new ArrayList<>();
                for (String a: approvedIndex){
                    approved.add(partiesList.get(Integer.valueOf(a)));
                }

                List<String> disapproved_index = (List<String>) countryDecisionsSection.get(index+".disapproved_index");
                List<Party> disapproved = new ArrayList<>();
                for (String d: disapproved_index){
                    disapproved.add(partiesList.get(Integer.valueOf(d)));
                }


                Decision d = null;
                if (id == 0){
                    int policy_id = (int) countryDecisionsSection.get(index+".policy_id");
                    int old_level = (int) countryDecisionsSection.get(index+".old_level");
                    int level = (int) countryDecisionsSection.get(index+".level");

                    d = new DecisionPolicy(name, needed, name, policy_id, old_level, level);
                    decisionList.add(d);
                }else if (id == 1){
                    int p = (int) countryDecisionsSection.get(index+".p");
                    d = new DecisionBanParty(name, needed, name, partiesList.get(p));
                    decisionList.add(d);
                }else if (id == 2){
                    boolean b = (boolean) countryDecisionsSection.get(index+".b");
                    d = new DecisionForbidParty(name, needed, name, b);
                    decisionList.add(d);
                }else if (id == 3){
                    boolean b = (boolean) countryDecisionsSection.get(index+".b");
                    d = new DecisionAboardMilitary(name, needed, name, b);
                    decisionList.add(d);
                }else if (id == 4){
                    String party_name = (String) countryDecisionsSection.get(index+".party_name");
                    d = new DecisionCoup(name, needed, name, party_name);
                    decisionList.add(d);
                }

                d.approved = approved;
                d.disapproved = disapproved;
            });

            /*
            * Load Election
            * */
            Election election = null;

            Map<String, Integer> vote_dict = new HashMap<>();


            ChatColor memberColor = ChatColor.valueOf(colorSection.get(name).toString());

            Location spawnLocation = (Location) spawnSection.get(name);

            int balance = (int) balanceSection.get(name);
            double taxRate = Double.parseDouble(taxRateSection.get(name).toString());
            double stability = Double.parseDouble(stabilitySection.get(name).toString());
            boolean forbidParty = Boolean.parseBoolean(forbidPartySection.get(name).toString());
            boolean aboardMilitary = Boolean.parseBoolean(aboardMilitarySection.get(name).toString());


            final Country country = new Country(name, balance,
                    ownerList, memberList, lawEnforcementList,
                    policyList, partiesList, decisionList, election,
                    memberColor, spawnLocation, taxRate, stability,
                    forbidParty, aboardMilitary
                    );

            countries.put(name, country);

            if (electionSection.contains(name)){
                ConfigurationSection countryElection = electionSection.getConfigurationSection(name);
                ConfigurationSection voteDict = countryElection.getConfigurationSection("vote_dict");

                voteDict.getKeys(false).forEach(voteKey -> {
                    vote_dict.put(voteKey, (int) voteDict.get(voteKey));
                });

                List<UUID> already_voted = new ArrayList<UUID>();
                for (String s: (List<String>) countryElection.get("voted")){
                    already_voted.add(UUID.fromString(s));
                }

                long time = (int) countryElection.get("to_wait");
                election = new Election(name, time+(System.currentTimeMillis()/1000));
                election.voteDict = vote_dict;
                election.alreadyVoted = already_voted;
                country.setCountryElection(election);
            }

            if (electionSection.contains(name)){
                /*
                 * Load election deadline
                 * */
                ConfigurationSection countryElection = electionSection.getConfigurationSection(name);
                long time = (int) countryElection.get("to_wait");

                setupElectionDeadline(country, time*20L);
            }

            countries.put(name, country);

        });


    }

    public static List<Country> getCountries(){
        return (List<Country>) countries.values();
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

                country.withdraw((int) country.getPolicyBonus(2, 0)+(int) base);
                SignClick.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(uuid), (int) country.getPolicyBonus(2, 0)+(int) base);
            }
        }
    }

    public static void runStability(){
        //no election
        for (Country country: countries.values()){

            double base = 1.0;
            base -= country.getPolicyBonus(2, 3);
            country.addStability(-base);

            if (country.isForbidParty()){
                country.addStability(-3.0);
            }

        }
    }
}
