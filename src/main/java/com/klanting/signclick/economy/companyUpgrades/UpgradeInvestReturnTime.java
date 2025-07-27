package com.klanting.signclick.economy.companyUpgrades;

import com.klanting.signclick.SignClick;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class UpgradeInvestReturnTime extends Upgrade{
    public UpgradeInvestReturnTime(Integer level) {
        super(level, 4);

        ConfigurationSection section = SignClick.getPlugin().getConfig().getConfigurationSection(
                "upgrades").getConfigurationSection("investReturnTime");

        assert section != null;

        bonus = section.getIntegerList("bonus");
        upgradeCost = section.getIntegerList("upgradeCost");

        name = "Invest Return Time";
        material = Material.EMERALD;

    }

    @Override
    public List<String> description() {

        List<String> l = new ArrayList<>();
        l.add("§7Make the time smaller that you need");
        l.add("§7to wait before getting your invested money back");

        if (level < 5){
            l.add("§7 From "+getBonus() +"->"+bonus.get(level+1));
        }

        return l;
    }
}
