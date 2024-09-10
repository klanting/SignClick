package com.klanting.signclick.Economy.CompanyPatent;

import com.klanting.signclick.Economy.Company;
import com.klanting.signclick.SignClick;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class PatentUpgrade {
    public Material material;
    public Integer level;
    public Map<Integer, Double> bonus = new HashMap<Integer, Double>();
    public Integer id;
    public String name;

    public Double getBonus() {
        return bonus.getOrDefault(level, 0.0);
    }

    PatentUpgrade(Integer id, Integer level){
        this.id = id;
        this.level = level;
    }

    public void save(Company comp, Integer index){
        String path = "com.company."+comp.Sname+".patent_up."+index+".";
        SignClick.getPlugin().getConfig().set(path+"id", id);
        SignClick.getPlugin().getConfig().set(path+"level", level);
    }

    public void saveAuction(Integer index){
        String path = "Auction.patent_up."+index+".";
        SignClick.getPlugin().getConfig().set(path+"id", id);
        SignClick.getPlugin().getConfig().set(path+"level", level);
    }
}
