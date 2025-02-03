package com.klanting.signclick.economy.companyPatent;

import com.klanting.signclick.SignClick;
import org.bukkit.Material;

import java.util.List;

public class PatentUpgradeEvade extends PatentUpgrade{
    public PatentUpgradeEvade(){
        super(1, 0);
        bonus = SignClick.getPlugin().getConfig().getDoubleList("patentUpgradeBonusEvade");
        material = Material.SHIELD;
        name = "§6Evade";
    }
}
