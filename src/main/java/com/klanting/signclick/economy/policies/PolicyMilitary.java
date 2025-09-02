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

        bonus.add(Arrays.asList(1000.0, 2000.0, 4000.0, 8000.0, 10000.0)); //b0 law enforcement salary
        bonus.add(Arrays.asList(-5.0, -3.0, 0.0, 5.0, 8.0)); //b1 stability
        bonus.add(Arrays.asList(0.08, 0.03, 0.0, 0.0, -0.03)); //b2 xpGain
        bonus.add(Arrays.asList(0.0, 0.0, 0.0, 0.25, 0.50)); //b3 electionPenaltyReduction
        bonus.add(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.50)); //b4 coupPenaltyReduction
        bonus.add(Arrays.asList(-8000.0, -4000.0, 0.0, 4000.0, 8000.0)); //b5 military funding
        bonus.add(Arrays.asList(0.0, 0.0, 0.0, 0.0, -2000.0)); //b6 transport funding
        bonus.add(Arrays.asList(2000.0, 0.0, 0.0, 0.0, -2000.0)); //b7 bank funding
        bonus.add(Arrays.asList(0.50, 0.0, 0.0, 0.0, -1.0)); //b8 switchLeaderPenaltyReduction
        bonus.add(Arrays.asList(0.5, 0.0, 0.0, 0.0, 0.0)); //b9 joinPlayerBonus
        bonus.add(Arrays.asList(-0.5, 0.0, 0.0, 0.0, 0.0)); //b10 removePlayerPenalty
        bonus.add(Arrays.asList(0.02, 0.0, 0.0, 0.0, 0.0)); //b11 spendable

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

        require.add(Arrays.asList(0, 0, 0, 3, 8));
        require.add(Arrays.asList(0, 0, 0, 0, 10000000));

        description.add(Arrays.asList("", "", "", "§9REQUIRE 3 law enforcement", "§9REQUIRE 8 law enforcement"));
        description.add(Arrays.asList("", "", "", "", "§9REQUIRE gov capital 10M+"));

    }
}
