package com.klanting.signclick.economy.companyPatent;

import com.klanting.signclick.economy.Company;
import com.klanting.signclick.SignClick;
import org.bukkit.Material;

public class PatentUpgradeCustom extends PatentUpgrade{

    public Material applied_item;
    public PatentUpgradeCustom(String name, Material item){
        super(4, 0);
        material = Material.MAP;
        applied_item = item;
        level = 1;
        this.name = "§6Texture "+name;
    }

}
