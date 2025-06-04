package com.klanting.signclick.menus.party;

import java.util.List;
import java.util.ArrayList;
import java.text.DecimalFormat;

import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.decisions.Decision;
import com.klanting.signclick.economy.parties.Party;

import com.klanting.signclick.menus.SelectionMenu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class DecisionVote extends SelectionMenu {

    public Party p;

    public DecisionVote(Party p){
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

            ItemStack decisionItem = new ItemStack(Material.PAPER, 1);
            ItemMeta m = decisionItem.getItemMeta();
            List<String> lores = new ArrayList<>();
            DecimalFormat df = new DecimalFormat("##0.00");
            lores.add("§7current approved: "+df.format(d.getApproved())+"%");
            lores.add("§7current disapproved: "+df.format(d.getDisapproved())+"%");
            lores.add("§7needed approved: "+df.format(d.needed*100)+"%");
            m.setLore(lores);
            m.setDisplayName(d.name);
            decisionItem.setItemMeta(m);
            getInventory().setItem(getInventory().firstEmpty(), decisionItem);
        }
    }
}
