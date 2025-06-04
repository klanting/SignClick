package com.klanting.signclick.menus;

import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.companyPatent.Auction;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.klanting.signclick.economy.Market;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.klanting.signclick.utils.Utils.getCompanyTypeMaterial;

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
            item = new ItemStack(getCompanyTypeMaterial(c.type),1);
            ItemMeta m = item.getItemMeta();
            m.setDisplayName("§b"+c.getStockName());

            List<String> lores = new ArrayList<>();
            DecimalFormat df = new DecimalFormat("###,###,###");
            lores.add("§7Type: "+c.type);
            lores.add("§7Value: "+df.format(c.getValue()));

            m.setLore(lores);
            item.setItemMeta(m);
            getInventory().setItem(getInventory().firstEmpty(), item);
        }


    }

}
