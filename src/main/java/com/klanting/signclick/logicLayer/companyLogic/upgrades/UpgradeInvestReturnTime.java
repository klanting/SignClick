package com.klanting.signclick.logicLayer.companyLogic.upgrades;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.utils.statefullSQL.ClassFlush;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

@ClassFlush
public class UpgradeInvestReturnTime extends Upgrade{
    public UpgradeInvestReturnTime(Integer level) {
        super(level, 4);

        ConfigurationSection section = SignClick.getConfigManager().getConfig("companies.yml").getConfigurationSection(
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
            l.add("§7From §f§n"+getBonus() +"§r§7->§f§n"+bonus.get(level+1));
        }

        return l;
    }
}
