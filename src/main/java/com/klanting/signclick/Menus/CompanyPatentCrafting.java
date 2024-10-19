package com.klanting.signclick.Menus;

import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.companyPatent.Patent;
import com.klanting.signclick.economy.companyPatent.PatentUpgrade;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class CompanyPatentCrafting implements InventoryHolder {

    private Inventory menu;

    private ArrayList<Integer> indexes = new ArrayList<>();

    public Company comp;

    public Patent patent;

    public CompanyPatentCrafting(Company comp, Patent patent){
        menu = Bukkit.createInventory(this, 27, "Company Upgrade Menu");
        ItemStack gear_item = new ItemStack(patent.item, 1);
        ItemMeta m = gear_item.getItemMeta();
        m.setDisplayName("§6"+comp.Sname+":"+patent.getName()+":"+comp.patent.indexOf(patent));
        gear_item.setItemMeta(m);

        menu.setItem(13, gear_item);
        this.comp = comp;
        this.patent = patent;
        indexes.add(3);
        indexes.add(4);
        indexes.add(5);
        indexes.add(12);
        indexes.add(14);
        indexes.add(21);
        indexes.add(22);
        indexes.add(23);
        int counter = 0;
        for (PatentUpgrade up: patent.upgrades){
            ItemStack item = new ItemStack(up.material, 1);
            menu.setItem(indexes.get(counter), item);
            counter++;
        }

        ItemStack item = new ItemStack(Material.PAPER, 1);

        m = item.getItemMeta();
        m.setDisplayName("§6Get Patent Sheet");
        item.setItemMeta(m);
        menu.setItem(8, item);


    }

    @Override
    public Inventory getInventory() {
        return menu;
    }
}
