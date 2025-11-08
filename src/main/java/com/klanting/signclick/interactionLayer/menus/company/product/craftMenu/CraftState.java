package com.klanting.signclick.interactionLayer.menus.company.product.craftMenu;

import com.klanting.signclick.logicLayer.companyLogic.producible.Product;

import java.util.List;

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
}
