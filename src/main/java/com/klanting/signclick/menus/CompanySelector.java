package com.klanting.signclick.menus;

import com.klanting.signclick.economy.Company;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.klanting.signclick.economy.Market;

import java.util.UUID;

public class CompanySelector extends SelectionMenu {

    final private UUID uuid;

    public CompanySelector(UUID uuid){
        super(54, "Company Selector", false);
        this.uuid = uuid;
        init();
    }

    public void init(){
        ItemStack item;
        for(Company c: Market.getBusinessByOwner(uuid)){
            item = new ItemStack(Material.DIAMOND_BLOCK,1);
            ItemMeta m = item.getItemMeta();
            m.setDisplayName(c.getStockName());
            item.setItemMeta(m);
            getInventory().setItem(getInventory().firstEmpty(), item);
        }


    }

}
