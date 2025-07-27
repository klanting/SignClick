package com.klanting.signclick.economy.companyUpgrades;

import com.klanting.signclick.SignClick;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<String> description() {

        List<String> l = new ArrayList<>();
        l.add("§7Upgrade research modifier");

        if (level < 5){
            l.add("§7 From "+getBonus() +"->"+bonus.get(level+1)+"%");
        }

        return l;
    }
}
