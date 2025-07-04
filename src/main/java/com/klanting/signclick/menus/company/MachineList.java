package com.klanting.signclick.menus.company;

import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.economy.Machine;
import com.klanting.signclick.economy.Product;
import com.klanting.signclick.menus.PagingMenu;
import com.klanting.signclick.utils.ItemFactory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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

        for (Map.Entry<Block, Machine> entry: comp.getMachines().entrySet()){

            Machine machine = entry.getValue();

            List<String> l = new ArrayList<>();

            Location pos = machine.getBlock().getLocation();

            String posString = "X: "+(int) pos.getX()+ " Y: "+(int) pos.getY()+ " Z: "+(int) pos.getZ();

            l.add("§7Machine Location: "+posString);
            ItemStack item = ItemFactory.create(machine.hasProduct() ? machine.getProduct().getMaterial(): Material.BLAST_FURNACE,
                    "§7Machine", l);
            addItem(item);
        }

        super.init();
    }

}