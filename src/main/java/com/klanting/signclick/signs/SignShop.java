package com.klanting.signclick.signs;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.utils.Prefix;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.dataflow.qual.AssertMethod;
import org.yaml.snakeyaml.error.Mark;

import java.text.DecimalFormat;
import java.util.Map;

import static com.klanting.signclick.utils.Utils.AssertMet;

public class SignShop {
    public static void setSign(SignChangeEvent sign, Player player){
        String[] splitted = sign.getLine(1).split(" ");

        double price = Double.parseDouble(splitted[0]);

        int amount;
        if(splitted.length > 1){
            amount = Integer.parseInt(splitted[1]);
        }else{
            amount = 1;
        }

        String companyString = sign.getLine(2);
        String materialString = sign.getLine(3);

        if (companyString == null){
            player.sendMessage(SignClick.getPrefix()+"please enter company name on 3th line");
            return;
        }

        CompanyI company = Market.getCompany(companyString.toUpperCase());

        if (company == null){
            player.sendMessage(SignClick.getPrefix()+"company name invalid");
            return;
        }

        if(!company.getCOM().isEmployee(player.getUniqueId())){
            player.sendMessage(SignClick.getPrefix()+"Only employees can make sign");
            return;
        }

        if(Material.getMaterial(materialString) == null){
            player.sendMessage(SignClick.getPrefix()+"item does not exist");
            return;
        }

        if (!(sign.getBlock().getBlockData() instanceof WallSign signData)){
            player.sendMessage(SignClick.getPrefix()+"needs to be a wallsign");
            return;
        }

        if(!(sign.getBlock().getRelative(signData.getFacing().getOppositeFace()).getState() instanceof Chest)){
            player.sendMessage(SignClick.getPrefix()+"needs to be a wallsign against a chest");
            return;
        }

        sign.setLine(0, "§b[sign_shop]");
        sign.setLine(1, price+" "+amount);
        sign.setLine(2, company.getStockName());
        sign.setLine(3, materialString);
    }

    public static void onSign(Sign sign, Player player){
        String[] splitted = sign.getLine(1).split(" ");

        double price = Double.parseDouble(splitted[0]);

        int amount;
        if(splitted.length > 1){
            amount = Integer.parseInt(splitted[1]);
        }else{
            amount = 1;
        }

        String companyString = sign.getLine(2);
        String materialString = sign.getLine(3);

        CompanyI company = Market.getCompany(companyString.toUpperCase());
        Material material = Material.getMaterial(materialString);

        if (!(sign.getBlock().getBlockData() instanceof WallSign signData)){
            player.sendMessage(SignClick.getPrefix()+"broken sign");
            return;
        }

        if(!(sign.getBlock().getRelative(signData.getFacing().getOppositeFace()).getState() instanceof Chest chest)){
            player.sendMessage(SignClick.getPrefix()+"broken sign chest missing");
            return;
        }

        ItemStack it = new ItemStack(material, amount);

        if(!chest.getInventory().containsAtLeast(it, it.getAmount())){
            player.sendMessage(SignClick.getPrefix()+"Not enough Stock");
            return;
        }

        if(!SignClick.getEconomy().has(player, price)){
            player.sendMessage(SignClick.getPrefix()+"You don't have enough money");
            return;
        }

        if(!canFit(player.getInventory(), it)){
            player.sendMessage(SignClick.getPrefix()+"You don't have enough inventory space");
            return;
        }

        /*
        * remove item from chest
        * */
        Map<Integer, ItemStack> notRemoved = chest.getInventory().removeItem(it);
        AssertMet(notRemoved.isEmpty(), "Not all items could be removed from chest");

        /*
        * add item to player inf
        * */
        Map<Integer, ItemStack> leftover = player.getInventory().addItem(it);
        AssertMet(leftover.isEmpty(), "Not all items could be removed from chest");

        /*
        * transfer player money
        * */
        SignClick.getEconomy().withdrawPlayer(player, price);
        company.addBal(price);

        DecimalFormat df = new DecimalFormat("###,###,##0.00");

        Prefix.sendMessage(player,"§7You bought "+amount+" "+material.name()+" FOR $"+df.format(price));
        company.update("Shop sales", "bought "+amount+" "+material.name()+" FOR $"+df.format(price),
                player.getUniqueId());

    }

    private static boolean canFit(Inventory inv, ItemStack toAdd) {
        int remaining = toAdd.getAmount();
        Material type = toAdd.getType();

        int counter = 0;
        for (ItemStack slot : inv.getContents()) {
            if(counter >= 36){
                break;
            }

            if (slot == null || slot.getType() == Material.AIR) {
                // Empty slot can hold a full stack
                remaining -= type.getMaxStackSize();
            } else if (slot.getType() == toAdd.getType()) {
                // Existing stack: how much space is left
                remaining -= (slot.getMaxStackSize() - slot.getAmount());
            }
            counter += 1;
        }

        return remaining <= 0; // true if all items can fit
    }
}
