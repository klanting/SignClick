package com.klanting.signclick.Menus;

import com.klanting.signclick.economy.Company;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class CompanyPatentSelectorMenu extends SelectionMenu {

    public Company comp;

    public CompanyPatentSelectorMenu(Company comp){
        super(27, "Company Patent Selector");
        this.comp = comp;
        init();
    }

    public void init(){
        ItemStack item;
        item = new ItemStack(Material.NETHERITE_HELMET,1);
        getInventory().setItem(10, item);

        item = new ItemStack(Material.NETHERITE_CHESTPLATE,1);
        getInventory().setItem(12, item);

        item = new ItemStack(Material.NETHERITE_LEGGINGS,1);
        getInventory().setItem(14, item);

        item = new ItemStack(Material.NETHERITE_BOOTS,1);
        getInventory().setItem(16, item);


    }
}
