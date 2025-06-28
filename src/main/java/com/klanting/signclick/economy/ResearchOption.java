package com.klanting.signclick.economy;

import com.klanting.signclick.SignClick;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class ResearchOption {

    //TODO get from config

    public Material getMaterial() {
        return material;
    }

    private final Material material;
    private long progress;

    public static final List<Pair<Double, Integer>> modifiers = new ArrayList<>();

    public static void initModifiers(){
        modifiers.add(Pair.of(0.0, 0));
        modifiers.add(Pair.of(1.0, 1000));
        modifiers.add(Pair.of(1.4, 1500));
        modifiers.add(Pair.of(1.7, 2000));
        modifiers.add(Pair.of(1.9, 2500));
        modifiers.add(Pair.of(2.0, 3000));
    }

    public int getModifierIndex() {
        return modifierIndex;
    }

    private int modifierIndex;

    public boolean isResearching(){
        return modifierIndex != 0;
    }

    private final String companyType;

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

    public int getCost(long delta){
        return (int) ((modifiers.get(modifierIndex).getRight()*delta)/3600);
    }

    public long canPayDelta(double amount){
        return (long) ((amount * 3600L) / modifiers.get(modifierIndex).getRight());
    }

    public boolean checkProgress(long delta){

        if (isComplete()){
            return false;
        }

        boolean addProduct = false;

        long change = (long) (delta*modifiers.get(modifierIndex).getLeft());

        if (progress < getCompleteTime() && progress+change >= getCompleteTime()){
            addProduct = true;
        }
        progress += change;

        return addProduct;
    }

    public long getRemainingTime(){

        return (long) ((getCompleteTime()-progress)/modifiers.get(modifierIndex).getLeft());
    }

    public boolean isComplete(){
        return getCompleteTime() <= progress;
    }
}
