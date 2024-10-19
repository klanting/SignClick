package com.klanting.signclick.economy.companyUpgrades;

import org.bukkit.Material;

public class UpgradeInvestReturnTime extends Upgrade{
    public UpgradeInvestReturnTime(Integer level) {
        super(level, 4);
        bonus.put(0, 0);
        bonus.put(1, 5);
        bonus.put(2, 10);
        bonus.put(3, 15);
        bonus.put(4, 20);
        bonus.put(5, 25);

        upgradeCost.put(0, 4000000);
        upgradeCost.put(1, 8000000);
        upgradeCost.put(2, 12000000);
        upgradeCost.put(3, 16000000);
        upgradeCost.put(4, 20000000);

        upgradeCostPoints.put(0, 100000);
        upgradeCostPoints.put(1, 400000);
        upgradeCostPoints.put(2, 600000);
        upgradeCostPoints.put(3, 800000);
        upgradeCostPoints.put(4, 1000000);

        name = "Invest Return Time";
        material = Material.EMERALD;

    }
}
