package com.klanting.signclick.Economy.CompanyUpgrades;

import org.bukkit.Material;

public class UpgradePatentUpgradeSlot extends Upgrade{
    public UpgradePatentUpgradeSlot(Integer level) {
        super(level, 2);
        bonus.put(0, 3);
        bonus.put(1, 4);
        bonus.put(2, 5);
        bonus.put(3, 6);
        bonus.put(4, 7);
        bonus.put(5, 8);

        UpgradeCost.put(0, 4000000);
        UpgradeCost.put(1, 8000000);
        UpgradeCost.put(2, 12000000);
        UpgradeCost.put(3, 16000000);
        UpgradeCost.put(4, 20000000);

        UpgradeCostPoints.put(0, 100000);
        UpgradeCostPoints.put(1, 200000);
        UpgradeCostPoints.put(2, 300000);
        UpgradeCostPoints.put(3, 600000);
        UpgradeCostPoints.put(4, 1000000);

        name = "Patent Upgrade Slot";
        material = Material.ITEM_FRAME;
    }
}
