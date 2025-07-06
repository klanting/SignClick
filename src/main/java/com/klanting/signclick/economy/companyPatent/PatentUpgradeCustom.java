package com.klanting.signclick.economy.companyPatent;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class PatentUpgradeCustom extends PatentUpgrade{

    public Material applied_item;
    public PatentUpgradeCustom(String name, Material item){
        super(4, 0);
        material = Material.MAP;
        applied_item = item;
        level = 1;
        this.name = "§6Texture "+name;
    }

    @Override public List<String> description(){
        List<String> l = new ArrayList<>();
        l.add("§7TBD");
        return l;
    }

}
