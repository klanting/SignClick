package com.klanting.signclick.economy.policies;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;

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

        description.add(Arrays.asList("§7Law-Enforcement 1k/week salary", "§7Law-Enforcement 2k/week salary", "§7Law-Enforcement 4k/week salary", "§7Law-Enforcement 8k/week salary", "§7Law-Enforcement 10k/week salary"));
        description.add(Arrays.asList("§7-5 stability", "§7-3 stability", "", "§7+5 stability", "§7+8 stability"));
        description.add(Arrays.asList("§7+8% xp", "§7+3% xp", "", "", "§7-3% xp"));
        description.add(Arrays.asList("", "", "", "§7-25% election penalty", "§7-50% election penalty"));
        description.add(Arrays.asList("", "", "", "", "§7-50% coup penalty"));
        description.add(Arrays.asList("§78k/week tax (military)", "§74k/week tax (military)", "", "§74k/week income (military)", "§78k/week income (military)"));
        description.add(Arrays.asList("", "", "", "", "§72k/week tax (transport)"));
        description.add(Arrays.asList("§72k/week income (bank)", "", "", "", "§72k/week tax (bank)"));
        description.add(Arrays.asList("§7-50% switch leader penalty", "", "", "", "§7+100% switch leader penalty"));
        description.add(Arrays.asList("§7+50% join player bonus", "", "", "", ""));
        description.add(Arrays.asList("§7+50% remove player bonus", "", "", "", ""));
        description.add(Arrays.asList("§7+2% spendable (bank)", "", "", "", ""));

        description.add(Arrays.asList("", "", "", "§9REQUIRE 3 law enforcement", "§9REQUIRE 8 law enforcement"));
        description.add(Arrays.asList("", "", "", "", "§9REQUIRE gov capital 10M+"));

    }
}
