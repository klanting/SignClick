package com.klanting.signclick.menus.company;

import com.klanting.signclick.economy.Company;
import com.klanting.signclick.menus.SelectionMenu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PatentSelectorMenu extends SelectionMenu {

    public Company comp;

    public PatentSelectorMenu(Company comp){
        super(27, "Company Patent Selector", true);
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

        super.init();
    }

}
