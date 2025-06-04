package com.klanting.signclick.menus.company;

import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.menus.SelectionMenu;
import com.klanting.signclick.utils.Utils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class MarketSelector extends SelectionMenu {
    public UUID uuid;

    public MarketSelector(UUID uuid){
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
