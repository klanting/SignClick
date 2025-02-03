package com.klanting.signclick.economy.companyUpgrades;

import com.klanting.signclick.economy.Company;
import com.klanting.signclick.SignClick;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Upgrade {
    public List<Integer> bonus = new ArrayList<>();
    public List<Integer> upgradeCost = new ArrayList<>();
    public List<Integer> upgradeCostPoints = new ArrayList<>();

    public String name;
    public Material material;
    public Integer id;

    public Integer level;
    Upgrade(Integer level, Integer id){
        this.level = level;
        this.id = id;
    }

    public Boolean canUpgrade(Integer balance, Integer points){
        if (level >= 5){
            return false;
        }

        return upgradeCost.get(level) <= balance && upgradeCostPoints.get(level) <= points;
    }

    public Integer getBonus(){
        return bonus.get(level);
    }

    public void DoUpgrade(){
        level += 1;
    }

    public Integer getUpgradeCost(){
        return upgradeCost.get(level);
    }

    public Integer getUpgradeCostPoints(){
        return upgradeCostPoints.get(level);
    }

}
