package com.klanting.signclick.economy.companyUpgrades;

import com.klanting.signclick.SignClick;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class UpgradeExtraPoints extends Upgrade{


    public UpgradeExtraPoints(Integer level) {
        super(level, 0);

        ConfigurationSection section = SignClick.getPlugin().getConfig().getConfigurationSection(
                "upgrades").getConfigurationSection("extraPoints");

        assert section != null;

        bonus = section.getIntegerList("bonus");
        upgradeCost = section.getIntegerList("upgradeCost");
        upgradeCostPoints = section.getIntegerList("upgradeCostPoints");

        name = "Extra Points";
        material = Material.GOLD_NUGGET;

    }



}
