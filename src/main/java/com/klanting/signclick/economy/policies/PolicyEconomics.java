package com.klanting.signclick.economy.policies;

import com.klanting.signclick.configs.ConfigManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;

import static com.klanting.signclick.SignClick.configManager;
import static com.klanting.signclick.utils.Utils.AssertMet;

public class PolicyEconomics extends Policy{
    public PolicyEconomics(Integer level){
        super(0, level, "Economical Policy");

        material = Material.GOLD_INGOT;

        ConfigurationSection section = configManager.getConfig("policies.yml").getConfigurationSection("policies").getConfigurationSection("economics");

        AssertMet(section != null, "Section economics not found");

        for(String title: section.getKeys(false)){
            titles.add(title);
            PolicyOption po = new PolicyOption(title, section.getConfigurationSection(title));
            options.add(po);
        }

        bonus.add(section.getDoubleList("taxReduction")); //b0 sell tax
        bonus.add(section.getDoubleList("dividendReduction")); //b1s
        bonus.add(section.getDoubleList("points")); //b2: points but removed, fix later
        bonus.add(section.getDoubleList("spendable")); //b3
        bonus.add(section.getDoubleList("funding product")); //b4
        bonus.add(section.getDoubleList("stabilityModifier")); //b5
        bonus.add(section.getDoubleList("funding building")); //b6
        bonus.add(section.getDoubleList("spendable bank")); //b7

        require.add(section.getIntegerList("required"));

        description.add(Arrays.asList("§7+2% sell tax", "§7+1% sell tax", "", "§7-2% sell tax", "§7-3% sell tax"));
        description.add(Arrays.asList("§7-0.5% dividends", "§7-0.3% dividends", "", "§7+0.2% dividends", "§7+0.4% dividends"));

        description.add(Arrays.asList("§7-5% spendable", "§7-2% spendable", "", "§7+2% spendable", "§7+5% spendable"));
        description.add(Arrays.asList("§7+2 stability", "§7+1 stability", "", "§7-1 stability", "§7-3 stability"));
        description.add(Arrays.asList("", "", "", "§7+4% spendable (bank)", "§7+8% spendable (bank)"));
        description.add(Arrays.asList("§71k/week tax (product)", "", "", "", "§72k/week income (product)"));
        description.add(Arrays.asList("§71k/week income (building)", "", "", "", ""));

        description.add(Arrays.asList("§9REQUIRE gov capital 20M+", "", "", "", "§9REQUIRE gov capital 20M+"));



    }
}
