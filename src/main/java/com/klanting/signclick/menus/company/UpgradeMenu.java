package com.klanting.signclick.menus.company;

import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.companyUpgrades.Upgrade;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.menus.SelectionMenu;
import com.klanting.signclick.utils.ItemFactory;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

public class UpgradeMenu extends SelectionMenu {

    public CompanyI comp;

    public UpgradeMenu(UUID uuid, CompanyI company){
        super(27, "Company Upgrade Menu", true);
        comp = company;

        init();
    }

    public void init(){
        int counter = 10;
        for (Upgrade up: comp.getUpgrades()){
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

            l.addAll(up.description());

            ItemStack value = ItemFactory.create(up.material, "§6"+up.name +" Lvl. §c"+ up.level, l);

            getInventory().setItem(counter, value);
            counter ++;
        }

        super.init();
    }
}
