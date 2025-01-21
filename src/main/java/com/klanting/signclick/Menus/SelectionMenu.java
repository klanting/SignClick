package com.klanting.signclick.Menus;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

abstract public class SelectionMenu implements InventoryHolder {
    private final Inventory menu;

    public SelectionMenu(int size, String title){
        menu = Bukkit.createInventory(this, size, title);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return menu;
    }

    abstract public void init();
}
