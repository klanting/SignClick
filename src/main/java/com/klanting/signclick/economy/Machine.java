package com.klanting.signclick.economy;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class Machine {

    private boolean doLoop = true;

    public boolean isFrozenByFunds() {
        return frozenByFunds;
    }

    private boolean frozenByFunds = false;

    public ItemStack results;

    public Product getProduct() {
        return product;
    }

    private Product product;

    private final String compName;

    public int getProductionProgress() {
        return productionProgress;
    }

    public boolean hasProduct(){
        return product != null;
    }

    public int getProductionTotal(){
        return product.getProductionTime();
    }

    private int productionProgress;

    public void clearProgress(){
        productionProgress = 0;
    }

    public Block getBlock() {
        return block;
    }

    private final Block block;

    public Machine(Block block, Company company){
        product = null;
        productionProgress = 0;
        this.block = block;
        this.compName = company.getStockName();
    }

    public void productionUpdate(){

        if (!block.getWorld().isChunkLoaded(block.getX() >> 4, block.getZ() >> 4)) {return;}

        if (!hasProduct()){
            productionProgress = 0;
            return;
        }

        productionProgress += 1;

        if (productionProgress >= product.getProductionTime()){

            if (!Market.getCompany(compName).removeBal(product.getPrice())){
                frozenByFunds = true;
                return;
            }
            frozenByFunds = false;

            productionProgress -= product.getProductionTime();

            if (results!= null){
                results.setAmount(results.getAmount() + 1);
            }else{
                results = new ItemStack(product.getMaterial());
            }
        }
    }

    public void setProduct(Product product){
        this.product = product;
    }
}
