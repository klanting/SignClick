package com.klanting.signclick.menus.company.machine;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.economy.Machine;
import com.klanting.signclick.menus.SelectionMenu;
import com.klanting.signclick.utils.ItemFactory;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.TileState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MachineMenu extends SelectionMenu {
    public CompanyI comp;
    public Machine machine;

    public MachineMenu(UUID uuid, CompanyI company, Machine machine){
        super(45, "Company Machine: "+ company.getStockName(), false);
        comp = company;
        this.machine = machine;

        assert comp.getCOM().isOwner(uuid);
        init();
    }

    public void update(){
        List<String> l = new ArrayList<>();
        if (machine.hasProduct()){
            int timeTillDone = machine.getProductionTotal()-machine.getProductionProgress();
            l.add("§7Next Produced: "+(machine.isFrozenByFunds() ? "NEVER": timeTillDone));
        }

        if (machine.isFrozenByFunds()){

            l.add("§cMachine frozen by lack of ");
            l.add("§cfunds or spendable");
        }

        getInventory().setItem(13, ItemFactory.create(Material.CLOCK, "§7Production", l));

        ItemStack result = machine.results;
        if (result != null){
            getInventory().setItem(34, result);
        }
    }

    public void init(){

        getInventory().clear();

        for (int i: List.of(0, 1, 2, 9, 11, 18, 19, 20)){
            getInventory().setItem(i, ItemFactory.create(Material.YELLOW_STAINED_GLASS_PANE, "§f"));
        }

        for (int i: List.of(6, 7, 8, 15, 17, 24, 26, 33, 35, 42, 43, 44)){
            getInventory().setItem(i, ItemFactory.create(Material.LIGHT_BLUE_STAINED_GLASS_PANE, "§f"));
        }

        update();

        getInventory().setItem(27, ItemFactory.create(Material.HOPPER, "§7Allow Hopper"));

        NamespacedKey productKey = new NamespacedKey(SignClick.getPlugin(), "signclick_company_machine_product");


        if (!(machine.getBlock().getState() instanceof TileState tileState)) return;
        String material = tileState.getPersistentDataContainer().getOrDefault(productKey, PersistentDataType.STRING, "");

        if (material.isEmpty()){
            getInventory().setItem(10, ItemFactory.create(Material.LIGHT_GRAY_DYE, "§7Product Slot"));
        }else{
            List<String> l = new ArrayList<>();
            l.add("§7Production Time: "+machine.getProduct().getProductionTime());

            double price = machine.getProduct().getPrice();
            if (machine.isLicensed()){
                price = price*(1.0+machine.getLicense().getRoyaltyFee()+machine.getLicense().getCostIncrease());
            }

            l.add("§7Production Cost: $"+price);

            if (machine.isLicensed()){
                l.add("§cThis Product is Licensed");
            }
            getInventory().setItem(10, ItemFactory.create(Material.valueOf(material), "§7"+material, l));
        }


        super.init();
    }
}
