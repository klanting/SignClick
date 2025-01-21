package com.klanting.signclick.Menus;

import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.parties.Party;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class CountryPartyBan extends SelectionMenu {
    private UUID uuid;

    public CountryPartyBan(UUID uuid){
        super(27, "Country Decision Menu");
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
