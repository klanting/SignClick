package com.klanting.signclick.Menus;

import com.klanting.signclick.Economy.Company;
import com.klanting.signclick.Economy.CompanyPatent.Patent;
import com.klanting.signclick.Economy.CompanyPatent.PatentUpgrade;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class CompanyPatentDesignerMenu implements InventoryHolder {
    private Inventory menu;

    public Company comp;

    public Patent patent;

    public CompanyPatentDesignerMenu(Patent patent, Company comp){
        menu = Bukkit.createInventory(this, 27, "Company Patent Designer");
        this.comp = comp;
        this.patent = patent;
        init();
    }
    public void init(){
        ItemStack item = new ItemStack(patent.item, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(patent.getName());
        item.setItemMeta(meta);
        menu.setItem(4, item);
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



            menu.setItem(18+i, upgrade);
        }

        ItemStack save_button = new ItemStack(Material.LIME_WOOL, 1);
        ItemMeta m = save_button.getItemMeta();
        m.setDisplayName("§aSave");
        save_button.setItemMeta(m);
        menu.setItem(8, save_button);
    }

    @Override
    public Inventory getInventory() {
        return menu;
    }
}
