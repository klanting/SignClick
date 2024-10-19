package com.klanting.signclick.Menus;

import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.companyPatent.Auction;
import com.klanting.signclick.economy.companyPatent.PatentUpgrade;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CompanyAuctionMenu implements InventoryHolder {

    private Inventory menu;

    public Company comp;

    public CompanyAuctionMenu(Company comp){
        this.comp = comp;
        menu = Bukkit.createInventory(this, 9, "Patent Upgrade Auction");
        init();

    }

    public void init(){
        for (int i = 0; i<Auction.toBuy.size(); i++){
            PatentUpgrade up = Auction.toBuy.get(i);
            ItemStack upgradeItem = new ItemStack(up.material, 1);
            ItemMeta m = upgradeItem.getItemMeta();
            List<String> lores = new ArrayList<>();
            DecimalFormat df = new DecimalFormat("###,###,###");
            lores.add("§7Current Bet: "+df.format(Auction.bits.get(i)));
            String comp = Auction.bitsOwner.get(i);
            if (comp == null){
                comp = "None";
            }
            lores.add("§7Bet by: "+ comp);
            m.setDisplayName(up.name+" "+up.level);
            m.setLore(lores);
            upgradeItem.setItemMeta(m);
            menu.setItem(i, upgradeItem);
        }
    }

    @Override
    public Inventory getInventory() {
        return menu;
    }
}
