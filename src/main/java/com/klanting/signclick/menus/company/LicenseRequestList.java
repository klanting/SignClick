package com.klanting.signclick.menus.company;

import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.License;
import com.klanting.signclick.economy.LicenseSingleton;
import com.klanting.signclick.economy.Product;
import com.klanting.signclick.menus.PagingMenu;
import com.klanting.signclick.utils.ItemFactory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class LicenseRequestList extends PagingMenu {

    public final Company comp;

    public final Function<License, Void> func;

    public LicenseRequestList(Company comp, Function<License, Void> func){
        super(54, "License Request List", true);
        this.comp = comp;
        this.func = func;

        init();
    }

    public void init(){
        clearItems();

        for (License license: LicenseSingleton.getInstance().getLicenseRequests().getLicensesFrom(comp)){

            Product product = license.getProduct();

            List<String> l = new ArrayList<>();
            l.add("§7Production Time: "+product.getProductionTime()+"s");
            l.add("§7Cost: $"+product.getPrice());
            ItemStack item = ItemFactory.create(product.getMaterial(), "§7"+product.getMaterial().name(), l);
            addItem(item);
        }

        super.init();
    }
}


