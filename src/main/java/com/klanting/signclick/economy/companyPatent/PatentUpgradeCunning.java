package com.klanting.signclick.economy.companyPatent;


import com.klanting.signclick.SignClick;
import org.bukkit.Material;


public class PatentUpgradeCunning extends PatentUpgrade{

    public PatentUpgradeCunning(){

        super(3, 0);
        bonus = SignClick.getPlugin().getConfig().getDoubleList("patentUpgradeBonusCunning");
        material = Material.PURPLE_DYE;
        name = "§6Cunning";
    }
}
