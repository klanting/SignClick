package com.klanting.signclick.interactionLayer.menus.company.product.craftMenu;

import com.klanting.signclick.interactionLayer.menus.company.product.ProductList;
import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.logicLayer.companyLogic.producible.Producible;
import com.klanting.signclick.logicLayer.companyLogic.producible.Product;

import java.util.List;
import java.util.function.Function;

public class CraftStateFurnace extends CraftState{

    private final Product[] products = new Product[2];

    @Override
    public Product getCrafted() {
        return null;
    }

    @Override
    public void setCrafted(int index, Product product) {

    }

    @Override
    public Product[] getProducts() {
        return new Product[0];
    }

    @Override
    public List<Integer> getCraftSlots() {
        return null;
    }

    @Override
    public List<Integer> getCraftCoverSlots() {
        return null;
    }

    @Override
    public int getProductionSlot(int uiSlot) {
        return 0;
    }

    @Override
    public ProductList getProductUI(CompanyI comp, Function<Producible, Void> lambda) {
        return null;
    }
}
