package com.klanting.signclick;


import com.google.common.reflect.TypeToken;
import com.klanting.signclick.configs.ConfigManager;
import com.klanting.signclick.economy.LicenseSingleton;
import com.klanting.signclick.economy.ResearchOption;
import com.klanting.signclick.migrations.MigrationManager;
import com.klanting.signclick.recipes.MachineRecipe;
import com.klanting.signclick.routines.*;
import com.klanting.signclick.commands.CompanyCommands;
import com.klanting.signclick.configs.DefaultConfig;
import com.klanting.signclick.economy.companyPatent.Auction;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.commands.*;
import com.klanting.signclick.events.*;
import com.klanting.signclick.utils.Utils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;

import java.lang.reflect.Field;
import java.util.*;


public class SignClick extends JavaPlugin{

    /*
    * Store the plugin instance as static to easily locate it
    * */
    private static SignClick plugin;
    public static boolean dynmapSupport;

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static ConfigManager configManager;

    @Override
    public void onEnable() {

        plugin = this;
        configManager = new ConfigManager(this);

        DefaultConfig.makeDefaultConfig();
        MigrationManager.Migrate();

        if (!setupEconomy() ) {
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "Economy failed!");
            getServer().getPluginManager().disablePlugin(this);
            return;}

        dynmapSupport = setupDynmap();
        if (!dynmapSupport) {
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "Dynmap Not Supported failed!");
        }

        this.RestoreDoors();

        CountryManager.restoreData();

        WeeklyPay.restore();

        ResearchOption.initModifiers();

        Market.restoreData();

        if (dynmapSupport && SignClick.getConfigManager().getConfig("general.yml").getBoolean("dynmapTax")){
            DynmapCheck.Hide();
        }

        AutoSave.start();

        WeeklyPay.check();
        WeeklyComp.check();

        Auction.Restore();
        Auction.getInstance().check();
        LicenseSingleton.Restore();

        MachineRecipe.create();


        MenuEvents.checkMachines();

        getServer().getPluginManager().registerEvents(new SignEvents(), this);
        getServer().getPluginManager().registerEvents(new DynmapEvents(), this);
        getServer().getPluginManager().registerEvents(new CountryEvents(), this);
        getServer().getPluginManager().registerEvents(new BusEvents(), this);
        getServer().getPluginManager().registerEvents(new MenuEvents(), this);
        getServer().getPluginManager().registerEvents(new PatentEvents(), this);
        getServer().getPluginManager().registerEvents(new PagingSearchEvent(), this);
        getServer().getPluginManager().registerEvents(new OpenSelectionMenuEvent(), this);
        getServer().getPluginManager().registerEvents(new AddSupportEvent(), this);
        getServer().getPluginManager().registerEvents(new AddEmployeeEvent(), this);
        getServer().getPluginManager().registerEvents(new AddChiefSupportEvent(), this);
        getServer().getPluginManager().registerEvents(new OpenFurnaceEvent(), this);
        getServer().getPluginManager().registerEvents(new MachineLiveUpdateEvent(), this);

        getCommand("signclickpos").setExecutor(new SignCommands());
        getCommand("signclick").setExecutor(new BasicCommands());
        getCommand("weeklypay").setExecutor(new BasicCommands());
        getCommand("discord").setExecutor(new BasicCommands());
        getCommand("dynmap").setExecutor(new BasicCommands());
        getCommand("country").setExecutor(new CountryCommands());
        getCommand("company").setExecutor(new CompanyCommands());
        getCommand("party").setExecutor(new PartyCommands());

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "SignClick is enabled!");


    }

    @Override
    public void onDisable() {

        try {
            Field gsonInstance = Class.forName("com.google.gson.Gson").getDeclaredField("DEFAULT_INSTANCE");
            gsonInstance.setAccessible(true);
            gsonInstance.set(null, null); // Force clear Gson instance
        } catch (Exception ignored) {}

        SaveDoors();

        WeeklyPay.save();
        CountryManager.saveData();
        Market.SaveData();
        Auction.Save();
        LicenseSingleton.Save();
        WeeklyComp.Save();

        getServer().getConsoleSender().sendMessage(ChatColor.RED + "SignClick is disabled!");
        configManager.save();
        Bukkit.getScheduler().cancelTasks(SignClick.getPlugin());
    }


    private static Economy econ = null;
    private static DynmapAPI dynmap = null;

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "Vault Not found!");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (rsp == null) {
            rsp = getServer().getServicesManager().getRegistration(Economy.class);
        }
        if (rsp == null) {
            rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        }
        if (rsp == null) {
            rsp = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        }
        if (rsp == null) {
            rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        }



        if (rsp == null) {
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "RSP is null!");
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupDynmap(){
        if (getServer().getPluginManager().getPlugin("dynmap") == null) {
            return false;
        }
        RegisteredServiceProvider<DynmapAPI> rsp = getServer().getServicesManager().getRegistration(DynmapAPI.class);

        if (rsp != null){
            dynmap = rsp.getProvider();
        }else{
            dynmap = (DynmapAPI) Bukkit.getServer().getPluginManager().getPlugin("dynmap");
        }

        return true;

    }

    public void SaveDoors(){
        Utils.writeSave("incomeDoors", SignIncome.owner);


    }
    public void RestoreDoors(){
        SignIncome.owner = Utils.readSave("incomeDoors",
                new TypeToken<HashMap<Location, UUID>>(){}.getType(), new HashMap<>());

    }


    public static Economy getEconomy() {
        return econ;
    }

    public static DynmapAPI getDynmap() {
        return dynmap;
    }

    public static SignClick getPlugin() {
        return plugin;
    }

}
