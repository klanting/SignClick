package com.klanting.signclick.configs;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ConfigManager {
    /*
    * Wrapper around the plugin.getConfig(),
    * allowing to automatically add comments to the config
    * */

    private final JavaPlugin plugin;

    private final HashMap<String, ConfigFile> fileMap = new HashMap<>();


    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        defaultSetup();
    }

    public void defaultSetup(){
        File configsDir = new File(plugin.getDataFolder(), "configs");

        /*
        * create config directory when absent
        * */
        assert (configsDir.exists() || configsDir.mkdirs());

    }

    public void createConfigFile(String name){
        /*
         * create the various config files
         * */

        assert name.endsWith(".yml") || name.endsWith(".yaml");

        File configFile = new File(plugin.getDataFolder()+"/configs", name);

        try{
            /*
             * create config file
             * */
            assert (configFile.exists() || configFile.createNewFile());
        }catch (IOException e) {
            throw new RuntimeException(e);
        }

        ConfigFile cf = new ConfigFile(plugin, name);
        fileMap.put(name, cf);
    }


}
