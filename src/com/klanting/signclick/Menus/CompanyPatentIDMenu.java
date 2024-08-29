package com.klanting.signclick.Menus;

import com.klanting.signclick.Economy.Company;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class CompanyPatentIDMenu implements InventoryHolder {
    private Inventory menu;

    public Company comp;

    public Boolean designer;


    public CompanyPatentIDMenu(Company comp, Boolean designer){
        menu = Bukkit.createInventory(this, 27, "Company Patent Selector");
        this.comp = comp;
        this.designer = designer;
        init();
    }

    private void init(){
        int size = comp.upgrades.get(1).getBonus();
        for (int i=0; i<size; i++){

            ItemStack parent;
            if (comp.patent.size() > i){
                parent = new ItemStack(comp.patent.get(i).item,1);

                ItemMeta m = parent.getItemMeta();
                m.setDisplayName("§6"+comp.patent.get(i).getName());
                parent.setItemMeta(m);
            }else{
                parent = new ItemStack(Material.LIGHT_GRAY_DYE,1);

                ItemMeta m = parent.getItemMeta();
                m.setDisplayName("§6Empty Patent");
                parent.setItemMeta(m);

            }

            menu.setItem(menu.firstEmpty(), parent);

        }
    }
    @Override
    public Inventory getInventory() {
        return menu;
    }
}
