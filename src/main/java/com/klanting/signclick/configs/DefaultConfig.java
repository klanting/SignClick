package com.klanting.signclick.configs;

import com.klanting.signclick.SignClick;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class DefaultConfig {

    private static void makeProductConfig(ConfigurationSection configSection, Material material,
                                          long researchTime, int productionCost, long productionTime){
        String item = material.name();
        configSection.createSection(item);

        int keys = configSection.getKeys(false).size();

        configSection.getConfigurationSection(item).addDefault("researchTime", researchTime);
        configSection.getConfigurationSection(item).addDefault("productionCost", productionCost);
        configSection.getConfigurationSection(item).addDefault("productionTime", productionTime);
        configSection.getConfigurationSection(item).addDefault("index", keys);
    }

    public static void makeDefaultConfig(){

        FileConfiguration config = SignClick.getPlugin().getConfig();

        /*
         * Configure the current version of the plugin storage
         * */
        if (config.saveToString().isEmpty()){
            config.addDefault("version", SignClick.getPlugin().getDescription().getVersion());
        }

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
        section.createSection("investReturnTime");
        section.createSection("patentSlot");
        section.createSection("patentUpgradeSlot");
        section.createSection("boardSize");
        section.createSection("productModifier");
        section.createSection("productSlot");
        section.createSection("researchModifier");

        section.getConfigurationSection("craftLimit").addDefault("bonus",
                List.of(5, 10, 20, 40, 80, 100));
        section.getConfigurationSection("craftLimit").addDefault("upgradeCost",
                List.of(4000, 8000, 12000, 16000, 20000));

        section.getConfigurationSection("investReturnTime").addDefault("bonus",
                List.of(0, 5, 10, 15, 20, 25));
        section.getConfigurationSection("investReturnTime").addDefault("upgradeCost",
                List.of(4000, 8000, 12000, 16000, 20000));

        section.getConfigurationSection("patentSlot").addDefault("bonus",
                List.of(1, 2, 3, 4, 5, 20));
        section.getConfigurationSection("patentSlot").addDefault("upgradeCost",
                List.of(5000, 10000, 20000, 40000, 60000));

        section.getConfigurationSection("patentUpgradeSlot").addDefault("bonus",
                List.of(3, 4, 5, 6, 7, 8));
        section.getConfigurationSection("patentUpgradeSlot").addDefault("upgradeCost",
                List.of(4000, 8000, 12000, 16000, 20000));

        section.getConfigurationSection("boardSize").addDefault("bonus",
                List.of(2, 3, 5, 8, 10, 18));
        section.getConfigurationSection("boardSize").addDefault("upgradeCost",
                List.of(4000, 8000, 12000, 16000, 20000));

        section.getConfigurationSection("productModifier").addDefault("bonus",
                List.of(100, 120, 140, 160, 180, 200));
        section.getConfigurationSection("productModifier").addDefault("upgradeCost",
                List.of(4000, 8000, 12000, 16000, 20000));

        section.getConfigurationSection("productSlot").addDefault("bonus",
                List.of(9, 27, 45, 63, 81, 99));
        section.getConfigurationSection("productSlot").addDefault("upgradeCost",
                List.of(4000, 8000, 12000, 16000, 20000));

        section.getConfigurationSection("researchModifier").addDefault("bonus",
                List.of(100, 125, 150, 175, 200, 225));
        section.getConfigurationSection("researchModifier").addDefault("upgradeCost",
                List.of(4000, 8000, 12000, 16000, 20000));

        config.addDefault("auctionBitIncrease", 1000);
        config.addDefault("auctionStartPrice", 1000);

        config.addDefault("auctionCycle", 60*60L);

        config.addDefault("electionTime", 60*60*24L);

        config.addDefault("stockBuySellAmount", List.of(10, 100, 200));
        config.addDefault("spendableAmount", List.of(10, 100, 1000, 10000));

        config.addDefault("autoSaveInterval", 1800);

        config.addDefault("researchModifiersCost", List.of(1000, 1500, 2000, 2500, 3000));
        config.addDefault("researchModifiersSpeed", List.of(1.0, 1.4, 1.7, 1.9, 2.0));

        config.addDefault("chiefSalaryChange", 1000.0);
        config.addDefault("maxChiefSalary", 10000.0);

        config.createSection("products");


        /*
        * Product configuration of bank products
        * */
        config.getConfigurationSection("products").createSection("bank");
        ConfigurationSection bankP = config.getConfigurationSection("products").getConfigurationSection("bank");

        makeProductConfig(bankP, Material.IRON_INGOT, 1200L, 100, 100L);
        makeProductConfig(bankP, Material.GOLD_INGOT, 3600L, 500, 300L);
        makeProductConfig(bankP, Material.DIAMOND, 18000L, 1000, 600L);
        makeProductConfig(bankP, Material.NETHERITE_INGOT, 36000L, 3000, 1200L);

        /*
         * Product configuration of transport products
         * */
        config.getConfigurationSection("products").createSection("transport");
        ConfigurationSection transportP = config.getConfigurationSection("products").getConfigurationSection("transport");

        makeProductConfig(transportP, Material.POWERED_RAIL, 3600L, 100, 60L);
        makeProductConfig(transportP, Material.DETECTOR_RAIL, 600L, 30, 20L);
        makeProductConfig(transportP, Material.RAIL, 120L, 20, 15L);
        makeProductConfig(transportP, Material.ACTIVATOR_RAIL, 600L, 30, 20L);
        makeProductConfig(transportP, Material.MINECART, 1800L, 450, 120L);
        makeProductConfig(transportP, Material.SADDLE, 7200L, 500, 180L);
        makeProductConfig(transportP, Material.LEATHER_HORSE_ARMOR, 60L, 50, 10L);
        makeProductConfig(transportP, Material.IRON_HORSE_ARMOR, 720L, 100, 20L);
        makeProductConfig(transportP, Material.GOLDEN_HORSE_ARMOR, 1800L, 300, 40L);
        makeProductConfig(transportP, Material.DIAMOND_HORSE_ARMOR, 7200L, 600, 120L);
        makeProductConfig(transportP, Material.OAK_BOAT, 60L, 5, 10L);
        makeProductConfig(transportP, Material.ELYTRA, 72000L, 10000, 3600L);

        /*
         * Product configuration of product products
         * */
        config.getConfigurationSection("products").createSection("product");
        ConfigurationSection productP = config.getConfigurationSection("products").getConfigurationSection("product");

        makeProductConfig(productP, Material.TOTEM_OF_UNDYING, 7200L, 1000, 300L);
        makeProductConfig(productP, Material.TRIDENT, 3600L, 500, 180L);
        makeProductConfig(productP, Material.ENDER_PEARL, 180L, 50, 20L);
        makeProductConfig(productP, Material.BLAZE_ROD, 180L, 50, 20L);
        makeProductConfig(productP, Material.ENDER_EYE, 360L, 30, 30L);
        makeProductConfig(productP, Material.ENDER_CHEST, 720L, 500, 300L);
        makeProductConfig(productP, Material.SLIME_BLOCK, 360L, 100, 120L);
        makeProductConfig(productP, Material.HONEY_BLOCK, 360L, 100, 120L);

        /*
         * Product configuration of real estate products
         * */
        config.getConfigurationSection("products").createSection("real estate");
        ConfigurationSection realEstateP = config.getConfigurationSection("products").getConfigurationSection("real estate");

        makeProductConfig(realEstateP, Material.WHITE_BED, 120L, 10, 60L);
        makeProductConfig(realEstateP, Material.ARMOR_STAND, 120L, 5, 60L);
        makeProductConfig(realEstateP, Material.ITEM_FRAME, 180L, 20, 120L);
        makeProductConfig(realEstateP, Material.GLOW_ITEM_FRAME, 240L, 20, 120L);
        makeProductConfig(realEstateP, Material.PAINTING, 120L, 5, 60L);

        /*
         * Product configuration of military products
         * */
        config.getConfigurationSection("products").createSection("military");
        ConfigurationSection militaryP = config.getConfigurationSection("products").getConfigurationSection("military");

        makeProductConfig(militaryP, Material.DIAMOND_SWORD, 7200L, 1500, 720L);
        makeProductConfig(militaryP, Material.NETHERITE_SWORD, 36000L, 3500, 2400L);
        makeProductConfig(militaryP, Material.BOW, 60L, 15, 60L);
        makeProductConfig(militaryP, Material.CROSSBOW, 120L, 30, 90L);
        makeProductConfig(militaryP, Material.SHIELD, 120L, 30, 90L);
        makeProductConfig(militaryP, Material.ARROW, 60L, 3, 10L);
        makeProductConfig(militaryP, Material.SPECTRAL_ARROW, 240L, 15, 30L);

        /*
         * Product configuration of building products
         * */
        config.getConfigurationSection("products").createSection("building");
        ConfigurationSection buildingP = config.getConfigurationSection("products").getConfigurationSection("building");

        makeProductConfig(buildingP, Material.COBBLESTONE, 60L, 1, 10L);
        makeProductConfig(buildingP, Material.GRANITE, 60L, 1, 10L);
        makeProductConfig(buildingP, Material.DIORITE, 60L, 1, 10L);
        makeProductConfig(buildingP, Material.ANDESITE, 60L, 1, 10L);
        makeProductConfig(buildingP, Material.STONE_BRICKS, 120L, 1, 10L);
        makeProductConfig(buildingP, Material.SAND, 240L, 5, 20L);
        makeProductConfig(buildingP, Material.GRAVEL, 240L, 5, 20L);
        makeProductConfig(buildingP, Material.GLASS, 300L, 5, 20L);
        makeProductConfig(buildingP, Material.WHITE_CONCRETE, 240L, 8, 30L);
        makeProductConfig(buildingP, Material.WHITE_CONCRETE_POWDER, 240L, 8, 30L);
        makeProductConfig(buildingP, Material.OAK_LOG, 120L, 4, 20L);
        makeProductConfig(buildingP, Material.OAK_LOG, 120L, 4, 20L);
        makeProductConfig(buildingP, Material.SPRUCE_LOG, 120L, 4, 20L);
        makeProductConfig(buildingP, Material.BIRCH_LOG, 120L, 4, 20L);
        makeProductConfig(buildingP, Material.JUNGLE_LOG, 120L, 4, 20L);
        makeProductConfig(buildingP, Material.ACACIA_LOG, 120L, 4, 20L);
        makeProductConfig(buildingP, Material.DARK_OAK_LOG, 120L, 4, 20L);
        makeProductConfig(buildingP, Material.BRICKS, 180L, 5, 20L);
        makeProductConfig(buildingP, Material.SMOOTH_STONE, 180L, 5, 20L);
        makeProductConfig(buildingP, Material.QUARTZ_BLOCK, 7200L, 200, 120L);
        makeProductConfig(buildingP, Material.CLAY, 180L, 5, 20L);

        /*
         * Product configuration of enchantment products
         * */
        config.getConfigurationSection("products").createSection("enchantment");
        ConfigurationSection enchantmentP = config.getConfigurationSection("products").getConfigurationSection("enchantment");

        makeProductConfig(enchantmentP, Material.ENCHANTING_TABLE, 7200L, 2500, 1200L);
        makeProductConfig(enchantmentP, Material.BOOK, 120L, 40, 120L);
        makeProductConfig(enchantmentP, Material.EXPERIENCE_BOTTLE, 240L, 50, 120L);

        /*
         * Product configuration of enchantment products
         * */
        config.getConfigurationSection("products").createSection("brewery");
        ConfigurationSection breweryP = config.getConfigurationSection("products").getConfigurationSection("brewery");

        makeProductConfig(breweryP, Material.BREWING_STAND, 120L, 50, 120L);
        makeProductConfig(breweryP, Material.BLAZE_POWDER, 120L, 15, 120L);
        makeProductConfig(breweryP, Material.GHAST_TEAR, 120L, 10, 120L);
        makeProductConfig(breweryP, Material.FERMENTED_SPIDER_EYE, 120L, 10, 120L);
        makeProductConfig(breweryP, Material.MAGMA_CREAM, 120L, 10, 120L);
        makeProductConfig(breweryP, Material.GLISTERING_MELON_SLICE, 120L, 10, 120L);
        makeProductConfig(breweryP, Material.GOLDEN_CARROT, 120L, 10, 120L);
        makeProductConfig(breweryP, Material.RABBIT_FOOT, 120L, 10, 120L);

        /*
         * Product configuration of other products
         * */
        config.getConfigurationSection("products").createSection("other");
        ConfigurationSection otherP = config.getConfigurationSection("products").getConfigurationSection("other");

        makeProductConfig(otherP, Material.AZALEA, 120L, 20, 30L);
        makeProductConfig(otherP, Material.FLOWERING_AZALEA, 120L, 20, 30L);
        makeProductConfig(otherP, Material.SPORE_BLOSSOM, 120L, 20, 30L);
        makeProductConfig(otherP, Material.SEAGRASS, 60L, 10, 10L);
        makeProductConfig(otherP, Material.SEA_PICKLE, 120L, 20, 30L);
        makeProductConfig(otherP, Material.DANDELION, 60L, 10, 10L);
        makeProductConfig(otherP, Material.POPPY, 60L, 10, 10L);
        makeProductConfig(otherP, Material.BLUE_ORCHID, 60L, 10, 10L);
        makeProductConfig(otherP, Material.ALLIUM, 60L, 10, 10L);
        makeProductConfig(otherP, Material.AZURE_BLUET, 60L, 10, 10L);
        makeProductConfig(otherP, Material.RED_TULIP, 60L, 10, 10L);
        makeProductConfig(otherP, Material.ORANGE_TULIP, 60L, 10, 10L);
        makeProductConfig(otherP, Material.WHITE_TULIP, 60L, 10, 10L);
        makeProductConfig(otherP, Material.PINK_TULIP, 60L, 10, 10L);
        makeProductConfig(otherP, Material.OXEYE_DAISY, 60L, 10, 10L);
        makeProductConfig(otherP, Material.CORNFLOWER, 60L, 10, 10L);
        makeProductConfig(otherP, Material.LILY_OF_THE_VALLEY, 60L, 10, 10L);
        makeProductConfig(otherP, Material.BAMBOO, 240L, 1, 3L);
        makeProductConfig(otherP, Material.SUGAR_CANE, 240L, 5, 3L);
        makeProductConfig(otherP, Material.KELP, 240L, 3, 3L);
        makeProductConfig(otherP, Material.CACTUS, 240L, 5, 3L);
        makeProductConfig(otherP, Material.VINE, 240L, 3, 3L);
        makeProductConfig(otherP, Material.LILY_PAD, 120L, 10, 10L);
        makeProductConfig(otherP, Material.CHORUS_PLANT, 3600L, 100, 120L);
        makeProductConfig(otherP, Material.LEATHER, 180L, 20, 60L);
        makeProductConfig(otherP, Material.LEAD, 3600L, 100, 120L);
        makeProductConfig(otherP, Material.SPYGLASS, 1800L, 150, 120L);
        makeProductConfig(otherP, Material.NAME_TAG, 3600L, 100, 120L);

        config.options().copyDefaults(true);
        SignClick.getPlugin().saveConfig();


    }

    public static void makeDefaultConfigHard(){
        makeDefaultConfig();

        FileConfiguration config = SignClick.getPlugin().getConfig();


        //config.addDefault("companyCreateCost", 40_000_000.0);
        //config.addDefault("companyConfirmation", true);
        //config.addDefault("companyStartShares", 1000_000);
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

        section.getConfigurationSection("investReturnTime").addDefault("bonus",
                List.of(0, 5, 10, 15, 20, 25));
        section.getConfigurationSection("investReturnTime").addDefault("upgradeCost",
                List.of(4000000, 8000000, 12000000, 16000000, 20000000));

        section.getConfigurationSection("patentSlot").addDefault("bonus",
                List.of(1, 2, 3, 4, 5, 20));
        section.getConfigurationSection("patentSlot").addDefault("upgradeCost",
                List.of(5000000, 10000000, 20000000, 40000000, 60000000));

        section.getConfigurationSection("patentUpgradeSlot").addDefault("bonus",
                List.of(3, 4, 5, 6, 7, 8));
        section.getConfigurationSection("patentUpgradeSlot").addDefault("upgradeCost",
                List.of(4000000, 8000000, 12000000, 16000000, 20000000));

        config.addDefault("auctionBitIncrease", 100000);
        config.addDefault("auctionStartPrice", 100000);

        config.addDefault("auctionCycle", 60*60*24*7L);

        config.addDefault("electionTime", 60*60*24*7L);

        config.addDefault("stockBuySellAmount", List.of(100, 10000, 100000));
        config.addDefault("spendableAmount", List.of(100, 10000, 100000, 1000000));


        config.options().copyDefaults(true);
        SignClick.getPlugin().saveConfig();
    }
}
