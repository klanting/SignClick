package com.klanting.signclick.logicLayer.companyPatent;

import org.bukkit.Material;
import java.util.ArrayList;
import java.util.List;

abstract public class PatentUpgrade {
    public Material material;
    public Integer level;

    public List<Double> bonus = new ArrayList<>();
    public Integer id;
    public String name;

    public Double getBonus() {
        if (level-1 < 0 || level-1 >= bonus.size()){
            return 0.0;
        }

        return bonus.get(level-1);
    }

    PatentUpgrade(Integer id, Integer level){
        this.id = id;
        this.level = level;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PatentUpgrade up)){
            return false;
        }

        return id.equals(up.id) && level.equals(up.level) && name.equals(up.name);
    }

    abstract public List<String> description();
}
