package com.klanting.signclick.logicLayer.companyUpgrades;

import com.klanting.signclick.SignClick;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class UpgradePatentSlot extends Upgrade{
    public UpgradePatentSlot(Integer level) {
        super(level, 0);

        ConfigurationSection section = SignClick.getConfigManager().getConfig("companies.yml").getConfigurationSection(
                "upgrades").getConfigurationSection("patentSlot");

        assert section != null;

        bonus = section.getIntegerList("bonus");
        upgradeCost = section.getIntegerList("upgradeCost");

        name = "Patent Slot";
        material = Material.END_CRYSTAL;

    }

    @Override
    public List<String> description() {

        List<String> l = new ArrayList<>();
        l.add("§7Upgrade amount of patent slots");

        if (level < 5){
            l.add("§7From §f§n"+getBonus() +"§r§7->§f§n"+bonus.get(level+1));
        }

        return l;
    }
}
