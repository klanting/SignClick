package com.klanting.signclick.interactionLayer.events;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.*;
import com.klanting.signclick.interactionLayer.menus.SelectionMenu;
import com.klanting.signclick.interactionLayer.menus.company.machine.MachineMenu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.*;

public class MenuEvents implements Listener {

    private static final HashMap<Player, Stack<SelectionMenu>> menuStack = new HashMap<>();

    public static final List<Machine> activeMachines = new ArrayList<>();

    public static void storeStack(Player player, SelectionMenu sm){
        Stack<SelectionMenu> playerStack = menuStack.getOrDefault(player, new Stack<>());
        playerStack.push(sm);
        menuStack.put(player, playerStack);
    }

    public static void loadStack(Player player){
        Stack<SelectionMenu> playerStack = menuStack.getOrDefault(player, new Stack<>());
        SelectionMenu sm = playerStack.pop();
        sm.getInventory().clear();
        sm.init();
        player.openInventory(sm.getInventory());
    }

    public static void clearStack(Player player){
        menuStack.put(player, new Stack<>());
    }

    public static void checkMachines(){
        Bukkit.getScheduler().runTaskTimer(SignClick.getPlugin(), () -> {

            for (Machine machine: activeMachines){
                machine.productionUpdate();
                machine.checkHopper();

            }

            for (MachineMenu mm: MachineLiveUpdateEvent.openMenus){
                mm.update();
            }

        }, 0L, 20L);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void OnClick(InventoryClickEvent event){

        /*
        * ensure we cannot move blocks into the ui if we have the same block
        * */
        if ((event.getInventory().getHolder() instanceof SelectionMenu
                && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY)
                && !(event.getClickedInventory().getHolder() instanceof SelectionMenu)
        ){
            event.setCancelled(true);
            return;
        }

        if (event.getClickedInventory() == null || event.getCurrentItem() == null){
            return;
        }

        if (!(event.getClickedInventory().getHolder() instanceof SelectionMenu selectionMenu)){
            return;
        }

        if(event.getAction() != InventoryAction.PICKUP_ALL
                && event.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY
                && event.getAction() != InventoryAction.UNKNOWN){
            event.setCancelled(true);
            return;
        }

        if (event.getCurrentItem().getType().equals(Material.BARRIER)){
            Player player = (Player) event.getWhoClicked();
            loadStack(player);
            return;
        }

        if (!selectionMenu.onClick(event)){
            return;
        }

        Player player = (Player) event.getWhoClicked();
        /*
        * Store the last inventory when inventory menu changes
        * */
        if (player.getOpenInventory().getTopInventory() == null){
            return;
        }

        if (!event.getClickedInventory().getHolder().equals(player.getOpenInventory().getTopInventory().getHolder())){
            storeStack(player, (SelectionMenu) event.getClickedInventory().getHolder());
        }


    }

}
