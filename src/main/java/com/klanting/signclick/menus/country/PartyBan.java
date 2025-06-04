package com.klanting.signclick.menus.country;

import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.parties.Party;
import com.klanting.signclick.menus.SelectionMenu;
import org.bukkit.Material;
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
    }

}
