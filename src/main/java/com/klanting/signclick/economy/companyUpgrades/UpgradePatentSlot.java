package com.klanting.signclick.economy.companyUpgrades;

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

        upgradeCost.put(0, 5000000);
        upgradeCost.put(1, 10000000);
        upgradeCost.put(2, 20000000);
        upgradeCost.put(3, 40000000);
        upgradeCost.put(4, 60000000);

        upgradeCostPoints.put(0, 100000);
        upgradeCostPoints.put(1, 300000);
        upgradeCostPoints.put(2, 500000);
        upgradeCostPoints.put(3, 1000000);
        upgradeCostPoints.put(4, 4000000);

        name = "Patent Slot";
        material = Material.END_CRYSTAL;

    }
}
