package com.klanting.signclick.economy.companyUpgrades;

import com.klanting.signclick.economy.Company;
import com.klanting.signclick.SignClick;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class Upgrade {
    public Map<Integer, Integer> bonus = new HashMap<Integer, Integer>();
    public Map<Integer, Integer> upgradeCost = new HashMap<Integer, Integer>();
    public Map<Integer, Integer> upgradeCostPoints = new HashMap<Integer, Integer>();

    public String name;
    public Material material;
    public Integer id;

    public Integer level;
    Upgrade(Integer level, Integer id){
        this.level = level;
        this.id = id;
    }

    public Boolean canUpgrade(Integer balance, Integer points){
        if (upgradeCost.get(level) == -1 || upgradeCostPoints.get(level) == -1){
            return false;
        }
        return upgradeCost.get(level) <= balance && upgradeCostPoints.get(level) <= points;
    }

    public Integer getBonus(){
        return bonus.getOrDefault(level, -1);
    }

    public void DoUpgrade(){
        level += 1;
    }

    public Integer getUpgradeCost(){
        return upgradeCost.getOrDefault(level, -1);
    }

    public Integer getUpgradeCostPoints(){
        return upgradeCostPoints.getOrDefault(level, -1);
    }

    public void save(Company company){
        SignClick.getPlugin().getConfig().set("company."+company.getStockName() +".upgrade."+id, level);
    }
}
