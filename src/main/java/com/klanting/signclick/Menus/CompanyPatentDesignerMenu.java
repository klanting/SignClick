package com.klanting.signclick.Menus;

import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.companyPatent.Patent;
import com.klanting.signclick.economy.companyPatent.PatentUpgrade;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CompanyPatentDesignerMenu extends SelectionMenu {

    public Company comp;

    public Patent patent;

    public CompanyPatentDesignerMenu(Patent patent, Company comp){
        super(27, "Company Patent Designer", true, 17);
        this.comp = comp;
        this.patent = patent;
        init();
    }
    public void init(){
        ItemStack item = new ItemStack(patent.item, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(patent.getName());
        item.setItemMeta(meta);
        getInventory().setItem(4, item);
        int upgrades = comp.upgrades.get(2).getBonus();
        for (int i=0; i<upgrades; i++){

            ItemStack upgrade;
            if (patent.upgrades.size() > i){
                PatentUpgrade pat_up = patent.upgrades.get(i);
                upgrade = new ItemStack(pat_up.material, 1);

                ItemMeta m = upgrade.getItemMeta();
                m.setDisplayName(pat_up.name + " "+pat_up.level);
                upgrade.setItemMeta(m);

            }else{
                upgrade = new ItemStack(Material.LIGHT_GRAY_DYE, 1);

                ItemMeta m = upgrade.getItemMeta();
                m.setDisplayName("§6Empty Upgrade");
                upgrade.setItemMeta(m);
            }



            getInventory().setItem(18+i, upgrade);
        }

        ItemStack save_button = new ItemStack(Material.LIME_WOOL, 1);
        ItemMeta m = save_button.getItemMeta();
        m.setDisplayName("§aSave");
        save_button.setItemMeta(m);
        getInventory().setItem(8, save_button);
    }
}
