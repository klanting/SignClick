package com.klanting.signclick.economy;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class Machine {


    public boolean isFrozenByFunds() {
        return frozenByFunds;
    }

    private boolean frozenByFunds = false;




    public ItemStack results;

    public Product getProduct() {
        return product;
    }

    private Product product;

    public License getLicense() {
        return license;
    }

    private License license;

    private final String compName;

    public int getProductionProgress() {
        return (int) productionProgress;
    }

    public boolean hasProduct(){
        return product != null;
    }

    public int getProductionTotal(){
        return product.getProductionTime();
    }

    private double productionProgress;

    public void clearProgress(){
        license = null;
        product = null;
        productionProgress = 0;
    }

    public Block getBlock() {
        return block;
    }

    private final Block block;

    public Machine(Block block, CompanyI company){
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

        productionProgress += 1*(Market.getCompany(compName).getUpgrades().get(6).getBonus()/100.0);

        if (productionProgress >= product.getProductionTime()){

            double amount = product.getPrice();
            if (isLicensed()){
                amount = amount*(1.0+license.getRoyaltyFee()+license.getCostIncrease());
            }

            if (getLicense() != null &&  getLicense().isFrozenByLicenseCost()){
                if (license.getTo().removeBal(getLicense().frozenByLicenseCost)){
                    getLicense().frozenByLicenseCost = 0;
                }
                return;
            }

            if (!Market.getCompany(compName).removeBal(amount)){
                frozenByFunds = true;
                productionProgress = Math.min(productionProgress, product.getProductionTime());
                return;
            }
            frozenByFunds = false;

            if (isLicensed()){
                license.getFrom().addBal(product.getPrice()*license.getRoyaltyFee());
            }

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
        this.license = null;
    }
    public void setLicense(License license){
        this.product = license.getProduct();
        this.license = license;
    }

    public boolean isLicensed(){
        return license != null;
    }
}
