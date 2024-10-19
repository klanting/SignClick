package com.klanting.signclick.Menus;

import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CountryDecisionMenu implements InventoryHolder {
    private Inventory menu;
    private UUID uuid;

    public CountryDecisionMenu(UUID uuid){
        menu = Bukkit.createInventory(this, 27, "Country Decision Menu");
        this.uuid = uuid;
        init();
    }

    public void init(){
        ItemStack value;
        ItemMeta m;
        value = new ItemStack(Material.RED_BANNER, 1);
        m = value.getItemMeta();
        m.setDisplayName("§6Ban party");
        List<String> lores = new ArrayList<>();
        lores.add("§7REQUIRES 40 stability");
        m.setLore(lores);
        value.setItemMeta(m);
        menu.setItem(12, value);

        value = new ItemStack(Material.IRON_BARS, 1);
        m = value.getItemMeta();

        String name = "§6Forbid party";
        Country country = CountryManager.getCountry(uuid);

        if (country.isForbidParty()){
            name = "§6Allow party";
        }

        lores = new ArrayList<>();
        lores.add("§7REQUIRES 30 stability");
        m.setLore(lores);

        m.setDisplayName(name);
        value.setItemMeta(m);
        menu.setItem(13, value);

        value = new ItemStack(Material.IRON_SWORD, 1);
        m = value.getItemMeta();
        name = "§6Abort military payments";

        if (country.isAboardMilitary()){
            name = "§6Allow military payments";
        }
        m.setDisplayName(name);
        value.setItemMeta(m);
        menu.setItem(14, value);
    }

    @Override
    public Inventory getInventory() {
        return menu;
    }
}
