package com.klanting.signclick.Menus;

import com.klanting.signclick.economy.parties.Election;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CountryElectionMenu implements InventoryHolder {
    private Inventory menu;
    public Election e;
    public CountryElectionMenu(Election e){
        menu = Bukkit.createInventory(this, 27, "Country Vote");
        this.e = e;
        init();
    }

    public void init(){
        for (String name: e.voteDict.keySet()){

            ItemStack click = new ItemStack(Material.PAPER, 1);
            ItemMeta m = click.getItemMeta();
            m.setDisplayName("§6"+name);
            click.setItemMeta(m);
            menu.setItem(menu.firstEmpty(), click);
        }
    }

    @Override
    public Inventory getInventory() {
        return menu;
    }
}
