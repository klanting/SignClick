package com.klanting.signclick.configs;

import com.klanting.signclick.SignClick;
import org.bukkit.configuration.file.FileConfiguration;

public class DefaultConfig {
    public static void makeDefaultConfig(){

        FileConfiguration config = SignClick.getPlugin().getConfig();

        config.addDefault("fee", 0.05);
        config.addDefault("flux", 1.01);
        config.addDefault("companyCreateCost", 4_000.0);
        config.addDefault("companyConfirmation", false);
        config.addDefault("companyStartShares", 1000);

        config.options().copyDefaults(true);
        SignClick.getPlugin().saveConfig();


    }

    public static void makeDefaultConfigHard(){
        makeDefaultConfig();

        FileConfiguration config = SignClick.getPlugin().getConfig();

        config.addDefault("flux", 1.15);
        config.addDefault("companyCreateCost", 40_000_000.0);
        config.addDefault("companyConfirmation", true);
        config.addDefault("companyStartShares", 1000_000);

        config.options().copyDefaults(true);
        SignClick.getPlugin().saveConfig();
    }
}
