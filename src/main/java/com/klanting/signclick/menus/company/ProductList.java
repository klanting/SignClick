package com.klanting.signclick.menus.company;

import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.Product;
import com.klanting.signclick.menus.PagingMenu;
import com.klanting.signclick.utils.ItemFactory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class ProductList extends PagingMenu {

    public final Company comp;
    public final Function<Product, Void> func;

    public ProductList(Company comp, Function<Product, Void> func){
        super(54, "Product List", true);
        this.comp = comp;
        this.func = func;

        init();
    }

    public void init(){

        clearItems();

        for (Product product: comp.getProducts()){
            List<String> l = new ArrayList<>();
            l.add("§7Production Time: "+product.getProductionTime()+"s");
            l.add("§7Cost: $"+product.getPrice());
            ItemStack item = ItemFactory.create(product.getMaterial(), "§7"+product.getMaterial().name(), l);
            addItem(item);
        }

        super.init();
    }

}
