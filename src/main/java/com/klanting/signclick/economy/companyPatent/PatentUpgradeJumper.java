package com.klanting.signclick.economy.companyPatent;

import com.klanting.signclick.SignClick;
import org.bukkit.Material;

import java.util.List;

public class PatentUpgradeJumper extends PatentUpgrade{
    public PatentUpgradeJumper(){
        super(0, 0);
        bonus = SignClick.getPlugin().getConfig().getDoubleList("patentUpgradeBonusJumper");
        material = Material.FEATHER;
        name = "§6Jumper";
    }
}
