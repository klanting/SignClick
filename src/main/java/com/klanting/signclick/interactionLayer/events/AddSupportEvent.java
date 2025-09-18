package com.klanting.signclick.interactionLayer.events;

import com.klanting.signclick.logicLayer.Board;
import com.klanting.signclick.interactionLayer.menus.company.BoardSupportMenu;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.event.Listener;

public class AddSupportEvent implements Listener {
    public static Map<Player, BoardSupportMenu> waitForMessage = new HashMap<>();
    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {

        if (waitForMessage.containsKey(event.getPlayer())){
            String name = event.getMessage();

            Board board = waitForMessage.get(event.getPlayer()).comp.getCOM().getBoard();

            OfflinePlayer target = Bukkit.getOfflinePlayer(name);

            if (target.hasPlayedBefore() && target != null && target.getUniqueId() != null && !board.
                    getBoardSupport(event.getPlayer().getUniqueId()).contains(target.getUniqueId())){
                board.addBoardSupport(event.getPlayer().getUniqueId(), target.getUniqueId());

                waitForMessage.get(event.getPlayer()).init();
                event.getPlayer().openInventory(waitForMessage.get(event.getPlayer()).getInventory());

            }else{
                event.getPlayer().sendMessage("§bPlayer does not exist or is already supported");
            }


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
