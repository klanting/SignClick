package com.klanting.signclick.menus.country;

import com.klanting.signclick.economy.parties.Election;
import com.klanting.signclick.menus.SelectionMenu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ElectionMenu extends SelectionMenu {
    public Election e;
    public ElectionMenu(Election e){
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
