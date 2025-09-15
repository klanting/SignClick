package com.klanting.signclick.configs;

import com.klanting.signclick.SignClick;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Bukkit.getServer;

public class ConfigManager {
    /*
    * Wrapper around the plugin.getConfig(),
    * allowing to automatically add comments to the config
    * */

    private final JavaPlugin plugin;

    private final HashMap<String, CommentConfig> fileMap = new HashMap<>();


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

        boolean takeAction = false;
        try{
            /*
             * create config file
             * */

            if (!configFile.exists() && name.equals("general.yml")){
                takeAction = true;
            }

            assert (configFile.exists() || configFile.createNewFile());

        }catch (IOException e) {
            throw new RuntimeException(e);
        }

        CommentConfig cc = CommentConfig.loadConfiguration(configFile);

        fileMap.put(name, cc);

        if (takeAction){
            String currentVersion = SignClick.getPlugin().getConfig().getString("version");
            if (currentVersion == null){
                currentVersion = "1.0.0";
            }

            SignClick.getConfigManager().getConfig("general.yml").set("version", currentVersion,
                    "Latest updated version, don't change this, it will be done automatically");

            SignClick.getConfigManager().getConfig("general.yml").options().copyDefaults(true);
            SignClick.getConfigManager().save();
        }
    }

    public CommentConfig getConfig(String name){
        return fileMap.get(name);
    }

    public void save(){
        for (Map.Entry<String, CommentConfig> commentConfig: fileMap.entrySet()){
            String fileName = commentConfig.getKey();
            CommentConfig cc = commentConfig.getValue();

            File configFile = new File(plugin.getDataFolder()+"/configs", fileName);
            try{
                cc.save(configFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }


}
