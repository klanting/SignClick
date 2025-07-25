package com.klanting.signclick.economy.policies;

import org.bukkit.Material;

import java.util.Arrays;

public class PolicyEconomics extends Policy{
    public PolicyEconomics(Integer level){
        super(0, level, "Economical Policy");

        material = Material.GOLD_INGOT;

        bonus.add(Arrays.asList(-0.02, -0.01, 0.0, 0.02, 0.03)); //b0
        bonus.add(Arrays.asList(0.005, 0.003, 0.0, -0.002, -0.004)); //b1s
        bonus.add(Arrays.asList(-0.25, -0.10, 0.0, 0.10, 0.25)); //b2: points but removed, fix later
        bonus.add(Arrays.asList(-0.05, -0.02, 0.0, 0.02, 0.05)); //b3
        bonus.add(Arrays.asList(-1000.0, 0.0, 0.0, 0.0, 2000.0)); //b4
        bonus.add(Arrays.asList(2.0, 1.0, 0.0, -1.0, -3.0)); //b5
        bonus.add(Arrays.asList(1000.0, 0.0, 0.0, 0.0, 0.0)); //b6
        bonus.add(Arrays.asList(0.0, 0.0, 0.0, 0.04, 0.08)); //b7

        require.add(Arrays.asList(20000000, 0, 0, 0, 20000000));

        titles = Arrays.asList("Conservative", "Saver", "Normal", "Invester", "Businessman");

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
