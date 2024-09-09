package com.klanting.signclick.Economy.CompanyUpgrades;

import org.bukkit.Material;

public class UpgradePatentSlot extends Upgrade{
    public UpgradePatentSlot(Integer level) {
        super(level, 1);
        bonus.put(0, 1);
        bonus.put(1, 2);
        bonus.put(2, 3);
        bonus.put(3, 4);
        bonus.put(4, 5);
        bonus.put(5, 20);

        UpgradeCost.put(0, 5000000);
        UpgradeCost.put(1, 10000000);
        UpgradeCost.put(2, 20000000);
        UpgradeCost.put(3, 40000000);
        UpgradeCost.put(4, 60000000);

        UpgradeCostPoints.put(0, 100000);
        UpgradeCostPoints.put(1, 300000);
        UpgradeCostPoints.put(2, 500000);
        UpgradeCostPoints.put(3, 1000000);
        UpgradeCostPoints.put(4, 4000000);

        name = "Patent Slot";
        material = Material.END_CRYSTAL;

    }
}
