package com.klanting.signclick.menus.company;

import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.economy.companyPatent.Auction;
import com.klanting.signclick.economy.companyPatent.PatentUpgrade;
import com.klanting.signclick.menus.SelectionMenu;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AuctionMenu extends SelectionMenu {

    public CompanyI comp;

    public AuctionMenu(CompanyI comp){
        super(9, "Patent Upgrade Auction", true);
        this.comp = comp;
        init();

    }

    public void init(){
        for (int i = 0; i<Auction.getInstance().toBuy.size(); i++){
            PatentUpgrade up = Auction.getInstance().toBuy.get(i);
            ItemStack upgradeItem = new ItemStack(up.material, 1);
            ItemMeta m = upgradeItem.getItemMeta();
            List<String> lores = new ArrayList<>();
            DecimalFormat df = new DecimalFormat("###,###,###");
            lores.addAll(up.description());
            lores.add("§7Current Bid: "+df.format(Auction.getInstance().getBit(i)));
            String comp = Auction.getInstance().bitsOwner.get(i);
            if (comp == null){
                comp = "None";
            }
            lores.add("§7Bid by: "+ comp);
            m.setDisplayName(up.name+" "+up.level);
            m.setLore(lores);
            upgradeItem.setItemMeta(m);
            getInventory().setItem(i, upgradeItem);
        }

        super.init();
    }
}
