package com.klanting.signclick.economy.policies;

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

    protected ArrayList<List<String>> description = new ArrayList<>();

    protected transient ArrayList<PolicyOption> options = new ArrayList<>();

    private Integer id;
    private Integer level;

    public String getName() {
        return name;
    }

    final private String name;

    public Double getBonus(String s){
        return options.get(level).getBonus(s);
    }

    public Double getFunding(String s){
        return options.get(level).getFunding(s);
    }

    public int getRequireLevel(String s, int lvl){
        return options.get(lvl).getRequire(s);
    }

    public Double getBonusLevel(String s, Integer lvl){
        return options.get(lvl).getBonus(s);
    }


    Policy(Integer id, Integer level, String name){
        this.name = name;
        this.id = id;
        this.level = level;
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
