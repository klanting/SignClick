package com.klanting.signclick.logicLayer.companyLogic.upgrades;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.utils.statefullSQL.ClassFlush;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

@ClassFlush
public class UpgradeResearchModifier extends Upgrade{
    public UpgradeResearchModifier(Integer level) {
        super(level, 5);

        ConfigurationSection section = SignClick.getConfigManager().getConfig("companies.yml").getConfigurationSection(
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
            l.add("§7From §f§n"+getBonus()+"%" +"§r§7->§f§n"+bonus.get(level+1)+"%");
        }

        return l;
    }
}
