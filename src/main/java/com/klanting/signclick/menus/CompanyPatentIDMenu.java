package com.klanting.signclick.menus;

import com.klanting.signclick.economy.Company;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CompanyPatentIDMenu extends SelectionMenu {

    public Company comp;

    public Boolean designer;


    public CompanyPatentIDMenu(Company comp, Boolean designer){
        super(27, "Company Patent Selector", true);
        this.comp = comp;
        this.designer = designer;
        init();
    }

    public void init(){
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

            getInventory().setItem(getInventory().firstEmpty(), parent);

        }
    }
}
