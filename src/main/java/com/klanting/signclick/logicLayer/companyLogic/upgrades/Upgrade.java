package com.klanting.signclick.logicLayer.companyLogic.upgrades;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

abstract public class Upgrade {
    public List<Integer> bonus = new ArrayList<>();
    public List<Integer> upgradeCost = new ArrayList<>();

    public String name;
    public Material material;
    public Integer id;

    public Integer level;
    Upgrade(Integer level, Integer id){
        this.level = level;
        this.id = id;
    }

    public Boolean canUpgrade(Integer balance){
        if (level >= 5){
            return false;
        }

        return upgradeCost.get(level) <= balance;
    }

    public Integer getBonus(){
        return bonus.get(level);
    }

    public void DoUpgrade(){
        level += 1;
    }

    public Integer getUpgradeCost(){

        if (level >= upgradeCost.size()){
            return -1;
        }

        return upgradeCost.get(level);
    }

    abstract public List<String> description();

}
