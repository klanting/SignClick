package com.klanting.signclick.interactionLayer.menus.company.product.craftMenu;

import com.klanting.signclick.logicLayer.companyLogic.producible.Product;
import com.klanting.signclick.utils.Utils;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class CraftStateCraftingTable extends CraftState{

    private Product[] products = new Product[9];

    @Override
    public Product getCrafted(){
        ItemStack recipe = Utils.simulateCraft(Arrays.stream(products).
                map(p -> p != null ? new ItemStack(p.getMaterial()): null).toArray(ItemStack[]::new));

        if (recipe == null){
            return null;
        }

        int time = 0;
        int cost = 0;

        for (Product product: products){
            if (product == null){
                continue;
            }
            time += product.getProductionTime();
            cost += product.getPrice();
        }

        return new Product(recipe.getType(), (double) cost/recipe.getAmount(), Math.max(time/recipe.getAmount(), 1));
    }

    @Override
    public void setCrafted(int index, Product product) {
        products[index] = product;
    }

    @Override
    public Product[] getProducts() {
        return products;
    }

    @Override
    public List<Integer> getCraftSlots() {
        /*
        * return all the UI positions a user can set a product: crafting table -> 3x3
        * */
        return List.of(10, 11, 12, 19, 20, 21, 28, 29, 30);
    }

    @Override
    public List<Integer> getCraftCoverSlots() {
        /*
        * get the list of slots surrounding the given craft slots to mark it
        * */
        return List.of(0, 1, 2, 3, 4, 9, 13, 18, 22, 27, 31, 36, 37, 38, 39, 40);
    }
}
