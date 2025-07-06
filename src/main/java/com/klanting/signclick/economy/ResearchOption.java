package com.klanting.signclick.economy;

import com.klanting.signclick.SignClick;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class ResearchOption {

    public Material getMaterial() {
        return material;
    }

    private final Material material;
    private long progress;

    public static final List<Pair<Double, Integer>> modifiers = new ArrayList<>();

    public static void initModifiers(){

        List<Integer> cost = SignClick.getPlugin().getConfig().getIntegerList("researchModifiersCost");
        List<Double> speed = SignClick.getPlugin().getConfig().getDoubleList("researchModifiersSpeed");

        assert cost.size() == speed.size();

        modifiers.add(Pair.of(0.0, 0));

        for (int i=0; i<cost.size(); i++){
            modifiers.add(Pair.of(speed.get(i), cost.get(i)));
        }

    }

    public int getModifierIndex() {
        return modifierIndex;
    }

    private int modifierIndex;

    public boolean isResearching(){
        return modifierIndex != 0;
    }

    public final String companyType;

    public ResearchOption(String companyType, Material material){

        this.material = material;
        this.progress = 0;
        this.modifierIndex = 0;
        this.companyType = companyType;

    }

    public void setModifierIndex(int index){
        this.modifierIndex = index;
    }

    public long getCompleteTime(){

        ConfigurationSection section = SignClick.getPlugin().getConfig().getConfigurationSection("products").
                getConfigurationSection(this.companyType).getConfigurationSection(this.material.name());

        return section.getLong("researchTime");
    }

    public double getProgress(){
        return Math.min(
                ((double) progress)/getCompleteTime(),
                1.0
        );
    }

    public double getCost(long delta){
        return (modifiers.get(modifierIndex).getRight()*delta)/3600.0;
    }

    public long canPayDelta(double amount){
        return (long) (Math.floor((amount * 3600.0) / modifiers.get(modifierIndex).getRight()));
    }

    public boolean checkProgress(long delta, double upgradeModifier){

        if (isComplete()){
            return false;
        }

        boolean addProduct = false;

        long change = (long) (delta*modifiers.get(modifierIndex).getLeft()*upgradeModifier);

        if (progress < getCompleteTime() && progress+change >= getCompleteTime()){
            addProduct = true;
        }
        progress += change;

        return addProduct;
    }

    public long getRemainingTime(){

        return Math.max((long) ((getCompleteTime()-progress)/modifiers.get(modifierIndex).getLeft()), 0);
    }

    public boolean isComplete(){
        return getCompleteTime() <= progress;
    }
}
