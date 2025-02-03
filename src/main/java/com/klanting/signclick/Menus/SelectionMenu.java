package com.klanting.signclick.Menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Stack;

abstract public class SelectionMenu implements InventoryHolder {
    private final Inventory menu;

    public SelectionMenu(int size, String title, boolean backButton){
        this(size, title, backButton, size-1);
    }

    public SelectionMenu(int size, String title, boolean backButton, int backPosition){
        menu = Bukkit.createInventory(this, size, title);

        if (backButton){
            ItemStack back = new ItemStack(Material.BARRIER);
            ItemMeta itemMeta = back.getItemMeta();
            itemMeta.setDisplayName("§cBack");
            back.setItemMeta(itemMeta);
            menu.setItem(backPosition, back);
        }

    }

    @Override
    public @NotNull Inventory getInventory() {
        return menu;
    }

    abstract public void init();
}
