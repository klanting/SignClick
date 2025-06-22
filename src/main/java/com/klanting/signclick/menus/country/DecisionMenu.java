package com.klanting.signclick.menus.country;

import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.menus.SelectionMenu;
import com.klanting.signclick.utils.ItemFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DecisionMenu extends SelectionMenu {
    private UUID uuid;

    public DecisionMenu(UUID uuid){
        super(27, "Country Decision Menu", true);
        this.uuid = uuid;
        init();
    }

    public void init(){
        ItemStack value;

        List<String> lores = new ArrayList<>();
        lores.add("§7REQUIRES 40 stability");
        value = ItemFactory.create(Material.RED_BANNER, "§6Ban party", lores);
        getInventory().setItem(12, value);

        String name = "§6Forbid party";
        Country country = CountryManager.getCountry(uuid);

        if (country.isForbidParty()){
            name = "§6Allow party";
        }

        lores = new ArrayList<>();
        lores.add("§7REQUIRES 30 stability");
        value = ItemFactory.create(Material.IRON_BARS, name, lores);
        getInventory().setItem(13, value);

        name = "§6Abort military payments";

        if (country.isAboardMilitary()){
            name = "§6Allow military payments";
        }
        value = ItemFactory.create(Material.IRON_SWORD, name);
        getInventory().setItem(14, value);

        super.init();
    }

}
