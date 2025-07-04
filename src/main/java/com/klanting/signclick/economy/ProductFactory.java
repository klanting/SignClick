package com.klanting.signclick.economy;

import com.klanting.signclick.SignClick;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class ProductFactory {
    public static Product create(Material material, String companyType){

        ConfigurationSection section = SignClick.getPlugin().getConfig().getConfigurationSection("products").
                getConfigurationSection(companyType).getConfigurationSection(material.name());

        return new Product(material, section.getInt("productionCost"), section.getInt("productionTime"));
    }
}
