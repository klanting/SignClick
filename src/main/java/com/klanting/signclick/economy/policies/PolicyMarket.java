package com.klanting.signclick.economy.policies;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;

import static com.klanting.signclick.SignClick.configManager;
import static com.klanting.signclick.utils.Utils.AssertMet;

public class PolicyMarket extends Policy{

    public PolicyMarket(Integer level){
        super(1, level, "Market Policy");

        material = Material.EMERALD;

        ConfigurationSection section = configManager.getConfig("policies.yml").getConfigurationSection("policies").getConfigurationSection("market");

        AssertMet(section != null, "Section economics not found");

        for(String title: section.getKeys(false)){
            titles.add(title);
            PolicyOption po = new PolicyOption(title, section.getConfigurationSection(title));
            options.add(po);
        }

        bonus.add(Arrays.asList(4000.0, 2000.0, 0.0, -5000.0, -10000.0)); //b0 weekly closed market
        bonus.add(Arrays.asList(-0.02, -0.01, 0.0, 0.01, 0.02)); //b1 taxReduction
        bonus.add(Arrays.asList(0.001, 0.0, 0.0, 0.0, 0.0)); //b2 div reduction
        bonus.add(Arrays.asList(-0.10, -0.05, 0.0, 0.05, 0.10)); //b3 upgradeDiscount
        bonus.add(Arrays.asList(0.0, 0.0, 0.0, 0.05, 0.10)); //b4 createDiscount
        bonus.add(Arrays.asList(0.02, 0.0, 0.0, 0.0, 0.0)); //b5 spendable

        description.add(Arrays.asList("§74k/week income (closed market)", "§72k/week income (closed market)", "", "§75k/week tax (closed market)", "§710k/week tax (closed market)"));
        description.add(Arrays.asList("§7+2% sell tax", "§7+1% sell tax", "", "§7-1% sell tax", "§7-2% sell tax"));
        description.add(Arrays.asList("§7-0.1% dividends", "", "", "", ""));
        description.add(Arrays.asList("§7+10% company upgrade cost", "§7+5% company upgrade cost", "", "§7-5% company upgrade cost", "§7-10% company upgrade cost"));
        description.add(Arrays.asList("", "", "", "§7-5% company create cost", "§7-10% company create cost"));
        description.add(Arrays.asList("§7+2% spendable (bank)", "", "", "", ""));

    }
}
