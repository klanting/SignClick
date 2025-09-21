package com.klanting.signclick.interactionLayer.menus.company.machine;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.interactionLayer.menus.company.ProductType;
import com.klanting.signclick.interactionLayer.menus.SelectionMenu;
import com.klanting.signclick.interactionLayer.menus.company.ProductList;
import com.klanting.signclick.logicLayer.companyLogic.*;
import com.klanting.signclick.logicLayer.companyLogic.producible.License;
import com.klanting.signclick.logicLayer.companyLogic.producible.Producible;
import com.klanting.signclick.logicLayer.companyLogic.producible.Product;
import com.klanting.signclick.utils.ItemFactory;

import com.klanting.signclick.utils.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

import static com.klanting.signclick.interactionLayer.events.MenuEvents.activeMachines;
import static com.klanting.signclick.interactionLayer.events.MenuEvents.loadStack;

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
            l.add("§7Next Produced: "+(machine.isFrozenByFunds() ? "NEVER": Utils.formatDuration(timeTillDone)));
        }

        if (machine.isLicensed() && machine.getLicense().isFrozenByLicenseCost()){
            l.add("§cMachine frozen by lack of ");
            l.add("§cLicense debt: $"+machine.getLicense().frozenByLicenseCost);
        }else if (machine.isFrozenByFunds()){

            l.add("§cMachine frozen by lack of ");
            l.add("§cfunds or spendable");
        }else if (machine.frozenByMachineFull){
            l.add("§cMachine frozen by ");
            l.add("§cfull storage");
        }
        l.add("§7Items to be produced: "+(machine.productionLooped() ? "infinite": machine.getProductionCount()));

        getInventory().setItem(13, ItemFactory.create(Material.CLOCK, "§7Production", l));

        ItemStack[] result = machine.results;
        for (int i=0;i<3;i++){
            if (result[i] != null){
                getInventory().setItem(16+(i*9), result[i]);
            }
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

        /*
        * Enable hopper option
        * */
        List<String> l = new ArrayList<>();
        l.add("§7Currently "+(machine.hopperAllowed ? "Enabled": "Disabled"));
        getInventory().setItem(27, ItemFactory.create(Material.HOPPER,
                machine.hopperAllowed ? "§cDisable Hopper": "§aEnable Hopper", l));

        /*
        * enable production looping
        * */
        l = new ArrayList<>();
        l.add("§7Currently "+(machine.productionLooped() ? "Enabled": "Disabled"));
        getInventory().setItem(28, ItemFactory.create(Material.REDSTONE_TORCH,
                machine.productionLooped() ? "§cDisable Production Loop": "§aEnable Production Loop", l));

        /*
        * add/remove production count
        * */
        getInventory().setItem(22, ItemFactory.create(Material.LIME_STAINED_GLASS,
                "§aAdd 1 to production count"));

        getInventory().setItem(31, ItemFactory.create(Material.RED_STAINED_GLASS,
                "§cRemove 1 from production count"));


        NamespacedKey productKey = new NamespacedKey(SignClick.getPlugin(), "signclick_company_machine_product");

        if (!(machine.getBlock().getState() instanceof TileState tileState)) return;
        String material = tileState.getPersistentDataContainer().getOrDefault(productKey, PersistentDataType.STRING, "");

        if (material.isEmpty()){
            getInventory().setItem(10, ItemFactory.create(Material.LIGHT_GRAY_DYE, "§7Product Slot"));
        }else{
            l = new ArrayList<>();
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

    public boolean onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);

        String option = event.getCurrentItem().getItemMeta().getDisplayName();
        if (option != null){
            if (option.equals("§7Product Slot")){

                Function<Producible, Void> lambda = (p) -> {

                    Product prod;
                    License license = null;
                    if(!(p instanceof Product)){
                        license = (License) p;
                        prod = license.getProduct();
                    }else{
                        prod = (Product) p;
                    }

                    if (!(machine.getBlock().getState() instanceof TileState tileState)) {
                        return null;
                    }
                    NamespacedKey productKey = new NamespacedKey(SignClick.getPlugin(), "signclick_company_machine_product");

                    /*
                     * set current item
                     * */
                    tileState.getPersistentDataContainer().set(productKey, PersistentDataType.STRING, prod.getMaterial().name());
                    tileState.update();

                    /*
                     * Start furnace
                     * */
                    if (license != null){
                        machine.clearProgress();
                        machine.setLicense(license);
                    }else{
                        machine.clearProgress();
                        machine.setProduct(prod);
                    }

                    activeMachines.add(machine);

                    loadStack(player);
                    return null;};

                ProductList new_screen = new ProductList(comp, lambda, false, ProductType.allOwned);
                player.openInventory(new_screen.getInventory());
            }

            if (option.contains("Hopper")){
                machine.hopperAllowed = !machine.hopperAllowed;
                init();
            }

            if (option.contains("Production Loop")){
                machine.changeProductionLoop();
                init();
            }

            if (option.contains("Add")){
                machine.changeProductionCount(1);
                init();
            }

            if (option.contains("Remove")){
                machine.changeProductionCount(-1);
                init();
            }

        }

        int slot = event.getSlot();
        if (List.of(16, 16+9, 16+18).contains(slot)){
            int index = (slot-16)/9;

            if ((event.getCursor() != null) && (!Objects.equals(event.getCursor().getType(), Material.AIR)) ){
                return false;
            }
            event.setCursor(machine.results[index]);
            machine.results[index] = null;
            init();
        }

        if (event.getSlot() == 10){
            if (!(machine.getBlock().getState() instanceof TileState tileState)){
                return false;
            }
            NamespacedKey productKey = new NamespacedKey(SignClick.getPlugin(), "signclick_company_machine_product");

            /*
             * set current item
             * */
            tileState.getPersistentDataContainer().set(productKey, PersistentDataType.STRING, "");
            tileState.update();
            machine.clearProgress();

            activeMachines.remove(machine);

            init();
        }

        return true;
    }
}
