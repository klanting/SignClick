package com.klanting.signclick.Menus;

import com.klanting.signclick.economy.Company;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

public class CompanyOwnerMenu extends SelectionMenu {
    public Company comp;

    public CompanyOwnerMenu(UUID uuid, Company company){
        super(54, "Company Menu");
        comp = company;

        if(!comp.getCOM().isOwner(uuid)){
            return;
        }
        init();
    }

    public void init(){
        ItemStack value = new ItemStack(Material.GOLD_BLOCK, 1);
        ItemMeta m = value.getItemMeta();
        DecimalFormat df = new DecimalFormat("###,###,###");
        ArrayList<String> l = new ArrayList<>();
        l.add("§6Value: §9"+ df.format(comp.getValue()));
        l.add("§6Spendable: §9"+ df.format(comp.getSpendable()));
        l.add("§6Points: §9"+ df.format(comp.getSecurityFunds()));
        l.add("§6Type: §9"+ comp.type);

        m.setDisplayName("§6Balance");
        m.setLore(l);
        value.setItemMeta(m);
        getInventory().setItem(13, value);

        value = new ItemStack(Material.EMERALD, 1);
        m = value.getItemMeta();
        m.setDisplayName("§6Upgrades");
        value.setItemMeta(m);
        getInventory().setItem(22, value);

        value = new ItemStack(Material.NETHERITE_HELMET, 1);
        m = value.getItemMeta();
        m.setDisplayName("§6Patent");
        value.setItemMeta(m);
        getInventory().setItem(21, value);

        value = new ItemStack(Material.GOLD_NUGGET, 1);
        m = value.getItemMeta();
        m.setDisplayName("§6Auction");
        value.setItemMeta(m);
        getInventory().setItem(23, value);

        value = new ItemStack(Material.CRAFTING_TABLE, 1);
        m = value.getItemMeta();
        m.setDisplayName("§6Recipes");
        value.setItemMeta(m);
        getInventory().setItem(30, value);

        if (comp.type.equals("other")){
            value = new ItemStack(Material.SUNFLOWER, 1);
            m = value.getItemMeta();
            m.setDisplayName("§6Type");
            value.setItemMeta(m);
            getInventory().setItem(8, value);
        }

    }
}
