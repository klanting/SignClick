package com.klanting.signclick.events;

import com.klanting.signclick.menus.PagingMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PagingSearchEvent implements Listener {

    public static Map<Player, PagingMenu> waitForMessage = new HashMap<>();
    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {

        if (waitForMessage.containsKey(event.getPlayer())){
            String searchKey = event.getMessage();

            waitForMessage.get(event.getPlayer()).setSearchKey(searchKey);
            event.getPlayer().openInventory(waitForMessage.get(event.getPlayer()).getInventory());
            event.setCancelled(true);
            waitForMessage.remove(event.getPlayer());
        }

    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        waitForMessage.remove(event.getPlayer());
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        waitForMessage.remove(event.getPlayer());
    }


}
