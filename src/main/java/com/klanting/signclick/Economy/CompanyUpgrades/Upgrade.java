package com.klanting.signclick.Economy.CompanyUpgrades;

import com.klanting.signclick.Economy.Company;
import com.klanting.signclick.SignClick;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Bukkit.getServer;

public class Upgrade {
    public Map<Integer, Integer> bonus = new HashMap<Integer, Integer>();
    public Map<Integer, Integer> UpgradeCost = new HashMap<Integer, Integer>();
    public Map<Integer, Integer> UpgradeCostPoints = new HashMap<Integer, Integer>();

    public String name;
    public Material material;
    public Integer id;

    public Integer level;
    Upgrade(Integer level, Integer id){
        this.level = level;
        this.id = id;
    }

    public Boolean canUpgrade(Integer balance, Integer points){
        if (UpgradeCost.get(level) == -1 || UpgradeCostPoints.get(level) == -1){
            return false;
        }
        return UpgradeCost.get(level) <= balance && UpgradeCostPoints.get(level) <= points;
    }

    public Integer getBonus(){
        return bonus.getOrDefault(level, -1);
    }

    public void DoUpgrade(){
        level += 1;
    }

    public Integer getUpgradeCost(){
        return UpgradeCost.getOrDefault(level, -1);
    }

    public Integer getUpgradeCostPoints(){
        return UpgradeCostPoints.getOrDefault(level, -1);
    }

    public void save(Company company){
        SignClick.getPlugin().getConfig().set("company."+company.Sname+".upgrade."+id, level);
    }
}
