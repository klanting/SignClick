package com.klanting.signclick.Menus;

import java.util.List;
import java.util.ArrayList;
import java.text.DecimalFormat;

import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.decisions.Decision;
import com.klanting.signclick.economy.parties.Party;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class PartyDecisionVote extends SelectionMenu {

    public Party p;

    public PartyDecisionVote(Party p){
        super(27, "Party Vote", true);
        this.p = p;
        init();
    }

    public void init(){
        Country country = CountryManager.getCountry(p.country);
        for (Decision d: country.getDecisions()){
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
            getInventory().setItem(getInventory().firstEmpty(), d_item);
        }
    }
}
