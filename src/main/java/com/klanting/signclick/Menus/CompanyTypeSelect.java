package com.klanting.signclick.Menus;

import com.klanting.signclick.economy.Company;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CompanyTypeSelect extends SelectionMenu {

    public Company comp;

    public CompanyTypeSelect(Company company){
        super(9, "Company Type Select", true);
        comp = company;

        init();
    }

    public void init(){
        ItemStack value;
        ItemMeta m;
        value = new ItemStack(Material.GOLD_INGOT, 1);
        m = value.getItemMeta();
        m.setDisplayName("§6bank");
        value.setItemMeta(m);
        getInventory().setItem(getInventory().firstEmpty(), value);

        value = new ItemStack(Material.MINECART, 1);
        m = value.getItemMeta();
        m.setDisplayName("§6transport");
        value.setItemMeta(m);
        getInventory().setItem(getInventory().firstEmpty(), value);

        value = new ItemStack(Material.IRON_CHESTPLATE, 1);
        m = value.getItemMeta();
        m.setDisplayName("§6product");
        value.setItemMeta(m);
        getInventory().setItem(getInventory().firstEmpty(), value);

        value = new ItemStack(Material.QUARTZ_BLOCK, 1);
        m = value.getItemMeta();
        m.setDisplayName("§6real estate");
        value.setItemMeta(m);
        getInventory().setItem(getInventory().firstEmpty(), value);

        value = new ItemStack(Material.BOW, 1);
        m = value.getItemMeta();
        m.setDisplayName("§6military");
        value.setItemMeta(m);
        getInventory().setItem(getInventory().firstEmpty(), value);

        value = new ItemStack(Material.BRICKS, 1);
        m = value.getItemMeta();
        m.setDisplayName("§6building");
        value.setItemMeta(m);
        getInventory().setItem(getInventory().firstEmpty(), value);
    }

}
