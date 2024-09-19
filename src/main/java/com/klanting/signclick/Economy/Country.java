package com.klanting.signclick.Economy;

import com.klanting.signclick.Economy.Decisions.Decision;
import com.klanting.signclick.Economy.Parties.Party;
import com.klanting.signclick.Economy.Policies.*;
import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.xml.stream.Location;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Country {

    private String name;

    private int balance;

    private List<UUID> owners = new ArrayList<>();
    private List<UUID> members = new ArrayList<>();

    private List<UUID> lawEnforcement = new ArrayList<>();

    private List<Policy> policies = new ArrayList<>();
    private List<Party> parties = new ArrayList<>();
    private List<Decision> decisions = new ArrayList<>();


    //TODO move default values to constructor
    private Color memberColor = Color.WHITE;

    private Location spawnLocation = null;

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

        forbidParty = false;
        aboardMilitary = false;

        policies = Arrays.asList(new PolicyEconomics(2), new PolicyMarket(2), new PolicyMilitary(2), new PolicyTourist(2), new PolicyTaxation(2));
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

    private void changeCapital(int oldCap){
        /*
        * TO DO
        * */
    }



}
