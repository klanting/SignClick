package com.klanting.signclick.economy.companyUpgrades;

import com.klanting.signclick.SignClick;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class UpgradeProductModifier extends Upgrade{
    public UpgradeProductModifier(Integer level) {
        super(level, 6);

        ConfigurationSection section = SignClick.getPlugin().getConfig().getConfigurationSection(
                "upgrades").getConfigurationSection("productModifier");

        assert section != null;

        bonus = section.getIntegerList("bonus");
        upgradeCost = section.getIntegerList("upgradeCost");

        name = "Product Modifier";
        material = Material.FURNACE;

    }

    @Override
    public List<String> description() {

        List<String> l = new ArrayList<>();
        l.add("§7Upgrade machine production modifier");

        if (level < 5){
            l.add("§7 From "+getBonus() +"->"+bonus.get(level+1)+"%");
        }

        return l;
    }
}
