package com.klanting.signclick.logicLayer.companyLogic.upgrades;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.utils.statefullSQL.ClassFlush;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

@ClassFlush
public class UpgradeProductModifier extends Upgrade{
    public UpgradeProductModifier(Integer level) {
        super(level, 6);

        ConfigurationSection section = SignClick.getConfigManager().getConfig("companies.yml").getConfigurationSection(
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
            l.add("§7From §f§n"+getBonus()+"%" +"§r§7->§f§n"+bonus.get(level+1)+"%");
        }

        return l;
    }
}
