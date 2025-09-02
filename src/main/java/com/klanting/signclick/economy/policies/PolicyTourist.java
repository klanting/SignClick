package com.klanting.signclick.economy.policies;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;

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

        description.add(Arrays.asList("§7+8% transport cost (foreigner)", "§7+4% transport cost (foreigner)", "", "§7-10% transport cost (foreigner)", "§7-15% transport cost (foreigner)"));
        description.add(Arrays.asList("§7block license with foreigner", "§7+2% license cost (foreigner)", "", "§7-2% license cost (foreigner)", "§7-5% license cost (foreigner)"));
        description.add(Arrays.asList("§7+5% upgrade return time", "", "", "", "§7-5% upgrade return time"));
        description.add(Arrays.asList("§71k/week tax (transport)", "", "", "", "§72k/week income (transport)"));
        description.add(Arrays.asList("§71k/week tax (real estate)", "", "", "", "§72k/week income (real estate)"));
        description.add(Arrays.asList("§72k/week income (building)", "", "", "", "§71k/week tax (building)"));

        description.add(Arrays.asList("", "", "", "§9REQUIRE gov capital 5M+", "§9REQUIRE gov capital 10M+"));

    }

}
