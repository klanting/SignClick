package com.klanting.signclick.logicLayer.companyLogic.producible;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class ProductFactory {
    public static Product create(Material material, CompanyI company){

        ConfigurationSection section = SignClick.getConfigManager().getConfig("production.yml").getConfigurationSection("products").
                getConfigurationSection(company.getType()).getConfigurationSection(material.name());

        return new Product(material, section.getDouble("productionCost"), section.getInt("productionTime"), company.getRef());
    }
}
