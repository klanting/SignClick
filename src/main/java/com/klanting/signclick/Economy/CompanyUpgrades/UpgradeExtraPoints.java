package com.klanting.signclick.Economy.CompanyUpgrades;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class UpgradeExtraPoints extends Upgrade{


    public UpgradeExtraPoints(Integer level) {
        super(level, 0);
        bonus.put(0, 0);
        bonus.put(1, 5);
        bonus.put(2, 10);
        bonus.put(3, 15);
        bonus.put(4, 20);
        bonus.put(5, 25);

        UpgradeCost.put(0, 2000000);
        UpgradeCost.put(1, 4000000);
        UpgradeCost.put(2, 6000000);
        UpgradeCost.put(3, 8000000);
        UpgradeCost.put(4, 10000000);

        UpgradeCostPoints.put(0, 50000);
        UpgradeCostPoints.put(1, 100000);
        UpgradeCostPoints.put(2, 200000);
        UpgradeCostPoints.put(3, 500000);
        UpgradeCostPoints.put(4, 1000000);

        name = "Extra Points";
        material = Material.GOLD_NUGGET;

    }



}
