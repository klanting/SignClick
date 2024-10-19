package com.klanting.signclick.economy.companyPatent;

import org.bukkit.Material;

public class PatentUpgradeCunning extends PatentUpgrade{

    public PatentUpgradeCunning(){
        super(3, 0);
        bonus.put(1, 0.2);
        bonus.put(2, 0.4);
        bonus.put(3, 0.6);
        bonus.put(4, 0.8);
        bonus.put(5, 1.0);
        bonus.put(6, 1.2);
        material = Material.PURPLE_DYE;
        name = "§6Cunning";
    }
}
