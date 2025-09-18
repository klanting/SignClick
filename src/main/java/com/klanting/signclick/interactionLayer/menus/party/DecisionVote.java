package com.klanting.signclick.interactionLayer.menus.party;

import java.util.List;
import java.util.ArrayList;
import java.text.DecimalFormat;

import com.klanting.signclick.logicLayer.countryLogic.Country;
import com.klanting.signclick.logicLayer.countryLogic.CountryManager;
import com.klanting.signclick.logicLayer.decisions.Decision;
import com.klanting.signclick.logicLayer.parties.Party;

import com.klanting.signclick.interactionLayer.menus.PagingMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class DecisionVote extends PagingMenu {

    public Party p;

    public DecisionVote(Party p){
        super(27, "Party Vote", true);
        this.p = p;
        init();
    }

    public void init(){
        Country country = CountryManager.getCountry(p.country);

        clearItems();

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

            addItem(decisionItem);
        }

        super.init();
    }

    public boolean onClick(InventoryClickEvent event){
        if (!super.onClick(event)){
            return false;
        }
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();

        int slot = getItemIndex(event.getCurrentItem());
        assert slot != -1;

        String countryName = p.country;
        Country country = CountryManager.getCountry(countryName);

        Decision d = country.getDecisions().get(slot);

        DecisionChoice new_screen = new DecisionChoice(p, d);
        player.openInventory(new_screen.getInventory());
        return true;
    }
}
