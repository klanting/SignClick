package com.klanting.signclick.economy.companyUpgrades;

import com.klanting.signclick.SignClick;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class UpgradeInvestReturnTime extends Upgrade{
    public UpgradeInvestReturnTime(Integer level) {
        super(level, 4);

        ConfigurationSection section = SignClick.getPlugin().getConfig().getConfigurationSection(
                "upgrades").getConfigurationSection("investReturnTime");

        assert section != null;

        bonus = section.getIntegerList("bonus");
        upgradeCost = section.getIntegerList("upgradeCost");
        upgradeCostPoints = section.getIntegerList("upgradeCostPoints");

        name = "Invest Return Time";
        material = Material.EMERALD;

    }
}
