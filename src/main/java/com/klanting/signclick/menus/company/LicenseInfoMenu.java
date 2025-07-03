package com.klanting.signclick.menus.company;

import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.economy.License;
import com.klanting.signclick.menus.SelectionMenu;
import com.klanting.signclick.utils.ItemFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class LicenseInfoMenu extends SelectionMenu {
    public License license;

    public LicenseInfoMenu(License license){
        super(9, "License info", true);
        this.license = license;

        init();
    }

    public void init(){

        ItemStack cancel = ItemFactory.create(Material.RED_WOOL, "§cCancel License");
        getInventory().setItem(7, cancel);

        super.init();
    }
}
