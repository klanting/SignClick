package com.klanting.signclick.Menus;

import com.klanting.signclick.economy.Company;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class CompanyPatentSelectorMenu implements InventoryHolder {
    private Inventory menu;

    public Company comp;

    public CompanyPatentSelectorMenu(UUID uuid, Company comp){
        menu = Bukkit.createInventory(this, 27, "Company Patent Selector");
        this.comp = comp;
        init();
    }

    private void init(){
        ItemStack item;
        item = new ItemStack(Material.NETHERITE_HELMET,1);
        menu.setItem(10, item);

        item = new ItemStack(Material.NETHERITE_CHESTPLATE,1);
        menu.setItem(12, item);

        item = new ItemStack(Material.NETHERITE_LEGGINGS,1);
        menu.setItem(14, item);

        item = new ItemStack(Material.NETHERITE_BOOTS,1);
        menu.setItem(16, item);


    }

    @Override
    public Inventory getInventory() {
        return menu;
    }
}
