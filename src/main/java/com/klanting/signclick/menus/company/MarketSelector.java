package com.klanting.signclick.menus.company;

import com.klanting.signclick.economy.Account;
import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.menus.SelectionMenu;
import com.klanting.signclick.utils.Utils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
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
            m.setDisplayName("§b"+c.getStockName());

            DecimalFormat df2 = new DecimalFormat("0.00");

            Integer shares = Market.getAccount(uuid).shares.getOrDefault(c.getStockName(), 0);

            List<String> lore = new ArrayList<>();
            lore.add("§7Owned Shares: "
                    + shares
                    + " ("+df2.format((shares.doubleValue()/c.getTotalShares().doubleValue()*100.0))+"%)\n");
            m.setLore(lore);
            item.setItemMeta(m);
            getInventory().setItem(getInventory().firstEmpty(), item);
        }
    }
}
