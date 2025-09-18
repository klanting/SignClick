package com.klanting.signclick.interactionLayer.events;

import com.klanting.signclick.interactionLayer.menus.SelectionMenu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;

public class OpenSelectionMenuEvent implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryOpen(InventoryOpenEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof SelectionMenu menu) {
            menu.onOpen();
        }
    }
}
