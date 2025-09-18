package com.klanting.signclick.interactionLayer.menus.party;

import com.klanting.signclick.logicLayer.countryLogic.decisions.Decision;
import com.klanting.signclick.logicLayer.countryLogic.parties.Party;
import com.klanting.signclick.interactionLayer.menus.SelectionMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DecisionChoice extends SelectionMenu {

    public Party p;
    public Decision d;

    public DecisionChoice(Party p, Decision d){
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

    public boolean onClick(InventoryClickEvent event){
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        d.vote(p, slot == 11);

        player.closeInventory();

        return true;
    }
}
