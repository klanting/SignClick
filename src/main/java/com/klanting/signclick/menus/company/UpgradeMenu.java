package com.klanting.signclick.menus.company;

import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.companyUpgrades.Upgrade;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.menus.SelectionMenu;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

public class UpgradeMenu extends SelectionMenu {

    public Company comp;

    public UpgradeMenu(UUID uuid, Company company){
        super(27, "Company Upgrade Menu", true);
        comp = company;
        if (!comp.getCOM().isOwner(uuid)){
            return;
        }

        init();
    }

     public void init(){
        int counter = 11;
        for (Upgrade up: comp.upgrades){
            ItemStack value = new ItemStack(up.material, 1);
            ItemMeta m = value.getItemMeta();
            m.setDisplayName("§6"+up.name +" Lvl. §c"+ up.level);
            ArrayList<String> l = new ArrayList<>();
            DecimalFormat df = new DecimalFormat("###,###,###");
            Country country = CountryManager.getCountry(comp.getCountry());

            double modifier = 0;
            if (country != null){
                modifier = country.getPolicyBonus(1, 3);
            }

            if (up.getUpgradeCost() != -1){
                l.add("§6Cost: §8"+ df.format((double) up.getUpgradeCost()*(1.0- modifier)));
            }
            if (up.getUpgradeCostPoints() != -1){
                l.add("§6Points: §8"+ df.format((double) up.getUpgradeCostPoints()*(1.0- modifier)));
            }


            m.setLore(l);
            value.setItemMeta(m);
            getInventory().setItem(counter, value);
            counter ++;
        }
    }
}
