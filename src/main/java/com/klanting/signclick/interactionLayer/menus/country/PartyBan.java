package com.klanting.signclick.interactionLayer.menus.country;

import com.klanting.signclick.logicLayer.Country;
import com.klanting.signclick.logicLayer.CountryManager;
import com.klanting.signclick.logicLayer.decisions.Decision;
import com.klanting.signclick.logicLayer.decisions.DecisionBanParty;
import com.klanting.signclick.logicLayer.parties.Party;
import com.klanting.signclick.interactionLayer.menus.SelectionMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class PartyBan extends SelectionMenu {
    private UUID uuid;

    public PartyBan(UUID uuid){
        super(27, "Country Decision Menu", true);
        this.uuid = uuid;
        init();
    }

    public void init(){
        Country country = CountryManager.getCountry(uuid);
        for (Party p: country.getParties()){
            String name = p.name;

            ItemStack value;
            ItemMeta m;
            value = new ItemStack(Material.WHITE_BANNER, 1);
            m = value.getItemMeta();
            m.setDisplayName("§6"+name);
            value.setItemMeta(m);
            getInventory().setItem(getInventory().firstEmpty(), value);
        }

        super.init();
    }

    public boolean onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);
        int slot = event.getSlot();
        Country country = CountryManager.getCountry(player);

        Party p = country.getParties().get(slot);
        Party ph = country.getRuling();
        if (ph == p){
            player.sendMessage("§bcan`t ban ruling party");
            return false;
        }

        if (country.getStability() < 40.0){
            player.sendMessage("§brequired stability is 40");
            return false;
        }

        Decision d = new DecisionBanParty("§6Ban Party §9"+p.name, 0.5, country.getName(), p);
        country.addDecision(d);

        player.closeInventory();

        return true;
    }

}
