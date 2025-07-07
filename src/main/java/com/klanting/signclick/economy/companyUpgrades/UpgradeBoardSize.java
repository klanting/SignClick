package com.klanting.signclick.economy.companyUpgrades;

import com.klanting.signclick.SignClick;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class UpgradeBoardSize extends Upgrade{

    public UpgradeBoardSize(Integer level) {
        super(level, 3);

        ConfigurationSection section = SignClick.getPlugin().getConfig().getConfigurationSection(
                "upgrades").getConfigurationSection("boardSize");

        assert section != null;

        bonus = section.getIntegerList("bonus");
        upgradeCost = section.getIntegerList("upgradeCost");

        name = "Board Size";
        material = Material.CHEST;

    }

    @Override
    public List<String> description() {

        List<String> l = new ArrayList<>();
        l.add("§7Upgrade the amount of board seats");

        if (level < 5){
            l.add("§7 From"+getBonus() +"->"+bonus.get(level+1));
        }

        return l;
    }
}
