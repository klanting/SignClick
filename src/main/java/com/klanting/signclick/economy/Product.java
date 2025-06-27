package com.klanting.signclick.economy;

import org.bukkit.Material;

public class Product {

    private final Material material;
    private final int price;

    private final int productionTime;

    public Product(Material material, int price, int productionTime){
        this.material = material;
        this.price = price;
        this.productionTime = productionTime;
    }
}
