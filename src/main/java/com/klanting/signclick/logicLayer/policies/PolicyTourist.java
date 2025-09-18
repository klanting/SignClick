package com.klanting.signclick.logicLayer.policies;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import static com.klanting.signclick.SignClick.configManager;
import static com.klanting.signclick.utils.Utils.AssertMet;

public class PolicyTourist extends Policy{
    public PolicyTourist(Integer level) {
        super(3, level, "Tourism Policy");

        material = Material.OAK_BOAT;

        ConfigurationSection section = configManager.getConfig("policies.yml").getConfigurationSection("policies").getConfigurationSection("tourism");

        AssertMet(section != null, "Section economics not found");

        for(String title: section.getKeys(false)){
            titles.add(title);
            PolicyOption po = new PolicyOption(title, section.getConfigurationSection(title));
            options.add(po);
        }

    }

}
