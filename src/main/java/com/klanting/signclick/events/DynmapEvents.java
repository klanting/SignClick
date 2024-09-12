package com.klanting.signclick.events;

import com.klanting.signclick.SignClick;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class DynmapEvents implements Listener {

    @EventHandler
    public static void OnJoin(PlayerJoinEvent event){
        if (!SignClick.getDynmap().getPlayerVisbility(event.getPlayer())){
            event.getPlayer().sendMessage("§cYou are now visible on the dynmap\n" +
                    "if you hide yourself on the dynmap it will cost you 1000$ every 10m");
        }
        SignClick.getDynmap().setPlayerVisiblity(event.getPlayer(), true);


    }

}
