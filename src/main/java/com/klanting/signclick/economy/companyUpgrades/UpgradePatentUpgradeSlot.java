package com.klanting.signclick.economy.companyUpgrades;

import com.klanting.signclick.SignClick;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class UpgradePatentUpgradeSlot extends Upgrade{
    public UpgradePatentUpgradeSlot(Integer level) {
        super(level, 1);

        ConfigurationSection section = SignClick.getPlugin().getConfig().getConfigurationSection(
                "upgrades").getConfigurationSection("patentUpgradeSlot");

        assert section != null;

        bonus = section.getIntegerList("bonus");
        upgradeCost = section.getIntegerList("upgradeCost");

        name = "Patent Upgrade Slot";
        material = Material.ITEM_FRAME;
    }
}
