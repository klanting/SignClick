package com.klanting.signclick.menus.company;

import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.economy.companyPatent.Patent;
import com.klanting.signclick.economy.companyPatent.PatentUpgrade;
import com.klanting.signclick.menus.SelectionMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class PatentSelectorMenu extends SelectionMenu {

    public CompanyI comp;

    public PatentSelectorMenu(CompanyI comp){
        super(27, "Company Patent Selector", true);
        this.comp = comp;
        init();
    }

    public void init(){
        ItemStack item;
        item = new ItemStack(Material.NETHERITE_HELMET,1);
        getInventory().setItem(10, item);

        item = new ItemStack(Material.NETHERITE_CHESTPLATE,1);
        getInventory().setItem(12, item);

        item = new ItemStack(Material.NETHERITE_LEGGINGS,1);
        getInventory().setItem(14, item);

        item = new ItemStack(Material.NETHERITE_BOOTS,1);
        getInventory().setItem(16, item);

        super.init();
    }

    public boolean onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);
        ItemStack item = event.getCurrentItem();

        Patent pat = new Patent("Nameless", item.getType(), new ArrayList<>());
        PatentDesignerMenu new_screen = new PatentDesignerMenu(pat, comp);
        player.openInventory(new_screen.getInventory());

        return true;
    }

}
