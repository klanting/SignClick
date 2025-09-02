package com.klanting.signclick.economy.policies;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
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
}
