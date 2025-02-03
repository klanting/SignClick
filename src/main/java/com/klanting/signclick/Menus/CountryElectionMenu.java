package com.klanting.signclick.Menus;

import com.klanting.signclick.economy.parties.Election;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CountryElectionMenu extends SelectionMenu {
    public Election e;
    public CountryElectionMenu(Election e){
        super(27, "Country Vote", true);
        this.e = e;
        init();
    }

    public void init(){
        for (String name: e.voteDict.keySet()){

            ItemStack click = new ItemStack(Material.PAPER, 1);
            ItemMeta m = click.getItemMeta();
            m.setDisplayName("§6"+name);
            click.setItemMeta(m);
            getInventory().setItem(getInventory().firstEmpty(), click);
        }
    }

}
