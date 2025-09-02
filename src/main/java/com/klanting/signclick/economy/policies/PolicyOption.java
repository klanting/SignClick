package com.klanting.signclick.economy.policies;

import com.klanting.signclick.utils.PreciseNumberFormatter;
import org.bukkit.configuration.ConfigurationSection;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.klanting.signclick.utils.Utils.AssertMet;

public class PolicyOption {

    private String title;
    private Map<String, Double> bonus = new HashMap<>();
    private Map<String, Integer> require = new HashMap<>();

    private Map<String, Double> funding = new HashMap<>();

    public Double getBonus(String s){
        return bonus.getOrDefault(s, 0.0);
    }

    public Double getFunding(String s){
        return funding.getOrDefault(s, 0.0);
    }

    public Integer getRequire(String s){
        return require.getOrDefault(s, 0);
    }

    private static String translationMethod(String type, double value){
        DecimalFormat df = new DecimalFormat("##0.##");

        return switch (type) {
            case "taxReduction" -> (value >= 0 ?"-":"+")+df.format(Math.abs(value*100))+"% sell tax";
            case "dividendReduction" -> (value >= 0 ?"-":"+")+df.format(Math.abs(value*100))+"% dividends";
            case "stabilityModifier" -> (value >= 0 ?"+":"-")+df.format(Math.abs(value))+" stability";
            case "upgradeDiscount" -> (value >= 0 ?"-":"+")+df.format(Math.abs(value*100))+"% company upgrade cost";
            case "createDiscount" -> (value >= 0 ?"-":"+")+df.format(Math.abs(value*100))+"% company create cost";
            case "UpgradeReturnTimeReduction" -> (value >= 0 ?"-":"+")+df.format(Math.abs(value*100))+"% upgrade return time";
            case "transportCost" -> (value >= 0 ?"+":"-")+df.format(Math.abs(value*100))+"% transport cost (foreigner)";
            case "xpGain" -> (value >= 0 ?"+":"-")+df.format(Math.abs(value*100))+"% xp gained";
            case "electionPenaltyReduction" -> (value >= 0 ?"-":"+")+df.format(Math.abs(value*100))+"% election penalty";
            case "coupPenaltyReduction" -> (value >= 0 ?"-":"+")+df.format(Math.abs(value*100))+"% coup penalty";
            case "switchLeaderPenaltyReduction" -> (value >= 0 ?"-":"+")+df.format(Math.abs(value*100))+"% switch leader penalty";
            case "joinPlayerBonus" -> (value >= 0 ?"+":"-")+df.format(Math.abs(value*100))+"% join player bonus";
            case "removePlayerPenalty" -> (value >= 0 ?"-":"+")+df.format(Math.abs(value*100))+"% remove player penalty";
            case "lawEnforcementSalary" -> "§7Law-Enforcement "+df.format(value)+"/week salary";


            case "product" -> df.format(Math.abs(value))+"/week "+(value >= 0 ?"income":"tax")+" (product)";
            case "building" -> df.format(Math.abs(value))+"/week "+(value >= 0 ?"income":"tax")+" (building)";
            case "realEstate" -> df.format(Math.abs(value))+"/week "+(value >= 0 ?"income":"tax")+" (real estate)";
            case "military" -> df.format(Math.abs(value))+"/week "+(value >= 0 ?"income":"tax")+" (military)";
            case "other" -> df.format(Math.abs(value))+"/week "+(value >= 0 ?"income":"tax")+" (other)";
            case "bank" -> df.format(Math.abs(value))+"/week "+(value >= 0 ?"income":"tax")+" (bank)";
            case "transport" -> df.format(Math.abs(value))+"/week "+(value >= 0 ?"income":"tax")+" (transport)";
            case "closedMarket" -> df.format(Math.abs(value))+"/week "+(value >= 0 ?"income":"tax")+" (closed trade)";

            case "capital" -> "gov capital > "+ PreciseNumberFormatter.format(value);

            default -> null;
        };
    }

    public PolicyOption(String title, ConfigurationSection section){
        this.title = title;

        AssertMet(section != null, "policy option section cannot be empty");

        for (String key: section.getKeys(false)){

            if(key.equals("required")){

                for(String requireType: section.getConfigurationSection("required").getKeys(false)){
                    int value = section.getConfigurationSection("required").getInt(requireType);

                    require.put(requireType, value);
                }
            } else if (key.equals("funding")) {
                for(String fundType: section.getConfigurationSection("funding").getKeys(false)){
                    double funds = section.getConfigurationSection("funding").getDouble(fundType);

                    funding.put(fundType, funds);
                }

            } else{
                double value = section.getDouble(key);

                bonus.put(key, value);
            }


        }
    }

    public List<String> getDescription() {

        List<String> descriptions = new ArrayList<>();

        for(Map.Entry<String, Double> field : bonus.entrySet()){
            descriptions.add("§7"+translationMethod(field.getKey(), field.getValue()));
        }
        descriptions.add("");

        for(Map.Entry<String, Double> field : funding.entrySet()){
            descriptions.add("§7"+translationMethod(field.getKey(), field.getValue()));
        }
        descriptions.add("");

        for(Map.Entry<String, Integer> field : require.entrySet()){
            descriptions.add("§9REQUIRED: "+translationMethod(field.getKey(), field.getValue()));
        }

        return descriptions;
    }
}
