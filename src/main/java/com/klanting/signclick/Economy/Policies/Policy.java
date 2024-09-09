package com.klanting.signclick.Economy.Policies;

import com.klanting.signclick.SignClick;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class Policy {
    public Material material;

    public List<String> titles = new ArrayList<>();

    public ArrayList<List<Double>> bonus = new ArrayList<>();

    public ArrayList<List<String>> description = new ArrayList<>();

    public ArrayList<List<Integer>> require = new ArrayList<>();

    public Integer id;
    public Integer level;

    public Double getBonus(Integer index){
        return bonus.get(index).get(level);
    }

    public int getRequireLevel(Integer index, int lvl){
        return require.get(index).get(lvl);
    }

    public Double getBonusLevel(Integer index, Integer lvl){
        return bonus.get(index).get(lvl);
    }


    Policy(Integer id, Integer level){
        this.id = id;
        this.level = level;
    }

    public void Save(String country){
        SignClick.getPlugin().getConfig().set("policies." + country+"."+id, level);
    }
}
