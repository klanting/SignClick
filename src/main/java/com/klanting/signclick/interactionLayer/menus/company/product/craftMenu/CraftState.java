package com.klanting.signclick.interactionLayer.menus.company.product.craftMenu;

import com.klanting.signclick.interactionLayer.menus.company.product.ProductList;
import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.logicLayer.companyLogic.producible.Producible;
import com.klanting.signclick.logicLayer.companyLogic.producible.Product;

import java.util.List;
import java.util.function.Function;

public abstract class CraftState {
    /*
    * State design pattern
    *
    * */
    abstract public Product getCrafted();
    abstract public void setCrafted(int index, Product product);

    abstract public Product[] getProducts();

    abstract public List<Integer> getCraftSlots();

    abstract public List<Integer> getCraftCoverSlots();

    abstract public int getProductionSlot(int uiSlot);

    abstract public ProductList getProductUI(CompanyI comp, Function<Producible, Void> lambda);
}
