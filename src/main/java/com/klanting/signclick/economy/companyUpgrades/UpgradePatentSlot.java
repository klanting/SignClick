package com.klanting.signclick.economy.companyUpgrades;

import com.klanting.signclick.SignClick;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class UpgradePatentSlot extends Upgrade{
    public UpgradePatentSlot(Integer level) {
        super(level, 1);

        ConfigurationSection section = SignClick.getPlugin().getConfig().getConfigurationSection(
                "upgrades").getConfigurationSection("patentSlot");

        assert section != null;

        bonus = section.getIntegerList("bonus");
        upgradeCost = section.getIntegerList("upgradeCost");
        upgradeCostPoints = section.getIntegerList("upgradeCostPoints");

        name = "Patent Slot";
        material = Material.END_CRYSTAL;

    }
}
