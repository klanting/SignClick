package com.klanting.signclick.configs;

import com.klanting.signclick.SignClick;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class DefaultConfig {

    private static void makeProductConfig(ConfigurationSection configSection, Material material,
                                          long researchTime, double productionCost, long productionTime){
        String item = material.name();

        int keys = configSection.getKeys(false).size();
        if(SignClick.essentialsSupport){
            BigDecimal price = SignClick.essentials.getPrice(new ItemStack(material));
            if(price != null){
                productionCost = price.doubleValue();
            }
        }

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
        configManager.createConfigFile("production.yml");
        configManager.createConfigFile("storage.yml");

        CommentConfig generalConfig = SignClick.getConfigManager().getConfig("general.yml");
        CommentConfig companiesConfig = SignClick.getConfigManager().getConfig("companies.yml");
        CommentConfig countriesConfig = SignClick.getConfigManager().getConfig("countries.yml");
        CommentConfig policiesConfig = SignClick.getConfigManager().getConfig("policies.yml");
        CommentConfig productionConfig = SignClick.getConfigManager().getConfig("production.yml");
        CommentConfig storageConfig = SignClick.getConfigManager().getConfig("storage.yml");

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

        companiesConfig.addDefault("machinesEnabled", true, "Enabled -> Companies can have machines that produce items");

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

        storageConfig.addDefault("autoSaveInterval", 1800,
                "After how much seconds the server auto-saves its plugin data (only relevant for JSON storage)");

        companiesConfig.addDefault("researchModifiersCost", List.of(1000, 1500, 2000, 2500, 3000),
                "Costs for different research modifiers");
        companiesConfig.addDefault("researchModifiersSpeed", List.of(1.0, 1.4, 1.7, 1.9, 2.0),
                "Modifiers of different research modifier options 1.4 -> 140%");

        companiesConfig.addDefault("chiefSalaryChange", 1000.0,
                "Salary button for chief salary UI");
        companiesConfig.addDefault("maxChiefSalary", 10000.0,
                "Max Amount a company chief position can get");

        productionConfig.createSection("products", "products that can be produced by each type of company");


        /*
        * Product configuration of bank products
        * */
        ConfigurationSection products = productionConfig.getConfigurationSection("products");

        YamlConfiguration productionInit = YamlConfiguration.loadConfiguration(
                new InputStreamReader(SignClick.productionConfig, StandardCharsets.UTF_8)
        );

        ConfigurationSection types = productionInit.getConfigurationSection("types");
        ConfigurationSection companyTypes = productionInit.getConfigurationSection("companyTypes");

        for(String category: companyTypes.getKeys(false)){
            ConfigurationSection categoryP = getOrCreate(products, category);

            if (categoryP.getKeys(false).isEmpty()){
                for(String MaterialString: companyTypes.getConfigurationSection(category).
                        getConfigurationSection("products").getKeys(false)){

                    try{
                        String level = companyTypes.getConfigurationSection(category).getConfigurationSection("products")
                                .getConfigurationSection(MaterialString).getString("TYPE");
                        makeProductConfig(categoryP, Material.valueOf(MaterialString),
                                types.getConfigurationSection(level).getInt("researchTime"),
                                companyTypes.getConfigurationSection(category).getConfigurationSection("products").get("COST") != null ?
                                        companyTypes.getConfigurationSection(category).getConfigurationSection("products").getInt("COST"):
                                        types.getConfigurationSection(level).getInt("COST"),
                                types.getConfigurationSection(level).getInt("productionTime"));
                    }catch (Exception e){
                        getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "SignClick: "+MaterialString+" INVALID");
                        continue;
                    }

                }
            }
        }

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
        getOrCreate(getOrCreate(policyEconomics, "Conservative"), "funding").addDefault("Farming", -1000.0);
        getOrCreate(policyEconomics, "Conservative").addDefault("stabilityModifier", 2.0);
        getOrCreate(getOrCreate(policyEconomics, "Conservative"), "funding").addDefault("Nature", 1000.0);
        getOrCreate(getOrCreate(policyEconomics, "Conservative"), "required").addDefault("capital", 20000);

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
        getOrCreate(getOrCreate(policyEconomics, "Businessman"), "funding").addDefault("Mining", 2000.0);
        getOrCreate(policyEconomics, "Businessman").addDefault("stabilityModifier", -3.0);
        getOrCreate(getOrCreate(policyEconomics, "Businessman"), "required").addDefault("capital", 20000);

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
        getOrCreate(getOrCreate(policyMilitary, "Low Arment"), "funding").addDefault("Fighter", -8000.0);
        getOrCreate(getOrCreate(policyMilitary, "Low Arment"), "funding").addDefault("Mining", 2000.0);
        getOrCreate(policyMilitary, "Low Arment").addDefault("switchLeaderPenaltyReduction", 0.5);
        getOrCreate(policyMilitary, "Low Arment").addDefault("joinPlayerBonus", 0.5);
        getOrCreate(policyMilitary, "Low Arment").addDefault("removePlayerPenalty", 0.5);

        getOrCreate(policyMilitary, "Police Force").addDefault("lawEnforcementSalary", 2000.0);
        getOrCreate(policyMilitary, "Police Force").addDefault("stabilityModifier", -3.0);
        getOrCreate(policyMilitary, "Police Force").addDefault("xpGain", 0.03);
        getOrCreate(getOrCreate(policyMilitary, "Police Force"), "funding").addDefault("Fighter", -4000.0);

        getOrCreate(policyMilitary, "Normal").addDefault("lawEnforcementSalary", 4000.0);
        getOrCreate(policyMilitary, "Normal").addDefault("stabilityModifier", 0.0);
        getOrCreate(policyMilitary, "Normal").addDefault("xpGain", 0.0);
        getOrCreate(getOrCreate(policyMilitary, "Normal"), "funding").addDefault("Fighter", 0.0);

        getOrCreate(policyMilitary, "Para-Militaire").addDefault("lawEnforcementSalary", 8000.0);
        getOrCreate(policyMilitary, "Para-Militaire").addDefault("stabilityModifier", 5.0);
        getOrCreate(policyMilitary, "Para-Militaire").addDefault("xpGain", 0.0);
        getOrCreate(policyMilitary, "Para-Militaire").addDefault("electionPenaltyReduction", 0.25);
        getOrCreate(getOrCreate(policyMilitary, "Para-Militaire"), "funding").addDefault("Fighter", 4000.0);
        getOrCreate(getOrCreate(policyMilitary, "Para-Militaire"), "required").addDefault("lawEnforcement", 3);

        getOrCreate(policyMilitary, "Military State").addDefault("lawEnforcementSalary", 10000.0);
        getOrCreate(policyMilitary, "Military State").addDefault("stabilityModifier", 8.0);
        getOrCreate(policyMilitary, "Military State").addDefault("xpGain", -0.03);
        getOrCreate(policyMilitary, "Military State").addDefault("electionPenaltyReduction", 0.5);
        getOrCreate(policyMilitary, "Military State").addDefault("coupPenaltyReduction", 0.5);
        getOrCreate(getOrCreate(policyMilitary, "Military State"), "funding").addDefault("Fighter", 8000.0);
        getOrCreate(getOrCreate(policyMilitary, "Military State"), "funding").addDefault("Redstone", -2000.0);
        getOrCreate(getOrCreate(policyMilitary, "Military State"), "funding").addDefault("Mining", -2000.0);
        getOrCreate(policyMilitary, "Military State").addDefault("switchLeaderPenaltyReduction", -1.0);
        getOrCreate(getOrCreate(policyMilitary, "Military State"), "required").addDefault("lawEnforcement", 8);
        getOrCreate(getOrCreate(policyMilitary, "Military State"), "required").addDefault("capital", 10_000);

        /*
         * tourism policy
         * */
        ConfigurationSection policyTourism = getOrCreate(policies, "tourism");

        getOrCreate(policyTourism, "Xenofobia").addDefault("transportCost", 0.08);
        getOrCreate(policyTourism, "Xenofobia").addDefault("UpgradeReturnTimeReduction", -0.05);
        getOrCreate(getOrCreate(policyTourism, "Xenofobia"), "funding").addDefault("Redstone", -1000.0);
        getOrCreate(getOrCreate(policyTourism, "Xenofobia"), "funding").addDefault("Woodcutter", -1000.0);
        getOrCreate(getOrCreate(policyTourism, "Xenofobia"), "funding").addDefault("Nature", 2000.0);

        getOrCreate(policyTourism, "Bigot").addDefault("transportCost", 0.04);

        getOrCreate(policyTourism, "Normal").addDefault("transportCost", 0.0);

        getOrCreate(policyTourism, "Open Arm").addDefault("transportCost", -0.1);
        getOrCreate(getOrCreate(policyTourism, "Open Arm"), "required").addDefault("capital", 5000);

        getOrCreate(policyTourism, "Tourist Hugger").addDefault("transportCost", -0.15);
        getOrCreate(policyTourism, "Tourist Hugger").addDefault("UpgradeReturnTimeReduction", 0.05);
        getOrCreate(getOrCreate(policyTourism, "Tourist Hugger"), "funding").addDefault("Redstone", 2000.0);
        getOrCreate(getOrCreate(policyTourism, "Tourist Hugger"), "funding").addDefault("Woodcutter", 2000.0);
        getOrCreate(getOrCreate(policyTourism, "Tourist Hugger"), "funding").addDefault("Nature", -1000.0);
        getOrCreate(getOrCreate(policyTourism, "Tourist Hugger"), "required").addDefault("capital", 10000);

        /*
         * taxation policy
         * */
        ConfigurationSection policyTaxation = getOrCreate(policies, "taxation");

        for(String category: companyTypes.getKeys(false)){
            getOrCreate(getOrCreate(policyTaxation, "Bankruptcy"), "funding").addDefault(category, -5000.0);
            getOrCreate(getOrCreate(policyTaxation, "High Taxer"), "funding").addDefault(category, -2000.0);
            getOrCreate(getOrCreate(policyTaxation, "Normal"), "funding").addDefault(category, 0.0);
            getOrCreate(getOrCreate(policyTaxation, "Supporter"), "funding").addDefault(category, 2000.0);
            getOrCreate(getOrCreate(policyTaxation, "The Hero"), "funding").addDefault(category, 5000.0);
        }
        getOrCreate(policyTaxation, "Bankruptcy").addDefault("stabilityModifier", -6.0);
        getOrCreate(getOrCreate(policyTaxation, "Bankruptcy"), "required").addDefault("lawEnforcement", 4);
        getOrCreate(getOrCreate(policyTaxation, "Bankruptcy"), "required").addDefault("minTaxRate", 10);

        getOrCreate(policyTaxation, "High Taxer").addDefault("stabilityModifier", -3.0);
        getOrCreate(getOrCreate(policyTaxation, "High Taxer"), "required").addDefault("lawEnforcement", 2);
        getOrCreate(getOrCreate(policyTaxation, "High Taxer"), "required").addDefault("minTaxRate", 10);


        getOrCreate(policyTaxation, "Supporter").addDefault("stabilityModifier", 3.0);
        getOrCreate(getOrCreate(policyTaxation, "Supporter"), "required").addDefault("capital", 5000);
        getOrCreate(getOrCreate(policyTaxation, "Supporter"), "required").addDefault("maxTaxRate", 5);

        getOrCreate(policyTaxation, "The Hero").addDefault("stabilityModifier", 5.0);
        getOrCreate(getOrCreate(policyTaxation, "The Hero"), "required").addDefault("capital", 10000);
        getOrCreate(getOrCreate(policyTaxation, "Supporter"), "required").addDefault("maxTaxRate", 5);

        /*
        * storage config
        * */
        storageConfig.addDefault("storageType", "JSON", "Choose between 'JSON' or 'SQL'");

        generalConfig.options().copyDefaults(true);
        companiesConfig.options().copyDefaults(true);
        countriesConfig.options().copyDefaults(true);
        policiesConfig.options().copyDefaults(true);
        productionConfig.options().copyDefaults(true);
        storageConfig.options().copyDefaults(true);

        configManager.save();


    }
}
