package com.klanting.signclick.menus.company;

import com.klanting.signclick.economy.Company;
import com.klanting.signclick.menus.SelectionMenu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PatentIDMenu extends SelectionMenu {

    public Company comp;

    public Boolean designer;


    public PatentIDMenu(Company comp, Boolean designer){
        super(27, "Company Patent Selector", true);
        this.comp = comp;
        this.designer = designer;
        init();
    }

    public void init(){
        int size = comp.upgrades.get(0).getBonus();
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

        super.init();
    }
}
