package com.klanting.signclick.Economy.Policies;

import com.klanting.signclick.SignClick;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class Policy {

    /*
    * This Class represents a policy of a country
    * Each country has its own policies to influence certain modifiers, and options for countries and companies
    * */
    protected Material material;

    protected List<String> titles = new ArrayList<>();

    protected ArrayList<List<Double>> bonus = new ArrayList<>();

    protected ArrayList<List<String>> description = new ArrayList<>();

    protected ArrayList<List<Integer>> require = new ArrayList<>();

    private Integer id;
    private Integer level;

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

    public Material getMaterial() {
        return material;
    }

    public String getTitle(int level){
        return titles.get(level);
    }

    public ArrayList<List<String>> getDescription() {
        return description;
    }

    public Integer getId() {
        return id;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
