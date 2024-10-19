package com.klanting.signclick.economy.companyUpgrades;

import org.bukkit.Material;

public class UpgradeExtraPoints extends Upgrade{


    public UpgradeExtraPoints(Integer level) {
        super(level, 0);
        bonus.put(0, 0);
        bonus.put(1, 5);
        bonus.put(2, 10);
        bonus.put(3, 15);
        bonus.put(4, 20);
        bonus.put(5, 25);

        upgradeCost.put(0, 2000000);
        upgradeCost.put(1, 4000000);
        upgradeCost.put(2, 6000000);
        upgradeCost.put(3, 8000000);
        upgradeCost.put(4, 10000000);

        upgradeCostPoints.put(0, 50000);
        upgradeCostPoints.put(1, 100000);
        upgradeCostPoints.put(2, 200000);
        upgradeCostPoints.put(3, 500000);
        upgradeCostPoints.put(4, 1000000);

        name = "Extra Points";
        material = Material.GOLD_NUGGET;

    }



}
