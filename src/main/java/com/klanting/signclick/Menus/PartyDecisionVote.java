package com.klanting.signclick.Menus;

import com.klanting.signclick.Economy.Country;
import com.klanting.signclick.Economy.Decisions.Decision;
import com.klanting.signclick.Economy.Parties.Party;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PartyDecisionVote implements InventoryHolder {

    private Inventory menu;

    public Party p;

    public PartyDecisionVote(Party p){
        menu = Bukkit.createInventory(this, 27, "Party Vote");
        this.p = p;
        init();
    }

    public void init(){
        List<Decision> des = Country.decisions.getOrDefault(p.country, new ArrayList<>());
        for (Decision d: des){
            if (d.hasVoted(p)){
                continue;
            }

            ItemStack d_item = new ItemStack(Material.PAPER, 1);
            ItemMeta m = d_item.getItemMeta();
            List<String> lores = new ArrayList<>();
            DecimalFormat df = new DecimalFormat("##0.00");
            lores.add("§7current approved: "+df.format(d.getApproved())+"%");
            lores.add("§7current disapproved: "+df.format(d.getDisapproved())+"%");
            lores.add("§7needed approved: "+df.format(d.needed*100)+"%");
            m.setLore(lores);
            m.setDisplayName(d.name);
            d_item.setItemMeta(m);
            menu.setItem(menu.firstEmpty(), d_item);
        }
    }

    @Override
    public Inventory getInventory() {
        return menu;
    }
}
