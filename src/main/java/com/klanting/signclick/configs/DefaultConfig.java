package com.klanting.signclick.configs;

import com.klanting.signclick.SignClick;
import org.bukkit.configuration.file.FileConfiguration;

public class DefaultConfig {
    public static void makeDefaultConfig(){

        FileConfiguration config = SignClick.getPlugin().getConfig();

        config.addDefault("fee", 0.05);
        config.addDefault("flux", 1.15);

        config.options().copyDefaults(true);
    }
}
