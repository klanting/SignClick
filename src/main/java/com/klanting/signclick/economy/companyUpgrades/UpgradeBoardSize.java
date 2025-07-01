package com.klanting.signclick.economy.companyUpgrades;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.Company;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class UpgradeBoardSize extends Upgrade{

    public UpgradeBoardSize(Integer level) {
        super(level, 3);

        ConfigurationSection section = SignClick.getPlugin().getConfig().getConfigurationSection(
                "upgrades").getConfigurationSection("boardSize");

        assert section != null;

        bonus = section.getIntegerList("bonus");
        upgradeCost = section.getIntegerList("upgradeCost");

        name = "Board Size";
        material = Material.CHEST;

    }
}
