package com.klanting.signclick.economy;

import org.bukkit.Material;

public class Product {

    public Material getMaterial() {
        return material;
    }

    private final Material material;

    public int getPrice() {
        return price;
    }

    public int getProductionTime() {
        return productionTime;
    }

    private final int price;

    private final int productionTime;

    public Product(Material material, int price, int productionTime){
        this.material = material;
        this.price = price;
        this.productionTime = productionTime;
    }
}
