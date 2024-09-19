package com.klanting.signclick.Economy;

import com.klanting.signclick.Economy.Decisions.Decision;
import com.klanting.signclick.Economy.Parties.Election;
import com.klanting.signclick.Economy.Parties.Party;
import com.klanting.signclick.Economy.Policies.*;
import com.klanting.signclick.SignClick;
import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.Location;

import java.util.*;

import static com.klanting.signclick.commands.BankCommands.countryElections;

public class Country {

    private String name;

    private int balance;

    private List<UUID> owners = new ArrayList<>();
    private List<UUID> members = new ArrayList<>();

    private List<UUID> lawEnforcement = new ArrayList<>();

    private List<Policy> policies = new ArrayList<>();
    private List<Party> parties = new ArrayList<>();
    private List<Decision> decisions = new ArrayList<>();

    private Election countryElection;

    private Color memberColor;

    private Location spawnLocation;

    /*
    * value between 0 and 1
    * */
    private double taxRate;

    private double stability;

    private boolean forbidParty;
    private boolean aboardMilitary;

    public Country(String countryName, OfflinePlayer player){
        name = countryName;
        owners.add(player.getUniqueId());

        taxRate = 0;
        stability = 70.0;
        balance = 0;
        memberColor = Color.WHITE;
        spawnLocation = null;
        countryElection = null;

        forbidParty = false;
        aboardMilitary = false;

        policies = Arrays.asList(new PolicyEconomics(2), new PolicyMarket(2), new PolicyMilitary(2), new PolicyTourist(2), new PolicyTaxation(2));
    }


    public Country(String name, int balance,
                   List<UUID> owners, List<UUID> members,
                   List<UUID> lawEnforcement,
                   List<Policy> policies, List<Party> parties,
                   List<Decision> decisions, Election countryElection,
                   Color memberColor, Location spawnLocation,
                   double taxRate, double stability,
                   boolean forbidParty, boolean aboardMilitary){
        /*
         * File loading constructor
         * */
        this.name = name;
        this.balance = balance;
        this.owners = owners;
        this.members = members;
        this.lawEnforcement = lawEnforcement;
        this.policies = policies;
        this.parties = parties;
        this.decisions = decisions;
        this.countryElection = countryElection;
        this.memberColor = memberColor;
        this.spawnLocation = spawnLocation;
        this.taxRate = taxRate;
        this.stability = stability;
        this.forbidParty = forbidParty;
        this.aboardMilitary = aboardMilitary;

    }

    public String getName() {
        /*
        * getter to retrieve the name of a country
        * */
        return name;
    }

    public List<UUID> getLawEnforcement() {
        return lawEnforcement;
    }

    public boolean isOwner(OfflinePlayer offlinePlayer){
        return owners.contains(offlinePlayer.getUniqueId());
    }

    public List<UUID> getOwners(){
        return owners;
    }

    public List<UUID> getMembers(){
        return members;
    }

    public boolean has (int amount){
        return amount <= balance;
    }

    public int getBalance(){
        return balance;
    }

    public void deposit(int amount){
        int oldBalance = balance;
        balance += amount;
        changeCapital(oldBalance);
    }

    public boolean withdraw(int amount){

        if (amount > balance){
            return false;
        }

        int oldBalance = balance;
        balance -= amount;
        changeCapital(oldBalance);
        return true;
    }

    private void changeCapital(int oldCap){
        //TODO finish this function
    }

    public Color getColor(){
        return memberColor;
    }

    public void setColor(Color color){
        memberColor = color;
    }

    public Location getSpawn(){
        return spawnLocation;
    }

    public void setSpawn(Location location){
        spawnLocation = location;
    }

    public void removeOwner(OfflinePlayer offlinePlayer){
        UUID uuid = offlinePlayer.getUniqueId();
        if (!owners.contains(uuid)){
            return;
        }

        owners.remove(uuid);
        CountryManager.leaveCountry(uuid);
    }

    public void addMember(OfflinePlayer offlinePlayer){
        UUID uuid = offlinePlayer.getUniqueId();


        if (!members.contains(uuid) || !owners.contains(uuid)) {
            members.add(uuid);

            CountryManager.joinCountry(this, uuid);

            addStability(3.0*(1.0+ getPolicyBonus(2, 9)));
        }

    }

    public void removeMember(OfflinePlayer offlinePlayer){
        UUID uuid = offlinePlayer.getUniqueId();
        if (!members.contains(uuid)) {
            return;
        }

        members.remove(uuid);

        CountryManager.leaveCountry(offlinePlayer);
        addStability(-3.0*(1.0- getPolicyBonus(2, 10)));
    }

    public Double addStability(double change){
        stability += change;
        return stability;
    }

    public double getPolicyBonus(int id, int index){

        return policies.get(id).getBonus(index);
    }

    public void save(){
        /*
        * Save balance
        * */
        SignClick.getPlugin().getConfig().set("bank." + name, balance);

        /*
        * Save owner
        * */
        List<String> f_list = new ArrayList<>();
        for (UUID uuid: owners){
            f_list.add(uuid.toString());
        }

        SignClick.getPlugin().getConfig().set("owners." + name, f_list);

        /*
        * Save members
        * */
        f_list = new ArrayList<>();
        for (UUID uuid: members){
            f_list.add(uuid.toString());
        }

        SignClick.getPlugin().getConfig().set("owners." + name, f_list);

        SignClick.getPlugin().getConfig().set("pct." + name, taxRate);
        SignClick.getPlugin().getConfig().set("color." + name, memberColor.toString());
        SignClick.getPlugin().getConfig().set("spawn." + name, spawnLocation);

        /*
        * add law enforcement
        * */
        f_list = new ArrayList<>();
        for (UUID uuid: lawEnforcement){
            f_list.add(uuid.toString());
        }

        SignClick.getPlugin().getConfig().set("law_enforcement." + name, f_list);
        SignClick.getPlugin().getConfig().set("stability_map." + name, stability);

        for (Policy p: policies){
            p.Save(name);
        }

        for (Party p: parties){
            p.Save();
        }

        if (countryElection != null){
            countryElection.Save();
        }

        /*
        * save decisions
        * */
        int counter = 0;
        for (Decision d: decisions){
            d.Save(counter);
            counter++;
        }

        SignClick.getPlugin().getConfig().set("forbid_party." + name, forbidParty);
        SignClick.getPlugin().getConfig().set("aboard_military." + name, aboardMilitary);


    }



}
