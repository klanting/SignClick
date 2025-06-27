package com.klanting.signclick.economy;

import org.bukkit.Material;

public class ProductFactory {
    public static Product create(Material material){
        return new Product(Material.DIRT, 1, 10);
    }
}
