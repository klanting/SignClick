package com.klanting.signclick;


import com.google.common.reflect.TypeToken;
import com.klanting.signclick.configs.ConfigManager;
import com.klanting.signclick.logicLayer.companyLogic.LicenseSingleton;
import com.klanting.signclick.logicLayer.companyLogic.ResearchOption;
import com.klanting.signclick.interactionLayer.commands.*;
import com.klanting.signclick.interactionLayer.events.*;
import com.klanting.signclick.interactionLayer.routines.*;
import com.klanting.signclick.migrations.MigrationManager;
import com.klanting.signclick.recipes.MachineRecipe;
import com.klanting.signclick.configs.DefaultConfig;
import com.klanting.signclick.logicLayer.companyLogic.patent.Auction;
import com.klanting.signclick.logicLayer.countryLogic.CountryManager;
import com.klanting.signclick.logicLayer.companyLogic.Market;
import com.klanting.signclick.utils.Utils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.MarkerSet;
import net.ess3.api.IEssentials;

import java.io.InputStream;
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

    public static MarkerSet markerSet;

    public static Scoreboard scoreboard;

    public static boolean essentialsSupport;
    public static IEssentials essentials;

    public static InputStream productionConfig;

    @Override
    public void onEnable() {
        plugin = this;
        configManager = new ConfigManager(this);

        essentialsSupport = getServer().getPluginManager().getPlugin("Essentials") != null;
        if(essentialsSupport){
            essentials = (IEssentials) getServer().getPluginManager().getPlugin("Essentials");
        }

        productionConfig = getResource("productionInit.yml");
        DefaultConfig.makeDefaultConfig();

        MigrationManager.Migrate();

        if (!setupEconomy() ) {
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "SignClick: Economy failed!: Failed to load vault");
            getServer().getPluginManager().disablePlugin(this);
            return;}

        dynmapSupport = setupDynmap();
        if (!dynmapSupport) {
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "SignClick: Dynmap Not Supported failed!");
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
        getServer().getPluginManager().registerEvents(new ChestShopEvent(), this);

        getCommand("signclickpos").setExecutor(new SignCommands());
        getCommand("signclick").setExecutor(new BasicCommands());
        getCommand("weeklypay").setExecutor(new BasicCommands());
        getCommand("dynmap").setExecutor(new BasicCommands());
        getCommand("country").setExecutor(new CountryCommands());
        getCommand("company").setExecutor(new CompanyCommands());
        getCommand("party").setExecutor(new PartyCommands());

        if (dynmapSupport){
            try{
                markerSet = dynmap.getMarkerAPI().getMarkerSet("signclick.markerset");
                if (markerSet == null) {
                    markerSet = dynmap.getMarkerAPI().createMarkerSet("signclick.markerset",
                            "SignClick markers", null, true);
                }
            }catch (Exception e){
                getServer().getConsoleSender().sendMessage(ChatColor.RED + "SignClick: Failed to connect with dynmap markers");
            }

        }

        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        SignClick.scoreboard.registerNewTeam("zzz_default");

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
        Bukkit.getScheduler().cancelTasks(SignClick.getPlugin());
    }


    private static Economy econ = null;
    private static DynmapAPI dynmap = null;

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "SignClick: Vault Not found!");
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
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "SignClick: RSP is null!");
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


        return dynmap != null;

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

    public static String getPrefix(){
        return configManager.getConfig("general.yml").getString("chatPrefix");
    }

    public static String getPrefixUI(){
        return configManager.getConfig("general.yml").getString("UIPrefix");
    }

}
