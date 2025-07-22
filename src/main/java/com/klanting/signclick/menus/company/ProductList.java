package com.klanting.signclick.menus.company;

import com.klanting.signclick.economy.*;
import com.klanting.signclick.menus.PagingMenu;
import com.klanting.signclick.utils.ItemFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ProductList extends PagingMenu {

    public final CompanyI comp;
    public final Function<Produceable, Void> func;
    public final boolean fullList;

    public final boolean showProducts;
    public final boolean showLicenses;

    public ProductList(CompanyI comp, Function<Produceable, Void> func){
        this(comp, func, false);
    }

    public ProductList(CompanyI comp, Function<Produceable, Void> func, boolean fullList){
        this(comp, func, fullList, true, false);
    }

    public ProductList(CompanyI comp, Function<Produceable, Void> func, boolean fullList,
                       boolean showProducts, boolean showLicenses){
        super(54, "Product List", true);
        this.comp = comp;
        this.func = func;
        this.fullList = fullList;
        this.showProducts = showProducts;
        this.showLicenses = showLicenses;

        init();
    }

    public void init(){
        clearItems();

        if (showProducts){
            for (Product product: comp.getProducts()){
                List<String> l = new ArrayList<>();
                l.add("§7Production Time: "+product.getProductionTime()+"s");
                l.add("§7Cost: $"+product.getPrice());
                ItemStack item = ItemFactory.create(product.getMaterial(), "§7"+product.getMaterial().name(), l);
                addItem(item);
            }
        }

        if (showLicenses){
            for (License license: LicenseSingleton.getInstance().getCurrentLicenses().getLicensesTo(comp)){
                Product product = license.getProduct();

                List<String> l = new ArrayList<>();
                l.add("§7Production Time: "+product.getProductionTime()*
                        (1.0+license.getCostIncrease()+license.getRoyaltyFee())+"s");
                l.add("§7Cost: $"+product.getPrice());
                l.add("§cThis Product is Licensed");
                l.add("§7Weekly License cost: $"+license.getWeeklyCost());
                ItemStack item = ItemFactory.create(product.getMaterial(), "§7"+product.getMaterial().name(), l);
                addItem(item);
            }
        }


        super.init();

        if (fullList){

            ItemStack book = ItemFactory.create(Material.BOOK, "§7Request Licenses");
            ItemStack writeBook = ItemFactory.create(Material.WRITABLE_BOOK, "§7See License Requests");
            ItemStack bookShelf = ItemFactory.create(Material.BOOKSHELF, "§7Received License List");
            ItemStack enchantmentTable = ItemFactory.create(Material.ENCHANTING_TABLE, "§7Given License List");

            getInventory().setItem(52, book);
            getInventory().setItem(51, writeBook);
            getInventory().setItem(50, bookShelf);
            getInventory().setItem(49, enchantmentTable);
        }
    }

}
