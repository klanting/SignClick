package com.klanting.signclick.logicLayer.countryLogic.policies;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import static com.klanting.signclick.SignClick.configManager;
import static com.klanting.signclick.utils.Utils.AssertMet;

public class PolicyTaxation extends Policy{
    public PolicyTaxation(Integer level) {
        super(4, level, "Taxation Policy");
        material = Material.GOLD_BLOCK;

        ConfigurationSection section = configManager.getConfig("policies.yml").getConfigurationSection("policies").getConfigurationSection("taxation");

        AssertMet(section != null, "Section economics not found");

        for(String title: section.getKeys(false)){
            titles.add(title);
            PolicyOption po = new PolicyOption(title, section.getConfigurationSection(title));
            options.add(po);
        }

    }
}
