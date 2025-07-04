package com.klanting.signclick.events;

import com.klanting.signclick.economy.Board;
import com.klanting.signclick.menus.company.BoardSupportMenu;
import com.klanting.signclick.menus.company.ChiefMenu;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class AddChiefSupportEvent implements Listener {
    public static Map<Player, ChiefMenu> waitForMessage = new HashMap<>();
    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {

        if (waitForMessage.containsKey(event.getPlayer())){
            String name = event.getMessage();

            Board board = waitForMessage.get(event.getPlayer()).comp.getCOM().getBoard();

            OfflinePlayer target = Bukkit.getOfflinePlayer(name);
            if (target != null && target.getUniqueId() != null){
                board.boardChiefVote(event.getPlayer().getUniqueId(),
                        waitForMessage.get(event.getPlayer()).position, target.getUniqueId());

                waitForMessage.get(event.getPlayer()).init();
                event.getPlayer().openInventory(waitForMessage.get(event.getPlayer()).getInventory());

            }else{
                event.getPlayer().sendMessage("§bPlayer does not exist");
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
