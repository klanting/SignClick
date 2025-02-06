package com.klanting.signclick.Menus;

import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CompanyMarketSelector extends SelectionMenu{
    public UUID uuid;

    public CompanyMarketSelector(UUID uuid){
        super(54, "Company Market Selector", false);
        this.uuid = uuid;
        init();
    }

    @Override
    public void init() {
        ItemStack item;
        for(Company c: Market.getTopMarketAvailable()){

            item = new ItemStack(Utils.getCompanyTypeMaterial(c.type),1);
            ItemMeta m = item.getItemMeta();
            m.setDisplayName(c.getStockName());
            item.setItemMeta(m);
            getInventory().setItem(getInventory().firstEmpty(), item);
        }
    }
}
