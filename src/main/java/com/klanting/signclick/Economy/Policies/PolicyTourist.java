package com.klanting.signclick.Economy.Policies;

import org.bukkit.Material;

import java.util.Arrays;

public class PolicyTourist extends Policy{
    public PolicyTourist(Integer level) {
        super(3, level);

        material = Material.OAK_BOAT;

        bonus.add(Arrays.asList(0.08, 0.04, 0.0, -0.1, -0.15)); //b0
        bonus.add(Arrays.asList(0.0, 0.02, 0.0, -0.02, -0.05)); //b1
        bonus.add(Arrays.asList(-0.05, 0.0, 0.0, 0.0, 0.05)); //b2
        bonus.add(Arrays.asList(-1000.0, 0.0, 0.0, 0.0, 2000.0)); //b3
        bonus.add(Arrays.asList(-1000.0, 0.0, 0.0, 0.0, 2000.0)); //b4
        bonus.add(Arrays.asList(2000.0, 0.0, 0.0, 0.0, -1000.0)); //b5

        require.add(Arrays.asList(0, 0, 0, 5000000, 10000000));

        titles = Arrays.asList("Xenofobia", "Bigot", "Normal", "Open Arm", "Tourist Hugger");

        description.add(Arrays.asList("§7+8% transport cost (foreigner)", "§7+4% transport cost (foreigner)", "", "§7-10% transport cost (foreigner)", "§7-15% transport cost (foreigner)"));
        description.add(Arrays.asList("§7block license with foreigner", "§7+2% license cost (foreigner)", "", "§7-2% license cost (foreigner)", "§7-5% license cost (foreigner)"));
        description.add(Arrays.asList("§7+5% upgrade return time", "", "", "", "§7-5% upgrade return time"));
        description.add(Arrays.asList("§71k/week tax (transport)", "", "", "", "§72k/week income (transport)"));
        description.add(Arrays.asList("§71k/week tax (real estate)", "", "", "", "§72k/week income (real estate)"));
        description.add(Arrays.asList("§72k/week income (building)", "", "", "", "§71k/week tax (building)"));

        description.add(Arrays.asList("", "", "", "§9REQUIRE gov capital 5M+", "§9REQUIRE gov capital 10M+"));

    }

}
