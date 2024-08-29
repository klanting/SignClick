package com.klanting.signclick.Economy.CompanyUpgrades;

import org.bukkit.Material;

public class UpgradeCraftLimit extends Upgrade{
    public UpgradeCraftLimit(Integer level) {
        super(level, 3);
        bonus.put(0, 5);
        bonus.put(1, 10);
        bonus.put(2, 20);
        bonus.put(3, 40);
        bonus.put(4, 80);
        bonus.put(5, 100);

        UpgradeCost.put(0, 4000000);
        UpgradeCost.put(1, 8000000);
        UpgradeCost.put(2, 12000000);
        UpgradeCost.put(3, 16000000);
        UpgradeCost.put(4, 20000000);

        UpgradeCostPoints.put(0, 100000);
        UpgradeCostPoints.put(1, 200000);
        UpgradeCostPoints.put(2, 300000);
        UpgradeCostPoints.put(3, 400000);
        UpgradeCostPoints.put(4, 500000);

        name = "Craft Limit";
        material = Material.CRAFTING_TABLE;

    }
}
