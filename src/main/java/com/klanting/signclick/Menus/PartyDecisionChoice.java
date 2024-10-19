package com.klanting.signclick.Menus;

import com.klanting.signclick.economy.decisions.Decision;
import com.klanting.signclick.economy.parties.Party;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PartyDecisionChoice implements InventoryHolder {
    private Inventory menu;

    public Party p;
    public Decision d;

    public PartyDecisionChoice(Party p, Decision d){
        menu = Bukkit.createInventory(this, 27, "Party Choice");
        this.p = p;
        this.d = d;
        init();
    }

    public void init(){
        ItemStack approve = new ItemStack(Material.LIME_WOOL);
        ItemMeta m = approve.getItemMeta();
        m.setDisplayName("§aPro");
        approve.setItemMeta(m);
        menu.setItem(11, approve);

        ItemStack dis_approve = new ItemStack(Material.RED_WOOL);
        m = dis_approve.getItemMeta();
        m.setDisplayName("§cContra");
        dis_approve.setItemMeta(m);
        menu.setItem(15, dis_approve);

    }

    @Override
    public Inventory getInventory() {
        return menu;
    }
}
