package com.klanting.signclick.menus.company;

import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.economy.Product;
import com.klanting.signclick.menus.SelectionMenu;
import com.klanting.signclick.utils.ItemFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LicenseRequestMenu extends SelectionMenu {
    public CompanyI fromComp;
    public CompanyI toComp;
    public Product product;

    public double royaltyFee;
    public double increaseCost;
    public double weeklyCost;

    public LicenseRequestMenu(UUID uuid, CompanyI fromCompany, CompanyI toCompany, Product product){
        super(54, "License Request Menu", true);
        this.fromComp = fromCompany;
        this.toComp = toCompany;
        this.product = product;

        this.royaltyFee = 0.0;
        this.increaseCost = 0.0;
        this.weeklyCost = 1000.0;


        assert toCompany.getCOM().isOwner(uuid);
        init();
    }

    public void init(){

        DecimalFormat df = new DecimalFormat("###,###,##0.00");

        List<String> l = new ArrayList<>();
        l.add("§7Production Time: "+product.getProductionTime()+"s");
        l.add("§7Weekly License cost: $"+weeklyCost);
        l.add("");
        l.add("§7Normal Cost: $"+product.getPrice());
        l.add("§7Increased Cost: "+df.format(increaseCost*100)+"%");
        l.add("§7Royalty Fee: "+df.format(royaltyFee*100)+"%");
        l.add("§7Production Cost: $"+(product.getPrice()*(1.0+increaseCost+royaltyFee)));

        getInventory().setItem(4, ItemFactory.create(product.getMaterial(), "§7"+product.getMaterial().name()+" license", l));

        l = new ArrayList<>();
        l.add("§7Weekly License cost: $"+weeklyCost);
        ItemStack licenseCost = ItemFactory.create(Material.GOLD_INGOT, "§7Weekly License Cost", l);
        l = new ArrayList<>();
        l.add("§7Increased Cost: "+df.format(increaseCost*100)+"%");
        ItemStack increaseCost = ItemFactory.create(Material.RAW_GOLD, "§7Production Cost Increase", l);
        l = new ArrayList<>();
        l.add("§7Royalty Fee: "+df.format(royaltyFee*100)+"%");
        ItemStack royaltyCost = ItemFactory.create(Material.GOLDEN_HORSE_ARMOR, "§7Royalty fee", l);

        ItemStack increaseAbsGreen = ItemFactory.create(Material.LIME_STAINED_GLASS_PANE,
                "§aIncrease by 1000");

        ItemStack decreaseAbsRed = ItemFactory.create(Material.RED_STAINED_GLASS_PANE,
                "§cDecrease by 1000");

        ItemStack increaseRelativeGreen = ItemFactory.create(Material.LIME_STAINED_GLASS_PANE,
                "§aIncrease by 1%");

        ItemStack decreaseRelativeRed = ItemFactory.create(Material.RED_STAINED_GLASS_PANE,
                "§cDecrease by 1%");

        getInventory().setItem(29, licenseCost);
        getInventory().setItem(29-9, increaseAbsGreen);
        getInventory().setItem(29+9, decreaseAbsRed);

        getInventory().setItem(31, increaseCost);
        getInventory().setItem(31-9, increaseRelativeGreen);
        getInventory().setItem(31+9, decreaseRelativeRed);

        getInventory().setItem(33, royaltyCost);
        getInventory().setItem(33-9, increaseRelativeGreen);
        getInventory().setItem(33+9, decreaseRelativeRed);

        getInventory().setItem(52, ItemFactory.create(Material.LIME_WOOL, "§aSend License offer"));


        super.init();
    }
}
