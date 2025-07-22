package com.klanting.signclick.menus.company;

import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.economy.License;
import com.klanting.signclick.economy.LicenseSingleton;
import com.klanting.signclick.economy.Product;
import com.klanting.signclick.menus.PagingMenu;
import com.klanting.signclick.utils.ItemFactory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class LicenseGivenList extends PagingMenu {

    public final CompanyI comp;

    public final Function<License, Void> func;

    public LicenseGivenList(CompanyI comp, Function<License, Void> func){
        super(54, "License Request List", true);
        this.comp = comp;
        this.func = func;

        init();
    }

    public void init(){
        clearItems();

        for (License license: LicenseSingleton.getInstance().getCurrentLicenses().getLicensesFrom(comp)){

            Product product = license.getProduct();

            List<String> l = new ArrayList<>();
            l.add("§7Production Time: "+product.getProductionTime()+"s");
            l.add("§7Cost: $"+product.getPrice());
            l.add("§7Given to: "+license.getTo().getStockName());
            ItemStack item = ItemFactory.create(product.getMaterial(), "§7"+product.getMaterial().name(), l);
            addItem(item);
        }

        super.init();
    }

    public boolean onClick(InventoryClickEvent event){
        if (!super.onClick(event)){
            return false;
        }
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);

        if(event.getCurrentItem().getType().equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE)){
            return false;
        }

        int item = event.getSlot();
        if (event.getSlot() < 45){
            int index = (getPage()*45+item);
            List<License> licenses = LicenseSingleton.getInstance().getCurrentLicenses().
                    getLicensesFrom(comp);
            func.apply(licenses.get(index));

            init();
            return false;
        }
        return true;
    }
}


