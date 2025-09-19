package com.klanting.signclick.logicLayer.companyLogic;

import com.klanting.signclick.SignClick;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class ProductFactory {
    public static Product create(Material material, String companyType){

        ConfigurationSection section = SignClick.getConfigManager().getConfig("production.yml").getConfigurationSection("products").
                getConfigurationSection(companyType).getConfigurationSection(material.name());

        return new Product(material, section.getInt("productionCost"), section.getInt("productionTime"));
    }
}
