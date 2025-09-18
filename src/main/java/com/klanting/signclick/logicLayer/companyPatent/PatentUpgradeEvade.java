package com.klanting.signclick.logicLayer.companyPatent;

import com.klanting.signclick.SignClick;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class PatentUpgradeEvade extends PatentUpgrade{
    public PatentUpgradeEvade(){
        super(1, 0);
        bonus =  SignClick.getConfigManager().getConfig("companies.yml").getDoubleList("patentUpgradeBonusEvade");
        material = Material.SHIELD;
        name = "§6Evade";
    }

    @Override public List<String> description(){
        List<String> l = new ArrayList<>();
        l.add("§7Chance that a hit");
        l.add("§7against you is ignored");
        l.add("§7Chance avoid damage: "+getBonus()+"%");
        return l;
    }
}
