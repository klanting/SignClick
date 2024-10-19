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

    public void save(Company comp, Integer index){
        String path = "company."+comp.Sname+".patent_up."+index+".";
        SignClick.getPlugin().getConfig().set(path+"name", name);
        SignClick.getPlugin().getConfig().set(path+"id", id);
        SignClick.getPlugin().getConfig().set(path+"level", level);
        SignClick.getPlugin().getConfig().set(path+"applied_item", applied_item.toString());
    }

}
