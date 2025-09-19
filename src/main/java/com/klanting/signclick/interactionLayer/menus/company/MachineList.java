package com.klanting.signclick.interactionLayer.menus.company;

import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.logicLayer.companyLogic.Machine;
import com.klanting.signclick.logicLayer.companyLogic.Product;
import com.klanting.signclick.interactionLayer.menus.PagingMenu;
import com.klanting.signclick.utils.BlockPosKey;
import com.klanting.signclick.utils.ItemFactory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MachineList extends PagingMenu {

    public final CompanyI comp;
    public final Function<Product, Void> func;

    public MachineList(CompanyI comp, Function<Product, Void> func){
        super(54, "Product List", true);
        this.comp = comp;
        this.func = func;

        init();
    }

    public void init(){

        clearItems();

        for (Map.Entry<BlockPosKey, Machine> entry: comp.getMachines().entrySet()){

            Machine machine = entry.getValue();

            List<String> l = new ArrayList<>();

            Location pos = machine.getBlock().getLocation();

            String posString = "X: "+(int) pos.getX()+ " Y: "+(int) pos.getY()+ " Z: "+(int) pos.getZ();

            l.add("§7Machine Location: "+posString);
            l.add(machine.isActive() ? "§aACTIVE": "§cINACTIVE");
            ItemStack item = ItemFactory.create(machine.hasProduct() ? machine.getProduct().getMaterial(): Material.BLAST_FURNACE,
                    "§7Machine", l);
            addItem(item);
        }

        super.init();
    }

    public boolean onClick(InventoryClickEvent event){
        if (!super.onClick(event)){
            return false;
        }
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);
        int item = event.getSlot();

        if(event.getCurrentItem().getType().equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE)){
            return false;
        }

        int index = (getPage()*45+item);
        func.apply(comp.getProducts().get(index));

        return false;
    }

}