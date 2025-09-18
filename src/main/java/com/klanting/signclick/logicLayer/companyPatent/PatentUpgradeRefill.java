package com.klanting.signclick.logicLayer.companyPatent;

import com.klanting.signclick.SignClick;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class PatentUpgradeRefill extends PatentUpgrade{
    public PatentUpgradeRefill(){
        super(2, 0);
        bonus = SignClick.getConfigManager().getConfig("companies.yml").getDoubleList("patentUpgradeBonusRefill");
        material = Material.BREAD;
        name = "§6Refill";
    }

    @Override public List<String> description(){
        List<String> l = new ArrayList<>();
        l.add("§7When taking damage, you have");
        l.add("§7have a chance that 1 hunger is restored");
        l.add("§7Chance refill hunger: "+getBonus()+"%");
        return l;
    }
}
