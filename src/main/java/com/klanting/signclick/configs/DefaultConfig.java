package com.klanting.signclick.configs;

import com.klanting.signclick.SignClick;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.List;

public class DefaultConfig {
    public static void makeDefaultConfig(){

        FileConfiguration config = SignClick.getPlugin().getConfig();

        /*
         * Configure the current version of the plugin storage
         * */
        config.addDefault("version", SignClick.getPlugin().getDescription().getVersion());

        config.addDefault("fee", 0.05);

        config.addDefault("flux", 1.01);
        config.addDefault("companyCreateCost", 4_000.0);
        config.addDefault("companyConfirmation", false);
        config.addDefault("companyStartShares", 1000);
        config.addDefault("dynmapTax", false);
        config.addDefault("dynmapTaxPeriod", 60*10);
        config.addDefault("dynmapTaxAmount", 1000);
        config.addDefault("signIncomeOpenTime", 5);
        config.addDefault("signStockCost", 1000.0);

        config.addDefault("patentUpgradeBonusCunning", List.of(0.5, 1.0, 1.5, 2.0, 2.5, 3.0));
        config.addDefault("patentUpgradeBonusEvade", List.of(0.5, 1.0, 1.5, 2.0, 2.5, 3.0));
        config.addDefault("patentUpgradeBonusJumper", List.of(1.0, 2.0, 3.0, 4.0, 5.0, 6.0));
        config.addDefault("patentUpgradeBonusRefill", List.of(1.0, 2.0, 3.0, 4.0, 5.0, 6.0));

        config.createSection("upgrades");
        ConfigurationSection section = config.getConfigurationSection("upgrades");
        assert section != null;
        section.createSection("craftLimit");
        section.createSection("extraPoints");
        section.createSection("investReturnTime");
        section.createSection("patentSlot");
        section.createSection("patentUpgradeSlot");

        section.getConfigurationSection("craftLimit").addDefault("bonus",
                List.of(5, 10, 20, 40, 80, 100));
        section.getConfigurationSection("craftLimit").addDefault("upgradeCost",
                List.of(40000, 80000, 120000, 160000, 200000));
        section.getConfigurationSection("craftLimit").addDefault("upgradeCostPoints",
                List.of(1000, 2000, 3000, 4000, 5000));

        section.getConfigurationSection("extraPoints").addDefault("bonus",
                List.of(0, 5, 10, 15, 20, 25));
        section.getConfigurationSection("extraPoints").addDefault("upgradeCost",
                List.of(20000, 40000, 60000, 80000, 100000));
        section.getConfigurationSection("extraPoints").addDefault("upgradeCostPoints",
                List.of(500, 1000, 2000, 5000, 10000));

        section.getConfigurationSection("investReturnTime").addDefault("bonus",
                List.of(0, 5, 10, 15, 20, 25));
        section.getConfigurationSection("investReturnTime").addDefault("upgradeCost",
                List.of(40000, 80000, 120000, 160000, 200000));
        section.getConfigurationSection("investReturnTime").addDefault("upgradeCostPoints",
                List.of(1000, 4000, 6000, 8000, 10000));

        section.getConfigurationSection("patentSlot").addDefault("bonus",
                List.of(1, 2, 3, 4, 5, 20));
        section.getConfigurationSection("patentSlot").addDefault("upgradeCost",
                List.of(50000, 100000, 200000, 400000, 600000));
        section.getConfigurationSection("patentSlot").addDefault("upgradeCostPoints",
                List.of(1000, 3000, 5000, 10000, 40000));

        section.getConfigurationSection("patentUpgradeSlot").addDefault("bonus",
                List.of(3, 4, 5, 6, 7, 8));
        section.getConfigurationSection("patentUpgradeSlot").addDefault("upgradeCost",
                List.of(40000, 80000, 120000, 160000, 200000));
        section.getConfigurationSection("patentUpgradeSlot").addDefault("upgradeCostPoints",
                List.of(1000, 2000, 3000, 6000, 10000));

        config.addDefault("auctionBitIncrease", 1000);
        config.addDefault("auctionStartPrice", 1000);

        config.addDefault("auctionCycle", 60*60L);

        config.addDefault("electionTime", 60*60*24L);

        config.addDefault("stockBuySellAmount", List.of(10, 100, 200));

        config.addDefault("autoSaveInterval", 300);

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
        config.addDefault("dynmapTax", true);
        config.addDefault("signStockCost", 100000.0);

        config.addDefault("patentUpgradeBonusCunning", List.of(0.2, 0.4, 0.6, 0.8, 1.0, 1.2));
        config.addDefault("patentUpgradeBonusEvade", List.of(0.2, 0.4, 0.6, 0.8, 1.0, 1.2));
        config.addDefault("patentUpgradeBonusJumper", List.of(0.5, 1.0, 1.5, 2.0, 2.5, 3.0));
        config.addDefault("patentUpgradeBonusRefill", List.of(0.5, 1.0, 1.5, 2.0, 2.5, 3.0));

        ConfigurationSection section = config.getConfigurationSection("upgrades");
        assert section != null;

        section.getConfigurationSection("craftLimit").addDefault("bonus",
                List.of(5, 10, 20, 40, 80, 100));
        section.getConfigurationSection("craftLimit").addDefault("upgradeCost",
                List.of(4000000, 8000000, 12000000, 16000000, 20000000));
        section.getConfigurationSection("craftLimit").addDefault("upgradeCostPoints",
                List.of(100000, 200000, 300000, 400000, 500000));

        section.getConfigurationSection("extraPoints").addDefault("bonus",
                List.of(0, 5, 10, 15, 20, 25));
        section.getConfigurationSection("extraPoints").addDefault("upgradeCost",
                List.of(2000000, 4000000, 6000000, 8000000, 10000000));
        section.getConfigurationSection("extraPoints").addDefault("upgradeCostPoints",
                List.of(50000, 100000, 200000, 500000, 1000000));

        section.getConfigurationSection("investReturnTime").addDefault("bonus",
                List.of(0, 5, 10, 15, 20, 25));
        section.getConfigurationSection("investReturnTime").addDefault("upgradeCost",
                List.of(4000000, 8000000, 12000000, 16000000, 20000000));
        section.getConfigurationSection("investReturnTime").addDefault("upgradeCostPoints",
                List.of(100000, 400000, 600000, 800000, 1000000));

        section.getConfigurationSection("patentSlot").addDefault("bonus",
                List.of(1, 2, 3, 4, 5, 20));
        section.getConfigurationSection("patentSlot").addDefault("upgradeCost",
                List.of(5000000, 10000000, 20000000, 40000000, 60000000));
        section.getConfigurationSection("patentSlot").addDefault("upgradeCostPoints",
                List.of(100000, 300000, 500000, 1000000, 4000000));

        section.getConfigurationSection("patentUpgradeSlot").addDefault("bonus",
                List.of(3, 4, 5, 6, 7, 8));
        section.getConfigurationSection("patentUpgradeSlot").addDefault("upgradeCost",
                List.of(4000000, 8000000, 12000000, 16000000, 20000000));
        section.getConfigurationSection("patentUpgradeSlot").addDefault("upgradeCostPoints",
                List.of(100000, 200000, 300000, 600000, 1000000));

        config.addDefault("auctionBitIncrease", 100000);
        config.addDefault("auctionStartPrice", 100000);

        config.addDefault("auctionCycle", 60*60*24*7L);

        config.addDefault("electionTime", 60*60*24*7L);

        config.addDefault("stockBuySellAmount", List.of(100, 10000, 100000));

        config.options().copyDefaults(true);
        SignClick.getPlugin().saveConfig();
    }
}
