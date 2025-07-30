package com.klanting.signclick.economy.companyPatent;

import com.klanting.signclick.SignClick;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class PatentUpgradeJumper extends PatentUpgrade{
    public PatentUpgradeJumper(){
        super(0, 0);
        bonus = SignClick.getConfigManager().getConfig("companies.yml").getDoubleList("patentUpgradeBonusJumper");
        material = Material.FEATHER;
        name = "§6Jumper";
    }

    @Override public List<String> description(){
        List<String> l = new ArrayList<>();
        l.add("§7When taking fall damage,");
        l.add("§7emit a splash of damage to");
        l.add("§7all nearby players");
        l.add("§7Damage on impact: "+getBonus()+"HP");
        return l;
    }
}
