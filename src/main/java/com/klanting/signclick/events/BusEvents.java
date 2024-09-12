package com.klanting.signclick.events;

import com.klanting.signclick.Economy.Market;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class BusEvents implements Listener {
    @EventHandler
    public static void OnJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        if (!Market.hasAccount(player)){
            Market.create_account(player);
        }

    }
}
