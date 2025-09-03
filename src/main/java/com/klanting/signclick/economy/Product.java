package com.klanting.signclick.economy;

import org.bukkit.Material;

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

    public Product(Material material, double price, int productionTime){
        this.material = material;
        this.price = price;
        this.productionTime = productionTime;
    }
}
