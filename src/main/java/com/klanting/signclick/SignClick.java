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
    #------------------------------------------------------------------------------------#
    #                                     SignClick                                      #
    #------------------------------------------------------------------------------------#
    # Description:                                                                       #
    # This is the main class for the SignClick plugin. It serves as the central entry    #
    # point and coordinator for all plugin functionality. It handles the startup and     #
    # shutdown logic, including loading configurations, registering commands and events, #
    # managing dependencies (like Vault and Dynmap), and orchestrating data saving and   #
    # loading operations.                                                                #
    #------------------------------------------------------------------------------------#
    */

    // Static instance of the plugin for easy global access.
    private static SignClick plugin;
    public static boolean dynmapSupport;
    public static ConfigManager configManager;
    public static MarkerSet markerSet;
    public static Scoreboard scoreboard;
    public static boolean essentialsSupport;
    public static IEssentials essentials;
    public static InputStream productionConfig;
    public static ConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public void onEnable() {
        /*
        * Called when the plugin is enabled (server startup or plugin reload).
        * */

        // Core Initializations
        plugin = this;
        configManager = new ConfigManager(this);
        essentialsSupport = setupEssentials();
        productionConfig = getResource("productionInit.yml");
        DefaultConfig.makeDefaultConfig();

        // Handle Data Migrations for updates
        MigrationManager.Migrate();

        // Setup Dependencies (Vault, Dynmap, Essentials)

        // Vault
        if (!setupEconomy() ) {
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "SignClick: Economy failed!: Failed to load vault");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        // Dynmap
        dynmapSupport = setupDynmap();
        if (!dynmapSupport) {
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "SignClick: Dynmap Not Supported failed!");
        }
        // Essentials
        essentialsSupport = setupEssentials();

        // Restore Data from files
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

        // Register Event Listeners
        getServer().getPluginManager().registerEvents(new SignEvents(), this);
        getServer().getPluginManager().registerEvents(new DynmapEvents(), this);
        getServer().getPluginManager().registerEvents(new CountryEvents(), this);
        getServer().getPluginManager().registerEvents(new BusEvents(), this);
        getServer().getPluginManager().registerEvents(new MenuEvents(), this);
        getServer().getPluginManager().registerEvents(new PatentEvents(), this);
        getServer().getPluginManager().registerEvents(new OpenFurnaceEvent(), this);
        getServer().getPluginManager().registerEvents(new MachineLiveUpdateEvent(), this);
        getServer().getPluginManager().registerEvents(new ChestShopEvent(), this);

        // GUI Interaction Events
        getServer().getPluginManager().registerEvents(new PagingSearchEvent(), this);
        getServer().getPluginManager().registerEvents(new OpenSelectionMenuEvent(), this);
        getServer().getPluginManager().registerEvents(new AddSupportEvent(), this);
        getServer().getPluginManager().registerEvents(new AddEmployeeEvent(), this);
        getServer().getPluginManager().registerEvents(new AddChiefSupportEvent(), this);

        // Register Command Executors
        getCommand("signclickpos").setExecutor(new SignCommands());
        getCommand("signclick").setExecutor(new BasicCommands());
        getCommand("weeklypay").setExecutor(new BasicCommands());
        getCommand("dynmap").setExecutor(new BasicCommands());
        getCommand("country").setExecutor(new CountryCommands());
        getCommand("company").setExecutor(new CompanyCommands());
        getCommand("party").setExecutor(new PartyCommands());

        // Setup Dynmap Markers if Dynmap is supported
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
        /*
        * Called when the plugin is disabled (server shutdown).
        * */

        // This is a workaround for a potential memory leak with the Gson library upon plugin reload.
        // It uses reflection to clear a static field in Gson, allowing the classloader to be garbage collected.
        try {
            Field gsonInstance = Class.forName("com.google.gson.Gson").getDeclaredField("DEFAULT_INSTANCE");
            gsonInstance.setAccessible(true);
            gsonInstance.set(null, null); // Force clear Gson instance
        } catch (Exception ignored) {}

        // --- Save all plugin data to prevent loss on shutdown/reload ---
        SaveDoors();
        WeeklyPay.save();
        CountryManager.saveData();
        Market.SaveData();
        Auction.Save();
        LicenseSingleton.Save();
        WeeklyComp.Save();

        // Log to the console that the plugin has been disabled.
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "SignClick is disabled!");

        // Cancel all scheduled tasks (like AutoSave) associated with this plugin.
        // This is crucial to prevent errors and memory leaks!
        Bukkit.getScheduler().cancelTasks(SignClick.getPlugin());
    }

    // Static references to external APIs and services.
    private static Economy econ = null;
    private static DynmapAPI dynmap = null;



    private boolean setupEconomy() {
        /*
        * Checks for the Vault plugin and initializes the economy provider.
        * This is a mandatory dependency for the plugin to function.
        * @return true if the economy was set up successfully, false otherwise.
        * */
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "SignClick: Vault Not found!");
            return false;
        }

        // The multiple checks for the service provider are for compatibility across different server versions.
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
        /*
        * Checks for the Dynmap plugin and initializes its API.
        * This is an optional dependency for map-related features.
        * @return true if Dynmap was set up successfully, false otherwise.
        * */
        if (getServer().getPluginManager().getPlugin("dynmap") == null) {
            return false;
        }
        RegisteredServiceProvider<DynmapAPI> rsp = getServer().getServicesManager().getRegistration(DynmapAPI.class);

        // Prefer getting the API via the service provider, but fall back to a direct cast.
        dynmap = (rsp != null) ? rsp.getProvider() : (DynmapAPI) getServer().getPluginManager().getPlugin("dynmap");
        return dynmap != null;
    }


    private boolean setupEssentials() {
        /*
        * Checks for the Essentials plugin and initializes its API.
        * This is an optional dependency for features like getting item prices.
        * @return true if Essentials was set up successfully, false otherwise.
        * */
        if (getServer().getPluginManager().getPlugin("Essentials") != null) {
            essentials = (IEssentials) getServer().getPluginManager().getPlugin("Essentials");
            return true;
        }
        return false;
    }

    public void SaveDoors(){
        //Saves the locations and owners of all income doors to a file.
        Utils.writeSave("incomeDoors", SignIncome.owner);
    }


    public void RestoreDoors(){
        //Loads the locations and owners of all income doors from a file.
        SignIncome.owner = Utils.readSave("incomeDoors",
                new TypeToken<HashMap<Location, UUID>>(){}.getType(), new HashMap<>());
    }


    public static Economy getEconomy() {
        //Provides global access to the Vault Economy API.
        //@return The active Economy provider.
        return econ;
    }

 
    public static DynmapAPI getDynmap() {
        //Provides global access to the Dynmap API.
        //@return The active DynmapAPI provider, or null if not found.
        return dynmap;
    }

    public static SignClick getPlugin() {
        //Provides global access to the main plugin instance.
        //@return The SignClick plugin instance.
        return plugin;
    }


    public static String getPrefix(){
        //Gets the chat prefix from the configuration file.
        //@return The formatted chat prefix string.
        return configManager.getConfig("general.yml").getString("chatPrefix");
    }

    public static String getPrefixUI(){
        //Gets the UI (menu/GUI) prefix from the configuration file.
        //@return The formatted UI prefix string.
        return configManager.getConfig("general.yml").getString("UIPrefix");
    }
}