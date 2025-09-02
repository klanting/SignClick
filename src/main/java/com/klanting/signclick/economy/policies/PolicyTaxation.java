package com.klanting.signclick.economy.policies;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;

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

        bonus.add(Arrays.asList(-5000.0, -2000.0, 0.0, 2000.0, 5000.0)); //b0 bank
        bonus.add(Arrays.asList(-5000.0, -2000.0, 0.0, 2000.0, 5000.0)); //b1 transport
        bonus.add(Arrays.asList(-5000.0, -2000.0, 0.0, 2000.0, 5000.0)); //b2 product
        bonus.add(Arrays.asList(-5000.0, -2000.0, 0.0, 2000.0, 5000.0)); //b3 realEstate
        bonus.add(Arrays.asList(-5000.0, -2000.0, 0.0, 2000.0, 5000.0)); //b4 military
        bonus.add(Arrays.asList(-5000.0, -2000.0, 0.0, 2000.0, 5000.0)); //b5 building
        bonus.add(Arrays.asList(-5000.0, -2000.0, 0.0, 2000.0, 5000.0)); //b6 other
        bonus.add(Arrays.asList(-6.0, -3.0, 0.0, 3.0, 5.0)); //b7 tax rate

        require.add(Arrays.asList(4, 2, 0, 0, 0)); //law enf req
        require.add(Arrays.asList(0, 0, 0, 5000000, 10000000)); // capital req
        require.add(Arrays.asList(10, 10, 0, 5, 5)); //tax rate

        description.add(Arrays.asList("§75k/week tax (bank)", "§72k/week tax (bank)", "", "§72k/week income (bank)", "§75k/week income (bank)"));
        description.add(Arrays.asList("§75k/week tax (transport)", "§72k/week tax (transport)", "", "§72k/week income (transport)", "§75k/week income (transport)"));
        description.add(Arrays.asList("§75k/week tax (product)", "§72k/week tax (product)", "", "§72k/week income (product)", "§75k/week income (product)"));
        description.add(Arrays.asList("§75k/week tax (real estate)", "§72k/week tax (real estate)", "", "§72k/week income (real estate)", "§75k/week income (real estate)"));
        description.add(Arrays.asList("§75k/week tax (military)", "§72k/week tax (military)", "", "§72k/week income (military)", "§75k/week income (military)"));
        description.add(Arrays.asList("§75k/week tax (building)", "§72k/week tax (building)", "", "§72k/week income (building)", "§75k/week income (building)"));
        description.add(Arrays.asList("§75k/week tax (other)", "§72k/week tax (other)", "", "§72k/week income (other)", "§75k/week income (other)"));
        description.add(Arrays.asList("§7-6 stability", "§7-3 stability", "", "§7+3 stability", "§7+5 stability"));

        description.add(Arrays.asList("§9REQUIRE 4 law enforcement", "§9REQUIRE 2 law enforcement", "", "", ""));
        description.add(Arrays.asList("", "", "", "§9REQUIRE gov capital 5M+", "§9REQUIRE gov capital 10M+"));
        description.add(Arrays.asList("§9REQUIRE taxrate > 10%", "§9REQUIRE taxrate > 10%", "", "§9REQUIRE taxrate < 5%", "§9REQUIRE taxrate < 5%"));
    }
}
