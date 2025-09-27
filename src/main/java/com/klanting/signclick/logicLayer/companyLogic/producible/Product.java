package com.klanting.signclick.logicLayer.companyLogic.producible;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class Product extends Producible {

    public Material getMaterial() {
        return material;
    }

    private final Material material;

    public double getPrice() {

        if(company == null) {
            return price;
        }

        ConfigurationSection section = SignClick.getConfigManager().getConfig("production.yml").getConfigurationSection("products").
                getConfigurationSection(company.getType()).getConfigurationSection(material.name());

        if(section != null){
            return section.getDouble("productionCost");
        }


        return price;
    }

    public int getProductionTime() {
        return productionTime;
    }

    private final double price;

    private final int productionTime;

    private List<Product> usedFor = new ArrayList<>();

    private CompanyI company;

    public Product(Material material, double price, int productionTime, CompanyI company){
        this(material, price, productionTime);

        this.company = company;
    }

    public Product(Material material, double price, int productionTime){
        this.material = material;
        this.price = price;
        this.productionTime = productionTime;
    }

    public void addUsedFor(Product product){
        if(usedFor == null){
            usedFor = new ArrayList<>();
        }

        usedFor.add(product);
    }

    public void onDelete(CompanyI company){
        if(usedFor == null){
            usedFor = new ArrayList<>();
        }

        for(Product p: usedFor){
            p.onDelete(company);
        }

        company.getProducts().remove(this);
        usedFor = new ArrayList<>();
    }
}
