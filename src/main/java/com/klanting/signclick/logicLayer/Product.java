package com.klanting.signclick.logicLayer;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class Product extends Produceable{

    public Material getMaterial() {
        return material;
    }

    private final Material material;

    public double getPrice() {
        return price;
    }

    public int getProductionTime() {
        return productionTime;
    }

    private final double price;

    private final int productionTime;

    private List<Product> usedFor = new ArrayList<>();

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
