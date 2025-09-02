package com.klanting.signclick.configs;

import com.klanting.signclick.SignClick;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class DefaultConfig {

    private static void makeProductConfig(ConfigurationSection configSection, Material material,
                                          long researchTime, int productionCost, long productionTime){
        String item = material.name();

        int keys = configSection.getKeys(false).size();


        getOrCreate(configSection, item).addDefault("researchTime", researchTime);
        getOrCreate(configSection, item).addDefault("productionCost", productionCost);
        getOrCreate(configSection, item).addDefault("productionTime", productionTime);
        getOrCreate(configSection, item).addDefault("index", keys);
    }

    private static ConfigurationSection getOrCreate(ConfigurationSection config, String path){
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section == null) {
            /*
            * create if not exists
            * */
            section = config.createSection(path);
        }

        return section;
    }

    public static void makeDefaultConfig(){
        ConfigManager configManager = SignClick.getConfigManager();
        configManager.createConfigFile("general.yml");
        configManager.createConfigFile("companies.yml");
        configManager.createConfigFile("countries.yml");
        configManager.createConfigFile("policies.yml");
        CommentConfig generalConfig = SignClick.getConfigManager().getConfig("general.yml");
        CommentConfig companiesConfig = SignClick.getConfigManager().getConfig("companies.yml");
        CommentConfig countriesConfig = SignClick.getConfigManager().getConfig("countries.yml");
        CommentConfig policiesConfig = SignClick.getConfigManager().getConfig("policies.yml");

        /*
         * Configure the current version of the plugin storage
         * */
        if (generalConfig.saveToString().isEmpty()){
            generalConfig.addDefault("version", SignClick.getPlugin().getDescription().getVersion(),
                    "Latest updated version, don't change this, it will be done automatically");
        }
        generalConfig.addDefault("chatPrefix", "§b",
                "prefix/color being applied to all plugin commands");
        generalConfig.addDefault("UIPrefix", "§6§l",
                "prefix/color being applied to all UI titles");

        companiesConfig.addDefault("fee", 0.05,
                "The amount (%) of money that goes to your country when you sell shares");

        companiesConfig.addDefault("flux", 1.01,
                "How much share prices increase or decrease when shares are bought/sold, " +
                "how higher how much more it changes");
        companiesConfig.addDefault("companyCreateCost", 4_000.0,
                "Amount a player needs to have to create a company");
        companiesConfig.addDefault("companyConfirmation", false,
                "true/false: whether the user needs to repeat the command to confirm");
        companiesConfig.addDefault("companyStartShares", 1000,
                "The amount of shares a company starts with");

        generalConfig.addDefault("dynmapTax", false,
                "by enabling this, people will be taxed for being hidden on the dynmap");
        generalConfig.addDefault("dynmapTaxPeriod", 60*10, "tax period");
        generalConfig.addDefault("dynmapTaxAmount", 1000,
                "Amount the user needs to pay, or player becomes visible");
        generalConfig.addDefault("signIncomeOpenTime", 5,
                "How long sign income door stays open when clicked");

        companiesConfig.addDefault("signStockCost", 1000.0,
                "Cost for making a stock sign, amount goes to company");

        companiesConfig.addDefault("patentUpgradeBonusCunning", List.of(0.5, 1.0, 1.5, 2.0, 2.5, 3.0));
        companiesConfig.addDefault("patentUpgradeBonusEvade", List.of(0.5, 1.0, 1.5, 2.0, 2.5, 3.0));
        companiesConfig.addDefault("patentUpgradeBonusJumper", List.of(1.0, 2.0, 3.0, 4.0, 5.0, 6.0));
        companiesConfig.addDefault("patentUpgradeBonusRefill", List.of(1.0, 2.0, 3.0, 4.0, 5.0, 6.0));

        companiesConfig.createSection("upgrades", "Config for upgrades of your company");
        ConfigurationSection section = companiesConfig.getConfigurationSection("upgrades");
        assert section != null;

        getOrCreate(section, "craftLimit").addDefault("bonus",
                List.of(5, 10, 20, 40, 80, 100));
        getOrCreate(section, "craftLimit").addDefault("upgradeCost",
                List.of(4000, 8000, 12000, 16000, 20000));

        getOrCreate(section, "investReturnTime").addDefault("bonus",
                List.of(0, 5, 10, 15, 20, 25));
        getOrCreate(section, "investReturnTime").addDefault("upgradeCost",
                List.of(4000, 8000, 12000, 16000, 20000));

        getOrCreate(section, "patentSlot").addDefault("bonus",
                List.of(1, 2, 3, 4, 5, 20));
        getOrCreate(section, "patentSlot").addDefault("upgradeCost",
                List.of(5000, 10000, 20000, 40000, 60000));

        getOrCreate(section, "patentUpgradeSlot").addDefault("bonus",
                List.of(3, 4, 5, 6, 7, 8));
        getOrCreate(section, "patentUpgradeSlot").addDefault("upgradeCost",
                List.of(4000, 8000, 12000, 16000, 20000));

        getOrCreate(section, "boardSize").addDefault("bonus",
                List.of(2, 3, 5, 8, 10, 18));
        getOrCreate(section, "boardSize").addDefault("upgradeCost",
                List.of(4000, 8000, 12000, 16000, 20000));

        getOrCreate(section, "productModifier").addDefault("bonus",
                List.of(100, 120, 140, 160, 180, 200));
        getOrCreate(section, "productModifier").addDefault("upgradeCost",
                List.of(4000, 8000, 12000, 16000, 20000));

        getOrCreate(section, "productSlot").addDefault("bonus",
                List.of(9, 27, 45, 63, 81, 99));
        getOrCreate(section, "productSlot").addDefault("upgradeCost",
                List.of(4000, 8000, 12000, 16000, 20000));

        getOrCreate(section, "researchModifier").addDefault("bonus",
                List.of(100, 125, 150, 175, 200, 225));
        getOrCreate(section, "researchModifier").addDefault("upgradeCost",
                List.of(4000, 8000, 12000, 16000, 20000));

        companiesConfig.addDefault("auctionBitIncrease", 1000,
                "By how much patent bids increase each time");
        companiesConfig.addDefault("auctionStartPrice", 1000,
                "Start price for patent auction");

        companiesConfig.addDefault("auctionCycle", 60*60L,
                "How long an auction takes before offering new items");

        countriesConfig.addDefault("electionTime", 60*60*24L,
                "Time that users have to vote at a country election before it is concluded");

        companiesConfig.addDefault("stockBuySellAmount", List.of(10, 100, 200),
                "Market Stock buttons buy/sell amount options UI");
        companiesConfig.addDefault("spendableAmount", List.of(10, 100, 1000, 10000),
                "Spendable buttons buy/sell amount options UI"
                );

        generalConfig.addDefault("autoSaveInterval", 1800,
                "After how much seconds the server auto-saves its plugin data");

        companiesConfig.addDefault("researchModifiersCost", List.of(1000, 1500, 2000, 2500, 3000),
                "Costs for different research modifiers");
        companiesConfig.addDefault("researchModifiersSpeed", List.of(1.0, 1.4, 1.7, 1.9, 2.0),
                "Modifiers of different research modifier options 1.4 -> 140%");

        companiesConfig.addDefault("chiefSalaryChange", 1000.0,
                "Salary button for chief salary UI");
        companiesConfig.addDefault("maxChiefSalary", 10000.0,
                "Max Amount a company chief position can get");

        companiesConfig.createSection("products", "products that can be produced by each type of company");


        /*
        * Product configuration of bank products
        * */
        ConfigurationSection products = companiesConfig.getConfigurationSection("products");

        ConfigurationSection bankP = getOrCreate(products, "bank");

        makeProductConfig(bankP, Material.IRON_INGOT, 1200L, 100, 100L);
        makeProductConfig(bankP, Material.GOLD_INGOT, 3600L, 500, 300L);
        makeProductConfig(bankP, Material.DIAMOND, 18000L, 1000, 600L);
        makeProductConfig(bankP, Material.NETHERITE_INGOT, 36000L, 3000, 1200L);

        /*
         * Product configuration of transport products
         * */
        ConfigurationSection transportP = getOrCreate(products, "transport");

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
        makeProductConfig(transportP, Material.ELYTRA, 72000L, 100000, 3600L);

        /*
         * Product configuration of product products
         * */
        ConfigurationSection productP = getOrCreate(products, "product");

        makeProductConfig(productP, Material.TOTEM_OF_UNDYING, 7200L, 2000, 300L);
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
        ConfigurationSection realEstateP = getOrCreate(products, "real estate");

        makeProductConfig(realEstateP, Material.WHITE_BED, 120L, 10, 60L);
        makeProductConfig(realEstateP, Material.ARMOR_STAND, 120L, 5, 60L);
        makeProductConfig(realEstateP, Material.ITEM_FRAME, 180L, 20, 120L);
        makeProductConfig(realEstateP, Material.GLOW_ITEM_FRAME, 240L, 20, 120L);
        makeProductConfig(realEstateP, Material.PAINTING, 120L, 5, 60L);

        /*
         * Product configuration of military products
         * */
        ConfigurationSection militaryP = getOrCreate(products, "military");

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
        ConfigurationSection buildingP = getOrCreate(products, "building");

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
        ConfigurationSection enchantmentP = getOrCreate(products, "enchantment");

        makeProductConfig(enchantmentP, Material.ENCHANTING_TABLE, 7200L, 2500, 1200L);
        makeProductConfig(enchantmentP, Material.BOOK, 120L, 40, 120L);
        makeProductConfig(enchantmentP, Material.EXPERIENCE_BOTTLE, 240L, 50, 120L);

        /*
         * Product configuration of potion products
         * */
        ConfigurationSection breweryP = getOrCreate(products, "brewery");

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
        ConfigurationSection otherP = getOrCreate(products, "other");

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

        /*
        * setup policies config
        * */
        ConfigurationSection policies = policiesConfig.createSection("policies",
                "configure all the country policy modifiers");

        /*
        * economy policy
        * */
        ConfigurationSection policyEconomics = getOrCreate(policies, "economics");

        getOrCreate(policyEconomics, "Conservative").addDefault("taxReduction", -0.02);
        getOrCreate(policyEconomics, "Conservative").addDefault("dividendReduction", 0.005);
        getOrCreate(getOrCreate(policyEconomics, "Conservative"), "funding").addDefault("product", -1000.0);
        getOrCreate(policyEconomics, "Conservative").addDefault("stabilityModifier", 2.0);
        getOrCreate(getOrCreate(policyEconomics, "Conservative"), "funding").addDefault("building", 1000.0);
        getOrCreate(getOrCreate(policyEconomics, "Conservative"), "required").addDefault("capital", 20000000);

        getOrCreate(policyEconomics, "Saver").addDefault("taxReduction", -0.01);
        getOrCreate(policyEconomics, "Saver").addDefault("dividendReduction", 0.003);
        getOrCreate(policyEconomics, "Saver").addDefault("stabilityModifier", 1.0);

        getOrCreate(policyEconomics, "Normal").addDefault("taxReduction", 0.0);
        getOrCreate(policyEconomics, "Normal").addDefault("dividendReduction", 0.0);
        getOrCreate(policyEconomics, "Normal").addDefault("stabilityModifier", 0.0);

        getOrCreate(policyEconomics, "Invester").addDefault("taxReduction", 0.02);
        getOrCreate(policyEconomics, "Invester").addDefault("dividendReduction", -0.002);
        getOrCreate(policyEconomics, "Invester").addDefault("stabilityModifier", -1.0);

        getOrCreate(policyEconomics, "Businessman").addDefault("taxReduction", 0.03);
        getOrCreate(policyEconomics, "Businessman").addDefault("dividendReduction", -0.004);
        getOrCreate(getOrCreate(policyEconomics, "Businessman"), "funding").addDefault("product", 2000.0);
        getOrCreate(policyEconomics, "Businessman").addDefault("stabilityModifier", -3.0);
        getOrCreate(getOrCreate(policyEconomics, "Businessman"), "required").addDefault("capital", 20000000);

        /*
         * market policy
         * */
        ConfigurationSection policyMarket = getOrCreate(policies, "market");

        getOrCreate(policyMarket, "Closed Market").addDefault("taxReduction", -0.02);
        getOrCreate(getOrCreate(policyMarket, "Closed Market"), "funding").addDefault("closedMarket", 4000.0);
        getOrCreate(policyMarket, "Closed Market").addDefault("dividendReduction", 0.001);
        getOrCreate(policyMarket, "Closed Market").addDefault("upgradeDiscount", -0.10);

        getOrCreate(policyMarket, "Limited Market").addDefault("taxReduction", -0.01);
        getOrCreate(getOrCreate(policyMarket, "Limited Market"), "funding").addDefault("closedMarket", 2000.0);
        getOrCreate(policyMarket, "Limited Market").addDefault("upgradeDiscount", -0.05);

        getOrCreate(policyMarket, "Normal").addDefault("taxReduction", 0);
        getOrCreate(getOrCreate(policyMarket, "Normal"), "funding").addDefault("closedMarket", 0.0);
        getOrCreate(policyMarket, "Normal").addDefault("upgradeDiscount", 0.0);

        getOrCreate(policyMarket, "Open Market").addDefault("taxReduction", 0.01);
        getOrCreate(getOrCreate(policyMarket, "Open Market"), "funding").addDefault("closedMarket", -5000.0);
        getOrCreate(policyMarket, "Open Market").addDefault("upgradeDiscount", 0.05);
        getOrCreate(policyMarket, "Open Market").addDefault("createDiscount", 0.05);

        getOrCreate(policyMarket, "Free Market").addDefault("taxReduction", 0.02);
        getOrCreate(getOrCreate(policyMarket, "Free Market"), "funding").addDefault("closedMarket", -10000.0);
        getOrCreate(policyMarket, "Free Market").addDefault("upgradeDiscount", 0.10);
        getOrCreate(policyMarket, "Free Market").addDefault("createDiscount", 0.10);

        /*
         * military policy
         * */
        ConfigurationSection policyMilitary = getOrCreate(policies, "military");

        getOrCreate(policyMilitary, "Low Arment").addDefault("lawEnforcementSalary", 1000.0);
        getOrCreate(policyMilitary, "Low Arment").addDefault("stabilityModifier", -5.0);
        getOrCreate(policyMilitary, "Low Arment").addDefault("xpGain", 0.08);
        getOrCreate(getOrCreate(policyMilitary, "Low Arment"), "funding").addDefault("military", -8000.0);
        getOrCreate(getOrCreate(policyMilitary, "Low Arment"), "funding").addDefault("bank", 2000.0);
        getOrCreate(policyMilitary, "Low Arment").addDefault("switchLeaderPenaltyReduction", 0.5);
        getOrCreate(policyMilitary, "Low Arment").addDefault("joinPlayerBonus", 0.5);
        getOrCreate(policyMilitary, "Low Arment").addDefault("removePlayerPenalty", 0.5);

        getOrCreate(policyMilitary, "Police Force").addDefault("lawEnforcementSalary", 2000.0);
        getOrCreate(policyMilitary, "Police Force").addDefault("stabilityModifier", -3.0);
        getOrCreate(policyMilitary, "Police Force").addDefault("xpGain", 0.03);
        getOrCreate(getOrCreate(policyMilitary, "Police Force"), "funding").addDefault("military", -4000.0);

        getOrCreate(policyMilitary, "Normal").addDefault("lawEnforcementSalary", 4000.0);
        getOrCreate(policyMilitary, "Normal").addDefault("stabilityModifier", 0.0);
        getOrCreate(policyMilitary, "Normal").addDefault("xpGain", 0.0);
        getOrCreate(getOrCreate(policyMilitary, "Normal"), "funding").addDefault("military", 0.0);

        getOrCreate(policyMilitary, "Para-Militaire").addDefault("lawEnforcementSalary", 8000.0);
        getOrCreate(policyMilitary, "Para-Militaire").addDefault("stabilityModifier", 5.0);
        getOrCreate(policyMilitary, "Para-Militaire").addDefault("xpGain", 0.0);
        getOrCreate(policyMilitary, "Para-Militaire").addDefault("electionPenaltyReduction", 0.25);
        getOrCreate(getOrCreate(policyMilitary, "Para-Militaire"), "funding").addDefault("military", 4000.0);

        getOrCreate(policyMilitary, "Military State").addDefault("lawEnforcementSalary", 10000.0);
        getOrCreate(policyMilitary, "Military State").addDefault("stabilityModifier", 8.0);
        getOrCreate(policyMilitary, "Military State").addDefault("xpGain", -0.03);
        getOrCreate(policyMilitary, "Military State").addDefault("electionPenaltyReduction", 0.5);
        getOrCreate(policyMilitary, "Military State").addDefault("coupPenaltyReduction", 0.5);
        getOrCreate(getOrCreate(policyMilitary, "Military State"), "funding").addDefault("military", 8000.0);
        getOrCreate(getOrCreate(policyMilitary, "Military State"), "funding").addDefault("transport", -2000.0);
        getOrCreate(getOrCreate(policyMilitary, "Military State"), "funding").addDefault("bank", -2000.0);
        getOrCreate(policyMilitary, "Military State").addDefault("switchLeaderPenaltyReduction", -1.0);

        /*
         * tourism policy
         * */
        ConfigurationSection policyTourism = getOrCreate(policies, "tourism");

        getOrCreate(policyTourism, "Xenofobia").addDefault("transportCost", 0.08);
        getOrCreate(policyTourism, "Xenofobia").addDefault("UpgradeReturnTimeReduction", -0.05);
        getOrCreate(getOrCreate(policyTourism, "Xenofobia"), "funding").addDefault("transport", -1000.0);
        getOrCreate(getOrCreate(policyTourism, "Xenofobia"), "funding").addDefault("realEstate", -1000.0);
        getOrCreate(getOrCreate(policyTourism, "Xenofobia"), "funding").addDefault("building", 2000.0);

        getOrCreate(policyTourism, "Bigot").addDefault("transportCost", 0.04);

        getOrCreate(policyTourism, "Normal").addDefault("transportCost", 0.0);

        getOrCreate(policyTourism, "Open Arm").addDefault("transportCost", -0.1);
        getOrCreate(getOrCreate(policyTourism, "Open Arm"), "required").addDefault("capital", 5000000);

        getOrCreate(policyTourism, "Tourist Hugger").addDefault("transportCost", -0.15);
        getOrCreate(policyTourism, "Tourist Hugger").addDefault("UpgradeReturnTimeReduction", 0.05);
        getOrCreate(getOrCreate(policyTourism, "Tourist Hugger"), "funding").addDefault("transport", 2000.0);
        getOrCreate(getOrCreate(policyTourism, "Tourist Hugger"), "funding").addDefault("realEstate", 2000.0);
        getOrCreate(getOrCreate(policyTourism, "Tourist Hugger"), "funding").addDefault("building", -1000.0);
        getOrCreate(getOrCreate(policyTourism, "Tourist Hugger"), "required").addDefault("capital", 10000000);

        /*
         * taxation policy
         * */
        ConfigurationSection policyTaxation = getOrCreate(policies, "taxation");

        getOrCreate(getOrCreate(policyTaxation, "Bankruptcy"), "funding").addDefault("bank", -5000.0);
        getOrCreate(getOrCreate(policyTaxation, "Bankruptcy"), "funding").addDefault("transport", -5000.0);
        getOrCreate(getOrCreate(policyTaxation, "Bankruptcy"), "funding").addDefault("realEstate", -5000.0);
        getOrCreate(getOrCreate(policyTaxation, "Bankruptcy"), "funding").addDefault("military", -5000.0);
        getOrCreate(getOrCreate(policyTaxation, "Bankruptcy"), "funding").addDefault("product", -5000.0);
        getOrCreate(getOrCreate(policyTaxation, "Bankruptcy"), "funding").addDefault("building", -5000.0);
        getOrCreate(getOrCreate(policyTaxation, "Bankruptcy"), "funding").addDefault("other", -5000.0);
        getOrCreate(policyTaxation, "Bankruptcy").addDefault("stabilityModifier", -6.0);
        getOrCreate(getOrCreate(policyTaxation, "Bankruptcy"), "required").addDefault("lawEnforcement", 4);
        getOrCreate(getOrCreate(policyTaxation, "Bankruptcy"), "required").addDefault("minTaxRate", 10);

        getOrCreate(getOrCreate(policyTaxation, "High Taxer"), "funding").addDefault("bank", -2000.0);
        getOrCreate(getOrCreate(policyTaxation, "High Taxer"), "funding").addDefault("transport", -2000.0);
        getOrCreate(getOrCreate(policyTaxation, "High Taxer"), "funding").addDefault("realEstate", -2000.0);
        getOrCreate(getOrCreate(policyTaxation, "High Taxer"), "funding").addDefault("military", -2000.0);
        getOrCreate(getOrCreate(policyTaxation, "High Taxer"), "funding").addDefault("product", -2000.0);
        getOrCreate(getOrCreate(policyTaxation, "High Taxer"), "funding").addDefault("building", -2000.0);
        getOrCreate(getOrCreate(policyTaxation, "High Taxer"), "funding").addDefault("other", -2000.0);
        getOrCreate(policyTaxation, "High Taxer").addDefault("stabilityModifier", -3.0);
        getOrCreate(getOrCreate(policyTaxation, "High Taxer"), "required").addDefault("lawEnforcement", 2);
        getOrCreate(getOrCreate(policyTaxation, "High Taxer"), "required").addDefault("minTaxRate", 10);

        getOrCreate(getOrCreate(policyTaxation, "Normal"), "funding").addDefault("bank", 0.0);

        getOrCreate(getOrCreate(policyTaxation, "Supporter"), "funding").addDefault("bank", 2000.0);
        getOrCreate(getOrCreate(policyTaxation, "Supporter"), "funding").addDefault("transport", 2000.0);
        getOrCreate(getOrCreate(policyTaxation, "Supporter"), "funding").addDefault("realEstate", 2000.0);
        getOrCreate(getOrCreate(policyTaxation, "Supporter"), "funding").addDefault("military", 2000.0);
        getOrCreate(getOrCreate(policyTaxation, "Supporter"), "funding").addDefault("product", 2000.0);
        getOrCreate(getOrCreate(policyTaxation, "Supporter"), "funding").addDefault("building", 2000.0);
        getOrCreate(getOrCreate(policyTaxation, "Supporter"), "funding").addDefault("other", 2000.0);
        getOrCreate(policyTaxation, "Supporter").addDefault("stabilityModifier", 3.0);
        getOrCreate(getOrCreate(policyTaxation, "Supporter"), "required").addDefault("capital", 5000000);
        getOrCreate(getOrCreate(policyTaxation, "Supporter"), "required").addDefault("maxTaxRate", 5);

        getOrCreate(getOrCreate(policyTaxation, "The Hero"), "funding").addDefault("bank", 5000.0);
        getOrCreate(getOrCreate(policyTaxation, "The Hero"), "funding").addDefault("transport", 5000.0);
        getOrCreate(getOrCreate(policyTaxation, "The Hero"), "funding").addDefault("realEstate", 5000.0);
        getOrCreate(getOrCreate(policyTaxation, "The Hero"), "funding").addDefault("military", 5000.0);
        getOrCreate(getOrCreate(policyTaxation, "The Hero"), "funding").addDefault("product", 5000.0);
        getOrCreate(getOrCreate(policyTaxation, "The Hero"), "funding").addDefault("building", 5000.0);
        getOrCreate(getOrCreate(policyTaxation, "The Hero"), "funding").addDefault("other", 5000.0);
        getOrCreate(policyTaxation, "The Hero").addDefault("stabilityModifier", 5.0);
        getOrCreate(getOrCreate(policyTaxation, "The Hero"), "required").addDefault("capital", 10000000);
        getOrCreate(getOrCreate(policyTaxation, "Supporter"), "required").addDefault("maxTaxRate", 5);

        generalConfig.options().copyDefaults(true);
        companiesConfig.options().copyDefaults(true);
        countriesConfig.options().copyDefaults(true);
        policiesConfig.options().copyDefaults(true);

        configManager.save();


    }
}
