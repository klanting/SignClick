package com.klanting.signclick.menus.company;

import com.klanting.signclick.economy.License;
import com.klanting.signclick.menus.SelectionMenu;
import com.klanting.signclick.utils.ItemFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class LicenseAcceptMenu extends SelectionMenu {
    public final License license;

    public LicenseAcceptMenu(License license){
        super(27, "License request decision", true);
        this.license = license;

        init();
    }

    public void init(){

        DecimalFormat df = new DecimalFormat("###,###,##0.00");

        List<String> l = new ArrayList<>();
        l.add("§7Production Time: "+license.getProduct().getProductionTime()+"s");
        l.add("§7Weekly License cost: $"+license.getWeeklyCost());
        l.add("");
        l.add("§7Normal Cost: $"+license.getProduct().getPrice());
        l.add("§7Increased Cost: "+df.format(license.getCostIncrease()*100)+"%");
        l.add("§7Royalty Fee: "+df.format(license.getRoyaltyFee()*100)+"%");
        l.add("§7Production Cost: $"+(license.getProduct().getPrice()*(1.0+license.getCostIncrease()+license.getRoyaltyFee())));
        ItemStack productItem = ItemFactory.create(license.getProduct().getMaterial(), "§7License Request", l);

        getInventory().setItem(13, productItem);

        getInventory().setItem(12, ItemFactory.create(Material.RED_WOOL, "§cDeny License"));
        getInventory().setItem(14, ItemFactory.create(Material.LIME_WOOL, "§aAccept License"));

        super.init();
    }
}
