package com.klanting.signclick.economy.companyPatent;


import com.klanting.signclick.SignClick;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;


public class PatentUpgradeCunning extends PatentUpgrade{

    public PatentUpgradeCunning(){

        super(3, 0);
        bonus = SignClick.getPlugin().getConfig().getDoubleList("patentUpgradeBonusCunning");
        material = Material.PURPLE_DYE;
        name = "§6Cunning";
    }

    @Override public List<String> description(){
        List<String> l = new ArrayList<>();
        l.add("§7Chance to give your attacker");
        l.add("§7blindness when hitting you");
        l.add("§7Chance blindness: "+getBonus()+"%");
        return l;
    }
}
