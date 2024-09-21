package com.klanting.signclick.Economy;

import com.klanting.signclick.Economy.Decisions.*;
import com.klanting.signclick.Economy.Parties.Election;
import com.klanting.signclick.Economy.Parties.Party;
import com.klanting.signclick.Economy.Policies.*;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.commands.BankCommands;
import com.klanting.signclick.utils.Utils;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.C;

import java.util.*;
import java.util.function.Consumer;

import static com.klanting.signclick.commands.BankCommands.countryElections;
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
        ConfigurationSection taxRateSection = SignClick.getPlugin().getConfig().getConfigurationSection("pct");
        ConfigurationSection colorSection = SignClick.getPlugin().getConfig().getConfigurationSection("color");
        ConfigurationSection spawnSection = SignClick.getPlugin().getConfig().getConfigurationSection("spawn");
        ConfigurationSection policiesSection = SignClick.getPlugin().getConfig().getConfigurationSection("policies");
        ConfigurationSection partiesSection = SignClick.getPlugin().getConfig().getConfigurationSection("parties");
        ConfigurationSection lawEnforcementSection = SignClick.getPlugin().getConfig().getConfigurationSection("law_enforcement");
        ConfigurationSection stabilitySection = SignClick.getPlugin().getConfig().getConfigurationSection("stability_map");
        ConfigurationSection forbidPartySection = SignClick.getPlugin().getConfig().getConfigurationSection("forbid_party");
        ConfigurationSection aboardMilitarySection = SignClick.getPlugin().getConfig().getConfigurationSection("aboard_military");
        ConfigurationSection decisionsSection = SignClick.getPlugin().getConfig().getConfigurationSection("aboard_military");
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
                election.vote_dict = vote_dict;
                election.alreadyVoted = already_voted;

                //TODO activate election deadline date here
            }


            Color memberColor = (Color) colorSection.get(name);
            Location spawnLocation = (Location) spawnSection.get(name);
            int balance = (int) balanceSection.get(name);
            double taxRate = (double) taxRateSection.get(name);
            double stability = (double) stabilitySection.get(name);
            boolean forbidParty = (boolean) forbidPartySection.get(name);
            boolean aboardMilitary = (boolean) aboardMilitarySection.get(name);


            Country country = new Country(name, balance,
                    ownerList, memberList, lawEnforcementList,
                    policyList, partiesList, decisionList, election,
                    memberColor, spawnLocation, taxRate, stability,
                    forbidParty, aboardMilitary
                    );

            countries.put(name, country);

        });

        ConfigurationSection userCountrySection = SignClick.getPlugin().getConfig().getConfigurationSection("country");
        userCountrySection.getKeys(true).forEach(key ->{
            String countryName = String.valueOf(SignClick.getPlugin().getPlugin().getConfig().get("country." + key));

            playerToCountryMap.put(UUID.fromString(key), countries.get(countryName));
        });
    }
}
