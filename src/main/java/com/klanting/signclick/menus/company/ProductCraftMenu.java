package com.klanting.signclick.menus.company;

import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.Product;
import com.klanting.signclick.menus.SelectionMenu;
import com.klanting.signclick.utils.ItemFactory;
import com.klanting.signclick.utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ProductCraftMenu extends SelectionMenu {
    public Company comp;

    public Product[] products = new Product[9];

    public ProductCraftMenu(UUID uuid, Company company){
        super(45, "Company Product Crafting: "+ company.getStockName(), true);
        comp = company;

        assert comp.getCOM().isOwner(uuid);
        init();
    }

    public Product getCrafted(){
        ItemStack recipe = Utils.simulateCraft(Arrays.stream(products).
                map(p -> p != null ? new ItemStack(p.getMaterial()): null).toArray(ItemStack[]::new));

        if (recipe == null){
            return null;
        }

        long time = 0L;
        int cost = 0;

        for (Product product: products){
            if (product == null){
                continue;
            }
            time += product.getProductionTime();
            cost += product.getPrice();
        }

        return new Product(recipe.getType(), cost/recipe.getAmount(), time/recipe.getAmount());
    }

    public void init(){

        ItemStack recipe = Utils.simulateCraft(Arrays.stream(products).
                map(p -> p != null ? new ItemStack(p.getMaterial()): null).toArray(ItemStack[]::new));

        getInventory().clear();

        for (int i: List.of(0, 1, 2, 3, 4, 9, 13, 18, 22, 27, 31, 36, 37, 38, 39, 40)){
            getInventory().setItem(i, ItemFactory.create(Material.YELLOW_STAINED_GLASS_PANE, ""));
        }

        int counter = 0;
        for (int i: List.of(10, 11, 12, 19, 20, 21, 28, 29, 30)){
            if (products[counter] == null){
                getInventory().setItem(i, ItemFactory.create(Material.LIGHT_GRAY_DYE, "§7Crafting Slot"));
            }else{
                List<String> l = new ArrayList<>();
                l.add("§7Production Time: "+products[counter].getProductionTime()+"s");
                l.add("§7Cost: $"+products[counter].getPrice());
                getInventory().setItem(i,
                        ItemFactory.create(products[counter].getMaterial(),
                                "§7"+products[counter].getMaterial().name(), l));
            }

            counter += 1;
        }

        for (int i: List.of(15, 16, 17, 24, 26, 33, 34, 35)){
            getInventory().setItem(i, ItemFactory.create(Material.LIGHT_BLUE_STAINED_GLASS_PANE, ""));
        }

        if (recipe != null){
            getInventory().setItem(25, ItemFactory.create(recipe.getType(), "§7"+recipe.getType().name()));
        }

        getInventory().setItem(43, ItemFactory.create(Material.LIME_WOOL, "§aSave Product"));

        super.init();
    }
}
