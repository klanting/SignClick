package com.klanting.signclick.logicLayer.companyLogic.upgrades;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.utils.statefullSQL.ClassFlush;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

@ClassFlush
public class UpgradePatentUpgradeSlot extends Upgrade{
    public UpgradePatentUpgradeSlot(Integer level) {
        super(level, 1);

        ConfigurationSection section = SignClick.getConfigManager().getConfig("companies.yml").getConfigurationSection(
                "upgrades").getConfigurationSection("patentUpgradeSlot");

        assert section != null;

        bonus = section.getIntegerList("bonus");
        upgradeCost = section.getIntegerList("upgradeCost");

        name = "Patent Upgrade Slot";
        material = Material.ITEM_FRAME;
    }

    @Override
    public List<String> description() {

        List<String> l = new ArrayList<>();
        l.add("§7Upgrade amount of patent upgrade slots");

        if (level < 5){
            l.add("§7From §f§n"+getBonus() +"§r§7->§f§n"+bonus.get(level+1));
        }

        return l;
    }
}
