package com.klanting.signclick.economy.companyPatent;

import com.klanting.signclick.SignClick;
import org.bukkit.Material;

import java.util.List;

public class PatentUpgradeRefill extends PatentUpgrade{
    public PatentUpgradeRefill(){
        super(2, 0);
        bonus = SignClick.getPlugin().getConfig().getDoubleList("patentUpgradeBonusRefill");
        material = Material.BREAD;
        name = "§6Refill";
    }
}
