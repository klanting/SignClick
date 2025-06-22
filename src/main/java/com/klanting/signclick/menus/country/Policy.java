package com.klanting.signclick.menus.country;

import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.menus.SelectionMenu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Policy extends SelectionMenu {

    private final UUID uuid;

    public Policy(UUID uuid){
        super(54, "Country Policy", true);
        this.uuid = uuid;
        init();
    }

    public void init(){
        int startIndex = 11;
        Country country = CountryManager.getCountry(uuid);
        for (com.klanting.signclick.economy.policies.Policy p: country.getPolicies()){
            ItemStack item = new ItemStack(p.getMaterial());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§6"+p.getName());
            item.setItemMeta(meta);

            getInventory().setItem(startIndex-1, item);
            for (int i=0; i<5; i++){

                ItemStack color;
                if (i != p.getLevel()){
                    color = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                }else{
                    color = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                }

                ItemMeta m = color.getItemMeta();

                int visual_pct = 51;
                if (i == 0 || i == 4){
                    visual_pct = 70;
                }

                m.setDisplayName("§6"+p.getTitle(i));

                List<String> lore_list = new ArrayList<>();
                lore_list.add("§9"+visual_pct+"% Approval needed");

                for (List<String> s: p.getDescription()){
                    String d = s.get(i);
                    if (d != ""){
                        lore_list.add(d);
                    }

                }
                m.setLore(lore_list);
                color.setItemMeta(m);

                getInventory().setItem(startIndex+i, color);

            }

            startIndex += 9;
        }

        super.init();
    }

}
