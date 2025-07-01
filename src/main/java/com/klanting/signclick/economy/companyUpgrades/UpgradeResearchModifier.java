package com.klanting.signclick.economy.companyUpgrades;

import com.klanting.signclick.SignClick;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class UpgradeResearchModifier extends Upgrade{
    public UpgradeResearchModifier(Integer level) {
        super(level, 5);

        ConfigurationSection section = SignClick.getPlugin().getConfig().getConfigurationSection(
                "upgrades").getConfigurationSection("researchModifier");

        assert section != null;

        bonus = section.getIntegerList("bonus");
        upgradeCost = section.getIntegerList("upgradeCost");

        name = "Research Modifier";
        material = Material.EXPERIENCE_BOTTLE;

    }
}
