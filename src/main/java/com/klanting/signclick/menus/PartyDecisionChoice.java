package com.klanting.signclick.menus;

import com.klanting.signclick.economy.decisions.Decision;
import com.klanting.signclick.economy.parties.Party;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PartyDecisionChoice extends SelectionMenu {

    public Party p;
    public Decision d;

    public PartyDecisionChoice(Party p, Decision d){
        super(27, "Party Choice", true);
        this.p = p;
        this.d = d;
        init();
    }

    public void init(){
        ItemStack approve = new ItemStack(Material.LIME_WOOL);
        ItemMeta m = approve.getItemMeta();
        m.setDisplayName("§aPro");
        approve.setItemMeta(m);
        getInventory().setItem(11, approve);

        ItemStack disApprove = new ItemStack(Material.RED_WOOL);
        m = disApprove.getItemMeta();
        m.setDisplayName("§cContra");
        disApprove.setItemMeta(m);
        getInventory().setItem(15, disApprove);

    }
}
