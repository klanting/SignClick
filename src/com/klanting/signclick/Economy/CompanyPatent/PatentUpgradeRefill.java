package com.klanting.signclick.Economy.CompanyPatent;

import org.bukkit.Material;

public class PatentUpgradeRefill extends PatentUpgrade{
    public PatentUpgradeRefill(){
        super(2, 0);
        bonus.put(1, 0.5);
        bonus.put(2, 1.0);
        bonus.put(3, 1.5);
        bonus.put(4, 2.0);
        bonus.put(5, 2.5);
        bonus.put(6, 3.0);
        material = Material.BREAD;
        name = "§6Refill";
    }
}
