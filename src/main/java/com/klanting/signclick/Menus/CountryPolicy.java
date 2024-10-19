package com.klanting.signclick.Menus;

import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.policies.Policy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CountryPolicy implements InventoryHolder {
    private Inventory menu;

    public CountryPolicy(UUID uuid){
        menu = Bukkit.createInventory(this, 54, "Country Policy");
        init(uuid);
    }

    public void init(UUID uuid){
        int start_index = 11;
        Country country = CountryManager.getCountry(uuid);
        for (Policy p: country.getPolicies()){

            menu.setItem(start_index-1, new ItemStack(p.getMaterial()));
            for (int i=0; i<5; i++){

                ItemStack color;
                if (i != p.getLevel()){
                    color = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                }else{
                    color = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                }

                ItemMeta m = color.getItemMeta();

                m.setDisplayName("§6"+p.getTitle(i));

                List<String> lore_list = new ArrayList<>();
                for (List<String> s: p.getDescription()){
                    String d = s.get(i);
                    if (d != ""){
                        lore_list.add(d);
                    }

                }
                m.setLore(lore_list);
                color.setItemMeta(m);

                menu.setItem(start_index+i, color);

            }

            start_index += 9;
        }


    }

    @Override
    public Inventory getInventory() {
        return menu;
    }
}
