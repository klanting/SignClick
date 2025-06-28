package com.klanting.signclick.economy;

import org.bukkit.Material;

public class Product {

    public Material getMaterial() {
        return material;
    }

    private final Material material;
    private final int price;

    private final long productionTime;

    public Product(Material material, int price, long productionTime){
        this.material = material;
        this.price = price;
        this.productionTime = productionTime;
    }
}
