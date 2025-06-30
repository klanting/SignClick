package com.klanting.signclick.events;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.Machine;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.menus.company.BoardMenu;
import com.klanting.signclick.menus.company.Selector;
import com.klanting.signclick.menus.company.machine.MachineMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.Function;

import static org.bukkit.Bukkit.getServer;

public class OpenFurnaceEvent implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){

        ItemStack item = event.getItemInHand();

        NamespacedKey key = new NamespacedKey(SignClick.getPlugin(), "signclick_company_machine");
        if (!item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE)){
            return;
        }

        Block block = event.getBlockPlaced();
        BlockState state = block.getState();

        if (state instanceof TileState tileState) {

            NamespacedKey compKey = new NamespacedKey(SignClick.getPlugin(), "signclick_company_machine_company");

            tileState.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);

            tileState.getPersistentDataContainer().set(compKey, PersistentDataType.STRING, "");

            tileState.update();
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getInventory().getHolder() instanceof Furnace)) return;

        Furnace furnace = (Furnace) event.getInventory().getHolder();
        Block block = furnace.getBlock();


        if (!(block.getState() instanceof TileState tileState)) return;

        NamespacedKey key = new NamespacedKey(SignClick.getPlugin(), "signclick_company_machine");
        NamespacedKey compKey = new NamespacedKey(SignClick.getPlugin(), "signclick_company_machine_company");
        NamespacedKey productKey = new NamespacedKey(SignClick.getPlugin(), "signclick_company_machine_product");

        if (tileState.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
            event.setCancelled(true);
            String companyName = tileState.getPersistentDataContainer().getOrDefault(compKey, PersistentDataType.STRING, "");

            if (companyName.isEmpty()){

                Function<Company, Void> func = (comp) -> {

                    tileState.getPersistentDataContainer().set(compKey, PersistentDataType.STRING, comp.getStockName());
                    tileState.getPersistentDataContainer().set(productKey, PersistentDataType.STRING, "");
                    tileState.update();

                    Machine machine = new Machine(block, comp);
                    comp.machines.put(block, machine);
                    InventoryHolder screen = new MachineMenu(event.getPlayer().getUniqueId(), comp, machine);
                    event.getPlayer().openInventory(screen.getInventory());
                    return null;
                };

                Selector screen = new Selector(event.getPlayer().getUniqueId(), func);
                event.getPlayer().openInventory(screen.getInventory());

            }else{
                InventoryHolder screen = new MachineMenu(event.getPlayer().getUniqueId(), Market.getCompany(companyName),
                        Market.getCompany(companyName).machines.get(block));
                event.getPlayer().openInventory(screen.getInventory());
            }

        }
    }
}
