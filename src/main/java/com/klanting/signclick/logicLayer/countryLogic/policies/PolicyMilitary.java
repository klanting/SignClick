package com.klanting.signclick.logicLayer.countryLogic.policies;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import static com.klanting.signclick.SignClick.configManager;
import static com.klanting.signclick.utils.Utils.AssertMet;

public class PolicyMilitary extends Policy{
    public PolicyMilitary(Integer level) {
        super(2, level, "Military Policy");

        material = Material.IRON_SWORD;

        ConfigurationSection section = configManager.getConfig("policies.yml").getConfigurationSection("policies").getConfigurationSection("military");

        AssertMet(section != null, "Section economics not found");

        for(String title: section.getKeys(false)){
            titles.add(title);
            PolicyOption po = new PolicyOption(title, section.getConfigurationSection(title));
            options.add(po);
        }

    }
}
