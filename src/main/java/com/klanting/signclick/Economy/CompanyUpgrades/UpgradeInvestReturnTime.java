package com.klanting.signclick.Economy.CompanyUpgrades;

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

        UpgradeCost.put(0, 4000000);
        UpgradeCost.put(1, 8000000);
        UpgradeCost.put(2, 12000000);
        UpgradeCost.put(3, 16000000);
        UpgradeCost.put(4, 20000000);

        UpgradeCostPoints.put(0, 100000);
        UpgradeCostPoints.put(1, 400000);
        UpgradeCostPoints.put(2, 600000);
        UpgradeCostPoints.put(3, 800000);
        UpgradeCostPoints.put(4, 1000000);

        name = "Invest Return Time";
        material = Material.EMERALD;

    }
}
