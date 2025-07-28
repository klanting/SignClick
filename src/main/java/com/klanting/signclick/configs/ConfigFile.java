package com.klanting.signclick.configs;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;

public class ConfigFile {
    private final String fileName;
    private final JavaPlugin plugin;

    private final HashMap<String, String> commentMap = new HashMap<>();

    public ConfigFile(JavaPlugin plugin, String fileName){
        this.plugin = plugin;
        this.fileName = fileName;
    }

    public void set(String key, Object value, String description){
        File configFile = new File(plugin.getDataFolder()+"/configs", this.fileName);
        YamlConfiguration messagesConfig = YamlConfiguration.loadConfiguration(configFile);

        commentMap.put(key, description);
        messagesConfig.set(key, value);
    }
}
