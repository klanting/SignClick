package com.klanting.signclick.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemFactory {

    public static ItemStack create(Material material, String displayName, List<String> lore){
        ItemStack item = new ItemStack(material, 1);
        ItemMeta m = item.getItemMeta();
        m.setDisplayName(displayName);
        m.setLore(lore);
        item.setItemMeta(m);

        return item;
    }

    public static ItemStack create(Material material, String displayName){
        List<String> lore = new ArrayList<>();

        return create(material, displayName, lore);
    }

    public static ItemStack createGray(){
        return create(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "§f");
    }
}
