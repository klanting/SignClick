package com.klanting.signclick.logicLayer.companyLogic.upgrades;

import com.klanting.signclick.SignClick;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class UpgradeProductSlot extends Upgrade{
    public UpgradeProductSlot(Integer level) {
        super(level, 2);

        ConfigurationSection section = SignClick.getConfigManager().getConfig("companies.yml").getConfigurationSection(
                "upgrades").getConfigurationSection("productSlot");

        assert section != null;

        bonus = section.getIntegerList("bonus");
        upgradeCost = section.getIntegerList("upgradeCost");

        name = "Product Slots";
        material = Material.APPLE;

    }

    @Override
    public List<String> description() {

        List<String> l = new ArrayList<>();
        l.add("§7Upgrade product slots");

        if (level < 5){
            l.add("§7From §f§n"+getBonus() +"§r§7->§f§n"+bonus.get(level+1));
        }

        return l;
    }
}
