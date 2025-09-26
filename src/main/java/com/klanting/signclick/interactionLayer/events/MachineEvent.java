package com.klanting.signclick.interactionLayer.events;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.logicLayer.companyLogic.Machine;
import com.klanting.signclick.logicLayer.companyLogic.Market;
import com.klanting.signclick.interactionLayer.menus.company.Selector;
import com.klanting.signclick.interactionLayer.menus.company.machine.MachineMenu;
import com.klanting.signclick.recipes.MachineRecipe;

import com.klanting.signclick.utils.BlockPosKey;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Iterator;
import java.util.function.Function;

import static com.klanting.signclick.utils.Utils.AssertMet;

public class MachineEvent implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
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

    private void dropMachine(Block block){
        NamespacedKey compKey = new NamespacedKey(SignClick.getPlugin(), "signclick_company_machine_company");

        TileState tileState = (TileState) block.getState();

        String compName = tileState.getPersistentDataContainer().get(compKey, PersistentDataType.STRING);
        if (tileState.getPersistentDataContainer().has(compKey, PersistentDataType.STRING) && !compName.isEmpty()){

            Machine machine = Market.getCompany(compName).getMachines().get(BlockPosKey.from(block.getLocation()));

            if (machine != null){
                for (int i=0; i<3; i++){
                    ItemStack itemStack = machine.results[i];
                    if (itemStack != null){
                        block.getWorld().dropItemNaturally(block.getLocation(), itemStack);
                    }
                }


                Market.getCompany(compName).getMachines().remove(BlockPosKey.from(block.getLocation()));
                MenuEvents.activeMachines.remove(machine);
            }

        }

        block.getWorld().dropItemNaturally(block.getLocation(), MachineRecipe.item());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event){
        Block block = event.getBlock();
        BlockState state = block.getState();

        if (state instanceof TileState tileState) {
            NamespacedKey key = new NamespacedKey(SignClick.getPlugin(), "signclick_company_machine");

            if (tileState.getPersistentDataContainer().has(key, PersistentDataType.BYTE)){
                event.setDropItems(false);
                dropMachine(block);

            }
        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityExplode(EntityExplodeEvent event){
        Iterator<Block> iterator = event.blockList().iterator();

        while (iterator.hasNext()) {
            Block block = iterator.next();
            BlockState state = block.getState();

            if (state instanceof TileState tileState) {
                NamespacedKey key = new NamespacedKey(SignClick.getPlugin(), "signclick_company_machine");
                if (!tileState.getPersistentDataContainer().has(key, PersistentDataType.BYTE)){
                    continue;
                }

                dropMachine(block);

                iterator.remove();
                block.setType(Material.AIR);

            }
        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
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

                Function<CompanyI, Void> func = (comp) -> {

                    tileState.getPersistentDataContainer().set(compKey, PersistentDataType.STRING, comp.getStockName());
                    tileState.getPersistentDataContainer().set(productKey, PersistentDataType.STRING, "");
                    tileState.update();

                    Machine machine = new Machine(block, comp);

                    comp.getMachines().put(BlockPosKey.from(block.getLocation()), machine);
                    InventoryHolder screen = new MachineMenu(event.getPlayer().getUniqueId(), comp, machine);
                    event.getPlayer().openInventory(screen.getInventory());
                    return null;
                };

                Selector screen = new Selector(event.getPlayer().getUniqueId(), func);
                event.getPlayer().openInventory(screen.getInventory());

            }else{
                CompanyI comp = Market.getCompany(companyName);
                Player player = (Player) event.getPlayer();
                if (!comp.getCOM().isEmployee(player.getUniqueId())){
                    player.sendMessage("§bOnly Chiefs and employees can access the machines");
                    return;
                }

                AssertMet(comp.getMachines().get(BlockPosKey.from(block.getLocation())) != null, "Machine Open: machine is null");
                InventoryHolder screen = new MachineMenu(event.getPlayer().getUniqueId(), comp,
                        comp.getMachines().get(BlockPosKey.from(block.getLocation())));
                event.getPlayer().openInventory(screen.getInventory());
            }

        }
    }
}
