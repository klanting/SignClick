package com.klanting.signclick.logicLayer;

import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.logicLayer.companyLogic.logs.itemLogEntry;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Hopper;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

import static com.klanting.signclick.utils.Utils.AssertMet;

public class Machine {


    public boolean isFrozenByFunds() {
        return frozenByFunds;
    }

    private boolean frozenByFunds = false;

    /*
    * productionCount == -1 -> inf production
    * */
    private int productionCount = 0;

    public void changeProductionCount(int amount){
        /*
        * change the amount that will be produced by the provided value
        * */
        if (productionLooped()){
            return;
        }
        productionCount = Math.max(productionCount+amount, 0);
    }

    public boolean productionLooped(){
        /*
        * verify if the production is looping
        * */
        return productionCount == -1;
    }

    public int getProductionCount(){
        /*
        * retrieve how many items that will be produced
        * */
        if (productionCount == -1){
            return Integer.MAX_VALUE;
        }

        return productionCount;
    }

    public boolean isActive(){
        /*
        * Check if the machine is currently active
        * */

        /*
        * if no product is present -> not active
        * */
        if(product == null){
            return false;
        }

        /*
        * if production is looped -> it is active
        * */
        if(productionLooped()){
            return true;
        }

        return productionCount > 0;
    }

    public void changeProductionLoop(){
        /*
        * When looped production is enabled -> set count to 0, disabling the loop
        * when disabled -> -1, enabling the loop
        * */
        productionCount = productionCount == -1 ? 0: -1;
    }


    public ItemStack[] results = new ItemStack[3];

    public Product getProduct() {
        return product;
    }

    private Product product;

    public License getLicense() {
        return license;
    }

    private License license;

    private final String compName;

    public boolean frozenByMachineFull = false;

    public boolean hopperAllowed = false;

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

    public void checkHopper(){
        if (!hopperAllowed){
            return;
        }

        Block belowBlock = getBlock().getRelative(BlockFace.DOWN);

        if (belowBlock.getState() instanceof Hopper hopper) {

            for (int i=0; i<3;i++){
                ItemStack item = results[i];

                if (item == null){
                    continue;
                }

                HashMap<Integer, ItemStack> leftovers = hopper.getInventory().addItem(item);
                results[i] = leftovers.isEmpty() ? null: leftovers.values().iterator().next();
            }


        }
    }

    public void productionUpdate(){

        if (!block.getWorld().isChunkLoaded(block.getX() >> 4, block.getZ() >> 4)) {return;}

        if (!hasProduct()){
            productionProgress = 0;
            return;
        }

        if (getProductionCount() == 0){
            return;
        }

        productionProgress += 1*(Market.getCompany(compName).getUpgrades().get(6).getBonus()/100.0);

        if (productionProgress >= product.getProductionTime()){

            double amount = product.getPrice();
            if (isLicensed()){
                amount = amount*(1.0+license.getRoyaltyFee()+license.getCostIncrease());
            }

            int index = 0;
            while (index < 3){

                if(results[index] == null){
                    break;
                }

                if (results[index].getType().equals(product.getMaterial()) && results[index].getAmount() < product.getMaterial().getMaxStackSize()){
                    break;
                }
                index++;
            }

            if (index == 3){
                frozenByMachineFull = true;
                productionProgress = Math.min(productionProgress, product.getProductionTime());
                return;
            }
            frozenByMachineFull = false;

            if (getLicense() != null &&  getLicense().isFrozenByLicenseCost()){
                if (license.getTo().removeBal(getLicense().frozenByLicenseCost)){
                    getLicense().frozenByLicenseCost = 0;
                }
                return;
            }

            AssertMet(Market.getCompany(compName).getMachines().containsValue(this), "Phantom machine not allowed: "+compName);

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
            productionProgress = Math.max(productionProgress, 0);


            if (results[index] != null){
                results[index].setAmount(results[index].getAmount() + 1);
            }else{
                results[index] = new ItemStack(product.getMaterial());
            }


            Market.getCompany(compName).update("Machine production",
                    new itemLogEntry(product.getMaterial(), null, 1, amount), null);


            if (!productionLooped()){
                productionCount -= 1;
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
