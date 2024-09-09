package com.klanting.signclick.Menus;

import com.klanting.signclick.Economy.Company;
import com.klanting.signclick.Economy.Market;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CompanyOwnerMenu implements InventoryHolder {
    private Inventory menu;
    public Company comp;

    public CompanyOwnerMenu(UUID uuid, Company company){
        menu = Bukkit.createInventory(this, 54, "Company Menu");
        comp = company;
        if (!comp.is_owner(uuid)){
            return;
        }

        init();
    }

    public void init(){
        ItemStack value = new ItemStack(Material.GOLD_BLOCK, 1);
        ItemMeta m = value.getItemMeta();
        DecimalFormat df = new DecimalFormat("###,###,###");
        ArrayList<String> l = new ArrayList<>();
        l.add("§6Value: §9"+ df.format(comp.get_value()));
        l.add("§6Spendable: §9"+ df.format(comp.get_spendable()));
        l.add("§6Points: §9"+ df.format(comp.security_funds));
        l.add("§6Type: §9"+ comp.type);

        m.setDisplayName("§6Balance");
        m.setLore(l);
        value.setItemMeta(m);
        menu.setItem(13, value);

        value = new ItemStack(Material.EMERALD, 1);
        m = value.getItemMeta();
        m.setDisplayName("§6Upgrades");
        value.setItemMeta(m);
        menu.setItem(22, value);

        value = new ItemStack(Material.NETHERITE_HELMET, 1);
        m = value.getItemMeta();
        m.setDisplayName("§6Patent");
        value.setItemMeta(m);
        menu.setItem(21, value);

        value = new ItemStack(Material.GOLD_NUGGET, 1);
        m = value.getItemMeta();
        m.setDisplayName("§6Auction");
        value.setItemMeta(m);
        menu.setItem(23, value);

        value = new ItemStack(Material.CRAFTING_TABLE, 1);
        m = value.getItemMeta();
        m.setDisplayName("§6Recipes");
        value.setItemMeta(m);
        menu.setItem(30, value);


        if (comp.type.equals("other")){
            value = new ItemStack(Material.SUNFLOWER, 1);
            m = value.getItemMeta();
            m.setDisplayName("§6Type");
            value.setItemMeta(m);
            menu.setItem(8, value);
        }

    }

    @Override
    public Inventory getInventory() {
        return menu;
    }
}
