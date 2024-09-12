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

        upgradeCost.put(0, 4000000);
        upgradeCost.put(1, 8000000);
        upgradeCost.put(2, 12000000);
        upgradeCost.put(3, 16000000);
        upgradeCost.put(4, 20000000);

        upgradeCostPoints.put(0, 100000);
        upgradeCostPoints.put(1, 200000);
        upgradeCostPoints.put(2, 300000);
        upgradeCostPoints.put(3, 600000);
        upgradeCostPoints.put(4, 1000000);

        name = "Patent Upgrade Slot";
        material = Material.ITEM_FRAME;
    }
}
