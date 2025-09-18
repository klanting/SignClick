package com.klanting.signclick.interactionLayer.events;

import com.klanting.signclick.interactionLayer.menus.company.machine.MachineMenu;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.HashSet;
import java.util.Set;

public class MachineLiveUpdateEvent implements Listener {

    public static Set<MachineMenu> openMenus = new HashSet<>();
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getInventory().getHolder() instanceof MachineMenu machineMenu)) return;

        openMenus.add(machineMenu);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof MachineMenu machineMenu)) return;

        openMenus.remove(machineMenu);
    }
}
