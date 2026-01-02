package com.klanting.signclick.interactionLayer.menus.company.product.craftMenu;

import com.klanting.signclick.interactionLayer.menus.company.product.ProductList;
import com.klanting.signclick.interactionLayer.menus.company.product.ProductType;
import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.logicLayer.companyLogic.producible.Producible;
import com.klanting.signclick.logicLayer.companyLogic.producible.Product;
import com.klanting.signclick.utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class CraftStateFurnace extends CraftState{

    private final Product[] products = new Product[2];

    @Override
    public Product getCrafted() {

        ItemStack recipe = Utils.simulateFurnace(Arrays.stream(products).
                map(p -> p != null ? new ItemStack(p.getMaterial()): null).toArray(ItemStack[]::new));

        if (recipe == null){
            return null;
        }

        int time = 0;
        int cost = 0;

        time += products[0].getProductionTime();
        cost += products[0].getPrice();

        double fuelNeeded = Utils.getFurnaceFuelAmount(Arrays.stream(products).
                map(p -> p != null ? new ItemStack(p.getMaterial()): null).toArray(ItemStack[]::new));

        fuelNeeded = fuelNeeded/recipe.getAmount();
        cost += (int) Math.ceil(fuelNeeded*products[1].getPrice());

        return new Product(recipe.getType(),
                ((double) cost/recipe.getAmount()),
                Math.max(time/recipe.getAmount(), 1));
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
        return List.of(11, 29);
    }

    @Override
    public List<Integer> getCraftCoverSlots() {
        return List.of(1, 2, 3, 10, 12, 19, 20, 21, 28, 30, 37, 38, 39);
    }

    @Override
    public int getProductionSlot(int uiSlot) {
        return (uiSlot == 11) ? 0: 1;
    }

    @Override
    public ProductList getProductUI(CompanyI comp, Function<Producible, Void> lambda) {
        return new ProductList(comp, lambda, false, ProductType.allOwned);
    }
}
