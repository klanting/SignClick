package com.klanting.signclick.logicLayer.companyLogic.research;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class ResearchOption {



    private final Material material;
    private double progress;

    private int modifierIndex;

    public CompanyI company;

    public Material getMaterial() {
        return material;
    }

    public static final List<Pair<Double, Integer>> modifiers = new ArrayList<>();

    public static void initModifiers(){

        List<Integer> cost = SignClick.getConfigManager().getConfig("companies.yml").getIntegerList("researchModifiersCost");
        List<Double> speed = SignClick.getConfigManager().getConfig("companies.yml").getDoubleList("researchModifiersSpeed");

        assert cost.size() == speed.size();

        modifiers.add(Pair.of(0.0, 0));

        for (int i=0; i<cost.size(); i++){
            modifiers.add(Pair.of(speed.get(i), cost.get(i)));
        }

    }

    public int getModifierIndex() {
        return modifierIndex;
    }



    public boolean isResearching(){
        return modifierIndex != 0;
    }



    public ResearchOption(CompanyI company, Material material){

        this.material = material;
        this.progress = 0;
        this.modifierIndex = 0;
        this.company = company.getRef();

    }

    public void setModifierIndex(int index){
        this.modifierIndex = index;
    }

    public long getCompleteTime(){
        ConfigurationSection section = SignClick.getConfigManager().getConfig("production.yml").getConfigurationSection("products").
                getConfigurationSection(this.company.getType()).getConfigurationSection(this.material.name());

        return section.getLong("researchTime");
    }

    public double getProgress(){
        return Math.min(
                (progress)/getCompleteTime(),
                1.0
        );
    }

    public double getCost(long delta){
        return (modifiers.get(modifierIndex).getRight()*delta)/3600.0;
    }

    public long canPayDelta(double amount){
        if(amount < 0){
            return 0;
        }

        if (modifierIndex == 0){
            return 0;
        }
        return (long) (Math.floor((amount * 3600.0) / modifiers.get(modifierIndex).getRight()));
    }

    public boolean checkProgress(long delta, double upgradeModifier){

        if (isComplete()){
            return false;
        }

        boolean addProduct = false;

        double change = delta*modifiers.get(modifierIndex).getLeft()*upgradeModifier;

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
