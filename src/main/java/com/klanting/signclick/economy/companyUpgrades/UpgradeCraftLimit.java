package com.klanting.signclick.economy.companyUpgrades;

import com.klanting.signclick.SignClick;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class UpgradeCraftLimit extends Upgrade{
    public UpgradeCraftLimit(Integer level) {
        super(level, 3);

        ConfigurationSection section = SignClick.getPlugin().getConfig().getConfigurationSection(
                "upgrades").getConfigurationSection("craftLimit");

        assert section != null;

        bonus = section.getIntegerList("bonus");
        upgradeCost = section.getIntegerList("upgradeCost");

        name = "Craft Limit";
        material = Material.CRAFTING_TABLE;

    }
}
